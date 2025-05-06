package me.tuanzi.curiosities.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import me.tuanzi.curiosities.items.bee_grenade.BeeGrenadeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * 蜜蜂手雷实体渲染器
 * 负责渲染投掷中的蜜蜂手雷
 */
public class BeeGrenadeRenderer extends EntityRenderer<BeeGrenadeEntity> {
    private static final float SCALE = 0.75F; // 渲染比例
    private final ItemRenderer itemRenderer;

    public BeeGrenadeRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.shadowRadius = 0.15F; // 阴影大小
        this.shadowStrength = 0.5F; // 阴影强度
    }

    /**
     * 渲染蜜蜂手雷实体
     */
    @Override
    public void render(BeeGrenadeEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // 旋转物品
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F));

        // 缩放物品
        poseStack.scale(SCALE, SCALE, SCALE);

        // 获取物品并渲染
        ItemStack itemStack = entity.getItem();
        this.itemRenderer.renderStatic(itemStack, ItemDisplayContext.GROUND,
                packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    /**
     * 获取实体纹理
     */
    @Override
    public ResourceLocation getTextureLocation(BeeGrenadeEntity entity) {
        return new ResourceLocation("textures/item/snowball.png"); // 使用雪球纹理作为后备
    }
} 