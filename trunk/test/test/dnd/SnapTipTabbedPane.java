/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.dnd;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.JToolTip;

/**
 *
 * @author SkyforceShen
 */
public class SnapTipTabbedPane extends JTabbedPane {
    //這個是縮略圖縮略比例

    private double scaleRatio = 0.5;
    //這兒放添加頁籤與其ToolTipText之間的映射，
    //後面定義的ImageToolTip通過它來獲取當前頁籤的容器組件
    private HashMap maps = new HashMap();
    //覆蓋insertTab，在添加每個頁籤時註冊提示框

    public void insertTab(String title, Icon icon, Component component, String tip, int index) {
        //這個提示文字將作為訪問頁籤面板的主鍵，提示框並不顯示它
        tip = "tab" + component.hashCode();
        maps.put(tip, component);
        //由於這兒tip不為空，調用父類的insertTab將該頁籤註冊成有提示框的
        super.insertTab(title, icon, component, tip, index);
    }
    //刪除面板，去掉映射

    public void removeTabAt(int index) {
        Component component = getComponentAt(index);
        maps.remove("tab" + component.hashCode());
        super.removeTabAt(index);
    }
    //覆蓋JComponent的createToolTip，提供自定義的ImageToolTip來顯示圖片

    public JToolTip createToolTip() {
        ImageToolTip tooltip = new ImageToolTip();
        tooltip.setComponent(this);
        return tooltip;
    }
    //圖片提示框的實現類

    class ImageToolTip extends JToolTip {
        //重新根據圖片大小來定義preferredSize，注意一定要重載這個方法
        //否則父類會使用當前的TipText來計算彈出提示框的大小

        public Dimension getPreferredSize() {
//獲取當前提示文字，並根據以它作為主鍵提取出對應面板組件。
//注意這兒提示文字沒有顯示作用，只是用來訪問當前組件
            String tip = getTipText();
            Component component = (Component) maps.get(tip);
            if (component != null) //根據面板的大小乘以縮略比例得到該preferredSize
            {
                return new Dimension((int) (getScaleRatio() * component.getWidth()), (int) (getScaleRatio() * component.getHeight()));
            } else {
                return super.getPreferredSize();
            }
        }
        //畫提示框的圖片內容

        public void paintComponent(Graphics g) {
            //同上面方法一樣根據提示文本獲取當前rollover的面板
            String tip = getTipText();
            Component component = (Component) maps.get(tip);
            if (component instanceof JComponent) {
                //必須是輕量級組件才能渲染
                JComponent jcomponent = (JComponent) component;
                //使用g2d的transform方法來放大縮小圖片
                Graphics2D g2d = (Graphics2D) g;
                //先保存老的transform
                AffineTransform at = g2d.getTransform();
                //設置縮略比例
//                g2d.transform(AffineTransform.getScaleInstance(getScaleRatio(), getScaleRatio()));
                
//                Dimension preferredSize = getPreferredSize();
                g2d.setColor(Color.red);
                g2d.fillRect(0,0,100,100);
//                g2d.drawOval(0, 0, preferredSize.width, preferredSize.height);

                //這兒很重要，以前的一篇文章也曾提到過，凡是採用
                //Swing輕量級組件渲染的要保證組件不是doubleBuffered
                //否則會影響畫到屏幕上的效果，屏幕往往會花掉。
                ArrayList dbcomponents = new ArrayList();
                //這兒採用遞歸的方式將組件樹上的所有組件都禁止掉雙緩衝
                //屬性，並且將這些組件放在一個數組中，以備恢復用
                updateDoubleBuffered(jcomponent, dbcomponents);
                //渲染
                jcomponent.paint(g);
                //恢復雙緩衝屬性
                resetDoubleBuffered(dbcomponents);
                //恢復以前的transform
                g2d.setTransform(at);
            }
        }
        //這個方法遞歸遍歷以component為根的組件樹，將組件樹上的所有組件禁止其雙緩衝

        private void updateDoubleBuffered(JComponent component, ArrayList dbcomponents) {
            if (component.isDoubleBuffered()) {
                dbcomponents.add(component);
                component.setDoubleBuffered(false);
            }
            for (int i = 0; i < component.getComponentCount(); i++) {
                Component c = component.getComponent(i);
                if (c instanceof JComponent) {
                    updateDoubleBuffered((JComponent) c, dbcomponents);
                }
            }
        }
        //根據前面記錄的數組恢復雙緩衝

        private void resetDoubleBuffered(ArrayList dbcomponents) {
            for (Object obj : dbcomponents) {
                JComponent component = (JComponent) obj;
                component.setDoubleBuffered(true);

            }
        }
    }

    public double getScaleRatio() {
        return scaleRatio;
    }

    public void setScaleRatio(double scaleRatio) {
        this.scaleRatio = scaleRatio;
    }
}
