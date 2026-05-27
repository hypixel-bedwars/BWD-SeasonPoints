package dev.bwd.seasonpoints.rendering.components;

import dev.bwd.seasonpoints.rendering.CanvasRenderer;
import dev.bwd.seasonpoints.rendering.utils.ImageUtils;
import dev.bwd.seasonpoints.rendering.utils.RenderUtils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Renders a Minecraft-style player profile card as a {@link BufferedImage}.
 *
 * Layout (logical units, scaled by SCALE = 3):
 * ┌──────────────────────────────────────────────┐
 * │  [avatar]  §fPlayerName                      │
 * │            §7Rank Title                      │
 * │            §e12,345 §7pts  §8(45,678 total)  │
 * │  [════════════════════░░░░░░░░░░░░░░░░░░░░]  │
 * └──────────────────────────────────────────────┘
 *
 * Output dimensions: 600 × 180 px (200 × 60 logical × scale 3).
 */
public final class PlayerCardComponent {

    /** Immutable data bag for a single player card render. */
    public record PlayerCardData(
        String playerName,
        String rankTitle,
        int seasonPoints,
        int totalPoints,
        float seasonProgress,
        BufferedImage avatar
    ) {
        /** Convenience constructor without avatar. */
        public PlayerCardData(String playerName, String rankTitle,
                              int seasonPoints, int totalPoints, float seasonProgress) {
            this(playerName, rankTitle, seasonPoints, totalPoints, seasonProgress, null);
        }
    }

    // ---- Layout constants (unscaled logical pixels) --------------------------
    private static final int CARD_W       = 200;
    private static final int CARD_H       = 60;
    private static final int PAD          = 5;
    private static final int AVATAR_SIZE  = 16;
    private static final int LINE_GAP     = 11;
    private static final int BAR_H        = 4;
    private static final int BAR_MARGIN   = 5;
    private static final int CORNER_R     = 4;
    private static final int SCALE        = 3;

    // ---- Palette -------------------------------------------------------------
    private static final Color BG        = new Color(0, 0, 0, 160);

    private final CanvasRenderer renderer;
    private final String fontName;

    public PlayerCardComponent(CanvasRenderer renderer, String fontName) {
        this.renderer = renderer;
        this.fontName = fontName;
    }

    // -------------------------------------------------------------------------

    public BufferedImage render(PlayerCardData data) {
        int outW = CARD_W * SCALE;
        int outH = CARD_H * SCALE;

        BufferedImage canvas = ImageUtils.createCanvas(outW, outH);
        Graphics2D g = canvas.createGraphics();
        try {
            RenderUtils.configurePixelPerfect(g);

            // Background panel
            renderer.drawRoundedRectangle(g, 0, 0, outW, outH, CORNER_R * SCALE, BG);

            // Optional avatar
            int textOffsetX = PAD;
            if (data.avatar() != null) {
                int avatarPx = AVATAR_SIZE * SCALE;
                int avatarY  = ((CARD_H - AVATAR_SIZE) / 2) * SCALE;
                BufferedImage scaledHead = ImageUtils.scalePixelPerfect(
                    data.avatar(), avatarPx, avatarPx);
                g.drawImage(scaledHead, PAD * SCALE, avatarY, null);
                textOffsetX += AVATAR_SIZE + PAD;
            }

            int tx = textOffsetX * SCALE;

            // Player name
            int nameY = PAD * SCALE;
            renderer.drawMinecraftText(g, "§f" + data.playerName(), tx, nameY, SCALE, fontName);

            // Rank / title
            int rankY = nameY + LINE_GAP * SCALE;
            renderer.drawMinecraftText(g, "§7" + data.rankTitle(), tx, rankY, SCALE, fontName);

            // Points line
            int pointsY = rankY + LINE_GAP * SCALE;
            String pointsLine = "§e" + String.format("%,d", data.seasonPoints())
                + " §7pts  §8(" + String.format("%,d", data.totalPoints()) + " total)";
            renderer.drawMinecraftText(g, pointsLine, tx, pointsY, SCALE, fontName);

            // Progress bar — pinned to the bottom with BAR_MARGIN breathing room
            int barX = PAD * SCALE;
            int barY = outH - (BAR_H + BAR_MARGIN) * SCALE;
            int barW = outW - barX * 2;
            int barH = BAR_H * SCALE;

            ProgressBarComponent.builder(barX, barY, barW, barH)
                .progress(data.seasonProgress())
                .fillColor(new Color(85, 255, 85))
                .bgColor(new Color(30, 30, 30, 200))
                .radius(2)
                .build()
                .draw(g);

        } finally {
            g.dispose();
        }

        return canvas;
    }

    public byte[] renderBytes(PlayerCardData data) throws IOException {
        return ImageUtils.toPngBytes(render(data));
    }
}
