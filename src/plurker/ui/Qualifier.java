/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui;

/**
 *
 * @author SkyforceShen
 */
public enum Qualifier {

    Freestyle(":", "FFFFFF"),
    Loves("loves", "B20C0C"),
    Likes("likes", "CB2728"),
    Shares("shares", "A74949"),
    Gives("gives", "620E0E"),
    Hates("hates", "111"),
    Wants("wants", "8DB241"),
    Wishes("wishes", "5BB017"),
    Needs("needs", "7A9A37"),
    Will("will", "B46DB9"),
    Hopes("hopes", "E05BE9"),
    Asks("asks", "8361BC"),
    Has("has", "777"),
    Was("was", "525252"),
    Wonders("wonders", "2E4E9E"),
    Feels("feels", "2D83BE"),
    Thinks("thinks", "689CC1"),
    Says("says", "E2560B"),
    Is("is", "E57C43"),
    Replurks("replurks", "3B8610");
    public String text;
    public String backgroundColor;

    Qualifier(String text, String backgroundColor) {
        this.text = text;
        this.backgroundColor = backgroundColor;
    }

    public static Qualifier getQualifier(String qualifier) {
        for (Qualifier q : Qualifier.values()) {
            if (q.text.equals(qualifier)) {
                return q;
            }
        }
        return Freestyle;
    }

    public String toHTMLString(String text) {
        return "<span style='background:#" + backgroundColor + "'>" + "<font style='color:white'>" + text + "</font></span>";
    }

    /**
     * Input Qualifier String, then it will select correct Qualifier.
     *
     * @param qualifier Qualifier String
     * @return
     */
    public static Qualifier fromString(String qualifier) {
        for (Qualifier q : Qualifier.values()) {
            if (q.text.equalsIgnoreCase(qualifier)) {
                return q;
            }
        }
        return null;
    }

    public com.google.jplurk_oauth.Qualifier toJplurkOauthQualifier() {
        return com.google.jplurk_oauth.Qualifier.fromString(this.text);
    }
}
