package ca.rttv.mindfuleating.configs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.stream.Collectors;

public class Configs {

    private static final File configFile = new File(getConfigDirectory(), "mindfuleating.json");
    private static final float configVersion = 1.0f;
    private static JsonObject jsonObject;

    public static File getConfigDirectory() {
        return new File(".", "config");
    }

    public static void loadConfigs() {
        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                jsonObject = JsonParser.parseString(reader.lines().collect(Collectors.joining("\n"))).getAsJsonObject(); // this is what matters
                if (jsonObject.get("version") == null || jsonObject.get("version").getAsFloat() != configVersion)
                    JsonHelper.writeJsonToFile(Configs.generateDefaultConfig(), configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static JsonObject generateDefaultConfig() {
        JsonObject json = new JsonObject();
        json.add("version", new JsonPrimitive(configVersion));
        json.add("exhaustionReductionAsDecimal", new JsonPrimitive(0.75f));
        JsonObject foodGroups = new JsonObject();
        {
            foodGroups.add("destroy", new JsonPrimitive("grains"));
            foodGroups.add("heal", new JsonPrimitive("sugars"));
            foodGroups.add("attack", new JsonPrimitive("attack"));
            foodGroups.add("hurt", new JsonPrimitive("proteins"));
            foodGroups.add("jump", new JsonPrimitive("vegetables"));
            foodGroups.add("swim", new JsonPrimitive("fruits"));
            foodGroups.add("walk", new JsonPrimitive("vegetables"));
        }
        json.add("foodGroups", foodGroups);
        JsonArray speedy = new JsonArray();
        {
            speedy.add("minecraft:melon_slice");
            speedy.add("minecraft:sweet_berries");
            speedy.add("minecraft:cooked_mutton");
            speedy.add("minecraft:cooked_rabbit");
            speedy.add("minecraft:cooked_salmon");
            speedy.add("minecraft:cooked_cod");
            speedy.add("minecraft:beetroot");
            speedy.add("minecraft:beetroot_soup");
        }
        json.add("speedy", speedy);
        JsonArray stackSize = new JsonArray();
        {
            {
                JsonObject box = new JsonObject();
                box.add("name", new JsonPrimitive("minecraft:cake"));
                box.add("value", new JsonPrimitive(64));
                stackSize.add(box);
            }
            {
                JsonObject box = new JsonObject();
                box.add("name", new JsonPrimitive("minecraft:mushroom_stew"));
                box.add("value", new JsonPrimitive(16));
                stackSize.add(box);
            }
            {
                JsonObject box = new JsonObject();
                box.add("name", new JsonPrimitive("minecraft:rabbit_stew"));
                box.add("value", new JsonPrimitive(16));
                stackSize.add(box);
            }
            {
                JsonObject box = new JsonObject();
                box.add("name", new JsonPrimitive("minecraft:beetroot_soup"));
                box.add("value", new JsonPrimitive(16));
                stackSize.add(box);
            }
            {
                JsonObject box = new JsonObject();
                box.add("name", new JsonPrimitive("minecraft:suspicious_stew"));
                box.add("value", new JsonPrimitive(16));
                stackSize.add(box);
            }
        }
        json.add("stackSize", stackSize);
        JsonArray saturationModifier = new JsonArray();
        {
            {
                JsonObject box = new JsonObject();
                box.add("name", new JsonPrimitive("minecraft:cooked_mutton"));
                box.add("value", new JsonPrimitive(0.2f));
                saturationModifier.add(box);
            }
            {
                JsonObject box = new JsonObject();
                box.add("name", new JsonPrimitive("minecraft:cooked_rabbit"));
                box.add("value", new JsonPrimitive(0.1f));
                saturationModifier.add(box);
            }
            {
                JsonObject box = new JsonObject();
                box.add("name", new JsonPrimitive("minecraft:cooked_salmon"));
                box.add("value", new JsonPrimitive(0.2f));
                saturationModifier.add(box);
            }
            {
                JsonObject box = new JsonObject();
                box.add("name", new JsonPrimitive("minecraft:cooked_cod"));
                box.add("value", new JsonPrimitive(0.1f));
                saturationModifier.add(box);
            }
        }
        json.add("saturationModifier", saturationModifier);
        JsonArray hunger = new JsonArray(); /*{...}*/
        json.add("hunger", hunger);
        return json;
    }

    public static JsonObject getJsonObject() {
        return jsonObject;
    }
}
