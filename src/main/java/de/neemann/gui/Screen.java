package de.neemann.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Class used to handle different screen resolution by defining a new default font
 * used by the GUI components. Also all the icons are scaled.
 * Created by hneemann on 09.05.17.
 */
public final class Screen {

    private static final class InstanceHolder {
        private static Screen instance = new Screen();
    }

    private final float size;
    private final float scaling;
    private final Font font;

    /**
     * @return the Screen instance
     */
    public static Screen getInstance() {
        return InstanceHolder.instance;
    }

    private Screen() {
        Font font = new JLabel().getFont();
        float scaling = 1;
        float size = 12;
        try {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            float s = screenSize.height / 90f;
            if (s > 12) {
                scaling = s / 12;
                size = s;
                font = font.deriveFont(s);
                for (Object key : javax.swing.UIManager.getLookAndFeel().getDefaults().keySet()) {
                    if (key.toString().endsWith(".font"))
                        javax.swing.UIManager.put(key, font);
                    if (key.toString().endsWith(".icon") || key.toString().endsWith("Icon")) {
                        Icon icon = UIManager.getIcon(key);
                        if (icon != null)
                            javax.swing.UIManager.put(key, new ScaleIcon(icon, scaling));
                    }
                }
                UIManager.put("ScrollBar.width", (int) (size * 17 / 12));
            }
        } catch (HeadlessException e) {
            // run with defaults if headless
        }
        this.scaling = scaling;
        this.size = size;
        this.font = font;
    }

    private static final class ScaleIcon implements Icon {
        private final Icon icon;
        private final float scaling;
        private final int width;
        private final int height;

        private ScaleIcon(Icon icon, float scaling) {
            this.icon = icon;
            this.scaling = scaling;
            width = (int) (icon.getIconWidth() * scaling);
            height = (int) (icon.getIconHeight() * scaling);
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D gr = (Graphics2D) graphics;
            AffineTransform tr = gr.getTransform();
            gr.translate(x, y);
            gr.scale(scaling, scaling);
            gr.translate(-x, -y);
            icon.paintIcon(component, gr, x, y);
            gr.setTransform(tr);
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }
    }

    /**
     * @return font size
     */
    public float getFontSize() {
        return size;
    }

    /**
     * @return the font
     */
    public Font getFont() {
        return font;
    }

    /**
     * Get scaled font
     *
     * @param scale the scaling factor
     * @return the scaled font
     */
    public Font getFont(float scale) {
        return font.deriveFont(font.getSize2D() * scale);
    }

    /**
     * @return the scaling
     */
    public float getScaling() {
        return scaling;
    }

    /**
     * Scales the given dimension
     *
     * @param dimension the given dimension
     * @return the scaled dimension
     */
    public Dimension scale(Dimension dimension) {
        if (scaling == 1)
            return dimension;
        else
            return new Dimension((int) (dimension.width * scaling), (int) (dimension.height * scaling));
    }

    /**
     * Get a scaled image
     *
     * @param image the original image
     * @return the scaled image
     */
    public Image getScaledImage(BufferedImage image) {
        if (scaling == 1)
            return image;
        else {
            int w = (int) (image.getWidth() * scaling);
            int h = (int) (image.getHeight() * scaling);
            return image.getScaledInstance(w, h, BufferedImage.SCALE_SMOOTH);
        }
    }

}