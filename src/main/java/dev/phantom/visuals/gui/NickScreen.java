package dev.phantom.visuals.gui;

import dev.phantom.visuals.util.Nick;
import dev.phantom.visuals.util.Theme;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_437;
import org.lwjgl.glfw.GLFW;

/** Экран смены ника (ввод через коды клавиш, поэтому работает с любой раскладкой). */
public class NickScreen extends class_437 {
    private final class_437 parent;
    private final StringBuilder text = new StringBuilder();
    private String status = "";
    private int statusColor = 0xFFAAAAAA;

    public NickScreen(class_437 parent) {
        super(class_2561.method_43470("Nickname"));
        this.parent = parent;
        String cur = Nick.current();
        if (Nick.valid(cur)) text.append(cur);
    }

    @Override
    public void method_25394(class_332 ctx, int mx, int my, float delta) {
        ctx.method_25296(0, 0, field_22789, field_22790, 0xFF0A0614, 0xFF1A0B3A);
        int cx = field_22789 / 2, cy = field_22790 / 2;

        String title = "Nickname";
        int tw = field_22793.method_1727(title) * 3;
        ctx.method_51448().method_22903();
        ctx.method_51448().method_46416((float) (cx - tw / 2), (float) (cy - 70), 0f);
        ctx.method_51448().method_22905(3f, 3f, 1f);
        ctx.method_51433(field_22793, title, 0, 0, Theme.accent(0f), true);
        ctx.method_51448().method_22909();

        int bw = 220, bh = 24;
        int x = cx - bw / 2, y = cy - 20;
        ctx.method_25294(x - 1, y - 1, x + bw + 1, y + bh + 1, Theme.accent(0.1f));
        ctx.method_25294(x, y, x + bw, y + bh, 0xFF121219);
        boolean caret = (System.currentTimeMillis() / 500) % 2 == 0;
        ctx.method_51433(field_22793, text + (caret ? "_" : ""), x + 8, y + 8, 0xFFFFFFFF, true);

        ctx.method_51433(field_22793, "Enter - применить   |   Esc - назад", cx - field_22793.method_1727("Enter - применить   |   Esc - назад") / 2, y + 36, 0xFF8C8CA6, false);
        if (!status.isEmpty()) {
            ctx.method_51433(field_22793, status, cx - field_22793.method_1727(status) / 2, y + 54, statusColor, false);
        }
    }

    @Override
    public boolean method_25404(int keyCode, int scanCode, int modifiers) {
        class_310 mc = class_310.method_1551();
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            mc.method_1507(parent);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            String r = Nick.change(text.toString());
            status = r;
            statusColor = Nick.valid(text.toString()) ? 0xFF55FF55 : 0xFFFF5555;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (text.length() > 0) text.deleteCharAt(text.length() - 1);
            return true;
        }
        if (text.length() >= 16) return true;
        boolean shift = (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;
        if (keyCode >= GLFW.GLFW_KEY_A && keyCode <= GLFW.GLFW_KEY_Z) {
            char c = (char) keyCode;
            text.append(shift ? c : Character.toLowerCase(c));
            return true;
        }
        if (keyCode >= GLFW.GLFW_KEY_0 && keyCode <= GLFW.GLFW_KEY_9) {
            text.append((char) keyCode);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_MINUS && shift) {
            text.append('_');
            return true;
        }
        return true;
    }
}
