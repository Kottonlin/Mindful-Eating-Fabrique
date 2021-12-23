package ca.rttv.mindfuleating.mixin;

import ca.rttv.mindfuleating.ExhaustionType;
import ca.rttv.mindfuleating.FoodGroups;
import ca.rttv.mindfuleating.configs.Configs;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Block.class)
abstract class BlockMixin {
    @ModifyArg(method = "afterBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V"), index = 0)
    public float afterBreak(float originalExhaustion) {
        return FoodGroups.shouldReceiveBuffs(ExhaustionType.DESTROY) ? originalExhaustion * (1 - Configs.getJsonObject().get("exhaustionReductionAsDecimal").getAsFloat()) : originalExhaustion;
    }
}
