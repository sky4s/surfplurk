/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 *
 * @author SkyforceShen
 */
public class ImageUtils {

    public static final BufferedImage loadImage(String filename) throws IOException {
        File file = new File(filename);
        BufferedImage image = ImageIO.read(file);
        return image;
    }

    public static final BufferedImage loadImageFromURL(URL url) throws IOException {
        BufferedImage image = ImageIO.read(url);
        return image;
    }
}
