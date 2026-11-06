package dev.phantom.visuals;

import dev.phantom.visuals.gui.ClickGuiScreen;
import dev.phantom.visuals.module.Feature;
import dev.phantom.visuals.module.FreeCam;
import dev.phantom.visuals.module.FreeLook;
import dev.phantom.visuals.module.ModuleManager;
import dev.phantom.visuals.render.RenderCtx;
import dev.phantom.visuals.util.Binds;
import dev.phantom.visuals.util.Nick;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.class_2561;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3675;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhantomVisuals implements ClientModInitializer {
    public static final String MOD_ID = "phantomvisuals";
    public static final String NAME = "PhantomVisuals";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    private static boolean nickApplied;
    private static class_304 guiKey;
    private static class_304 swapKey;
    private static class_304 freeLookKey;

    @Override
    public void onInitializeClient() {
        String version = FabricLoader.getInstance().getModContainer(MOD_ID)
                .map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("?");
        LOGGER.info("==========================================");
        LOGGER.info("{} v{} - loading...", NAME, version);
        LOGGER.info("Features: Predictions, ItemPhysic, Custom Particles & Trails, Nametags, ClickGUI");

        Config.load();

        guiKey = KeyBindingHelper.registerKeyBinding(new class_304(
                "key.phantomvisuals.gui", class_3675.class_307.field_1668, GLFW.GLFW_KEY_RIGHT_SHIFT, "key.category.phantomvisuals"));
        swapKey = KeyBindingHelper.registerKeyBinding(new class_304(
                "key.phantomvisuals.swap", class_3675.class_307.field_1668, GLFW.GLFW_KEY_X, "key.category.phantomvisuals"));

        freeLookKey = KeyBindingHelper.registerKeyBinding(new class_304(
                "key.phantomvisuals.freelook", class_3675.class_307.field_1668, GLFW.GLFW_KEY_LEFT_ALT, "key.category.phantomvisuals"));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("nick")
                        .then(ClientCommandManager.argument("name", StringArgumentType.word())
                                .executes(ctx -> {
                                    ctx.getSource().sendFeedback(class_2561.method_43470(Nick.change(StringArgumentType.getString(ctx, "name"))));
                                    return 1;
                                }))));


        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("bind")
                    .then(ClientCommandManager.argument("module", StringArgumentType.word())
                            .then(ClientCommandManager.argument("key", StringArgumentType.word())
                                    .executes(ctx -> {
                                        Feature f = Binds.find(StringArgumentType.getString(ctx, "module"));
                                        if (f == null) {
                                            ctx.getSource().sendFeedback(class_2561.method_43470("Модуль не найден. Список: /binds"));
                                            return 0;
                                        }
                                        String k = StringArgumentType.getString(ctx, "key");
                                        if (k.equalsIgnoreCase("none")) {
                                            f.bind = -1;
                                            ctx.getSource().sendFeedback(class_2561.method_43470(f.name + ": бинд снят"));
                                            return 1;
                                        }
                                        int code = Binds.parse(k);
                                        if (code < 0) {
                                            ctx.getSource().sendFeedback(class_2561.method_43470("Неизвестная клавиша: " + k + " (A-Z, 0-9, F1-F12, SPACE, LALT, ...)"));
                                            return 0;
                                        }
                                        f.bind = code;
                                        Config.save();
                                        ctx.getSource().sendFeedback(class_2561.method_43470(f.name + " -> " + Binds.name(code)));
                                        return 1;
                                    }))));
            dispatcher.register(ClientCommandManager.literal("unbind")
                    .then(ClientCommandManager.argument("module", StringArgumentType.word())
                            .executes(ctx -> {
                                Feature f = Binds.find(StringArgumentType.getString(ctx, "module"));
                                if (f == null) return 0;
                                f.bind = -1;
                                Config.save();
                                ctx.getSource().sendFeedback(class_2561.method_43470(f.name + ": бинд снят"));
                                return 1;
                            })));
            dispatcher.register(ClientCommandManager.literal("binds")
                    .executes(ctx -> {
                        StringBuilder sb = new StringBuilder();
                        for (Feature f : ModuleManager.ALL) {
                            if (f.isPermanent()) continue;
                            sb.append(f.name).append(f.bind > 0 ? " [" + Binds.name(f.bind) + "]" : "").append(", ");
                        }
                        ctx.getSource().sendFeedback(class_2561.method_43470("Модули: " + sb));
                        return 1;
                    }));
        });

        HudRenderCallback.EVENT.register((drawCtx, tickCounter) -> {
            ModuleManager.ARROWS.render(drawCtx);
            ModuleManager.POTIONHUD.render(drawCtx);
            ModuleManager.ARMORHUD.render(drawCtx);
            ModuleManager.MODULELIST.render(drawCtx);
        });

        ClientTickEvents.END_CLIENT_TICK.register(PhantomVisuals::tick);
        WorldRenderEvents.AFTER_TRANSLUCENT.register(PhantomVisuals::render);
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> Config.save());

        LOGGER.info("{} loaded: {} features. Press RIGHT SHIFT in game to open ClickGUI.", NAME, ModuleManager.ALL.size());
        LOGGER.info("==========================================");
    }

    private static void tick(class_310 mc) {
        if (!nickApplied) {
            nickApplied = true;
            Nick.applySaved();
        }
        if (mc.field_1724 == null || mc.field_1687 == null) {
            FreeLook.active = false;
            FreeCam.active = false;
            return;
        }

        Binds.tick(mc);
        ModuleManager.FREELOOK.tickKey(mc, freeLookKey);

        while (guiKey.method_1436()) {
            if (mc.field_1755 == null) ClickGuiScreen.open();
        }
        while (swapKey.method_1436()) {
            ModuleManager.TOTEM_SWAP.run(mc);
        }
        for (Feature f : ModuleManager.ALL) {
            if (f.isEnabled()) f.onTick(mc);
        }
    }

    private static void render(WorldRenderContext ctx) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null || mc.field_1724 == null) return;

        class_4587 matrices = ctx.matrixStack();
        class_4597 consumers = ctx.consumers();
        if (matrices == null || consumers == null) return;

        RenderCtx rc = new RenderCtx(mc, matrices, consumers, ctx.camera(), mc.method_61966().method_60637(true));
        for (Feature f : ModuleManager.ALL) {
            if (f.isEnabled()) f.onWorldRender(rc);
        }
        if (consumers instanceof class_4597.class_4598 immediate) {
            immediate.method_22993();
        }
    }
}
