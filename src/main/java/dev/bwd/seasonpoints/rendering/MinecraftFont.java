package dev.bwd.seasonpoints.rendering;

import dev.bwd.seasonpoints.rendering.models.BitmapGlyph;
import dev.bwd.seasonpoints.rendering.models.FontSet;
import dev.bwd.seasonpoints.rendering.utils.ImageUtils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads Minecraft-format font atlases and maintains a named registry of FontSets.
 *
 * Atlas format expected: a PNG arranged as a 16×16 grid of character cells,
 * covering Unicode code points U+0000–U+00FF in row-major order.
 * Cell width  = atlasWidth  / 16
 * Cell height = atlasHeight / 16
 * Advance per character is computed from the rightmost non-transparent pixel.
 *
 * Thread-safe: the internal registry uses ConcurrentHashMap; individual loads
 * should be performed during plugin startup (single-thread).
 */
public final class MinecraftFont {

    private static final int ATLAS_COLS        = 16;
    private static final int ATLAS_ROWS        = 16;
    private static final int SPACE_ADVANCE     = 4;
    private static final int DEFAULT_BOLD_OFF  = 1;
    private static final int DEFAULT_SHADOW_OFF = 1;

    private final ConcurrentHashMap<String, FontSet> registry = new ConcurrentHashMap<>();

    // -------------------------------------------------------------------------
    // Loading
    // -------------------------------------------------------------------------

    public FontSet loadFromAtlas(String name, InputStream atlasStream) throws IOException {
        BufferedImage atlas = ImageUtils.loadFromStream(atlasStream);
        int cellW = atlas.getWidth()  / ATLAS_COLS;
        int cellH = atlas.getHeight() / ATLAS_ROWS;

        BitmapGlyph fallback = buildFallbackGlyph(cellW, cellH);
        FontSet fontSet = new FontSet(name, fallback);

        for (int code = 0; code < 256; code++) {
            char c = (char) code;
            if (c == ' ') {
                fontSet.registerGlyph(c, BitmapGlyph.empty(SPACE_ADVANCE));
                continue;
            }

            int col  = code % ATLAS_COLS;
            int row  = code / ATLAS_COLS;
            int srcX = col * cellW;
            int srcY = row * cellH;

            BufferedImage cell    = atlas.getSubimage(srcX, srcY, cellW, cellH);
            int           advance = computeAdvance(cell);

            if (advance == 0) {
                fontSet.registerGlyph(c, BitmapGlyph.empty(SPACE_ADVANCE));
            } else {
                // Trim to actual glyph width to keep memory usage down
                int trimW = Math.min(advance, cellW);
                BufferedImage trimmed = copyRegion(cell, 0, 0, trimW, cellH);
                fontSet.registerGlyph(c, new BitmapGlyph(trimmed, advance,
                    DEFAULT_BOLD_OFF, DEFAULT_SHADOW_OFF));
            }
        }

        registry.put(name, fontSet);
        return fontSet;
    }

    /** Convenience overload — resolves the stream from the caller's ClassLoader. */
    public FontSet loadFromClasspath(String name, String resourcePath,
                                     ClassLoader classLoader) throws IOException {
        try (InputStream is = classLoader.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found on classpath: " + resourcePath);
            }
            return loadFromAtlas(name, is);
        }
    }

    // -------------------------------------------------------------------------
    // Registry access
    // -------------------------------------------------------------------------

    public FontSet get(String name) {
        return registry.get(name);
    }

    public boolean isLoaded(String name) {
        return registry.containsKey(name);
    }

    /** Registers an already-constructed FontSet (e.g. from ProgrammaticFontBuilder). */
    public void registerFontSet(FontSet fontSet) {
        registry.put(fontSet.getName(), fontSet);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Scan right-to-left for the last column containing a non-transparent pixel. */
    private int computeAdvance(BufferedImage cell) {
        int width  = cell.getWidth();
        int height = cell.getHeight();

        for (int x = width - 1; x >= 0; x--) {
            for (int y = 0; y < height; y++) {
                if (((cell.getRGB(x, y) >> 24) & 0xFF) > 0) {
                    return x + 2; // +1 inclusive, +1 inter-character spacing
                }
            }
        }
        return 0;
    }

    /** Defensive copy of a sub-region (avoids shared raster with the atlas). */
    private BufferedImage copyRegion(BufferedImage src, int x, int y, int w, int h) {
        BufferedImage copy = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = copy.createGraphics();
        try {
            g.drawImage(src, 0, 0, w, h, x, y, x + w, y + h, null);
        } finally {
            g.dispose();
        }
        return copy;
    }

    /** Simple border box used when a cell cannot be decoded. */
    private BitmapGlyph buildFallbackGlyph(int cellW, int cellH) {
        int w = Math.max(cellW, 6);
        int h = Math.max(cellH, 8);
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setColor(Color.WHITE);
            g.drawRect(1, 1, w - 3, h - 3);
        } finally {
            g.dispose();
        }
        return new BitmapGlyph(img, w + 1, DEFAULT_BOLD_OFF, DEFAULT_SHADOW_OFF);
    }
}
