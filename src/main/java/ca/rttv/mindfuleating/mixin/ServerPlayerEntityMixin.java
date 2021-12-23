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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
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

    /**
     * <p>so all of this complicated jsonArrayToPacketString
     * is a simple system where since I can't send string
     * arrays through packets I use a delimeter
     * ( {@code ::} because :'s cant be used in item ids cuz {@code minecraft:} )
     * so I just make rally long strings and format them on both sides
     */

    @Inject(method = "playerTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 1))
    private void playerTick(CallbackInfo ci) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer()); // makes an empty packet
        NbtCompound nbt = new NbtCompound();
     // noinspection RedundantCast
        ((ServerPlayerEntity) (Object) this).writeNbt(nbt); // this actually writes the players playerdata to our nbtcompound, not let us write to this's nbt file, confusing right?

        // this is fine
        for (int i = 0; i < Configs.getJsonObject().get("stackSize").getAsJsonArray().size(); i++)
            ((IItemAccessor) Registry.ITEM.get(new Identifier(Configs.getJsonObject().get("stackSize").getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString()))).setMaxCount(Configs.getJsonObject().get("stackSize").getAsJsonArray().get(i).getAsJsonObject().get("value").getAsInt());

        for (int i = 0; i < Configs.getJsonObject().get("speedy").getAsJsonArray().size(); i++)
            ((IFoodComponentAccessor)Registry.ITEM.get(new Identifier(Configs.getJsonObject().get("speedy").getAsJsonArray().get(i).getAsString())).getFoodComponent()).setSnack(true);

        for (int i = 0; i < Configs.getJsonObject().get("saturationModifier").getAsJsonArray().size(); i++)
            ((IFoodComponentAccessor) Registry.ITEM.get(new Identifier(Configs.getJsonObject().get("saturationModifier").getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString())).getFoodComponent()).setSaturationModifier(Configs.getJsonObject().get("saturationModifier").getAsJsonArray().get(i).getAsJsonObject().get("value").getAsFloat());


        for (int i = 0; i < Configs.getJsonObject().get("hunger").getAsJsonArray().size(); i++)
            ((IFoodComponentAccessor) Registry.ITEM.get(new Identifier(Configs.getJsonObject().get("hunger").getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString())).getFoodComponent()).setSaturationModifier(Configs.getJsonObject().get("hunger").getAsJsonArray().get(i).getAsJsonObject().get("value").getAsInt());

        packet.writeString(nbt.getString("mostRecentFood")); // adds a string to the packet // 1st

        packet.writeString(MindfulEating.jsonArrayToPacketString(Configs.getJsonObject().get("stackSize").getAsJsonArray(), "name", "string")); // 2nd
        packet.writeString(MindfulEating.jsonArrayToPacketString(Configs.getJsonObject().get("stackSize").getAsJsonArray(), "value", "int")); // 3rd

        packet.writeString(MindfulEating.jsonArrayToPacketString(Configs.getJsonObject().get("speedy").getAsJsonArray(), "string")); // 4th

        packet.writeString(MindfulEating.jsonArrayToPacketString(Configs.getJsonObject().get("saturationModifier").getAsJsonArray(), "name", "string")); // 5th
        packet.writeString(MindfulEating.jsonArrayToPacketString(Configs.getJsonObject().get("saturationModifier").getAsJsonArray(), "value", "int")); // 6th

        packet.writeString(MindfulEating.jsonArrayToPacketString(Configs.getJsonObject().get("hunger").getAsJsonArray(), "name", "string")); // 7th
        packet.writeString(MindfulEating.jsonArrayToPacketString(Configs.getJsonObject().get("hunger").getAsJsonArray(), "value", "int")); // 8th

        packet.writeString(MindfulEating.jsonArrayToPacketString(Configs.getJsonObject().get("alwaysEdible").getAsJsonArray(), "string")); // 9th
         ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, MindfulEating.MindfulEatingDataS2CPacket, packet); // sends a packet
    }
}
