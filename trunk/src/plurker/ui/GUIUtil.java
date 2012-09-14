/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui;

import com.ctreber.aclib.image.ico.ICOFile;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import javax.swing.JEditorPane;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
//import org.fit.cssbox.swingbox.BrowserPane;

/**
 *
 * @author SkyforceShen
 */
public class GUIUtil {

    public final static void initGUI() {
        ToolTipManager.sharedInstance().setInitialDelay(0);
        GUIUtil.setUI(new FontUIResource(GUIUtil.font));
    }
    public static Font font = new Font("微軟正黑體", Font.PLAIN, 13);
    public static Font boldFont = new Font("微軟正黑體", Font.BOLD, 13);
    private static JEditorPane dummyEditorPane;//= null;

    public static int getContentHeight(String content, int width, Dictionary imageCache) {
//        if (null == dummyEditorPane) {
        JEditorPane dummyEditorPane = new JEditorPane();
        dummyEditorPane.setEditorKit(FixedHTMLEditorKit.getInstance());
        dummyEditorPane.setContentType("text/html");
        dummyEditorPane.setFont(GUIUtil.font);
        dummyEditorPane.getDocument().putProperty("imageCache", imageCache);
        dummyEditorPane.setBorder(null);
//        }

        dummyEditorPane.setSize(width, Short.MAX_VALUE);
        dummyEditorPane.setText(content);

        return dummyEditorPane.getPreferredSize().height;
    }
//    public final static int DefaultScrollBarWidth = 15;
    public final static int DefaultUnitIncrement = 20;
    public final static int PlurksPerFetch = 20;

    public static void setUI(FontUIResource f) {
        Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value != null && value instanceof FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }

    public static java.util.List getIconImages(String icoFilename) throws IOException {
        ICOFile ico = new ICOFile(icoFilename);
        java.util.List list = ico.getImages();
        return list;
    }
}
