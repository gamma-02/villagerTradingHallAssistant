package gamma02.villagertradinghallassistant.mixin;

import gamma02.villagertradinghallassistant.feature.BoundKeyHolder;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyBinding.class)
public class KeyBindingMixin implements BoundKeyHolder {


    @Shadow
    protected InputUtil.Key boundKey;

    @Override
    public InputUtil.Key getBoundKey() {
        return this.boundKey;
    }
}
