package me.tuanzi.curiosities.crafting;

import com.google.gson.JsonObject;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

/**
 * 概率圣剑启用条件
 * 用于控制概率圣剑相关配方是否可用
 */
public class ProbabilityHolySwordEnabledCondition implements ICondition {

    private static final ResourceLocation NAME = new ResourceLocation(Curiosities.MODID, "probability_holy_sword_enabled");

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        return ModConfigManager.PROBABILITY_HOLY_SWORD_ENABLED.get();
    }

    public static class Serializer implements IConditionSerializer<ProbabilityHolySwordEnabledCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, ProbabilityHolySwordEnabledCondition value) {
            // 不需要写入额外数据
        }

        @Override
        public ProbabilityHolySwordEnabledCondition read(JsonObject json) {
            return new ProbabilityHolySwordEnabledCondition();
        }

        @Override
        public ResourceLocation getID() {
            return NAME;
        }
    }
}
