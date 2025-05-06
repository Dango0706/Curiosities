package me.tuanzi.curiosities.blocks;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * 假TNT方块
 * 外观与普通TNT相似，但行为不同：
 * - 左键点击点燃TNT
 * - 右键点击直接破坏方块
 */
public class FakeTntBlock extends Block {
    private static final Logger LOGGER = LogUtils.getLogger();

    public FakeTntBlock() {
        super(Properties.of()
                .mapColor(MapColor.COLOR_RED)
                .strength(0.5F)  // 设置方块硬度
                .sound(SoundType.GRASS)  // 设置方块声音
                .lightLevel((state) -> 0)  // 设置光照级别
        );
    }

    /**
     * 处理方块被右键点击
     */
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!ModConfigManager.FAKE_TNT_ENABLED.get()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            LOGGER.info("玩家 {} 右键点击了假TNT方块，游戏模式: {}",
                    player.getName().getString(),
                    player.getAbilities().instabuild ? "创造模式" : "生存模式");


            // 判断是否是创造模式，创造模式下不掉落物品
            boolean dropItems = !player.getAbilities().instabuild;
            LOGGER.info("是否应该掉落物品: {}", dropItems);

            // 破坏方块
            // 使用标准的destroyBlock方法，它会处理方块的掉落物表
            level.destroyBlock(pos, dropItems);
            LOGGER.info("使用标准方法破坏方块，掉落物参数: {}", dropItems);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.SUCCESS;
    }

    /**
     * 处理方块被左键点击
     */
    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!ModConfigManager.FAKE_TNT_ENABLED.get()) {
            super.attack(state, level, pos, player);
            return;
        }

        if (!level.isClientSide) {
            LOGGER.info("玩家 {} 左键点击了假TNT方块", player.getName().getString());

            // 移除方块
            level.removeBlock(pos, false);

            // 生成已激活的TNT实体
            PrimedTnt primedTnt = new PrimedTnt(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, null);
            level.addFreshEntity(primedTnt);

            // 播放点燃音效
            level.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    /**
     * 防止普通TNT的爆炸逻辑
     */
    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        if (!level.isClientSide && ModConfigManager.FAKE_TNT_ENABLED.get()) {
            // 不会因为爆炸而爆炸，而是直接掉落物品
            level.destroyBlock(pos, true);
        } else {
            super.wasExploded(level, pos, explosion);
        }
    }

    /**
     * 阻止默认的TNT爆炸逻辑
     */
    @Override
    public void onCaughtFire(BlockState state, Level level, BlockPos pos, net.minecraft.core.Direction direction, @Nullable LivingEntity igniter) {
        if (!ModConfigManager.FAKE_TNT_ENABLED.get()) {
            super.onCaughtFire(state, level, pos, direction, igniter);
            return;
        }

        // 仅掉落方块而不爆炸
        if (!level.isClientSide) {
            level.destroyBlock(pos, true);
        }
    }
} 