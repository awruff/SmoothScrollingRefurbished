package me.wolfii.smoothscrollingrefurbished;

import me.wolfii.smoothscrollingrefurbished.config.SmoothScrollingConfig;

public class ScrollMath {
    public static double scrollbarVelocity(double timer, double factor) {
        return Math.pow(1 - SmoothScrollingConfig.scrollbarFriction, timer) * factor;
    }

    public static double pushBackStrength(double distance, float delta) {
        return ((distance + 4d) * delta / 0.3d) / (3.2d / SmoothScrollingConfig.pushBackStrength);
    }
}
