package gamma02.villagertradinghallassistant.feature;

import gamma02.villagertradinghallassistant.VillagerTradingHallAssistant;
import gamma02.villagertradinghallassistant.config.Configs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestTypes;

import java.util.Objects;

public class MainMod {
    public static MainMod instance = new MainMod();



    public boolean toRefreshTrades = false;


    public Block workstation = null;

    public boolean toolWarning = true;

    public boolean hasRefreshedTrades = true;

    public TradeOfferList trades = null;


    public void tick(){

        ClientWorld world = MinecraftClient.getInstance().world;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        InputUtil.Key attackKey = ((BoundKeyHolder) MinecraftClient.getInstance().options.attackKey).getBoundKey();

        if(VillagerTradingHallAssistant.isBreakingBlock && !MinecraftClient.getInstance().options.attackKey.isPressed()){
            KeyBinding.onKeyPressed(attackKey);
            KeyBinding.setKeyPressed(attackKey, true);
        }

        if(
                VillagerTradingHallAssistant.workstation != null
                && !PointOfInterestTypes.isPointOfInterest(Objects.requireNonNull(world)
                .getBlockState(VillagerTradingHallAssistant.workstation))
                && toRefreshTrades
            ){
            toRefreshTrades = false;
            hasRefreshedTrades = true;
//            InputUtil.Key attackKey = ((BoundKeyHolder) MinecraftClient.getInstance().options.attackKey).getBoundKey();
//            KeyBinding.onKeyPressed(attackKey);
//            KeyBinding.setKeyPressed(attackKey, false);
        }


        if(VillagerTradingHallAssistant.villager != null && MinecraftClient.getInstance().currentScreen == null && !toRefreshTrades){
            MinecraftClient.getInstance().interactionManager.interactEntity(player, VillagerTradingHallAssistant.villager, player.getActiveHand());
        }




        if(VillagerTradingHallAssistant.workstation != null) {


            if(toRefreshTrades && MinecraftClient.getInstance().currentScreen != null){
                MinecraftClient.getInstance().currentScreen.close();
//                InputUtil.Key attackKey = ((BoundKeyHolder) MinecraftClient.getInstance().options.attackKey).getBoundKey();
//                KeyBinding.onKeyPressed(attackKey);
//                KeyBinding.setKeyPressed(attackKey, true);
//                KeyBinding.setKeyPressed(attackKey, false);
            }

//            if(toBreakWorkstation && MinecraftClient.getInstance().currentScreen != null){
//                MinecraftClient.getInstance().currentScreen.close();
//            }


            if (toRefreshTrades && MinecraftClient.getInstance().currentScreen == null) {
                workstation = world.getBlockState(VillagerTradingHallAssistant.workstation).getBlock();
                if(!switchToEffectiveTool(world.getBlockState(VillagerTradingHallAssistant.workstation), player)){
                    if(toolWarning) {
                        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("No effective tool in the hotbar! please put the effective tool for the workstation in your hotbar."));
                        toolWarning = false;
                    }
                    return;
                }

                player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(VillagerTradingHallAssistant.workstation.getX() + 0.5, VillagerTradingHallAssistant.workstation.getY()+0.5, VillagerTradingHallAssistant.workstation.getZ() + 0.5));

//                MinecraftClient.getInstance().options.attackKey.setPressed(true);

//                InputUtil.Key attackKey = ((BoundKeyHolder) MinecraftClient.getInstance().options.attackKey).getBoundKey();
                KeyBinding.onKeyPressed(attackKey);
                KeyBinding.setKeyPressed(attackKey, true);


                VillagerTradingHallAssistant.isBreakingBlock = true;

//                Objects.requireNonNull(MinecraftClient.getInstance().interactionManager).attackBlock(VillagerTradingHallAssistant.workstation, Direction.UP);
                hasRefreshedTrades = false;
//                toBreakWorkstation = false;
            } else{
//                KeyBinding.onKeyPressed(attackKey);
                KeyBinding.setKeyPressed(attackKey, false);
                VillagerTradingHallAssistant.isBreakingBlock = false;
            }


