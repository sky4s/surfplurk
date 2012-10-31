/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui.notify;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.Timer;
import org.json.JSONException;
import plurker.ui.ContentPanel;
import plurker.ui.ContentPanel.Type;
import plurker.ui.FollowerIF;
import plurker.ui.notify.NotificationsPanel.NotificationsDialog;

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
    private boolean displayNotifyDialog = true;
//    private NotificationsFrame notificationsFrame;
    private FollowerIF followerIF;

    public void setDisplayTinyWindow(boolean displayTinyWindow) {
        this.displayTinyWindow = displayTinyWindow;
    }

    public boolean isDisplayTinyWindow() {
        return displayTinyWindow;
    }

    public boolean isDisplayNotifydialog() {
        return displayNotifyDialog;
    }

    public void setDisplayNotifyDialog(boolean displayNotifyFrame) {
        this.displayNotifyDialog = displayNotifyFrame;
        if (null != notificationsDialog) {
            notificationsDialog.setVisible(false);
        }
    }

    public boolean isTinyWindowVisible() {
        return nowTinyList.size() != 0;
    }
    private NotificationsPanel notificationsPanel;
    private NotificationsDialog notificationsDialog;

    private void initNotificationsDialog() {
        if (null == notificationsPanel) {
            notificationsPanel = new NotificationsPanel();
            notificationsDialog = notificationsPanel.getNotificationsDialog(null);
            Dimension size = notificationsDialog.getSize();
            int y = desktopBounds.y + desktopBounds.height - size.height;
            int x = desktopBounds.width - size.width - NotifyXOffset;

            notificationsDialog.setLocation(x, y);
            notificationsDialog.setVisible(true);

        }
    }
    MouseAdapter notifyPanelMouseAdapter = new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            Object source = e.getSource();
            System.out.println(source);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            Object source = e.getSource();
            System.out.println(source);
        }
    };

    public boolean addToNotificationsDialog(ContentPanel notify) {
        if (!displayNotifyDialog) {
            return false;
        }
        initNotificationsDialog();
        notify.setAutoHighlight(true);
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
 
        if (null != followerIF && followerIF.isInFollow(plurkId)) {
            ContentPanel clone = (ContentPanel) notify.clone();
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

    public boolean isNotifyDialogVisible() {
        if (null != notificationsDialog) {
            return notificationsDialog.isVisible();// && notificationsDialog.isFocused();
        } else {
            return false;
        }
    }

    public void setNotifyDialogVisible(boolean visible) {
        if (null == notificationsDialog) {
            return;
        }
        if (visible) {
            notificationsDialog.setVisible(visible);
            notificationsDialog.setAlwaysOnTop(true);
            notificationsDialog.setAlwaysOnTop(false);
        } else {
            notificationsDialog.setVisible(visible);
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
//            instance.addToTinyWindow(new JLabel(Integer.toString(x + 1)));
//            instance.addToNotificationsDialog(ContentPanel.getNotifyInstance("1234", 300));

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
