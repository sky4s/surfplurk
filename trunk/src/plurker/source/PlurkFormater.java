/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.source;

import com.google.jplurk_oauth.data.Comment;
import com.google.jplurk_oauth.data.ContextIF;
import com.google.jplurk_oauth.data.Plurk;
import com.google.jplurk_oauth.data.UserInfo;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.IDN;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import plurker.image.AnimatedGifEncoder;
import plurker.image.GIFFrame;
import plurker.image.ImageUtils;
import plurker.ui.PlurkerApplication;
import plurker.ui.Qualifier;

/**
 *
 * @author SkyforceShen
 */
public class PlurkFormater {

    public final static Color HighLightColor = new Color(255, 255, 187);
    private PlurkPool plurkPool;
    private static PlurkFormater plurkFormater;

    public final static PlurkFormater getInstance(PlurkPool plurkPool) {
        if (null == plurkFormater) {
            plurkFormater = new PlurkFormater(plurkPool);
        }
        return plurkFormater;
    }

    private PlurkFormater(PlurkPool plurkPool) {
        this.plurkPool = plurkPool;
        initImageCache();
    }

    public String getContent(Plurk plurk) throws JSONException {
        if (null == plurkPool) {
            return plurk.getContent();
        }
//        long start = System.currentTimeMillis();
        long ownderid = plurk.getOwnerId();
        UserInfo userinfo = plurkPool.getUserInfo(ownderid);
        String pretext = getPreText(userinfo, plurk);
//        System.out.println("#getPreText " + (System.currentTimeMillis() - start) / 1000.);
        String content = pretext + filterConten(plurk.getContent());
//        System.out.println("filter " + (System.currentTimeMillis() - start) / 1000.);
        return content;
    }

    public String getContent(Comment comment) throws JSONException {
        return getContent(comment, false);
    }

    public String getContent(Comment comment, boolean isNotify) throws JSONException {
        if (null == plurkPool) {
            return comment.getContent();
        }
//        long start = System.currentTimeMillis();
        long ownderid = comment.getOwnerId();
        UserInfo userinfo = plurkPool.getUserInfo(ownderid);
        String pretext = getPreText(userinfo, comment, false, isNotify);
//        System.out.println("#getPreText " + (System.currentTimeMillis() - start) / 1000.);
        String content = pretext + filterConten(comment.getContent());
//        System.out.println("filter " + (System.currentTimeMillis() - start) / 1000.);
        return content;
    }

