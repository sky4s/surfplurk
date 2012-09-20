/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui;

import com.google.jplurk_oauth.data.Comment;
import com.google.jplurk_oauth.data.Plurk;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.json.JSONException;
import plurker.source.PlurkFormater;
import plurker.source.PlurkPool;
import plurker.source.PlurkSourcer;
import plurker.ui.tooltip.CommentToolTipPanel;
import plurker.ui.tooltip.PlurkerToolTip;
import shu.util.Persistence;

/**
 *
 * @author SkyforceShen
 */
public class NotifyPanel2 extends ContentPanel {
    
    @Override
    public void eventDispatched(AWTEvent event) {
        if (event instanceof MouseEvent) {
            MouseEvent mouseevent = (MouseEvent) event;
            if (mouseevent.getID() == MouseEvent.MOUSE_CLICKED) {
                Point point = mouseevent.getPoint();
                Component component = mouseevent.getComponent();
                
                if ((SwingUtilities.isDescendingFrom(component, this) || SwingUtilities.isDescendingFrom(component, this.getParent())) && SwingUtilities.isLeftMouseButton(mouseevent)) {
                    //點到, 就應該設定到current
//                    System.out.println("click");
                    if (null != plurker) {
//                        plurker.setCurrentFollow(this);
                    }
                }
            }
        }
    }
    private PlurkerApplication plurker;
    
    public void setPlurker(PlurkerApplication plurker) {
        this.plurker = plurker;
    }
    
    public NotifyPanel2(Plurk plurk, PlurkPool plurkPool) {
        super(plurk, null, plurkPool, -1, null, Type.Plurk, true);
    }
    
    public NotifyPanel2(Comment comment, PlurkPool plurkPool) {
        super(null, comment, plurkPool, -1, null, Type.Comment, true);
    }
    
    public NotifyPanel2(String content, int width) {
        super(null, null, null, width, content, Type.Unknow, true);
    }
    
    protected String getContent() throws JSONException {
        if (null != plurk) {
            return PlurkFormater.getInstance(plurkPool).getContent(plurk);
        } else if (null != comment) {
            return PlurkFormater.getInstance(plurkPool).getContent(comment);
        } else {
            return "N/A";
        }
    }
    
    public static void main(String[] args) {
        PlurkSourcer.setDoValidToken(false);
        PlurkSourcer plurkSourcerInstance = PlurkerApplication.getPlurkSourcerInstance();
        PlurkPool plurkpool = new PlurkPool(plurkSourcerInstance);
        
        JFrame frame = new JFrame();
        Plurk plurk = (Plurk) Persistence.readObjectAsXML("plurk.obj");
        NotifyPanel2 notifyPanel2 = new NotifyPanel2(plurk, plurkpool);
        notifyPanel2.updateWidth(300);
        frame.add(notifyPanel2);
        frame.pack();
        frame.setVisible(true);
    }
}
