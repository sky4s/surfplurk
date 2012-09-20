/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui.notify;

import com.google.jplurk_oauth.data.Plurk;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import plurker.source.PlurkPool;
import plurker.source.PlurkSourcer;
import plurker.ui.NotifyPanel2;
import plurker.ui.PlurkerApplication;
import shu.util.Persistence;

/**
 *
 * @author SkyforceShen
 */
public class NotificationPanel extends javax.swing.JPanel {

    private NotificationWindow window;

    public JWindow getJWindow() {
        if (null == window) {
            window = new NotificationWindow(this);
            window.setSize(this.getSize());
        }
        return window;
    }
    public static int DisappearWaitTime = 10000;
//    public static int DisappearShortWaitTime = 1000;

    public static void main(String[] args) {
        PlurkSourcer.setDoValidToken(false);
        PlurkSourcer plurkSourcerInstance = PlurkerApplication.getPlurkSourcerInstance();
        PlurkPool plurkpool = new PlurkPool(plurkSourcerInstance);
        Plurk plurk = (Plurk) Persistence.readObjectAsXML("plurk.obj");
//        NotifyPanel2 notifyPanel2 = new NotifyPanel2("1234", 300);
        NotifyPanel2 notifyPanel2 = new NotifyPanel2(plurk, plurkpool);
        notifyPanel2.updateWidth(300);

//        JLabel label = new JLabel("123");
        NotificationPanel panel = new NotificationPanel(notifyPanel2, 300);
        JWindow jWindow = panel.getJWindow();
//        jWindow.setBounds(0, 0, 100, 100);
//        jWindow.addComponentListener(new ComponentAdapter() {
//            public void componentHidden(ComponentEvent e) {
//                System.out.println(e);
//            }
//        });


        jWindow.setVisible(true);
    }

//    @Override
//    public void eventDispatched(AWTEvent event) {
//        if (event instanceof MouseEvent) {
//            MouseEvent mouseevent = (MouseEvent) event;
//            if (mouseevent.getID() == MouseEvent.MOUSE_EXITED) {
//                Component component = mouseevent.getComponent();
//                System.out.println(mouseevent);
//            }
//        }
//    }
    class NotificationWindow extends JWindow implements ActionListener, AWTEventListener {

        Timer dispearTimer;
        NotificationPanel notifyPanel;

        NotificationWindow(NotificationPanel panel) {
            this.notifyPanel = panel;
            getContentPane().add(panel);
            dispearTimer = new Timer(DisappearWaitTime, this);
            dispearTimer.start();
            panel.getjButton_Close().addActionListener(this);
//            this.addMouseMotionListener(new NotifyMouseMotionListener());
//            this.addMouseListener(new NotifyMouseListener(this));
            Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        }

        private boolean isMouseInWindow() {
            PointerInfo pointerInfo = MouseInfo.getPointerInfo();
            Point location = pointerInfo.getLocation();
            SwingUtilities.convertPointFromScreen(location, this);
            Component component = this.getComponentAt(location);
            boolean mouseInWindow = (null != component) ? SwingUtilities.isDescendingFrom(component, this) : false;
            return mouseInWindow;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
//            System.out.println(e + " " + e.getSource());
            boolean fromCloseButton = notifyPanel.getjButton_Close() == e.getSource();
            //執行關閉的動作
            if (!isMouseInWindow() || fromCloseButton) {
                removeAll();
                setVisible(false);
                if (null != dispearTimer) {
                    dispearTimer.stop();
                    dispearTimer = null;
                }
            } else {
                dispearTimer.restart();
            }
        }

        @Override
        public void eventDispatched(AWTEvent event) {
            if (event instanceof MouseEvent) {
                MouseEvent mouseevent = (MouseEvent) event;
                if (mouseevent.getID() == MouseEvent.MOUSE_EXITED) {
                    if (!isMouseInWindow()) {
                        //若離開了視窗
                        notifyPanel.jPanel1.setVisible(false);
//                        if (null != dispearTimer) {
//                            dispearTimer.setInitialDelay(DisappearShortWaitTime);
//                            dispearTimer.setDelay(DisappearShortWaitTime);
//                            dispearTimer.restart();
//                        }
                    }
                } else if (mouseevent.getID() == MouseEvent.MOUSE_ENTERED) {
                    if (SwingUtilities.isDescendingFrom(mouseevent.getComponent(), this)) {
                        notifyPanel.jPanel1.setVisible(true);
                    }
                }

            }
        }
    }

    /**
     * Creates new form NotificationPanel
     */
    public NotificationPanel(JComponent component, int width) {
        initComponents();
        int contentHeight = getContentHeight(component, width);
        Dimension dimension = new Dimension(width, contentHeight);
        component.setSize(dimension);
        component.setPreferredSize(dimension);
        this.setSize(dimension);
        this.setPreferredSize(dimension);
        this.add(component, java.awt.BorderLayout.CENTER);
//        this.jButton_Close.setVisible(false);
        this.jPanel1.setVisible(false);
//        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    private NotificationPanel(JComponent component) {
        initComponents();
        this.add(component, java.awt.BorderLayout.CENTER);
    }

    private static int getContentHeight(JComponent component, int width) {
        NotificationPanel tmppanel = new NotificationPanel(component);
        tmppanel.setSize(width, Short.MAX_VALUE);
        return tmppanel.getPreferredSize().height;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton_Close = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setLayout(new java.awt.BorderLayout());

        jButton_Close.setIcon(new javax.swing.ImageIcon(getClass().getResource("/plurker/ui/notify/dialog_close.png"))); // NOI18N
        jButton_Close.setBorder(null);
        jButton_Close.setName(""); // NOI18N
        jButton_Close.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/plurker/ui/notify/dialog_cancel.png"))); // NOI18N
        jPanel1.add(jButton_Close);

        add(jPanel1, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_Close;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

    public JButton getjButton_Close() {
        return jButton_Close;
    }
}
