package me.tuanzi.curiosities;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.enchantments.ModEnchantments;
import me.tuanzi.curiosities.enchantments.chain_mining.ChainMiningEnchantment;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.*;

/**
 * 连锁挖掘核心逻辑
 * 负责处理连锁挖掘的触发和执行
 */
public class ChainMiningLogic {
    // 日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 触发连锁挖掘过程
     *
     * @param player 玩家实例
     * @param origin 起始方块位置
     * @param level  世界实例
     */
    public static void triggerChainMining(Player player, BlockPos origin, Level level) {
        // 检查配置是否启用连锁挖掘
        if (!ModConfigManager.CHAIN_MINING_ENABLED.get()) {
            LOGGER.info("[连锁挖掘] 连锁挖掘功能已在配置中禁用");
            return;
        }

        // 参数验证
        if (level == null || player == null) {
            LOGGER.debug("[连锁挖掘] 无效调用: level={}, player={}", level != null ? "有效" : "无效", player != null ? "有效" : "无效");
            return;
        }

        // 确保在服务端执行
        if (level.isClientSide) {
            LOGGER.debug("[连锁挖掘] 在客户端调用，忽略");
            return;
        }

        // 验证工具
        ItemStack tool = player.getMainHandItem();
        LOGGER.info("[连锁挖掘] 玩家 {} 尝试使用工具 {} 进行连锁挖掘", player.getName().getString(), tool.getDisplayName().getString());

        if (tool.isEmpty()) {
            LOGGER.info("[连锁挖掘] 工具为空，取消连锁挖掘");
            return;
        }

        // 检查工具是否有连锁挖掘附魔，且该附魔是否可用
        if (!ChainMiningEnchantment.isChainMiningUsable(tool)) {
            LOGGER.info("[连锁挖掘] 工具没有有效的连锁挖掘附魔，取消连锁挖掘");
            return;
        }

        // 获取连锁挖掘附魔等级
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.CHAIN_MINING.get(), tool);
        LOGGER.info("[连锁挖掘] 工具连锁挖掘附魔等级: {}", enchantLevel);

        // 获取起始方块信息
        BlockState originState = level.getBlockState(origin);
        Block originBlock = originState.getBlock();
        LOGGER.info("[连锁挖掘] 目标方块: {}, 位置: {}", originBlock.getName().getString(), origin);

        // 计算挖掘参数
        int maxBlocksPerLevel = ModConfigManager.CHAIN_MINING_BLOCKS_PER_LEVEL.get();
        int configMaxBlocks = ModConfigManager.CHAIN_MINING_MAX_BLOCKS.get();
        int maxBlocks = Math.min(maxBlocksPerLevel * enchantLevel, configMaxBlocks);

        LOGGER.info("[连锁挖掘] 每级连锁方块数: {}, 附魔等级: {}, 最大连锁数: {}", maxBlocksPerLevel, enchantLevel, maxBlocks);

        // 转换为服务端对象
        ServerLevel serverLevel = (ServerLevel) level;
        ServerPlayer serverPlayer = player instanceof ServerPlayer ? (ServerPlayer) player : null;

        if (serverPlayer == null) {
            LOGGER.warn("[连锁挖掘] 无法获取服务端玩家实例，取消连锁挖掘");
            return;
        }

