package dev.bwd.seasonpoints.rendering.models;

import java.util.concurrent.ConcurrentHashMap;

public final class FontSet {

    private final String name;
    private final ConcurrentHashMap<Character, BitmapGlyph> glyphs;
    private final BitmapGlyph fallbackGlyph;

    public FontSet(String name, BitmapGlyph fallbackGlyph) {
        this.name = name;
        this.glyphs = new ConcurrentHashMap<>(256);
        this.fallbackGlyph = fallbackGlyph;
    }

    public void registerGlyph(char c, BitmapGlyph glyph) {
        glyphs.put(c, glyph);
    }

    public BitmapGlyph getGlyph(char c) {
        return glyphs.getOrDefault(c, fallbackGlyph);
    }

    public String getName() { return name; }
}
