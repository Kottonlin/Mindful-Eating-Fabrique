package ca.rttv.mindfuleating.mixin;

import ca.rttv.mindfuleating.ExhaustionType;
import ca.rttv.mindfuleating.FoodGroups;
import ca.rttv.mindfuleating.MindfulEating;
import ca.rttv.mindfuleating.access.HungerManagerDuck;
import ca.rttv.mindfuleating.configs.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("PointlessBooleanExpression")
@Mixin(HungerManager.class)
abstract class HungerManagerMixin implements HungerManagerDuck {

    private Item mostRecentFood;
    private int[] hungerIcons = new int[10]; // this is the types (y axis on the hunger_icons.png file) of icons which render at different positions
    private final MinecraftClient client = MinecraftClient.getInstance();

    @ModifyArg(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;addExhaustion(F)V"), index = 0)
    private float add(float originalExhaustion) {
        return FoodGroups.shouldReceiveBuffs(ExhaustionType.HEAL) ? originalExhaustion * (1 - Configs.getJsonObject().get("exhaustionReductionAsDecimal").getAsFloat()) : originalExhaustion;
    }

    @Inject(method = "eat", at = @At("HEAD"))
    private void eat(Item item, ItemStack stack, CallbackInfo ci) {
        this.mostRecentFood = item;
        setHungerIcons(generateHungerIcons(item));
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    private void readNbt(NbtCompound nbt, CallbackInfo ci) {
        this.mostRecentFood = Registry.ITEM.get(new Identifier(nbt.getString("mostRecentFood")));
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    private void writeNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putString("mostRecentFood", Registry.ITEM.getId(this.mostRecentFood).toString());
    }

    @Override
    public void mostRecentFood(Item mostRecentFood) {
        this.mostRecentFood = mostRecentFood;
    }

    @Override
    public Item mostRecentFood() {
        return this.mostRecentFood;
    }

    @Override
    public int getHungerIcon(int y) {
        return this.hungerIcons[y];
    }

    @Override
    public void setHungerIcons(int[] hungerIcons) {
        this.hungerIcons = hungerIcons;
    }

    @Override
    public int[] generateHungerIcons(Item item) {
        int[] hungerIcons = new int[10];
        if (MindfulEating.inOnMEServer == false) return hungerIcons;
        if (this.client.player != null) {
            if (FoodGroups.fruits.contains(item) == false &&
                FoodGroups.grains.contains(item) == false &&
                FoodGroups.proteins.contains(item) == false &&
                FoodGroups.sugars.contains(item) == false &&
                FoodGroups.vegetables.contains(item) == false) {
                if (item != Items.AIR)
                this.client.player.sendMessage(Text.of("§cYour most recent food is not under the Mindful Eating food lists, if this is a modded food please add it to the custom list via a datapack"), false);
                return hungerIcons;
            }
            int i = 0;
                for (;;) {
                    if (FoodGroups.fruits.contains(item)) {
                        hungerIcons[i++] = 9;
                        if (i > hungerIcons.length - 1) break;
                    }
                    if (FoodGroups.grains.contains(item)) {
                        hungerIcons[i++] = 27;
                        if (i > hungerIcons.length - 1) break;
                    }
                    if (FoodGroups.proteins.contains(item)) {
                        hungerIcons[i++] = 0;
                        if (i > hungerIcons.length - 1) break;
                    }
                    if (FoodGroups.sugars.contains(item)) {
                        hungerIcons[i++] = 36;
                        if (i > hungerIcons.length - 1) break;
                    }
                    if (FoodGroups.vegetables.contains(item)) {
                        hungerIcons[i++] = 18;
                        if (i > hungerIcons.length - 1) break;
                    }
            }
        }
        return hungerIcons;
    }
}
