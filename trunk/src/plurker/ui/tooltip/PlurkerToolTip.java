/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui.tooltip;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.Timer;
import plurker.ui.GUIUtil;

/**
 *
 * @author SkyforceShen
 */
public class PlurkerToolTip extends JWindow implements ActionListener {

    class ToolTipMouseListener extends MouseAdapter {

        @Override
        public void mouseExited(MouseEvent e) {
//            System.out.println("exited");
            if (null != timer) {
                timer.restart();
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
//            System.out.println("enter");
            if (null != timer) {
                timer.stop();
            }
        }
    }

    private PlurkerToolTip(ToolTipPanel panel) {
        initComponents(panel);
//        this.addMouseMotionListener(this);
        this.addMouseListener(new ToolTipMouseListener());
    }
    private ToolTipPanel toolTipPanel;

//    public ToolTipPanel getToolTipPanel() {
//        return toolTipPanel;
//    }

    private void initComponents(ToolTipPanel panel) {
        this.toolTipPanel = panel;
//        JRootPane rootPane1 = this.getRootPane();
        Container contentPane = this.getContentPane();
        if (null == panel) {
//            this.getContentPane().add(new PlurkToolTipPanel());
        } else {
            contentPane.removeAll();
            contentPane.add(panel);
        }
        this.setSize(this.getContentPane().getPreferredSize());
    }

    public final static PlurkerToolTip getInstance(Object index, ToolTipPanel content) {
        PlurkerToolTip tooltip = tooltipMap.get(index);
        if (null == tooltip) {
            tooltip = new PlurkerToolTip(content);
            tooltipMap.put(index, tooltip);
        } else {
            if (tooltip.toolTipPanel != content) {
                tooltip.setVisible(false);
                tooltip.initComponents(content);
            }
        }

        return tooltip;
    }
    private static Map<Object, PlurkerToolTip> tooltipMap = new HashMap<>();
    private Timer timer;

//    private void resetTimer() {
//        timer.restart();
//    }
    public void begin() {
        if (null != timer) {
            timer.restart();
        } else {
            timer = new Timer(3000, this);
            timer.setRepeats(false);
            timer.start();
        }
        this.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.setVisible(false);
    }

    public static void main(String[] args) {
//        GUIUtil.initGUI();
//        JPanel panel = new JPanel();
//        JLabel label = new JLabel("1234");
//        panel.add(label);
////        new PlurkerToolTip(panel).setVisible(true);
//        PlurkerToolTip.getInstance(0, panel).setVisible(true);
//        new PlurkerToolTip(new PlurkToolTipPanel()).setVisible(true);
    }
}
