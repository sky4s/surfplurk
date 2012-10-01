/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui;

import plurker.ui.util.GUIUtil;
import com.google.jplurk_oauth.data.Comment;
import com.google.jplurk_oauth.data.Plurk;
import com.google.jplurk_oauth.data.Plurk.PlurkType;
import com.google.jplurk_oauth.module.Timeline;
import com.google.jplurk_oauth.skeleton.DateTime;
import com.google.jplurk_oauth.skeleton.RequestException;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.json.JSONException;
import plurker.source.PlurkPool;
import plurker.source.PlurkSourcer;
import plurker.ui.notify.NotificationManager;
import plurker.ui.util.DirectScroll;
import plurker.ui.util.ScrollBarAdjustmentListener;
import plurker.ui.util.WaitLayerUI;
import shu.util.Persistence;

/**
 *
 * @author Egg Hsu
 */
public class PlurksPanel extends javax.swing.JPanel implements AWTEventListener, ScrollBarAdjustmentListener.TriggerInterface {

//    private DirectScroll directScroll;
    @Override
    public void eventDispatched(AWTEvent event) {
        MouseEvent mouseevent = (MouseEvent) event;
        if (mouseevent.getID() == MouseEvent.MOUSE_CLICKED && SwingUtilities.isLeftMouseButton(mouseevent)) {
            if (null != mouseevent.getComponent() && SwingUtilities.isDescendingFrom(mouseevent.getComponent(), this)) {
                Point point = mouseevent.getPoint();
                Point convertPoint = SwingUtilities.convertPoint(mouseevent.getComponent(), point, this.jPanel1);
                Component child = jPanel1.getComponentAt(convertPoint);
//                System.out.println(mouseevent.getWhen() + " " + mouseevent);
                if (child instanceof ContentPanel) {
                    System.out.println("inHyperlink: " + ((ContentPanel) child).isInHyperlink(mouseevent));
                }
                if (child instanceof ContentPanel && !((ContentPanel) child).isInHyperlink(mouseevent) && null != plurker) {
                    //滑鼠點下去, 如果是contentpanel, 而且plurker不是空的, 就設定plurker的current follow
                    ContentPanel contentPanel = (ContentPanel) child;
//                    System.out.println("set from plurks panel");
                    plurker.setCurrentFollow(contentPanel);
                }
            }
            
        }
    }
    
