package ca.rttv.mindfuleating.mixin;

import net.minecraft.item.FoodComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FoodComponent.class)
public interface IFoodComponentAccessor {
    @Accessor
    @Mutable
    void setSnack(boolean snack);

    @Accessor
    @Mutable
    void setSaturationModifier(float saturationModifier);

    @Accessor
    @Mutable
    void setHunger(int hunger);
}
