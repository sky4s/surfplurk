/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author SkyforceShen
 */
public class Utils {

    /**
     * Read an ASCII File
     *
     * @return String
     */
    public static String readContent(File file) {
        FileInputStream stream = null;
        if (file.exists()) {
            try {
                stream = new FileInputStream(file);
            } catch (FileNotFoundException ex) {
                throw new IllegalArgumentException("File " + file.getName()
                        + " is unreadable : " + ex.toString());
            }
        } else {
            return null;
        }


        StringBuffer text = (file != null) ? new StringBuffer((int) file.length())
                : new StringBuffer();
        try {
//      FileReader fr = new FileReader(file);
            BufferedReader b = new BufferedReader(new InputStreamReader(stream));
            boolean eof = false;
            String line;
            String ret = "\n";
            while (!eof) {
                line = b.readLine();
                if (line == null) {
                    eof = true;
                } else {
                    text.append(line);
                    text.append(ret);
                }
            }
            b.close();
        } catch (IOException e) {
            throw new IllegalArgumentException("File " + file.getName()
                    + " is unreadable : " + e.toString());
        }
        return text.toString();
    }
}
