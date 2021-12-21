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
    public static Logger LOGGER = LogManager.getLogger("mindfuleating");
    public static Identifier MINDFUL_EATING_ICONS = new Identifier("mindfuleating", "textures/hunger_icons.png");
    public static Identifier NOURISHED_ICONS = new Identifier("mindfuleating", "textures/nourished_icons.png");
    public static Identifier MindfulEatingDataS2CPacket = new Identifier("mindfuleating", "datapacket");
    // awakened tysm for the gson help ♥
    public static File configFile = new File(Configs.getConfigDirectory(), "mindfuleating.json");
    public static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static boolean[] sheen = {false, false, false, false, false, false, true, true, true, true};
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
            else if (type.equalsIgnoreCase("byte"))
                finishedString.append(jsonArray.get(i).getAsByte());
            else if (type.equalsIgnoreCase("double"))
                finishedString.append(jsonArray.get(i).getAsDouble());
            else if (type.equalsIgnoreCase("long"))
                finishedString.append(jsonArray.get(i).getAsLong());

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
            newSheen[0] = sheen[1];
            newSheen[1] = sheen[2];
            newSheen[2] = sheen[3];
            newSheen[3] = sheen[4];
            newSheen[4] = sheen[5];
            newSheen[5] = sheen[6];
            newSheen[6] = sheen[7];
            newSheen[7] = sheen[8];
            newSheen[8] = sheen[9];
            newSheen[9] = sheen[0];
            sheen = newSheen;
        }
    }

    @Override
    public void onInitialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (shouldHaveSheen > -1) --shouldHaveSheen;
            if (tick == 2) tick = 0;
            updateSheen(++tick);
        });
        // i really could directly put the arraylists here but its a bit large so I made it a method
//        FoodGroups.registerFoodGroups();

        File dir = Configs.getConfigDirectory();
        if ((dir.exists() && dir.isDirectory() || dir.mkdirs()))
            if (!configFile.exists())
                JsonHelper.writeJsonToFile(Configs.generateDefaultConfig(), configFile);

        Configs.loadConfigs();
        FoodGroups.registerExhaustionGroups();

        // this is the packet listener which starts up on init ofc but sets the clients mostRecentFood to the string sent from the server.
        ClientPlayNetworking.registerGlobalReceiver(MindfulEating.MindfulEatingDataS2CPacket,
                (client, handler, buf, responceSender) -> {
                    // write values here
                    FoodGroups.registerExhaustionGroups();
//                    FoodGroups.registerFoodGroups();

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

                    client.execute(() -> {
                                if (client.player != null) {
                                    // write code here
                                    ((HungerManagerDuck) client.player.getHungerManager()).mostRecentFood(Registry.ITEM.get(new Identifier(mostRecentFood)));
                                    // this, using the power of duck interfaces, will set the clients mostRecentFood to whatever was sent in the packet (ofc using the registry to turn String -> Item).
                                    ((HungerManagerDuck) client.player.getHungerManager()).generateHungerIcons();

                                    for (int i = 0; i < stringStackabilityItems.length; i++)
                                        itemStackabilityItems[i] = (Registry.ITEM.get(new Identifier(stringStackabilityItems[i])));
                                    for (int i = 0; i < itemStackabilityItems.length; i++)
                                        ((IItemAccessor) itemStackabilityItems[i]).setMaxCount(Integer.parseInt(stringStackabilityCounts[i]));

                                    for (int i = 0; i < stringSpeedyItems.length; i++)
                                        itemSpeedyItems[i] = Registry.ITEM.get(new Identifier(stringSpeedyItems[i])).getFoodComponent();
                                    for (FoodComponent itemSpeedyItem : itemSpeedyItems)
                                        if (itemSpeedyItem != null)
                                            ((IFoodComponentAccessor) itemSpeedyItem).setSnack(true);

                                    // i can make this one for loop but I would call ITEM.get twice per run and since mc is ram > clock cycles its variables all the way boi
                                    for (int i = 0; i < stringSaturationItems.length; i++)
                                        itemSaturationItems[i] = Registry.ITEM.get(new Identifier(stringSaturationItems[i])).getFoodComponent();
                                    for (int i = 0; i < itemSaturationItems.length; i++)
                                        if (itemSaturationItems[i] != null)
                                            ((IFoodComponentAccessor) itemSaturationItems[i]).setSaturationModifier(Float.parseFloat(stringSaturationCounts[i]));

                                    // this is fine
                                    for (int i = 0; i < stringHungerItems.length; i++)
                                        itemHungerItems[i] = Registry.ITEM.get(new Identifier(stringHungerItems[i])).getFoodComponent();
                                    for (int i = 0; i < itemHungerItems.length; i++)
                                        if (itemHungerItems[i] != null)
                                            ((IFoodComponentAccessor) itemHungerItems[i]).setHunger(Integer.parseInt(stringHungerCounts[i]));
                                }
                            }
                    );
                }
        );
    }
}