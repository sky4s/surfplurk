/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui.notify;

import com.google.jplurk_oauth.data.Plurk;
import java.awt.Dimension;
import java.awt.Frame;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.Timer;
import org.json.JSONException;
import plurker.ui.ContentPanel.Type;
import plurker.ui.FollowerIF;
import plurker.ui.NotifyPanel;
import plurker.ui.TabbedResponsePanel;
import plurker.ui.notify.NotificationsPanel.NotificationsWindow;

/**
 *
 * @author SkyforceShen
 */
public class NotificationManager {

    private Frame owner;

    private NotificationManager(Frame owner) {
        this.owner = owner;
    }
    private static NotificationManager notificationManager;

    public final static NotificationManager getInstance() {
        return getInstance(null);
//        if (null == notificationManager) {
//            notificationManager = new NotificationManager();
//        }
//        return notificationManager;
    }

    public final static NotificationManager getInstance(Frame owner) {
        if (null == notificationManager) {
            notificationManager = new NotificationManager(owner);
        }
        return notificationManager;
    }
    private LinkedList<TinyNotificationWindow> tinyList = new LinkedList<>();
    private ArrayList<JComponent> nowTinyList = new ArrayList<>();
    public final static int NotifyWidth = 263;
    public final static int NotifyXOffset = 20;
    public static int DisappearWaitTime = 10000;
    private boolean displayTinyWindow = false;
    private boolean displayNotifyWindow = true;
//    private NotificationsFrame notificationsFrame;
    private FollowerIF followerIF;

    public void setDisplayTinyWindow(boolean displayTinyWindow) {
        this.displayTinyWindow = displayTinyWindow;
    }

    public boolean isDisplayTinyWindow() {
        return displayTinyWindow;
    }

    public boolean isDisplayNotifyWindow() {
        return displayNotifyWindow;
    }

    public void setDisplayNotifyWindow(boolean displayNotifyFrame) {
        this.displayNotifyWindow = displayNotifyFrame;
        if (null != notificationsWindow) {
            notificationsWindow.setVisible(false);
        }
    }

    public boolean isTinyWindowVisible() {
        return nowTinyList.size() != 0;
    }
    private NotificationsPanel notificationsPanel;
    private NotificationsWindow notificationsWindow;

    private void initNotificationsWindow() {
        if (null == notificationsPanel) {
            notificationsPanel = new NotificationsPanel();
            notificationsWindow = notificationsPanel.getWindow(owner);
            Dimension size = notificationsWindow.getSize();
            int y = desktopBounds.y + desktopBounds.height - size.height;
            int x = desktopBounds.width - size.width - NotifyXOffset;
            notificationsWindow.setLocation(x, y);
            notificationsWindow.setVisible(true);
        }
    }

    public boolean addtToNotificationsWindow(NotifyPanel notify) {
        if (!displayNotifyWindow) {
            return false;
        }
        initNotificationsWindow();
        notificationsPanel.addToAll(notify);
        Type type = notify.getType();
        long plurkId = -1;
        try {
            if (Type.Comment == type) {
                plurkId = notify.getComment().getPlurkId();
            } else if (type.Plurk == type) {
                plurkId = notify.getPlurk().getPlurkId();
            }
        } catch (JSONException ex) {
            Logger.getLogger(NotificationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        //        notify.getComment()
        //        notificationsFrame.addToAll(notify);
        //        Plurk plurk = notify.getPlurk();
        if (null != followerIF && followerIF.isInFollow(plurkId)) {
            NotifyPanel clone = (NotifyPanel) notify.clone();
            notificationsPanel.addToFollow(clone);
        }
        return true;
    }

    public boolean addToTinyWindow(JComponent content) {
        if (!displayTinyWindow) {
            return false;
        }
        if (null == closeTimer) {
            closeTimer = new Timer(DisappearWaitTime, closeActionListener);
            closeTimer.start();
        } else {
            closeTimer.restart();
        }
        if (nowTinyList.contains(content)) {
            return false;
        }
        nowTinyList.add(content);

        TinyNotificationPanel notity = new TinyNotificationPanel(content, NotifyWidth);
        TinyNotificationWindow window = notity.getWindow();
        window.addWindowListener(closeWindowListener);

        TinyNotificationWindow last = (tinyList.size() != 0) ? tinyList.getLast() : null;
        tinyList.add(window);
        int y = (null != last) ? last.getLocation().y - window.getHeight()/*- last.getHeight()*/ : desktopBounds.y + desktopBounds.height - window.getHeight();
        int x = desktopBounds.width - NotifyWidth - NotifyXOffset;
        window.setLocation(x, y);
        window.setAlwaysOnTop(true);
        window.setVisible(true);
        return true;
    }
    WindowListener closeWindowListener = new WindowAdapter() {
        private boolean listen = true;

        public void windowClosed(WindowEvent e) {
            if (!listen) {
                return;
            }
            listen = false;
            for (TinyNotificationWindow window : tinyList) {
                if (window.isVisible()) {
                    Object source = e.getSource();
                    ActionEvent ae = new ActionEvent(source, e.getID(), null);
                    window.actionPerformed(ae);
                }
            }
            tinyList.clear();
            nowTinyList.clear();
            listen = true;
        }
    };

    public boolean isNotifyWindowVisible() {
        //T F F T
//        System.out.println(notificationsWindow.isVisible());
//        System.out.println(notificationsWindow.isActive()); //F
//        System.out.println(notificationsWindow.isFocused()); //F //T
//        System.out.println(notificationsWindow.isShowing());
//        System.out.println(notificationsWindow.isFocusable());
//        System.out.println(notificationsWindow.isFocusableWindow());
        return notificationsWindow.isVisible();
//        System.out.println(notificationsWindow.getFocusOwner());
//        return false;
    }

    public void setNotifyWindowVisible(boolean visible) {
        if (visible) {
            notificationsWindow.setVisible(visible);
            notificationsWindow.setAlwaysOnTop(true);
            notificationsWindow.setAlwaysOnTop(false);
        } else {
            notificationsWindow.setVisible(visible);
        }
    }

    public void setTinyWindowInvisible() {
        closeActionListener.actionPerformed(null);
    }
    private Timer closeTimer;
    private ActionListener closeActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (tinyList.size() == 0) {
                closeTimer.stop();
            } else if (!isMouseInWindow()) {
                //滑鼠不在視窗裡 就進行關閉
                tinyList.getFirst().actionPerformed(e);
            }


        }
    };

    public boolean isMouseInWindow() {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        Point location = pointerInfo.getLocation();
        for (TinyNotificationWindow window : tinyList) {
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
