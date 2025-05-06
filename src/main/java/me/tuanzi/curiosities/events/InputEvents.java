package me.tuanzi.curiosities.events;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

/**
 * 输入事件处理器
 * 用于处理颠颠倒倒效果的输入反转
 */
@Mod.EventBusSubscriber(modid = Curiosities.MODID, value = Dist.CLIENT)
public class InputEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 处理移动输入更新事件
     * 当玩家有颠颠倒倒效果时，反转WASD键的移动方向
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onMovementInputUpdate(MovementInputUpdateEvent event) {
        // 检查效果是否启用
        if (!ModConfigManager.DIZZY_EFFECT_ENABLED.get()) {
            return;
        }

        Player player = event.getEntity();

        // 检查玩家是否有颠颠倒倒效果
        if (player.hasEffect(ModEffects.DIZZY.get())) {
            // 获取玩家的输入对象
            Input input = event.getInput();

            // 直接反转前进和横向移动的值
            input.leftImpulse = -input.leftImpulse;
            input.forwardImpulse = -input.forwardImpulse;

            // 交换按键状态，确保GUI和其他功能也能正常工作
            boolean tempUp = input.up;
            boolean tempDown = input.down;
            boolean tempLeft = input.left;
            boolean tempRight = input.right;

            input.up = tempDown;
            input.down = tempUp;
            input.left = tempRight;
            input.right = tempLeft;

            // 记录调试信息
            if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.tickCount % 20 == 0) {
                LOGGER.debug("颠颠倒倒效果生效，控制方向已反转，前进值: {}, 横向值: {}",
                        input.forwardImpulse, input.leftImpulse);
            }
        }
    }
} 