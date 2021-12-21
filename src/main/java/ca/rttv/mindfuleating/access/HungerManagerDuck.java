package ca.rttv.mindfuleating.access;

import net.minecraft.item.Item;

// duck
public interface HungerManagerDuck {
    Item mostRecentFood();

    void mostRecentFood(Item item);

    void generateHungerIcons();

    int getHungerIcon(int y);
}
