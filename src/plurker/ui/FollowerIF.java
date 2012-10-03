/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui;

import com.google.jplurk_oauth.data.Plurk;

/**
 *
 * @author SkyforceShen
 */
public interface FollowerIF {

    boolean isInFollow(Plurk plurk);
    boolean isInFollow(long plurkID);
}
