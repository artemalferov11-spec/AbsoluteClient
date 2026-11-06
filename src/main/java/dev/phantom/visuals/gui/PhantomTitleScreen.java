package dev.phantom.visuals.gui;

import dev.phantom.visuals.util.Nick;
import dev.phantom.visuals.util.Theme;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_429;
import net.minecraft.class_437;
import net.minecraft.class_500;
import net.minecraft.class_526;
import org.lwjgl.glfw.GLFW;

/** Красивое главное меню вместо ванильного. */
public class PhantomTitleScreen extends class_437 {
    private static final String[] LABELS = {"Singleplayer", "Multiplayer", "Options", "Nickname", "Quit"};
    private static final int BW = 220, BH = 24, GAP = 7;

    private final float[] hov = new float[LABELS.length];
    private long last = System.nanoTime();

    public PhantomTitleScreen() {
        super(class_2561.method_43470("PhantomVisuals"));
    }

    private int bx() {
        return (field_22789 - BW) / 2;
    }

    private int by(int i) {
        return field_22790 / 2 - 14 + i * (BH + GAP);
    }

    private static void roundRect(class_332 c, int x, int y, int w, int h, int r, int color) {
        r = Math.min(r, Math.min(w, h) / 2);
        c.method_25294(x, y + r, x + w, y + h - r, color);
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int inset = r - (int) Math.round(Math.sqrt(Math.max(0, r * r - dy * dy)));
            c.method_25294(x + inset, y + i, x + w - inset, y + i + 1, color);
            c.method_25294(x + inset, y + h - i - 1, x + w - inset, y + h - i, color);
        }
    }

    @Override
    public void method_25394(class_332 ctx, int mx, int my, float delta) {
        long now = System.nanoTime();
        float dt = Math.min(0.1f, (now - last) / 1.0E9f);
        last = now;
        float k = 1f - (float) Math.exp(-dt * 14f);
        float t = (System.currentTimeMillis() % 1000000L) / 1000f;

        ctx.method_25296(0, 0, field_22789, field_22790, 0xFF080512, 0xFF1C0B3D);

        // плавающие частицы
        for (int i = 0; i < 70; i++) {
            float sp = 6f + (i % 7) * 2.5f;
            float x = (i * 73.13f + t * sp) % field_22789;
            float y = field_22790 - ((i * 41.7f + t * (10f + (i % 5) * 6f)) % field_22790);
            int s = 1 + (i % 3);
            int a = 40 + (i * 7) % 90;
            ctx.method_25294((int) x, (int) y, (int) x + s, (int) y + s, Theme.withAlpha(Theme.accent(i * 0.02f), a));
        }

        // заголовок
        String title = "PhantomVisuals";
        int scale = 4;
        int tw = field_22793.method_1727(title) * scale;
        float bob = (float) Math.sin(t * 1.5f) * 3f;
        ctx.method_51448().method_22903();
        ctx.method_51448().method_46416((float) ((field_22789 - tw) / 2), field_22790 / 2f - 90f + bob, 0f);
        ctx.method_51448().method_22905(scale, scale, 1f);
        int cx = 0;
        for (int i = 0; i < title.length(); i++) {
            String ch = title.substring(i, i + 1);
            ctx.method_51433(field_22793, ch, cx, 0, Theme.accent(i * 0.05f), true);
            cx += field_22793.method_1727(ch);
        }
        ctx.method_51448().method_22909();
        String sub = "Minecraft 1.21.4";
        ctx.method_51433(field_22793, sub, (field_22789 - field_22793.method_1727(sub)) / 2, field_22790 / 2 - 42, 0xFF8C8CA6, false);

        // кнопки
        for (int i = 0; i < LABELS.length; i++) {
            int x = bx(), y = by(i);
            boolean over = mx >= x && mx < x + BW && my >= y && my < y + BH;
            hov[i] += ((over ? 1f : 0f) - hov[i]) * k;
            int grow = Math.round(hov[i] * 4);
            roundRect(ctx, x - grow - 1, y - 1, BW + grow * 2 + 2, BH + 2, 6, Theme.withAlpha(Theme.accent(i * 0.1f), (int) (60 + hov[i] * 150)));
            roundRect(ctx, x - grow, y, BW + grow * 2, BH, 5, Theme.lerp(0xF0121219, 0xF01E1A33, hov[i]));
            int tw2 = field_22793.method_1727(LABELS[i]);
            ctx.method_51433(field_22793, LABELS[i], (field_22789 - tw2) / 2, y + 8, Theme.lerp(0xFFB4B4CC, 0xFFFFFFFF, hov[i]), true);
        }

        String nick = "Nick: " + Nick.current();
        ctx.method_51433(field_22793, nick, 8, field_22790 - 14, 0xFF8C8CA6, false);
    }

    @Override
    public boolean method_25402(double mxD, double myD, int button) {
        if (button == 0) {
            int mx = (int) mxD, my = (int) myD;
            for (int i = 0; i < LABELS.length; i++) {
                int x = bx(), y = by(i);
                if (mx >= x && mx < x + BW && my >= y && my < y + BH) {
                    action(i);
                    return true;
                }
            }
        }
        return super.method_25402(mxD, myD, button);
    }

    private void action(int i) {
        class_310 mc = class_310.method_1551();
        switch (i) {
            case 0 -> mc.method_1507(new class_526(this));
            case 1 -> mc.method_1507(new class_500(this));
            case 2 -> mc.method_1507(new class_429(this, mc.field_1690));
            case 3 -> mc.method_1507(new NickScreen(this));
            default -> GLFW.glfwSetWindowShouldClose(mc.method_22683().method_4490(), true);
        }
    }

    @Override
    public boolean method_25404(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) return true; // из главного меню Esc не выходит
        return super.method_25404(keyCode, scanCode, modifiers);
    }
}
