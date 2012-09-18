/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui.notify;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JWindow;
//import plurker.ui.FixedHTMLEditorKit;
import plurker.ui.GUIUtil;

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
    private LinkedList<SlideInNotification> linkedList = new LinkedList<>();
    private HashMap<JWindow, SlideInNotification> map = new HashMap<>();
    public final static int NotifyWidth = 200;

    class NotifyComponentListener extends ComponentAdapter {

        @Override
        public void componentHidden(ComponentEvent e) {
            Component component = e.getComponent();
            SlideInNotification slide = map.get((JWindow) component);
            linkedList.remove(slide);
            //            linkedList.remove((SlideInNotification)  component);
        }
    };
    private NotifyComponentListener notifyComponentListener = new NotifyComponentListener();

//    public static int getContentHeight(NotificationPanel panel, int width) {
////        if (null == dummyEditorPane) {
//        JWindow window = new JWindow();
//
//        window.setSize(width, Short.MAX_VALUE);
////        dummyEditorPane.setText(content);
//        window.getContentPane().add(panel);
//        window.pack();
// 
//        return window.getPreferredSize().height;
//    }
    public void addContent(JComponent content) {
        NotificationPanel notity = new NotificationPanel(content, NotifyWidth);
        SlideInNotification slide = new SlideInNotification(notity);

        JWindow window = slide.getWindow();
        window.addComponentListener(notifyComponentListener);

        SlideInNotification last = linkedList.size() != 0 ? linkedList.getLast() : null;
        linkedList.add(slide);
        map.put(window, slide);
        Dimension lastsize = last != null ? last.getWindow().getSize() : null;

        int starty = (null != last) ? last.getStartY() - lastsize.height : desktopBounds.y + desktopBounds.height;
        int desktopwidth = desktopBounds.width;
        int startx = desktopwidth - NotifyWidth;
        slide.showAt(startx, starty);
    }
    private static Rectangle desktopBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

    public static void main(String[] args) {
        NotificationManager instance = NotificationManager.getInstance();
        for (int x = 0; x < 20; x++) {
            instance.addContent(new JLabel(Integer.toString(x + 1)));
        }
    }
}
