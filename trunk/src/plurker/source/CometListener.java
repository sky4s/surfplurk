/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.source;

import com.google.jplurk_oauth.data.Comet;
import com.google.jplurk_oauth.data.Comment;
import com.google.jplurk_oauth.data.Plurk;
import com.google.jplurk_oauth.data.UserChannel;
import com.google.jplurk_oauth.data.UserInfo;
import com.google.jplurk_oauth.module.Realtime;
import com.google.jplurk_oauth.skeleton.RequestException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.json.JSONException;
import plurker.source.PlurkSourcer;
import plurker.ui.PlurkerApplication;

/**
 *
 * @author SkyforceShen
 */
public class CometListener {

    private PlurkSourcer source;
    private UserChannel userChannel;
    private Realtime realtime;
    private PlurkPool plurkPool;

    public CometListener(PlurkSourcer source, PlurkPool plurkPool) {
        this.source = source;
        this.plurkPool = plurkPool;
    }

    public void stop() {
        stop = false;
    }
    private Comparator<Plurk> plurkComparator = new Comparator() {
        @Override
        public int compare(Object t, Object t1) {
            Plurk p = (Plurk) t;
            Plurk p1 = (Plurk) t1;
            try {
                return Long.compare(p.getPlurkId(), p1.getPlurkId());
            } catch (JSONException ex) {
                Logger.getLogger(CometListener.class.getName()).log(Level.SEVERE, null, ex);
                return -1;
            }
        }
    };
    private Comparator<Comment> responseComparator = new Comparator() {
        @Override
        public int compare(Object t, Object t1) {
            Comment c = (Comment) t;
            Comment c1 = (Comment) t1;
            try {
                int result = Long.compare(c.getId(), c1.getId());
                return result;
            } catch (JSONException ex) {
                Logger.getLogger(CometListener.class.getName()).log(Level.SEVERE, null, ex);
                return -1;
            }
        }
    };
    private boolean stop = false;
    private LinkedList<ChangeListener> listenerList = new LinkedList<>();
    private TreeSet<Plurk> newPlurkSet;//= new TreeSet<>(plurkComparator);
    private TreeSet<Plurk> oldPlurkSet;
    private TreeSet<Plurk> stackPlurkSet = new TreeSet<>(plurkComparator);
    private TreeSet<Comment> newResponseSet;// = new TreeSet<>(responseComparator);
    private TreeSet<Comment> oldResponseSet;
    private TreeSet<Comment> stackResponseSet = new TreeSet<>(responseComparator);
    private List<UserInfo[]> userInfoList;

    public TreeSet<Plurk> getStackPlurkSet() {
        return stackPlurkSet;
    }

    public TreeSet<Comment> getStackResponseSet() {
        return stackResponseSet;
    }

    public List<UserInfo[]> getUserInfoList() {
        return userInfoList;
    }

    public TreeSet<Plurk> getNewPlurkSet() {
        return newPlurkSet;
    }

    public TreeSet<Comment> getNewCommentSet() {
        return newResponseSet;
    }

    public void addChangeListener(ChangeListener listener) {
        listenerList.add(listener);
    }

    public void removeChangeListenerr(ChangeListener listener) {
        listenerList.remove(listener);
    }

    private void fireCometChange() {
        ChangeEvent e = new ChangeEvent(plurkPool);
        for (ChangeListener listenr : listenerList) {
            listenr.stateChanged(e);
        }
    }
    private Thread thread = new Thread() {
        public void run() {
            int offset = -1;

            try {
                while (!stop) {
                    Comet comet = realtime.getComet(userChannel, offset);
                    offset = comet.getNewOffset();
                    if (comet.isDataAvailable()) {

//                        newPlurkSet.clear();
                        newPlurkSet = new TreeSet<>(plurkComparator);
                        newResponseSet = new TreeSet<>(responseComparator);
                        userInfoList = new ArrayList<>();

                        int size = comet.size();
                        for (int x = 0; x < size; x++) {
                            if (comet.isNewPlurk(x)) {
                                Plurk plurk = comet.getPlurk(x);

                                boolean addPlurk = (null != oldPlurkSet && !newPlurkSet.contains(plurk)) || (null == newPlurkSet);
                                if (addPlurk) {
                                    newPlurkSet.add(plurk);
                                }

                            } else if (comet.isNewResponse(x)) {
                                Comment comment = comet.getComment(x);

                                boolean addComment = (null != oldResponseSet && !oldResponseSet.contains(comment)) || (null == oldResponseSet);

                                if (addComment) {
                                    newResponseSet.add(comment);
                                    UserInfo[] userInfoOfComment = comet.getUserInfoOfComment(x);
                                    userInfoList.add(userInfoOfComment);
                                }

                            }
                        }

                        oldResponseSet = newResponseSet;
                        oldPlurkSet = newPlurkSet;
                        if (!newResponseSet.isEmpty()) {
                            stackResponseSet = newResponseSet;
                        }
                        if (!newPlurkSet.isEmpty()) {
                            stackPlurkSet = newPlurkSet;
                        }
                        if (!newPlurkSet.isEmpty() || !newResponseSet.isEmpty()) {
                            fireCometChange();
                        }
                    }
                }
            } catch (JSONException | RequestException ex) {
                Logger.getLogger(CometListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };

    public void start() throws RequestException {
        realtime = source.getRealtime();
        userChannel = realtime.getUserChannel();
        thread.start();
    }

    public static void main(String[] args) throws RequestException, JSONException {
        PlurkSourcer source = PlurkerApplication.getPlurkSourcerInstance();
        final PlurkPool pool = new PlurkPool(source);
        pool.addCometChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                System.out.println("state change");
                try {
                    for (Plurk p : pool.getNewPlurkSet()) {
                        System.out.println("Plurk: " + p.getPlurkId() + " " + p.getContent());
                    }
                    for (Comment c : pool.getNewResponseSet()) {
                        System.out.println("Response: " + c.getId() + " " + c.getContent());
                    }
                } catch (JSONException ex) {
                    Logger.getLogger(CometListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        pool.startComet();
    }
}
