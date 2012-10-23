/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui.notify;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import plurker.ui.util.GUIUtil;

class StandardDialog extends JDialog implements ActionListener/*, AWTEventListener*/ {

    StandardDialog(JPanel contentPanel, JPanel closePanel) {
        this(contentPanel, closePanel, null);
    }

    StandardDialog(JPanel contentPanel, JPanel closePanel, Frame owener) {
        super(owener);
        this.contentPanel = contentPanel;
        this.closePanel = closePanel;
        this.setAlwaysOnTop(false);
        getContentPane().add(contentPanel);
//        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK);
    }
    protected JPanel contentPanel;
    private JPanel closePanel;

    @Override
    public void actionPerformed(ActionEvent e) {
        removeAll();
        setVisible(false);
        dispose();
    }
//    JFrame frame;

//    @Override
//    public void eventDispatched(AWTEvent event) {
////        System.out.println(event);
//        if (event instanceof MouseEvent) {
//            MouseEvent mouseevent = (MouseEvent) event;
//            int id = mouseevent.getID();
//            if (MouseEvent.MOUSE_EXITED == id) {
//                if (!isMouseInWindow()) {
//                    //若離開了視窗
//                    closePanel.setVisible(false);
////                    this.setAlwaysOnTop(false);
//                }
//            } else if (MouseEvent.MOUSE_ENTERED == id) {
//                Component component = mouseevent.getComponent();
//                if (null != component && SwingUtilities.isDescendingFrom(component, this)) {
//                    closePanel.setVisible(true);
////                    this.setAlwaysOnTop(true);
//
//                }
//            } else if (MouseEvent.MOUSE_MOVED == id) {
//                Component component = mouseevent.getComponent();
//                if (null != component && SwingUtilities.isDescendingFrom(component, this)) {
//                }
//            }
//
//            if (MouseEvent.MOUSE_WHEEL == id) {
//                System.out.println(mouseevent);
//            }
//
//        }
//    }
    public boolean isMouseInWindow() {
        return GUIUtil.isMouseInWindow(this.getBounds());
    }
}

/**
 *
 * @author SkyforceShen
 */
class StandardWindow extends JWindow implements ActionListener, AWTEventListener {

    StandardWindow(JPanel contentPanel, JPanel closePanel) {
        this(contentPanel, closePanel, null);
    }

    StandardWindow(JPanel contentPanel, JPanel closePanel, Frame owener) {
        super(owener);
//        this.setType(Type.UTILITY);
        this.contentPanel = contentPanel;
        this.closePanel = closePanel;
        getContentPane().add(contentPanel);
        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK);
    }
    protected JPanel contentPanel;
    private JPanel closePanel;

    @Override
    public void actionPerformed(ActionEvent e) {
        removeAll();
        setVisible(false);
        dispose();
    }
//    JFrame frame;

    @Override
    public void eventDispatched(AWTEvent event) {
//        System.out.println(event);
        if (event instanceof MouseEvent) {
            MouseEvent mouseevent = (MouseEvent) event;
            int id = mouseevent.getID();
            if (MouseEvent.MOUSE_EXITED == id) {
                if (!isMouseInWindow()) {
                    //若離開了視窗
                    closePanel.setVisible(false);
//                    this.setAlwaysOnTop(false);
                }
            } else if (MouseEvent.MOUSE_ENTERED == id) {
                Component component = mouseevent.getComponent();
                if (null != component && SwingUtilities.isDescendingFrom(component, this)) {
                    closePanel.setVisible(true);
//                    this.setAlwaysOnTop(true);

                }
            } else if (MouseEvent.MOUSE_MOVED == id) {
                Component component = mouseevent.getComponent();
                if (null != component && SwingUtilities.isDescendingFrom(component, this)) {
                }
            }

//            if (MouseEvent.MOUSE_WHEEL == id) {
//                System.out.println(mouseevent);
//            }

        }
    }

    public boolean isMouseInWindow() {
        return GUIUtil.isMouseInWindow(this.getBounds());
//        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
//        Point location = pointerInfo.getLocation();
//        Rectangle bounds = this.getBounds();
//        boolean mouseInWindow = bounds.contains(location);
//        return mouseInWindow;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("frame");
        frame.setSize(600, 600);
        frame.setVisible(true);
//                JFrame frame2 = new JFrame();
//        frame2.setVisible(true);
//        JDialog dialog = new JDialog(null, "dialog", false);
        JDialog dialog = new JDialog();
        dialog.setModal(false);
        dialog.setTitle("dialog");
//        dialog.setResizable(false);
//        dialog.setDefaultCloseOperation(WIDTH);
//        dialog.setUndecorated(true);
//        dialog.setOpacity(0.5f);
//        dialog.setAlwaysOnTop(false);
        dialog.setSize(300, 300);
        dialog.setVisible(true);
    }
}
