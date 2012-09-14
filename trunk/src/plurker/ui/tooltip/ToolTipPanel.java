/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui.tooltip;

import javax.swing.JPanel;
import plurker.source.PlurkPool;
import plurker.source.PlurkSourcer;
import plurker.ui.ContentPanel;

/**
 *
 * @author SkyforceShen
 */
public class ToolTipPanel extends JPanel {

    protected ContentPanel contentPanel;
    protected PlurkPool plurkPool;
    protected PlurkSourcer sourcer;

    ToolTipPanel(PlurkPool plurkPool, ContentPanel contentPanel) {
        this.plurkPool = plurkPool;
        this.contentPanel = contentPanel;
        this.sourcer = plurkPool.getSourcer();
    }
}
