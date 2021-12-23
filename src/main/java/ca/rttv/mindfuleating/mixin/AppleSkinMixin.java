package ca.rttv.mindfuleating.mixin;

import ca.rttv.mindfuleating.MindfulEating;
import ca.rttv.mindfuleating.access.HungerManagerDuck;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import squeek.appleskin.client.HUDOverlayHandler;

@Mixin(HUDOverlayHandler.class)
public abstract class AppleSkinMixin {

    private int saturationIndex;
    private int hungerIndex;

    @ModifyArg(method = "drawSaturationOverlay(Lnet/minecraft/client/util/math/MatrixStack;FFLnet/minecraft/client/MinecraftClient;IIF)V", at = @At(value = "INVOKE", target = "Ljava/util/Vector;get(I)Ljava/lang/Object;", ordinal = 0), index = 0)
    private int mirrorSaturation(int i) {
        this.saturationIndex = i;
        return i;
    }

    @ModifyArgs(method = "drawSaturationOverlay(Lnet/minecraft/client/util/math/MatrixStack;FFLnet/minecraft/client/MinecraftClient;IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"))
    private void drawSaturationOverlay(Args args) {
        RenderSystem.setShaderTexture(0, MindfulEating.SATURATION_ICONS);
        DrawableHelper.drawTexture(args.get(0), args.get(1), args.get(2), 422, (int) args.get(3) + 9, (int) args.get(4) + ((HungerManagerDuck)MinecraftClient.getInstance().player.getHungerManager()).getHungerIcon(saturationIndex), 9, 9, 54, 45);
        args.set(2, ((int) args.get(2)) + 128);
    }

    @ModifyArg(method = "drawHungerOverlay(Lnet/minecraft/client/util/math/MatrixStack;IILnet/minecraft/client/MinecraftClient;IIFZ)V", at = @At(value = "INVOKE", target = "Ljava/util/Vector;get(I)Ljava/lang/Object;", ordinal = 0), index = 0)
    private int mirrorHunger(int i) {
        this.hungerIndex = i;
        return i;
    }

    @ModifyArgs(
            method = "drawHungerOverlay(Lnet/minecraft/client/util/math/MatrixStack;IILnet/minecraft/client/MinecraftClient;IIFZ)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V")
    )
    private void drawHungerOverlay(Args args) {
        RenderSystem.setShaderTexture(0, MindfulEating.MINDFUL_EATING_ICONS);
        DrawableHelper.drawTexture(args.get(0), args.get(1), args.get(2), 420, (int) args.get(3) - 16, (int) args.get(4) - 27 + ((HungerManagerDuck) MinecraftClient.getInstance().player.getHungerManager()).getHungerIcon(hungerIndex), 9, 9, 126, 45);
        args.set(2, (int) args.get(2) + 128);
        RenderSystem.setShaderTexture(0, Screen.GUI_ICONS_TEXTURE);
    }
}
