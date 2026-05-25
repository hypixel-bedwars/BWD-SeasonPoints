package dev.bwd.seasonpoints.rendering;

import dev.bwd.seasonpoints.rendering.models.FontSet;
import dev.bwd.seasonpoints.rendering.text.MinecraftTextRenderer;
import dev.bwd.seasonpoints.rendering.text.TextMeasurement;
import dev.bwd.seasonpoints.rendering.utils.ImageUtils;
import dev.bwd.seasonpoints.rendering.utils.RenderUtils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * Central facade for the Minecraft rendering framework.
 *
 * Thread-safe: all state is either immutable or stored in thread-safe
 * structures (MinecraftFont, FontSet, BitmapGlyph colour caches).
 * Safe to call from concurrent Discord command handlers.
 *
 * Quick-start (no atlas):
 * <pre>{@code
 *   FontSet font = ProgrammaticFontBuilder.buildFallbackFont("default");
 *   MinecraftFont fonts = new MinecraftFont();
 *   // register the programmatic font directly:
 *   fonts.loadFromAtlas("default", /* your own atlas stream or skip *‌/);
 *   // OR just keep the FontSet and pass it to MinecraftTextRenderer directly.
 *
 *   CanvasRenderer renderer = CanvasRenderer.withFallbackFont();
 *   byte[] png = renderer.generateMinecraftImageBytes(
 *       List.of("§aHello §lWorld§r!"), true, 2f, "default");
 * }</pre>
 *
 * With a real Minecraft atlas (ascii.png):
 * <pre>{@code
 *   MinecraftFont fonts = new MinecraftFont();
 *   try (InputStream is = plugin.getResource("assets/fonts/ascii.png")) {
 *       fonts.loadFromAtlas("minecraft", is);
 *   }
 *   CanvasRenderer renderer = new CanvasRenderer(fonts);
 * }</pre>
 */
public final class CanvasRenderer {

    /** Unscaled logical pixels between the canvas edge and the first character. */
    public static final int H_PADDING = 2;
    public static final int V_PADDING = 2;

    /** Unscaled logical pixel height per line of text (matches the TypeScript constant). */
    public static final int LINE_HEIGHT = 10;

    private final MinecraftFont fontManager;
    private final MinecraftTextRenderer textRenderer = new MinecraftTextRenderer();

    // -------------------------------------------------------------------------
    // Construction
    // -------------------------------------------------------------------------

    public CanvasRenderer(MinecraftFont fontManager) {
        this.fontManager = fontManager;
    }

    /**
     * Creates a renderer pre-loaded with a programmatic fallback font registered
     * under the name {@code "default"}.  No atlas file required.
     */
    public static CanvasRenderer withFallbackFont() {
        MinecraftFont fonts = new MinecraftFont();
        FontSet fallback = ProgrammaticFontBuilder.buildFallbackFont("default");
        // Register the built font directly in the registry via a thin wrapper
        fonts.registerFontSet(fallback);
        return new CanvasRenderer(fonts);
    }

    // -------------------------------------------------------------------------
    // Core drawing API
    // -------------------------------------------------------------------------

    /**
     * Draws §-formatted Minecraft text at absolute pixel coordinates.
     * {@code x} / {@code y} are in output (scaled) pixels; the renderer
     * internally translates and scales so callers do not need to pre-scale
     * coordinates.
     */
    public void drawMinecraftText(Graphics2D g, String text, int x, int y,
                                  float scale, String fontName) {
        textRenderer.drawText(g, text, x, y, scale, requireFont(fontName));
    }

    /** Returns the unscaled width (in logical Minecraft pixels) of the formatted text. */
    public TextMeasurement measureMinecraftText(String text, String fontName) {
        return textRenderer.measure(text, requireFont(fontName));
    }

    /**
     * Generates a transparent or black-background canvas containing one or more
     * lines of §-formatted Minecraft text, returned as a {@link BufferedImage}.
     */
    public BufferedImage generateMinecraftImage(List<String> lines, boolean transparentBg,
                                                float scale, String fontName) {
        FontSet font = requireFont(fontName);
        List<String> safeLines = lines.isEmpty() ? List.of("") : lines;

        int maxLogicalWidth = safeLines.stream()
            .mapToInt(l -> textRenderer.measure(l, font).width())
            .max()
            .orElse(0);

        int width  = Math.max(1, Math.round((maxLogicalWidth + H_PADDING * 2) * scale));
        int height = Math.max(1, Math.round(
            (safeLines.size() * LINE_HEIGHT + V_PADDING * 2) * scale));

        BufferedImage canvas = ImageUtils.createCanvas(width, height);
        Graphics2D g = canvas.createGraphics();
        try {
            RenderUtils.configurePixelPerfect(g);

            if (!transparentBg) {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, width, height);
            }

            for (int i = 0; i < safeLines.size(); i++) {
                // Padding and line offset are defined in unscaled pixels,
                // so multiply by scale to get output pixel coordinates.
                int x = Math.round(H_PADDING * scale);
                int y = Math.round((V_PADDING + i * LINE_HEIGHT) * scale);
                textRenderer.drawText(g, safeLines.get(i), x, y, scale, font);
            }
        } finally {
            g.dispose();
        }

        return canvas;
    }

    /** Same as {@link #generateMinecraftImage} but returns PNG-encoded bytes. */
    public byte[] generateMinecraftImageBytes(List<String> lines, boolean transparentBg,
                                              float scale, String fontName) throws IOException {
        return ImageUtils.toPngBytes(generateMinecraftImage(lines, transparentBg, scale, fontName));
    }

    // -------------------------------------------------------------------------
    // Shape helpers
    // -------------------------------------------------------------------------

    /**
     * Draws a semi-transparent rounded rectangle.
     * Coordinates are normalised: the smaller of each pair becomes the origin.
     */
    public void drawRoundedRectangle(Graphics2D g, int x1, int y1, int x2, int y2,
                                     int radius, Color color) {
        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);
        int w = Math.abs(x2 - x1);
        int h = Math.abs(y2 - y1);
        RenderUtils.drawRoundedRect(g, x, y, w, h, radius, color);
    }

    public void drawProgressBar(Graphics2D g, int x, int y, int width, int height,
                                float progress, Color fillColor, Color bgColor, int radius) {
        RenderUtils.drawProgressBar(g, x, y, width, height, progress, fillColor, bgColor, radius);
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public MinecraftFont getFontManager() { return fontManager; }

    // -------------------------------------------------------------------------
    // Internal
    // -------------------------------------------------------------------------

    private FontSet requireFont(String name) {
        FontSet font = fontManager.get(name);
        if (font == null) {
            throw new IllegalArgumentException(
                "Font '" + name + "' is not loaded. Call MinecraftFont.loadFromAtlas() " +
                "or CanvasRenderer.withFallbackFont() first.");
        }
        return font;
    }
}
