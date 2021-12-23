package ca.rttv.mindfuleating;

public enum FoodGroup {
    FRUITS("fruits"),
    GRAINS("grains"),
    PROTEINS("proteins"),
    SUGARS("sugars"),
    VEGETABLES("vegetables");

    private final String name;

    FoodGroup(String name) {
        this.name = name;
    }

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

    public String getName() {
        return this.name;
    }
}
