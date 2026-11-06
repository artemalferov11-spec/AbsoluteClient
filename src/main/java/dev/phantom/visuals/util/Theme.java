package dev.phantom.visuals.util;

import dev.phantom.visuals.module.ModuleManager;
import net.minecraft.class_3532;

public final class Theme {
    private Theme() {}

    /** Цвет акцента (ARGB). shift сдвигает оттенок — получается градиент. */
    public static int accent(float shift) {
        float h;
        if (ModuleManager.THEME.rainbow.get()) {
            h = (System.currentTimeMillis() % 8000L) / 8000f + shift;
        } else {
            h = ModuleManager.THEME.hue.getF() / 360f + shift * 0.5f;
        }
        h = h - (float) Math.floor(h);
        return 0xFF000000 | class_3532.method_15369(h, 0.62f, 1.0f);
    }

    public static int withAlpha(int color, int alpha) {
        alpha = Math.max(0, Math.min(255, alpha));
        return (alpha << 24) | (color & 0xFFFFFF);
    }

    public static int lerp(int a, int b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int aa = (a >>> 24) & 255, ar = (a >> 16) & 255, ag = (a >> 8) & 255, ab = a & 255;
        int ba = (b >>> 24) & 255, br = (b >> 16) & 255, bg = (b >> 8) & 255, bb = b & 255;
        return ((int) (aa + (ba - aa) * t) << 24) | ((int) (ar + (br - ar) * t) << 16)
                | ((int) (ag + (bg - ag) * t) << 8) | (int) (ab + (bb - ab) * t);
    }
}
