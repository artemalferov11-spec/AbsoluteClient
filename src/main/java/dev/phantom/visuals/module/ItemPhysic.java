package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.NumberSetting;

public class ItemPhysic extends Feature {
    public final NumberSetting drop = add(new NumberSetting("Ground offset", 0.0, 0.3, 0.01, 0.13));

    public ItemPhysic() {
        super("ItemPhysic", "Realistic item drop physics: lying flat, no bobbing", Category.VISUALS, false);
    }
}
