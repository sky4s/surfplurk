/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui.util;

import javax.swing.JComponent;

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

    public void addContent(JComponent content) {
    }
}
