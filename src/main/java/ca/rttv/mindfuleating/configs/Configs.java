package ca.rttv.mindfuleating.configs;

import ca.rttv.mindfuleating.MindfulEating;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.stream.Collectors;

public class Configs {
   
   private static final File  configFile    = new File(getConfigDirectory(), "mindfuleating.json");
   private static final float configVersion = 1.1f;
   private static       JsonObject jsonObject;
   
   public static void generateSheenTexture() {
      for (int i = 0; i < jsonObject.get("sheenTexture").getAsJsonArray().size(); i++) {
         MindfulEating.sheen[i] = jsonObject.get("sheenTexture").getAsJsonArray().get(i).getAsBoolean();
      }
   }
   
   public static File getConfigDirectory() {
      return new File(".", "config");
   }
   
   public static JsonObject getJsonObject() {
      return jsonObject;
   }
   
   public static void loadConfigs() {
      if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
         try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            jsonObject = JsonParser.parseString(reader.lines().collect(Collectors.joining("\n"))).getAsJsonObject(); // this is what matters
            if (jsonObject.get("version") == null || jsonObject.get("version").getAsFloat() != configVersion) {
               JsonHelper.writeJsonToFile(generateDefaultConfig(), configFile);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }
   
   public static JsonObject generateDefaultConfig() {
      JsonObject json = new JsonObject();
      
      json.addProperty("version", configVersion);
      
      json.addProperty("exhaustionReductionAsDecimal", 0.75f);
      
      json.addProperty("useClassicIcons", false);
      
      JsonObject foodGroups = new JsonObject();
      {
         foodGroups.addProperty("destroy", "grains");
         foodGroups.addProperty("heal", "sugars");
         foodGroups.addProperty("attack", "proteins");
         foodGroups.addProperty("hurt", "proteins");
         foodGroups.addProperty("jump", "vegetables");
         foodGroups.addProperty("swim", "fruits");
         foodGroups.addProperty("walk", "vegetables");
      }
      json.add("foodGroups", foodGroups);
      
      JsonArray sheenTexture = new JsonArray();
      {
         sheenTexture.add(false);
         sheenTexture.add(false);
         sheenTexture.add(false);
         sheenTexture.add(false);
         sheenTexture.add(false);
         sheenTexture.add(false);
         sheenTexture.add(true);
         sheenTexture.add(true);
         sheenTexture.add(true);
         sheenTexture.add(true);
      }
      json.add("sheenTexture", sheenTexture);
      
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
            box.addProperty("name", "minecraft:cake");
            box.addProperty("value", 64);
            stackSize.add(box);
         }
         {
            JsonObject box = new JsonObject();
            box.addProperty("name", "minecraft:mushroom_stew");
            box.addProperty("value", 16);
            stackSize.add(box);
         }
         {
            JsonObject box = new JsonObject();
            box.addProperty("name", "minecraft:rabbit_stew");
            box.addProperty("value", 16);
            stackSize.add(box);
         }
         {
            JsonObject box = new JsonObject();
            box.addProperty("name", "minecraft:beetroot_soup");
            box.addProperty("value", 16);
            stackSize.add(box);
         }
         {
            JsonObject box = new JsonObject();
            box.addProperty("name", "minecraft:suspicious_stew");
            box.addProperty("value", 16);
            stackSize.add(box);
         }
      }
      json.add("stackSize", stackSize);
      
      JsonArray alwaysEdible = new JsonArray();
      json.add("alwaysEdible", alwaysEdible);
      
      JsonArray saturationModifier = new JsonArray();
      {
         {
            JsonObject box = new JsonObject();
            box.addProperty("name", "minecraft:cooked_mutton");
            box.addProperty("value", 0.2f);
            saturationModifier.add(box);
         }
         {
            JsonObject box = new JsonObject();
            box.addProperty("name", "minecraft:cooked_rabbit");
            box.addProperty("value", 0.1f);
            saturationModifier.add(box);
         }
         {
            JsonObject box = new JsonObject();
            box.addProperty("name", "minecraft:cooked_salmon");
            box.addProperty("value", 0.2f);
            saturationModifier.add(box);
         }
         {
            JsonObject box = new JsonObject();
            box.addProperty("name", "minecraft:cooked_cod");
            box.addProperty("value", 0.1f);
            saturationModifier.add(box);
         }
      }
      json.add("saturationModifier", saturationModifier);
      
      JsonArray hunger = new JsonArray();
      json.add("hunger", hunger);
      
      return json;
   }
}