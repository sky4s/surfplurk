/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;
import java.io.*;

/**
 *
 * @author skyforce
 */
public class JTextPaneTest {

    JFrame frame;
    JTextPane textPane;
    //File   file; 
    //Icon   image; 

    public JTextPaneTest() {
        frame = new JFrame("JTextPane ");
        textPane = new JTextPane();
        //file   =   new   File( "./classes/test/icon.gif "); 
        //image   =   new   ImageIcon(file.getAbsoluteFile().toString()); 
    }

    public void insert(String str, AttributeSet attrSet) {
        Document doc = textPane.getDocument();
        str = "\n " + str;
        try {
            doc.insertString(doc.getLength(), str, attrSet);
        } catch (BadLocationException e) {
            System.out.println("BadLocationException:   " + e);
        }
    }

    public void setDocs(String str, Color col, boolean bold, int fontSize) {
        SimpleAttributeSet attrSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attrSet, col);
        //颜色 
        if (bold == true) {
            StyleConstants.setBold(attrSet, true);
        }//字体类型 
        StyleConstants.setFontSize(attrSet, fontSize);
        //字体大小 
        insert(str, attrSet);
    }

    public void gui() {
        //textPane.insertIcon(image);   //插入图片 
        setDocs("第一行的文字文字文字文字文字文字文字文字文字文字 ", Color.red, false, 20);   //折行测试 
        setDocs("第二行的文字 ", Color.BLACK, true, 25);
        setDocs("第三行的文字 ", Color.BLUE, false, 20);
        frame.getContentPane().add(textPane, BorderLayout.CENTER);
        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.setSize(200, 300);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        JTextPaneTest test = new JTextPaneTest();
        test.gui();
    }
}
