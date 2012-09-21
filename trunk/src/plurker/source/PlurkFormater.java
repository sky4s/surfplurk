/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.source;

import com.google.jplurk_oauth.data.ContextIF;
import com.google.jplurk_oauth.data.Plurk;
import com.google.jplurk_oauth.data.UserInfo;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
import javax.net.ssl.HttpsURLConnection;
//import org.jivesoftware.stringprep.Punycode;

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
import sun.net.idn.Punycode;
//import org.jivesoftware.stringprep.*;

/**
 *
 * @author SkyforceShen
 */
public class PlurkFormater {

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
        long ownderid = plurk.getOwnerId();
        UserInfo userinfo = plurkPool.getUserInfo(ownderid);
        String pretext = getPreText(userinfo, plurk);
        String content = pretext + filterConten(plurk.getContent());
        return content;
    }

    public String getContent(ContextIF context) throws JSONException {
        long ownderid = context.getOwnerId();
        UserInfo userinfo = plurkPool.getUserInfo(ownderid);
        String pretext = getPreText(userinfo, context);
        String content = pretext + filterConten(context.getContent());
        return content;
    }

    private void precache(String url, String path) {
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

    public String getPreText(UserInfo userInfo, ContextIF context) throws JSONException {
        return getPreText(userInfo, context, false);
    }

    public String getPreText(UserInfo userInfo, ContextIF context, boolean isPlurk) throws JSONException {
        String qualifier = context.getQualifier();
        Qualifier q = Qualifier.getQualifier(qualifier);
        String qualifierTranslated = null;
        qualifierTranslated = context.getQualifierTranslated();
        qualifier = qualifierTranslated != null && qualifierTranslated.length() != 0 ? qualifierTranslated : qualifier;
        String pretext = getDisplayName(userInfo) + " " + q.toHTMLString(" " + qualifier + " ") + " ";
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

        return pretext;
    }

    public String getPreText(UserInfo userInfo, Plurk plurk) throws JSONException {
        return getPreText(userInfo, plurk, true);
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
//        if (null != userInfo) {
//            String displayName = userInfo.getDisplayName();
//            if (null == displayName || "".equals(displayName)) {
//                displayName = userInfo.getNickName();
//            }
//            String htmlNameColor = userInfo.getHTMLNameColor();
//            String result = "<b><font color=\"" + htmlNameColor + "\">" + displayName + "</font></b>";
//            return result;
//        } else {
//            return "N/A";
//        }

    }
//    private Parser parser = new Parser();

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
            String src = e.attr("src");
            if (PlurkerApplication.cacheImage) {
                cacheImage(src);
            }
        }

        Elements atag = doc.select("a");
//        for (int x = 0; x < atag.size(); x++) {
//            Element e = atag.get(x);
//            String text = e.text();
//            e.html("<u>" + text + "</u>");
//            Element child = e.child(0);
//            e.appendChild(child);
//        }

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
//        try {

        String host = url.getHost();
        //            System.out.println(host);
        //            try {
        //                host = java.net.URLEncoder.encode(url.getHost(), "Unicode");
        //            } catch (UnsupportedEncodingException ex) {
        //                Logger.getLogger(PlurkFormater.class.getName()).log(Level.SEVERE, null, ex);
        //            }
        //            String host = url.getHost();
        //            org.jivesoftware.stringprep.
        //            Punycode.encode(null, blns)
        //            host = org.jivesoftware.stringprep.Punycode.encode(host);
        host = IDN.toASCII(host);


        String path = url.getPath();
//        try {
//            path = java.net.URLEncoder.encode(path, "UTF-8");
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(PlurkFormater.class.getName()).log(Level.SEVERE, null, ex);
//        }

//            StringBuffer punyurl = Punycode.encode(new StringBuffer(host), null);
//            host = punyurl.toString();
//            host = java.net.URLEncoder.encode(host, "UTF-8");
//            String path = java.net.URLEncoder.encode(url.getPath(), "UTF-8");
        newurl = url.getProtocol() + "://" + host + path;

//        } catch (PunycodeException ex) {
//            Logger.getLogger(PlurkFormater.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return src;
        return newurl;
    }

    private void cacheImage(String src) {
        URL url = null;
        try {
            url = new URL(encodeToUTF8(src));
        } catch (MalformedURLException ex) {
            Logger.getLogger(PlurkFormater.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        GIFFrame[] gifFrame = null;
        Image image = null;
        //僅處理 gif
        if (-1 != src.indexOf(".gif") || -1 != src.indexOf(".GIF")) {
            try {
                InputStream inputStream = url.openConnection().getInputStream();
                gifFrame = GIFFrame.getGIFFrame(inputStream);
//                gifFrame = GIFFrame.getGIFFrame(url.openStream());
            } catch (IOException ex) {
                Logger.getLogger(PlurkFormater.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            //檢查是不是delay time==0
            if (isDelayTimeZero(gifFrame)) {
                //若是, 則重新設定delay time並且產生新的gif
                image = getGIFImage(gifFrame, 100);
            }
        }
        if (null == plurkPool.imageCache.get(url)) {
            if (null == image) {
                if (url.getProtocol().equals("https")) {
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
                    image = Toolkit.getDefaultToolkit().createImage(httpurl);
                } else {
                    image = Toolkit.getDefaultToolkit().createImage(url);
                }
            }
            plurkPool.imageCache.put(url, image);
        }
    }

    private Image createImageFromHttps(URL url) {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            int c;
            while ((c = in.read()) != -1) {
                byteArrayOut.write(c);
            }
            Image image = Toolkit.getDefaultToolkit().createImage(
                    byteArrayOut.toByteArray());
            return image;
        } catch (IOException ex) {
            Logger.getLogger(PlurkFormater.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
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
//    private NodeList recurseTagRemove(NodeList list) throws MalformedURLException {
//        if (list == null) {
//            return null;
//        }
//
//        Node node = null;
//        SimpleNodeIterator iterator = list.elements();
//
//        while (iterator.hasMoreNodes()) {
//            node = iterator.nextNode();
//            if (node == null) {
//                break;
//            }
//            if (node instanceof Tag) {
//                Tag tag = (Tag) node;
//                if (tag instanceof ImageTag)//<img> 标签
//                {
//                    ImageTag imageTag = (ImageTag) tag;
//                    String imageURL = imageTag.getImageURL();
//                    boolean isGIF = false;// imageURL.indexOf("gif") != -1;
//                    URL url = new URL(imageURL);
//                    if (null == plurkPool.imageCache.get(url) && !isGIF) {
//                        Image image = Toolkit.getDefaultToolkit().createImage(url);
//                        imageTag.setAttribute("height", "40");
//                        if (null == imageTag.getAttribute("width")) {
//                        }
//
//                        plurkPool.imageCache.put(url, image);
//                    }
//                }
//
//                recurseTagRemove(node.getChildren());
//            }
//        }
//
//        return list;
//    }
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
//        } catch (JSONException ex) {
//            Logger.getLogger(PlurkerApplication.class.getName()).log(Level.SEVERE, null, ex);


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
}
