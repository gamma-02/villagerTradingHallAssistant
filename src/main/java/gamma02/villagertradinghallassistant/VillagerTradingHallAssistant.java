package gamma02.villagertradinghallassistant;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import gamma02.villagertradinghallassistant.config.Configs;
import gamma02.villagertradinghallassistant.config.HotkeyHandlers;
import gamma02.villagertradinghallassistant.config.Hotkeys;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.structure.VillageGenerator;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static gamma02.villagertradinghallassistant.config.Hotkeys.HOTKEYS;
import static gamma02.villagertradinghallassistant.config.Hotkeys.HOTKEYS_FOR_MAP;

public class VillagerTradingHallAssistant implements ClientModInitializer, IInitializationHandler {

    public static final String MOD_ID = "villagertradinghallassistant";
    public static final String modid = MOD_ID;
    public static final String mod_id = MOD_ID;

    @Nullable
    public static BlockPos workstation = null;
    @Nullable
    public static VillagerEntity villager = null;





    @Override
    public void onInitializeClient() {

        InitializationHandler.getInstance().registerInitializationHandler(this);

        ClientPlayConnectionEvents.DISCONNECT.register( (a, b) -> this.onLeaveGame());

    }

    @Override
    public void registerModHandlers() {
        ConfigManager.getInstance().registerConfigHandler(MOD_ID, new Configs());
        InputEventHandler.getKeybindManager().registerKeybindProvider(InputHandler.getInstance());
//        InputEventHandler.getInputManager().registerKeyboardInputHandler(InputHandler.getInstance());
//        InputEventHandler.getInputManager().registerMouseInputHandler(InputHandler.getInstance());
        Hotkeys.OPEN_CONFIG.getKeybind().setCallback(new HotkeyHandlers.OpenConfigScreen());
        Hotkeys.GET_VILLAGER.getKeybind().setCallback(new HotkeyHandlers.SetVillager());
        Hotkeys.RAYTRACE_WORKSTATION.getKeybind().setCallback(new HotkeyHandlers.SetWorkstation());

    }

    public static class InputHandler implements IKeybindProvider{

        public static InputHandler instance = new InputHandler();

        private InputHandler(){
            super();
        }

        public static InputHandler getInstance(){
            return instance;
        }

        @Override
        public void addKeysToMap(IKeybindManager manager) {

            for(ConfigHotkey base : HOTKEYS_FOR_MAP){
                manager.addKeybindToMap(base.getKeybind());
            }
            manager.addKeybindToMap(Configs.ENABLE_MOD.getKeybind());
        }

        @Override
        public void addHotkeys(IKeybindManager manager) {
            List<IHotkey> hotkeys = List.of(Hotkeys.RAYTRACE_WORKSTATION, Hotkeys.GET_VILLAGER, Hotkeys.OPEN_CONFIG, Configs.ENABLE_MOD);
            manager.addHotkeysForCategory(MOD_ID, mod_id+".hotkeys.category.generic", hotkeys);
        }
    }


    public void onLeaveGame(){
        VillagerTradingHallAssistant.workstation = null;
        VillagerTradingHallAssistant.villager = null;
    }
}
