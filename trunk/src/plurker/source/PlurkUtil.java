/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.source;

import com.google.jplurk_oauth.data.Plurk;
import com.google.jplurk_oauth.skeleton.DateTime;
import java.awt.Font;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
//import org.codehaus.jettison.json.JSONException;

/**
 *
 * @author skyforce
 */
public class PlurkUtil {

    private final static Comparator PlurkComparator = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            try {
                Plurk p1 = (Plurk) o1;
                Date postedDate1 = p1.getPostedDate();
                Date postedDate2 = (Date) o2;
                return postedDate1.compareTo(postedDate2);
            } catch (ParseException ex) {
                Logger.getLogger(PlurkUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
            return -1;
        }
    };
//        int index = Collections.binarySearch(noneList, c.getTime(), comp);

    public final static int leftNearBinarySearch0(int arrayLength,
            int binarySearchResult) {
        int result = binarySearchResult;
        if (result < -1) {
            //interstion的場合
            result = -result;
            if (result > arrayLength) {
//        return result - 1;
                return -result;
            } else {
                return result - 2;
            }
        } else if (result != 0 && result != -1) {
            //一般的場合
            return result;
        } else {
            //為0 or -1的場合
            return result;
        }
    }

    public final static int leftNearSearch(List<Plurk> list, DateTime offset) {
        Date time = offset.toCalendar().getTime();
        return leftNearSearch(list, time);
    }

    public final static int leftNearSearch(List<Plurk> list, Date offset) {
        int index = Collections.binarySearch(list, offset, PlurkComparator);
        return leftNearBinarySearch0(list.size(), index);
    }
}
