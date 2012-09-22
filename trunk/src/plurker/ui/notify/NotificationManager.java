/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui.notify;

import com.google.jplurk_oauth.data.Plurk;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.Timer;
import plurker.source.PlurkPool;
import plurker.source.PlurkSourcer;
import plurker.ui.NotifyPanel2;
import plurker.ui.PlurkerApplication;
import shu.util.Persistence;
//import plurker.ui.FixedHTMLEditorKit;

/**
 *
 * @author SkyforceShen
 */
public class NotificationManager {

    private static NotificationManager notificationManager;

    public final static NotificationManager getInstance() {
        if (null == notificationManager) {
            notificationManager = new NotificationManager();
        }
        return notificationManager;
    }
//    private LinkedList<NotificationPanel> linkedList = new LinkedList<>();
    private LinkedList<NotificationWindow> linkedList = new LinkedList<>();
//    private HashMap<JWindow, NotificationPanel> map = new HashMap<>();
    public final static int NotifyWidth = 300;
//    class NotifyComponentListener extends ComponentAdapter {
//
//        @Override
//        public void componentHidden(ComponentEvent e) {
//            if (e.getComponent() instanceof JWindow) {
//                JWindow window = (JWindow) e.getComponent();
//                NotificationPanel notify = map.get(window);
//                map.remove(window);
//                linkedList.remove(notify);
//                window.dispose();
//            }
//        }
//    };
//    private NotifyComponentListener notifyComponentListener = new NotifyComponentListener();
    public static int DisappearWaitTime = 10000;

    public void addContent(JComponent content) {
        if (null == closeTimer) {
            closeTimer = new Timer(DisappearWaitTime, closeActionListener);
            closeTimer.start();
        } else {
            closeTimer.restart();
        }


        NotificationPanel notity = new NotificationPanel(content, NotifyWidth);
        NotificationWindow window = notity.getWindow();
        window.addWindowListener(closeWindowListener);

        //        jWindow.addComponentListener(notifyComponentListener);
//        NotificationPanel last = linkedList.size() != 0 ? linkedList.getLast() : null;
//        linkedList.add(notity);
//        map.put(jWindow, notity);
        //        int y = (null != last) ? last.getJWindow().getLocation().y - jWindow.getHeight()/*- last.getHeight()*/ : desktopBounds.y + desktopBounds.height - jWindow.getHeight();
        NotificationWindow last = (linkedList.size() != 0) ? linkedList.getLast() : null;
        linkedList.add(window);
        int y = (null != last) ? last.getLocation().y - window.getHeight()/*- last.getHeight()*/ : desktopBounds.y + desktopBounds.height - window.getHeight();
//        System.out.println(y);
        int x = desktopBounds.width - NotifyWidth;
        window.setLocation(x, y);
        window.setAlwaysOnTop(true);
        window.setVisible(true);
    }
    WindowListener closeWindowListener = new WindowAdapter() {
        private boolean listen = true;

        public void windowClosed(WindowEvent e) {
            if (!listen) {
                return;
            }
            listen = false;
            for (NotificationWindow window : linkedList) {
                if (window.isVisible()) {
                    Object source = e.getSource();
                    ActionEvent ae = new ActionEvent(source, e.getID(), null);
                    window.actionPerformed(ae);
                }
            }
            linkedList.clear();
            listen = true;
        }
    };
    Timer closeTimer;
    ActionListener closeActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
//            for (NotificationWindow window : linkedList) {
////                windows.
//                window.actionPerformed(e);
//            }
            if (linkedList.size() != 0) {
                linkedList.getFirst().actionPerformed(e);
            }
        }
    };
    private static Rectangle desktopBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

    public static void main(String[] args) throws InterruptedException {
        NotificationManager instance = NotificationManager.getInstance();
        for (int x = 0; x < 15; x++) {
            instance.addContent(new JLabel(Integer.toString(x + 1)));
        }
//        Thread.currentThread().sleep(5000);
//        for (int x = 15; x < 25; x++) {
//            instance.addContent(new JLabel(Integer.toString(x + 1)));
//        }

//        PlurkSourcer.setDoValidToken(false);
//        PlurkSourcer plurkSourcerInstance = PlurkerApplication.getPlurkSourcerInstance();
//        PlurkPool plurkpool = new PlurkPool(plurkSourcerInstance);
//
//        Plurk plurk = (Plurk) Persistence.readObjectAsXML("plurk.obj");
//        NotifyPanel2 notifyPanel2 = new NotifyPanel2(plurk, plurkpool);
//        notifyPanel2.updateWidth(300);
//        
//        instance.addContent(notifyPanel2);
    }
}
