package me.tuanzi.curiosities.crafting;

import com.google.gson.JsonObject;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.items.WolfFangPotatoConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

/**
 * 自定义配方条件：狼牙土豆是否启用
 * 用于控制根据配置启用或禁用狼牙土豆的合成配方
 */
public class WolfFangPotatoEnabledCondition implements ICondition {
    /**
     * 条件ID
     */
    private static final ResourceLocation ID = new ResourceLocation(Curiosities.MODID, "wolf_fang_potato_enabled");
    
    /**
     * 构造函数
     */
    public WolfFangPotatoEnabledCondition() {
    }
    
    /**
     * 检查条件是否满足（狼牙土豆是否启用）
     */
    @Override
    public boolean test(IContext context) {
        return WolfFangPotatoConfig.isWolfFangPotatoEnabled();
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
    public static class Serializer implements IConditionSerializer<WolfFangPotatoEnabledCondition> {
        /**
         * 序列化器ID
         */
        private static final ResourceLocation ID = new ResourceLocation(Curiosities.MODID, "wolf_fang_potato_enabled");
        
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
        public WolfFangPotatoEnabledCondition read(JsonObject json) {
            return new WolfFangPotatoEnabledCondition();
        }
        
        /**
         * 将条件写入JSON
         */
        @Override
        public void write(JsonObject json, WolfFangPotatoEnabledCondition condition) {
            // 无需写入额外数据
        }
    }
} 