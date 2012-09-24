/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.source;

import com.google.jplurk_oauth.data.*;
import com.google.jplurk_oauth.module.Timeline;
import com.google.jplurk_oauth.skeleton.DateTime;
import com.google.jplurk_oauth.skeleton.RequestException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.json.JSONException;
import plurker.image.ImageUtils;
import plurker.ui.Qualifier;

/**
 *
 * @author SkyforceShen
 */
public class PlurkPool implements ChangeListener {

    private PlurkSourcer sourcer;

    public PlurkSourcer getSourcer() {
        return sourcer;
    }

    public PlurkPool(PlurkSourcer sourcer) {
        this.sourcer = sourcer;
        sourcer.plurkPool = this;
        cometListener = new CometListener(sourcer, this);
    }

    public void stopComet() {
        cometListener.stop();
        cometListener.removeChangeListenerr(this);
    }
    private CometListener cometListener;

    /**
     * start comet後不應該是把新資訊丟到pool這麼簡單, 應該要: 1.主動丟到plurk 2.主動丟到comment並且發出訊息
     *
     * 有兩種機制可以通知comet的更新
     *
     * 1. listener機制 對新訊息有興趣的UI, 來跟pool註冊listen, 新訊息到了會發出通知. 此法撰寫比較簡單,
     * 但是若listener一多...要大量派發, 形成loading
     *
     * 2. mapping機制 pool內部維護一對照表, 當有新訊息來時, 依據訊息與UI的關係, 僅通知有關聯的UI. 此法較為麻煩,
     * 但是會比較有效率.
     *
     * @param listener
     */
    public void startComet() throws RequestException {
        cometListener.addChangeListener(this);
        cometListener.start();
    }

    public void addCometChangeListener(ChangeListener listener) {
        cometListener.addChangeListener(listener);
    }
    private Map<Long, List<PlurkChangeListener>> plurkChangeListenerMap = new HashMap<>();

    public void addPlurkChangeListener(long plurkID, PlurkChangeListener listener) {
        List<PlurkChangeListener> list = plurkChangeListenerMap.get(plurkID);
        if (null == list) {
            list = new LinkedList<>();
            plurkChangeListenerMap.put(plurkID, list);
        }
        list.add(listener);
    }

    private void firePlurkChange(long plurkId, PlurkChangeListener.Type type, Data data) {
        List<PlurkChangeListener> list = plurkChangeListenerMap.get(plurkId);
        if (null != list) {
            for (PlurkChangeListener listener : list) {
                listener.plurkChange(type, data);
            }
        }
//        PlurkChangeListener listener = plurkChangeListenerMap.get(plurkId);
//        if (null != listener) {
//            listener.plurkChange(type, data);
//        }
    }

//    public void removeCometChangeListener(ChangeListener listener) {
//        cometListener.removeChangeListenerr(listener);
//    }
    public TreeSet<Plurk> getNewPlurkSet() {
        if (null != cometListener) {
            return cometListener.getNewPlurkSet();
        } else {
            return null;
        }
    }

    public TreeSet<Comment> getNewResponseSet() {
        if (null != cometListener) {
            return cometListener.getNewCommentSet();
        } else {
            return null;
        }
    }

    public TreeSet<Plurk> getStackPlurkSet() {
        if (null != cometListener) {
            return cometListener.getStackPlurkSet();
        } else {
            return null;
        }
    }

    public TreeSet<Comment> getStackResponseSet() {
        if (null != cometListener) {
            return cometListener.getStackResponseSet();
        } else {
            return null;
        }
    }
    private Map<Long, UserInfo> userinfoMap = new HashMap<>();

