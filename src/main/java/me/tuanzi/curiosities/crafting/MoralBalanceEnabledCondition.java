package me.tuanzi.curiosities.crafting;

import com.google.gson.JsonObject;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.enchantments.moral_balance.MoralBalanceConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

/**
 * 道德天平附魔启用条件
 * 用于控制合成配方是否可用
 */
public class MoralBalanceEnabledCondition implements ICondition {
    // 条件ID
    private static final ResourceLocation ID = new ResourceLocation(Curiosities.MODID, "moral_balance_enabled");
    
    /**
     * 道德天平附魔启用条件构造函数
     */
    public MoralBalanceEnabledCondition() {}
    
    /**
     * 检查条件是否满足
     * 
     * @param context 合成条件上下文
     * @return 道德天平附魔是否启用
     */
    @Override
    public boolean test(IContext context) {
        return MoralBalanceConfig.isMoralBalanceEnabled();
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
     * 道德天平附魔启用条件序列化器
     */
    public static class Serializer implements IConditionSerializer<MoralBalanceEnabledCondition> {
        // 序列化器ID
        private static final ResourceLocation ID = MoralBalanceEnabledCondition.ID;
        
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
         * 从JSON解析条件
         * 
         * @param json JSON对象
         * @return 道德天平附魔启用条件
         */
        @Override
        public MoralBalanceEnabledCondition read(JsonObject json) {
            return new MoralBalanceEnabledCondition();
        }
        
        /**
         * 将条件写入JSON
         * 
         * @param json JSON对象
         * @param condition 道德天平附魔启用条件
         */
        @Override
        public void write(JsonObject json, MoralBalanceEnabledCondition condition) {
            // 无需额外数据
        }
    }
} 