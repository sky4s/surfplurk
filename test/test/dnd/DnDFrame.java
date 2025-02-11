/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.dnd;

/**
 *
 * @author SkyforceShen
 */
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

public class DnDFrame extends javax.swing.JFrame implements DropTargetListener {
    
    private DefaultListModel listModel = new DefaultListModel();
    private DropTarget dropTarget;

    /**
     * Creates new form DnDFrame
     */
    public DnDFrame() {
        initComponents();
        dropTarget = new DropTarget(list, this);
        list.setModel(listModel);
        list.setDragEnabled(true);
        list.setTransferHandler(new FileTransferHandler());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());
        
        jLabel1.setText("Files:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(jLabel1, gridBagConstraints);
        
        jScrollPane1.setViewportView(list);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(jScrollPane1, gridBagConstraints);
        
        pack();
    }// </editor-fold>
    // Variables declaration - do not modify
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList list;
    // End of variables declaration

    public void dragEnter(DropTargetDragEvent arg0) {
        // nothing
    }
    
    public void dragOver(DropTargetDragEvent arg0) {
        // nothing
    }
    
    public void dropActionChanged(DropTargetDragEvent arg0) {
        // nothing
    }
    
    public void dragExit(DropTargetEvent arg0) {
        // nothing
    }
    
    public void drop(DropTargetDropEvent evt) {
        int action = evt.getDropAction();
        evt.acceptDrop(action);
        try {
            Transferable data = evt.getTransferable();
            if (data.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                List<File> files = (List<File>) data.getTransferData(
                        DataFlavor.javaFileListFlavor);
                for (File file : files) {
                    listModel.addElement(file);
                }
            }
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            evt.dropComplete(true);
        }
    }
    
    private class FileTransferHandler extends TransferHandler {
        
        @Override
        protected Transferable createTransferable(JComponent c) {
            JList list = (JList) c;
            List<File> files = new ArrayList<File>();
            for (Object obj : list.getSelectedValuesList()) {
                files.add((File) obj);
            }
            return new FileTransferable(files);
        }
        
        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }
    }
    
    private class FileTransferable implements Transferable {
        
        private List<File> files;
        
        public FileTransferable(List<File> files) {
            this.files = files;
        }
        
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.javaFileListFlavor};
        }
        
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(DataFlavor.javaFileListFlavor);
        }
        
        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException, IOException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return files;
        }
    }
    
    public static void main(String[] args) {
        new DnDFrame().setVisible(true);
    }
}
