/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.image;

//import com.sun.media.imageioimpl.plugins.gif.GIFImageMetadata;
import com.sun.imageio.plugins.gif.GIFImageMetadata;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 *
 * @author SkyforceShen
 */
public class GIFFrame {

    public BufferedImage image;
    public int x;
    public int y;
    public int width;
    public int height;
    public int disposalMethod;
    public int delayTime;

    public static GIFFrame[] getGIFFrame(String filename) throws IOException {
        File gifFile = new File(filename);
        return getGIFFrame(gifFile);
    }

    public static GIFFrame[] getGIFFrame(Object input) throws IOException {
        ImageInputStream imageIn = ImageIO.createImageInputStream(input);
        Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("gif");
        ImageReader reader = null;
        if (iter.hasNext()) {
            reader = iter.next();
        }
        reader.setInput(imageIn, false);
        int count = reader.getNumImages(true);
        final GIFFrame[] frames = new GIFFrame[count];
        for (int i = 0; i < count; i++) {
            frames[i] = new GIFFrame();
            frames[i].image = reader.read(i);
            frames[i].x = ((GIFImageMetadata) reader.getImageMetadata(i)).imageLeftPosition;
            frames[i].y = ((GIFImageMetadata) reader.getImageMetadata(i)).imageTopPosition;
            frames[i].width = ((GIFImageMetadata) reader.getImageMetadata(i)).imageWidth;
            frames[i].height = ((GIFImageMetadata) reader.getImageMetadata(i)).imageHeight;
            frames[i].disposalMethod = ((GIFImageMetadata) reader.getImageMetadata(i)).disposalMethod;
            frames[i].delayTime = ((GIFImageMetadata) reader.getImageMetadata(i)).delayTime;
        }
        return frames;
    }
}
