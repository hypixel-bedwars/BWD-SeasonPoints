package dev.bwd.seasonpoints.rendering;

import dev.bwd.seasonpoints.rendering.models.BitmapGlyph;
import dev.bwd.seasonpoints.rendering.models.FontSet;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Generates a FontSet from Java's built-in MONOSPACED font.
 * Requires no external atlas file — works out-of-the-box on any JDK.
 *
 * The output will not be pixel-identical to the Minecraft font atlas,
 * but it produces clean, aliasing-free bitmap glyphs that follow the
 * same rendering pipeline.  Drop in a real ascii.png via MinecraftFont
 * whenever you want true Minecraft aesthetics.
 *
 * Usage:
 *   FontSet fallback = ProgrammaticFontBuilder.buildFallbackFont("default");
 *   // register it in a MinecraftFont instance or use directly
 */
public final class ProgrammaticFontBuilder {

    private static final int CELL_SIZE  = 8;
    private static final int FONT_SIZE  = 7;
    private static final int BOLD_OFF   = 1;
    private static final int SHADOW_OFF = 1;

    private ProgrammaticFontBuilder() {}

    /**
     * Builds a FontSet covering printable ASCII (0x20–0x7E) using the
     * platform's MONOSPACED font rendered at 7 pt onto 8×8 cells.
     */
    public static FontSet buildFallbackFont(String name) {
        Font javaFont = new Font(Font.MONOSPACED, Font.PLAIN, FONT_SIZE);

        // Measure the font to centre glyphs vertically inside the cell
        int baseline = measureBaseline(javaFont);

        BitmapGlyph fallback = renderGlyph('?', javaFont, baseline);
        FontSet fontSet = new FontSet(name, fallback);

        fontSet.registerGlyph(' ', BitmapGlyph.empty(4));

        for (int code = 0x21; code <= 0x7E; code++) {
            char c = (char) code;
            fontSet.registerGlyph(c, renderGlyph(c, javaFont, baseline));
        }

        return fontSet;
    }

    // -------------------------------------------------------------------------

    private static BitmapGlyph renderGlyph(char c, Font font, int baseline) {
        BufferedImage img = new BufferedImage(CELL_SIZE, CELL_SIZE,
            BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            g.setFont(font);
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(c), 0, baseline);
        } finally {
            g.dispose();
        }

        int advance = computeAdvance(img);
        return new BitmapGlyph(img, Math.max(advance, 2), BOLD_OFF, SHADOW_OFF);
    }

    /** Determine the pixel row that acts as the text baseline inside a CELL_SIZE box. */
    private static int measureBaseline(Font font) {
        BufferedImage probe = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = probe.createGraphics();
        try {
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            // Vertically centre within the cell
            int textHeight = fm.getAscent() + fm.getDescent();
            int topPad = Math.max(0, (CELL_SIZE - textHeight) / 2);
            return topPad + fm.getAscent();
        } finally {
            g.dispose();
        }
    }

    /** Rightmost non-transparent column, +2 for inter-character spacing. */
    private static int computeAdvance(BufferedImage img) {
        for (int x = img.getWidth() - 1; x >= 0; x--) {
            for (int y = 0; y < img.getHeight(); y++) {
                if (((img.getRGB(x, y) >> 24) & 0xFF) > 0) {
                    return x + 2;
                }
            }
        }
        return 4;
    }
}
