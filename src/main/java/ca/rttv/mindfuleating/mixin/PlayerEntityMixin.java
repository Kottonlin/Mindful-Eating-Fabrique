package ca.rttv.mindfuleating.mixin;

import ca.rttv.mindfuleating.ExhaustionType;
import ca.rttv.mindfuleating.FoodGroups;
import ca.rttv.mindfuleating.MindfulEating;
import ca.rttv.mindfuleating.configs.Configs;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin( PlayerEntity.class )
abstract class PlayerEntityMixin {
   
   PlayerEntity player = (PlayerEntity) (Object) this;
   
   // hitting check
   @ModifyArg( method = "attack", at = @At( value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V" ), index = 0 )
   private float attack(float originalExhaustion) {
      PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
      packet.writeInt(ExhaustionType.ATTACK.bonusSheenTicks);
      ServerPlayNetworking.send((ServerPlayerEntity) player, MindfulEating.MindfulEatingSheenS2CPacket, packet);
      return FoodGroups.shouldReceiveBuffs(ExhaustionType.ATTACK) ? originalExhaustion * (1 - Configs.getJsonObject().get("exhaustionReductionAsDecimal").getAsFloat()) : originalExhaustion;
   }
   
   // check for when hit to take less exhaustion
   @ModifyArg( method = "applyDamage", at = @At( value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V" ), index = 0 )
   private float damage(float originalExhaustion) {
      PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
      packet.writeInt(ExhaustionType.HURT.bonusSheenTicks);
      ServerPlayNetworking.send((ServerPlayerEntity) player, MindfulEating.MindfulEatingSheenS2CPacket, packet);
      return FoodGroups.shouldReceiveBuffs(ExhaustionType.HURT) ? originalExhaustion * (1 - Configs.getJsonObject().get("exhaustionReductionAsDecimal").getAsFloat()) : originalExhaustion;
   }
   
   // jumping check
   @ModifyArg( method = "jump", at = @At( value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V" ), index = 0 )
   private float jumpWithSprint(float originalExhaustion) {
      return FoodGroups.shouldReceiveBuffs(ExhaustionType.JUMP) ? originalExhaustion * (1 - Configs.getJsonObject().get("exhaustionReductionAsDecimal").getAsFloat()) : originalExhaustion;
   }
   
   // this is walking
   @ModifyArg( method = "increaseTravelMotionStats", slice = @Slice( from = @At( value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isClimbing()Z" ), to = @At( value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isFallFlying()Z" ) ), at = @At( value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V" ) )
   private float sprintingAddExhaustion(float originalExhaustion) {
      return FoodGroups.shouldReceiveBuffs(ExhaustionType.WALKING) ? originalExhaustion * (1 - Configs.getJsonObject().get("exhaustionReductionAsDecimal").getAsFloat()) : originalExhaustion;
   }
   
   // slice is epic (I had to use a slice to get a slice of the exhaustion system for water and air), this is water
   @ModifyArg( method = "increaseTravelMotionStats", slice = @Slice( from = @At( value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSwimming()Z" ), to = @At( value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isClimbing()Z" ) ), at = @At( value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V" ) )
   private float underwaterAddExhaustion(float originalExhaustion) {
      return FoodGroups.shouldReceiveBuffs(ExhaustionType.SWIMMING) ? originalExhaustion * (1 - Configs.getJsonObject().get("exhaustionReductionAsDecimal").getAsFloat()) : originalExhaustion;
   }
}