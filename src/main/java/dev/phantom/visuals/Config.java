package dev.phantom.visuals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.phantom.visuals.module.Feature;
import dev.phantom.visuals.module.ModuleManager;
import dev.phantom.visuals.setting.Setting;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public final class Config {
    private Config() {}

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("phantomvisuals.json");
    }

    public static void save() {
        try {
            JsonObject root = new JsonObject();
            for (Feature f : ModuleManager.ALL) {
                JsonObject o = new JsonObject();
                o.addProperty("enabled", f.isEnabled());
                o.addProperty("bind", f.bind);
                JsonObject s = new JsonObject();
                for (Setting st : f.settings) s.add(st.name, st.save());
                o.add("settings", s);
                root.add(f.name, o);
            }
            Files.writeString(file(), GSON.toJson(root));
        } catch (Exception e) {
            PhantomVisuals.LOGGER.warn("Could not save config", e);
        }
    }

    public static void load() {
        Path p = file();
        if (!Files.exists(p)) return;
        try {
            JsonObject root = JsonParser.parseString(Files.readString(p)).getAsJsonObject();
            for (Feature f : ModuleManager.ALL) {
                if (!root.has(f.name)) continue;
                JsonObject o = root.getAsJsonObject(f.name);
                if (o.has("enabled")) f.setEnabled(o.get("enabled").getAsBoolean());
                if (o.has("bind")) f.bind = o.get("bind").getAsInt();
                if (o.has("settings")) {
                    JsonObject s = o.getAsJsonObject("settings");
                    for (Setting st : f.settings) {
                        if (s.has(st.name)) {
                            try {
                                st.load(s.get(st.name));
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            PhantomVisuals.LOGGER.warn("Could not load config", e);
        }
    }
}
