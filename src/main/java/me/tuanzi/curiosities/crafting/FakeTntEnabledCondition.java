package me.tuanzi.curiosities.crafting;

import com.google.gson.JsonObject;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

/**
 * 假TNT启用条件
 * 用于在配方中检查假TNT功能是否启用
 */
public class FakeTntEnabledCondition implements ICondition {
    private static final ResourceLocation ID = new ResourceLocation(Curiosities.MODID, "fake_tnt_enabled");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public boolean test(IContext context) {
        return ModConfigManager.FAKE_TNT_ENABLED.get();
    }

    public static class Serializer implements IConditionSerializer<FakeTntEnabledCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, FakeTntEnabledCondition value) {
            // 无需写入额外数据，条件本身就足够
        }

        @Override
        public FakeTntEnabledCondition read(JsonObject json) {
            return new FakeTntEnabledCondition();
        }

        @Override
        public ResourceLocation getID() {
            return FakeTntEnabledCondition.ID;
        }
    }
} 