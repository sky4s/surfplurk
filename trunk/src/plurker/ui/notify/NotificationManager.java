/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui.notify;

import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.Timer;
import plurker.ui.PlurkerApplication;

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
    private LinkedList<TinyNotificationWindow> linkedList = new LinkedList<>();
    private ArrayList<JComponent> nowShowingList = new ArrayList<>();
    public final static int NotifyWidth = 300;
    public final static int NotifyXOffset = 20;
    public static int DisappearWaitTime = 10000;

    public boolean isShowing() {
        return nowShowingList.size() != 0;
    }

    public void addtToNotificationsWindow(JComponent content) {
    }

    public void addToTinyWindow(JComponent content) {
        if (null == closeTimer) {
            closeTimer = new Timer(DisappearWaitTime, closeActionListener);
            closeTimer.start();
        } else {
            closeTimer.restart();
        }
        if (nowShowingList.contains(content)) {
            return;
        }
        nowShowingList.add(content);

        TinyNotificationPanel notity = new TinyNotificationPanel(content, NotifyWidth);
        TinyNotificationWindow window = notity.getWindow();
        window.addWindowListener(closeWindowListener);

        TinyNotificationWindow last = (linkedList.size() != 0) ? linkedList.getLast() : null;
        linkedList.add(window);
        int y = (null != last) ? last.getLocation().y - window.getHeight()/*- last.getHeight()*/ : desktopBounds.y + desktopBounds.height - window.getHeight();
        int x = desktopBounds.width - NotifyWidth - NotifyXOffset;
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
            for (TinyNotificationWindow window : linkedList) {
                if (window.isVisible()) {
                    Object source = e.getSource();
                    ActionEvent ae = new ActionEvent(source, e.getID(), null);
                    window.actionPerformed(ae);
                }
            }
            linkedList.clear();
            nowShowingList.clear();
            listen = true;
        }
    };

    public void stopShowing() {
        closeActionListener.actionPerformed(null);
    }
    private Timer closeTimer;
    private ActionListener closeActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (linkedList.size() == 0) {
                closeTimer.stop();
            } else if (!isMouseInWindow()) {
                //滑鼠不在視窗裡 就進行關閉
                linkedList.getFirst().actionPerformed(e);
            }


        }
    };

    public boolean isMouseInWindow() {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        Point location = pointerInfo.getLocation();
        for (TinyNotificationWindow window : linkedList) {
            Rectangle bounds = window.getBounds();
            boolean contains = bounds.contains(location);
            if (contains) {
                return true;
            }
        }
        return false;
    }
    private static Rectangle desktopBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

    public static void main(String[] args) throws InterruptedException {
        NotificationManager instance = NotificationManager.getInstance();
        for (int x = 0; x < 15; x++) {
            instance.addToTinyWindow(new JLabel(Integer.toString(x + 1)));
        }
//        Thread.currentThread().sleep(5000);
//        for (int x = 15; x < 25; x++) {
//            instance.addToTinyWindow(new JLabel(Integer.toString(x + 1)));
//        }

//        PlurkSourcer.setDoValidToken(false);
//        PlurkSourcer plurkSourcerInstance = PlurkerApplication.getPlurkSourcerInstance();
//        PlurkPool plurkpool = new PlurkPool(plurkSourcerInstance);
//
//        Plurk plurk = (Plurk) Persistence.readObjectAsXML("plurk.obj");
//        NotifyPanel2 notifyPanel2 = new NotifyPanel2(plurk, plurkpool);
//        notifyPanel2.updateWidth(300);
//        
//        instance.addToTinyWindow(notifyPanel2);
    }
}
