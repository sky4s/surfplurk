/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui;

import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.Updater;
import com.panayotis.jupidator.UpdaterException;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import plurker.source.PlurkFormater;

/**
 *
 * @author SkyforceShen
 */
public class AboutFrame extends javax.swing.JFrame {

    public final static String Version = "0.3 (Build 20120926)";

    /**
     * Creates new form AboutFrame
     */
    public AboutFrame() {
        initComponents();
        this.jEditorPane1.setText(
                "<html>SurfPlurk (2012) Version: " + Version + "<br>"
                + "<a href=\"http://code.google.com/p/surfplurk/\">" + "官方網站" + " http://code.google.com/p/surfplurk/</a><br>"
                + "<a href=\"http://www.plurk.com/goplurker/\">" + "噗浪" + " http://www.plurk.com/goplurker/</a><br>"
                + "</html>");
        this.jEditorPane1.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                            URI uri = e.getURL().toURI();
                            Desktop.getDesktop().browse(uri);
                        }
                    } catch (URISyntaxException | IOException ex) {
                        Logger.getLogger(AboutFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }


            }
        });

//        File versionFile = new File(VersionFilename);
//        if (versionFile.exists()) {
//            try {
//                FileReader reader = new FileReader(versionFile);
//                BufferedReader breader = new BufferedReader(reader);
//                String version = breader.readLine();
//                this.jTextArea1.setText("Plurker version: " + version);
//            } catch (FileNotFoundException ex) {
//                Logger.getLogger(AboutFrame.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (IOException ex) {
//                Logger.getLogger(AboutFrame.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    }
//    public final static String VersionFilename = "version.txt";

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("plurker/ui/Bundle"); // NOI18N
        setTitle(bundle.getString("AboutFrame.title")); // NOI18N
        setPreferredSize(new java.awt.Dimension(350, 150));

        jButton2.setText(bundle.getString("AboutFrame.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2);

        jButton1.setText(bundle.getString("AboutFrame.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jEditorPane1.setEditable(false);
        jEditorPane1.setContentType("text/html"); // NOI18N
        jScrollPane2.setViewportView(jEditorPane1);

        jPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        MyAppUpdate myAppUpdate = new MyAppUpdate();
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AboutFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AboutFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AboutFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AboutFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AboutFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

    class MyAppUpdate implements UpdatedApplication {

        public MyAppUpdate() {
            try {
                new Updater(
                        "http://surfplurk.googlecode.com/svn/trunk/update/update.xml",
                        "./", ".surfplurk",
                        2,
                        "0.2",
                        this).actionDisplay();
            } catch (UpdaterException ex) {
                ex.printStackTrace();
            }
        }

        public boolean requestRestart() {
            return true;
//        return check_if_we_can_restart();
        }

        public void receiveMessage(String message) {
            System.err.println(message);
        }
    }
}
