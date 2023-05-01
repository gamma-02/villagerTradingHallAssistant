package gamma02.villagertradinghallassistant.mixin;

import gamma02.villagertradinghallassistant.config.Configs;
import gamma02.villagertradinghallassistant.feature.MainMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin{

    @Inject(method = "handleInputEvents", at = @At("HEAD"))
    public void mmmmmInputs(CallbackInfo ci){
        MinecraftClient client = (MinecraftClient) (Object) this;//:)
        if(MainMod.instance.isBreakingBlock && Configs.ENABLE_MOD.getBooleanValue()){
            KeyBinding.setKeyPressed(client.options.attackKey.boundKey, true);
        }

    }

}
