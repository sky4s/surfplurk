/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author SkyforceShen
 */
import java.applet.Applet;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

//
//
// MediaTrackerDemo
//
//
public class MediaTrackerDemo extends Applet implements Runnable {

    Image[] imgArray = null;
    MediaTracker tracker = null;
    int current = 0;
    Thread animThread = null;

    // Check for a mouse click, to start the images downloading
    public boolean mouseDown(Event evt, int x, int y) {
        if (tracker == null) {
            // Create a new media tracker, to track loading images
            tracker = new MediaTracker(this);

            // Create an array of three images
            imgArray = new Image[2];

            // Start downloading the images
//            for (int index = 0; index < 1; index++) {
            try {

                // Load the image
                imgArray[0] = ImageIO.read(new URL("http://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/211754_100002061543113_1257468604_q.jpg"));

                // Register it with media tracker
                tracker.addImage(imgArray[0], 0);
                System.out.println(tracker.checkAll());
                imgArray[1] = ImageIO.read(new URL("http://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/49103_100000211198366_1142344367_q.jpg"));

                // Register it with media tracker
                tracker.addImage(imgArray[1], 1);
            } catch (IOException ex) {
                Logger.getLogger(MediaTrackerDemo.class.getName()).log(Level.SEVERE, null, ex);
            }
//            }


            // Start animation thread
            animThread = new Thread(this);
            animThread.start();
        }

        return true;
    }

    public void update(Graphics g) {
        // Don't repaint gray background
        paint(g);
    }

    public void paint(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, 200, 200);
        g.setColor(Color.black);

        // Check to see if images have started loading
        if (tracker == null) {
            g.drawString("Click to start loading", 20, 20);
        } else // Check to see if images have loaded
        if (tracker.checkAll()) {
            g.drawImage(imgArray[current++], 0, 0, this);

            if (current >= imgArray.length) {
                current = 0;
            }
        } else // Still loading
        {
            g.drawString("Images are loading...", 20, 20);
        }
    }

    public void run() {
        try {
            tracker.waitForAll();

            for (;;) {
                // Repaint the images
                repaint();

                Thread.sleep(300);
            }
        } catch (InterruptedException ie) {
        };
    }

    public static void main(String[] args) {
        startAppletAsApplicaiton(new MediaTrackerDemo(), 600, 600);
    }

    public final static JFrame startAppletAsApplicaiton(Applet applet,
            int width,
            int height) {
        JFrame frame = new JFrame();
        frame.add(applet);
        Dimension d = new Dimension(width, height);
        frame.setSize(d);
        applet.setSize(d);
        applet.setPreferredSize(d);
        applet.init();
        frame.pack();
        frame.setVisible(true);
        return frame;

    }
}
