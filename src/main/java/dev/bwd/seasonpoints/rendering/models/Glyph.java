package dev.bwd.seasonpoints.rendering.models;

import java.awt.Color;
import java.awt.Graphics2D;

public interface Glyph {

    int getAdvance();

    int getBoldOffset();

    int getShadowOffset();

    boolean isEmpty();

    void render(Graphics2D g, int x, int y, Color color);
}
