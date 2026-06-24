package scalinghealthissues.util;

import net.minecraft.util.text.TextFormatting;

public abstract class HexToColorMap {

    private static final TextFormatting[] COLORS = {
            TextFormatting.BLACK,
            TextFormatting.DARK_BLUE,
            TextFormatting.DARK_GREEN,
            TextFormatting.DARK_AQUA,
            TextFormatting.DARK_RED,
            TextFormatting.DARK_PURPLE,
            TextFormatting.GOLD,
            TextFormatting.GRAY,
            TextFormatting.DARK_GRAY,
            TextFormatting.BLUE,
            TextFormatting.GREEN,
            TextFormatting.AQUA,
            TextFormatting.RED,
            TextFormatting.LIGHT_PURPLE,
            TextFormatting.YELLOW,
            TextFormatting.WHITE
    };

    private static final int[] RGB = {
            0x000000,
            0x0000AA,
            0x00AA00,
            0x00AAAA,
            0xAA0000,
            0xAA00AA,
            0xFFAA00,
            0xAAAAAA,
            0x555555,
            0x5555FF,
            0x55FF55,
            0x55FFFF,
            0xFF5555,
            0xFF55FF,
            0xFFFF55,
            0xFFFFFF
    };

    public static TextFormatting nearestColor(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        TextFormatting best = TextFormatting.WHITE;
        double bestDistance = Double.MAX_VALUE;

        for (int i = 0; i < RGB.length; i++) {
            int pr = (RGB[i] >> 16) & 0xFF;
            int pg = (RGB[i] >> 8) & 0xFF;
            int pb = RGB[i] & 0xFF;

            double dist =
                    Math.pow(r - pr, 2) +
                            Math.pow(g - pg, 2) +
                            Math.pow(b - pb, 2);

            if (dist < bestDistance) {
                bestDistance = dist;
                best = COLORS[i];
            }
        }

        return best;
    }
}
