/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import plurker.ui.AboutFrame;

/**
 *
 * @author skyforceshen
 */
public class HyperlinkHandler implements HyperlinkListener {

    private static HyperlinkHandler hyperlinkHandler;

    public static HyperlinkHandler getInstance() {
        if (null == hyperlinkHandler) {
            hyperlinkHandler = new HyperlinkHandler();
        }
        return hyperlinkHandler;
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent e) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                            URI uri = e.getURL().toURI();
                            Desktop desktop = Desktop.getDesktop();
                            desktop.browse(uri);
                        }
                    } catch (URISyntaxException | IOException ex) {
                        Logger.getLogger(AboutFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
    }
}
