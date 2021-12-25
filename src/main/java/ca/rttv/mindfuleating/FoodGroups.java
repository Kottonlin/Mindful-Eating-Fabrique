package ca.rttv.mindfuleating;

import ca.rttv.mindfuleating.access.HungerManagerDuck;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

@SuppressWarnings("PointlessBooleanExpression")
public class FoodGroups {
    public static Tag<Item> fruits = TagFactory.ITEM.create(new Identifier("mindfuleating", "fruits"));
    public static Tag<Item> grains = TagFactory.ITEM.create(new Identifier("mindfuleating", "grains"));
    public static Tag<Item> proteins = TagFactory.ITEM.create(new Identifier("mindfuleating", "proteins"));
    public static Tag<Item> sugars = TagFactory.ITEM.create(new Identifier("mindfuleating", "sugars"));
    public static Tag<Item> vegetables = TagFactory.ITEM.create(new Identifier("mindfuleating", "vegetables"));
    public static Object[][] exhaustionGroups = new Object[7][2];
    static MinecraftClient client = MinecraftClient.getInstance();

    public static boolean shouldReceiveBuffs(ExhaustionType type) {
        if (MindfulEating.inOnMEServer == false) return false;
        if (client.player != null) {
            int bonusSheenTicks = type.bonusSheenTicks;
            Item mostRecentFood = ((HungerManagerDuck) client.player.getHungerManager()).mostRecentFood();
            for (Object[] exhaustionGroup : exhaustionGroups)
                if (exhaustionGroup[0].equals(type))
                    for (int i = 0; i < ((ArrayList<?>) exhaustionGroup[1]).size(); i++)
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
        return false;
    }

    public static void registerExhaustionGroups(String[] input) {
        if (input.length != 7) throw new IllegalArgumentException("Invalid length for exhaustion groups, what did you do?");
        exhaustionGroups[0][0] = ExhaustionType.SWIMMING;
        exhaustionGroups[1][0] = ExhaustionType.DESTROY;
        exhaustionGroups[2][0] = ExhaustionType.ATTACK;
        exhaustionGroups[3][0] = ExhaustionType.HURT;
        exhaustionGroups[4][0] = ExhaustionType.HEAL;
        exhaustionGroups[5][0] = ExhaustionType.JUMP;
        exhaustionGroups[6][0] = ExhaustionType.WALKING;
        for (int a = 0; a < input.length; ++a) {
            String[] b = input[a].split("/");
            ArrayList<FoodGroup> c = new ArrayList<>();
            for (String d : b) c.add(FoodGroup.stringToFoodGroup(d));
            exhaustionGroups[a][1] = c;
        }
    }

    public static void registerDefaultExhaustionGroups() {
        registerExhaustionGroups(new String[]{"fruits","grains","proteins","proteins","sugars","vegetables","vegetables"});
    }
}