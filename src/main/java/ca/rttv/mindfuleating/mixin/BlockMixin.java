package ca.rttv.mindfuleating.mixin;

import ca.rttv.mindfuleating.ExhaustionType;
import ca.rttv.mindfuleating.FoodGroups;
import ca.rttv.mindfuleating.MindfulEating;
import ca.rttv.mindfuleating.configs.Configs;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin( Block.class )
abstract class BlockMixin {
   @ModifyArgs( method = "afterBreak", at = @At( value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V" ) )
   public void afterBreak(Args args, World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
      PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
      packet.writeInt(ExhaustionType.DESTROY.bonusSheenTicks);
      ServerPlayNetworking.send((ServerPlayerEntity) player, MindfulEating.MindfulEatingSheenS2CPacket, packet);
      args.set(0, FoodGroups.shouldReceiveBuffs(ExhaustionType.DESTROY) ? (float) args.get(0) * (1 - Configs.getJsonObject().get("exhaustionReductionAsDecimal").getAsFloat()) : args.get(0));
   }
}
