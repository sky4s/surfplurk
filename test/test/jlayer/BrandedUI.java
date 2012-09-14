/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.jlayer;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.plaf.LayerUI;

public class BrandedUI {

    private static Color PALE_BLUE = new Color(0.0f, 0.0f, 1.0f, 0.3f);
    private static Color PALE_RED = new Color(1.0f, 0.0f, 0.0f, 0.3f);
    private static Font BRAND_FONT = new Font("Arial", Font.BOLD, 18);
    private static String MSG = "My brand";

    private static JLayer<JPanel> createLayer() {
        LayerUI<JPanel> layerUI;
        layerUI = new LayerUI<JPanel>() {

            private Color color = PALE_BLUE;

            public void installUI(JComponent c) {
                super.installUI(c);
                ((JLayer) c).setLayerEventMask(AWTEvent.MOUSE_MOTION_EVENT_MASK);
            }

            public void eventDispatched(AWTEvent e,
                    JLayer<? extends JPanel> l) {
                MouseEvent me = (MouseEvent) e;
                Point pt = SwingUtilities.convertPoint((Component) me.getSource(),
                        me.getX(), me.getY(), l);
                int cx = l.getWidth() / 2;
                int cy = l.getHeight() / 2;
                if (pt.x > cx - 45 && pt.x < cx + 45 && pt.y > cy - 10 && pt.y < cy + 10) {
                    color = PALE_RED;
                } else {
                    color = PALE_BLUE;
                }
                l.repaint();
            }

            public void paint(Graphics g, JComponent c) {
                // Paint the view.
                super.paint(g, c);
                // Paint the brand.
                g.setColor(color);
                g.setFont(BRAND_FONT);
                int width = g.getFontMetrics().stringWidth(MSG);
                int height = g.getFontMetrics().getHeight();
                g.drawString(MSG, (c.getWidth() - width) / 2,
                        c.getHeight() / 2 + height / 4);
            }

            public void uninstallUI(JComponent c) {
                super.uninstallUI(c);
                ((JLayer) c).setLayerEventMask(0);
            }
        };
        // Create a user interface to be decorated.
        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new GridLayout(2, 1));
        JPanel pnlTemp = new JPanel();
        JLabel lblName = new JLabel("Name:");
        pnlTemp.add(lblName);
        JTextField txtName = new JTextField(20);
        pnlTemp.add(txtName);
        pnlMain.add(pnlTemp);
        pnlTemp = new JPanel();
        JLabel lblAddr = new JLabel("Address:");
        pnlTemp.add(lblAddr);
        JTextField txtAddr = new JTextField(20);
        pnlTemp.add(txtAddr);
        pnlMain.add(pnlTemp);
        // Create the layer for the main panel using our custom layerUI.
        return new JLayer<JPanel>(pnlMain, layerUI);
    }

    private static void createAndShowUI() {
        JFrame frame = new JFrame("Branded UI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(createLayer());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Runnable r = new Runnable() {

            public void run() {
                createAndShowUI();
            }
        };
        EventQueue.invokeLater(r);
    }
}