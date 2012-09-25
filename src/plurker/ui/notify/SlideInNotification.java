/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.ui.notify;

import plurker.ui.notify.TinyNotificationPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public class SlideInNotification extends Object {

//    protected static final int ANIMATION_TIME = 500;
//    protected static final float ANIMATION_TIME_F =
//            (float) ANIMATION_TIME;
//    protected static final int ANIMATION_DELAY = 16;
    private int dispearTime = 320;
    private int dispearWait = 3000;
    private int animationTime = 320;
    private int animationDelay = 16;
    private JWindow window;
    private JComponent contents;
    private AnimatingSheet animatingSheet;
    private static Rectangle desktopBounds;
    private Dimension windowSize;
    private Timer animationTimer;
    private Timer dispearTimer;
    private int showX, startY;
    private long animationStart;

    public int getShowX() {
        return showX;
    }

    public int getStartY() {
        return startY;
    }

    public JWindow getWindow() {
        return window;
    }

    public static Rectangle getDesktopBounds() {
        initDesktopBounds();
        return desktopBounds;
    }

    public SlideInNotification() {
        initDesktopBounds();
    }

    public SlideInNotification(JComponent contents) {
        this();
        setContents(contents);
    }

    public SlideInNotification(TinyNotificationPanel contents) {
        this((JComponent) contents);
        contents.getjButton_Close().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (null != dispearTimer) {
                    dispearTimer.stop();
                    dispearTimer = null;
                }
                window.getContentPane().removeAll();
                window.setVisible(false);
            }
        });
    }

    protected static void initDesktopBounds() {
        if (null == desktopBounds) {
            GraphicsEnvironment env =
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
            desktopBounds = env.getMaximumWindowBounds();
        }
    }

    public void setContents(JComponent contents) {
        this.contents = contents;
//        JWindow tempWindow = new JWindow();
//        tempWindow.getContentPane().add(contents);
//        tempWindow.pack();
//        windowSize = tempWindow.getSize();
//        tempWindow.getContentPane().removeAll();
        window = new JWindow();
//        animatingSheet = new AnimatingSheet();
//        animatingSheet.setSource(contents);
//        window.getContentPane().add(animatingSheet);

        window.addMouseMotionListener(new NotifyMouseMotionListener());
    }

    class NotifyMouseMotionListener implements MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent e) {
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (null != dispearTimer) {
                dispearTimer.stop();
                dispearTimer = null;
            }
        }
    }

    public void showAt(int x) {
        showAt(x, desktopBounds.y + desktopBounds.height);
    }

    private void initWindowSize() {
        window.getContentPane().add(contents);
        window.pack();
        windowSize = window.getSize();
        window.getContentPane().removeAll();
    }

    public void showAt(int x, int y) {
        // create a window with an animating sheet
        // copy over its contents from the temp window
        // animate it
        // when done, remove animating sheet and add real contents
        initWindowSize();

        animatingSheet = new AnimatingSheet();
        animatingSheet.setSource(contents);
        window.getContentPane().add(animatingSheet);

        showX = x;
        startY = y;//desktopBounds.y + desktopBounds.height;

        ActionListener animationLogic = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                long elapsed =
                        System.currentTimeMillis() - animationStart;
                if (elapsed > animationTime) {
                    // put real contents in window and show
                    window.getContentPane().removeAll();
                    window.getContentPane().add(contents);
                    window.pack();
                    window.setLocation(showX,
                            startY - window.getSize().height);
                    window.setVisible(true);
                    window.repaint();
                    if (null != animatingSheet) {
                        animationTimer.stop();
                        animationTimer = null;
                    }

                    ActionListener animationLogic = new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            dispear();
                            if (null != dispearTimer) {
                                dispearTimer.stop();
                                dispearTimer = null;
                            }
                        }
                    };
                    dispearTimer = new Timer(dispearWait, animationLogic);
                    dispearTimer.start();
                } else {
                    if (null != animatingSheet) {
                        // calculate % done
                        float progress =
                                (float) elapsed / animationTime;
                        // get height to show
                        int animatingHeight =
                                (int) (progress * windowSize.getHeight());
                        animatingHeight = Math.max(animatingHeight, 1);
                        animatingSheet.setAnimatingHeight(animatingHeight);
                        window.pack();
                        window.setLocation(showX,
                                startY - window.getHeight());
                        window.setVisible(true);
                        window.repaint();
                    }
                }
            }
        };
        animationTimer =
                new Timer(animationDelay, animationLogic);
        animationStart = System.currentTimeMillis();
        animationTimer.start();
    }

    public void dispear() {
//        if (null != animationTimer && animationTimer.isRunning()) {
//            return;
//        }

        ActionListener animationLogic = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                long elapsed =
                        System.currentTimeMillis() - animationStart;
                if (elapsed > dispearTime) {
                    // put real contents in window and show
                    window.getContentPane().removeAll();
                    window.setVisible(false);
                    if (null != animatingSheet) {
                        animationTimer.stop();
                        animationTimer = null;
                    }
                } else {
                    if (null != animatingSheet) {
                        // calculate % done
                        float progress =
                                1f - ((float) elapsed) / dispearTime;
                        // get height to show
                        int animatingHeight =
                                (int) (progress * windowSize.getHeight());
                        animatingHeight = Math.max(animatingHeight, 0);
                        animatingSheet.setAnimatingHeight(animatingHeight);

                        window.getContentPane().removeAll();
                        window.getContentPane().add(animatingSheet);

                        window.pack();
                        window.setLocation(showX,
                                startY - window.getHeight());
                        window.setVisible(true);
                        window.repaint();
                    }
                }
            }
        };

        animationTimer =
                new Timer(animationDelay, animationLogic);
        animationStart = System.currentTimeMillis();
        animationTimer.start();
    }

    class AnimatingSheet extends JPanel {

        Dimension animatingSize = new Dimension(0, 1);
        JComponent source;
        BufferedImage offscreenImage;

        public AnimatingSheet() {
            super();
            setOpaque(true);
        }

        public void setSource(JComponent source) {
            this.source = source;
            animatingSize.width = source.getWidth();
            makeOffscreenImage(source);
        }

        public void setAnimatingHeight(int height) {
            animatingSize.height = height;
            setSize(animatingSize);
        }

        private void makeOffscreenImage(JComponent source) {
            GraphicsEnvironment ge =
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsConfiguration gfxConfig =
                    ge.getDefaultScreenDevice().getDefaultConfiguration();
            offscreenImage =
                    gfxConfig.createCompatibleImage(source.getWidth(),
                    source.getHeight());
            Graphics2D offscreenGraphics =
                    (Graphics2D) offscreenImage.getGraphics();
            // windows workaround
            offscreenGraphics.setColor(source.getBackground());
            offscreenGraphics.fillRect(0, 0,
                    source.getWidth(), source.getHeight());
            // paint from source to offscreen buffer
//            System.out.println((source == null) + " " + (offscreenGraphics == null));

//                    Window w =  SwingUtilities.getWindowAncestor(source);
            System.out.println(window.toString());
            source.paint(offscreenGraphics);
        }

        public Dimension getPreferredSize() {
            return animatingSize;
        }

        public Dimension getMinimumSize() {
            return animatingSize;
        }

        public Dimension getMaximumSize() {
            return animatingSize;
        }

        public void update(Graphics g) {
            // override to eliminate flicker from
            // unneccessary clear
            paint(g);
        }

        public void paint(Graphics g) {
            // get the top-most n pixels of source and 
            // paint them into g, where n is height
            // (different from sheet example, which used bottom-most)
            BufferedImage fragment =
                    offscreenImage.getSubimage(0,
                    0,
                    source.getWidth(),
                    animatingSize.height);
            g.drawImage(fragment, 0, 0, this);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Icon errorIcon = UIManager.getIcon("OptionPane.errorIcon");
        JLabel label = new JLabel("Your application asplode");
        TinyNotificationPanel panel = new TinyNotificationPanel(label, 300);
//        JFrame label = new JFrame();
//        label.setSize(300,100);
//        label.setBorder(Border.);
        SlideInNotification slider = new SlideInNotification(panel);
        int preferwidth = panel.getPreferredSize().width;
        int desktopwidth = slider.desktopBounds.width;
        int pos = desktopwidth - preferwidth;


//        SlideInNotification slider2 = new SlideInNotification(label);
//        slider.animationDelay = 32;

        slider.showAt(pos);
//        Thread.currentThread().sleep(2000);
//        slider.dispear();
//        slider2.showAt(preferwidth+5);

    }
}
