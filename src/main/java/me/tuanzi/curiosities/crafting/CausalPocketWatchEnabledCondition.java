package me.tuanzi.curiosities.crafting;

import com.google.gson.JsonObject;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

/**
 * 因果怀表启用条件
 * 用于控制因果怀表的合成配方是否可用
 */
public class CausalPocketWatchEnabledCondition implements ICondition {
    private static final ResourceLocation NAME = new ResourceLocation(Curiosities.MODID, "causal_pocket_watch_enabled");

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        // 检查因果怀表是否启用且允许合成
        return ModConfigManager.CAUSAL_POCKET_WATCH_ENABLED.get() &&
                ModConfigManager.CAUSAL_POCKET_WATCH_CRAFTABLE.get();
    }

    public static class Serializer implements IConditionSerializer<CausalPocketWatchEnabledCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, CausalPocketWatchEnabledCondition value) {
            // 不需要写入任何数据
        }

        @Override
        public CausalPocketWatchEnabledCondition read(JsonObject json) {
            return new CausalPocketWatchEnabledCondition();
        }

        @Override
        public ResourceLocation getID() {
            return NAME;
        }
    }
}
