package ca.rttv.mindfuleating.mixin;

import ca.rttv.mindfuleating.ExhaustionType;
import ca.rttv.mindfuleating.FoodGroups;
import ca.rttv.mindfuleating.MindfulEating;
import ca.rttv.mindfuleating.access.HungerManagerDuck;
import ca.rttv.mindfuleating.configs.Configs;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@SuppressWarnings( "PointlessBooleanExpression" )
@Mixin( HungerManager.class )
public abstract class HungerManagerMixin implements HungerManagerDuck {
   
   private Item  mostRecentFood;
   private int[] hungerIcons = new int[10]; // this is the types (y axis on the hunger_icons.png file) of icons which render at different positions
   
   @ModifyArgs( method = "update", at = @At( value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;addExhaustion(F)V" ) )
   private void add(Args args, PlayerEntity player) {
      PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer()); // sadly I cant directly make a packet byte buf object inside the send method :(
      packet.writeInt(ExhaustionType.HEAL.bonusSheenTicks);
      ServerPlayNetworking.send((ServerPlayerEntity) player, MindfulEating.MindfulEatingSheenS2CPacket, packet);
      args.set(0, FoodGroups.shouldReceiveBuffs(ExhaustionType.HEAL) ? (int) args.get(0) * (1 - Configs.getJsonObject().get("exhaustionReductionAsDecimal").getAsFloat()) : args.get(0));
   }
   
   @Inject( method = "eat", at = @At( "HEAD" ) )
   private void eat(Item item, ItemStack stack, CallbackInfo ci) {
      this.mostRecentFood = item;
      setHungerIcons(generateHungerIcons(item));
   }
   
   @Override
   public void setHungerIcons(int[] hungerIcons) {
      this.hungerIcons = hungerIcons;
   }
   
   @Override
   public int[] generateHungerIcons(Item item) {
      int[] hungerIcons = new int[10];
       if (MindfulEating.isOnMEServer == false || FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
           return hungerIcons;
       }
      //        if (MindfulEating.client.player != null) {
      if (FoodGroups.fruits.contains(item) == false && FoodGroups.grains.contains(item) == false && FoodGroups.proteins.contains(item) == false && FoodGroups.sugars.contains(item) == false && FoodGroups.vegetables.contains(item) == false) {
          if (item != Items.AIR) {
              MindfulEating.Logger.warn("Your most recent food is not under the Mindful Eating food lists, if this is a modded food please add it to the custom list via a datapack");
          }
         return hungerIcons;
      }
      int i = 0;
      for (; ; ) {
         if (FoodGroups.fruits.contains(item)) {
            hungerIcons[i++] = 9;
             if (i > hungerIcons.length - 1) {
                 break;
             }
         }
         if (FoodGroups.grains.contains(item)) {
            hungerIcons[i++] = 27;
             if (i > hungerIcons.length - 1) {
                 break;
             }
         }
         if (FoodGroups.proteins.contains(item)) {
            hungerIcons[i++] = 0;
             if (i > hungerIcons.length - 1) {
                 break;
             }
         }
         if (FoodGroups.sugars.contains(item)) {
            hungerIcons[i++] = 36;
             if (i > hungerIcons.length - 1) {
                 break;
             }
         }
         if (FoodGroups.vegetables.contains(item)) {
            hungerIcons[i++] = 18;
             if (i > hungerIcons.length - 1) {
                 break;
             }
         }
      }
      //        }
      return hungerIcons;
   }
   
   @Override
   public int getHungerIcon(int y) {
      return this.hungerIcons[y];
   }
   
   @Override
   public Item mostRecentFood() {
      return this.mostRecentFood;
   }
   
   @Override
   public void mostRecentFood(Item mostRecentFood) {
      this.mostRecentFood = mostRecentFood;
   }
   
   @Inject( method = "readNbt", at = @At( "HEAD" ) )
   private void readNbt(NbtCompound nbt, CallbackInfo ci) {
      this.mostRecentFood = Registry.ITEM.get(new Identifier(nbt.getString("mostRecentFood")));
   }
   
   @Inject( method = "writeNbt", at = @At( "HEAD" ) )
   private void writeNbt(NbtCompound nbt, CallbackInfo ci) {
      nbt.putString("mostRecentFood", Registry.ITEM.getId(this.mostRecentFood).toString());
   }
}
