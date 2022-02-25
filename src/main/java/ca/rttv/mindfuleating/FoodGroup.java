package ca.rttv.mindfuleating;

public enum FoodGroup {
   FRUITS,
   GRAINS,
   PROTEINS,
   SUGARS,
   VEGETABLES;
   
   public static FoodGroup stringToFoodGroup(String str) {
      return switch (str.toLowerCase()) {
         case "fruits" -> FRUITS;
         case "grains" -> GRAINS;
         case "proteins" -> PROTEINS;
         case "sugars" -> SUGARS;
         case "vegetables" -> VEGETABLES;
         default -> throw new IllegalStateException();
      };
   }
}