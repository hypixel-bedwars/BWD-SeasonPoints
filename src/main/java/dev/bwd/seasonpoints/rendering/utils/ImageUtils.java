package dev.bwd.seasonpoints.rendering.utils;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ImageUtils {

    private ImageUtils() {}

    public static BufferedImage createCanvas(int width, int height) {
        return new BufferedImage(
            Math.max(1, width),
            Math.max(1, height),
            BufferedImage.TYPE_INT_ARGB
        );
    }

    public static byte[] toPngBytes(BufferedImage image) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", out);
            return out.toByteArray();
        }
    }

    public static void savePng(BufferedImage image, Path path) throws IOException {
        Files.createDirectories(path.getParent());
        ImageIO.write(image, "PNG", path.toFile());
    }

    public static BufferedImage loadFromStream(InputStream stream) throws IOException {
        BufferedImage img = ImageIO.read(stream);
        if (img == null) {
            throw new IOException("Failed to decode image — unsupported format or empty stream");
        }
        return img;
    }

    /** Nearest-neighbor scale — preserves pixel-art sharpness at integer multiples. */
    public static BufferedImage scalePixelPerfect(BufferedImage source, int targetW, int targetH) {
        BufferedImage out = createCanvas(targetW, targetH);
        Graphics2D g = out.createGraphics();
        try {
            RenderUtils.configurePixelPerfect(g);
            g.drawImage(source, 0, 0, targetW, targetH, null);
        } finally {
            g.dispose();
        }
        return out;
    }
}
