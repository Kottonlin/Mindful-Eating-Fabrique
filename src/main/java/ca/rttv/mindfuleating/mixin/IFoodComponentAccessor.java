package ca.rttv.mindfuleating.mixin;

import net.minecraft.item.FoodComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin( FoodComponent.class )
public interface IFoodComponentAccessor {
   @Accessor
   @Mutable
   void setAlwaysEdible(boolean alwaysEdible);
   
   @Accessor
   @Mutable
   void setHunger(int hunger);
   
   @Accessor
   @Mutable
   void setSaturationModifier(float saturationModifier);
   
   // thing here override original values so I might want to store it for reloading midgame in another update
   @Accessor
   @Mutable
   void setSnack(boolean snack);
}