    public void updatePlurks() {
        
        if (null == plurkPool && !PlurkerApplication.offlineMode) {
            return;
        }
        
        if (PlurkerApplication.offlineMode) {
            List<Plurk> debugPlurkList = readPlurkList();
            if (null == debugPlurkList) {
                String content = "1233";
                
                content =
                        "<html><head></head><body>"
                        + "wenbao.icm :  Michelle "
                        + "Jenneke (complete race, no 80s music)</a> Michelle Jenneke<br>&#36229;&#27491;. &#36229;&#19978;&#30456;."
                        + "&#32780;&#19988;&#36229;&#24375;....&#28459;&#30059;&#35201;&#26159;&#26377;&#35282;&#33394;&#36889;&#40636;&#23436;&#32654;&#20854;&#20182;&#20154;&#36996;&#35201;&#28436;&#21861;  "
                        + "</body></html>";
                
                ContentPanel pane = new ContentPanel(content);
                pane.updateWidth(jPanel1.getWidth());
                pane.setComponentPopupMenu(jPopupMenu1);
                jPanel1.add(pane, 0);
            } else {
                try {
                    addToPanel(debugPlurkList, newPlurkAtTop);
                } catch (JSONException ex) {
                    Logger.getLogger(PlurksPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        } else {
            retrievePlurkProcess(!newPlurkAtTop, null);
        }
        
    }
    
    private void storePlurkList(List<Plurk> plurkList) {
        if (PlurkerApplication.debugMode && !new File("plurks.obj").exists()) {
            Persistence.writeObjectAsXML(plurkList, "plurks.obj");
        }
    }
    
    private List<Plurk> readPlurkList() {
        if (PlurkerApplication.debugMode && new File("plurks.obj").exists()) {
            return (List<Plurk>) Persistence.readObjectAsXML("plurks.obj");
        } else {
            return null;
        }
    }
    private DirectScroll.PopupMenuListener popupMenuListener;
    
    private List<ContentPanel> addToPanel(final List<Plurk> plurkList, final boolean addToTop) throws JSONException {
        /*
         * 預設為list順向 add to top New @ Top: list順向 T!=T Old @ Top: list逆向 F!=T O
         *
         * add to bot: New @ Top: list逆向 T!=F O Old @ Top: list順向 F!=F
         */
        if (newPlurkAtTop != addToTop) {
            Collections.reverse(plurkList);
        }
        List<ContentPanel> contentPanelList = new ArrayList<ContentPanel>();
        
        for (Plurk p : plurkList) {
            ContentPanel pane = new ContentPanel(p, plurkPool);
//            pane.setNewBie(true);
            pane.setPopupMenuListener(popupMenuListener);
            pane.updateWidth(jPanel1.getWidth());
            contentPanelList.add(pane);
            
        }
        for (ContentPanel pane : contentPanelList) {
            if (addToTop) {
                //新的在最上面
                jPanel1.add(pane, 0);
            } else {
                //新的在最下面
                jPanel1.add(pane);
            }
        }
        return contentPanelList;
        
    }
    private PlurkPool plurkPool = null;
    private Timeline.Filter filter = null;
    private int plurkPanelWidth = -1;
    private boolean newerProcess;
    private boolean newPlurkAtTop = true;
    
    public void setNewPlurkAtTop(boolean newPlurkAtTop) {
        this.newPlurkAtTop = newPlurkAtTop;
    }
    
    public void setPlurkPool(PlurkPool plurkPool) {
        this.plurkPool = plurkPool;
        if (null != plurkPool) {
            plurkPool.addCometChangeListener(new CometChangeListener());
        }
    }
    
    private class CometChangeListener implements ChangeListener {
        
        private void addToPanel0(Plurk plurk) throws JSONException {
            List<Plurk> list = new ArrayList<>();
            list.add(plurk);
            addToPanel(list, true);
        }
        
        @Override
        @SuppressWarnings("empty-statement")
        public void stateChanged(ChangeEvent e) {
            PlurkPool pool = (PlurkPool) e.getSource();
            TreeSet<Plurk> newPlurkSet = pool.getNewPlurkSet();
            TreeSet<Comment> newResponseSet = pool.getNewResponseSet();
            
            try {
                for (Plurk plurk : newPlurkSet) {
                    switch (filter) {
                        case None://all
                            addToPanel0(plurk);
                            break;
                        case only_user://my
                            long userID = pool.getSourcer().getUserID();
                            if (plurk.getOwnerId() == userID) {
                                addToPanel0(plurk);
                            }
                            break;
                        case only_private:
                            PlurkType plurkType = plurk.getPlurkType();
                            if (PlurkType.Private == plurkType || PlurkType.Private_Logged == plurkType) {
                                addToPanel0(plurk);
                            }
                            break;
                        case only_responded:
                            break;
                        case only_favorite:
                            if (plurk.isFavorite()) {
                                addToPanel0(plurk);
                            }
                            break;
                    }
                    
                }
            } catch (JSONException ex) {
                Logger.getLogger(PlurksPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
//            for (Comment comment : newResponseSet) {
//                NotifyPanel2 notify = new NotifyPanel2(comment, plurkPool);
//                notify.updateWidth(NotificationManager.NotifyWidth);
//                notify.setPlurker(plurker);
//                notifyManager.addContent(notify);
//            }
        }
    }

    /**
     * Creates new form PlurksPanel
     */
    public PlurksPanel(PlurkPool plurkPool, Timeline.Filter filter, boolean newerProcess) {
        initComponents();
//        this.plurkPool = plurkPool;
        setPlurkPool(plurkPool);
//        plurkPool.add
        this.filter = filter;
        this.newerProcess = newerProcess;
        if (true) {
            javax.swing.JViewport viewport = (JViewport) jPanel1.getParent();
            if (null != viewport) {
                JScrollPane scrollPane = (JScrollPane) viewport.getParent();
                JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                ScrollBarAdjustmentListener adjustmentListener = new ScrollBarAdjustmentListener(jPanel1, newerProcess, this);
                verticalScrollBar.addAdjustmentListener(adjustmentListener);
                verticalScrollBar.setUnitIncrement(GUIUtil.DefaultUnitIncrement);
                verticalScrollBar.setBackground(Color.white);
            }
            
            this.jMenuItem_CopyPlurk.setVisible(PlurkerApplication.debugMode);
            this.jMenuItem_CopyContent.setVisible(PlurkerApplication.debugMode);
            this.jMenuItem_CopyContentRaw.setVisible(PlurkerApplication.debugMode);
            Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
            
            popupMenuListener = new DirectScroll.PopupMenuListener(jPopupMenu1);
            DirectScroll.initDirectScroll(this.jScrollPane1.getVerticalScrollBar(), true);
        }
        
    }
    private PlurkerApplication plurker;
    
    public void setPlurker(PlurkerApplication plurker) {
        this.plurker = plurker;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem_setCurrentFollow = new javax.swing.JMenuItem();
        jMenuItem_AddToFollow = new javax.swing.JMenuItem();
        jMenuItem_CopyContent = new javax.swing.JMenuItem();
        jMenuItem_CopyContentRaw = new javax.swing.JMenuItem();
        jMenuItem_CopyPlurk = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();

        jMenuItem_setCurrentFollow.setAction(setCurrentFollowAction);
        jMenuItem_setCurrentFollow.setText("閱讀");
        jPopupMenu1.add(jMenuItem_setCurrentFollow);

        jMenuItem_AddToFollow.setAction(addToFollowAction);
        jMenuItem_AddToFollow.setText("加入追蹤");
        jPopupMenu1.add(jMenuItem_AddToFollow);

        jMenuItem_CopyContent.setAction(this.copyContentAction);
        jMenuItem_CopyContent.setText("複製Content");
        jPopupMenu1.add(jMenuItem_CopyContent);

        jMenuItem_CopyContentRaw.setAction(this.copyContentRawAction);
        jMenuItem_CopyContentRaw.setText("複製ContentRaw");
        jPopupMenu1.add(jMenuItem_CopyContentRaw);

        jMenuItem_CopyPlurk.setAction(this.copyPlurkAction);
        jMenuItem_CopyPlurk.setText("複製Plurk");
        jPopupMenu1.add(jMenuItem_CopyPlurk);

        setMinimumSize(new java.awt.Dimension(399, 544));
        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jScrollPane1ComponentResized(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane1.setViewportView(jPanel1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    private Dimension lastViewportsize;
    private void jScrollPane1ComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jScrollPane1ComponentResized
        JViewport viewport = jScrollPane1.getViewport();
        Dimension size = viewport.getSize();
        if (lastViewportsize != null && size.width != lastViewportsize.width) {
            for (Component c : this.jPanel1.getComponents()) {
                if (c instanceof ContentPanel) {
                    ContentPanel plurkPanel = (ContentPanel) c;
                    Rectangle bounds = plurkPanel.getBounds();
                    plurkPanel.updateWidth(size.width);
                }
            }
        }
        lastViewportsize = size;
    }//GEN-LAST:event_jScrollPane1ComponentResized
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem jMenuItem_AddToFollow;
    private javax.swing.JMenuItem jMenuItem_CopyContent;
    private javax.swing.JMenuItem jMenuItem_CopyContentRaw;
    private javax.swing.JMenuItem jMenuItem_CopyPlurk;
    private javax.swing.JMenuItem jMenuItem_setCurrentFollow;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    private ContentPanel getContentPanel(JMenuItem menuItem) {
        JPopupMenu popupMenu = (JPopupMenu) menuItem.getParent();
        Point location = popupMenu.getLocation();
        Point convertPoint = SwingUtilities.convertPoint(popupMenu.getInvoker(), location, this.jPanel1);
        Component child = jPanel1.getComponentAt(convertPoint);
        if (child instanceof ContentPanel) {
            return (ContentPanel) child;
        } else {
            return null;
        }
    }
    Action setCurrentFollowAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (null != plurker) {
                JMenuItem source = (JMenuItem) e.getSource();
                ContentPanel plurkPanel = getContentPanel(source);
                plurker.setCurrentFollow(plurkPanel);
            }
        }
    };
    Action addToFollowAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (null != plurker) {
                JMenuItem source = (JMenuItem) e.getSource();
                ContentPanel plurkPanel = getContentPanel(source);
                plurker.addNewFollow(plurkPanel);
            }
        }
    };
    Action copyContentAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem source = (JMenuItem) e.getSource();
            ContentPanel plurkPanel = getContentPanel(source);
            plurkPanel.copyContentToClipboard();
        }
    };
    Action copyContentRawAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem source = (JMenuItem) e.getSource();
            ContentPanel plurkPanel = getContentPanel(source);
            plurkPanel.copyContentRawToClipboard();
        }
    };
    Action copyPlurkAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem source = (JMenuItem) e.getSource();
            ContentPanel plurkPanel = getContentPanel(source);
            plurkPanel.copyPlurkToClipboard();
        }
    };
    
    public static void main(String[] args) throws RequestException, JSONException {
        GUIUtil.initGUI();
        PlurkSourcer.setDoValidToken(false);
        PlurkSourcer plurkSourcer = new PlurkSourcer(PlurkSourcer.API_KEY, PlurkSourcer.APP_SECRET, "GIRLiuAIdINH", "79aUVlCqRw4GzU04kiRIkfzHTSwonWBR");
        PlurkPool plurkPool = new PlurkPool(plurkSourcer);
        plurkPool.startComet();
        
        JFrame frame = new JFrame();
        PlurksPanel panel = new PlurksPanel(plurkPool, Timeline.Filter.None, true);
//        panel.setNewPlurkAtTop(false);
        frame.add(panel);
        frame.setSize(400, 600);
        frame.setVisible(true);
        panel.updatePlurks();
        panel.updateUI();
        
    }
    public final static String OffLine = "Off-line mode...";
    public final static String Loading = "Loading...";
    private ContentPanel offlinePane;
    private ContentPanel loadingPane;
    private JLayer<JPanel> jlayer;
    private WaitLayerUI layerUI = new WaitLayerUI();
    private boolean topProcess;
    
    public boolean isUpdating() {
        return (null != layerUI) ? layerUI.isRunning() : false;
    }
    
    private void retrievePlurkProcess(boolean topProcess, CallBack callBack) {
        this.topProcess = topProcess;
        //用來判斷是抓新的還是舊的
        boolean getNewer = (newPlurkAtTop == topProcess);
        if (false == newerProcess && true == getNewer || layerUI.isRunning()) {
            if (null != callBack) {
                callBack.callback();
            }
            return;
        }
        
        int componentCount = jPanel1.getComponentCount();
        //======================================================================
        //show出loading訊息
        if (null == loadingPane) {
            loadingPane = new ContentPanel(" ", jPanel1.getWidth());
            jlayer = new JLayer<>(loadingPane, layerUI);
        }
        jPanel1.add(jlayer, topProcess ? 0 : componentCount);
        layerUI.start();
        jPanel1.updateUI();

        //======================================================================
        Component component = 0 != componentCount ? jPanel1.getComponent(topProcess ? 0 : componentCount - 1) : null;
        ContentPanel targetpane = (component instanceof ContentPanel) ? (ContentPanel) component : null;
        Plurk offsetPlurk = null != targetpane ? targetpane.getPlurk() : null;
        DateTime offset = (null == offsetPlurk) ? DateTime.now() : DateTime.create(offsetPlurk.getPosted());
        
        plurkRetriever = new PlurkRetriever(offset, getNewer, new PlurkRetrieverCallback());
        if (null != callBack) {
            plurkRetriever.addCallBack(callBack);
        }
        plurkRetriever.execute();
    }
    private PlurkRetriever plurkRetriever;
    
    @Override
    public void trigger(boolean topProcess, JPanel panel, CallBack callBack) {
        this.retrievePlurkProcess(topProcess, callBack);
    }
    
    private class PlurkRetrieverCallback implements CallBack {
        
        PlurkRetrieverCallback() {
        }
        private PlurkRetriever plurkRetriever;
        
        @Override
        public void callback() {
            if (null == plurkRetriever) {
                return;
            }
            List<Plurk> plurkList;
            try {
                plurkList = plurkRetriever.get();
                storePlurkList(plurkList);
                addToPanel(plurkList, topProcess);
            } catch (InterruptedException ex) {
                Logger.getLogger(PlurksPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(PlurksPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSONException ex) {
                Logger.getLogger(PlurksPanel.class.getName()).log(Level.SEVERE, null, ex);
            }

            //關掉loading訊息
            layerUI.stop();
            jPanel1.remove(jlayer);
            jPanel1.updateUI();
        }
    }
    
    class PlurkRetriever extends SwingWorker<  List<Plurk>, Void> {
        
        private DateTime offset;
        private boolean getNewer;
        private PlurkRetrieverCallback retrieverCallback;
        private List<CallBack> callBackList;
        
        public void addCallBack(CallBack callback) {
            if (null == callBackList) {
                callBackList = new ArrayList<>();
            }
            callBackList.add(callback);
        }
        
        PlurkRetriever(DateTime offset, boolean getNewer, PlurkRetrieverCallback retrieverCallback) {
            this.offset = offset;
            this.getNewer = getNewer;
            this.retrieverCallback = retrieverCallback;
            retrieverCallback.plurkRetriever = this;
        }
        
        protected void done() {
            new Thread() {
                public void run() {
                    retrieverCallback.callback();
                    if (null != callBackList) {
                        for (CallBack c : callBackList) {
                            c.callback();
                        }
                    }
                }
            }.start();
        }
        
        @Override
        protected List<Plurk> doInBackground() throws Exception {
            List<Plurk> plurkList = null;
            if (getNewer) {
                plurkList = plurkPool.getNewerPlurks(offset, GUIUtil.PlurksPerFetch);
            } else {
                plurkList = plurkPool.getOlderPlurks(offset, GUIUtil.PlurksPerFetch, filter);
            }
            return plurkList;
        }
    }
}
