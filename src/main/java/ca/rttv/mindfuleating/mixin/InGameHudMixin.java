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

@Mixin(InGameHud.class)
abstract class InGameHudMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    private int y;

    @ModifyVariable(
            method = "renderStatusBars",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/effect/StatusEffects;HUNGER:Lnet/minecraft/entity/effect/StatusEffect;", ordinal = 0, opcode = Opcodes.GETSTATIC),
            ordinal = 14
    )
    private int modifyY(int y) {
        this.y = y;
        return y;
    }

    // yes I am aware that I can use an inject but it didn't start off as one and im not gonna fix this it works ok
    @ModifyArgs(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V"), to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getMaxAir()I")))
    private void modifyHungerBar(Args args) {
        // applying this on every render makes it so I don't have to do checks for half hunger and hunger effects myself or override a lot of code.
        MatrixStack matrices = args.get(0);
        int screenX = args.get(1);
        int screenY = args.get(2);
        int textureX = (int) args.get(3) - 16;
        int textureY = (int) args.get(4) - 27;
        int textureWidth = 9;
        int textureHeight = 9;

        args.set(2, (int) args.get(2) + 128); // I know its messy but I can't think of anything better don't judge me (throws the original offscreen)

        // its actually great that the template that is used has the same icons formation (x axis) so I don't need to have cases for hunger effects and halfs
        textureY += ((HungerManagerDuck) this.client.player.getHungerManager()).getHungerIcon(y);
        if (MindfulEating.sheen[9 - y] && MindfulEating.shouldHaveSheen >= 0) {
            RenderSystem.setShaderTexture(0, MindfulEating.NOURISHED_ICONS);
            DrawableHelper.drawTexture(matrices, screenX, screenY, 420, textureX, textureY, textureWidth, textureHeight, 126 /* this is the mindful eating icons png stuff*/, 45);
        } else {
            RenderSystem.setShaderTexture(0, MindfulEating.MINDFUL_EATING_ICONS);
            DrawableHelper.drawTexture(matrices, screenX, screenY, 420, textureX, textureY, textureWidth, textureHeight, 126 /* this is the mindful eating icons png stuff*/, 45);
        }
        RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
//        this.client.player.sendMessage(Text.of(String.valueOf(this.y)), false);
    }
}
