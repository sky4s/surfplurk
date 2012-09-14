/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

/**
 *
 * @author SkyforceShen
 */
public class DateTester {

    public static void main(String[] args) throws ParseException {
        String dateString = "Fri, 05 Jun 2009 23:07:13 GMT";
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        dateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
        //    String str dateFormat.format(d);
        Date parse = dateFormat.parse(dateString);
        System.out.println(parse);
        
    }
}
