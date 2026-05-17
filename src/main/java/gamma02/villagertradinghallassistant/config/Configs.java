package gamma02.villagertradinghallassistant.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gamma02.villagertradinghallassistant.VillagerTradingHallAssistant.modid;

public class Configs implements IConfigHandler {

    public static final String CONFIG_FILE_NAME = modid + ".json";

    public static final ConfigBoolean AUTO_FIND_WORKSTATION = new ConfigBoolean("autoFindWorkstation", true, "enables/disables automatically find the villager's workstation. May be buggy.");
    public static final ConfigStringList ACCEPTABLE_ENCHANTMENTS = new ConfigStringList("acceptableEnchantments", ImmutableList.of(""), "enchantments to look for in a librarian villager, put their id's here");

    public static final ConfigBooleanHotkeyed ENABLE_MOD = new ConfigBooleanHotkeyed("enableMod", false, "F,E", "enables finding the villager trade");

    public static final ConfigInteger MAX_COST = new ConfigInteger("maxTradeCost", 64, 0, 64, "this is a cutoff for how expensive a trade should be at max");

    public static final ConfigStringList ENCHANTMENT_LEVELS = new ConfigStringList("enchantLevel", ImmutableList.of(""), "level of enchantments, defaulting to automatically finding max. format: <(optional if minecraft)namespace:>path;level");

    public static Map<Identifier, Integer> EnchantLevelMap = new HashMap<>();

    public static final List<IConfigBase> CONFIGS = List.of(AUTO_FIND_WORKSTATION, ACCEPTABLE_ENCHANTMENTS, ENABLE_MOD, MAX_COST, ENCHANTMENT_LEVELS);


    public static void loadFromFile() {
        File configFile = new File(FileUtils.getConfigDirectoryAsPath().resolve(CONFIG_FILE_NAME).toString());

        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                ConfigUtils.readConfigBase(obj, "Config", CONFIGS);
                ConfigUtils.readConfigBase(obj, "Hotkeys", Hotkeys.HOTKEYS);
                ENABLE_MOD.resetToDefault();
            }
        }
    }

    public static void saveToFile() {
        File dir = FileUtils.getConfigDirectoryAsPath().toFile();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
            JsonObject obj = new JsonObject();

            ConfigUtils.writeConfigBase(obj, "Config", CONFIGS);
            ConfigUtils.writeConfigBase(obj, "Hotkeys", Hotkeys.HOTKEYS);

            JsonUtils.writeJsonToFile(obj, new File(dir, CONFIG_FILE_NAME));
        }
    }


    @Override
    public void load() {
        loadFromFile();

        rebuildEnchantLevelMap();
    }

    @Override
    public void save() {
        saveToFile();

        rebuildEnchantLevelMap();
    }

    private static void rebuildEnchantLevelMap() {
        EnchantLevelMap.clear();

        for (String s : ENCHANTMENT_LEVELS.getStrings()){
            String[] split = s.split(";");

            Identifier id = Identifier.of(split[0]);

            int level;
            try {
                level = Integer.parseInt(split[1]);
            } catch (Exception e) {
                continue;
            }

            EnchantLevelMap.put(id, level);
        }
    }
}
