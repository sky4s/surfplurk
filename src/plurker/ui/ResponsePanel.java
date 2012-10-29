/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui;

import plurker.ui.util.GUIUtil;
import com.google.jplurk_oauth.data.*;
import com.google.jplurk_oauth.skeleton.HttpRequestException;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import org.json.JSONException;
import org.sexydock.tabs.DefaultTab;
import org.sexydock.tabs.TabbedPane;
import org.sexydock.tabs.jhrome.JhromeTabUI;
import plurker.image.ImageUtils;
import plurker.source.PlurkFormater;
import plurker.source.PlurkPool;
import plurker.source.PlurkSourcer;
import plurker.ui.util.DirectScroll;
import plurker.ui.util.ScrollBarAdjustmentListener;
import plurker.ui.util.WaitLayerUI;
//import shu.image.ImageUtils;
import shu.util.Persistence;

/**
 *
 * @author Egg Hsu
 */
public class ResponsePanel extends javax.swing.JPanel implements ScrollBarAdjustmentListener.TriggerInterface {

    private boolean startListenComet = false;
    private NewResponseListener newResponseListener;

    private void listenComent() {
        if (!startListenComet && null != plurkPool) {
            plurkPool.addCometChangeListener(new NewResponseListener());
            startListenComet = true;
        }
    }
    private CallBack triggerCallBack;

    /**
     * 整個scrollbar驅動的方式如下 ScrollBarAdjustmentListener監聽scrollbar的動作,
     * 若需要觸發則call這裡的trigger(透過TriggerInterface的實作)
     * 之後..因為ScrollBarAdjustmentListener需要一個callback, 被告知可以繼續監聽 所以trigger
     * 裡面的thread結束後, 要去呼叫這個callback(triggerCallBack)
     *
     * @param topProcess
     * @param panel
     * @param callBack
     */
    @Override
    public void trigger(boolean topProcess, JPanel panel, CallBack callBack) {


        if (!topProcess) {
            if (isFetchThreadRunning()) {
                commentsFetchThread.terminate();
            }
            //接著去抓comment, 因為費時, 所以要用thread
            commentsFetchThread = new CommentsFetchThread(true);
            this.triggerCallBack = callBack;
            System.out.println("trigger");
            commentsFetchThread.start();
        } else {
            callBack.callback();
        }
    }

    public boolean isFetchThreadRunning() {
        return null != commentsFetchThread && commentsFetchThread.isAlive() && !commentsFetchThread.stop;
    }

