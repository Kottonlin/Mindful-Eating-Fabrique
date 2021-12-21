package ca.rttv.mindfuleating;

import ca.rttv.mindfuleating.access.HungerManagerDuck;
import ca.rttv.mindfuleating.configs.Configs;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class FoodGroups {
    public static Tag<Item> fruits = TagFactory.ITEM.create(new Identifier("mindfuleating", "fruits"));
    public static Tag<Item> grains = TagFactory.ITEM.create(new Identifier("mindfuleating", "grains"));
    public static Tag<Item> proteins = TagFactory.ITEM.create(new Identifier("mindfuleating", "proteins"));
    public static Tag<Item> sugars = TagFactory.ITEM.create(new Identifier("mindfuleating", "sugars"));
    public static Tag<Item> vegetables = TagFactory.ITEM.create(new Identifier("mindfuleating", "vegetables"));
    public static Object[][] exhaustionGroups = new Object[7][2];
    static MinecraftClient mc = MinecraftClient.getInstance();

    public static boolean shouldReceiveBuffs(ExhaustionType type) {
        Item mostRecentFood = ((HungerManagerDuck) mc.player.getHungerManager()).mostRecentFood();
        for (Object[] exhaustionGroup : exhaustionGroups) {
            if (exhaustionGroup[0].equals(type)) {
                for (int i = 0; i < ((ArrayList<?>) exhaustionGroup[1]).size(); i++) {
                    switch ((FoodGroup) ((ArrayList<?>) exhaustionGroup[1]).get(i)) {
                        case FRUITS -> {
                            if (fruits.contains(mostRecentFood)) {
                                MindfulEating.shouldHaveSheen = 60;
                                return true;
                            }
                        }
                        case GRAINS -> {
                            if (grains.contains(mostRecentFood)) {
                                MindfulEating.shouldHaveSheen = 60;
                                return true;
                            }
                        }
                        case PROTEINS -> {
                            if (proteins.contains(mostRecentFood)) {
                                MindfulEating.shouldHaveSheen = 60;
                                return true;
                            }
                        }
                        case SUGARS -> {
                            if (sugars.contains(mostRecentFood)) {
                                MindfulEating.shouldHaveSheen = 60;
                                return true;
                            }
                        }
                        case VEGETABLES -> {
                            if (vegetables.contains(mostRecentFood)) {
                                MindfulEating.shouldHaveSheen = 60;
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void registerFoodGroups() {
//        fruits.removeAll(fruits);
//        grains.removeAll(grains);
//        proteins.removeAll(proteins);
//        sugars.removeAll(sugars);
//        vegetables.removeAll(vegetables);
//
//        fruits.add(Items.APPLE);
//        fruits.add(Items.CHORUS_FRUIT);
//        fruits.add(Items.ENCHANTED_GOLDEN_APPLE);
//        fruits.add(Items.GOLDEN_APPLE);
//        fruits.add(Items.SWEET_BERRIES);
//        fruits.add(Items.GLOW_BERRIES);
//        grains.add(Items.BREAD);
//        grains.add(Items.COOKIE);
//        grains.add(Items.ENCHANTED_GOLDEN_APPLE);
//        grains.add(Items.CAKE);
//        proteins.add(Items.BEEF);
//        proteins.add(Items.CHICKEN);
//        proteins.add(Items.COD);
//        proteins.add(Items.COOKED_BEEF);
//        proteins.add(Items.COOKED_CHICKEN);
//        proteins.add(Items.COOKED_COD);
//        proteins.add(Items.COOKED_MUTTON);
//        proteins.add(Items.COOKED_PORKCHOP);
//        proteins.add(Items.COOKED_RABBIT);
//        proteins.add(Items.COOKED_SALMON);
//        proteins.add(Items.ENCHANTED_GOLDEN_APPLE);
//        proteins.add(Items.MUTTON);
//        proteins.add(Items.PORKCHOP);
//        proteins.add(Items.PUFFERFISH);
//        proteins.add(Items.PUMPKIN_PIE);
//        proteins.add(Items.ROTTEN_FLESH);
//        proteins.add(Items.SALMON);
//        proteins.add(Items.SPIDER_EYE);
//        proteins.add(Items.TROPICAL_FISH);
//        proteins.add(Items.CAKE);
//        sugars.add(Items.COOKIE);
//        sugars.add(Items.ENCHANTED_GOLDEN_APPLE);
//        sugars.add(Items.GOLDEN_APPLE);
//        sugars.add(Items.GOLDEN_CARROT);
//        sugars.add(Items.HONEY_BOTTLE);
//        sugars.add(Items.PUMPKIN_PIE);
//        sugars.add(Items.CAKE);
//        vegetables.add(Items.BAKED_POTATO);
//        vegetables.add(Items.BEETROOT);
//        vegetables.add(Items.BEETROOT_SOUP);
//        vegetables.add(Items.CARROT);
//        vegetables.add(Items.DRIED_KELP);
//        vegetables.add(Items.ENCHANTED_GOLDEN_APPLE);
//        vegetables.add(Items.GOLDEN_CARROT);
//        vegetables.add(Items.MELON_SLICE);
//        vegetables.add(Items.MUSHROOM_STEW);
//        vegetables.add(Items.POISONOUS_POTATO);
//        vegetables.add(Items.POTATO);
//        vegetables.add(Items.PUMPKIN_PIE);
//        vegetables.add(Items.SUSPICIOUS_STEW);
    }

    public static void registerExhaustionGroups() {
        exhaustionGroups[0][0] = ExhaustionType.SWIMMING;
        String[] sSwim = Configs.getJsonObject().get("foodGroups").getAsJsonObject().get("swim").getAsString().split("/");
        ArrayList<FoodGroup> aSwim = new ArrayList<>();
        for (String s : sSwim) aSwim.add(FoodGroup.stringToFoodGroup(s));
        exhaustionGroups[0][1] = aSwim;

        exhaustionGroups[1][0] = ExhaustionType.DESTROY;
        String[] sDestroy = Configs.getJsonObject().get("foodGroups").getAsJsonObject().get("destroy").getAsString().split("/");
        ArrayList<FoodGroup> aDestroy = new ArrayList<>();
        for (String s : sDestroy) aDestroy.add(FoodGroup.stringToFoodGroup(s));
        exhaustionGroups[1][1] = aDestroy;

        exhaustionGroups[2][0] = ExhaustionType.ATTACK;
        String[] sAttack = Configs.getJsonObject().get("foodGroups").getAsJsonObject().get("attack").getAsString().split("/");
        ArrayList<FoodGroup> aAttack = new ArrayList<>();
        for (String s : sAttack) aAttack.add(FoodGroup.stringToFoodGroup(s));
        exhaustionGroups[2][1] = aAttack;

        exhaustionGroups[3][0] = ExhaustionType.HURT;
        String[] sHurt = Configs.getJsonObject().get("foodGroups").getAsJsonObject().get("hurt").getAsString().split("/");
        ArrayList<FoodGroup> aHurt = new ArrayList<>();
        for (String s : sHurt) aHurt.add(FoodGroup.stringToFoodGroup(s));
        exhaustionGroups[3][1] = aHurt;

        exhaustionGroups[4][0] = ExhaustionType.HEAL;
        String[] sHeal = Configs.getJsonObject().get("foodGroups").getAsJsonObject().get("heal").getAsString().split("/");
        ArrayList<FoodGroup> aHeal = new ArrayList<>();
        for (String s : sHeal) aHeal.add(FoodGroup.stringToFoodGroup(s));
        exhaustionGroups[4][1] = aHeal;

        exhaustionGroups[5][0] = ExhaustionType.JUMP;
        String[] sJump = Configs.getJsonObject().get("foodGroups").getAsJsonObject().get("jump").getAsString().split("/");
        ArrayList<FoodGroup> aJump = new ArrayList<>();
        for (String s : sJump) aJump.add(FoodGroup.stringToFoodGroup(s));
        exhaustionGroups[5][1] = aJump;

        exhaustionGroups[6][0] = ExhaustionType.WALKING;
        String[] sWalk = Configs.getJsonObject().get("foodGroups").getAsJsonObject().get("walk").getAsString().split("/");
        ArrayList<FoodGroup> aWalk = new ArrayList<>();
        for (String s : sWalk) aWalk.add(FoodGroup.stringToFoodGroup(s));
        exhaustionGroups[6][1] = aWalk;
    }
}