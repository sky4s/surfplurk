/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui.notify;

import com.google.jplurk_oauth.data.Comment;
import com.google.jplurk_oauth.data.Plurk;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import plurker.source.PlurkPool;
import plurker.ui.ContentPanel;
import plurker.ui.NotifyPanel;
import plurker.ui.PlurkerApplication;
import plurker.ui.ResponsePanel;
import plurker.ui.util.GUIUtil;
import shu.util.Persistence;

/**
 *
 * @author SkyforceShen
 */
public class NotificationsPanel extends javax.swing.JPanel implements AWTEventListener {

    private ResponsePanel allPanel;
    private ResponsePanel followPanel;

    /**
     * Creates new form NotificationsPanel
     */
    public NotificationsPanel() {
        initComponents();
        allPanel = new ResponsePanel(true);
//        allPanel.setToNotifyMode();
        followPanel = new ResponsePanel(true);
//        followPanel.setToNotifyMode();
        jTabbedPane2.addTab("所有更新", allPanel);
        jTabbedPane2.addTab("追蹤中", followPanel);

        this.setSize(this.getPreferredSize());
        this.jPanel3.setVisible(false);
//        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_WHEEL_EVENT_MASK);
        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK);
    }
    private ResponsePanel responsePanel;
    private JDialog responseDialog;

    public JDialog getResponseDialog(NotifyPanel notifyPanel) {
        Plurk plurk = notifyPanel.getPlurk();
        if (null == plurk) {
            Comment comment = notifyPanel.getComment();
            plurk = comment.getParentPlurk();
        }
        PlurkPool plurkPool = notifyPanel.getPlurkPool();

        ContentPanel contentPanel = new ContentPanel(plurk, plurkPool);

        if (null == responsePanel) {
            responsePanel = new ResponsePanel(contentPanel, ResponsePanel.Mode.Simple);
        } else {
            responsePanel.setRootContentPanel(contentPanel);
        }
        if (null == responseDialog) {
            responseDialog = new JDialog(notificationsDialog, "", false);
            responseDialog.getContentPane().add(responsePanel);
        }

        return responseDialog;
    }
    private final static int ResponseDialogWidth = 300;
    private final static int ResponseDialogHeight = 475;

    @Override
    public void eventDispatched(AWTEvent event) {
        if (event instanceof MouseEvent) {
            MouseEvent mouseevent = (MouseEvent) event;
            if (mouseevent.getID() == MouseEvent.MOUSE_CLICKED&& SwingUtilities.isLeftMouseButton(mouseevent) ) {

                Component component = mouseevent.getComponent();
                if (null != component && SwingUtilities.isDescendingFrom(component, this)) {
                    int selectedIndex = jTabbedPane2.getSelectedIndex();
                    ResponsePanel responsePanel = (ResponsePanel) jTabbedPane2.getComponentAt(selectedIndex);

                    JPanel commentsPanel = responsePanel.getCommentsPanel();
                    mouseevent = SwingUtilities.convertMouseEvent(mouseevent.getComponent(), mouseevent, commentsPanel);
                    Component componentAt = commentsPanel.getComponentAt(mouseevent.getPoint());
//                    System.out.println(componentAt);
                    if (componentAt instanceof NotifyPanel) {
                        NotifyPanel notifyPanel = (NotifyPanel) componentAt;
                        JDialog responseDialog = getResponseDialog(notifyPanel);
                        responseDialog.setSize(ResponseDialogWidth, ResponseDialogHeight);

                        Point panelLocation = notifyPanel.getLocationOnScreen();
                        int height = notifyPanel.getHeight();
                        Point dialogLocation = notificationsDialog.getLocationOnScreen();

                        responseDialog.setLocation(dialogLocation.x - ResponseDialogWidth, panelLocation.y + height - ResponseDialogHeight);
                        responseDialog.setVisible(true);
                        //                        Plurk plurk = notifyPanel.getPlurk();
                        //                        notifyPanel.getPlurkPanel().getpl
                    }

                }

//                System.out.println(mouseevent);
//                System.out.println(component);
//                if (SwingUtilities.isDescendingFrom(component, this) && component instanceof NotifyPanel) {
////                    System.out.println(mouseevent);
//                }

            }

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jButton_Close = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setPreferredSize(new java.awt.Dimension(300, 475));
        setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.BorderLayout());

        jButton_Close.setIcon(new javax.swing.ImageIcon(getClass().getResource("/plurker/ui/resource/dialog_close.png"))); // NOI18N
        jButton_Close.setBorder(null);
        jButton_Close.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/plurker/ui/resource/dialog_cancel.png"))); // NOI18N
        jPanel3.add(jButton_Close, java.awt.BorderLayout.EAST);
        jPanel3.add(jLabel1, java.awt.BorderLayout.CENTER);

        add(jPanel3, java.awt.BorderLayout.PAGE_START);

        jTabbedPane2.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        add(jTabbedPane2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_Close;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTabbedPane jTabbedPane2;
    // End of variables declaration//GEN-END:variables

    private void alterToFitsize(NotifyPanel notify) {
        int width = this.allPanel.getWidth();
//        System.out.println(width);
        int contentHeight = getContentHeight(notify, width);
        Dimension size = new Dimension(width, contentHeight);
        notify.setSize(width, contentHeight);
        notify.setPreferredSize(size);
    }

    public void addToAll(NotifyPanel notify) {
//        alterToFitsize(notify);
        this.allPanel.addContentPanel(notify);
    }

    private static int getContentHeight(NotifyPanel notify, int width) {
//        TinyNotificationPanel tmppanel = new TinyNotificationPanel(component);
        notify.setSize(width, Short.MAX_VALUE);
        return notify.getPreferredSize().height;
    }

    public void addToFollow(NotifyPanel notify) {
//        jPanel2.add(notify);
        this.followPanel.addContentPanel(notify);
    }

    public static void main(String[] args) {


        PlurkPool plurkpool = null;// new PlurkPool(plurkSourcerInstance);
//        PlurkPool plurkpool = new PlurkPool(plurkSourcerInstance);
        Plurk plurk = (Plurk) Persistence.readObjectAsXML("plurk.obj");
//        NotifyPanel notifyPanel2 = new NotifyPanel("1234", 300);


//        NotificationsPanel panel = new NotificationsPanel();
//        NotificationsDialog window1 = panel.getNotificationsDialog();
//        window1.setDragable(true);
//        window1.setVisible(true);

//        NotificationsFrame frame = new NotificationsFrame();
//        frame.setVisible(true);
//        window1.frame = frame;
        NotificationManager notifyManager = NotificationManager.getInstance();
        for (int x = 0; x < 15; x++) {
            NotifyPanel notifyPanel2 = new NotifyPanel(plurk, plurkpool);
//            notifyPanel2.updateWidth(300);
//            panel.addToAll(notifyPanel2);
            notifyManager.addToNotificationsDialog(notifyPanel2);
//            frame.addToAll((NotifyPanel) notifyPanel2.clone());
        }


//        panel.addToFollow(notifyPanel2);
    }

    public NotificationsDialog getNotificationsDialog(Frame owener) {
        if (null == notificationsDialog) {
            notificationsDialog = new NotificationsDialog(this, owener);
            notificationsDialog.setIconImage(PlurkerApplication.PlurkIcon);

//            notificationsDialog.addWindowFocusListener(new WindowFocusListener() {
//                @Override
//                public void windowGainedFocus(WindowEvent e) {
////                    System.out.println(e);
//                }
//
//                @Override
//                public void windowLostFocus(WindowEvent e) {
//                    System.out.println(e);
//                }
//            });

            Dimension size = this.getSize();
            notificationsDialog.setSize(this.getSize());
            jButton_Close.addActionListener(notificationsDialog);
        }
        return notificationsDialog;
    }
    private NotificationsDialog notificationsDialog;

    class NotificationsDialog extends StandardDialog {

        private NotificationsPanel notificationsPanel;

        NotificationsDialog(NotificationsPanel notificationsPanel, Frame owener) {
            super(notificationsPanel, jPanel3, owener);
            this.setIconImage(PlurkerApplication.PlurkIcon);
            this.notificationsPanel = notificationsPanel;
            getContentPane().add(notificationsPanel);
//            notificationsPanel.jButton_Close.addActionListener(this);
            //            this.addMouseMotionListener(null);
//            NotifyMouseAdapter notifyMouseAdapter = new NotifyMouseAdapter();
//            this.addMouseListener(notifyMouseAdapter);
//            this.addMouseMotionListener(notifyMouseAdapter);
        }
        private boolean dragable = false;

        public void setDragable(boolean dragable) {
            this.dragable = dragable;
        }

        class NotifyMouseAdapter extends MouseAdapter {

            public void mousePressed(MouseEvent e) {
                dragStartpoint = e.getPoint();
            }
            private Point dragStartpoint;

            public void mouseDragged(MouseEvent e) {
                if (null != dragStartpoint) {
                    Point locationOnScreen = e.getLocationOnScreen();
                    int x = locationOnScreen.x - dragStartpoint.x;
                    int y = locationOnScreen.y - dragStartpoint.y;
                    setLocation(x, y);
                }
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == jButton_Close) {
                this.setVisible(false);
            }
        }
    }
}
