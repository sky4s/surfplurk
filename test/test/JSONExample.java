/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import org.json.*;

/**
 *
 * @author skyforce
 */
public class JSONExample {

    public static void main(String[] args) throws JSONException {
        JSONObject jobj = new JSONObject();
        jobj.put("JSON", "Hello, World!").put("JSON1", "Hello, World!");
        System.out.println(jobj.toString());

        Integer[] iarray = new Integer[2];
        iarray[0] = 1;
        iarray[1] = 2;
        JSONArray jarry = new JSONArray(iarray);
        System.out.println(jarry.toString());

        JSONStringer stringer = (JSONStringer) new JSONStringer().object().key("JSON").value("Hello, World!").endObject();
        System.out.println(stringer.toString());

        JSONTokener tokener = new JSONTokener(stringer.toString() + jarry.toString());
        for (int x = 0; tokener.more(); x++) {
            System.out.println(x + " " + tokener.nextValue());
        }
    }
}
