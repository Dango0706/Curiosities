package me.tuanzi.curiosities.items.bee_grenade;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 蜜蜂手雷物品
 * 允许玩家投掷蜜蜂手雷，爆炸后释放愤怒的蜜蜂
 */
public class BeeGrenadeItem extends Item {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 构造函数
     */
    public BeeGrenadeItem() {
        super(new Properties().stacksTo(16));
    }

    /**
     * 添加物品悬停文本
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltip, isAdvanced);

        // 添加蜜蜂数量信息
        tooltip.add(Component.translatable("item.curiosities.bee_grenade.tooltip.bee_count", ModConfigManager.BEE_GRENADE_BEE_COUNT.get())
                .withStyle(ChatFormatting.YELLOW));

        // 如果配置禁用了蜜蜂手雷，显示警告信息
        if (!ModConfigManager.BEE_GRENADE_ENABLED.get()) {
            tooltip.add(Component.translatable("item.curiosities.bee_grenade.tooltip.disabled")
                    .withStyle(ChatFormatting.RED));
        }
    }

    /**
     * 处理右键使用动作 - 投掷蜜蜂手雷
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // 检查是否启用
        if (!ModConfigManager.BEE_GRENADE_ENABLED.get()) {
            return InteractionResultHolder.fail(itemStack);
        }

        // 播放扔东西的声音
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL,
                0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

        // 冷却时间
        if (!level.isClientSide) {
            // 创建并投掷蜜蜂手雷实体
            BeeGrenadeEntity beeGrenade = new BeeGrenadeEntity(level, player);
            beeGrenade.setItem(itemStack);
            beeGrenade.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(beeGrenade);

            LOGGER.debug("玩家 {} 投掷了蜜蜂手雷", player.getName().getString());
        }

        // 增加统计信息
        player.awardStat(Stats.ITEM_USED.get(this));

        // 在非创造模式下减少物品数量
        if (!player.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
} 