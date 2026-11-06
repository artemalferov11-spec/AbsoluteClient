package dev.phantom.visuals.module;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager {
    private ModuleManager() {}

    public static final Nametags NAMETAGS = new Nametags();
    public static final Predictions PREDICTIONS = new Predictions();
    public static final ItemPhysic ITEM_PHYSIC = new ItemPhysic();
    public static final Invisibles INVISIBLES = new Invisibles();
    public static final WorldParticles WORLD_PARTICLES = new WorldParticles();
    public static final Trails TRAILS = new Trails();
    public static final DamageEffects DAMAGE = new DamageEffects();
    public static final TotemSwap TOTEM_SWAP = new TotemSwap();
    public static final AutoSprint AUTO_SPRINT = new AutoSprint();
    public static final FreeLook FREELOOK = new FreeLook();
    public static final FastPlace FASTPLACE = new FastPlace();
    public static final NoSlow NOSLOW = new NoSlow();
    public static final Spider SPIDER = new Spider();
    public static final KillAura KILLAURA = new KillAura();
    public static final AutoCrystal AUTOCRYSTAL = new AutoCrystal();
    public static final SwingAnimation SWING = new SwingAnimation();
    public static final Strafe STRAFE = new Strafe();
    public static final Speed SPEED = new Speed();
    public static final Fly FLY = new Fly();
    public static final AutoTotem AUTOTOTEM = new AutoTotem();
    public static final ESP ESP = new ESP();
    public static final PotionHud POTIONHUD = new PotionHud();
    public static final JumpCircles JUMPCIRCLES = new JumpCircles();
    public static final FreeCam FREECAM = new FreeCam();
    public static final Arrows ARROWS = new Arrows();
    public static final ModuleList MODULELIST = new ModuleList();
    public static final Sky SKY = new Sky();
    public static final ArmorHud ARMORHUD = new ArmorHud();
    public static final CustomMenu MENU = new CustomMenu();
    public static final ThemeFeature THEME = new ThemeFeature();

    public static final List<Feature> ALL = List.of(
            NAMETAGS, PREDICTIONS, ITEM_PHYSIC, INVISIBLES, FREELOOK, FREECAM, ESP, ARROWS, POTIONHUD, ARMORHUD, MODULELIST, JUMPCIRCLES, SKY,
            WORLD_PARTICLES, TRAILS, DAMAGE,
            TOTEM_SWAP, AUTO_SPRINT, FASTPLACE,
            NOSLOW, SPIDER, STRAFE, SPEED, FLY, KILLAURA, AUTOCRYSTAL, AUTOTOTEM, SWING,
            MENU, THEME
    );

    static {
        // Включено по умолчанию
        NAMETAGS.setEnabled(true);
        PREDICTIONS.setEnabled(true);
        ITEM_PHYSIC.setEnabled(true);
        DAMAGE.setEnabled(true);
        TOTEM_SWAP.setEnabled(true);
        FREELOOK.setEnabled(true);
        MENU.setEnabled(true);
    }

    public static List<Feature> of(Category category) {
        List<Feature> list = new ArrayList<>();
        for (Feature f : ALL) {
            if (f.category == category) list.add(f);
        }
        return list;
    }
}
