package ca.rttv.mindfuleating.mixin;

import ca.rttv.mindfuleating.MindfulEating;
import ca.rttv.mindfuleating.configs.Configs;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
abstract class ServerPlayerEntityMixin extends PlayerEntity {

    private ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Shadow
    public abstract void sendMessage(Text message, boolean actionBar);

    @Inject(method = "playerTick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 1))
    private void playerTick(CallbackInfo ci) {
        Configs.loadConfigs();
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer()); // makes an empty packet
        NbtCompound nbt = new NbtCompound();
        //noinspection RedundantCast
        ((ServerPlayerEntity) (Object) this).writeNbt(nbt); // this actually writes this's playerdata to our nbtcompound, not let us write to this's nbt file, confusing right?
        packet.writeString(nbt.getString("mostRecentFood")); // adds a string to the packet // 1st

        packet.writeString(MindfulEating.jsonArrayToPacketString(Configs.getJsonObject().get("stackSize").getAsJsonArray(), "name", "string")); // adds a stack // 2nd
        packet.writeString(MindfulEating.jsonArrayToPacketString(Configs.getJsonObject().get("stackSize").getAsJsonArray(), "value", "int")); // 3rd

        packet.writeString(MindfulEating.jsonArrayToPacketString(Configs.getJsonObject().get("speedy").getAsJsonArray(), "string")); // 4th

        packet.writeString(MindfulEating.jsonArrayToPacketString(Configs.getJsonObject().get("saturationModifier").getAsJsonArray(), "name", "string")); // 5th
        packet.writeString(MindfulEating.jsonArrayToPacketString(Configs.getJsonObject().get("saturationModifier").getAsJsonArray(), "value", "int")); // 6th

        packet.writeString(MindfulEating.jsonArrayToPacketString(Configs.getJsonObject().get("hunger").getAsJsonArray(), "name", "string")); // 7th
        packet.writeString(MindfulEating.jsonArrayToPacketString(Configs.getJsonObject().get("hunger").getAsJsonArray(), "value", "int")); // 8th
        ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, MindfulEating.MindfulEatingDataS2CPacket, packet); // sends a packet
    }
}
