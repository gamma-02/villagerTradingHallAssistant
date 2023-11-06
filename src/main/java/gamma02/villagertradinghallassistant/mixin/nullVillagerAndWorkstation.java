package gamma02.villagertradinghallassistant.mixin;

import gamma02.villagertradinghallassistant.VillagerTradingHallAssistant;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class nullVillagerAndWorkstation {


    @Inject(method = "handleDisconnection", at = @At("HEAD"))
    public void nullStuff(CallbackInfo ci){
        VillagerTradingHallAssistant.workstation = null;
        VillagerTradingHallAssistant.villager = null;
    }

}
