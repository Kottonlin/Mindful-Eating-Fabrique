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
            default -> null;
        };
    }

    public static String toString(FoodGroup f) {
        return switch (f) {
            case FRUITS -> "FRUITS";
            case GRAINS -> "GRAINS";
            case PROTEINS -> "PROTEINS";
            case SUGARS -> "SUGARS";
            case VEGETABLES -> "VEGETABLES";
        };
    }
}
