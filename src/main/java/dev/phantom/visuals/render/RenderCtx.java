package dev.phantom.visuals.render;

import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4597;

/** Всё, что нужно модулям для рисования в мире. */
public final class RenderCtx {
    public final class_310 mc;
    public final class_4587 matrices;
    public final class_4597 consumers;
    public final class_4184 camera;
    public final class_243 cam;
    public final float delta;

    public RenderCtx(class_310 mc, class_4587 matrices, class_4597 consumers, class_4184 camera, float delta) {
        this.mc = mc;
        this.matrices = matrices;
        this.consumers = consumers;
        this.camera = camera;
        this.cam = camera.method_19326();
        this.delta = delta;
    }
}
