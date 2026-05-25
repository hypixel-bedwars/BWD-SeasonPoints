package dev.bwd.seasonpoints.rendering.utils;

import java.awt.Color;

public final class ColorUtils {

    private ColorUtils() {}

    public static Color withAlpha(Color base, int alpha) {
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha);
    }

    public static Color fromHex(String hex) {
        String normalized = hex.startsWith("#") ? hex : "#" + hex;
        return Color.decode(normalized);
    }

    public static Color darken(Color color, float factor) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        hsb[2] = Math.max(0f, hsb[2] * (1f - factor));
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

    public static Color blend(Color a, Color b, float ratioA) {
        float ratioB = 1f - ratioA;
        int r = Math.round(a.getRed()   * ratioA + b.getRed()   * ratioB);
        int g = Math.round(a.getGreen() * ratioA + b.getGreen() * ratioB);
        int bl = Math.round(a.getBlue() * ratioA + b.getBlue()  * ratioB);
        return new Color(r, g, bl);
    }
}