    private class NewResponseListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            final TreeSet<Comment> newResponseSet = plurkPool.getNewResponseSet();
            final Plurk plurk = rootContentPanel.getPlurk();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        long plurkID = plurk.getPlurkId();
                        for (Comment comment : newResponseSet) {
                            long plurkIDOfComment = comment.getPlurkId();
                            if (plurkID == plurkIDOfComment) {
                                addCommentToUI(comment);
                                updateUnread();
                            }
                        }
                    } catch (JSONException ex) {
                        Logger.getLogger(ResponsePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
//            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(600, 600);
        Plurk plurk = (Plurk) Persistence.readObjectAsXML("plurk.obj");
        PlurkSourcer.setDoValidToken(false);
        PlurkSourcer plurkSourcerInstance = PlurkerApplication.getPlurkSourcerInstance();
        PlurkPool plurkPool = new PlurkPool(plurkSourcerInstance);
        ContentPanel contentPanel = new ContentPanel(plurk, plurkPool);
        ResponsePanel panel = new ResponsePanel(contentPanel);
        frame.setLayout(new java.awt.BorderLayout());
        frame.add(panel, java.awt.BorderLayout.CENTER);
        frame.setVisible(true);
        final JPanel commentsPanel = panel.getCommentsPanel();
        int width = commentsPanel.getWidth();

        commentsPanel.repaint();

    }
    private static BufferedImage refreshImage;
    private static BufferedImage plurkImage;
    private static BufferedImage emoticon_mini_off;
    private PlurkPool plurkPool;
//    private java.util.List<Comment> commentList;

    void updateWidth(int width) {
        if (null != firstPanel) {
            firstPanel.updateWidth(width);
        }
    }

    public final void setRootContentPanel(final ContentPanel rootContentPanel) {
        if (isFetchThreadRunning()) {
            commentsFetchThread.terminate();
        }

        this.reset();
        this.rootContentPanel = rootContentPanel;
        setPlurkPool(rootContentPanel.plurkPool);
        listenComent();

        //首先設定第一頁, 速度應該很快
        firstPanel = new ContentPanel(rootContentPanel);
        firstPanel.updateWidth(getWidth());

        jPanel_Plurk.add(firstPanel, java.awt.BorderLayout.CENTER);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateUI();
            }
        });

        //接著去抓comment, 因為費時, 所以要用thread
        this.commentsAdjustmentListener.stopListen();
        commentsFetchThread = new CommentsFetchThread();
        commentsFetchThread.start();
    }
    private ContentPanel loadingPane;
    public final static String RedFont = "<font color=\"#FF0000\">";

    String getTabTitle() {
        String name = null;
        try {
            Plurk plurk = this.firstPanel.getPlurk();
            long ownerId = plurk.getOwnerId();
            UserInfo userInfo = this.plurkPool.getUserInfo(ownerId);
            name = PlurkFormater.getName(userInfo, true);
//            if (showCommentCount) {
//                int commentCount = getCommentCount();
//                if (isUnread) {
//                    name = name + " " + RedFont + "<b>" + commentCount + "</b><font>";
//                } else {
//                    name = name + " " + commentCount;
//                }
//            }
//            if (isUnread) {
//                name = "<html><body bgcolor=\"red\">" + name + "</body></html>";
//            } else {
            name = "<html>" + name + "</html>";
//            }
            return name;
            //            userInfo.getn
        } catch (JSONException ex) {
            Logger.getLogger(PlurkerApplication.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private void updateUnread() {
        if (null != defaultTab /*&& !defaultTab.getLabel().getText().equals(PlurkerApplication.Current)*/) {
            Container parent = defaultTab.getParent();
            if (parent instanceof TabbedPane) {
                TabbedPane tabbedPane = (TabbedPane) parent;
                if (tabbedPane.getSelectedTab() == defaultTab) {
                    return;
                }
            }
//            String tabTitle = getTabTitle();
//            defaultTab.setTitle(tabTitle);
            JhromeTabUI ui = new JhromeTabUI();
            //把背景設成紅色
            ui.getNormalAttributes().topColor = Color.red;
            defaultTab.setUI(ui);

        }
    }

    public void setDefaultTab(DefaultTab defaultTab) {
        this.defaultTab = defaultTab;
    }
    private DefaultTab defaultTab;
    private CommentsFetchThread commentsFetchThread;
    private ContentPanel whitePanel;
    private static ContentPanel noResponsePanel;
    private ContentPanel rootContentPanel;
    private ContentPanel firstPanel;

    public ContentPanel getRootContentPanel() {
        return rootContentPanel;
    }

    private static void initImage() {
        try {
            if (null == plurkImage) {
                plurkImage = ImageUtils.loadImage("./image/595fd1c19cbfa3545c4268d1c2e4056b.png");
            }
            if (null == emoticon_mini_off) {
                emoticon_mini_off = ImageUtils.loadImage("./image/emoticon_mini_off.png");
            }
            if (null == refreshImage) {
                refreshImage = ImageUtils.loadImageFromURL(ResponsePanel.class.getResource("/plurker/ui/resource/1349158187_refresh.png"));
            }
        } catch (IOException ex) {
            Logger.getLogger(ResponsePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private ScrollBarAdjustmentListener commentsAdjustmentListener;

    public ResponsePanel(ContentPanel rootContentPanel) {
        this(rootContentPanel, Mode.Normal);

    }

    public ResponsePanel(boolean notifyMode) {
        this(null, notifyMode ? Mode.Notify : Mode.Normal);
    }
//    private boolean notifyMode;
    private Mode mode = Mode.Normal;

    public static enum Mode {

        Normal, Notify, Simple
    }

    public ResponsePanel(ContentPanel rootContentPanel, Mode mode) {
        initImage();
        initComponents();
//        this.notifyMode = notifyMode;
        this.mode = mode;

        JScrollBar verticalScrollBar = jScrollPane2.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(GUIUtil.DefaultUnitIncrement);

        switch (mode) {
            case Notify:
                this.jPanel_Plurk.setVisible(false);
                this.jPanel_ResponseInput.setVisible(false);
            case Normal:
            case Simple:
                updateMouseListener = new UpdateMouseListener();
                commentsAdjustmentListener = new ScrollBarAdjustmentListener(jPanel_Comments, true, this);
                verticalScrollBar.addAdjustmentListener(commentsAdjustmentListener);

                alterEnterKeyMap(jEditorPane_ResponseInput);

                plurkerDocumentListener = new PlurkerDocumentListener(jLabel_InputNotify, Mode.Simple == mode);
                jEditorPane_ResponseInput.getDocument().addDocumentListener(plurkerDocumentListener);
                if (mode == Mode.Simple) {
                    this.jLabel_DisplayName.setVisible(false);
                    this.jLabel3.setVisible(false);
//                    jLabel_InputNotify.setVisible(false);
                }
        }


        if (null != rootContentPanel) {
            setRootContentPanel(rootContentPanel);
        }

        DirectScroll.initDirectScroll(this.jScrollPane2.getVerticalScrollBar(), true);

    }
    private UpdateMouseListener updateMouseListener;// = new UpdateMouseListener();

    class UpdateMouseListener extends MouseAdapter {

        public void mouseClicked(MouseEvent e) {
            if (isFetchThreadRunning()) {
                commentsFetchThread.terminate();
            }
            //接著去抓comment, 因為費時, 所以要用thread
//            if (jPanel_Comments.contains(whitePanel)) {
//                jPanel_Comments.remove(whitePanel);
//            }
//            if (jPanel_Comments.contains(whitePanel)) {
//                jPanel_Comments.remove(noResponsePanel);
//            }
            jPanel_Comments.removeAll();
            commentsFetchThread = new CommentsFetchThread(true);
            commentsFetchThread.start();

        }
    }

    /**
     * Creates new form ResponsePanel
     */
    public ResponsePanel() {
        this(null, Mode.Normal);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel_Plurk = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel_ResponseInput = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel_DisplayName = new javax.swing.JLabel();
        jComboBox_Qualifier1 = new javax.swing.JComboBox();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane_ResponseInput = new javax.swing.JEditorPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel_InputNotify = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel_Media = new javax.swing.JLabel();
        jLabel_Emoticon = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel_Comments = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(400, 600));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());

        jPanel_Plurk.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_Plurk.setLayout(new java.awt.BorderLayout());

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("最近回應：");
        jPanel_Plurk.add(jLabel1, java.awt.BorderLayout.SOUTH);

        add(jPanel_Plurk, java.awt.BorderLayout.NORTH);

        jPanel_ResponseInput.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_ResponseInput.setLayout(new java.awt.BorderLayout());

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel_DisplayName.setBackground(javax.swing.UIManager.getDefaults().getColor("EditorPane.background"));
        jPanel9.add(jLabel_DisplayName);

        jComboBox_Qualifier1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { ":", "愛", "喜歡", "分享", "給", "討厭", "想要", "期待", "需要", "打算", "希望", "問", "已經", "曾經", "好奇", "覺得", "想", "說", "正在" }));
        jComboBox_Qualifier1.setSelectedIndex(17);
        jPanel9.add(jComboBox_Qualifier1);

        jPanel_ResponseInput.add(jPanel9, java.awt.BorderLayout.WEST);

        jPanel8.setLayout(new java.awt.BorderLayout());

        jEditorPane_ResponseInput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jEditorPane_ResponseInputKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jEditorPane_ResponseInput);

        jPanel8.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        jLabel_InputNotify.setBackground(new java.awt.Color(255, 255, 255));
        jLabel_InputNotify.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_InputNotify.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel_InputNotify.setVisible(false);
        jPanel1.add(jLabel_InputNotify);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel_Media.setIcon(new ImageIcon( plurkImage.getSubimage(0, 366, 17, 17)));
        jLabel_Media.setToolTipText("分享資訊");
        jLabel_Media.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_MediaMouseClicked(evt);
            }
        });
        jPanel2.add(jLabel_Media);

        jLabel_Emoticon.setIcon(new ImageIcon( emoticon_mini_off));
        jLabel_Emoticon.setToolTipText("選擇表情符號");
        jLabel_Emoticon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_EmoticonMouseClicked(evt);
            }
        });
        jPanel2.add(jLabel_Emoticon);

        jPanel1.add(jPanel2);

        jPanel8.add(jPanel1, java.awt.BorderLayout.SOUTH);

        jPanel_ResponseInput.add(jPanel8, java.awt.BorderLayout.CENTER);

        jLabel3.setText("您的回應：");
        jPanel_ResponseInput.add(jLabel3, java.awt.BorderLayout.PAGE_START);

        add(jPanel_ResponseInput, java.awt.BorderLayout.SOUTH);

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jPanel_Comments.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_Comments.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel_CommentsMouseClicked(evt);
            }
        });
        jPanel_Comments.setLayout(new javax.swing.BoxLayout(jPanel_Comments, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane2.setViewportView(jPanel_Comments);

        add(jScrollPane2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jEditorPane_ResponseInputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jEditorPane_ResponseInputKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jEditorPane_ResponseInputKeyPressed

    private void jPanel_CommentsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel_CommentsMouseClicked
        JPanel source = (JPanel) evt.getSource();
//        Component component = source.getComponent(0);
//        int width = component.getWidth();
    }//GEN-LAST:event_jPanel_CommentsMouseClicked

    private void jLabel_MediaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_MediaMouseClicked
//        System.out.println("jLabel_MediaMouseClicked");
    }//GEN-LAST:event_jLabel_MediaMouseClicked

    private void jLabel_EmoticonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_EmoticonMouseClicked
//        System.out.println("jLabel_EmoticonMouseClicked");
    }//GEN-LAST:event_jLabel_EmoticonMouseClicked

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        int width = this.getWidth();
        updateWidth(width);
