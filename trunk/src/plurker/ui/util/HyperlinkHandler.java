/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
//    public static boolean openToBrowser;

    public final static HyperlinkEvent modifyToActivatedType(HyperlinkEvent e) {
        HyperlinkEvent result = new HyperlinkEvent(e.getSource(), HyperlinkEvent.EventType.ACTIVATED, e.getURL(), e.getDescription(), e.getSourceElement(), e.getInputEvent());
        return result;
    }

    public static void hyperlinkUpdate(HyperlinkEvent e, boolean openLinkToBrowser) {
//        openToBrowser = openLinkToBrowser;
        mode = openLinkToBrowser ? Mode.Desktop : Mode.EditorPane;
        getInstance().hyperlinkUpdate(e);
    }

    public static void hyperlinkUpdate(HyperlinkEvent e, Mode openmode) {
        mode = openmode;
        getInstance().hyperlinkUpdate(e);
    }
    public static Mode mode = Mode.Desktop;

    public static enum Mode {

        EditorPane, DJSwing, Desktop
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (Desktop.isDesktopSupported()) {
            try {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    URI uri = e.getURL().toURI();
                    switch (mode) {
                        case Desktop:
                            Desktop desktop = Desktop.getDesktop();
                            desktop.browse(uri);
                            break;
                        case EditorPane:
                            break;
                        case DJSwing:
                            break;
                    }
//                    if (openToBrowser) {
//                        Desktop desktop = Desktop.getDesktop();
//                        desktop.browse(uri);
//                    } else {
//                    }
                }
            } catch (URISyntaxException | IOException ex) {
                Logger.getLogger(AboutFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) throws MalformedURLException {
        HyperlinkHandler instance = HyperlinkHandler.getInstance();
        HyperlinkEvent e = new HyperlinkEvent(instance, HyperlinkEvent.EventType.ACTIVATED, new URL("http://google.com"));
        instance.hyperlinkUpdate(e);
    }
}
