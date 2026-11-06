package dev.phantom.visuals.util;

import dev.phantom.visuals.module.Feature;
import dev.phantom.visuals.module.ModuleManager;
import net.minecraft.class_310;
import org.lwjgl.glfw.GLFW;

/** Бинды клавиш на модули: /bind <module> <key>, /unbind <module>, /binds. */
public final class Binds {
    private Binds() {}

    private static final String[] NAMES = {
            "SPACE", "TAB", "UP", "DOWN", "LEFT", "RIGHT", "INSERT", "DELETE", "HOME", "END", "PAGEUP", "PAGEDOWN",
            "LSHIFT", "RSHIFT", "LCTRL", "RCTRL", "LALT", "RALT", "CAPSLOCK", "GRAVE",
            "MINUS", "EQUAL", "COMMA", "PERIOD", "SLASH", "SEMICOLON", "APOSTROPHE", "BACKSLASH"
    };
    private static final int[] CODES = {
            GLFW.GLFW_KEY_SPACE, GLFW.GLFW_KEY_TAB, GLFW.GLFW_KEY_UP, GLFW.GLFW_KEY_DOWN, GLFW.GLFW_KEY_LEFT, GLFW.GLFW_KEY_RIGHT,
            GLFW.GLFW_KEY_INSERT, GLFW.GLFW_KEY_DELETE, GLFW.GLFW_KEY_HOME, GLFW.GLFW_KEY_END, GLFW.GLFW_KEY_PAGE_UP, GLFW.GLFW_KEY_PAGE_DOWN,
            GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT, GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL,
            GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_ALT, GLFW.GLFW_KEY_CAPS_LOCK, GLFW.GLFW_KEY_GRAVE_ACCENT,
            GLFW.GLFW_KEY_MINUS, GLFW.GLFW_KEY_EQUAL, GLFW.GLFW_KEY_COMMA, GLFW.GLFW_KEY_PERIOD, GLFW.GLFW_KEY_SLASH,
            GLFW.GLFW_KEY_SEMICOLON, GLFW.GLFW_KEY_APOSTROPHE, GLFW.GLFW_KEY_BACKSLASH
    };

    /** -1 — не распознано. */
    public static int parse(String s) {
        s = s.toUpperCase();
        if (s.length() == 1) {
            char c = s.charAt(0);
            if (c >= 'A' && c <= 'Z') return GLFW.GLFW_KEY_A + (c - 'A');
            if (c >= '0' && c <= '9') return GLFW.GLFW_KEY_0 + (c - '0');
        }
        if (s.matches("F([1-9]|1[0-9]|2[0-5])")) return GLFW.GLFW_KEY_F1 + Integer.parseInt(s.substring(1)) - 1;
        for (int i = 0; i < NAMES.length; i++) if (NAMES[i].equals(s)) return CODES[i];
        return -1;
    }

    public static String name(int key) {
        if (key <= 0) return "NONE";
        if (key >= GLFW.GLFW_KEY_A && key <= GLFW.GLFW_KEY_Z) return String.valueOf((char) ('A' + key - GLFW.GLFW_KEY_A));
        if (key >= GLFW.GLFW_KEY_0 && key <= GLFW.GLFW_KEY_9) return String.valueOf((char) ('0' + key - GLFW.GLFW_KEY_0));
        if (key >= GLFW.GLFW_KEY_F1 && key <= GLFW.GLFW_KEY_F25) return "F" + (key - GLFW.GLFW_KEY_F1 + 1);
        for (int i = 0; i < CODES.length; i++) if (CODES[i] == key) return NAMES[i];
        return "KEY" + key;
    }

    public static Feature find(String name) {
        for (Feature f : ModuleManager.ALL) if (f.name.equalsIgnoreCase(name)) return f;
        return null;
    }

    public static void tick(class_310 mc) {
        long window = mc.method_22683().method_4490();
        boolean canToggle = mc.field_1755 == null;
        for (Feature f : ModuleManager.ALL) {
            if (f.bind <= 0) continue;
            boolean down = GLFW.glfwGetKey(window, f.bind) == GLFW.GLFW_PRESS;
            if (down && !f.bindDown && canToggle) f.toggle();
            f.bindDown = down;
        }
    }
}
