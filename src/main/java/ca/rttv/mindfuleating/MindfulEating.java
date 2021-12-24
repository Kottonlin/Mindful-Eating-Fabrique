package ca.rttv.mindfuleating;

import ca.rttv.mindfuleating.access.HungerManagerDuck;
import ca.rttv.mindfuleating.configs.Configs;
import ca.rttv.mindfuleating.configs.JsonHelper;
import ca.rttv.mindfuleating.mixin.IFoodComponentAccessor;
import ca.rttv.mindfuleating.mixin.IItemAccessor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class MindfulEating implements ModInitializer {
    public static final Logger Logger = LogManager.getLogger("mindfuleating");
    private static boolean useClassicIcons;
    public static Identifier MINDFUL_EATING_ICONS;
    public static Identifier NOURISHED_ICONS;
    public static Identifier SATURATION_ICONS;
    public static final Identifier MindfulEatingDataS2CPacket = new Identifier("mindfuleating", "datapacket");
    // awakened tysm for the gson help ♥
    public static File configFile = new File(Configs.getConfigDirectory(), "mindfuleating.json");
    public static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static boolean[] sheen = new boolean[10];
    public static int shouldHaveSheen = 0;
    private int tick = 0;

    public static String jsonArrayToPacketString(JsonArray jsonArray, String type) {
        StringBuilder finishedString = new StringBuilder();
        for (int i = 0; i < jsonArray.size(); i++) {
            if (type.equalsIgnoreCase("string"))
                finishedString.append(jsonArray.get(i).getAsString());
            else if (type.equalsIgnoreCase("int"))
                finishedString.append(jsonArray.get(i).getAsInt());
            else if (type.equalsIgnoreCase("float"))
                finishedString.append(jsonArray.get(i).getAsFloat());
            else throw new IllegalArgumentException();
            if (jsonArray.size() - 1 != i) finishedString.append("::");
        }
        return finishedString.toString();
    }

    public static String jsonArrayToPacketString(JsonArray jsonArray, String key, String type) {
        StringBuilder finishedString = new StringBuilder();
        for (int i = 0; i < jsonArray.size(); i++) {
            if (type.equalsIgnoreCase("string"))
                finishedString.append(jsonArray.get(i).getAsJsonObject().get(key).getAsString());
            else if (type.equalsIgnoreCase("int"))
                finishedString.append(jsonArray.get(i).getAsJsonObject().get(key).getAsInt());
            else if (type.equalsIgnoreCase("float"))
                finishedString.append(jsonArray.get(i).getAsJsonObject().get(key).getAsFloat());
            if (jsonArray.size() - 1 != i) finishedString.append("::");
        }
        return finishedString.toString();
    }

    private void updateSheen(int tick) {
        if (tick == 2) {
            // shift sheen
            boolean[] newSheen = new boolean[sheen.length];
            for (int i = 0; i < sheen.length; ++i)
                newSheen[i] = sheen[(i+1)%sheen.length];
            sheen = newSheen;
        }
    }

    @Override
    public void onInitialize() {
        File dir = Configs.getConfigDirectory();
        if ((dir.exists() && dir.isDirectory() || dir.mkdirs()))
            if (!configFile.exists())
                JsonHelper.writeJsonToFile(Configs.generateDefaultConfig(), configFile);
        Configs.loadConfigs();

        Configs.generateSheenTexture();

        useClassicIcons = Configs.getJsonObject().get("useClassicIcons").getAsBoolean();
        MINDFUL_EATING_ICONS = useClassicIcons ? new Identifier("mindfuleating", "textures/classic/hunger_icons.png") : new Identifier("mindfuleating", "textures/hunger_icons.png");
        NOURISHED_ICONS = useClassicIcons ? new Identifier("mindfuleating", "textures/classic/nourished_icons.png") : new Identifier("mindfuleating", "textures/nourished_icons.png");
        SATURATION_ICONS = useClassicIcons ? new Identifier("mindfuleating", "textures/classic/saturation_icons.png") : new Identifier("mindfuleating", "textures/saturation_icons.png");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (shouldHaveSheen > -1) --shouldHaveSheen;
            if (tick >= 2 && shouldHaveSheen > -1) tick = 0;
            if (shouldHaveSheen > -1) updateSheen(++tick); // i know tick needs to be 19 and then add to 20
        });

        // this is the packet listener which starts up on init ofc but sets the clients mostRecentFood to the string sent from the server.
        ClientPlayNetworking.registerGlobalReceiver(MindfulEating.MindfulEatingDataS2CPacket,
                (client, handler, buf, responceSender) -> {
                    // write values here
                    String mostRecentFood = buf.readString(); // first string
                    String[] stringStackabilityItems = buf.readString().split("::"); // second string
                    String[] stringStackabilityCounts = buf.readString().split("::"); // third string
                    Item[] itemStackabilityItems = new Item[stringStackabilityItems.length];

                    String[] stringSpeedyItems = buf.readString().split("::"); // fourth string
                    FoodComponent[] itemSpeedyItems = new FoodComponent[stringSpeedyItems.length];

                    String[] stringSaturationItems = buf.readString().split("::"); // fifth string
                    String[] stringSaturationCounts = buf.readString().split("::"); // sixth string
                    FoodComponent[] itemSaturationItems = new FoodComponent[stringSaturationItems.length];

                    String[] stringHungerItems = buf.readString().split("::"); // seventh string
                    String[] stringHungerCounts = buf.readString().split("::"); // eighth string
                    FoodComponent[] itemHungerItems = new FoodComponent[stringHungerItems.length];

                    String[] stringAlwaysEdibleItems = buf.readString().split("::"); // ninth string
                    FoodComponent[] itemAlwaysEdibleItems = new FoodComponent[stringAlwaysEdibleItems.length];

                    String[] stringFoodGroups = buf.readString().split("::"); // tenth string

                    client.execute(() -> {
                                if (client.player != null) {
                                    // write code here
                                    ((HungerManagerDuck) client.player.getHungerManager()).mostRecentFood(Registry.ITEM.get(new Identifier(mostRecentFood)));
                                    // this, using the power of duck interfaces, will set the clients mostRecentFood to whatever was sent in the packet (ofc using the registry to turn String -> Item).
                                    ((HungerManagerDuck) client.player.getHungerManager()).generateHungerIcons();

                                    FoodGroups.registerExhaustionGroups(stringFoodGroups);

                                    for (int i = 0; i < stringStackabilityItems.length; i++)
                                        itemStackabilityItems[i] = (Registry.ITEM.get(new Identifier(stringStackabilityItems[i])));
                                    for (int i = 0; i < itemStackabilityItems.length; i++)
                                        ((IItemAccessor) itemStackabilityItems[i]).setMaxCount(Integer.parseInt(stringStackabilityCounts[i]));

                                    for (int i = 0; i < stringSpeedyItems.length; i++)
                                        itemSpeedyItems[i] = Registry.ITEM.get(new Identifier(stringSpeedyItems[i])).getFoodComponent();
                                    for (FoodComponent itemSpeedyItem : itemSpeedyItems)
                                            ((IFoodComponentAccessor) itemSpeedyItem).setSnack(true);

                                    for (int i = 0; i < stringSaturationItems.length; i++)
                                        itemSaturationItems[i] = Registry.ITEM.get(new Identifier(stringSaturationItems[i])).getFoodComponent();
                                    for (int i = 0; i < itemSaturationItems.length; i++)
                                            ((IFoodComponentAccessor) itemSaturationItems[i]).setSaturationModifier(Float.parseFloat(stringSaturationCounts[i]));

                                    // this is fine
                                    for (int i = 0; i < stringHungerItems.length; i++)
                                        itemHungerItems[i] = Registry.ITEM.get(new Identifier(stringHungerItems[i])).getFoodComponent();
                                    for (int i = 0; i < itemHungerItems.length; i++)
                                            ((IFoodComponentAccessor) itemHungerItems[i]).setHunger(Integer.parseInt(stringHungerCounts[i]));

                                    for (int i = 0; i < stringAlwaysEdibleItems.length; i++)
                                        itemAlwaysEdibleItems[i] = Registry.ITEM.get(new Identifier(stringAlwaysEdibleItems[i])).getFoodComponent();
                                    for (FoodComponent itemAlwaysEdibleItem : itemAlwaysEdibleItems)
                                            ((IFoodComponentAccessor) itemAlwaysEdibleItem).setAlwaysEdible(true);
                                }
                            }
                    );
                }
        );
    }
}