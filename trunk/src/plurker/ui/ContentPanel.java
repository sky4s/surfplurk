/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui;

import com.google.jplurk_oauth.data.Comment;
import com.google.jplurk_oauth.data.Data;
import com.google.jplurk_oauth.data.Plurk;
import com.google.jplurk_oauth.data.Plurk.UnreadType;
import com.google.jplurk_oauth.data.UserInfo;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.InlineView;
import javax.swing.text.html.ParagraphView;
import org.json.JSONException;
import plurker.source.PlurkChangeListener;
import plurker.source.PlurkFormater;
import plurker.source.PlurkPool;
import plurker.ui.tooltip.CommentToolTipPanel;
import plurker.ui.tooltip.PlurkerToolTip;
import plurker.ui.tooltip.ToolTipPanel;
import plurker.ui.util.DirectScroll;
import plurker.util.Utils;
//import org.fit.cssbox.swingbox.*;

/**
 *
 * @author skyforceshen
 */
public class ContentPanel extends javax.swing.JPanel implements AWTEventListener, PlurkChangeListener {

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        String content = Utils.readContent(new File("b.html"));
        frame.setLayout(new java.awt.BorderLayout());
        frame.add(new ContentPanel(content), java.awt.BorderLayout.CENTER);
        frame.setSize(600, 600);
        frame.setVisible(true);
    }

    public Plurk getPlurk() {
        return plurk;
    }

