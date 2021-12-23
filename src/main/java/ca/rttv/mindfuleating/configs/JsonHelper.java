package ca.rttv.mindfuleating.configs;

import ca.rttv.mindfuleating.MindfulEating;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JsonHelper {
    public static boolean writeJsonToFile(JsonObject root, File file) {
        FileWriter writer = null;

        try {
            writer = new FileWriter(file);
            writer.write(MindfulEating.GSON.toJson(root));
            writer.close();

            return true;
        } catch (IOException e) {
            MindfulEating.Logger.warn("Failed to write JSON data to file '{}'", file.getAbsolutePath(), e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception e) {
                MindfulEating.Logger.warn("Failed to close JSON file", e);
            }
        }

        return false;
    }

    @Nullable
    public static JsonObject getNestedObject(JsonObject parent, String key, boolean create) {
        if (!parent.has(key) || !parent.get(key).isJsonObject()) {
            if (!create) {
                return null;
            }

            JsonObject obj = new JsonObject();
            parent.add(key, obj);
            return obj;
        } else {
            return parent.get(key).getAsJsonObject();
        }
    }
}
