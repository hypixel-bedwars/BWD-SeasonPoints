package dev.bwd.seasonpoints.rendering.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public final class RenderUtils {

    private RenderUtils() {}

    /**
     * Nearest-neighbor hints — keeps bitmap glyphs crisp at any integer scale.
     */
    public static void configurePixelPerfect(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    }

    /**
     * Smooth bilinear hints — for background shapes and UI chrome.
     */
    public static void configureSmooth(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
    }

    /**
     * Draws a filled, optionally-rounded rectangle.
     * Radius is clamped so it never exceeds half of either dimension.
     */
    public static void drawRoundedRect(Graphics2D g, int x, int y, int width, int height,
                                       int radius, Color color) {
        int r = Math.min(radius, Math.min(width / 2, height / 2));
        g.setColor(color);
        if (r <= 0) {
            g.fillRect(x, y, width, height);
        } else {
            configureSmooth(g);
            g.fillRoundRect(x, y, width, height, r * 2, r * 2);
        }
    }

    /**
     * Two-pass progress bar: background rect, then filled rect clipped to progress.
     */
    public static void drawProgressBar(Graphics2D g, int x, int y, int width, int height,
                                       float progress, Color fillColor, Color bgColor,
                                       int radius) {
        float clamped = Math.max(0f, Math.min(1f, progress));
        drawRoundedRect(g, x, y, width, height, radius, bgColor);
        int fillWidth = Math.round(width * clamped);
        if (fillWidth > 0) {
            drawRoundedRect(g, x, y, fillWidth, height, radius, fillColor);
        }
    }
}