//        updateWhiteUI();
//        System.out.println(evt);

    }//GEN-LAST:event_formComponentResized
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox_Qualifier1;
    private javax.swing.JEditorPane jEditorPane_ResponseInput;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel_DisplayName;
    private javax.swing.JLabel jLabel_Emoticon;
    private javax.swing.JLabel jLabel_InputNotify;
    private javax.swing.JLabel jLabel_Media;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanel_Comments;
    private javax.swing.JPanel jPanel_Plurk;
    private javax.swing.JPanel jPanel_ResponseInput;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
    private PlurkerDocumentListener plurkerDocumentListener;

    public javax.swing.JPanel getCommentsPanel() {
        return jPanel_Comments;
    }

    private void reset() {
        if (null != rootContentPanel && null != firstPanel) {
            jPanel_Plurk.remove(firstPanel);
            rootContentPanel = firstPanel = null;
        }
        jPanel_Comments.removeAll();
        this.jEditorPane_ResponseInput.setText("");
    }

    private void setPlurkPool(PlurkPool plurkPool) {
        if (null == this.plurkPool) {
            this.plurkPool = plurkPool;
        }

        if (null != this.plurkPool && !PlurkerApplication.offlineMode) {
            try {
                UserProfile ownProfile = plurkPool.getSourcer().getOwnProfile();
                String displayName = ownProfile.getUserInfo().getDisplayName();
                jLabel_DisplayName.setText(displayName);
            } catch (JSONException ex) {
                Logger.getLogger(ResponsePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void addContentPanel(ContentPanel contentPanel) {
//              ContentPanel contentPanel = new ContentPanel(comment, plurkPool, this.firstPanel, this.jEditorPane_ResponseInput);
        Dimension size = jPanel_Comments.getSize();
        contentPanel.updateWidth(size.width);
        if (null != whitePanel && SwingUtilities.isDescendingFrom(whitePanel, jPanel_Comments)) {
            jPanel_Comments.remove(whitePanel);
        }
        jPanel_Comments.add(contentPanel);
        updateWhiteUI();
    }

    private void addCommentToUI(Comment comment) {

        if (jPanel_Comments.getComponentCount() == 1 && jPanel_Comments.getComponent(0) == noResponsePanel) {
            jPanel_Comments.remove(noResponsePanel);
        }

        firstPanel.addNofityLabelCount();

        int width = jPanel_Comments.getWidth();
        int scrollBarWidth = this.jScrollPane2.getVerticalScrollBar().getWidth();
        ContentPanel contentPanel = initContentPanel(comment, width);
        contentPanel.setOffsetOfToolTip(scrollBarWidth);
        addContentPanel(contentPanel);

    }

    private void updateWhiteUI() {
        if (Mode.Notify != mode && 0 == jPanel_Comments.getComponentCount()) {
            //不是notify,而且又沒東西
            if (null == noResponsePanel) {
                noResponsePanel = new ContentPanel("還沒有人回應哦，趕快來搶頭香囉！:)");
                noResponsePanel.addMouseListener(updateMouseListener);
            }
            jPanel_Comments.add(noResponsePanel);
        } else {

            Dimension size = jPanel_Comments.getSize();
            Dimension preferredSize = jPanel_Comments.getPreferredSize();
            int deltaHeight = size.height - preferredSize.height;
//            
            if (deltaHeight > 0) {
                if (null == whitePanel) {

                    if (Mode.Notify == mode) {
                        whitePanel = new ContentPanel("");
                    } else {
                        whitePanel = new ContentPanel(refreshImage);
                        whitePanel.addMouseListener(updateMouseListener);
                        whitePanel.getjLabel_Image().addMouseListener(updateMouseListener);
                    }
                }

                Dimension whitesize = new Dimension(size.width, deltaHeight);
                whitePanel.setSize(whitesize);
                whitePanel.setPreferredSize(whitesize);
                if (!SwingUtilities.isDescendingFrom(whitePanel, jPanel_Comments)) {
                    jPanel_Comments.add(whitePanel);
                }

            } else {
//                whitePanel = null;
                JViewport viewport = jScrollPane2.getViewport();
                if (null != commentsAdjustmentListener) {
                    commentsAdjustmentListener.stopListen();
                }
                viewport.setViewPosition(new Point(0, jPanel_Comments.getPreferredSize().height));
//                if (null != commentsAdjustmentListener) {
//                    commentsAdjustmentListener.startListen();
//                }
            }

        }

    }
    private boolean stopUpdateUI = false;

    private void setCommentListToUI(final java.util.List<Comment> commentList) {


        if (null != commentList && !commentList.isEmpty()) {
            int totalHeight = 0;
            int length = commentList.size();
            int width = jPanel_Comments.getWidth();
            final java.util.List<ContentPanel> panelList = new ArrayList<>(length);
            int scrollBarWidth = this.jScrollPane2.getVerticalScrollBar().getWidth();
            long start = System.currentTimeMillis();
            for (int x = 0; x < length; x++) {
                if (stopUpdateUI) {
                    stopUpdateUI = false;
                    return;
                }
                Comment comment = commentList.get(x);
                ContentPanel content = initContentPanel(comment, width);
                content.setOffsetOfToolTip(scrollBarWidth);
                panelList.add(content);
                totalHeight += content.getHeight();
            }
            System.out.println("init ui " + (System.currentTimeMillis() - start) / 1000.);
            jPanel_Comments.removeAll();
            jPanel_Comments.updateUI();
            for (ContentPanel panel : panelList) {
                if (stopUpdateUI) {
                    stopUpdateUI = false;
                    return;
                }
                jPanel_Comments.add(panel);
            }
            System.out.println("add ui " + (System.currentTimeMillis() - start) / 1000.);

        } else {
            jPanel_Comments.removeAll();
            jPanel_Comments.updateUI();
        }
        updateWhiteUI();
    }

    /**
     * 判斷scrollbar是不是可以捲了
     *
     * @param scrollbar
     * @return
     */
    boolean isScrollBarActive(JScrollBar scrollbar) {
        return scrollbar.getMaximum() > scrollbar.getVisibleAmount();
    }
    private JLayer<JPanel> jlayer;
    private WaitLayerUI layerUI = new WaitLayerUI();
    static int ThreadSerialID = 0;

    private class CommentsFetchThread extends Thread {

        int serialID;

        CommentsFetchThread(boolean fetchFromPlurkSourcer) {
            this.fetchFromPlurkSourcer = fetchFromPlurkSourcer;
            serialID = ThreadSerialID++;
        }

        CommentsFetchThread() {
            this(false);
        }
        private boolean fetchFromPlurkSourcer = false;
        boolean stop = false;

        @Override
        public void run() {
            if (null == rootContentPanel) {
                return;
            }
            Plurk plurk = rootContentPanel.getPlurk();
            if (null == plurk) {
                return;
            }
            System.out.println(serialID + " Thread Start");

            if (null == loadingPane) {
                loadingPane = new ContentPanel(" ", jPanel_Comments.getWidth());
                jlayer = new JLayer<>(loadingPane, layerUI);
            }

            jPanel_Comments.add(jlayer);
            layerUI.start();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateUI();
                }
            });
            if (stop) {
                stopUpdating();
            }
            long start = System.currentTimeMillis();
            java.util.List<Comment> commentList = null;
            try {
                System.out.println(serialID + " fetch comments");
                if (PlurkerApplication.offlineMode && new File("comments.obj").exists()) {
                    commentList = (java.util.List<Comment>) Persistence.readObjectAsXML("comments.obj");
                } else if (null != plurkPool) {
                    commentList = plurkPool.getComments(plurk, 0, fetchFromPlurkSourcer);
                }
                System.out.println(serialID + " fetch end " + ((System.currentTimeMillis() - start) / 1000.));
            } catch (JSONException ex) {
                Logger.getLogger(PlurkerApplication.class.getName()).log(Level.SEVERE, null, ex);
            } catch (java.lang.IllegalArgumentException ex) {
            }
            if (null == commentList) {
                return;
            }

            if (PlurkerApplication.offlineMode && !new File("comments.obj").exists()) {
                Persistence.writeObjectAsXML(commentList, "comments.obj");
            }

            if (stop) {
                stopUpdating();
            }
            commentsAdjustmentListener.stopListen();
            System.out.println(serialID + " set ui " + ((System.currentTimeMillis() - start) / 1000.));
            setCommentListToUI(commentList);
            System.out.println(serialID + " set ui end " + ((System.currentTimeMillis() - start) / 1000.));
            if (stop) {
                stopUpdating();
            }
            updateCommentCountToPlurk(commentList);


            stopUpdating();
            System.out.println(serialID + " Thread End");
        }

        private void stopUpdating() {
            System.out.println("stop updating");
            if (null != triggerCallBack) {
                triggerCallBack.callback();
            }
            layerUI.stop();
            if (jPanel_Comments.isAncestorOf(jlayer)) {
                jPanel_Comments.remove(jlayer);
            }
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateUI();
                }
            });
        }

        public void terminate() {
            stop = true;
            stopUpdateUI = true;
        }

        private void updateCommentCountToPlurk(java.util.List<Comment> commentList) {
            int size = commentList.size();
            if (null != firstPanel) {
                firstPanel.setNofityLabelCount(size);
            }
        }
    }

    private ContentPanel getWhiteContentPanel(int width, int height) {
        if (null == whitePanel) {
            whitePanel = new ContentPanel("");
        }
        Dimension whitesize = new Dimension(width, height);
        whitePanel.setSize(whitesize);
        whitePanel.setPreferredSize(whitesize);
        return whitePanel;
    }

    private int getCommentCount() {

        int count = jPanel_Comments.getComponentCount();
        if (jPanel_Comments.isAncestorOf(noResponsePanel)) {
            return 0;
        } else if (jPanel_Comments.isAncestorOf(whitePanel)) {
            return count - 1;
        }
        return count;
    }
    
    private ContentPanel initContentPanel(Comment comment, int width) {
        ContentPanel contentPanel = new ContentPanel(comment, plurkPool, this.firstPanel, this.jEditorPane_ResponseInput);
        contentPanel.updateWidth(width);
        return contentPanel;
    }
    private ResponsePanel thisObject = this;
    private AbstractAction enterAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (null == plurkPool || !plurkerDocumentListener.isInLimit()) {
                return;
            }

            String text = jEditorPane_ResponseInput.getText();
            if (text.length() == 0) {
                return;
            }

            int selectedIndex = jComboBox_Qualifier1.getSelectedIndex();
            Qualifier qualifier = Qualifier.values()[selectedIndex];
            try {
                Plurk plurk = rootContentPanel.getPlurk();
                Comment comment = plurkPool.responseAdd(plurk.getPlurkId(), text, qualifier);
                if (null != comment) {
                    commentsAdjustmentListener.stopListen();
                    addCommentToUI(comment);
                    jEditorPane_ResponseInput.setText("");
                    commentsAdjustmentListener.startListen();
                } else {
                    HttpRequestException httpRequestException = plurkPool.getSourcer().getHttpRequestException();
                    String errorText = httpRequestException.getErrorText();
                    if (errorText.equals(AntiFloodSameContent)) {
                        errorText = AntiFloodSameContent_;
                    } else if (errorText.equals(AntiFloodTooManyNew)) {
                        errorText = AntiFloodTooManyNew_;
                    }
                    JOptionPane.showMessageDialog(thisObject, errorText, "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (JSONException ex) {
                Logger.getLogger(ResponsePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };
    public final static String AntiFloodSameContent = "anti-flood-same-content";
    public final static String AntiFloodSameContent_ = "呼!基於防洪規範，您暫時無法傳送噗浪訊息。\n看起來您好像幾分鐘前張貼這則訊息了？";
    public final static String AntiFloodTooManyNew = "anti-flood-too-many-new";
    public final static String AntiFloodTooManyNew_ = "呼！基於防洪規範，您暫時無法傳送噗浪訊息。\n您在過去幾分鐘內發出太多訊息了。請冷靜一下，10分鐘後再回來吧。";

    private int getEnterCount(JEditorPane pane) {
        String text = pane.getText();
        int count = 0;
        for (int x = 0; x < text.length(); x++) {
            if ((x = text.indexOf('\n', x)) != -1) {
                count++;
            } else {
                return 0;
            }

        }
        return count;
    }
    private AbstractAction shiftEnterAction = new AbstractAction() {
        private AbstractAction defaultAction = new DefaultEditorKit.InsertBreakAction();

        @Override
        public void actionPerformed(ActionEvent e) {
            defaultAction.actionPerformed(e);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateUI();
                }
            });
        }
    };

    private void alterEnterKeyMap(JEditorPane editorPane) {
        InputMap inputMap = editorPane.getInputMap();
        ActionMap actionMap = editorPane.getActionMap();

        Object enterKey = inputMap.get(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_MASK), enterKey); //Shift+Enter
        actionMap.put(enterKey, shiftEnterAction);


        Action get = actionMap.get(enterKey);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");//Enter
        actionMap.put("Enter", enterAction);

    }
}

class PlurkerDocumentListener implements DocumentListener {

    public final static int MaxInputCharCount = 140;
    private JLabel notify;
    private boolean simple;

    public PlurkerDocumentListener(JLabel notify, boolean simple) {
        this.notify = notify;
        this.simple = simple;
    }
    private boolean inLimit = true;

    public boolean isInLimit() {
        return inLimit;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        Document document = e.getDocument();
        int remainder = MaxInputCharCount - document.getLength();
        if (remainder >= 0 && remainder <= 50) {
            String text = "尚餘 " + remainder + " 個字元. 按 Enter 送出";
            notify.setText(text);
            notify.setForeground(Color.black);
            inLimit = true;
        } else if (remainder < 0) {
            String text = "字太多囉，請移除掉 " + (-remainder) + " 個字。";
            notify.setText(text);
            notify.setForeground(Color.red);
            inLimit = false;
        } else {
            if (!simple) {
                notify.setVisible(true);
                notify.setText("按 Enter 送出");
                notify.setForeground(Color.black);
            }
            inLimit = true;
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        insertUpdate(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }
}
