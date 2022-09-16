package gamma02.villagertradinghallassistant.mixin;

import gamma02.villagertradinghallassistant.VillagerTradingHallAssistant;
import gamma02.villagertradinghallassistant.config.Configs;
import gamma02.villagertradinghallassistant.feature.MainMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.BooleanSupplier;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {

    @Shadow @Final private List<AbstractClientPlayerEntity> players;

    private ThreadLocal<Boolean> idk = ThreadLocal.withInitial(() -> true);

    @Shadow public abstract void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ);

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickMixin(BooleanSupplier shouldKeepTicking, CallbackInfo ci){
        if(VillagerTradingHallAssistant.workstation!= null){
            if(Random.create().nextBetween(1, 10) == 5) {
                BlockPos workstation = VillagerTradingHallAssistant.workstation;
                Vec3d particlePos = new Vec3d(workstation.getX() + 0.5, workstation.getY() + 1.3, workstation.getZ() + 0.5);
                MinecraftClient.getInstance().world.addParticle(ParticleTypes.HAPPY_VILLAGER, particlePos.x, particlePos.y, particlePos.z, 0, 0, 0);
            }
        }
        if(VillagerTradingHallAssistant.villager != null){
            if(Random.create().nextBetween(1, 10) == 5)
                produceParticles(ParticleTypes.HAPPY_VILLAGER);
        }



        if(Configs.ENABLE_MOD.getBooleanValue()){
            MainMod.instance.tick();
        }

    }



    protected void produceParticles(ParticleEffect parameters) {
        Random random = Random.create();

        for (int i = 0; i < 5; ++i) {
            double d = random.nextGaussian() * 0.02;
            double e = random.nextGaussian() * 0.02;
            double f = random.nextGaussian() * 0.02;
            this.addParticle(parameters, VillagerTradingHallAssistant.villager.getParticleX(1.0), VillagerTradingHallAssistant.villager.getRandomBodyY() + 1.0, VillagerTradingHallAssistant.villager.getParticleZ(1.0), d, e, f);
        }
    }

}
