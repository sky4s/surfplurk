/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.awt.Desktop;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class HyperlinkTester extends JFrame {

    public HyperlinkTester() {
        JEditorPane jEditorPane = new JEditorPane();
        jEditorPane.setEditable(false);
        jEditorPane.setContentType("text/html");
        jEditorPane.setText("<html><body><a href=http://www.baidu.com>baidu</a></body></html>");
        jEditorPane.addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
//                        String command = "explorer.exe "
//                                + e.getURL().toString();
//                        Runtime.getRuntime().exec(command);
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.err.println("connection error");
                    }
                }

            }
        });
        this.getContentPane().add(jEditorPane);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        HyperlinkTester temp = new HyperlinkTester();
        temp.setSize(200, 200);
        temp.setVisible(true);
    }
}