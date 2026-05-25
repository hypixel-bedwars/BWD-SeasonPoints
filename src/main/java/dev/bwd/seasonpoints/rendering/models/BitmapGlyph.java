package dev.bwd.seasonpoints.rendering.models;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;

public final class BitmapGlyph implements Glyph {

    private final BufferedImage mask;
    private final int advance;
    private final int boldOffset;
    private final int shadowOffset;
    private final boolean empty;

    // Lazily caches one colorized image per Color.getRGB() key.
    // Minecraft's 16 text + 16 shadow colors means this stays very small per glyph.
    private final ConcurrentHashMap<Integer, BufferedImage> colorCache = new ConcurrentHashMap<>();

    public BitmapGlyph(BufferedImage mask, int advance, int boldOffset, int shadowOffset) {
        this.mask = mask;
        this.advance = advance;
        this.boldOffset = boldOffset;
        this.shadowOffset = shadowOffset;
        this.empty = (mask == null);
    }

    public static BitmapGlyph empty(int advance) {
        return new BitmapGlyph(null, advance, 1, 1);
    }

    @Override
    public int getAdvance() { return advance; }

    @Override
    public int getBoldOffset() { return boldOffset; }

    @Override
    public int getShadowOffset() { return shadowOffset; }

    @Override
    public boolean isEmpty() { return empty; }

    @Override
    public void render(Graphics2D g, int x, int y, Color color) {
        if (empty || mask == null) return;
        BufferedImage colored = colorCache.computeIfAbsent(color.getRGB(), k -> colorize(color));
        g.drawImage(colored, x, y, null);
    }

    // Fill target color, then clip to glyph alpha via DstIn composite.
    // Result: every pixel carries (color.rgb, glyph.alpha).
    private BufferedImage colorize(Color color) {
        int w = mask.getWidth();
        int h = mask.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D cg = out.createGraphics();
        try {
            cg.setColor(color);
            cg.fillRect(0, 0, w, h);
            cg.setComposite(AlphaComposite.DstIn);
            cg.drawImage(mask, 0, 0, null);
        } finally {
            cg.dispose();
        }
        return out;
    }
}
