package dev.bwd.seasonpoints.rendering.components;

import dev.bwd.seasonpoints.rendering.utils.RenderUtils;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Immutable progress-bar component built via a fluent builder.
 *
 * Usage:
 * <pre>{@code
 *   ProgressBarComponent.builder(x, y, width, height)
 *       .progress(0.65f)
 *       .fillColor(new Color(85, 255, 85))
 *       .bgColor(new Color(40, 40, 40, 200))
 *       .radius(3)
 *       .build()
 *       .draw(g);
 * }</pre>
 */
public final class ProgressBarComponent {

    public static final Color DEFAULT_BG   = new Color(40, 40, 40, 200);
    public static final Color DEFAULT_FILL = new Color(85, 255, 85);

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final float progress;
    private final Color fillColor;
    private final Color bgColor;
    private final int radius;

    private ProgressBarComponent(Builder b) {
        this.x         = b.x;
        this.y         = b.y;
        this.width     = b.width;
        this.height    = b.height;
        this.progress  = b.progress;
        this.fillColor = b.fillColor;
        this.bgColor   = b.bgColor;
        this.radius    = b.radius;
    }

    public void draw(Graphics2D g) {
        RenderUtils.drawProgressBar(g, x, y, width, height, progress, fillColor, bgColor, radius);
    }

    public static Builder builder(int x, int y, int width, int height) {
        return new Builder(x, y, width, height);
    }

    public static final class Builder {
        private final int x, y, width, height;
        private float progress  = 0f;
        private Color fillColor = DEFAULT_FILL;
        private Color bgColor   = DEFAULT_BG;
        private int   radius    = 2;

        private Builder(int x, int y, int width, int height) {
            this.x = x; this.y = y; this.width = width; this.height = height;
        }

        public Builder progress(float progress)    { this.progress  = progress;  return this; }
        public Builder fillColor(Color fillColor)  { this.fillColor = fillColor; return this; }
        public Builder bgColor(Color bgColor)      { this.bgColor   = bgColor;   return this; }
        public Builder radius(int radius)          { this.radius    = radius;    return this; }

        public ProgressBarComponent build() { return new ProgressBarComponent(this); }
    }
}
