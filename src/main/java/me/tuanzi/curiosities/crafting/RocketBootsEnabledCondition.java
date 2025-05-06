package me.tuanzi.curiosities.crafting;

import com.google.gson.JsonObject;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

/**
 * 火箭靴启用条件
 * 用于控制火箭靴相关合成配方的可用性
 */
public class RocketBootsEnabledCondition implements ICondition {
    private static final ResourceLocation NAME = new ResourceLocation(Curiosities.MODID, "rocket_boots_enabled");

    public RocketBootsEnabledCondition() {
    }

    /**
     * 检查条件是否满足
     *
     * @return 如果火箭靴功能启用，则返回true
     */
    @Override
    public boolean test(IContext context) {
        return ModConfigManager.ROCKET_BOOTS_ENABLED.get();
    }

    /**
     * 获取条件的名称
     *
     * @return 条件名称资源位置
     */
    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    /**
     * 条件序列化器内部类
     * 处理JSON序列化和反序列化
     */
    public static class Serializer implements IConditionSerializer<RocketBootsEnabledCondition> {
        private static final ResourceLocation NAME = new ResourceLocation(Curiosities.MODID, "rocket_boots_enabled");

        /**
         * 获取条件的名称
         *
         * @return 条件名称资源位置
         */
        @Override
        public ResourceLocation getID() {
            return NAME;
        }

        /**
         * 从JSON反序列化条件
         *
         * @param json JSON对象
         * @return 火箭靴启用条件
         */
        @Override
        public RocketBootsEnabledCondition read(JsonObject json) {
            return new RocketBootsEnabledCondition();
        }

        /**
         * 将条件序列化为JSON
         *
         * @param condition 火箭靴启用条件
         * @param json      JSON对象
         */
        @Override
        public void write(JsonObject json, RocketBootsEnabledCondition condition) {
            // 无需添加任何属性
        }
    }
} 