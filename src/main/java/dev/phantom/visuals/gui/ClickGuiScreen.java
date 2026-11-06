package dev.phantom.visuals.gui;

import dev.phantom.visuals.module.Category;
import dev.phantom.visuals.module.Feature;
import dev.phantom.visuals.module.ModuleManager;
import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.ModeSetting;
import dev.phantom.visuals.setting.NumberSetting;
import dev.phantom.visuals.setting.Setting;
import dev.phantom.visuals.util.Theme;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_437;

public class ClickGuiScreen extends class_437 {
    private static ClickGuiScreen instance;

    public static void open() {
        if (instance == null) instance = new ClickGuiScreen();
        class_310.method_1551().method_1507(instance);
    }

    private static final int PW = 126;
    private static final int HEAD = 22;
    private static final int ROW = 20;

    private static final int C_PANEL = 0xF0121219;
    private static final int C_HEAD = 0xFF1A1A26;
    private static final int C_TEXT = 0xFFE8E8F4;
    private static final int C_DIM = 0xFF8C8CA6;
    private static final int C_TRACK = 0xFF2A2A3A;

    private static final class Panel {
        final Category cat;
        final List<Feature> features;
        int x, y;
        boolean collapsed;
        int bodyBottom;

        Panel(Category cat) {
            this.cat = cat;
            this.features = ModuleManager.of(cat);
        }
    }

    private final List<Panel> panels = new ArrayList<>();
    private final Map<String, Float> anims = new HashMap<>();
    private final Set<Feature> open = new HashSet<>();
    private boolean placed;
    private Panel dragging;
    private int dragDx, dragDy;
    private NumberSetting sliding;
    private int slideX, slideW;
    private long lastNanos = System.nanoTime();
    private float k = 0.3f;
    private String hoverDesc;

    private ClickGuiScreen() {
        super(class_2561.method_43470("PhantomVisuals"));
        for (Category c : Category.values()) panels.add(new Panel(c));
    }

    @Override
    protected void method_25426() {
        if (!placed) {
            int n = panels.size();
            int gap = 10;
            int total = n * PW + (n - 1) * gap;
            int x = Math.max(6, (field_22789 - total) / 2);
            for (Panel p : panels) {
                p.x = x;
                p.y = 34;
                x += PW + gap;
            }
            placed = true;
        }
    }

    @Override
    public boolean method_25421() {
        return false;
    }

    @Override
    public void method_25432() {
        dragging = null;
        sliding = null;
        dev.phantom.visuals.Config.save();
    }

    // ---------------------------------------------------------------- helpers

    private float anim(String key, float target) {
        float v = anims.getOrDefault(key, target);
        v += (target - v) * k;
        if (Math.abs(target - v) < 0.003f) v = target;
        anims.put(key, v);
        return v;
    }

    private static int rowHeight(Setting s) {
        return s instanceof NumberSetting ? 26 : 16;
    }

    private static int settingsHeight(Feature f) {
        int h = 0;
        for (Setting s : f.settings) h += rowHeight(s);
        return h;
    }

