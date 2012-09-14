/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.source;

import com.google.jplurk_oauth.data.Comment;
import com.google.jplurk_oauth.data.Data;

/**
 *
 * @author SkyforceShen
 */
public interface PlurkChangeListener {

    public void plurkChange(Type type, Data data);

    public enum Type {

        PlurkAdd, CommentAdd,
    }
}
