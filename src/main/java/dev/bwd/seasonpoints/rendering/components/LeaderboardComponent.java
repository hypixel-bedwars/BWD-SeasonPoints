package dev.bwd.seasonpoints.rendering.components;

import dev.bwd.seasonpoints.models.LeaderboardEntry;
import dev.bwd.seasonpoints.rendering.CanvasRenderer;
import dev.bwd.seasonpoints.rendering.utils.ImageUtils;
import dev.bwd.seasonpoints.rendering.utils.RenderUtils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * Renders a Minecraft-style leaderboard image.
 *
 * Layout:
 * ┌──────────────────────────┐
 * │  §e Season 1 Leaderboard │  ← header
 * │  §6 1. §fPlayerA §e1,234 │  ← gold   (1st)
 * │  §7 2. §fPlayerB §e  987 │  ← silver (2nd)
 * │  §c 3. §fPlayerC §e  765 │  ← bronze (3rd)
 * │  §7 4. §fPlayerD §e  432 │
 * │  ...                     │
 * └──────────────────────────┘
 */
public final class LeaderboardComponent {

    // ---- Layout constants (logical pixels) -----------------------------------
    private static final int SCALE      = 2;
    private static final int CARD_W     = 170;
    private static final int ROW_H      = 12;
    private static final int PAD        = 5;
    private static final int CORNER_R   = 4;

    // ---- Palette -------------------------------------------------------------
    private static final Color BG = new Color(0, 0, 0, 180);

    /** §-code for each rank position (index 0 = 1st place). */
    private static final String[] RANK_COLORS = { "§6", "§7", "§c" };

    private final CanvasRenderer renderer;
    private final String fontName;

    public LeaderboardComponent(CanvasRenderer renderer, String fontName) {
        this.renderer = renderer;
        this.fontName = fontName;
    }

    // -------------------------------------------------------------------------

    public BufferedImage render(List<LeaderboardEntry> entries, String seasonTitle) {
        int rows  = entries.size() + 1; // +1 for the header
        int outW  = CARD_W * SCALE;
        int outH  = (rows * ROW_H + PAD * 2) * SCALE;

        BufferedImage canvas = ImageUtils.createCanvas(outW, outH);
        Graphics2D g = canvas.createGraphics();
        try {
            RenderUtils.configurePixelPerfect(g);

            // Background panel
            renderer.drawRoundedRectangle(g, 0, 0, outW, outH, CORNER_R * SCALE, BG);

            int tx = PAD * SCALE;
            int y  = PAD * SCALE;

            // Header
            renderer.drawMinecraftText(g, "§e" + seasonTitle, tx, y, SCALE, fontName);
            y += ROW_H * SCALE;

            // Rows
            for (int i = 0; i < entries.size(); i++) {
                LeaderboardEntry entry = entries.get(i);
                String rankColor = i < RANK_COLORS.length ? RANK_COLORS[i] : "§7";
                String line = rankColor + String.format("%2d. §f%-14s §e%,d pts",
                    i + 1, entry.username(), entry.points());
                renderer.drawMinecraftText(g, line, tx, y, SCALE, fontName);
                y += ROW_H * SCALE;
            }
        } finally {
            g.dispose();
        }

        return canvas;
    }

    public byte[] renderBytes(List<LeaderboardEntry> entries, String seasonTitle) throws IOException {
        return ImageUtils.toPngBytes(render(entries, seasonTitle));
    }
}