    private static void roundRect(class_332 c, int x, int y, int w, int h, int r, int color) {
        if (w <= 0 || h <= 0) return;
        r = Math.min(r, Math.min(w, h) / 2);
        c.method_25294(x, y + r, x + w, y + h - r, color);
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int inset = r - (int) Math.round(Math.sqrt(Math.max(0, r * r - dy * dy)));
            c.method_25294(x + inset, y + i, x + w - inset, y + i + 1, color);
            c.method_25294(x + inset, y + h - i - 1, x + w - inset, y + h - i, color);
        }
    }

    private void pill(class_332 ctx, int x, int y, int w, int h, float t) {
        int accent = Theme.accent(0f);
        roundRect(ctx, x, y, w, h, h / 2, Theme.lerp(C_TRACK, accent, t));
        int knob = h - 4;
        int kx = x + 2 + Math.round(t * (w - knob - 4));
        roundRect(ctx, kx, y + 2, knob, knob, knob / 2, 0xFFFFFFFF);
    }

    // ----------------------------------------------------------------- render

    @Override
    public void method_25394(class_332 ctx, int mx, int my, float delta) {
        long now = System.nanoTime();
        float dt = (now - lastNanos) / 1.0E9f;
        lastNanos = now;
        k = 1f - (float) Math.exp(-Math.min(dt, 0.1f) * 16f);
        hoverDesc = null;

        ctx.method_25296(0, 0, field_22789, field_22790, 0xB0070711, 0xD0120A26);

        // Заголовок с градиентом
        String title = "PhantomVisuals";
        int tw = field_22793.method_1727(title) * 2;
        ctx.method_51448().method_22903();
        ctx.method_51448().method_46416((float) ((field_22789 - tw) / 2), 8f, 0f);
        ctx.method_51448().method_22905(2f, 2f, 1f);
        int cx = 0;
        for (int i = 0; i < title.length(); i++) {
            String ch = title.substring(i, i + 1);
            ctx.method_51433(field_22793, ch, cx, 0, Theme.accent(i * 0.05f), true);
            cx += field_22793.method_1727(ch);
        }
        ctx.method_51448().method_22909();

        for (Panel p : panels) renderPanel(ctx, p, mx, my);

        String hint = hoverDesc != null ? hoverDesc
                : "Left click: toggle   |   Right click: settings   |   RIGHT SHIFT: close";
        int hw = field_22793.method_1727(hint);
        int hx = (field_22789 - hw) / 2;
        roundRect(ctx, hx - 8, field_22790 - 24, hw + 16, 16, 4, 0xB0000000);
        ctx.method_51433(field_22793, hint, hx, field_22790 - 20, hoverDesc != null ? C_TEXT : C_DIM, false);
    }

    private void renderPanel(class_332 ctx, Panel p, int mx, int my) {
        float col = anim("col:" + p.cat, p.collapsed ? 0f : 1f);
        float[] oa = new float[p.features.size()];
        int contentH = 0;
        for (int i = 0; i < oa.length; i++) {
            Feature f = p.features.get(i);
            oa[i] = anim("open:" + f.name, open.contains(f) ? 1f : 0f);
            contentH += ROW + Math.round(settingsHeight(f) * oa[i]);
        }
        int bodyH = Math.round(contentH * col);
        p.bodyBottom = p.y + HEAD + bodyH;
        int total = HEAD + bodyH + (bodyH > 0 ? 3 : 0);

        roundRect(ctx, p.x - 1, p.y - 1, PW + 2, total + 2, 5, 0x55000000);
        roundRect(ctx, p.x, p.y, PW, total, 4, C_PANEL);
        roundRect(ctx, p.x, p.y, PW, HEAD, 4, C_HEAD);
        ctx.method_25294(p.x, p.y + HEAD - 4, p.x + PW, p.y + HEAD, C_HEAD);

        // акцентная линия под заголовком
        for (int i = 0; i < PW; i++) {
            ctx.method_25294(p.x + i, p.y + HEAD - 1, p.x + i + 1, p.y + HEAD, Theme.accent(i / (float) PW));
        }
        String label = p.cat.label.toUpperCase();
        ctx.method_51433(field_22793, label, p.x + 9, p.y + 7, C_TEXT, true);
        ctx.method_51433(field_22793, p.collapsed ? "+" : "-", p.x + PW - 14, p.y + 7, C_DIM, false);

        if (bodyH <= 0) return;

        int accent = Theme.accent(0f);
        int ry = p.y + HEAD;
        ctx.method_44379(p.x, p.y + HEAD, p.x + PW, p.y + HEAD + bodyH);
        for (int i = 0; i < p.features.size(); i++) {
            Feature f = p.features.get(i);
            boolean hover = mx >= p.x && mx < p.x + PW && my >= ry && my < ry + ROW && my < p.bodyBottom;
            float hv = anim("hov:" + f.name, hover ? 1f : 0f);
            float en = anim("en:" + f.name, f.isEnabled() ? 1f : 0f);
            if (hover) hoverDesc = f.description;

            ctx.method_25294(p.x, ry, p.x + PW, ry + ROW, Theme.withAlpha(accent, (int) (en * 34)));
            ctx.method_25294(p.x, ry, p.x + PW, ry + ROW, ((int) (hv * 20) << 24) | 0xFFFFFF);
            if (en > 0.02f) ctx.method_25294(p.x, ry + 3, p.x + 2, ry + ROW - 3, Theme.withAlpha(accent, (int) (en * 255)));

            ctx.method_51433(field_22793, f.name, p.x + 9, ry + 6, Theme.lerp(C_DIM, C_TEXT, f.isPermanent() ? 1f : en), true);

            if (!f.isPermanent()) pill(ctx, p.x + PW - 29, ry + 5, 22, 10, en);
            if (!f.settings.isEmpty()) {
                String ind = open.contains(f) ? "-" : "+";
                ctx.method_51433(field_22793, ind, p.x + PW - (f.isPermanent() ? 14 : 41), ry + 6, C_DIM, false);
            }
            ry += ROW;

            int sh = Math.round(settingsHeight(f) * oa[i]);
            if (sh > 0) {
                renderSettings(ctx, f, p.x, ry, sh);
                ry += sh;
            }
        }
        ctx.method_44380();
    }

    private void renderSettings(class_332 ctx, Feature f, int x, int y, int h) {
        ctx.method_44379(x, y, x + PW, y + h);
        ctx.method_25294(x, y, x + PW, y + h, 0x50000000);
        int accent = Theme.accent(0.1f);
        int ry = y;
        for (Setting s : f.settings) {
            int rh = rowHeight(s);
            if (s instanceof BoolSetting b) {
                float a = anim("b:" + f.name + s.name, b.get() ? 1f : 0f);
                ctx.method_51433(field_22793, s.name, x + 10, ry + 4, C_DIM, false);
                pill(ctx, x + PW - 28, ry + 3, 20, 9, a);
            } else if (s instanceof ModeSetting m) {
                ctx.method_51433(field_22793, s.name, x + 10, ry + 4, C_DIM, false);
                String v = m.get();
                ctx.method_51433(field_22793, v, x + PW - 8 - field_22793.method_1727(v), ry + 4, accent, false);
            } else if (s instanceof NumberSetting n) {
                ctx.method_51433(field_22793, s.name, x + 10, ry + 4, C_DIM, false);
                String v = n.display();
                ctx.method_51433(field_22793, v, x + PW - 8 - field_22793.method_1727(v), ry + 4, accent, false);
                int tx = x + 10, tw = PW - 20, ty = ry + 17;
                float t = (float) ((n.get() - n.min) / (n.max - n.min));
                t = Math.max(0f, Math.min(1f, t));
                ctx.method_25294(tx, ty, tx + tw, ty + 3, C_TRACK);
                int fw = Math.round(t * tw);
                for (int i = 0; i < fw; i++) {
                    ctx.method_25294(tx + i, ty, tx + i + 1, ty + 3, Theme.accent(i / (float) tw * 0.5f));
                }
                ctx.method_25294(tx + fw - 2, ty - 2, tx + fw + 2, ty + 5, 0xFFFFFFFF);
            }
            ry += rh;
        }
        ctx.method_44380();
    }

    // ------------------------------------------------------------------ input

    @Override
    public boolean method_25402(double mxD, double myD, int button) {
        int mx = (int) mxD, my = (int) myD;
        for (int i = panels.size() - 1; i >= 0; i--) {
            Panel p = panels.get(i);
            if (mx < p.x || mx >= p.x + PW || my < p.y) continue;

            if (my < p.y + HEAD) {
                if (button == 0) {
                    dragging = p;
                    dragDx = mx - p.x;
                    dragDy = my - p.y;
                } else if (button == 1) {
                    p.collapsed = !p.collapsed;
                }
                panels.remove(i);
                panels.add(p);
                return true;
            }
            if (my >= p.bodyBottom) continue;

            int ry = p.y + HEAD;
            for (Feature f : p.features) {
                if (my >= ry && my < ry + ROW) {
                    if (button == 0) f.toggle();
                    else if (button == 1 && !f.settings.isEmpty()) {
                        if (!open.remove(f)) open.add(f);
                    }
                    return true;
                }
                ry += ROW;
                float oa = anims.getOrDefault("open:" + f.name, 0f);
                int sh = Math.round(settingsHeight(f) * oa);
                if (my >= ry && my < ry + sh) {
                    clickSettings(f, p.x, ry, mx, my, button);
                    return true;
                }
                ry += sh;
            }
            return true;
        }
        return super.method_25402(mxD, myD, button);
    }

    private void clickSettings(Feature f, int x, int y, int mx, int my, int button) {
        int ry = y;
        for (Setting s : f.settings) {
            int rh = rowHeight(s);
            if (my >= ry && my < ry + rh) {
                if (s instanceof BoolSetting b) {
                    if (button == 0) b.toggle();
                } else if (s instanceof ModeSetting m) {
                    if (button == 0) m.next();
                    else if (button == 1) m.prev();
                } else if (s instanceof NumberSetting n) {
                    if (button == 0) {
                        sliding = n;
                        slideX = x + 10;
                        slideW = PW - 20;
                        updateSlider(mx);
                    }
                }
                return;
            }
            ry += rh;
        }
    }

    private void updateSlider(int mx) {
        if (sliding == null) return;
        double t = Math.max(0.0, Math.min(1.0, (mx - slideX) / (double) slideW));
        sliding.set(sliding.min + t * (sliding.max - sliding.min));
    }

    @Override
    public boolean method_25403(double mx, double my, int button, double dx, double dy) {
        if (dragging != null && button == 0) {
            dragging.x = Math.max(0, Math.min(field_22789 - PW, (int) mx - dragDx));
            dragging.y = Math.max(0, Math.min(field_22790 - HEAD, (int) my - dragDy));
            return true;
        }
        if (sliding != null && button == 0) {
            updateSlider((int) mx);
            return true;
        }
        return super.method_25403(mx, my, button, dx, dy);
    }

    @Override
    public boolean method_25406(double mx, double my, int button) {
        dragging = null;
        sliding = null;
        return super.method_25406(mx, my, button);
    }

    @Override
    public boolean method_25404(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            method_25419();
            return true;
        }
        return super.method_25404(keyCode, scanCode, modifiers);
    }
}
