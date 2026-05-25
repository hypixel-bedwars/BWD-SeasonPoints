package dev.bwd.seasonpoints.rendering.models;

import dev.bwd.seasonpoints.rendering.MinecraftColors;

public record TextStyle(
    MinecraftColors.ColorPair color,
    boolean bold,
    boolean italic,
    boolean underline,
    boolean strikethrough
) {

    public static final TextStyle DEFAULT = new TextStyle(
        MinecraftColors.DEFAULT,
        false, false, false, false
    );

    // Applying a color code resets all active formatting (Minecraft spec)
    public TextStyle withColor(MinecraftColors.ColorPair newColor) {
        return new TextStyle(newColor, false, false, false, false);
    }

    public TextStyle withBold(boolean bold) {
        return new TextStyle(color, bold, italic, underline, strikethrough);
    }

    public TextStyle withItalic(boolean italic) {
        return new TextStyle(color, bold, italic, underline, strikethrough);
    }

    public TextStyle withUnderline(boolean underline) {
        return new TextStyle(color, bold, italic, underline, strikethrough);
    }

    public TextStyle withStrikethrough(boolean strikethrough) {
        return new TextStyle(color, bold, italic, underline, strikethrough);
    }

    public TextStyle reset() {
        return DEFAULT;
    }
}