        // 执行连锁挖掘
        performChainMining(serverPlayer, serverLevel, tool, origin, originBlock, maxBlocks);
    }

    /**
     * 执行连锁挖掘的主要逻辑
     *
     * @param player      玩家
     * @param level       服务端世界
     * @param tool        工具
     * @param origin      原始方块位置
     * @param targetBlock 目标方块类型
     * @param maxBlocks   最大挖掘数量
     */
    private static void performChainMining(ServerPlayer player, ServerLevel level, ItemStack tool,
                                           BlockPos origin, Block targetBlock, int maxBlocks) {
        LOGGER.info("[连锁挖掘] 开始执行连锁挖掘，目标: {}, 最大数量: {}", targetBlock.getName().getString(), maxBlocks);

        // 设置方块查找范围
        int harvestRange = ModConfigManager.CHAIN_MINING_HARVEST_RANGE.get();
        LOGGER.info("[连锁挖掘] 设置方块查找范围: {}", harvestRange);

        // 使用BFS算法查找并挖掘相同类型的方块
        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(origin);
        visited.add(origin);
        int broken = 0;

        // BFS主循环
        while (!queue.isEmpty() && broken < maxBlocks) {
            BlockPos pos = queue.poll();

            // 验证方块类型
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() != targetBlock) {
                continue;
            }

            // 记录日志
            LOGGER.debug("[连锁挖掘] 破坏位置 {} 的方块，已破坏 {}/{} 个方块", pos, broken + 1, maxBlocks);

            // 破坏方块并应用工具附魔效果
            breakBlockWithEnchantments(level, player, pos, tool);
            broken++;

            // 消耗工具耐久
            if (!player.getAbilities().instabuild) {
                tool.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            }

            // 检查工具是否损坏
            if (tool.isEmpty() || tool.getDamageValue() >= tool.getMaxDamage()) {
                LOGGER.info("[连锁挖掘] 工具已损坏或耐久不足，停止连锁挖掘");
                break;
            }

            // 添加相邻方块到队列
            addAdjacentBlocks(level, pos, targetBlock, queue, visited, maxBlocks);
        }

        LOGGER.info("[连锁挖掘] 连锁挖掘完成，共破坏 {} 个方块", broken);
    }

    /**
     * 添加相邻方块到BFS队列
     *
     * @param level       世界
     * @param pos         当前方块位置
     * @param targetBlock 目标方块类型
     * @param queue       BFS队列
     * @param visited     已访问位置集合
     * @param maxBlocks   最大挖掘方块数
     */
    private static void addAdjacentBlocks(Level level, BlockPos pos, Block targetBlock,
                                          Queue<BlockPos> queue, Set<BlockPos> visited, int maxBlocks) {
        for (BlockPos adjacent : getAdjacent(pos)) {
            // 如果已经检查过足够多的方块，停止搜索
            if (visited.size() >= maxBlocks * 2) break;

            // 如果这个位置还没有被访问过
            if (!visited.contains(adjacent)) {
                visited.add(adjacent);

                // 检查相邻方块是否与原始方块相同
                BlockState adjState = level.getBlockState(adjacent);
                if (adjState.getBlock() == targetBlock) {
                    queue.add(adjacent);
                    LOGGER.debug("[连锁挖掘] 添加相邻方块到队列: {}", adjacent);
                }
            }
        }
    }

    /**
     * 使用带有附魔效果的工具来破坏方块
     * 保留精准采集和时运等附魔效果
     *
     * @param level  服务端世界
     * @param player 玩家
     * @param pos    方块位置
     * @param tool   工具
     */
    private static void breakBlockWithEnchantments(ServerLevel level, ServerPlayer player, BlockPos pos, ItemStack tool) {
        BlockState state = level.getBlockState(pos);
        BlockEntity blockEntity = level.getBlockEntity(pos);

        // 使用玩家的工具破坏方块
        boolean wasRemovedByPlayer = state.onDestroyedByPlayer(level, pos, player, true, level.getFluidState(pos));

        if (wasRemovedByPlayer) {
            state.getBlock().destroy(level, pos, state);

            // 处理掉落物和经验（仅非创造模式）
            handleBlockDrops(level, player, pos, state, blockEntity, tool);
        }
    }

    /**
     * 处理方块掉落物和经验
     *
     * @param level       服务端世界
     * @param player      玩家
     * @param pos         方块位置
     * @param state       方块状态
     * @param blockEntity 方块实体（如果存在）
     * @param tool        工具
     */
    private static void handleBlockDrops(ServerLevel level, ServerPlayer player, BlockPos pos,
                                         BlockState state, BlockEntity blockEntity, ItemStack tool) {
        // 创造模式下不处理掉落物
        if (player.getAbilities().instabuild) {
            return;
        }

        // 创建掉落参数
        LootParams.Builder lootParamsBuilder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, tool)
                .withParameter(LootContextParams.THIS_ENTITY, player);

        // 添加方块实体参数（如果存在）
        if (blockEntity != null) {
            lootParamsBuilder.withParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
        }

        // 生成掉落物品
        List<ItemStack> drops = state.getDrops(lootParamsBuilder);
        for (ItemStack drop : drops) {
            Block.popResource(level, pos, drop);
        }

        // 处理经验值掉落
        handleExperienceDrop(level, pos, state, tool);
    }

    /**
     * 处理经验值掉落
     *
     * @param level 服务端世界
     * @param pos   方块位置
     * @param state 方块状态
     * @param tool  工具
     */
    private static void handleExperienceDrop(ServerLevel level, BlockPos pos, BlockState state, ItemStack tool) {
        // 获取附魔等级
        int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.BLOCK_FORTUNE, tool);
        int silkTouchLevel = EnchantmentHelper.getItemEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.SILK_TOUCH, tool);

        // 计算经验值
        RandomSource randomSource = level.getRandom();
        int experience = state.getExpDrop(level, randomSource, pos, fortuneLevel, silkTouchLevel);
        if (experience > 0) {
            state.getBlock().popExperience(level, pos, experience);
        }
    }

    /**
     * 获取周围6个相邻方块的位置
     *
     * @param pos 中心方块位置
     * @return 相邻方块位置列表
     */
    private static List<BlockPos> getAdjacent(BlockPos pos) {
        List<BlockPos> list = new ArrayList<>(6);
        list.add(pos.north());
        list.add(pos.south());
        list.add(pos.east());
        list.add(pos.west());
        list.add(pos.above());
        list.add(pos.below());
        return list;
    }
}