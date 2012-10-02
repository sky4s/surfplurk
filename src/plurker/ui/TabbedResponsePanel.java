/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui;

import com.google.jplurk_oauth.data.Plurk;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.json.JSONException;
import org.sexydock.tabs.DefaultTab;
import org.sexydock.tabs.ITab;
import org.sexydock.tabs.ITabbedPaneDnDPolicy;
import org.sexydock.tabs.TabbedPane;
import org.sexydock.tabs.event.ITabbedPaneListener;
import org.sexydock.tabs.event.TabbedPaneEvent;
import org.sexydock.tabs.jhrome.JhromeTabUI;
import plurker.ui.util.GUIUtil;
import shu.util.Persistence;

/**
 *
 * @author SkyforceShen
 */
public class TabbedResponsePanel extends javax.swing.JPanel implements FollowerIF {

    public static void main(String[] args) {
        GUIUtil.initGUI();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                Plurk plurk = (Plurk) Persistence.readObjectAsXML("plurk.obj");
                ContentPanel content = new ContentPanel(plurk, null);
                TabbedResponsePanel tabbed = new TabbedResponsePanel();
                tabbed.setCurrentFollow(content);
                frame.add(tabbed);
                frame.setSize(400, 600);
                frame.setVisible(true);
            }
        });

    }

    /**
     * Creates new form TabbedResponsePanel
     */
    public TabbedResponsePanel() {
        initComponents();

        //jhrome的 tab
        tabbedPane = new TabbedPane();
        tabbedPane.addTabbedPaneListener(tabbedPaneListener);
        tabbedPane.setBackground(Color.white);

        tabbedPane.setUseUniformWidth(false);
        tabbedPane.getNewTabButton().setVisible(false);
        //先關掉dnd...因為拖出新視窗會因為beautyeye無法生出新視窗相關size而fail
//        tabbedPane.setDnDPolicy(new ITabbedPaneDnDPolicy() {
//            @Override
//            public boolean isTearAwayAllowed(TabbedPane tabbedPane, ITab tab) {
//                return false;
//            }
//
//            @Override
//            public boolean isSnapInAllowed(TabbedPane tabbedPane, ITab tab) {
//                return false;
//            }
//        });
    }
    private TabbedPane tabbedPane;
    private TabbedPaneListener tabbedPaneListener = new TabbedPaneListener();
    public final static String Current = "目前";
//    public final static String NewPlurk = "發噗";
//    private TabbedPane tabbedPane;
    private DefaultTab currentPlurklTab;
    private ResponsePanel currentResponsePanel = new ResponsePanel();

    public boolean isInFollow(long plurkID) {
        List<ITab> tabs = tabbedPane.getTabs();
        try {
            for (ITab tab : tabs) {
                DefaultTab dtab = (DefaultTab) tab;
                String text = dtab.getLabel().getText();
                if (text.equals(Current)) {
                    continue;
                }
                Component content = tab.getContent();
                ResponsePanel responsePanel = (ResponsePanel) content;
                ContentPanel rootContentPanel = responsePanel.getRootContentPanel();
                Plurk plurk1 = rootContentPanel.getPlurk();
                if (plurk1.getPlurkId() == plurkID) {
                    return true;
                }
            }
            return false;
        } catch (JSONException ex) {
            Logger.getLogger(TabbedResponsePanel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean isInFollow(Plurk plurk) {
        try {
            return isInFollow(plurk.getPlurkId());
        } catch (JSONException ex) {
            Logger.getLogger(TabbedResponsePanel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    class TabbedPaneListener implements ITabbedPaneListener {

        @Override
        public void onEvent(TabbedPaneEvent event) {
            TabbedPane tabbedPane1 = event.getTabbedPane();
            ITab selectedTab = tabbedPane1.getSelectedTab();
            if (selectedTab instanceof DefaultTab) {
                DefaultTab defaultTab = (DefaultTab) selectedTab;
                //做reset
                defaultTab.setUI(new JhromeTabUI());

            }
        }
    }

    DefaultTab addToTabbedPane(String title, ResponsePanel responsePanel, boolean enableCloseButton) {
        title = (null == title) ? responsePanel.getTabTitle() : title;
        DefaultTab tab = new DefaultTab(title, responsePanel);
        responsePanel.setDefaultTab(tab);
        tab.getCloseButton().setVisible(enableCloseButton);
        int tabCount = tabbedPane.getTabCount();
        tabbedPane.addTab(tabCount, tab, false);
        Dimension size = tab.getSize();
        return tab;
    }

    void setCurrentFollow(final ContentPanel contentPanel) {
//        this.getRootPane().
//        Container contentPane = this.getRootPane().getContentPane();
        if (!jPanel1.isAncestorOf(tabbedPane)) {
            jPanel1.removeAll();
            jPanel1.add(tabbedPane, java.awt.BorderLayout.CENTER);
        }

        if (null == currentPlurklTab) {
            currentPlurklTab = addToTabbedPane(Current, currentResponsePanel, false);
        }

        currentResponsePanel.setRootContentPanel(contentPanel);
        tabbedPane.setSelectedTab(currentPlurklTab);
//        System.out.println(currentPlurklTab.getSize() + " " + currentPlurklTab.getPreferredSize());
//        currentPlurklTab.setSize(currentPlurklTab.getPreferredSize());
        if (PlurkerApplication.debugMode && !new File("plurk.obj").exists()) {
            Persistence.writeObjectAsXML(contentPanel.getPlurk(), "plurk.obj");
        }
    }

    boolean addNewFollow(ContentPanel plurkPanel) {
//        Container contentPane = this.getRootPane().getContentPane();
        if (!jPanel1.isAncestorOf(tabbedPane)) {
            jPanel1.removeAll();
            jPanel1.add(tabbedPane, java.awt.BorderLayout.CENTER);
        }

        if (isInFollow(plurkPanel.getPlurk())) {
            return false;
        }
//        isInFollow()

        ResponsePanel responsePanel = new ResponsePanel();
        responsePanel.setRootContentPanel(plurkPanel);
        DefaultTab tab = addToTabbedPane(null, responsePanel, true);
        tabbedPane.setSelectedTab(tab);
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(400, 600));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(400, 600));
        jPanel1.setLayout(new java.awt.BorderLayout());
        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
