package me.tuanzi.curiosities.client;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.client.renderer.BeeGrenadeRenderer;
import me.tuanzi.curiosities.entities.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

/**
 * 客户端设置类
 * 负责注册客户端专用的渲染器和其他资源
 */
@Mod.EventBusSubscriber(modid = Curiosities.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 注册实体渲染器
     */
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        LOGGER.info("注册实体渲染器");
        event.registerEntityRenderer(ModEntities.BEE_GRENADE.get(), BeeGrenadeRenderer::new);
    }
} 