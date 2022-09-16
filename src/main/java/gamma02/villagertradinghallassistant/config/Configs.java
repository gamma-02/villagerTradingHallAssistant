package gamma02.villagertradinghallassistant.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigBooleanHotkeyed;
import fi.dy.masa.malilib.config.options.ConfigStringList;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;

import java.io.File;
import java.util.List;

import static gamma02.villagertradinghallassistant.VillagerTradingHallAssistant.modid;

public class Configs implements IConfigHandler {

    public static final String CONFIG_FILE_NAME = modid + ".json";

    public static final ConfigBoolean AUTO_FIND_WORKSTATION = new ConfigBoolean("autoFindWorkstation", true, "enables/disables automatically find the villager's workstation. May be buggy.");
    public static final ConfigStringList ACCEPTABLE_ENCHANTMENTS = new ConfigStringList("acceptableEnchantments", ImmutableList.of(""), "enchantments to look for in a librarian villager, put their id's here");

    public static final ConfigBooleanHotkeyed ENABLE_MOD = new ConfigBooleanHotkeyed("enableMod", false, "F,E", "enables finding the villager trade");

    public static final List<IConfigBase> CONFIGS = List.of(AUTO_FIND_WORKSTATION, ACCEPTABLE_ENCHANTMENTS, ENABLE_MOD);


    public static void loadFromFile() {
        File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                ConfigUtils.readConfigBase(obj, "Config", CONFIGS);
                ConfigUtils.readConfigBase(obj, "Hotkeys", Hotkeys.HOTKEYS);
                ENABLE_MOD.setBooleanValue(false);
            }
        }
    }

    public static void saveToFile() {
        File dir = FileUtils.getConfigDirectory();

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
    }

    @Override
    public void save() {
        saveToFile();
    }
}
