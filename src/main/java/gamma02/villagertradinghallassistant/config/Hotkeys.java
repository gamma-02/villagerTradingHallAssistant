package gamma02.villagertradinghallassistant.config;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigHotkey;

import java.util.List;

public class Hotkeys {
    public static final ConfigHotkey RAYTRACE_WORKSTATION = new ConfigHotkey("findVillagerWorkstation", "F,O", "sets the block you are looking at as the villager's workstation");
    public static final ConfigHotkey GET_VILLAGER = new ConfigHotkey("findVillager", "F,V", "sets the villager you are looking at as the villager to find trades for");

    public static final ConfigHotkey OPEN_CONFIG = new ConfigHotkey("openGui", "V,C", "opens the config GUI");

    public static final List<IConfigBase> HOTKEYS = List.of(RAYTRACE_WORKSTATION, GET_VILLAGER, OPEN_CONFIG, Configs.ENABLE_MOD);
    public static final List<ConfigHotkey> HOTKEYS_FOR_MAP = List.of(RAYTRACE_WORKSTATION, GET_VILLAGER, OPEN_CONFIG);

}
