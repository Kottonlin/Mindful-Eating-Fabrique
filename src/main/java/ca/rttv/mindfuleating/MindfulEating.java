package ca.rttv.mindfuleating;

import ca.rttv.mindfuleating.access.HungerManagerDuck;
import ca.rttv.mindfuleating.configs.Configs;
import ca.rttv.mindfuleating.configs.JsonHelper;
import ca.rttv.mindfuleating.mixin.IFoodComponentAccessor;
import ca.rttv.mindfuleating.mixin.IItemAccessor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import java.io.File;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MindfulEating implements ModInitializer {
   public static final Logger          Logger                      = LogManager.getLogger("mindfuleating");
   public static final Identifier      MindfulEatingDataS2CPacket  = new Identifier("mindfuleating", "data");
   public static final Identifier      MindfulEatingSheenS2CPacket = new Identifier("mindfuleating", "sheen");
   public static       Gson            GSON                        = new GsonBuilder().setPrettyPrinting().create();
   public static       Identifier      MINDFUL_EATING_ICONS;
   public static       Identifier      NOURISHED_ICONS;
   public static       Identifier      SATURATION_ICONS;
   public static       Identifier      SMALL_SATURATION_ICONS      = new Identifier("mindfuleating", "textures/small_saturation_icons.png");
   public static       MinecraftClient client;
   // awakened tysm for the gson help ♥
   public static       File            configFile                  = new File(Configs.getConfigDirectory(), "mindfuleating.json");
   public static       boolean         isOnMEServer                = false;
   public static       boolean[]       sheen                       = new boolean[10];
   public static       int             shouldHaveSheen             = -1;
   private             int             tick                        = 0;
   
   public static String jsonArrayToPacketString(JsonArray jsonArray, String type) {
      if (jsonArray.size() == 0) {
         return "";
      }
      StringBuilder finishedString = new StringBuilder();
      for (int i = 0; i < jsonArray.size(); i++) {
         if (type.equalsIgnoreCase("string")) {
            finishedString.append(jsonArray.get(i).getAsString());
         } else if (type.equalsIgnoreCase("int")) {
            finishedString.append(jsonArray.get(i).getAsInt());
         } else if (type.equalsIgnoreCase("float")) {
            finishedString.append(jsonArray.get(i).getAsFloat());
         } else {
            throw new IllegalArgumentException();
         }
         if (jsonArray.size() - 1 != i) {
            finishedString.append("::");
         }
      }
      return finishedString.toString();
   }
   
   public static String jsonArrayToPacketString(JsonArray jsonArray, String key, String type) {
      if (jsonArray.size() == 0) {
         return "";
      }
      StringBuilder finishedString = new StringBuilder();
      for (int i = 0; i < jsonArray.size(); i++) {
         if (type.equalsIgnoreCase("string")) {
            finishedString.append(jsonArray.get(i).getAsJsonObject().get(key).getAsString());
         } else if (type.equalsIgnoreCase("int")) {
            finishedString.append(jsonArray.get(i).getAsJsonObject().get(key).getAsInt());
         } else if (type.equalsIgnoreCase("float")) {
            finishedString.append(jsonArray.get(i).getAsJsonObject().get(key).getAsFloat());
         }
         if (jsonArray.size() - 1 != i) {
            finishedString.append("::");
         }
      }
      return finishedString.toString();
   }
   
   @Override
   public void onInitialize() {
      
      /*
       * SERVER & CLIENT VVV
       */
      
      File dir = Configs.getConfigDirectory();
      if ((dir.exists() && dir.isDirectory() || dir.mkdirs())) {
         if (!configFile.exists()) {
            JsonHelper.writeJsonToFile(Configs.generateDefaultConfig(), configFile);
         }
      }
      Configs.loadConfigs();
      Configs.generateSheenTexture();
      FoodGroups.registerDefaultExhaustionGroups();
      
      boolean useClassicIcons = Configs.getJsonObject().get("useClassicIcons").getAsBoolean();
      MINDFUL_EATING_ICONS = useClassicIcons ? new Identifier("mindfuleating", "textures/classic/hunger_icons.png") : new Identifier("mindfuleating", "textures/hunger_icons.png");
      NOURISHED_ICONS = useClassicIcons ? new Identifier("mindfuleating", "textures/classic/nourished_icons.png") : new Identifier("mindfuleating", "textures/nourished_icons.png");
      SATURATION_ICONS = useClassicIcons ? new Identifier("mindfuleating", "textures/classic/saturation_icons.png") : new Identifier("mindfuleating", "textures/saturation_icons.png");
      
      /*
       * CLIENT VVV
       */
      
      if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) { // for some reason this doesn't work in onInitializeClient() idk why
         
         client = MinecraftClient.getInstance();
         
         ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (shouldHaveSheen > -1) {
               --shouldHaveSheen;
            }
            if (tick >= 2 && shouldHaveSheen > -1) {
               tick = 0;
            }
            if (shouldHaveSheen > -1) {
               updateSheen(++tick); // i know tick needs to be 1 and then add to 2 not be 2 at start
            }
         });
         
         ClientPlayNetworking.registerGlobalReceiver(MindfulEatingSheenS2CPacket, (client, handler, buf, responceSender) -> {
            int shouldHaveSheen = buf.readInt();
            client.execute(() -> MindfulEating.shouldHaveSheen = shouldHaveSheen);
         });
         
         ClientPlayNetworking.registerGlobalReceiver(MindfulEatingDataS2CPacket, (client, handler, buf, responceSender) -> {
            // write values here
            String mostRecentFood = buf.readString(); // first string
            
            String[] stackSizeNames = buf.readString().split("::"); // second string
            String[] stackSizeValues = buf.readString().split("::"); // third string
            
            String[] speedy = buf.readString().split("::"); // fourth string
            
            String[] saturationModifierNames = buf.readString().split("::"); // fifth string
            String[] saturationModifierValues = buf.readString().split("::"); // sixth string
            
            String[] hungerNames = buf.readString().split("::"); // seventh string
            String[] hungerValues = buf.readString().split("::"); // eighth string
            
            String[] alwaysEdibleNames = buf.readString().split("::"); // ninth string
            
            String[] foodGroups = buf.readString().split("::"); // tenth string
            
            client.execute(() -> {
               if (client.player != null) {
                  // write code here
                  isOnMEServer = true;
                  
                  ((HungerManagerDuck) client.player.getHungerManager()).mostRecentFood(Registry.ITEM.get(new Identifier(mostRecentFood)));
                  
                  ((HungerManagerDuck) client.player.getHungerManager()).setHungerIcons(((HungerManagerDuck) client.player.getHungerManager()).generateHungerIcons(((HungerManagerDuck) client.player.getHungerManager()).mostRecentFood()));
                  
                  FoodGroups.registerExhaustionGroups(foodGroups);
                  
                  for (int i = 0; i < stackSizeNames.length; i++) {
                     ((IItemAccessor) Registry.ITEM.get(new Identifier(stackSizeNames[i]))).setMaxCount(Integer.parseInt(stackSizeValues[i]));
                  }
                  
                  for (String stringSpeedyItem : speedy) {
                     ((IFoodComponentAccessor) Objects.requireNonNull(Registry.ITEM.get(new Identifier(stringSpeedyItem)).getFoodComponent())).setSnack(true);
                  }
                  
                  for (int i = 0; i < saturationModifierNames.length; i++) {
                     ((IFoodComponentAccessor) Objects.requireNonNull(Registry.ITEM.get(new Identifier(saturationModifierNames[i])).getFoodComponent())).setSaturationModifier(Float.parseFloat(saturationModifierValues[i]));
                  }
                  
                  // this is fine
                  for (int i = 0; i < hungerNames.length; i++) {
                     ((IFoodComponentAccessor) Objects.requireNonNull(Registry.ITEM.get(new Identifier(hungerNames[i])).getFoodComponent())).setHunger(Integer.parseInt(hungerValues[i]));
                  }
                  
                  for (String stringAlwaysEdibleItem : alwaysEdibleNames) {
                     ((IFoodComponentAccessor) Objects.requireNonNull(Registry.ITEM.get(new Identifier(stringAlwaysEdibleItem)).getFoodComponent())).setAlwaysEdible(true);
                  }
               }
            });
         });
      }
   }
   
   private void updateSheen(int tick) {
      if (tick == 2) {
         // shift sheen
         boolean[] newSheen = new boolean[sheen.length];
         for (int i = 0; i < sheen.length; ++i) {
            newSheen[i] = sheen[(i + 1) % sheen.length];
         }
         sheen = newSheen;
      }
   }
}