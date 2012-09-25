/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author SkyforceShen
 */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JWindow;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

/**
 * 消息窗口实例
 *
 * @author <a href="mailto:nanjifengchen@163.com">LanXJ</a>
 */
public class WindowMessage extends JWindow implements HyperlinkListener, Runnable {//继承JWindow是为了实现window，实现Runnable是为了线程控制窗口位置  

    //定义枚举值  
    public enum MOUSEOVER_TYPE {

        FAST("当鼠标移动到窗口上时候，迅速将窗口的位置调整到初始化完成的结果位置（极速效果）"), SLOWLY("当鼠标移动到窗口上时候，系统慢慢将窗口的位置调整到初始化完成的结果位置（滑出效果）");
        String info;

        MOUSEOVER_TYPE(String info) {
            this.info = info;
        }

        public String toString() {
            return info;
        }
    }

    public enum MESSAGE_WINDOW_STATE {

        NOINIT, OPENNING, OPEN, CLOSING, CLOSED
    }
    private MESSAGE_WINDOW_STATE messageWindowState;
    private MOUSEOVER_TYPE mouseOverType;//默认是极速效果  
    private Integer screenWidth, screenHeight, msgWindowWidth, msgWindowHeight, stateBarHeight;
    private String title, msg;
    private JEditorPane jEditorPane = new JEditorPane();//一款可以支持HTML的组件  
    boolean canHidden = true;

    private void initValue(String width, String height, String title, String msg) {
        this.messageWindowState = MESSAGE_WINDOW_STATE.NOINIT;
        this.mouseOverType = MOUSEOVER_TYPE.FAST;//默认是极速效果    `P  
//        this.mouseOverType = MOUSEOVER_TYPE.SLOWLY;//默认是滑出效果  

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();//获取屏幕大小  
        this.screenWidth = dimension.width;//屏幕宽度  
        this.screenHeight = dimension.height;//屏幕高度  
        this.msgWindowWidth = Integer.parseInt(width);//消息窗体高度  
        this.msgWindowHeight = Integer.parseInt(height);//消息窗体宽度  
        this.stateBarHeight = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration()).bottom;//获取任务栏高度  
        this.title = title;//消息窗体标题头  
        this.msg = msg;//消息内容  
    }

    /**
     * 初始化window
     */
    private void initWindow(int x) {
        this.setSize(this.msgWindowWidth, this.msgWindowHeight);//设置窗口大小  
        this.setLayout(new BorderLayout());//设置布局  

        jEditorPane.setEditable(false);//设置jEditorPane不可编辑  
        jEditorPane.setSize(this.msgWindowWidth, this.msgWindowHeight);//设置jEditorPane大小  
        jEditorPane.setContentType("text/html");//设置jEditorPane的现实格式  
        //设置窗口内容  
        jEditorPane.setText(
                "<html>"
                + "<head></head>"
                + "<body>"
                + "<table border=0 cellspacing=0 width=100% height=100%>"
                + "<tr style='height:30;background-color:#81A6C8;'>"
                + "<td align=left>"
                + this.title
                + "</td>"
                + "<td align=right><a href=\"http://closeMsgWindow\" style=\"cursor:hand;\">关闭</a></td>"
                + "</tr>"
                + "<tr style=\"background-color:#E0F2FF;"
                + "<td valign=top align=left colspan=2 height=" + (this.msgWindowHeight - 30) + ">"
                + this.msg
                + "<br><br><center>友情链接：<a href=\"http://tieba.baidu.com\">百度贴吧</a></center>"
                + "</td>"
                + "</tr>"
                + "</table>"
                + "</body>");

        this.getContentPane().add(jEditorPane, BorderLayout.CENTER);//将jEditorPane加入window中  
        this.setAlwaysOnTop(true);//设置窗体总在最前  
        this.setLocation(x, this.screenHeight);//初始化窗体位置  
    }

    private void addListener() {
        /**
         * 为jEditorPane加事件监听，实现鼠标进入后暂时还原消息窗体的功能
         */
        jEditorPane.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (mouseOverType == MOUSEOVER_TYPE.FAST) {//根据配置快速显示消息窗体  
                    System.err.println("mouse in");
                    WindowMessage.this.setLocation(WindowMessage.this.screenWidth - WindowMessage.this.msgWindowWidth,
                            WindowMessage.this.screenHeight - WindowMessage.this.msgWindowHeight - WindowMessage.this.stateBarHeight);
                }
                WindowMessage.this.canHidden = false;//鼠标进入  修改隐藏条件为不成立  
            }

            public void mouseExited(MouseEvent e) {
                System.err.println("mouse out");
                WindowMessage.this.canHidden = true;
            }
        });
        /**
         * 为jEditorPane加Hyperlink事件监听，实现点击链接的功能（包括关闭窗体的功能）
         */
        jEditorPane.addHyperlinkListener(this);
    }

    public WindowMessage(String width, String height, String title, String msg) {
//        this(width, height, this.screenWidth - this.msgWindowWidth, title, msg);
        initValue(width, height, title, msg);
        initWindow(this.screenWidth - this.msgWindowWidth);
        addListener();
        this.setVisible(true);
        new Thread(this).run();
    }

    public WindowMessage(String width, String height, int x, String title, String msg) {
        initValue(width, height, title, msg);
        initWindow(x);
        addListener();
        this.setVisible(true);
//        new Thread(this).run();
        new Thread(this).start();
    }

    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == EventType.ACTIVATED) {
            if (e.getURL().toString().equals("http://closeMsgWindow")) {
                System.exit(0);//关闭程序窗体  
            } else {
                try {
                    Runtime.getRuntime().exec("explorer.exe " + e.getURL());//打开链接  
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void run() {
        messageWindowState = MESSAGE_WINDOW_STATE.OPENNING;
        begin:
        while (true) {
            //窗体显示  
            while (this.getLocation().y > this.screenHeight - this.msgWindowHeight - this.stateBarHeight) {
                this.setLocation(this.getLocation().x, this.getLocation().y - 2);
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            messageWindowState = MESSAGE_WINDOW_STATE.OPEN;
            try {
                Thread.sleep(5 * 1000);//窗体显示5秒钟  
            } catch (Exception e) {
                e.printStackTrace();
            }
            messageWindowState = MESSAGE_WINDOW_STATE.CLOSING;
            while (this.getLocation().y < this.screenHeight) {
                if (canHidden) {
                    this.setLocation(this.getLocation().x, this.getLocation().y + 2);
                } else {//如果canHidden窗体隐藏条件不成立，就把窗体还原  
                    continue begin;
                }
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            messageWindowState = MESSAGE_WINDOW_STATE.CLOSED;
//            System.exit(0);
            break;
        }
    }

    public static void main(String[] args) {
        for (int x = 0; x < 3; x++) {
            new WindowMessage("200", "100", 200 * x, "标题", "消息:来自消息窗体的提示信息，你可以定义自己的消息体(包括HTML代码体，将会将代码解析到table中。)");
            System.out.println("xx");
        }

    }
}
