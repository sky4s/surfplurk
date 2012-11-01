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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import plurker.source.PlurkPool;
import plurker.ui.ContentPanel;
import plurker.ui.PlurkerApplication;
import plurker.ui.ResponsePanel;
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
//        this.jPanel3.setVisible(false);
//        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_WHEEL_EVENT_MASK);
        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK);
    }
    private ResponsePanel responsePanel;
    private JDialog responseDialog;

    public JDialog getResponseDialog(ContentPanel notifyPanel) {
        Plurk plurk = notifyPanel.getPlurk();
        if (null == plurk) {
            Comment comment = notifyPanel.getComment();
            if (null == comment) {
                int x = 1;
            }
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
            if (mouseevent.getID() == MouseEvent.MOUSE_CLICKED && SwingUtilities.isLeftMouseButton(mouseevent)) {

                Component component = mouseevent.getComponent();
                if (null != component && SwingUtilities.isDescendingFrom(component, this)) {
                    int selectedIndex = jTabbedPane2.getSelectedIndex();
                    ResponsePanel responsePanel = (ResponsePanel) jTabbedPane2.getComponentAt(selectedIndex);

                    JPanel commentsPanel = responsePanel.getCommentsPanel();
                    mouseevent = SwingUtilities.convertMouseEvent(mouseevent.getComponent(), mouseevent, commentsPanel);
                    Component componentAt = commentsPanel.getComponentAt(mouseevent.getPoint());
//                    System.out.println(componentAt);
                    if (componentAt instanceof ContentPanel) {
                        ContentPanel notifyPanel = (ContentPanel) componentAt;
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

        jTabbedPane2 = new javax.swing.JTabbedPane();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setPreferredSize(new java.awt.Dimension(300, 475));
        setLayout(new java.awt.BorderLayout());

        jTabbedPane2.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        add(jTabbedPane2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane jTabbedPane2;
    // End of variables declaration//GEN-END:variables

    private void alterToFitSize(ContentPanel notify) {
//        int width = this.allPanel.getWidth();
        int width = this.allPanel.getPreferredSize().width;
        System.out.println(width);
//        int contentHeight = getContentHeight(notify, width);
//        Dimension size = new Dimension(width, contentHeight);
//        notify.setSize(width, contentHeight);
//        notify.setPreferredSize(size);
        notify.updateWidth(width);
    }

    public void addToAll(ContentPanel notify) {
        alterToFitSize(notify);
        this.allPanel.addContentPanel(notify);
    }

    private static int getContentHeight(ContentPanel notify, int width) {
        notify.setSize(width, Short.MAX_VALUE);
        return notify.getPreferredSize().height;
    }

    public void addToFollow(ContentPanel notify) {
        alterToFitSize(notify);
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
            ContentPanel notifyPanel2 = new ContentPanel(plurk, plurkpool);
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

            Dimension size = this.getSize();
            notificationsDialog.setSize(size);
//            jButton_Close.addActionListener(notificationsDialog);
        }
        return notificationsDialog;
    }
    private NotificationsDialog notificationsDialog;

    class NotificationsDialog extends StandardDialog {

        private NotificationsPanel notificationsPanel;

        NotificationsDialog(NotificationsPanel notificationsPanel, Frame owener) {
            super(notificationsPanel, owener);
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
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            if (e.getSource() == jButton_Close) {
//                this.setVisible(false);
//            }
//        }
    }
}
