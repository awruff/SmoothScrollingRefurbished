package me.wolfii.smoothscrollingrefurbished.config;

import org.polyfrost.oneconfig.api.config.v1.Config;
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider;
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch;

public class SmoothScrollingConfig extends Config {
    public static final SmoothScrollingConfig INSTANCE = new SmoothScrollingConfig();

    @Slider(title = "Scroll strength", description = "How far a single scroll moves the list.", min = 0.1f, max = 2.5f, step = 0.05f)
    public static float scrollStrength = 0.5f;

    @Slider(title = "Scrollbar friction", description = "How quickly scrolling slows down.", min = 0.01f, max = 0.1f, step = 0.001f)
    public static float scrollbarFriction = 0.025f;

    @Switch(title = "Push back", description = "Let lists overscroll and bounce back at the ends.")
    public static boolean pushBack = true;

    @Slider(title = "Push back strength", description = "How hard an overscrolled list snaps back.", min = 0.1f, max = 2.0f, step = 0.05f)
    public static float pushBackStrength = 1.0f;

    private SmoothScrollingConfig() {
        super("smoothscrollingrefurbished.json", "/assets/smoothscrollingrefurbished/icon.png", "Smooth Scrolling Refurbished", Category.QOL);
        addDependency("pushBackStrength", "pushBack");
    }
}
