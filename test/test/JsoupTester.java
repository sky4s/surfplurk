/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author SkyforceShen
 */
public class JsoupTester {

    public static void main(String[] args) throws IOException {
        File input = new File("a - 複製.html");
        // 直接从字符串中输入 HTML 文档
//        String html = "<html><head><title> 开源中国社区 </title></head>"
//                + "<body><p> 这里是 jsoup 项目的相关文章 </p></body></html>";
//        Document doc = Jsoup.parse(html);
        Document doc = Jsoup.parse(input, "UTF-8", "http://www.oschina.net/");
        //        String title = doc.title();
        //         doc.select("img").add
        //        System.out.println(doc);
        Elements select = doc.select("img");
//        System.out.println(select.toString());
//        System.out.println(select.hasAttr("width"));
        select.attr("width", "30");
        select.attr("height", "30");
//        System.out.println(select.toString());
//        System.out.println(select.html());
//        select.add(new Elemenet());
//        System.out.println(select.html());
        System.out.println(doc.html());
    }
}
