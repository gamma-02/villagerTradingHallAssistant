package gamma02.villagertradinghallassistant.feature;

import gamma02.villagertradinghallassistant.VillagerTradingHallAssistant;
import gamma02.villagertradinghallassistant.config.Configs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;

import java.util.Objects;

public class MainMod {
    public static MainMod instance = new MainMod();



    public boolean toBreakWorkstation = false;


    public Block workstation = null;

    public boolean toolWarning = true;

    public boolean hasBrokenBlock = true;


    public void tick(){

        ClientWorld world = MinecraftClient.getInstance().world;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if(VillagerTradingHallAssistant.workstation != null && world.getBlockState(VillagerTradingHallAssistant.workstation).getBlock() == Blocks.AIR && toBreakWorkstation){
            toBreakWorkstation = false;
            hasBrokenBlock = true;
        }

        if(VillagerTradingHallAssistant.villager != null && MinecraftClient.getInstance().currentScreen == null && !toBreakWorkstation){
            MinecraftClient.getInstance().interactionManager.interactEntity(player, VillagerTradingHallAssistant.villager, player.getActiveHand());
        }




        if(VillagerTradingHallAssistant.workstation != null) {

//            if(toBreakWorkstation && MinecraftClient.getInstance().currentScreen != null){
//                MinecraftClient.getInstance().currentScreen.close();
//            }


            if (toBreakWorkstation && MinecraftClient.getInstance().currentScreen == null) {
                workstation = world.getBlockState(VillagerTradingHallAssistant.workstation).getBlock();
                if(!switchToEffectiveTool(world.getBlockState(VillagerTradingHallAssistant.workstation), player)){
                    if(toolWarning) {
                        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("No effective tool in the hotbar! please put the effective tool for the workstation in your hotbar."));
                        toolWarning = false;
                    }
                    return;
                }
                player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(VillagerTradingHallAssistant.workstation.getX() + 0.5, VillagerTradingHallAssistant.workstation.getY()+0.5, VillagerTradingHallAssistant.workstation.getZ() + 0.5));
                MinecraftClient.getInstance().options.attackKey.setPressed(true);
//                VillagerTradingHallAssistant.isBreakingBlock = true;
//                Objects.requireNonNull(MinecraftClient.getInstance().interactionManager).attackBlock(VillagerTradingHallAssistant.workstation, Direction.UP);
                hasBrokenBlock = false;
//                toBreakWorkstation = false;
            }else{
                VillagerTradingHallAssistant.isBreakingBlock = false;
            }


            if (Objects.requireNonNull(world).getBlockState(VillagerTradingHallAssistant.workstation).getBlock() == Blocks.AIR && VillagerTradingHallAssistant.villager != null && VillagerTradingHallAssistant.villager.getVillagerData().getProfession() == VillagerProfession.NONE){
                VillagerTradingHallAssistant.isBreakingBlock = false;
                placeWorkstation(VillagerTradingHallAssistant.workstation, (BlockItem) workstation.asItem());//please never be weird lol
            }


        }
        if(VillagerTradingHallAssistant.villager != null && VillagerTradingHallAssistant.workstation != null && world.getBlockState(VillagerTradingHallAssistant.workstation).getBlock() != Blocks.AIR && hasBrokenBlock){



            VillagerEntity villager = VillagerTradingHallAssistant.villager;


            if(villager.getVillagerData().getProfession() == VillagerProfession.LIBRARIAN && MinecraftClient.getInstance().currentScreen instanceof MerchantScreen) {
                toBreakWorkstation = true;
                for (TradeOffer offer : ((MerchantScreen) MinecraftClient.getInstance().currentScreen).getScreenHandler().getRecipes()) {
                    ItemStack stack = offer.getSellItem();
                    if (stack.getItem() == Items.ENCHANTED_BOOK) {
                        Identifier enchant = new Identifier(((NbtCompound) EnchantedBookItem.getEnchantmentNbt(stack).get(0)).getString("id"));
                        System.out.println(enchant);
                        System.out.println("lvl: " + ((NbtCompound) EnchantedBookItem.getEnchantmentNbt(stack).get(0)).getInt("lvl"));
                        if (Configs.ACCEPTABLE_ENCHANTMENTS.getStrings().contains(enchant.getPath()) &&
                                Objects.requireNonNull(Registries.ENCHANTMENT.get(enchant)).getMaxLevel()/*I SHOULD HOPE that this will never be null lmao*/ == ((NbtCompound) EnchantedBookItem.getEnchantmentNbt(stack).get(0)).getInt("lvl")
                                && offer.getAdjustedFirstBuyItem().getCount() <= Configs.MAX_COST.getIntegerValue()) {
                            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("Found enchantment " + enchant.getPath()));
                            toBreakWorkstation = false;
                            toolWarning = true;
                            Configs.ENABLE_MOD.resetToDefault();
                        }
                    }
                }

                if(toBreakWorkstation){
                    MinecraftClient.getInstance().currentScreen.close();
                }
            }
        }

    }

    public static void placeWorkstation(BlockPos pos, BlockItem block){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        int slot = player.getInventory().getSlotWithStack(block.getDefaultStack());
        if(PlayerInventory.isValidHotbarIndex(slot)){
            player.getInventory().selectedSlot = slot;
        }else if(slot != -1){
            MinecraftClient.getInstance().interactionManager.pickFromInventory(slot);
        }
        BlockHitResult result = new BlockHitResult(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), Direction.UP, pos, false);
        placeBlockWithoutInteractingBlock(MinecraftClient.getInstance(), result);
    }

    public boolean switchToEffectiveTool(BlockState workstation, ClientPlayerEntity player){
        boolean switched = false;


        int slot = 0;
        for(ItemStack stack : player.getInventory().main){
            if(stack.getItem().isSuitableFor(workstation)){
                break;
            }

            slot++;
        }
        if(PlayerInventory.isValidHotbarIndex(slot)) {
            player.getInventory().selectedSlot = slot;
            switched = true;
        }


        return switched;
    }




    private static void placeBlockWithoutInteractingBlock(MinecraftClient minecraftClient, BlockHitResult hitResult) {
        ClientPlayerEntity player = minecraftClient.player;
        ItemStack itemStack = player.getStackInHand(Hand.MAIN_HAND);

        minecraftClient.interactionManager.sendSequencedPacket(minecraftClient.world, sequence ->
                new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hitResult, sequence));

        if (!itemStack.isEmpty() && !player.getItemCooldownManager().isCoolingDown(itemStack.getItem())) {
            ItemUsageContext itemUsageContext = new ItemUsageContext(player, Hand.MAIN_HAND, hitResult);
            itemStack.useOnBlock(itemUsageContext);

        }
    }
}
