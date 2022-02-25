package ca.rttv.mindfuleating.mixin;

import ca.rttv.mindfuleating.MindfulEating;
import ca.rttv.mindfuleating.access.HungerManagerDuck;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import squeek.appleskin.client.TooltipOverlayHandler;

@Mixin( TooltipOverlayHandler.class )
public abstract class TooltipOverlayHandlerMixin {
   
   private int[] hungerIcons = new int[10];
   private int   hungerIndex;
   private int   saturationIndex;
   private Item  previousItem;
   
   @ModifyVariable( method = "onRenderTooltip", at = @At( value = "FIELD", target = "Lsqueek/appleskin/client/TooltipOverlayHandler$FoodOverlay;hungerBars:I", opcode = Opcodes.GETFIELD, ordinal = 1 ), ordinal = 7 )
   private int getIndex(int i) {
      this.hungerIndex = i / 2;
      return i;
   }
   
   @ModifyArg( method = "onRenderTooltip", at = @At( value = "INVOKE", target = "Lsqueek/appleskin/helpers/FoodHelper;isRotten(Lnet/minecraft/item/ItemStack;)Z" ), index = 0 )
   private ItemStack getItem(ItemStack itemStack) {
      if (itemStack.getItem() != previousItem && MinecraftClient.getInstance().player != null) {
         hungerIcons = ((HungerManagerDuck) MinecraftClient.getInstance().player.getHungerManager()).generateHungerIcons(itemStack.getItem());
      }
      previousItem = itemStack.getItem();
      return itemStack;
   }
   
   @ModifyVariable( method = "onRenderTooltip", at = @At( value = "FIELD", target = "Lsqueek/appleskin/client/TooltipOverlayHandler$FoodOverlay;saturationBars:I", opcode = Opcodes.GETFIELD, ordinal = 1 ), ordinal = 7 )
   private int mirrorSaturationIndex(int i) {
      this.saturationIndex = i / 2;
      return i;
   }
   
   @ModifyArgs( method = "onRenderTooltip", at = @At( value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawableHelper;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIFFIIII)V" ) )
   private void onRenderTooltip(Args args) {
      if ((int) args.get(6) == 9 && (int) args.get(7) == 9) {
         RenderSystem.setShaderTexture(0, MindfulEating.MINDFUL_EATING_ICONS);
         args.set(4, (float) args.get(4) - 16.0f);
         args.set(5, (float) args.get(5) - 27.0f + (float) hungerIcons[hungerIndex]);
         args.set(8, 126);
         args.set(9, 45);
      } else if ((int) args.get(6) == 7 && (int) args.get(7) == 7) {
         RenderSystem.setShaderTexture(0, MindfulEating.SMALL_SATURATION_ICONS);
         args.set(5, (float) args.get(5) - 27.0f + (float) (hungerIcons[saturationIndex] / 9 * 7));
         args.set(8, 35);
         args.set(9, 35);
      }
   }
}
