package me.tuanzi.curiosities.items;

import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 无限水桶物品
 * 可以无限倒出水，右键收水时可以收集3x3范围的水源
 */
public class InfiniteWaterBucketItem extends BucketItem {

    /**
     * 构造函数
     */
    public InfiniteWaterBucketItem() {
        super(Fluids.WATER, new Item.Properties().stacksTo(1));
    }

    /**
     * 使用物品时执行的逻辑
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // 如果禁用了无限水桶功能，则直接返回失败（物品不可使用）
        if (!ModConfigManager.INFINITE_WATER_BUCKET_ENABLED.get()) {
            player.displayClientMessage(Component.translatable("item.curiosities.disabled").withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        ItemStack itemStack = player.getItemInHand(hand);
        // 对于收集水源，我们只检测水源方块
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);

        if (hitResult.getType() == HitResult.Type.MISS) {
            // 如果没有命中水源，尝试放置水（检测任何流体，包括水流）
            hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
            if (hitResult.getType() != HitResult.Type.BLOCK) {
                return InteractionResultHolder.pass(itemStack);
            }

            BlockPos blockPos = hitResult.getBlockPos();
            Direction direction = hitResult.getDirection();
            BlockPos adjacentPos = blockPos.relative(direction);

            if (!level.mayInteract(player, blockPos) || !player.mayUseItemAt(adjacentPos, direction, itemStack)) {
                return InteractionResultHolder.fail(itemStack);
            }

            // 尝试放置水源
            if (tryPlaceWater(level, player, adjacentPos)) {
                level.playSound(player, adjacentPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResultHolder.success(itemStack);
            } else {
                return InteractionResultHolder.fail(itemStack);
            }
        } else if (hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemStack);
        }

        BlockPos blockPos = hitResult.getBlockPos();
        Direction direction = hitResult.getDirection();
        BlockPos adjacentPos = blockPos.relative(direction);

        if (!level.mayInteract(player, blockPos) || !player.mayUseItemAt(adjacentPos, direction, itemStack)) {
            return InteractionResultHolder.fail(itemStack);
        }

        BlockState blockState = level.getBlockState(blockPos);

        // 判断是否点击了水源方块
        if (blockState.getBlock() instanceof BucketPickup && blockState.getFluidState().is(FluidTags.WATER)) {
            // 收集3x3范围内的所有水源
            collectWaterInArea(level, blockPos, player);
            level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
            return InteractionResultHolder.success(itemStack);
        } else {
            // 尝试放置水源
            if (tryPlaceWater(level, player, adjacentPos)) {
                level.playSound(player, adjacentPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResultHolder.success(itemStack);
            } else {
                return InteractionResultHolder.fail(itemStack);
            }
        }
    }

    /**
     * 尝试在指定位置放置水
     */
    private boolean tryPlaceWater(Level level, Player player, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        // 修改判断逻辑，允许替换流动的水
        boolean canPlace = blockState.canBeReplaced() || (blockState.getBlock() instanceof LiquidBlock && blockState.getFluidState().is(FluidTags.WATER));

        if (canPlace) {
            if (!level.isClientSide) {
                level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
            }
            return true;
        }
        return false;
    }

    /**
     * 收集指定位置周围3x3范围内的所有水源
     */
    private void collectWaterInArea(Level level, BlockPos center, Player player) {
        if (!level.isClientSide()) {
            // 遍历3x3x3的范围
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        BlockPos pos = center.offset(x, y, z);
                        BlockState state = level.getBlockState(pos);

                        // 检查是否为水源方块
                        if (state.getBlock() == Blocks.WATER) {
                            // 移除水源
                            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        }
                    }
                }
            }
        }
    }

    /**
     * 添加物品工具提示
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // 添加物品描述
        tooltip.add(Component.translatable("item.curiosities.infinite_water_bucket.desc")
                .withStyle(ChatFormatting.GRAY));

        // 如果物品被禁用，添加禁用提示
        if (!ModConfigManager.INFINITE_WATER_BUCKET_ENABLED.get()) {
            tooltip.add(Component.translatable("item.curiosities.disabled")
                    .withStyle(ChatFormatting.RED));
        }
    }
} 