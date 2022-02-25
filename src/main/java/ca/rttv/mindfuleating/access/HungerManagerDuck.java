package ca.rttv.mindfuleating.access;

import net.minecraft.item.Item;

// duck
public interface HungerManagerDuck {
   int[] generateHungerIcons(Item item);
   
   int getHungerIcon(int y);
   
   Item mostRecentFood();
   
   void mostRecentFood(Item item);
   
   void setHungerIcons(int[] hungerIcons);
}
