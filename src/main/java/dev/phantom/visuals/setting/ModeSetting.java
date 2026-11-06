package dev.phantom.visuals.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting {
    public final List<String> modes;
    private int index;

    public ModeSetting(String name, String def, String... modes) {
        super(name);
        this.modes = Arrays.asList(modes);
        int i = this.modes.indexOf(def);
        this.index = i < 0 ? 0 : i;
    }

    public String get() {
        return modes.get(index);
    }

    public boolean is(String mode) {
        return get().equals(mode);
    }

    public void next() {
        index = (index + 1) % modes.size();
    }

    public void prev() {
        index = (index - 1 + modes.size()) % modes.size();
    }

    @Override
    public JsonElement save() {
        return new JsonPrimitive(get());
    }

    @Override
    public void load(JsonElement element) {
        int i = modes.indexOf(element.getAsString());
        if (i >= 0) index = i;
    }
}
