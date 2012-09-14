/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui.util;

import java.awt.AWTEvent;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;

/**
 *
 * @author SkyforceShen
 */
public class DirectScroll implements AWTEventListener {

    public static int PopupMoveTolerance = 5;
    private JScrollBar scrollBar;
    private int directScrolllY;
    private boolean inverse;

    public static void initDirectScroll(JScrollBar scrollBar, boolean inverse) {
        DirectScroll directScroll = new DirectScroll(scrollBar, true);
    }

    public DirectScroll(JScrollBar scrollBar, boolean inverse) {
        this.scrollBar = scrollBar;
        this.inverse = inverse;

        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    public void eventDispatched(AWTEvent event) {
        if (event instanceof MouseEvent) {
            MouseEvent mouseevent = (MouseEvent) event;

            if (mouseevent.getID() == MouseEvent.MOUSE_PRESSED && mouseevent.getButton() == MouseEvent.BUTTON3) {
                directScrolllY = mouseevent.getYOnScreen();
            }

            if (mouseevent.getID() == MouseEvent.MOUSE_DRAGGED && (mouseevent.getModifiers() & MouseEvent.BUTTON3_MASK) == MouseEvent.BUTTON3_MASK) {
                int nowY = mouseevent.getYOnScreen();
                int deltaY = nowY - directScrolllY;

                int value = scrollBar.getValue();
                if (inverse) {
                    deltaY = -deltaY;
                }
                scrollBar.setValue(value + deltaY);

                directScrolllY = nowY;
            }

        }
    }

    public static class PopupMenuListener extends MouseAdapter implements MouseMotionListener {

        public PopupMenuListener(JPopupMenu jPopupMenu1) {
            this.jPopupMenu1 = jPopupMenu1;
        }
        private boolean canPopup = false;
        private JPopupMenu jPopupMenu1;

        @Override
        public void mouseDragged(MouseEvent e) {
            if (null == pressedPoint) {
                return;
            }
            Point point = e.getLocationOnScreen();
            if (Math.abs(point.x - pressedPoint.x) < PopupMoveTolerance && Math.abs(point.y - pressedPoint.y) < PopupMoveTolerance) {
                canPopup = true;
            } else {
                canPopup = false;
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            canPopup = true;
        }
        private Point pressedPoint;

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                pressedPoint = e.getLocationOnScreen();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (true == canPopup && e.isPopupTrigger()) {
                jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
            } else if (false == canPopup) {
                canPopup = true;
            }
        }
    }
}