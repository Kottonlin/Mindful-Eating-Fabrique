package ca.rttv.mindfuleating.mixin;

import ca.rttv.mindfuleating.ExhaustionType;
import ca.rttv.mindfuleating.FoodGroups;
import ca.rttv.mindfuleating.MindfulEating;
import ca.rttv.mindfuleating.configs.Configs;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin {

    // check for when hit to take less exhaustion
    @ModifyArg(method = "applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V"), index = 0)
    private float damage(float originalExhaustion) {
        return FoodGroups.shouldReceiveBuffs(ExhaustionType.HURT) ? originalExhaustion * (1 - Configs.getJsonObject().get("exhaustionReductionAsDecimal").getAsFloat()) : originalExhaustion;
    }

    // hitting check
    @ModifyArg(method = "attack(Lnet/minecraft/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V"), index = 0)
    private float attack(float originalExhaustion) {
        return FoodGroups.shouldReceiveBuffs(ExhaustionType.ATTACK) ? originalExhaustion * (1 - Configs.getJsonObject().get("exhaustionReductionAsDecimal").getAsFloat()) : originalExhaustion;
    }

    // jumping check
    @ModifyArg(method = "jump()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V"), index = 0)
    private float jumpWithSprint(float originalExhaustion) {
        return FoodGroups.shouldReceiveBuffs(ExhaustionType.JUMP) ? originalExhaustion * (1 - Configs.getJsonObject().get("exhaustionReductionAsDecimal").getAsFloat()) : originalExhaustion;
    }

    // slice is epic
    @ModifyArg(method = "increaseTravelMotionStats(DDD)V", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSwimming()Z"), to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isClimbing()Z")), at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V"))
    private float underwaterAddExhaustion(float originalExhaustion) {
        return FoodGroups.shouldReceiveBuffs(ExhaustionType.SWIMMING) ? originalExhaustion * (1 - Configs.getJsonObject().get("exhaustionReductionAsDecimal").getAsFloat()) : originalExhaustion;
    }

    // sounds cool also
    @ModifyArg(method = "increaseTravelMotionStats(DDD)V", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isClimbing()Z"), to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isFallFlying()Z")), at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V"))
    private float sprintingAddExhaustion(float originalExhaustion) {
        return FoodGroups.shouldReceiveBuffs(ExhaustionType.WALKING) ? originalExhaustion * (1 - Configs.getJsonObject().get("exhaustionReductionAsDecimal").getAsFloat()) : originalExhaustion;
    }
}
