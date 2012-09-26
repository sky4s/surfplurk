/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SlidingWindow extends JFrame {

    private int xSize, ySize, xLoc, yLoc;
    Timer animationTimer;

    public SlidingWindow(int yloc) {
        super("The Amazing Sliding Window");
        final Dimension screenSize =
                Toolkit.getDefaultToolkit().getScreenSize();
        xSize = 100;
        ySize = 100;
        xLoc = screenSize.width + 10;
        yLoc = yloc;// (screenSize.height / 2) - (ySize / 2);
        setBounds(xLoc, yLoc, xSize, ySize);
        setVisible(true);

//        for (; xLoc > (screenSize.width / 2) - (xSize / 2); xLoc--) {
//            setBounds(xLoc, yLoc, xSize, ySize);
//        }

        ActionListener animationLogic = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (xLoc <= (screenSize.width / 2) - (xSize / 2)) {
                    animationTimer.stop();
                }
                setBounds(xLoc -= 20, yLoc, xSize, ySize);
            }
        };

        animationTimer =
                new Timer(16, animationLogic);
//        animationStart = System.currentTimeMillis();
        animationTimer.start();
    }

    public static void main(String args[]) {
        for (int x = 0; x < 1; x++) {
            SlidingWindow sw = new SlidingWindow(x * 100);
        }
//        sw.addWindowListener(sw.new SlidingExit());
    }

    public class SlidingExit extends WindowAdapter {

        public void windowClosing(WindowEvent event) {
            Dimension screenSize =
                    Toolkit.getDefaultToolkit().getScreenSize();
            for (; xLoc < screenSize.width + 10; xLoc++) {
                setBounds(xLoc, yLoc, xSize, ySize);
            }
            System.exit(0);
        }
    }
}
