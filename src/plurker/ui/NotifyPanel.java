/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui;

import com.google.jplurk_oauth.data.Comment;
import com.google.jplurk_oauth.data.Plurk;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.json.JSONException;
import plurker.image.ImageUtils;
import plurker.source.PlurkFormater;
import plurker.source.PlurkPool;
import plurker.source.PlurkSourcer;
import plurker.ui.util.GUIUtil;
import plurker.util.Utils;
import shu.util.Persistence;

/**
 *
 * @author SkyforceShen
 * @deprecated
 */
public class NotifyPanel extends ContentPanel {

    @Override
    public Object clone() {
        switch (type) {
            case Plurk:
                return new NotifyPanel(this.plurk, this.plurkPool);
            case Comment:
                return new NotifyPanel(this.comment, this.plurkPool);
            case Unknow:
                return new NotifyPanel(this.content, this.prefferedWidth);
            default:
                throw new UnsupportedOperationException();
        }
    }

//    private PlurkerApplication plurker;
//
//    public void setPlurker(PlurkerApplication plurker) {
//        this.plurker = plurker;
//    }
    public NotifyPanel(Plurk plurk, PlurkPool plurkPool) {
        super(plurk, null, plurkPool, -1, null, Type.Plurk, true);
    }

    public NotifyPanel(Comment comment, PlurkPool plurkPool) {
        super(null, comment, plurkPool, -1, null, Type.Comment, true);
    }

    public NotifyPanel(String content, int width) {
        super(null, null, null, width, content, Type.Unknow, true);
        this.content = content;
    }
    private String content;

    @Override
    public boolean equals(Object obj) {
        try {
            if (obj instanceof NotifyPanel) {
                NotifyPanel that = (NotifyPanel) obj;
                if (null != this.plurk && null != that.plurk) {
                    return this.plurk.getPlurkId() == that.plurk.getPlurkId();
                } else if (null != this.comment && null != that.comment) {
                    return this.comment.getId() == that.comment.getId();
                } else {
                    return false;//super.equals(obj);
                }
            } else {
                return super.equals(obj);
            }
        } catch (JSONException ex) {
            Logger.getLogger(NotifyPanel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
//        return super.equals(obj);
    }

    @Override
    protected String getContent() throws JSONException {
        if (null != plurk) {
            return PlurkFormater.getInstance(plurkPool).getContent(plurk);
        } else if (null != comment) {
            return PlurkFormater.getInstance(plurkPool).getContent(comment, true);
        } else {
            return "N/A";
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        GUIUtil.initGUI();

        BufferedImage refreshImage = ImageUtils.loadImageFromURL(ContentPanel.class.getResource("/plurker/ui/resource/1349158187_refresh.png"));
//        PlurkSourcer.setDoValidToken(false);
//        PlurkSourcer plurkSourcerInstance = PlurkerApplication.getPlurkSourcerInstance();
//        final PlurkPool pool = new PlurkPool(plurkSourcerInstance);




        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    final JFrame frame = new JFrame();
                    frame.setLayout(new java.awt.BorderLayout());
                    MediaTracker mediaTracker = new MediaTracker(frame);



                    final URL url = new URL("http://localhost/win8.bmp");
                    long start = System.currentTimeMillis();
                    final Image win8 = Toolkit.getDefaultToolkit().getImage(url);
                    System.out.println((System.currentTimeMillis() - start) / 1000.);
                    String content = Utils.readContent(new File("c.html"));
                    mediaTracker.addImage(win8, 0);
                    mediaTracker.waitForID(0);
                    System.out.println((System.currentTimeMillis() - start) / 1000.);


                    JScrollPane scroll = new JScrollPane();
                    scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                    frame.add(scroll, java.awt.BorderLayout.CENTER);
                    final JPanel panel = new JPanel();
                    panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
                    scroll.setViewportView(panel);
                    frame.setSize(300, 400);
                    frame.setVisible(true);


                    int sw = scroll.getVerticalScrollBar().getWidth();
                    final int w = panel.getWidth() - sw;//-100;
                    System.out.println(w);
                    final ContentPanel contentPanel = new ContentPanel(content, w);
                    final ContentPanel contentPanel2 = new ContentPanel(content, w);
//        w = panel.getPreferredSize().width;
//        w = 100;

                    AWTEventListener listener = new AWTEventListener() {
                        @Override
                        public void eventDispatched(AWTEvent event) {
                            if (panel == event.getSource()) {
                                System.out.print("panel ");
                            }
                            System.out.println(event);
                            if (event.getSource() instanceof ContentPanel) {

//                     panel.updateUI();
                                ContentPanel contentPanel = (ContentPanel) event.getSource();
                                Container parent = contentPanel.getParent();
//                    parent.repaint();
                                SwingUtilities.updateComponentTreeUI(parent);
                            }
                        }
                    };
                    Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.COMPONENT_EVENT_MASK);

                    panel.add(contentPanel);
                    panel.add(contentPanel2);
                    System.out.println("add over");


                    new Thread() {
                        public void run() {
                            try {
                                Thread.currentThread().sleep(2000);
                                System.out.println("wait 2s over");
                                contentPanel.updateWidth(w);
                                contentPanel2.updateWidth(w);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(NotifyPanel.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }.start();

                } catch (MalformedURLException ex) {
                    Logger.getLogger(NotifyPanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(NotifyPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });





    }
}
