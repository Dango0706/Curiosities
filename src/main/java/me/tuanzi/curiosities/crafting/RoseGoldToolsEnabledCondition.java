package me.tuanzi.curiosities.crafting;

import com.google.gson.JsonObject;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

/**
 * 玫瑰金工具启用条件
 * 用于检查玫瑰金工具功能是否启用
 */
public class RoseGoldToolsEnabledCondition implements ICondition {
    /**
     * 条件ID
     */
    private static final ResourceLocation ID = new ResourceLocation(Curiosities.MODID, "rose_gold_tools_enabled");

    /**
     * 构造函数
     */
    public RoseGoldToolsEnabledCondition() {
    }

    /**
     * 检查条件是否满足（玫瑰金工具是否启用）
     */
    @Override
    public boolean test(ICondition.IContext context) {
        return ModConfigManager.ROSE_GOLD_TOOLS_ENABLED.get();
    }

    /**
     * 获取条件ID
     */
    @Override
    public ResourceLocation getID() {
        return ID;
    }

    /**
     * 条件序列化器
     */
    public static class Serializer implements IConditionSerializer<RoseGoldToolsEnabledCondition> {
        /**
         * 序列化器ID
         */
        private static final ResourceLocation ID = new ResourceLocation(Curiosities.MODID, "rose_gold_tools_enabled");

        /**
         * 获取序列化器ID
         */
        @Override
        public ResourceLocation getID() {
            return ID;
        }

        /**
         * 从JSON读取条件
         */
        @Override
        public RoseGoldToolsEnabledCondition read(JsonObject json) {
            return new RoseGoldToolsEnabledCondition();
        }

        /**
         * 将条件写入JSON
         */
        @Override
        public void write(JsonObject json, RoseGoldToolsEnabledCondition condition) {
            // 无需写入额外数据
        }
    }
}
