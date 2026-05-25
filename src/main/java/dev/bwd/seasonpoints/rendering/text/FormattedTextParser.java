package dev.bwd.seasonpoints.rendering.text;

import dev.bwd.seasonpoints.rendering.MinecraftColors;
import dev.bwd.seasonpoints.rendering.models.StyledSegment;
import dev.bwd.seasonpoints.rendering.models.TextStyle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stateless parser — safe to share across threads.
 * Converts a §-formatted Minecraft string into a flat list of
 * (character, style) pairs, one entry per visible character.
 */
public final class FormattedTextParser {

    private static final char SECTION = '§';

    public List<StyledSegment> parse(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }

        List<StyledSegment> result = new ArrayList<>(text.length());
        TextStyle current = TextStyle.DEFAULT;
        int i = 0;

        while (i < text.length()) {
            char c = text.charAt(i);

            if (c == SECTION && i + 1 < text.length()) {
                current = applyCode(current, text.charAt(i + 1));
                i += 2;
                continue;
            }

            result.add(new StyledSegment(c, current));
            i++;
        }

        return result;
    }

    private TextStyle applyCode(TextStyle current, char code) {
        char lower = Character.toLowerCase(code);

        if (MinecraftColors.isColorCode(lower)) {
            return current.withColor(MinecraftColors.fromCode(lower));
        }

        return switch (lower) {
            case 'l' -> current.withBold(true);
            case 'o' -> current.withItalic(true);
            case 'n' -> current.withUnderline(true);
            case 'm' -> current.withStrikethrough(true);
            case 'r' -> TextStyle.DEFAULT;
            default  -> current;
        };
    }
}
