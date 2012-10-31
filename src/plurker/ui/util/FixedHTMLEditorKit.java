/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui.util;

import javax.swing.SizeRequirements;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.*;

/**
 *
 * @author SkyforceShen
 */
public class FixedHTMLEditorKit extends HTMLEditorKit {

    private static FixedHTMLEditorKit fixedHTMLEditorKit;

    public final static FixedHTMLEditorKit getInstance() {
        if (null == fixedHTMLEditorKit) {
            fixedHTMLEditorKit = new FixedHTMLEditorKit();
        }
        return fixedHTMLEditorKit;
    }
    final ViewFactory defaultFactory = new FixedHTMLEditorKit.FixedHTMLFactory();

    @Override
    public ViewFactory getViewFactory() {
        return defaultFactory;
    }

    static class FixedHTMLFactory extends HTMLEditorKit.HTMLFactory {

        public View create(Element elem) {
            Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
            if (o instanceof HTML.Tag) {
                HTML.Tag kind = (HTML.Tag) o;
                if (kind == HTML.Tag.IMG) {
                    return new PlurkerImageView(elem);
                }
            }
            return super.create(elem);
        }

        public View _create(Element e) {
            View v = super.create(e);
            if (v instanceof InlineView) {
                return new InlineView(e) {
                    public int getBreakWeight(int axis, float pos, float len) {
                        return GoodBreakWeight;
                    }

                    public View breakView(int axis, int p0, float pos, float len) {
                        if (axis == View.X_AXIS) {
                            checkPainter();
                            int p1 = getGlyphPainter().getBoundedPosition(this, p0, pos, len);
                            if (p0 == getStartOffset() && p1 == getEndOffset()) {
                                return this;
                            }
                            return createFragment(p0, p1);
                        }
                        return this;
                    }
                };
            } else if (v instanceof ParagraphView) {
                return new ParagraphView(e) {
                    protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) {
                        if (r == null) {
                            r = new SizeRequirements();
                        }
                        float pref = layoutPool.getPreferredSpan(axis);
                        float min = layoutPool.getMinimumSpan(axis);
                        // Don't include insets, Box.getXXXSpan will include them. 
                        r.minimum = (int) min;
                        r.preferred = Math.max(r.minimum, (int) pref);
                        r.maximum = Integer.MAX_VALUE;
                        r.alignment = 0.5f;
                        return r;
                    }
                };
            }
            return v;
        }
    }
}
