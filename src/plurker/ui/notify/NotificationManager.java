/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui.notify;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JWindow;
//import plurker.ui.FixedHTMLEditorKit;

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
    private LinkedList<NotificationPanel> linkedList = new LinkedList<>();
    private HashMap<JWindow, NotificationPanel> map = new HashMap<>();
    public final static int NotifyWidth = 200;

    class NotifyComponentListener extends ComponentAdapter {

        @Override
        public void componentHidden(ComponentEvent e) {
            if (e.getComponent() instanceof JWindow) {
                JWindow window = (JWindow) e.getComponent();
                NotificationPanel notify = map.get(window);
                map.remove(window);
                linkedList.remove(notify);
                window.dispose();
            }
        }
    };
    private NotifyComponentListener notifyComponentListener = new NotifyComponentListener();

    public void addContent(JComponent content) {
        NotificationPanel notity = new NotificationPanel(content, NotifyWidth);
        JWindow jWindow = notity.getJWindow();
        jWindow.addComponentListener(notifyComponentListener);
        NotificationPanel last = linkedList.size() != 0 ? linkedList.getLast() : null;
        linkedList.add(notity);
        map.put(jWindow, notity);

        int y = (null != last) ? last.getJWindow().getLocation().y - jWindow.getHeight()/*- last.getHeight()*/ : desktopBounds.y + desktopBounds.height - jWindow.getHeight();
        int x = desktopBounds.width - NotifyWidth;
        jWindow.setLocation(x, y);
        jWindow.setVisible(true);
    }
    private static Rectangle desktopBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

    public static void main(String[] args) throws InterruptedException {
        NotificationManager instance = NotificationManager.getInstance();
        for (int x = 0; x < 15; x++) {
            instance.addContent(new JLabel(Integer.toString(x + 1)));
        }
        Thread.currentThread().sleep(5000);
        for (int x = 10; x < 25; x++) {
            instance.addContent(new JLabel(Integer.toString(x + 1)));
        }
    }
}
