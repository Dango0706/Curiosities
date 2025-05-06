package me.tuanzi.curiosities.crafting;

import com.google.gson.JsonObject;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.config.ModConfigManager;
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
    public MoralBalanceEnabledCondition() {
    }

    /**
     * 检查条件是否满足
     *
     * @param context 合成条件上下文
     * @return 道德天平附魔是否启用
     */
    @Override
    public boolean test(IContext context) {
        return ModConfigManager.MORAL_BALANCE_ENABLED.get();
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
        private static final ResourceLocation NAME = new ResourceLocation(Curiosities.MODID, "moral_balance_enabled");

        @Override
        public void write(JsonObject json, MoralBalanceEnabledCondition value) {
            // 无需额外数据
        }

        @Override
        public MoralBalanceEnabledCondition read(JsonObject json) {
            return new MoralBalanceEnabledCondition();
        }

        @Override
        public ResourceLocation getID() {
            return NAME;
        }
    }
} 