package gamma02.villagertradinghallassistant.config;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import gamma02.villagertradinghallassistant.VillagerTradingHallAssistant;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.Iterator;

public class HotkeyHandlers {

    public static HotkeyHandlers INSTANCE = new HotkeyHandlers();

    public static class SetVillager implements IHotkeyCallback{

        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key) {
            if(MinecraftClient.getInstance().targetedEntity instanceof VillagerEntity villager){
                VillagerTradingHallAssistant.villager = villager;
                if(Configs.AUTO_FIND_WORKSTATION.getBooleanValue() && villager.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).isPresent()){
                    BlockPos workstation = villager.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get().getPos();
                    VillagerTradingHallAssistant.workstation = villager.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get().getPos();
                    System.out.println(workstation);
                }
                return true;
            }else{
                MinecraftClient.getInstance().gameRenderer.updateTargetedEntity(MinecraftClient.getInstance().getTickDelta());
            }





            return false;
        }
    }

    public static class SetWorkstation implements IHotkeyCallback{

        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key) {

            ClientWorld world = MinecraftClient.getInstance().world;
            ClientPlayerEntity entity = MinecraftClient.getInstance().player;

            if(world != null && entity != null) {
                BlockPos pos = world.raycast(new RaycastContext(entity.getEyePos(), entity.getEyePos().add(getRotationVec3d(entity.getPitch(), entity.getYaw()).multiply(5)), RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, entity)).getBlockPos();
                BlockState state = world.getBlockState(pos);

                for (Iterator<PointOfInterestType> it = Registries.POINT_OF_INTEREST_TYPE.iterator(); it.hasNext(); ) {
                    PointOfInterestType poit = it.next();
                    if(poit.contains(state)){
                        VillagerTradingHallAssistant.workstation = pos;
                        return true;
                    }
                }
            }
            return false;
        }
    }





    public static class OpenConfigScreen implements IHotkeyCallback{

        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key) {

                GuiBase screen = new ConfigScreen();
                GuiBase.openGui(screen);


            return true;
        }
    }

    public static void init(){

    }

    public static Vec3d getRotationVec3d(float pitch, float yaw) {
        float f = pitch * 0.017453292F;
        float g = -yaw * 0.017453292F;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d((i * j), (-k), (h * j));
    }
}
