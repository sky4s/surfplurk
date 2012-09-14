/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author SkyforceShen
 */
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import plurker.ui.PlurkerApplication;
import shu.image.ImageUtils;

public class HTMLLocalImages {

    private BufferedImage responeseImage;
    private BufferedImage newResponeseImage;
    private final static String ResponeseImage = "http://localhost/responeseImage.png";
    private final static String NewResponeseImage = "http://localhost/newResponeseImage.png";
//    public static String localImageSrc = "http:\\test.jpg";
//    public static Image localImage = createImage();
    public static String HTML = "<html>\n"
            + "<body>\n"
            + "Local image accessed from HTML<br>\n"
            + "<img src=\"" + NewResponeseImage + "\">\n"
            + "</body>\n"
            + "</html>";
    JTextPane edit = new JTextPane();

    private void upadteImage() {
        try {
            BufferedImage loadImage = ImageUtils.loadImage("./image/2128abd380f5cf369ff966eb5e36d979.png");
            int width = loadImage.getWidth();
            int height = loadImage.getHeight();
            responeseImage = loadImage.getSubimage(0, 0, width, height / 2);
            newResponeseImage = loadImage.getSubimage(0, height / 2, width, height / 2);
//            Dictionary imageCache = plurkPool.getImageCache();
            cache.put(new URL(ResponeseImage), responeseImage);
            cache.put(new URL(NewResponeseImage), newResponeseImage);
        } catch (IOException ex) {
            Logger.getLogger(PlurkerApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    Dictionary cache = new Hashtable();

    public HTMLLocalImages() {
        JFrame frame = new JFrame("Using local images example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(edit);
        edit.setContentType("text/html");
        edit.setText(HTML);
        upadteImage();
//        try {
//            Dictionary cache = (Dictionary) edit.getDocument().getProperty("imageCache");
//            if (cache == null) {
//                cache = new Hashtable();
        edit.getDocument().putProperty("imageCache", cache);
//            }

//            cache.put(new URL(ResponeseImage), responeseImage);

//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }



        frame.setSize(500, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static Image createImage() {
        BufferedImage img = new BufferedImage(100, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, 100, 50);

        g.setColor(Color.YELLOW);
        g.fillOval(5, 5, 90, 40);
        img.flush();

        return img;
    }

    public static void main(String[] args) throws Exception {
        new HTMLLocalImages();
    }
}