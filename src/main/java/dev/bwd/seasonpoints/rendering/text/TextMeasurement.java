package dev.bwd.seasonpoints.rendering.text;

public record TextMeasurement(int width, int height) {

    public int scaledWidth(float scale) {
        return Math.round(width * scale);
    }

    public int scaledHeight(float scale) {
        return Math.round(height * scale);
    }
}
