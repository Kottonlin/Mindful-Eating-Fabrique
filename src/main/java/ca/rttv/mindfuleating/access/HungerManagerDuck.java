package ca.rttv.mindfuleating.access;

import net.minecraft.item.Item;

// duck
public interface HungerManagerDuck {
    Item mostRecentFood();

    void mostRecentFood(Item item);

    int[] generateHungerIcons(Item item);

    int getHungerIcon(int y);

    void setHungerIcons(int[] hungerIcons);
}
