/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui.util;

import javax.swing.JFrame;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author SkyforceShen
 */
public class SheetableJFrameHelper {

    private JFrame frame;

    public SheetableJFrameHelper(JFrame frame) {
        this.frame = frame;
        glass = (JPanel) frame.getGlassPane();

    }
    private JComponent sheet;
    private JPanel glass;

    public JComponent showJDialogAsSheet(JDialog dialog) {
        sheet = (JComponent) dialog.getContentPane();
//        sheet.setBackground(Color.red);
        glass.setLayout(new GridBagLayout());
        sheet.setBorder(new LineBorder(Color.black, 1));
        glass.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        glass.add(sheet, gbc);
        gbc.gridy = 1;
        gbc.weighty = Integer.MAX_VALUE;
        glass.add(Box.createGlue(), gbc);
        glass.setVisible(true);
        return sheet;

    }

    public void hideSheet() {
        glass.setVisible(false);
    }
}
