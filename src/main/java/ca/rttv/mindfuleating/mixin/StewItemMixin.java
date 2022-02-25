package ca.rttv.mindfuleating.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.StewItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( StewItem.class )
public abstract class StewItemMixin extends Item {
   public StewItemMixin(Settings settings) {
      super(settings);
   }
   
   @Inject( method = "finishUsing", at = @At( value = "HEAD", shift = At.Shift.BY, by = 1 ), cancellable = true )
   private void finishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
      super.finishUsing(stack, world, user);
      PlayerEntity playerEntity = (PlayerEntity) user;
      
      if (!playerEntity.getAbilities().creativeMode) {
         stack.decrement(1);
      }
      if (!playerEntity.getAbilities().creativeMode) {
         if (stack.isEmpty()) {
            cir.setReturnValue(new ItemStack(Items.BOWL));
         }
         playerEntity.getInventory().insertStack(new ItemStack(Items.BOWL));
      }
      cir.setReturnValue(stack);
   }
}
