package me.wolfii.smoothscrollingrefurbished.mixin;

import me.wolfii.smoothscrollingrefurbished.ScrollMath;
import me.wolfii.smoothscrollingrefurbished.config.SmoothScrollingConfig;
import net.minecraft.client.gui.widget.ListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ListWidget.class)
public abstract class ListWidgetMixin {
    @Shadow
    protected float scrollAmount;
    @Shadow
    protected int mouseYStart;

    @Unique
    private double animationTimer = 0;
    @Unique
    private double scrollStartVelocity = 0;
    @Unique
    private boolean scrollSmooth = false;
    @Unique
    private float scrollBefore = 0;
    @Unique
    private long lastFrameNanos = 0;

    @Shadow
    public abstract int getMaxScroll();

    @Unique
    private void applyMotion(float delta) {
        this.scrollAmount += (float) (ScrollMath.scrollbarVelocity(this.animationTimer, this.scrollStartVelocity) * delta);
        this.animationTimer += delta * 10;
        if (!SmoothScrollingConfig.pushBack && (this.scrollAmount < 0 || this.scrollAmount > this.getMaxScroll())) {
            this.scrollStartVelocity = 0;
            this.animationTimer = 0;
        }
    }

    @Unique
    private void checkOutOfBounds(float delta) {
        if (this.scrollAmount < 0) {
            this.scrollAmount += (float) ScrollMath.pushBackStrength(Math.abs(this.scrollAmount), delta);
            if (this.scrollAmount > -0.2f) this.scrollAmount = 0;
        }
        int maxScroll = this.getMaxScroll();
        if (this.scrollAmount > maxScroll) {
            this.scrollAmount -= (float) ScrollMath.pushBackStrength(this.scrollAmount - maxScroll, delta);
            if (this.scrollAmount < maxScroll + 0.2f) this.scrollAmount = maxScroll;
        }
    }

    @Unique
    private float frameDelta() {
        long now = System.nanoTime();
        float delta = this.lastFrameNanos == 0L ? 0f : Math.min(2f, (now - this.lastFrameNanos) / 5.0e7f);
        this.lastFrameNanos = now;
        return delta;
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void manipulateScrollAmount(int mouseX, int mouseY, float tickDelta, CallbackInfo ci) {
        float delta = this.frameDelta();

        this.scrollSmooth = this.getMaxScroll() > 0;
        if (!this.scrollSmooth) {
            this.scrollStartVelocity = 0;
            this.animationTimer = 0;
            return;
        }

        if (SmoothScrollingConfig.pushBack) this.checkOutOfBounds(delta);

        if (Math.abs(ScrollMath.scrollbarVelocity(this.animationTimer, this.scrollStartVelocity)) < 1.0) return;
        this.applyMotion(delta);
    }

    @Inject(method = "capScrolling", at = @At("HEAD"), cancellable = true)
    private void allowOverscroll(CallbackInfo ci) {
        if (this.scrollSmooth && SmoothScrollingConfig.pushBack) ci.cancel();
    }

    @Inject(method = "handleMouse", at = @At("HEAD"))
    private void rememberScrollAmount(CallbackInfo ci) {
        this.scrollBefore = this.scrollAmount;
    }

    @Inject(method = "handleMouse", at = @At("RETURN"))
    private void setVelocity(CallbackInfo ci) {
        if (!this.scrollSmooth || this.scrollAmount == this.scrollBefore) return;

        if (this.mouseYStart >= 0) {
            this.scrollAmount = Math.clamp(this.scrollAmount, 0, this.getMaxScroll());
            this.scrollStartVelocity = 0;
            this.animationTimer = 0;
            return;
        }

        double diff = this.scrollAmount - this.scrollBefore;
        this.scrollAmount = this.scrollBefore;

        diff = Math.signum(diff) * Math.min(Math.abs(diff), 10);
        diff *= SmoothScrollingConfig.scrollStrength;
        if (Math.signum(diff) != Math.signum(this.scrollStartVelocity)) diff *= 2.5d;
        this.animationTimer *= 0.5;
        this.scrollStartVelocity = ScrollMath.scrollbarVelocity(this.animationTimer, this.scrollStartVelocity) + diff;
        this.animationTimer = 0;
    }
}
