package me.tuanzi.curiosities.events;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.Random;

/**
 * 客户端事件处理类
 * 用于实现天旋地转效果，自动平滑随机旋转玩家视角，不需要玩家输入
 */
@Mod.EventBusSubscriber(modid = Curiosities.MODID, value = Dist.CLIENT)
public class CameraEvents {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Random RANDOM = new Random();
    // 目标更新间隔（毫秒）
    private static final long TARGET_UPDATE_INTERVAL = 1000; // 1秒更新一次目标
    // 旋转幅度基础值
    private static final float BASE_ROTATION_AMOUNT = 5.0f;
    // 垂直旋转幅度系数（相对水平旋转的比例）
    private static final float PITCH_ROTATION_FACTOR = 0.3f;
    // 平滑因子（值越小越平滑）
    private static final float SMOOTH_FACTOR = 0.05f;
    // 最大垂直角度限制（避免过度朝天/朝地）
    private static final float MAX_VERTICAL_ANGLE = 60.0f;
    // 保存当前的旋转偏移量
    private static float yawOffset = 0;
    private static float pitchOffset = 0;
    // 保存目标旋转偏移量（渐变目标）
    private static float targetYawOffset = 0;
    private static float targetPitchOffset = 0;
    // 上次更新目标的时间
    private static long lastTargetUpdateTime = 0;

    /**
     * 客户端Tick事件处理器
     * 主动修改玩家视角，实现自动旋转效果
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        // 只在阶段结束时处理，避免一帧处理两次
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        // 检查游戏是否暂停、玩家是否存在
        if (minecraft.isPaused() || minecraft.player == null) {
            return;
        }

        Player player = minecraft.player;

        // 检查效果是否启用
        if (!ModConfigManager.SPINNING_EFFECT_ENABLED.get()) {
            resetRotation();
            return;
        }

        // 检查玩家是否有天旋地转效果
        MobEffectInstance effectInstance = player.getEffect(ModEffects.SPINNING.get());
        if (effectInstance == null) {
            // 没有效果时，渐渐恢复正常视角
            resetRotation();
            return;
        }

        // 获取当前时间
        long currentTime = System.currentTimeMillis();

        // 获取效果等级（从0开始）
        int amplifier = effectInstance.getAmplifier();

        // 计算实际旋转幅度，随等级增加
        float rotationAmount = BASE_ROTATION_AMOUNT * (amplifier + 1);

        // 定期更新目标旋转角度
        if (currentTime - lastTargetUpdateTime > TARGET_UPDATE_INTERVAL) {
            // 生成新的随机目标
            targetYawOffset = (RANDOM.nextFloat() * 2 - 1) * rotationAmount;

            // 获取当前俯仰角，用于智能调整
            float currentPitch = player.getXRot();

            // 根据当前俯仰角调整垂直旋转方向，避免过度朝天/朝地
            float pitchDirectionBias = 0;
            if (Math.abs(currentPitch) > 30) {
                // 如果当前已经有一定的俯仰角，倾向于向中间位置旋转
                pitchDirectionBias = -Math.signum(currentPitch) * 0.5f;
            }

            // 垂直方向的旋转幅度较小，并根据当前俯仰角调整偏向
            // 使用(RANDOM.nextFloat() * 1.5f - 0.75f + pitchDirectionBias)生成范围约为[-0.75+bias, 0.75+bias]的随机数
            // 这样可以在保持随机性的同时，根据当前视角适当偏向中间位置
            targetPitchOffset = (RANDOM.nextFloat() * 1.5f - 0.75f + pitchDirectionBias)
                    * rotationAmount * PITCH_ROTATION_FACTOR;

            lastTargetUpdateTime = currentTime;

            // 记录日志
            if (RANDOM.nextInt(5) == 0) { // 20%概率记录，避免刷日志
                LOGGER.debug("更新旋转目标: yaw={}, pitch={}, 等级={}, 当前pitch={}",
                        targetYawOffset, targetPitchOffset, amplifier, currentPitch);
            }
        }

        // 根据效果等级调整平滑度
        float smoothFactor = SMOOTH_FACTOR * (1 + amplifier * 0.5f);

        // 平滑插值当前偏移量到目标偏移量
        yawOffset += (targetYawOffset - yawOffset) * smoothFactor;
        pitchOffset += (targetPitchOffset - pitchOffset) * smoothFactor;

        // 直接设置玩家的视角朝向（客户端）
        // 保存原始角度
        float originalYaw = player.getYRot();
        float originalPitch = player.getXRot();

        // 应用视角旋转
        player.setYRot(originalYaw + yawOffset);

        // 计算新的俯仰角，同时确保不会过度向上或向下
        float newPitch = originalPitch + pitchOffset;

        // 进一步限制垂直视角范围，比Minecraft默认的限制更严格
        newPitch = Math.max(-MAX_VERTICAL_ANGLE, Math.min(MAX_VERTICAL_ANGLE, newPitch));

        player.setXRot(newPitch);
    }

    /**
     * 重置旋转状态，平滑恢复正常视角
     */
    private static void resetRotation() {
        // 将目标设为0
        targetYawOffset = 0;
        targetPitchOffset = 0;

        // 平滑减小当前偏移
        yawOffset *= 0.9f;
        pitchOffset *= 0.9f;

        // 如果足够小，直接归零
        if (Math.abs(yawOffset) < 0.01f && Math.abs(pitchOffset) < 0.01f) {
            yawOffset = 0;
            pitchOffset = 0;
        }
    }
} 