    private void precache(String url, String path) {
        if (null == plurkPool) {
            throw new java.lang.IllegalStateException("null == plurkPool");
        }
        try {
            BufferedImage loadImage = ImageUtils.loadImage(path);
            plurkPool.imageCache.put(new URL(url), loadImage);
        } catch (IOException ex) {
            Logger.getLogger(PlurkFormater.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initImageCache() {
        precache("http://replurker.png", "./image/f82b1fb13b0692732212b8d112aa9a14.png");
    }

    public String getPreText(UserInfo userInfo, ContextIF context, boolean isPlurk, boolean isNotify) throws JSONException {
        String qualifier = context.getQualifier();
        Qualifier q = Qualifier.getQualifier(qualifier);
        String qualifierTranslated = null;
        qualifierTranslated = context.getQualifierTranslated();
        qualifier = qualifierTranslated != null && qualifierTranslated.length() != 0 ? qualifierTranslated : getTranslatedQualifier(qualifier);
        String middle = "";
        if (isNotify) {
            if (context instanceof Comment) {
                Comment comment = (Comment) context;
                Plurk plurk = comment.getParentPlurk();
                long plurkOwnerId = plurk.getOwnerId();
                long commentOwnerId = comment.getOwnerId();
                String displayName = (plurkOwnerId == commentOwnerId) ? "自己"
                        : " " + getDisplayName(plurkPool.getUserInfo(plurkOwnerId)) + " ";
                middle = " 回應" + displayName + "的噗";
            }
        }

        String pretext = getDisplayName(userInfo) + middle + " " + q.toHTMLString(" " + qualifier + " ") + " ";
        if (isPlurk) {
            Plurk plurk = (Plurk) context;
            long replurkerId = plurk.getReplurkerId();
            //檢查是不是轉噗
            if (-1 != replurkerId && 0 != replurkerId) {
                UserInfo replurkUserinfo = plurkPool.getUserInfo(replurkerId);
                String replurkPretext = getDisplayName(replurkUserinfo) + " " + Qualifier.Replurks.toHTMLString(" 轉噗<img src='http://replurker.png'>");
                pretext = replurkPretext + pretext;
            }
        }

        return pretext + (isNotify ? "<br>" : "");
    }

    public String getPreText(UserInfo userInfo, Plurk plurk) throws JSONException {
        return getPreText(userInfo, plurk, true, false);
    }

    public static String getName(UserInfo userInfo, boolean withHTMLColor) throws JSONException {
        if (null != userInfo) {
            String displayName = userInfo.getDisplayName();
            if (null == displayName || "".equals(displayName)) {
                displayName = userInfo.getNickName();
            }

            if (withHTMLColor) {
                String htmlNameColor = userInfo.getHTMLNameColor();
                String result = "<b><font color=\"" + htmlNameColor + "\">" + displayName + "</font></b>";
                return result;
            } else {
                return displayName;
            }

        } else {
            return "N/A";
        }
    }

    private static String getDisplayName(UserInfo userInfo) throws JSONException {
        return getName(userInfo, true);
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
//        FileReader reader = new FileReader("b.html");
//        BufferedReader breader = new BufferedReader(reader);
//        String html = "";
//        while (breader.ready()) {
//            html += breader.readLine();
//        }
//        System.out.println(_filterConten(html));

        URL url = new URL(encodeToUTF8("http://搞冰友.的專家.tw/c/CJCXBMN.gif"));
        System.out.println(url);
//        URL url = new URL(encodeToUTF8("http://xn--37q24cf7z.xn--fct5g966e.tw/c/CJDDNUH.gif"));
//        url.openStream();
    }

    public static String _filterConten(String content) {
        Document doc = Jsoup.parse(content);
        Elements imgtag = doc.select("img");

        for (int x = 0; x < imgtag.size(); x++) {
            Element e = imgtag.get(x);
            if (e.hasAttr("height")) {
                String height = e.attr("height");
                if (-1 != height.indexOf("px")) {
                    height = height.substring(0, height.indexOf("px"));
                    e.attr("height", height);
                }
                e.attr("width", height);
            }
        }

        Elements atag = doc.select("a");
        for (int x = 0; x < atag.size(); x++) {
            Element e = atag.get(x);
            String text = e.text();
            e.html("<u>" + text + "</u>");
        }

        return doc.body().html();

    }

    public String filterConten(String content) {
        Document doc = Jsoup.parse(content);
        Elements imgtag = doc.select("img");

        for (int x = 0; x < imgtag.size(); x++) {
            Element e = imgtag.get(x);
            if (e.hasAttr("height")) {
                String height = e.attr("height");
                if (-1 != height.indexOf("px")) {
                    height = height.substring(0, height.indexOf("px"));
                    e.attr("height", height);
                }
                e.attr("width", height);
            }
//            String src = e.attr("src");
//            if (PlurkerApplication.cacheImage) {
//                cacheImage(src);
//            }
        }


        return doc.body().html();

    }

    private boolean isDelayTimeZero(GIFFrame[] gifFrame) {
        if (null == gifFrame || gifFrame.length == 1) {
            return false;
        }
        for (GIFFrame gif : gifFrame) {
            if (0 == gif.delayTime) {
                return true;
            }
        }
        return false;
    }

    private static String encodeToUTF8(String src) {
        URL url = null;
        try {
            url = new URL(src);
        } catch (MalformedURLException ex) {
            Logger.getLogger(PlurkFormater.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        String newurl = null;

        String host = url.getHost();
        host = IDN.toASCII(host);
        String path = url.getPath();
        newurl = url.getProtocol() + "://" + host + path;

        return newurl;
    }

    public Image processGIFImage(URL url) {
        String src = url.getFile();

        if (-1 != src.indexOf(".gif") || -1 != src.indexOf(".GIF")) {
            GIFFrame[] gifFrame = null;
            try {
                HttpURLConnection openConnection = (HttpURLConnection) url.openConnection();
                openConnection.setFollowRedirects(true);
                openConnection.setInstanceFollowRedirects(false);
                openConnection.connect();
                InputStream inputStream = openConnection.getInputStream();
                gifFrame = GIFFrame.getGIFFrame(inputStream);
            } catch (IOException ex) {
                Logger.getLogger(PlurkFormater.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            //檢查是不是delay time==0
            if (isDelayTimeZero(gifFrame)) {
                //若是, 則重新設定delay time並且產生新的gif
                return getGIFImage(gifFrame, 100);
            }
        }
        return null;
    }

    public Image cacheImage(URL url) {
        Image image = processGIFImage(url);
        if (null == plurkPool.imageCache.get(url)) {
            if (null == image) {
                if (url.getProtocol().equals("https")) {
                    image = createImageFromHttps(url);
                } else {
                    image = Toolkit.getDefaultToolkit().getImage(url);
                }
            }
            plurkPool.imageCache.put(url, image);

            if (null != image) {
// Force the image to be loaded by using an ImageIcon.
                ImageIcon ii = new ImageIcon();
                ii.setImage(image);
            }
        }
        return image;
    }

    public Image cacheImage(String src) {
        URL url = null;
        try {
            url = new URL(encodeToUTF8(src));
        } catch (MalformedURLException ex) {
            Logger.getLogger(PlurkFormater.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return cacheImage(url);

    }

    private Image createImageFromHttps(URL url) {
        //因為https需要認證才能抓圖...乾脆直接換成http...
        //此為治標的行為, 先這樣
        String str = url.toString();
        str = str.replaceAll("https", "http");
        URL httpurl = null;
        try {
            httpurl = new URL(str);
        } catch (MalformedURLException ex) {
            Logger.getLogger(PlurkFormater.class.getName()).log(Level.SEVERE, null, ex);
        }
//        Image image = Toolkit.getDefaultToolkit().createImage(httpurl);
        Image image = Toolkit.getDefaultToolkit().getImage(httpurl);
        return image;
    }

    private static Image getGIFImage(GIFFrame[] gifFrame, int delayTime) {
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        encoder.start(buffer);
        encoder.setDelay(100);
        for (GIFFrame gif : gifFrame) {
            encoder.addFrame(gif.image);
        }
        encoder.finish();

        byte[] imageData = buffer.toByteArray();
        Image image = Toolkit.getDefaultToolkit().createImage(imageData);
        buffer = null;
        return image;
    }
    private static SimpleDateFormat GMTDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
    private static SimpleDateFormat PlurkerDateFormat = new SimpleDateFormat("MMM dd - HH:mm", Locale.TAIWAN);

    public static String getPostedString(String posted) {
        try {
            Date date = GMTDateFormat.parse(posted);
            return PlurkerDateFormat.format(date);


        } catch (ParseException ex) {
            Logger.getLogger(PlurkerApplication.class
                    .getName()).log(Level.SEVERE, null, ex);

            return null;
        }
    }

    public static Date getPostedDate(String posted) {
        try {
            return GMTDateFormat.parse(posted);
        } catch (ParseException ex) {
            Logger.getLogger(PlurkFormater.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static String getPostedString(Plurk plurk) {
        try {
            return PlurkerDateFormat.format(plurk.getPostedDate());
        } catch (ParseException ex) {
            Logger.getLogger(PlurkerApplication.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public static Calendar getBeginOfTheDay(Date day) {
        Calendar calandarOfDay = Calendar.getInstance();
        calandarOfDay.setTime(day);
        calandarOfDay.set(Calendar.HOUR, 0);
        calandarOfDay.set(Calendar.MINUTE, 0);
        calandarOfDay.set(Calendar.SECOND, 0);
        return calandarOfDay;
    }
    private final static long OneDayInMillis = 86400000;
    private final static long OneHourInMillis = 3600000;
    private final static long OneMinInMillis = 60000;

    public static String getTimeText(Date postedDate) {
        Calendar posted = Calendar.getInstance();
        posted.setTime(postedDate);
        Calendar now = Calendar.getInstance();

        long nowTimeInMillis = now.getTimeInMillis();
        long postedTimeInMillis = posted.getTimeInMillis();
        long deltaTime = nowTimeInMillis - postedTimeInMillis;

        String timeText = null;
        if (deltaTime > OneDayInMillis) {
            //超過一天
            int nowday = now.get(Calendar.DAY_OF_YEAR);
            int postedday = posted.get(Calendar.DAY_OF_YEAR);
            int deltaDay = nowday - postedday;
            String ampm = posted.get(Calendar.AM_PM) == Calendar.AM ? "上午" : "下午";
            String time = ampm + " " + String.format("%2d:%02d", posted.get(Calendar.HOUR), posted.get(Calendar.MINUTE));
            switch (deltaDay) {
                case 1:
                    timeText = "昨天 " + time;
                    break;
                case 2:
                    timeText = "前天 " + time;
                    break;
                default:
                    timeText = posted.get(Calendar.MONTH) + 1 + " 月 " + posted.get(Calendar.DAY_OF_MONTH) + " 日 " + time;
            }
        } else {
            //一天以內
            if (deltaTime < OneHourInMillis) {
                //一小時以內
                long min = deltaTime / OneMinInMillis;
                timeText = Long.toString(min) + "分鐘以前";
            } else {
                long hour = deltaTime / OneHourInMillis;
                timeText = Long.toString(hour) + "小時以前";
            }
        }
        return timeText;
    }

    public static String getTranslatedQualifier(String qualifier) {
        if (null == QualifierStrings) {
            Qualifier[] qualifierValues = Qualifier.values();
            int size = qualifierValues.length;
            QualifierStrings = new String[size];
            for (int x = 0; x < size; x++) {
                QualifierStrings[x] = qualifierValues[x].text;
            }

        }
//        Arrays.
        int length = QualifierStrings.length;
        int index = 0;
        for (; index < length; index++) {
            if (QualifierStrings[index].equals(qualifier)) {
                break;
            }
        }
//        int index = Arrays.binarySearch(QualifierStrings, qualifier);
        return TranslatedQualifier[index];
    }
//    private static Qualifier[] QualifierValues;
    private static String[] QualifierStrings;
    /**
     *
     */
    public final static String[] TranslatedQualifier = new String[]{":", "愛", "喜歡", "分享", "給", "討厭", "想要", "期待", "需要", "打算", "希望", "問", "已經", "曾經", "好奇", "覺得", "想", "說", "正在"};
}