//    @Override
//    public void setComponentPopupMenu(JPopupMenu popup) {
//        super.setComponentPopupMenu(popup);
//        this.jEditorPane1.setComponentPopupMenu(popup);
//        this.jLabel_Avatar.setComponentPopupMenu(popup);
//        this.jLabel_Time.setComponentPopupMenu(popup);
////        this.jPanel1.setComponentPopupMenu(popup);
//    }
    public Comment getComment() {
        return comment;
    }
    private Plurk plurk;
    private Comment comment;
    PlurkPool plurkPool;
    private int prefferedWidth;

    private void initEditorPane1(String content, int width) {
        jEditorPane1.setEditorKit(FixedHTMLEditorKit.getInstance());
        jEditorPane1.setFont(GUIUtil.font);

        if (null != plurkPool && PlurkerApplication.cacheImage) {
            jEditorPane1.getDocument().putProperty("imageCache", plurkPool.getImageCache());
        }

        jEditorPane1.setText(content);
        updateWidth(width);
    }
    private boolean copyFromOther = false;
    private ContentPanel plurkPanel;

    public ContentPanel getPlurkPanel() {
        return plurkPanel;
    }

    public void setPopupMenuListener(DirectScroll.PopupMenuListener popupMenuListener) {

        jLabel_Avatar.addMouseListener(popupMenuListener);
        jLabel_Avatar.addMouseMotionListener(popupMenuListener);
        jLabel_Time.addMouseListener(popupMenuListener);
        jLabel_Time.addMouseMotionListener(popupMenuListener);
        jEditorPane1.addMouseListener(popupMenuListener);
        jEditorPane1.addMouseMotionListener(popupMenuListener);

    }

    public static enum Type {

        Plurk, FirstOfResponse, Comment, Unknow
    }
    private Type type;

    //plurk panel, 屬於first panel擁有
    //first panel, 屬於comment panel擁有
    public ContentPanel(ContentPanel plurkPanel) {
        this(plurkPanel.plurk, plurkPanel.comment, plurkPanel.plurkPool, plurkPanel.prefferedWidth, plurkPanel.getJEditorPane().getText(), Type.FirstOfResponse);
//        copyFromOther = true;
        this.plurkPanel = plurkPanel;
        plurkPanel.setNotifyLabelNormal();
    }

    public ContentPanel(String content) {
        this(null, null, null, -1, content, Type.Unknow);
    }

    public ContentPanel(String content, int width) {
        this(null, null, null, width, content, Type.Unknow);
    }

    private void initLabel_Notify() {
        if ((Type.Plurk == type || Type.FirstOfResponse == type) && null != plurk) {
            try {
                UnreadType unreadType = plurk.getUnreadType();
                if (UnreadType.Unread == unreadType && Type.FirstOfResponse != type) {
                    this.setNotifyLabel(Long.toString(plurk.getResponseCount()), HighLight, true);
                } else {
                    this.setNotifyLabel(Long.toString(plurk.getResponseCount()), Normal, true);
                }
            } catch (JSONException ex) {
                Logger.getLogger(ContentPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    private BufferedImage labelAvatarImage;

    private boolean isReplurk() {
        long replurkerId = plurk.getReplurkerId();
        return -1 != replurkerId;
    }

    private void initLabel_Avatar() {
        String profileImage = null;
        try {
            if ((Type.Plurk == type || Type.FirstOfResponse == type) && null != plurk) {
                long replurkerId = plurk.getReplurkerId();
                long ownerId = isReplurk() ? plurk.getReplurkerId() : plurk.getOwnerId();
                UserInfo userInfo = plurkPool.getUserInfo(ownerId);
                UserInfo.ImageSize imageSize = isMuted() ? UserInfo.ImageSize.Medium.Small : UserInfo.ImageSize.Medium.Medium;
                profileImage = userInfo != null ? userInfo.getProfileImage(imageSize) : null;
            } else if (Type.Comment == type && null != comment) {
                long ownerId = comment.getOwnerId();
                UserInfo userInfo = plurkPool.getUserInfo(ownerId);
                if (null != userInfo) {
                    profileImage = userInfo.getProfileImage(UserInfo.ImageSize.Small);
                }
            }
        } catch (JSONException ex) {
            Logger.getLogger(ContentPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (null != profileImage) {
            try {
                URL url = new URL(profileImage);
                labelAvatarImage = plurkPool.getImage(url);
                this.jLabel_Avatar.setIcon(new ImageIcon(labelAvatarImage));
            } catch (MalformedURLException ex) {
                Logger.getLogger(ContentPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ContentPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    private UnreadType unreadType;

    private boolean isMuted() {
        if (null != plurk) {
            if (null == unreadType) {
                try {
                    unreadType = plurk.getUnreadType();
                } catch (JSONException ex) {
                    Logger.getLogger(ContentPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return unreadType == UnreadType.Muted;
        } else {
            return false;
        }

//        if (null != plurk && null == unreadType) {
//            try {
//                unreadType = plurk.getUnreadType();
//            } catch (JSONException ex) {
//                Logger.getLogger(ContentPanel.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            return unreadType == UnreadType.Muted;
//        }
//        return false;
    }

    private ContentPanel(Plurk plurk, Comment comment, PlurkPool plurkPool, int width, String content, Type type) {
        initComponents();
        this.plurk = plurk;
        this.comment = comment;
        this.plurkPool = plurkPool;
        this.prefferedWidth = width;
        this.type = type;
        if (null == content) {
            try {
                content = getContent();
            } catch (JSONException ex) {
                Logger.getLogger(ContentPanel.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }
        initLabel_Avatar();
        initEditorPane1(content, width);
        this.jLabel_Time.setVisible(false);
        if (!isMuted()) {
            initLabel_Notify();
            initLabel_Time();
        }

        //暫時不提供的功能
        this.jLabel_Floor.setVisible(false);
        this.jPanel_Button.setVisible(false);

        this.jEditorPane1.addHyperlinkListener(new PlurkerHyperlinkListener());

        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK);
    }

    public void setFloor(int floor) {
        if (null != comment) {
            jLabel_Floor.setVisible(true);
            jLabel_Floor.setText(floor + "樓");
        }
    }

    private void initLabel_Time() {
        if (null == plurk) {
//            this.jLabel_Time.setVisible(false);
            return;
        }
        Date postedDate = null;
        try {
            postedDate = plurk.getPostedDate();
        } catch (ParseException ex) {
            Logger.getLogger(ContentPanel.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        String timeText = PlurkFormater.getTimeText(postedDate);
        this.jLabel_Time.setText(timeText);
        this.jLabel_Time.setVisible(true);
    }

    /**
     * Creates new form JEditorPanel
     */
    public ContentPanel(Plurk plurk, PlurkPool plurkPool) {
        this(plurk, null, plurkPool, -1, null, Type.Plurk);
    }

    public ContentPanel(Comment comment, PlurkPool plurkPool, ContentPanel firstPanel, JEditorPane responseInput) {
        this(null, comment, plurkPool, -1, null, Type.Comment);
        this.plurkPanel = firstPanel;
        this.responseInput = responseInput;
    }
    private JEditorPane responseInput;
//    private ContentPanel firstPanel;

//    public ContentPanel getFirstPanel() {
//        return firstPanel;
//    }
    private String getContent() throws JSONException {
        if (null != plurk) {
            return PlurkFormater.getInstance(plurkPool).getContent(plurk);
        } else if (null != comment) {
            return PlurkFormater.getInstance(plurkPool).getContent(comment);
        } else {
            return "N/A";
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel_Avatar = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jLabel_Notify = new javax.swing.JLabel();
        jEditorPane1 = new javax.swing.JEditorPane();
        //        jEditorPane1 = new BrowserPane();
        jPanel_Info = new javax.swing.JPanel();
        jPanel_Button = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel_Time = new javax.swing.JLabel();
        jLabel_Floor = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setLayout(new java.awt.BorderLayout());

        jLabel_Avatar.setBackground(new java.awt.Color(255, 255, 255));
        jLabel_Avatar.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        add(jLabel_Avatar, java.awt.BorderLayout.WEST);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.PAGE_AXIS));

        jLayeredPane1.setPreferredSize(new java.awt.Dimension(400, 300));
        jLayeredPane1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jLayeredPane1ComponentResized(evt);
            }
        });

        jLabel_Notify.setBackground(new java.awt.Color(204, 0, 0));
        jLabel_Notify.setOpaque(true);
        jLabel_Notify.setBounds(360, 0, 0, 0);
        jLayeredPane1.add(jLabel_Notify, javax.swing.JLayeredPane.PALETTE_LAYER);

        jEditorPane1.setEditable(false);
        jEditorPane1.setBorder(null);
        jEditorPane1.setContentType("text/html"); // NOI18N
        jEditorPane1.setPreferredSize(new java.awt.Dimension(400, 300));
        jEditorPane1.setBounds(0, 0, 390, 280);
        jLayeredPane1.add(jEditorPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jPanel1.add(jLayeredPane1);

        jPanel_Info.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_Info.setLayout(new java.awt.BorderLayout());

        jPanel_Button.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_Button.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        jLabel1.setText("消音");
        jPanel_Button.add(jLabel1);

        jLabel2.setText("推文");
        jPanel_Button.add(jLabel2);

        jLabel3.setText("轉噗");
        jPanel_Button.add(jLabel3);

        jLabel4.setText("讚");
        jPanel_Button.add(jLabel4);

        jPanel_Info.add(jPanel_Button, java.awt.BorderLayout.SOUTH);

        jLabel_Time.setText(" ");
        jPanel_Info.add(jLabel_Time, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel_Info);

        add(jPanel1, java.awt.BorderLayout.CENTER);

        jLabel_Floor.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        add(jLabel_Floor, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    private void jLayeredPane1ComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jLayeredPane1ComponentResized
        Dimension size = this.jLayeredPane1.getSize();
        jEditorPane1.setSize(size);
        Dimension labelPreferredSize = this.jLabel_Notify.getPreferredSize();
        jLabel_Notify.setBounds(size.width - labelPreferredSize.width, 0, labelPreferredSize.width, labelPreferredSize.height);
    }//GEN-LAST:event_jLayeredPane1ComponentResized

    Rectangle getViewportViewRect() {
        Container parent = this.getParent();
        if (null != parent) {
            parent = parent.getParent();
        }
        Rectangle viewRect = null;
        if (null != parent) {
            JViewport viewport = (JViewport) parent;
            viewRect = viewport.getViewRect();
        }
        return viewRect;
    }

    @SuppressWarnings("empty-statement")
    void updateWidth(int width) {
//        Rectangle viewRect = getViewportViewRect();
        Rectangle bounds = this.getBounds();
        boolean seen = true;// viewRect != null ? viewRect.contains(bounds.x, bounds.y) : false;

        if (seen) {
            Border border = this.getBorder();
            Insets borderInsets = border.getBorderInsets(this);

            int editorPaneWidth = width - (null != labelAvatarImage ? labelAvatarImage.getWidth() : 0) - (borderInsets.left + borderInsets.right);
            String content = jEditorPane1.getText();
            int prefferedHeight = GUIUtil.getContentHeight(content, editorPaneWidth, null != plurkPool ? plurkPool.getImageCache() : null);

            int label2Height = (null != labelAvatarImage ? labelAvatarImage.getHeight() : 0);
            prefferedHeight = Math.max(prefferedHeight, label2Height);
            Dimension preferredSizeOfInfo = this.jPanel_Info.getPreferredSize();
            prefferedHeight += preferredSizeOfInfo.getHeight();

            Dimension fit = new Dimension(editorPaneWidth, prefferedHeight);
            jLayeredPane1.setSize(fit);
            jLayeredPane1.setPreferredSize(fit);
            jEditorPane1.setSize(fit);
            jEditorPane1.setPreferredSize(fit);

            Dimension fitWithBorder = new Dimension(width, prefferedHeight + borderInsets.top + borderInsets.bottom);
            this.setSize(fitWithBorder);
            this.setPreferredSize(fitWithBorder);
        } else {
            int height = this.getHeight();
            Dimension fitWithBorder = new Dimension(width, height);
            this.setSize(fitWithBorder);
            this.setPreferredSize(fitWithBorder);
        }

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel_Avatar;
    private javax.swing.JLabel jLabel_Floor;
    private javax.swing.JLabel jLabel_Notify;
    private javax.swing.JLabel jLabel_Time;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel_Button;
    private javax.swing.JPanel jPanel_Info;
    // End of variables declaration//GEN-END:variables

    public JEditorPane getJEditorPane() {
        return jEditorPane1;
    }

    public JLabel getNotifyLabel() {
        return jLabel_Notify;
    }

    public JLabel getAvatarLabel() {
        return jLabel_Avatar;
    }

    public JLabel getTimeLabel() {
        return jLabel_Time;
    }

    public void setNotifyLabel(String text, Color background, boolean isOpaque) {
        jLabel_Notify.setText(text);
        jLabel_Notify.setBackground(background);
        jLabel_Notify.setOpaque(isOpaque);
    }
    public final static Color HighLight = Color.red;
    public final static Color Normal = Color.white;

    public void setNofityLabelCount(long count) {
        if (null != plurkPanel) {
            plurkPanel.setNofityLabelCount(count);
        }
        if (Type.Plurk == type && null != plurk) {
            try {
//                long responseCount = plurk.getResponseCount();
                plurk.setResponseCount(count);
            } catch (JSONException ex) {
                Logger.getLogger(ContentPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.initLabel_Notify();
        this.setNotifyLabelNormal();
    }

    public void addNofityLabelCount() {
        try {
            long responseCount = plurk.getResponseCount();
            setNofityLabelCount(responseCount + 1);

            //        if (null != plurkPanel) {
            //            plurkPanel.addNofityLabelCount();
            //        }
            //        if (Type.Plurk == type && null != plurk) {
            //            try {
            //                long responseCount = plurk.getResponseCount();
            //                plurk.setResponseCount(responseCount + 1);
            //            } catch (JSONException ex) {
            //                Logger.getLogger(ContentPanel.class.getName()).log(Level.SEVERE, null, ex);
            //            }
            //        this.initLabel_Notify();
            //        this.initLabel_Notify();
        } catch (JSONException ex) {
            Logger.getLogger(ContentPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void minusNofityLabelCount() {
        try {
            long responseCount = plurk.getResponseCount();
            setNofityLabelCount(responseCount - 1);

            //        if (null != plurkPanel) {
            //            plurkPanel.addNofityLabelCount();
            //        }
            //        if (Type.Plurk == type && null != plurk) {
            //            try {
            //                long responseCount = plurk.getResponseCount();
            //                plurk.setResponseCount(responseCount + 1);
            //            } catch (JSONException ex) {
            //                Logger.getLogger(ContentPanel.class.getName()).log(Level.SEVERE, null, ex);
            //            }
            //        this.initLabel_Notify();
            //        this.initLabel_Notify();
        } catch (JSONException ex) {
            Logger.getLogger(ContentPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setNotifyLabelNormal() {
        jLabel_Notify.setBackground(Normal);
        if (null != plurkPanel) {
            plurkPanel.setNotifyLabelNormal();
        }
    }
//    public static void main(String[] args) throws IOException {
//        JFrame frame = new JFrame();
//        //    frame.
//        String content = "<html><head></head><body>"
//                + "bigmaomao#3739 &#35498;: &#27491;&#22969;&#20063;&#30332;PS&#25991; XDD <a href=\"http://www.mobile01.com/topicdetail.php?f=256&t=2875277&p=1\" class=\"ex_link meta\" title=\"如下圖，謝謝大家！^________^\" rel=\"nofollow\"><img height=40  src=\"http://download.mobile01.com/topic/256-2875277-58973f3ab686a478f60604e0806548df.jpg\">&#27714;&#31070;&#20154;&#24171;&#25105;&#25226;&#24460;&#38754;&#37027;&#20301;P&#25481;&#65281;</a>"
//                + "</body></html>";
//        ContentPanel plurkPanel = new ContentPanel(content);
////        frame.setLayout(new javax.swing.BoxLayout(frame, javax.swing.BoxLayout.Y_AXIS));
//        frame.getContentPane().add(plurkPanel);//, java.awt.BorderLayout.CENTER);
//        frame.setSize(400, 300);
//        plurkPanel.setNotifyLabel("Label1", Color.blue, true);
//        BufferedImage loadImage = ImageUtils.loadImage("./image/font.png");
//        plurkPanel.getAvatarLabel().setIcon(new ImageIcon(loadImage));
//        frame.setVisible(true);
////        plurkPanel.updateWidth(100);
//
//    }
//    private Dimension originalSize = null;
//    private static java.util.Timer timer = new java.util.Timer();
//    private DisappearTask disappearTask = null;
//    public final static long DisappearTime = 2000;
//
//    public class DisappearTask extends TimerTask {
//
//        boolean stop = false;
//
//        public void run() {
//            if (!stop) {
//                panel1Disappear();
//            }
//        }
//    }
//    private void panel1Disappear() {
//        this.jPanel_Info.setVisible(false);
//        this.setSize(originalSize);
//        this.setPreferredSize(originalSize);
//        this.updateUI();
//    }
    private ToolTipPanel toolTipPanel;
    private PlurkerToolTip tooltip;
    private int offsetOfToolTip = 0;

    public void setOffsetOfToolTip(int offsetOfToolTip) {
        this.offsetOfToolTip = offsetOfToolTip;
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void eventDispatched(AWTEvent event) {
        if (event instanceof MouseEvent) {
            MouseEvent mouseevent = (MouseEvent) event;
            if (mouseevent.getID() == MouseEvent.MOUSE_MOVED) {
                Component component = mouseevent.getComponent();
                if (null != component && SwingUtilities.isDescendingFrom(component, this)) {
                    //如果是在這個component內就把notify關掉
                    this.jLabel_Notify.setVisible(false);
                    if (null != this.plurkPool && null == toolTipPanel) {
                        if (Type.Plurk == type && null != this.plurk) {
                            //plurk用的小視窗
                            //toolTipPanel = new PlurkToolTipPanel(this.plurkPool, this.plurk, this);
                        }
                        if (null != this.comment) {
                            //回噗的小視窗
                            toolTipPanel = new CommentToolTipPanel(this.plurkPool, this.comment, this, responseInput);
                        }
                    }

                    if (null != toolTipPanel) {
                        tooltip = PlurkerToolTip.getInstance(this.getClass(), toolTipPanel);
                        int width = this.getWidth();
                        Point locationOnScreen = this.getLocationOnScreen();
                        tooltip.setLocation(locationOnScreen.x + width + offsetOfToolTip, locationOnScreen.y);
                        tooltip.begin();
                    }
                } else {
                    //如果不是在這個component內就把notify打開
                    this.jLabel_Notify.setVisible(true);
                    //離開了component..就要開始計時把PlurkerToolTip關掉
                    //但是還要確保不是跑到別的component去
                }
            } else if (mouseevent.getID() == MouseEvent.MOUSE_CLICKED) {
                Point point = mouseevent.getPoint();
                if (null != tooltip && !SwingUtilities.isDescendingFrom(mouseevent.getComponent(), tooltip)) {
                    tooltip.setVisible(false);
                }
            } else if (mouseevent.getID() == MouseEvent.MOUSE_WHEEL) {
                if (null != tooltip) {
                    tooltip.setVisible(false);
                }
            }

        }
    }
    private static Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public void copyContentToClipboard() {
        String text = this.jEditorPane1.getText();
        StringSelection contents = new StringSelection(text);
        clipboard.setContents(contents, null);
    }

    public void copyContentRawToClipboard() {
        String text = "";
        if (null != plurk) {
            text = plurk.getContentRaw();
        } else if (null != comment) {
            text = comment.toString();
        }

        StringSelection contents = new StringSelection(text);
        clipboard.setContents(contents, null);
    }

    public void copyPlurkToClipboard() {
        String text = "";
        if (null != plurk) {
            text = plurk.toString();
        } else if (null != comment) {
            text = comment.toString();
        }

        StringSelection contents = new StringSelection(text);
        clipboard.setContents(contents, null);
    }

    @Override
    public void plurkChange(PlurkChangeListener.Type type, Data data) {
        if (type == PlurkChangeListener.Type.CommentAdd && data instanceof Comment) {
        }
    }
}

class FixedHTMLEditorKit extends HTMLEditorKit {

    private static FixedHTMLEditorKit fixedHTMLEditorKit = new FixedHTMLEditorKit();

    public final static FixedHTMLEditorKit getInstance() {
        if (null == fixedHTMLEditorKit) {
            fixedHTMLEditorKit = new FixedHTMLEditorKit();
        }
        return fixedHTMLEditorKit;
    }
    final ViewFactory defaultFactory = new FixedHTMLFactory();

    @Override
    public ViewFactory getViewFactory() {
        return defaultFactory;
    }

    static class FixedHTMLFactory extends HTMLEditorKit.HTMLFactory {

        public View create(Element elem) {
            Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
            if (o instanceof HTML.Tag) {
                HTML.Tag kind = (HTML.Tag) o;
                if (kind == HTML.Tag.IMG) {
                    return new PlurkerImageView(elem);
                }
            }
            return super.create(elem);
        }

        public View _create(Element e) {
            View v = super.create(e);
            if (v instanceof InlineView) {
                return new InlineView(e) {
                    public int getBreakWeight(int axis, float pos, float len) {
                        return GoodBreakWeight;
                    }

                    public View breakView(int axis, int p0, float pos, float len) {
                        if (axis == View.X_AXIS) {
                            checkPainter();
                            int p1 = getGlyphPainter().getBoundedPosition(this, p0, pos, len);
                            if (p0 == getStartOffset() && p1 == getEndOffset()) {
                                return this;
                            }
                            return createFragment(p0, p1);
                        }
                        return this;
                    }
                };
            } else if (v instanceof ParagraphView) {
                return new ParagraphView(e) {
                    protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) {
                        if (r == null) {
                            r = new SizeRequirements();
                        }
                        float pref = layoutPool.getPreferredSpan(axis);
                        float min = layoutPool.getMinimumSpan(axis);
                        // Don't include insets, Box.getXXXSpan will include them. 
                        r.minimum = (int) min;
                        r.preferred = Math.max(r.minimum, (int) pref);
                        r.maximum = Integer.MAX_VALUE;
                        r.alignment = 0.5f;
                        return r;
                    }
                };
            }
            return v;
        }
    }
}

class PlurkerHyperlinkListener implements HyperlinkListener {

    @Override
    public void hyperlinkUpdate(HyperlinkEvent e) {
        EventType eventType = e.getEventType();
        URL url = e.getURL();
        //        System.out.println("d: "+e.getDescription());
        //        System.out.println(e.getInputEvent());
        //        System.out.println(e.getSourceElement());
        //        System.out.println("u: "+e.getURL());

        if (EventType.ACTIVATED == eventType) {
        } else if (EventType.ENTERED == eventType) {
        } else if (EventType.EXITED == eventType) {
        }

//        System.out.println(e.getEventType());
//        System.out.println(e);
    }
}