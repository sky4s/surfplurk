/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;

/**
 *
 * @author egg0608
 */
public class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {

    public MultiLineCellRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        setFont(table.getFont());
        if (hasFocus) {
            setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
            if (table.isCellEditable(row, column)) {
                setForeground(UIManager.getColor("Table.focusCellForeground"));
                setBackground(UIManager.getColor("Table.focusCellBackground"));
            }
        } else {
            setBorder(new EmptyBorder(1, 2, 1, 2));
        }
        setText((value == null) ? "" : value.toString());
        return this;
    }
}
