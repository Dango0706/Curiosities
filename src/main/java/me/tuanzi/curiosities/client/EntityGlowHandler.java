package me.tuanzi.curiosities.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 客户端生物发光效果处理器
 * 负责为生物指南针找到的生物添加发光效果
 */
@Mod.EventBusSubscriber(modid = "curiosities", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class EntityGlowHandler {

    // 存储需要发光的生物UUID和对应的玩家UUID
    private static final Map<UUID, UUID> glowingEntities = new HashMap<>();

    // 发光效果持续时间（毫秒）
    private static final long GLOW_DURATION = 30000; // 30秒

    // 存储发光开始时间
    private static final Map<UUID, Long> glowStartTimes = new HashMap<>();

    // 存储生物的原始发光状态
    private static final Map<UUID, Boolean> originalGlowStates = new HashMap<>();
    private static final int CLEANUP_INTERVAL = 100; // 5秒 (20 ticks/秒 * 5)
    // 清理计数器（每5秒清理一次）
    private static int cleanupCounter = 0;

    /**
     * 添加生物发光效果（仅对当前客户端玩家可见）
     * 使用自定义渲染系统而不是药水效果
     */
    public static void addGlowingEntity(Entity entity) {
        System.out.println("[EntityGlowHandler] addGlowingEntity 被调用");
        if (entity != null) {
            System.out.println("[EntityGlowHandler] 生物不为空: " + entity.getType().getDescriptionId());
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null) {
                UUID entityUUID = entity.getUUID();
                UUID playerUUID = minecraft.player.getUUID();

                System.out.println("[EntityGlowHandler] 玩家存在: " + minecraft.player.getName().getString());

                // 保存原始发光状态
                if (!originalGlowStates.containsKey(entityUUID)) {
                    originalGlowStates.put(entityUUID, entity.isCurrentlyGlowing());
                    System.out.println("[EntityGlowHandler] 保存原始发光状态: " + entity.isCurrentlyGlowing());
                }

                glowingEntities.put(entityUUID, playerUUID);
                glowStartTimes.put(entityUUID, System.currentTimeMillis());

                System.out.println("[EntityGlowHandler] 添加到自定义发光列表，发光实体列表大小: " + glowingEntities.size());
            } else {
                System.out.println("[EntityGlowHandler] 玩家为空");
            }
        } else {
            System.out.println("[EntityGlowHandler] 生物为空");
        }
    }

    /**
     * 移除生物发光效果
     */
    public static void removeGlowingEntity(Entity entity) {
        if (entity != null) {
            UUID entityUUID = entity.getUUID();

            System.out.println("[EntityGlowHandler] 从自定义发光列表中移除生物");


            glowingEntities.remove(entityUUID);
            glowStartTimes.remove(entityUUID);
            originalGlowStates.remove(entityUUID);
        }
    }

    /**
     * 清除所有发光效果
     */
    public static void clearAllGlowing() {
        glowingEntities.clear();
        glowStartTimes.clear();
        originalGlowStates.clear();
        System.out.println("[EntityGlowHandler] 清除所有自定义发光效果");
    }

    /**
     * 检查生物是否应该发光（仅对当前客户端玩家）
     */
    public static boolean shouldGlow(Entity entity) {
        if (entity == null) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return false;
        }

        UUID entityUUID = entity.getUUID();
        UUID currentPlayerUUID = minecraft.player.getUUID();

        // 检查是否是当前玩家触发的发光效果
        UUID glowPlayerUUID = glowingEntities.get(entityUUID);
        if (glowPlayerUUID == null || !glowPlayerUUID.equals(currentPlayerUUID)) {
            return false;
        }

        // 检查发光时间是否过期
        Long startTime = glowStartTimes.get(entityUUID);
        if (startTime != null && System.currentTimeMillis() - startTime > GLOW_DURATION) {
            // 发光时间过期，移除
            removeGlowingEntity(entity);
            return false;
        }

        return true;
    }

    /**
     * 渲染生物事件处理 - 在生物渲染前检查发光状态
     */
    @SubscribeEvent
    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
        LivingEntity entity = event.getEntity();
        UUID entityUUID = entity.getUUID();

        // 检查是否应该为此生物添加发光效果
        if (shouldGlow(entity)) {
            renderCustomGlowEffect(entity, event.getPoseStack(), event.getMultiBufferSource(),
                    event.getPackedLight(), event.getPartialTick());
        }
    }

    /**
     * 渲染自定义发光效果
     * 使用简化的方法来实现发光效果，避免复杂的轮廓渲染
     */
    private static void renderCustomGlowEffect(LivingEntity entity, PoseStack poseStack,
                                               MultiBufferSource bufferSource, int packedLight, float partialTick) {
        try {
            Minecraft minecraft = Minecraft.getInstance();
            EntityRenderDispatcher renderDispatcher = minecraft.getEntityRenderDispatcher();

            // 保存当前渲染状态
            poseStack.pushPose();

            // 启用混合和深度测试
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(GL11.GL_LEQUAL);

            // 设置发光颜色 (青色发光效果)
            RenderSystem.setShaderColor(0.0f, 1.0f, 1.0f, 0.8f);

            // 创建轮廓缓冲源
            if (bufferSource instanceof MultiBufferSource.BufferSource bufferSourceImpl) {
                OutlineBufferSource outlineBufferSource = minecraft.renderBuffers().outlineBufferSource();
                outlineBufferSource.setColor(0, 255, 255, 255); // 青色轮廓

                // 渲染发光轮廓
                renderDispatcher.render(entity, entity.getX() - renderDispatcher.camera.getPosition().x,
                        entity.getY() - renderDispatcher.camera.getPosition().y,
                        entity.getZ() - renderDispatcher.camera.getPosition().z,
                        entity.getYRot(), partialTick, poseStack, outlineBufferSource, packedLight);

                outlineBufferSource.endOutlineBatch();
            }
            // 恢复渲染状态
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.disableBlend();
            poseStack.popPose();


        } catch (Exception e) {
            System.err.println("[EntityGlowHandler] 渲染发光效果时出错: " + e.getMessage());
        }
    }

    /**
     * 客户端tick事件处理
     * 定期清理过期的发光效果
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            cleanupCounter++;
            if (cleanupCounter >= CLEANUP_INTERVAL) {
                cleanupExpiredGlow();
                cleanupCounter = 0;
            }
        }
    }

    /**
     * 定期清理过期的发光效果
     */
    public static void cleanupExpiredGlow() {
        long currentTime = System.currentTimeMillis();

        glowStartTimes.entrySet().removeIf(entry -> {
            if (currentTime - entry.getValue() > GLOW_DURATION) {
                UUID entityUUID = entry.getKey();

                // 清理相关数据
                glowingEntities.remove(entityUUID);
                originalGlowStates.remove(entityUUID);
                System.out.println("[EntityGlowHandler] 清理过期的发光效果: " + entityUUID);
                return true;
            }
            return false;
        });
    }
}
