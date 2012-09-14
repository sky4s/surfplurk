/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author SkyforceShen
 */
public class ActiveJComponentTableCellRenderer<T extends JComponent> extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {

    private Map<TableCell, T> components;
    private JComponentFactory<T> factory;

    public ActiveJComponentTableCellRenderer() {
        this.components = new HashMap<>();
    }

    public ActiveJComponentTableCellRenderer(JComponentFactory<T> factory) {
        this();
        this.factory = factory;
    }

    public T getComponent(TableCell key) {
        T component = components.get(key);
        if (component == null && factory != null) {
            // lazy-load component
            component = factory.build();
            initialiseComponent(component);
            components.put(key, component);
        }
        return component;
    }

    /**
     * Override this method to provide custom component initialisation code
     *
     * @param component passed in component from getComponent(cell)
     */
    protected void initialiseComponent(T component) {
//        component.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                stopCellEditing();
//            }
//        });
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return getComponent(new TableCell(row, column));
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return getComponent(new TableCell(row, column));
    }

    public void setComponentFactory(JComponentFactory factory) {
        this.factory = factory;
    }
}
