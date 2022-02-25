package ca.rttv.mindfuleating;

public enum ExhaustionType {
   DESTROY(20),
   HEAL(20),
   ATTACK(20),
   HURT(20),
   JUMP(20),
   SWIMMING(7),
   WALKING(7);
   
   public final int bonusSheenTicks;
   
   ExhaustionType(int bonusSheenTicks) {
      this.bonusSheenTicks = bonusSheenTicks;
   }
}
