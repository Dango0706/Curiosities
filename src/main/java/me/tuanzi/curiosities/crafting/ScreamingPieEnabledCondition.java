package me.tuanzi.curiosities.crafting;

import com.google.gson.JsonObject;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

/**
 * 尖叫派配方启用条件
 * 用于根据配置控制尖叫派的配方显示
 */
public class ScreamingPieEnabledCondition implements ICondition {
    // 条件ID
    private static final ResourceLocation ID = new ResourceLocation(Curiosities.MODID, "screaming_pie_enabled");

    // 构造函数
    public ScreamingPieEnabledCondition() {
    }

    /**
     * 检查条件是否满足
     *
     * @param context 条件上下文
     * @return 尖叫派是否启用
     */
    @Override
    public boolean test(IContext context) {
        return ModConfigManager.SCREAMING_PIE_ENABLED.get();
    }

    /**
     * 获取条件ID
     *
     * @return 条件ID
     */
    @Override
    public ResourceLocation getID() {
        return ID;
    }

    /**
     * 尖叫派启用条件序列化器
     */
    public static class Serializer implements IConditionSerializer<ScreamingPieEnabledCondition> {
        // 序列化器ID
        private static final ResourceLocation ID = new ResourceLocation(Curiosities.MODID, "screaming_pie_enabled");

        /**
         * 获取序列化器ID
         *
         * @return 序列化器ID
         */
        @Override
        public ResourceLocation getID() {
            return ID;
        }

        /**
         * 从JSON读取条件
         *
         * @param json JSON对象
         * @return 尖叫派启用条件
         */
        @Override
        public ScreamingPieEnabledCondition read(JsonObject json) {
            return new ScreamingPieEnabledCondition();
        }

        /**
         * 将条件写入JSON
         *
         * @param condition 尖叫派启用条件
         * @param json      JSON对象
         */
        @Override
        public void write(JsonObject json, ScreamingPieEnabledCondition condition) {
            // 无需额外数据
        }
    }
} 