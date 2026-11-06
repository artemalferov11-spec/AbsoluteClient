package dev.phantom.visuals.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_310;

/**
 * Смена ника (Session) через рефлексию по ТИПАМ полей, без привязки к именам из Minecraft.
 * Работает для офлайн-серверов / одиночной игры. На лицензионных (online-mode) серверах вход всё равно не пройдёт.
 */
public final class Nick {
    private Nick() {}

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("phantomvisuals-nick.txt");
    }

    private static boolean looksLikeSession(Class<?> t) {
        if (!t.getName().startsWith("net.minecraft.")) return false;
        boolean str = false, uuid = false, opt = false, en = false;
        for (Field f : t.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) continue;
            Class<?> ft = f.getType();
            if (ft == String.class) str = true;
            else if (ft == UUID.class) uuid = true;
            else if (ft == Optional.class) opt = true;
            else if (ft.isEnum()) en = true;
        }
        return str && uuid && opt && en;
    }

    private static Field sessionField() {
        for (Field f : class_310.class.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) continue;
            if (looksLikeSession(f.getType())) return f;
        }
        return null;
    }

    private static List<Field> instanceFields(Class<?> c) {
        List<Field> list = new ArrayList<>();
        for (Field f : c.getDeclaredFields()) {
            if (!Modifier.isStatic(f.getModifiers())) list.add(f);
        }
        return list;
    }

    /** Текущий ник или "?" */
    public static String current() {
        try {
            Field sf = sessionField();
            if (sf == null) return "?";
            sf.setAccessible(true);
            Object s = sf.get(class_310.method_1551());
            for (Field f : instanceFields(s.getClass())) {
                if (f.getType() == String.class) {
                    f.setAccessible(true);
                    return String.valueOf(f.get(s));
                }
            }
        } catch (Throwable ignored) {
        }
        return "?";
    }

    public static boolean valid(String name) {
        return name != null && name.matches("[A-Za-z0-9_]{3,16}");
    }

    /** Меняет ник и сохраняет. Возвращает сообщение для игрока. */
    public static String change(String name) {
        if (!valid(name)) return "Ник: 3-16 символов, только A-Z, a-z, 0-9 и _";
        String err = apply(name);
        if (err != null) return err;
        try {
            Files.writeString(file(), name);
        } catch (Exception ignored) {
        }
        return "Ник изменён на " + name + ". Применится при следующем входе на сервер.";
    }

    public static void applySaved() {
        try {
            Path p = file();
            if (!Files.exists(p)) return;
            String name = Files.readString(p).trim();
            if (valid(name)) apply(name);
        } catch (Throwable ignored) {
        }
    }

    /** null = успех, иначе текст ошибки. */
    private static String apply(String name) {
        try {
            Field sf = sessionField();
            if (sf == null) return "Не удалось найти Session в Minecraft";
            sf.setAccessible(true);
            class_310 mc = class_310.method_1551();
            Object old = sf.get(mc);
            Class<?> sc = old.getClass();
            List<Field> fields = instanceFields(sc);
            for (Field f : fields) f.setAccessible(true);

            for (Constructor<?> c : sc.getDeclaredConstructors()) {
                Class<?>[] pt = c.getParameterTypes();
                Object[] args = new Object[pt.length];
                boolean[] used = new boolean[fields.size()];
                boolean usernameSet = false;
                boolean ok = true;
                for (int i = 0; i < pt.length && ok; i++) {
                    int found = -1;
                    for (int j = 0; j < fields.size(); j++) {
                        if (!used[j] && fields.get(j).getType() == pt[i]) {
                            found = j;
                            break;
                        }
                    }
                    if (found < 0) {
                        ok = false;
                        break;
                    }
                    used[found] = true;
                    Object v = fields.get(found).get(old);
                    if (pt[i] == String.class && !usernameSet) {
                        v = name; // первое String-поле = username
                        usernameSet = true;
                    }
                    args[i] = v;
                }
                if (!ok || !usernameSet) continue;
                c.setAccessible(true);
                Object ns = c.newInstance(args);
                sf.set(mc, ns);
                return null;
            }
            return "Не нашёл подходящий конструктор Session";
        } catch (Throwable t) {
            return "Ошибка смены ника: " + t;
        }
    }
}
