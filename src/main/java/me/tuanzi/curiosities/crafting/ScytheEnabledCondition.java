package me.tuanzi.curiosities.crafting;

import com.google.gson.JsonObject;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.items.scythe.ScytheConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

/**
 * 镰刀启用条件
 * 用于检查镰刀功能是否启用
 */
public class ScytheEnabledCondition implements ICondition {
    /**
     * 条件ID
     */
    private static final ResourceLocation ID = new ResourceLocation(Curiosities.MODID, "scythe_enabled");
    
    /**
     * 构造函数
     */
    public ScytheEnabledCondition() {
    }
    
    /**
     * 检查条件是否满足（镰刀是否启用）
     */
    @Override
    public boolean test(ICondition.IContext context) {
        return ScytheConfig.isScytheEnabled();
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
    public static class Serializer implements IConditionSerializer<ScytheEnabledCondition> {
        /**
         * 序列化器ID
         */
        private static final ResourceLocation ID = new ResourceLocation(Curiosities.MODID, "scythe_enabled");
        
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
        public ScytheEnabledCondition read(JsonObject json) {
            return new ScytheEnabledCondition();
        }
        
        /**
         * 将条件写入JSON
         */
        @Override
        public void write(JsonObject json, ScytheEnabledCondition condition) {
            // 无需写入额外数据
        }
    }
} 