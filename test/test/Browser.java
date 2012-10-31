/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import plurker.source.PlurkFormater;
import plurker.source.PlurkPool;
import plurker.source.PlurkSourcer;
import plurker.ui.PlurkerApplication;
import plurker.ui.util.FixedHTMLEditorKit;

/**
 * Very simplistic "Web browser" using Swing. Supply a URL on the command line
 * to see it initially, and to set the destination of the "home" button. 1998
 * Marty Hall, http://www.apl.jhu.edu/~hall/java/
 */
public class Browser extends JFrame implements HyperlinkListener,
        ActionListener {

    public static void main(String[] args) {
        PlurkSourcer.setDoValidToken(false);
        PlurkSourcer sourcer = PlurkerApplication.getPlurkSourcerInstance();
        PlurkPool pool = new PlurkPool(sourcer);
        PlurkFormater.getInstance(pool);

        if (args.length == 0) {
            new Browser("http://localhost/b.html");
        } else {
            new Browser(args[0]);
        }
    }
    private JIconButton homeButton;
    private JTextField urlField;
    private JEditorPane htmlPane;
    private String initialURL;

    public Browser(String initialURL) {
        super("Simple Swing Browser");
        this.initialURL = initialURL;
        addWindowListener(new ExitListener());
        WindowUtilities.setNativeLookAndFeel();

        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.lightGray);
        homeButton = new JIconButton("home.gif");
        homeButton.addActionListener(this);
        JLabel urlLabel = new JLabel("URL:");
        urlField = new JTextField(30);
        urlField.setText(initialURL);
        urlField.addActionListener(this);
        topPanel.add(homeButton);
        topPanel.add(urlLabel);
        topPanel.add(urlField);
        getContentPane().add(topPanel, BorderLayout.NORTH);

        try {
            htmlPane = new JEditorPane();
            htmlPane.setEditorKit(FixedHTMLEditorKit.getInstance());
            htmlPane.setPage(initialURL);
            htmlPane.setEditable(false);
            htmlPane.addHyperlinkListener(this);
            JScrollPane scrollPane = new JScrollPane(htmlPane);
            getContentPane().add(scrollPane, BorderLayout.CENTER);
        } catch (IOException ioe) {
            warnUser("Can't build HTML pane for " + initialURL
                    + ": " + ioe);
        }

        Dimension screenSize = getToolkit().getScreenSize();
        int width = screenSize.width * 8 / 10;
        int height = screenSize.height * 8 / 10;
        setBounds(width / 8, height / 8, width, height);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent event) {
        String url;
        if (event.getSource() == urlField) {
            url = urlField.getText();
        } else // Clicked "home" button instead of entering URL
        {
            url = initialURL;
        }
        try {
            htmlPane.setPage(new URL(url));
            urlField.setText(url);
        } catch (IOException ioe) {
            warnUser("Can't follow link to " + url + ": " + ioe);
        }
    }

    public void hyperlinkUpdate(HyperlinkEvent event) {
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                htmlPane.setPage(event.getURL());
                urlField.setText(event.getURL().toExternalForm());
            } catch (IOException ioe) {
                warnUser("Can't follow link to "
                        + event.getURL().toExternalForm() + ": " + ioe);
            }
        }
    }

    private void warnUser(String message) {
        JOptionPane.showMessageDialog(this, message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}

class JIconButton extends JButton {

    public JIconButton(String file) {
        super(new ImageIcon(file));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
    }
}

class ExitListener extends WindowAdapter {

    public void windowClosing(WindowEvent event) {
        System.exit(0);
    }
}

class WindowUtilities {

    /**
     * Tell system to use native look and feel, as in previous releases. Metal
     * (Java) LAF is the default otherwise.
     */
    public static void setNativeLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Error setting native LAF: " + e);
        }
    }

    public static void setJavaLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Error setting Java LAF: " + e);
        }
    }

    public static void setMotifLookAndFeel() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
        } catch (Exception e) {
            System.out.println("Error setting Motif LAF: " + e);
        }
    }

    /**
     * A simplified way to see a JPanel or other Container. Pops up a JFrame
     * with specified Container as the content pane.
     */
    public static JFrame openInJFrame(Container content,
            int width,
            int height,
            String title,
            Color bgColor) {
        JFrame frame = new JFrame(title);
        frame.setBackground(bgColor);
        content.setBackground(bgColor);
        frame.setSize(width, height);
        frame.setContentPane(content);
        frame.addWindowListener(new ExitListener());
        frame.setVisible(true);
        return (frame);
    }

    /**
     * Uses Color.white as the background color.
     */
    public static JFrame openInJFrame(Container content,
            int width,
            int height,
            String title) {
        return (openInJFrame(content, width, height, title, Color.white));
    }

    /**
     * Uses Color.white as the background color, and the name of the Container's
     * class as the JFrame title.
     */
    public static JFrame openInJFrame(Container content,
            int width,
            int height) {
        return (openInJFrame(content, width, height,
                content.getClass().getName(),
                Color.white));
    }
}
