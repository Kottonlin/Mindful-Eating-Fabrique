package ca.rttv.mindfuleating.mixin;

import ca.rttv.mindfuleating.MindfulEating;
import ca.rttv.mindfuleating.access.HungerManagerDuck;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * <p>ik my code sucks im lllleeeeeaaaaarrrrrnnnnniiiiinnnnnggggg,
 * learning is spelt l e a r n i n g
 *
 * <p>{@code learn·ing}
 * <p>/ˈlərniNG/
 * <p>noun
 *
 * <p>the acquisition of knowledge or skills through experience, study, or by being taught.
 * <p>"these children experienced difficulties in learning"
 *
 * <p>please do not
 * tell me about my useless slices and variables, no I would
 * not like to hear about how to make this better give me a pr
 */

@Mixin( value = InGameHud.class, priority = 2147483647 )
abstract class InGameHudMixin {
   // ive put a lot of effort in to make this change which occurs EVERY FRAME as minimal to the cpu and gpu as possible
   
   @Shadow
   @Final
   private MinecraftClient client;
   
   private int y;
   
   // yes I am aware that I can use an inject but it didn't start off as one and im not gonna fix this it works ok
   @ModifyArgs( method = "renderStatusBars", at = @At( value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V" ), slice = @Slice( from = @At( value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V" ), to = @At( value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getMaxAir()I" ) ) )
   private void modifyHungerBar(Args args, MatrixStack matrices) {
      // this is a simple mixin which occurs every time the hunger background or foreground is drawn.
      if (MindfulEating.isOnMEServer && this.client.player != null) {
         if (MindfulEating.shouldHaveSheen >= 0 && MindfulEating.sheen[9 - y]) {
            RenderSystem.setShaderTexture(0, MindfulEating.NOURISHED_ICONS);
         } else {
            RenderSystem.setShaderTexture(0, MindfulEating.MINDFUL_EATING_ICONS);
         }
         DrawableHelper.drawTexture(matrices, args.get(1), args.get(2), 420, (Integer) args.get(3) - 16, (Integer) args.get(4) - 27 + ((HungerManagerDuck) this.client.player.getHungerManager()).getHungerIcon(y), 9, 9, 126, 45);
         RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
         
         args.set(2, (Integer) args.get(2) + 128); // I know its messy but I can't think of anything better don't judge me (throws the original offscreen)
      }
   }
   
   // this y variable is mirroring the y variable in the hunger drawing system so I can see what index im at for what icon tye to draw
   @ModifyVariable( method = "renderStatusBars", at = @At( value = "FIELD", target = "Lnet/minecraft/entity/effect/StatusEffects;HUNGER:Lnet/minecraft/entity/effect/StatusEffect;", ordinal = 0, opcode = Opcodes.GETSTATIC ), ordinal = 14 )
   private int modifyY(int y) {
      this.y = y;
      return y;
   }
}