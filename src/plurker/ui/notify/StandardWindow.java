/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui.notify;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import plurker.ui.util.GUIUtil;

/**
 *
 * @author SkyforceShen
 */
class StandardWindow extends JWindow implements ActionListener, AWTEventListener {

    StandardWindow(JPanel contentPanel, JPanel closePanel) {
        this.contentPanel = contentPanel;
        this.closePanel = closePanel;
        getContentPane().add(contentPanel);
        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
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
        if (event instanceof MouseEvent) {
            MouseEvent mouseevent = (MouseEvent) event;
            if (mouseevent.getID() == MouseEvent.MOUSE_EXITED) {
                if (!isMouseInWindow()) {
                    //若離開了視窗
                    closePanel.setVisible(false);
                    this.setAlwaysOnTop(false);
                }
            } else if (mouseevent.getID() == MouseEvent.MOUSE_ENTERED) {
                Component component = mouseevent.getComponent();
                if (null != component && SwingUtilities.isDescendingFrom(component, this)) {
                    closePanel.setVisible(true);
                    this.setAlwaysOnTop(true);

                }
            } else if (mouseevent.getID() == MouseEvent.MOUSE_MOVED) {
                Component component = mouseevent.getComponent();
                if (null != component && SwingUtilities.isDescendingFrom(component, this)) {
                }
            }

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
}