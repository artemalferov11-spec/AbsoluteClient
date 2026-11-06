package dev.phantom.visuals.module;

/** Включает своё главное меню (подмена в MinecraftClientMixin). */
public class CustomMenu extends Feature {
    public CustomMenu() {
        super("Custom Menu", "Custom main menu (restart or reopen the menu to apply)", Category.CLIENT, false);
    }
}
