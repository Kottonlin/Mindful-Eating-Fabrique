package ca.rttv.mindfuleating.mixin;

import ca.rttv.mindfuleating.MindfulEating;
import ca.rttv.mindfuleating.access.HungerManagerDuck;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import squeek.appleskin.client.HUDOverlayHandler;

@Mixin( HUDOverlayHandler.class )
public abstract class HUDOverlayHandlerMixin {
   
   private final MinecraftClient client       = MinecraftClient.getInstance();
   private       int             saturationIndex;
   private       int             hungerIndex;
   private       int[]           hungerIcons  = new int[10];
   private       Item            previousItem = Items.AIR;
   private       boolean         hasAlpha;
   
   @ModifyArgs( method = "drawHungerOverlay(Lnet/minecraft/client/util/math/MatrixStack;IILnet/minecraft/client/MinecraftClient;IIFZ)V", at = @At( value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V" ) )
   private void drawHungerOverlay(Args args, MatrixStack matrixStack, int hungerRestored, int foodLevel, MinecraftClient mc, int right, int top, float alpha, boolean useRottenTextures) {
      RenderSystem.setShaderTexture(0, MindfulEating.MINDFUL_EATING_ICONS);
      DrawableHelper.drawTexture(matrixStack, args.get(1), args.get(2), 420, (int) args.get(3) - 16, (int) args.get(4) - 27 + hungerIcons[hungerIndex], 9, 9, 126, 45);
      args.set(2, (int) args.get(2) + 128);
      RenderSystem.setShaderTexture(0, Screen.GUI_ICONS_TEXTURE);
   }
   
   @ModifyArgs( method = "drawSaturationOverlay(Lnet/minecraft/client/util/math/MatrixStack;FFLnet/minecraft/client/MinecraftClient;IIF)V", at = @At( value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V" ) )
   private void drawSaturationOverlay(Args args) {
      RenderSystem.setShaderTexture(0, MindfulEating.SATURATION_ICONS);
      if (this.client.player != null) {
         DrawableHelper.drawTexture(args.get(0), args.get(1), args.get(2), 422, (int) args.get(3) + 9, (int) args.get(4) + (hasAlpha ? ((HungerManagerDuck) this.client.player.getHungerManager()).getHungerIcon(saturationIndex) : hungerIcons[saturationIndex]), 9, 9, 54, 45);
      }
      args.set(2, (int) args.get(2) + 128);
   }
   
   @ModifyArg( method = "onRender", at = @At( value = "INVOKE", target = "Lsqueek/appleskin/helpers/FoodHelper;isRotten(Lnet/minecraft/item/ItemStack;)Z" ), index = 0 )
   private ItemStack getItem(ItemStack itemStack) {
       if (itemStack.getItem() != previousItem) {
          if (MinecraftClient.getInstance().player != null) {
             hungerIcons = ((HungerManagerDuck) MinecraftClient.getInstance().player.getHungerManager()).generateHungerIcons(itemStack.getItem());
          }
       }
      previousItem = itemStack.getItem();
      return itemStack;
   }
   
   @ModifyArg( method = "drawSaturationOverlay(Lnet/minecraft/client/util/math/MatrixStack;FFLnet/minecraft/client/MinecraftClient;IIF)V", at = @At( value = "INVOKE", target = "Lsqueek/appleskin/client/HUDOverlayHandler;enableAlpha(F)V", ordinal = 0 ), index = 0 )
   private float mirrorAlpha(float alpha) {
      hasAlpha = alpha == 1.0f;
      return alpha;
   }
   
   @ModifyArg( method = "drawHungerOverlay(Lnet/minecraft/client/util/math/MatrixStack;IILnet/minecraft/client/MinecraftClient;IIFZ)V", at = @At( value = "INVOKE", target = "Ljava/util/Vector;get(I)Ljava/lang/Object;", ordinal = 0 ), index = 0 )
   private int mirrorHunger(int i) {
      this.hungerIndex = i;
      return i;
   }
   
   @ModifyArg( method = "drawSaturationOverlay(Lnet/minecraft/client/util/math/MatrixStack;FFLnet/minecraft/client/MinecraftClient;IIF)V", at = @At( value = "INVOKE", target = "Ljava/util/Vector;get(I)Ljava/lang/Object;", ordinal = 0 ), index = 0 )
   private int mirrorSaturation(int i) {
      this.saturationIndex = i;
      return i;
   }
}
