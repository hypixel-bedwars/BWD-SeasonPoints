package dev.bwd.seasonpoints.rendering;

import java.awt.Color;
import java.util.Map;

public final class MinecraftColors {

    public record ColorPair(Color text, Color shadow) {}

    // Shadow channel = text channel / 4  (Minecraft's spec)
    private static final Map<Character, ColorPair> COLOR_MAP = Map.ofEntries(
        Map.entry('0', pair(0x000000, 0x000000)),
        Map.entry('1', pair(0x0000AA, 0x00002A)),
        Map.entry('2', pair(0x00AA00, 0x002A00)),
        Map.entry('3', pair(0x00AAAA, 0x002A2A)),
        Map.entry('4', pair(0xAA0000, 0x2A0000)),
        Map.entry('5', pair(0xAA00AA, 0x2A002A)),
        Map.entry('6', pair(0xFFAA00, 0x3F2A00)),
        Map.entry('7', pair(0xAAAAAA, 0x2A2A2A)),
        Map.entry('8', pair(0x555555, 0x151515)),
        Map.entry('9', pair(0x5555FF, 0x15153F)),
        Map.entry('a', pair(0x55FF55, 0x153F15)),
        Map.entry('b', pair(0x55FFFF, 0x153F3F)),
        Map.entry('c', pair(0xFF5555, 0x3F1515)),
        Map.entry('d', pair(0xFF55FF, 0x3F153F)),
        Map.entry('e', pair(0xFFFF55, 0x3F3F15)),
        Map.entry('f', pair(0xFFFFFF, 0x3F3F3F))
    );

    public static final ColorPair DEFAULT = COLOR_MAP.get('f');

    public static ColorPair fromCode(char code) {
        return COLOR_MAP.getOrDefault(Character.toLowerCase(code), DEFAULT);
    }

    public static boolean isColorCode(char code) {
        return COLOR_MAP.containsKey(Character.toLowerCase(code));
    }

    private static ColorPair pair(int textRgb, int shadowRgb) {
        return new ColorPair(new Color(textRgb), new Color(shadowRgb));
    }

    private MinecraftColors() {}
}