    public UserInfo getUserInfo(long userid) {
        UserInfo userInfo = userinfoMap.get(userid);
        if (null == userInfo) {
            UserProfile profile = sourcer.getProfile(userid);
            if (null != profile) {
                try {
                    userInfo = profile.getUserInfo();
                    userinfoMap.put(userid, userInfo);
                } catch (JSONException ex) {
                    Logger.getLogger(PlurkPool.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return userInfo;
    }

    private void storeUserInfo(UserInfo[] userinfos) {
        for (UserInfo info : userinfos) {
            long userid = info.getID();
            userinfoMap.put(userid, info);
        }
    }

    public List<Plurk> getNewerPlurks(DateTime offset, int limit) throws JSONException {
        Plurks newerPlurks = sourcer.getNewerPlurks(offset, limit);
        storeUserInfo(newerPlurks.getUserInfos());
        return newerPlurks.getSortedPlurkList();
    }
//    boolean fetchCommentsFromSourcer = false;

    @Override
    public void stateChanged(ChangeEvent e) {
        List<UserInfo[]> userInfoList = cometListener.getUserInfoList();
        for (UserInfo[] infoarray : userInfoList) {
            storeUserInfo(infoarray);
        }
        TreeSet<Comment> newCommentSet = cometListener.getNewCommentSet();
        TreeSet<Plurk> newPlurkSet = cometListener.getNewPlurkSet();
        try {
            for (Comment c : newCommentSet) {
                storeComment(c);
                long plurkId = c.getPlurkId();
                firePlurkChange(plurkId, PlurkChangeListener.Type.CommentAdd, c);
            }
            for (Plurk p : newPlurkSet) {
                storePlurk(p);
                long plurkId = p.getPlurkId();
                firePlurkChange(plurkId, PlurkChangeListener.Type.PlurkAdd, p);
            }
        } catch (JSONException ex) {
            Logger.getLogger(PlurkPool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void storeComment(Comment comment) throws JSONException {
        long plurkId = comment.getPlurkId();
        if (commentsMap.containsKey(plurkId)) {
            List<Comment> list = commentsMap.get(plurkId);
            list.add(comment);
        }
    }

    public Comment responseAdd(Long plurkId, String content, Qualifier qualifier) throws JSONException {
        Comment comment = sourcer.responseAdd(plurkId, content, qualifier);
        if (null != comment) {
            storeComment(comment);
        }
        return comment;
    }
    private Map<Long, java.util.List<Comment>> commentsMap = new HashMap<>();
    private Map<Long, Plurk> plurkMap = new HashMap<>();

    public Plurk getPlurk(long plurkID) {
        return plurkMap.get(plurkID);
    }

    void storePlurks(Plurks plurks) {
        try {
            for (Plurk plurk : plurks.getPlurkList()) {
                storePlurk(plurk);
            }
        } catch (JSONException ex) {
            Logger.getLogger(PlurkPool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void storePlurk(Plurk plurk) {
        try {
            long plurkId = plurk.getPlurkId();
            plurkMap.put(plurkId, plurk);
        } catch (JSONException ex) {
            Logger.getLogger(PlurkPool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public java.util.List<Comment> getComments(Plurk plurk) throws JSONException {
        return getComments(plurk, 0, false);
    }

    /*public Comments getComments(Plurk plurk, long fromResponse) throws JSONException {
     Comments comments = sourcer.getComments(plurk, fromResponse);
     if (null == comments) {
     return null;
     }
     UserInfo[] userInfos = comments.getUserInfos();
     if (null != userInfos) {
     storeUserInfo(userInfos);
     }
     return comments;
     }*/
    public java.util.List<Comment> getComments(Plurk plurk, long fromResponse) throws JSONException {
        return getComments(plurk, fromResponse, false);
    }

    public java.util.List<Comment> getComments(Plurk plurk, long fromResponse, boolean fromPlurkSourcer) throws JSONException {
        return getComments(plurk.getPlurkId(), fromResponse, fromPlurkSourcer);
    }

    public java.util.List<Comment> getComments(long plurkID, long fromResponse, boolean fromPlurkSourcer) throws JSONException {

        java.util.List<Comment> result = null;
//        long plurkId = plurk.getPlurkId();
        if (commentsMap.containsKey(plurkID) && false == fromPlurkSourcer) {
            result = commentsMap.get(plurkID);
        } else {
            Comments comments = sourcer.getComments(plurkID, fromResponse);
            if (null == comments) {
                return null;
            }
            UserInfo[] userInfos = comments.getUserInfos();
            if (null != userInfos) {
                storeUserInfo(userInfos);
            }
            result = comments.getCommentList();
            commentsMap.put(plurkID, result);
        }


        return result;
    }

    public List<Plurk> getOlderPlurks(DateTime offset, int limit, Timeline.Filter filter) throws JSONException {
        Plurks olderPlurks = sourcer.getOlderPlurks(offset, limit, filter);
        for (UserInfo info : olderPlurks.getUserInfos()) {
            long userid = info.getID();
            userinfoMap.put(userid, info);
        }
        return olderPlurks.getSortedPlurkList();
    }

    private static int fetchPlurkCount(int limit, int index, int size, boolean older) {
        if (0 < index) {
            return limit;
        } else {
            if (older) {
                return index < limit ? limit - index : 0;
            } else {
                return (size - index - 1) < limit ? limit - (size - index - 1) : 0;
            }
        }
    }
    Dictionary imageCache = new Hashtable();

    public BufferedImage getImage(URL url) throws IOException {
        BufferedImage image = (BufferedImage) imageCache.get(url);
        if (null == image) {
            image = ImageUtils.loadImageFromURL(url);
            imageCache.put(url, image);
        }
        return image;
    }

    public Dictionary getImageCache() {
        return imageCache;
    }
}
