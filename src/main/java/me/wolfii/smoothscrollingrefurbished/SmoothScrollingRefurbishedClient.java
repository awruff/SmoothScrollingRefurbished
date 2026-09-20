package me.wolfii.smoothscrollingrefurbished;

import me.wolfii.smoothscrollingrefurbished.config.SmoothScrollingConfig;
import net.fabricmc.api.ClientModInitializer;

public class SmoothScrollingRefurbishedClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SmoothScrollingConfig.INSTANCE.preload();
    }
}
