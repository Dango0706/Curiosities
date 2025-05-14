package me.tuanzi.curiosities.enchantments.super_fortune;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.enchantments.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 超级时运事件处理器
 * 处理超级时运附魔的掉落物计算
 */
@Mod.EventBusSubscriber(modid = Curiosities.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SuperFortuneHandler {

    // 日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 处理方块破坏事件，应用超级时运效果
     *
     * @param event 方块破坏事件
     */
    @SubscribeEvent(priority = EventPriority.LOW) // 低优先级确保在原版处理后执行
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        // 检查超级时运是否启用
        if (!ModConfigManager.SUPER_FORTUNE_ENABLED.get()) {
            return;
        }

        // 检查玩家是否存在
        Player player = event.getPlayer();
        if (player == null) return;

        // 检查是否为服务端
        if (event.getLevel().isClientSide()) return;

        // 获取工具
        ItemStack tool = player.getMainHandItem();
        if (tool.isEmpty()) return;

        // 检查是否有超级时运附魔
        int superFortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.SUPER_FORTUNE.get(), tool);
        if (superFortuneLevel <= 0) return;

        // 获取是否有精准采集
        boolean hasSilkTouch = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0;

        // 获取方块状态和位置
        BlockState state = event.getState();
        BlockPos pos = event.getPos();
        ServerLevel level = (ServerLevel) event.getLevel();

        // 防止事件被取消
        if (event.isCanceled()) return;

        // 应用超级时运效果
        applySuperFortune(level, player, pos, state, tool, superFortuneLevel, hasSilkTouch);
    }

    /**
     * 应用超级时运效果，生成额外掉落物
     *
     * @param level             服务端世界
     * @param player            玩家
     * @param pos               方块位置
     * @param state             方块状态
     * @param tool              工具
     * @param superFortuneLevel 超级时运等级
     * @param hasSilkTouch      是否有精准采集
     */
    private static void applySuperFortune(ServerLevel level, Player player, BlockPos pos, BlockState state,
                                          ItemStack tool, int superFortuneLevel, boolean hasSilkTouch) {
        // 计算额外掉落倍率 (原有时运的1.5倍)
        float multiplier = calculateMultiplier(superFortuneLevel);

        // 需要生成的额外物品列表
        List<ItemStack> extraDrops = new ArrayList<>();

        if (hasSilkTouch) {
            // 如果有精准采集，生成额外的方块本身
            ItemStack blockItem = new ItemStack(state.getBlock().asItem());
            int extraCount = Math.round(multiplier) - 1; // 减1是因为精准采集已经给了一个

            if (extraCount > 0) {
                blockItem.setCount(extraCount);
                extraDrops.add(blockItem);
                LOGGER.debug("超级时运+精准采集生成额外掉落: {} x{}", blockItem.getDisplayName().getString(), extraCount);
            }
        } else {
            // 如果没有精准采集，生成额外的掉落物
            extraDrops = generateExtraDrops(level, player, pos, state, tool, multiplier);
        }

        // 生成额外掉落物
        for (ItemStack drop : extraDrops) {
            Block.popResource(level, pos, drop);
        }
    }

    /**
     * 计算超级时运的掉落倍率
     *
     * @param level 超级时运等级
     * @return 掉落倍率
     */
    private static float calculateMultiplier(int level) {
        // 超级时运效果是原有时运的1.5倍
        return switch (level) {
            case 1 -> 1.5f; // 原版时运1的掉落是1.33倍，超级时运1是2倍
            case 2 -> 2.25f; // 原版时运2的掉落是1.5倍，超级时运2是2.25倍
            case 3 -> 3.0f; // 原版时运3的掉落是2倍，超级时运3是3倍
            default -> 1.0f;
        };
    }

    /**
     * 生成额外的掉落物
     *
     * @param level      服务端世界
     * @param player     玩家
     * @param pos        方块位置
     * @param state      方块状态
     * @param tool       工具
     * @param multiplier 掉落倍率
     * @return 额外掉落物列表
     */
    private static List<ItemStack> generateExtraDrops(ServerLevel level, Player player, BlockPos pos,
                                                      BlockState state, ItemStack tool, float multiplier) {
        List<ItemStack> extraDrops = new ArrayList<>();

        // 创建掉落参数
        LootParams.Builder lootParamsBuilder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, tool.copy()) // 使用不带超级时运的工具副本
                .withParameter(LootContextParams.THIS_ENTITY, player);

        // 移除掉原版时运效果，只保留超级时运效果
        ItemStack toolWithoutFortune = tool.copy();
        toolWithoutFortune.removeTagKey("Enchantments"); // 移除所有附魔
        // 重新添加除了时运以外的所有附魔
        for (net.minecraft.world.item.enchantment.Enchantment enchantment : EnchantmentHelper.getEnchantments(tool).keySet()) {
            if (enchantment != Enchantments.BLOCK_FORTUNE && enchantment != ModEnchantments.SUPER_FORTUNE.get()) {
                int enchLevel = EnchantmentHelper.getItemEnchantmentLevel(enchantment, tool);
                toolWithoutFortune.enchant(enchantment, enchLevel);
            }
        }

        lootParamsBuilder.withParameter(LootContextParams.TOOL, toolWithoutFortune);

        // 生成基础掉落物
        List<ItemStack> baseDrops = state.getDrops(lootParamsBuilder);

        // 计算额外掉落物
        // 这个实现会大致生成与掉落倍率相匹配的额外物品数量
        for (ItemStack baseDrop : baseDrops) {
            int originalCount = baseDrop.getCount();
            int extraCount = Math.round(originalCount * multiplier) - originalCount;

            if (extraCount > 0) {
                ItemStack extraDrop = baseDrop.copy();
                extraDrop.setCount(extraCount);
                extraDrops.add(extraDrop);
                LOGGER.debug("超级时运生成额外掉落: {} x{}", extraDrop.getDisplayName().getString(), extraCount);
            }
        }

        return extraDrops;
    }
} 