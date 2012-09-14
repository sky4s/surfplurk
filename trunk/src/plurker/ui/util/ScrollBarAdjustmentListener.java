/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui.util;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import plurker.ui.CallBack;

/**
 *
 * @author SkyforceShen
 */
public class ScrollBarAdjustmentListener implements AdjustmentListener, CallBack {

    public interface TriggerInterface {

        public void trigger(boolean topProcess, JPanel panel, CallBack callBack);
    };
    private boolean doListen = true;
    private int prevalue = -1;
    private JPanel panel;
    private boolean newerProcess = true;
    private TriggerInterface triggerInterface;

    public ScrollBarAdjustmentListener(JPanel targetPanel, boolean newerProcess, TriggerInterface triggerInterface) {
        this.panel = targetPanel;
        this.newerProcess = newerProcess;
        this.triggerInterface = triggerInterface;
    }

    @Override
    public void callback() {
        doListen = true;
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        JScrollBar source = (JScrollBar) e.getSource();
        if (!doListen || 0 == panel.getComponentCount()) {
            return;
        }
        doListen = false;
        JScrollBar verticalScrollBar = (JScrollBar) e.getSource();
        int value = e.getValue();
        if (value == 0) {
            //頂端
            if (value == prevalue || -1 == prevalue) {
                //重複過就不行
                prevalue = value;
                doListen = true;
                return;
            }
            //擋住不要再監聽新的
            triggerInterface.trigger(true, panel, this);
        } else if (value == (verticalScrollBar.getMaximum() - verticalScrollBar.getVisibleAmount())) {
            //底端
            if (value == prevalue || -1 == prevalue) {
                prevalue = value;
                doListen = true;
                return;
            }
            triggerInterface.trigger(false, panel, this);
        } else {
            prevalue = value;
            doListen = true;
        }

    }
}