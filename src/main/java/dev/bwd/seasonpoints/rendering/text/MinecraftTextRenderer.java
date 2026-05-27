package dev.bwd.seasonpoints.rendering.text;

import dev.bwd.seasonpoints.rendering.models.BitmapGlyph;
import dev.bwd.seasonpoints.rendering.models.FontSet;
import dev.bwd.seasonpoints.rendering.models.StyledSegment;
import dev.bwd.seasonpoints.rendering.models.TextStyle;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

/**
 * Stateless text renderer — safe to share across threads.
 *
 * Coordinate contract (mirrors the TypeScript original):
 *   x / y are absolute pixel positions on the parent canvas.
 *   The renderer translates to (x, y) first, then applies scale,
 *   so callers work in unscaled "Minecraft pixel" units when
 *   passing cursor positions.
 */
public final class MinecraftTextRenderer {

    private static final int LINE_HEIGHT = 10;

    private final FormattedTextParser parser = new FormattedTextParser();

    /**
     * Draws a §-formatted Minecraft string onto {@code g}.
     * Uses {@code g.create()} so transforms and hints are fully isolated.
     */
    public void drawText(Graphics2D g, String text, int x, int y, float scale, FontSet fontSet) {
        List<StyledSegment> segments = parser.parse(text);
        if (segments.isEmpty()) return;

        Graphics2D ctx = (Graphics2D) g.create();
        try {
            ctx.translate(x, y);
            ctx.scale(scale, scale);
            ctx.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            ctx.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);

            int cursorX = 0;

            for (StyledSegment seg : segments) {
                BitmapGlyph glyph = fontSet.getGlyph(seg.character());
                TextStyle style = seg.style();

                int boldOffset   = glyph.getBoldOffset();
                int shadowOffset = glyph.getShadowOffset();

                if (!glyph.isEmpty()) {
                    // Shadow pass — drawn at (+shadowOffset, +shadowOffset)
                    glyph.render(ctx, cursorX + shadowOffset, shadowOffset,
                        style.color().shadow());
                    if (style.bold()) {
                        glyph.render(ctx, cursorX + shadowOffset + boldOffset, shadowOffset,
                            style.color().shadow());
                    }

                    // Main pass
                    glyph.render(ctx, cursorX, 0, style.color().text());
                    if (style.bold()) {
                        glyph.render(ctx, cursorX + boldOffset, 0, style.color().text());
                    }
                }

                cursorX += glyph.getAdvance() + (style.bold() ? boldOffset : 0);
            }
        } finally {
            ctx.dispose();
        }
    }

    /** Returns the pixel width (unscaled) needed to render {@code text}. */
    public TextMeasurement measure(String text, FontSet fontSet) {
        List<StyledSegment> segments = parser.parse(text);
        int width = 0;
        for (StyledSegment seg : segments) {
            BitmapGlyph glyph = fontSet.getGlyph(seg.character());
            width += glyph.getAdvance() + (seg.style().bold() ? glyph.getBoldOffset() : 0);
        }
        return new TextMeasurement(width, LINE_HEIGHT);
    }

    public int getLineHeight() { return LINE_HEIGHT; }
}
