package gamma02.villagertradinghallassistant.feature;

import de.maxhenkel.tradecycling.FabricTradeCyclingClientMod;
import gamma02.villagertradinghallassistant.VillagerTradingHallAssistant;
import gamma02.villagertradinghallassistant.config.Configs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerProfession;

import java.util.Optional;

public class MainMod {
    public static MainMod instance = new MainMod();



    public boolean toRefreshTrades = false;


    public Block workstation = null;

    public boolean toolWarning = true;

    public boolean hasRefreshedTrades = true;

    public TradeOfferList trades = null;


    public void tick(){

        MinecraftClient mc = MinecraftClient.getInstance();

        ClientWorld world = mc.world;
        ClientPlayerEntity player = mc.player;

        if(player == null)
            return;

        if(world == null)
            return;

        if(mc.interactionManager == null)
            return;

//        InputUtil.Key attackKey = ((BoundKeyHolder) mc.options.attackKey).getBoundKey();

//        if(VillagerTradingHallAssistant.isBreakingBlock && !mc.options.attackKey.isPressed()){
//            KeyBinding.onKeyPressed(attackKey);
//            KeyBinding.setKeyPressed(attackKey, true);
//        }
//
//        if(
//                VillagerTradingHallAssistant.workstation != null
//                && !PointOfInterestTypes.isPointOfInterest(Objects.requireNonNull(world)
//                .getBlockState(VillagerTradingHallAssistant.workstation))
//                && toRefreshTrades
//            ){
//            toRefreshTrades = false;
//            hasRefreshedTrades = true;
////            InputUtil.Key attackKey = ((BoundKeyHolder) mc.options.attackKey).getBoundKey();
////            KeyBinding.onKeyPressed(attackKey);
////            KeyBinding.setKeyPressed(attackKey, false);
//        }


        if(VillagerTradingHallAssistant.villager != null && mc.currentScreen == null && !toRefreshTrades){
            mc.interactionManager.interactEntity(player, VillagerTradingHallAssistant.villager, player.getActiveHand());
            hasRefreshedTrades = true;
        }




//        if(VillagerTradingHallAssistant.workstation != null) {
//
//
//            if(toRefreshTrades && mc.currentScreen != null){
//                mc.currentScreen.close();
////                InputUtil.Key attackKey = ((BoundKeyHolder) mc.options.attackKey).getBoundKey();
////                KeyBinding.onKeyPressed(attackKey);
////                KeyBinding.setKeyPressed(attackKey, true);
////                KeyBinding.setKeyPressed(attackKey, false);
//            }
//
////            if(toBreakWorkstation && mc.currentScreen != null){
////                mc.currentScreen.close();
////            }
//
//
//            if (toRefreshTrades && mc.currentScreen == null) {
//                workstation = world.getBlockState(VillagerTradingHallAssistant.workstation).getBlock();
//                if(!switchToEffectiveTool(world.getBlockState(VillagerTradingHallAssistant.workstation), player)){
//                    if(toolWarning) {
//                        mc.inGameHud.getChatHud().addMessage(Text.of("No effective tool in the hotbar! please put the effective tool for the workstation in your hotbar."));
//                        toolWarning = false;
//                    }
//                    return;
//                }
//
//                player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(VillagerTradingHallAssistant.workstation.getX() + 0.5, VillagerTradingHallAssistant.workstation.getY()+0.5, VillagerTradingHallAssistant.workstation.getZ() + 0.5));
//
////                mc.options.attackKey.setPressed(true);
//
////                InputUtil.Key attackKey = ((BoundKeyHolder) mc.options.attackKey).getBoundKey();
//                KeyBinding.onKeyPressed(attackKey);
//                KeyBinding.setKeyPressed(attackKey, true);
//
//
//                VillagerTradingHallAssistant.isBreakingBlock = true;
//
////                Objects.requireNonNull(mc.interactionManager).attackBlock(VillagerTradingHallAssistant.workstation, Direction.UP);
//                hasRefreshedTrades = false;
////                toBreakWorkstation = false;
//            } else{
////                KeyBinding.onKeyPressed(attackKey);
//                KeyBinding.setKeyPressed(attackKey, false);
//                VillagerTradingHallAssistant.isBreakingBlock = false;
//            }
//
//
//            if (!PointOfInterestTypes.isPointOfInterest(Objects.requireNonNull(world)
//                    .getBlockState(VillagerTradingHallAssistant.workstation))
//                    && VillagerTradingHallAssistant.villager != null
//                    && VillagerTradingHallAssistant.villager
//                        .getVillagerData()
//                        .getProfession() == VillagerProfession.NONE
////                        .matchesKey(VillagerProfession.NONE)
//                ){
////                VillagerTradingHallAssistant.isBreakingBlock = false;
//                placeWorkstation(VillagerTradingHallAssistant.workstation, (BlockItem) workstation.asItem());//please never be weird lol
//            }
//
//
//        }

        VillagerEntity villager = VillagerTradingHallAssistant.villager;
        if(villager != null && villager.getVillagerData().getProfession() == VillagerProfession.LIBRARIAN && hasRefreshedTrades &&
                mc.currentScreen instanceof MerchantScreen merchantScreen &&
                !merchantScreen.getScreenHandler().getRecipes().equals(trades)){
            toRefreshTrades = true;
            hasRefreshedTrades = false;
            trades = merchantScreen.getScreenHandler().getRecipes();

            for (TradeOffer offer : (merchantScreen).getScreenHandler().getRecipes()) {
                ItemStack stack = offer.getSellItem();
                if (stack.getItem() == Items.ENCHANTED_BOOK) {
                    ItemEnchantmentsComponent enchants = stack.getComponents().get(DataComponentTypes.STORED_ENCHANTMENTS);

                    if(enchants == null) continue;

                    Optional<RegistryEntry<Enchantment>> enchantHolder = enchants.getEnchantments().stream().findAny();

                    if(enchantHolder.isEmpty()){
                        continue;
                    }

                    RegistryEntry<Enchantment> enchant = enchantHolder.orElseThrow();
                    String enchantId = enchant.getIdAsString();

                    System.out.println("%s at lvl: %d cost: %d%n".formatted(enchantId, enchants.getLevel(enchant), offer.getDisplayedFirstBuyItem().getCount()));

                    if (Configs.ACCEPTABLE_ENCHANTMENTS.getStrings().contains(enchantId)) {
                        foundAcceptableEnchant(mc, enchant, enchantId, enchants, offer);
                    }
                }
            }
        }

        if(toRefreshTrades && !hasRefreshedTrades && mc.currentScreen instanceof MerchantScreen screen && screen.getScreenHandler().getRecipes().equals(trades)){
//            System.out.println("Sending refresh packet");

            //send the trade cycling packet
            if (screen.getScreenHandler().isLeveled() && screen.getScreenHandler().getExperience() <= 0) {
                FabricTradeCyclingClientMod.instance().sendCycleTradesPacket();
//                mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            hasRefreshedTrades = true;

        }

    }

    public void foundAcceptableEnchant(MinecraftClient mc, RegistryEntry<Enchantment> enchant, String enchantIdString, ItemEnchantmentsComponent enchants, TradeOffer offer) {

        Identifier enchantId = Identifier.of(enchantIdString);
        int enchantLevel = enchants.getLevel(enchant);

        if(Configs.EnchantLevelMap.containsKey(enchantId) && Configs.EnchantLevelMap.get(enchantId) > enchantLevel)
            return;
        else if(enchant.value().getMaxLevel() != enchantLevel)
            return;

        if(offer.getDisplayedFirstBuyItem().getCount() > Configs.MAX_COST.getIntegerValue())
            return;


        mc.inGameHud.getChatHud().addMessage(Text.of("Found enchantment " + enchant.getKey().map(key -> key.getValue().getPath()).orElse("[unregistered]")));
        toRefreshTrades = false;
        trades = null;
        Configs.ENABLE_MOD.resetToDefault();
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
