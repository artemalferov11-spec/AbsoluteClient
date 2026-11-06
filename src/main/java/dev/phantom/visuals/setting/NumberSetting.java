package dev.phantom.visuals.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Locale;

public class NumberSetting extends Setting {
    public final double min;
    public final double max;
    public final double step;
    private double value;

    public NumberSetting(String name, double min, double max, double step, double def) {
        super(name);
        this.min = min;
        this.max = max;
        this.step = step;
        this.value = def;
    }

    public double get() {
        return value;
    }

    public float getF() {
        return (float) value;
    }

    public int getI() {
        return (int) Math.round(value);
    }

    public void set(double v) {
        v = Math.round(v / step) * step;
        value = Math.max(min, Math.min(max, v));
    }

    public String display() {
        if (step >= 1) return String.valueOf((int) Math.round(value));
        if (step >= 0.1) return String.format(Locale.ROOT, "%.1f", value);
        return String.format(Locale.ROOT, "%.2f", value);
    }

    @Override
    public JsonElement save() {
        return new JsonPrimitive(value);
    }

    @Override
    public void load(JsonElement element) {
        set(element.getAsDouble());
    }
}
