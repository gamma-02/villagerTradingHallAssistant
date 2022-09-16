package gamma02.villagertradinghallassistant.config;

import fi.dy.masa.malilib.MaLiLibConfigGui;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import gamma02.villagertradinghallassistant.VillagerTradingHallAssistant;
import net.minecraft.client.resource.language.I18n;;

import java.util.List;

import static gamma02.villagertradinghallassistant.VillagerTradingHallAssistant.modid;

public class ConfigScreen extends GuiConfigsBase {

    private static Tab tab = Tab.CONFIG;

    ConfigScreen(){
        super(10, 50, VillagerTradingHallAssistant.MOD_ID, null, "config." + modid + ".title.config");
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        return ConfigOptionWrapper.createFor(ConfigScreen.tab != Tab.CONFIG ? Hotkeys.HOTKEYS : Configs.CONFIGS);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.clearOptions();

        int x = 10;
        int y = 26;

        for (Tab tab : Tab.values())
        {
            x += this.createButton(x, y, -1, tab) + 2;
        }
    }

    @Override
    protected int getConfigWidth()
    {
        Tab tab = ConfigScreen.tab;

        if (tab == Tab.CONFIG)
        {
            return 200;
        }

        return super.getConfigWidth();
    }

    private int createButton(int x, int y, int width, Tab tab)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getDisplayName());
        button.setEnabled(ConfigScreen.tab != tab);
        this.addButton(button, new ButtonListener(tab, this));

        return button.getWidth();
    }


    private static class ButtonListener implements IButtonActionListener
    {
        private final ConfigScreen parent;
        private final Tab tab;

        public ButtonListener(Tab tab, ConfigScreen parent)
        {
            this.tab = tab;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            ConfigScreen.tab = this.tab;

            this.parent.reCreateListWidget(); // apply the new config width
            this.parent.getListWidget().resetScrollbarPosition();
            this.parent.initGui();
        }
    }


    public enum Tab {
        HOTKEYS("config."+modid +".hotkeys"),
        CONFIG("config."+modid+".config");

        final String translation;
        private Tab(String translation){
            this.translation = translation;
        }

        public String getDisplayName(){
            return I18n.translate(translation);
        }
    }



}