            if (!PointOfInterestTypes.isPointOfInterest(Objects.requireNonNull(world)
                    .getBlockState(VillagerTradingHallAssistant.workstation))
                    && VillagerTradingHallAssistant.villager != null
                    && VillagerTradingHallAssistant.villager
                        .getVillagerData()
                        .getProfession() == VillagerProfession.NONE
//                        .matchesKey(VillagerProfession.NONE)
                ){
//                VillagerTradingHallAssistant.isBreakingBlock = false;
                placeWorkstation(VillagerTradingHallAssistant.workstation, (BlockItem) workstation.asItem());//please never be weird lol
            }


        }


        if(VillagerTradingHallAssistant.villager != null){

            VillagerEntity villager = VillagerTradingHallAssistant.villager;

            if(villager.getVillagerData().getProfession() == VillagerProfession.LIBRARIAN && hasRefreshedTrades &&
                    MinecraftClient.getInstance().currentScreen instanceof MerchantScreen merchantScreen &&
                !merchantScreen.getScreenHandler().getRecipes().equals(trades)) {
                toRefreshTrades = true;
                hasRefreshedTrades = false;
                trades = merchantScreen.getScreenHandler().getRecipes();

                for (TradeOffer offer : (merchantScreen).getScreenHandler().getRecipes()) {
                    ItemStack stack = offer.getSellItem();
                    if (stack.getItem() == Items.ENCHANTED_BOOK) {
                        var enchants = stack.getComponents().get(DataComponentTypes.STORED_ENCHANTMENTS);

                        if(enchants == null) continue;

                        var enchantHolder = enchants.getEnchantments().stream().findAny();

                        if(enchantHolder.isEmpty()){
                            continue;
                        }

                        RegistryEntry<Enchantment> enchant = enchantHolder.orElseThrow();
//                        System.out.println(enchant.getIdAsString());
                        System.out.printf("%s at lvl: %d cost: %d%n", enchant.getIdAsString(), enchants.getLevel(enchant), offer.getDisplayedFirstBuyItem().getCount());

                        if (Configs.ACCEPTABLE_ENCHANTMENTS.getStrings().contains(enchant.getKey().map(key -> key.getValue().getPath()).orElse("[unregistered]"))
                                && enchant.value().getMaxLevel() == enchants.getLevel(enchant)
                                && offer.getDisplayedFirstBuyItem().getCount() <= Configs.MAX_COST.getIntegerValue()) {
                            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("Found enchantment " + enchant.getKey().map(key -> key.getValue().getPath()).orElse("[unregistered]")));
                            toRefreshTrades = false;
                            trades = null;
                            Configs.ENABLE_MOD.resetToDefault();
                        }
                    }
                }
            }
        }
//        if(toRefreshTrades && !hasRefreshedTrades && MinecraftClient.getInstance().currentScreen instanceof MerchantScreen screen && screen.getScreenHandler().getRecipes().equals(trades)){
//            System.out.println("REFRESHING!");
//            //send the trade cycling packet
//            if (screen.getScreenHandler().isLeveled() && screen.getScreenHandler().getExperience() <= 0) {
//                FabricTradeCyclingClientMod.instance().sendCycleTradesPacket();
//                mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
//            }
//            hasRefreshedTrades = true;
//
//        }

    }

    public static void placeWorkstation(BlockPos pos, BlockItem block){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if(player == null)
            return;

        int slot = player.getInventory().getSlotWithStack(block.getDefaultStack());

        if(PlayerInventory.isValidHotbarIndex(slot)){
//            player.getInventory().setSelectedSlot(slot);
            player.getInventory().selectedSlot = slot;
        }else if(slot != -1){
//            MinecraftClient.getInstance().interactionManager.pickItemFromBlock(pos, true);
            MinecraftClient.getInstance().interactionManager.pickFromInventory(slot);
        }
        BlockHitResult result = new BlockHitResult(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), Direction.UP, pos, false);
        placeBlockWithoutInteractingBlock(MinecraftClient.getInstance(), result);
    }

    public boolean switchToEffectiveTool(BlockState workstation, ClientPlayerEntity player){
        boolean switched = false;


        int slot = 0;
        //searches through hotbar for correct tool
        for(ItemStack stack : player.getInventory().main){
            if(stack.getItem().isCorrectForDrops(stack, workstation)){
                break;
            }

            slot++;
        }

        //sets tool
        if(PlayerInventory.isValidHotbarIndex(slot)) {
//            player.getInventory().setSelectedSlot(slot);
            player.getInventory().selectedSlot = slot;
            switched = true;
        }


        return switched;
    }




    private static void placeBlockWithoutInteractingBlock(MinecraftClient minecraftClient, BlockHitResult hitResult) {
        ClientPlayerEntity player = minecraftClient.player;

        if(player == null || minecraftClient.world == null)
            return;

        ItemStack itemStack = player.getStackInHand(Hand.MAIN_HAND);

        if(minecraftClient.interactionManager == null)
            return;

        minecraftClient.interactionManager.sendSequencedPacket(minecraftClient.world, sequence ->
                new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hitResult, sequence));

        if (!itemStack.isEmpty() && !player.getItemCooldownManager().isCoolingDown(itemStack.getItem())) {
            ItemUsageContext itemUsageContext = new ItemUsageContext(player, Hand.MAIN_HAND, hitResult);
            itemStack.useOnBlock(itemUsageContext);

        }
    }
}
