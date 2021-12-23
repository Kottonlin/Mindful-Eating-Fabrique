package ca.rttv.mindfuleating;

import ca.rttv.mindfuleating.access.HungerManagerDuck;
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
    static MinecraftClient client = MinecraftClient.getInstance();

    public static boolean shouldReceiveBuffs(ExhaustionType type) {
        if (client.player != null) {
            int bonusSheenTicks = type.getBonusSheenTicks();
            Item mostRecentFood = ((HungerManagerDuck) client.player.getHungerManager()).mostRecentFood();
            for (Object[] exhaustionGroup : exhaustionGroups) {
                if (exhaustionGroup[0].equals(type)) {
                    for (int i = 0; i < ((ArrayList<?>) exhaustionGroup[1]).size(); i++) {
                        switch ((FoodGroup) ((ArrayList<?>) exhaustionGroup[1]).get(i)) {
                            case FRUITS -> {
                                if (fruits.contains(mostRecentFood)) {
                                    MindfulEating.shouldHaveSheen = bonusSheenTicks;
                                    return true;
                                }
                            }
                            case GRAINS -> {
                                if (grains.contains(mostRecentFood)) {
                                    MindfulEating.shouldHaveSheen = bonusSheenTicks;
                                    return true;
                                }
                            }
                            case PROTEINS -> {
                                if (proteins.contains(mostRecentFood)) {
                                    MindfulEating.shouldHaveSheen = bonusSheenTicks;
                                    return true;
                                }
                            }
                            case SUGARS -> {
                                if (sugars.contains(mostRecentFood)) {
                                    MindfulEating.shouldHaveSheen = bonusSheenTicks;
                                    return true;
                                }
                            }
                            case VEGETABLES -> {
                                if (vegetables.contains(mostRecentFood)) {
                                    MindfulEating.shouldHaveSheen = bonusSheenTicks;
                                    return true;
                                }
                            }
                            default -> throw new IllegalArgumentException("couldn't find a correct food group, what did you do?");
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void registerExhaustionGroups(String[] input) {
        if (input.length != 7) throw new IllegalArgumentException("Invalid length for exhaustion groups, what did you do?");
        exhaustionGroups[0][0] = ExhaustionType.SWIMMING;
        String[] sSwim = input[0].split("/");
        ArrayList<FoodGroup> aSwim = new ArrayList<>();
        for (String s : sSwim) aSwim.add(FoodGroup.stringToFoodGroup(s));
        exhaustionGroups[0][1] = aSwim;

        exhaustionGroups[1][0] = ExhaustionType.DESTROY;
        String[] sDestroy = input[1].split("/");
        ArrayList<FoodGroup> aDestroy = new ArrayList<>();
        for (String s : sDestroy) aDestroy.add(FoodGroup.stringToFoodGroup(s));
        exhaustionGroups[1][1] = aDestroy;

        exhaustionGroups[2][0] = ExhaustionType.ATTACK;
        String[] sAttack = input[2].split("/");
        ArrayList<FoodGroup> aAttack = new ArrayList<>();
        for (String s : sAttack) aAttack.add(FoodGroup.stringToFoodGroup(s));
        exhaustionGroups[2][1] = aAttack;

        exhaustionGroups[3][0] = ExhaustionType.HURT;
        String[] sHurt = input[3].split("/");
        ArrayList<FoodGroup> aHurt = new ArrayList<>();
        for (String s : sHurt) aHurt.add(FoodGroup.stringToFoodGroup(s));
        exhaustionGroups[3][1] = aHurt;

        exhaustionGroups[4][0] = ExhaustionType.HEAL;
        String[] sHeal = input[4].split("/");
        ArrayList<FoodGroup> aHeal = new ArrayList<>();
        for (String s : sHeal) aHeal.add(FoodGroup.stringToFoodGroup(s));
        exhaustionGroups[4][1] = aHeal;

        exhaustionGroups[5][0] = ExhaustionType.JUMP;
        String[] sJump = input[5].split("/");
        ArrayList<FoodGroup> aJump = new ArrayList<>();
        for (String s : sJump) aJump.add(FoodGroup.stringToFoodGroup(s));
        exhaustionGroups[5][1] = aJump;

        exhaustionGroups[6][0] = ExhaustionType.WALKING;
        String[] sWalk = input[6].split("/");
        ArrayList<FoodGroup> aWalk = new ArrayList<>();
        for (String s : sWalk) aWalk.add(FoodGroup.stringToFoodGroup(s));
        exhaustionGroups[6][1] = aWalk;
    }
}