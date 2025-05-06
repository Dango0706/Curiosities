package me.tuanzi.curiosities.crafting;

import com.google.gson.JsonObject;
import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

/**
 * 幸运剑合成配方条件
 * 只有当幸运剑功能在配置中启用时，才会启用相关合成配方
 */
public class LuckySwordEnabledCondition implements ICondition {
    private static final ResourceLocation NAME = new ResourceLocation("curiosities", "config_enabled");
    private final String config;

    public LuckySwordEnabledCondition(String config) {
        this.config = config;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        if ("lucky_sword".equals(config)) {
            return ModConfigManager.LUCKY_SWORD_ENABLED.get();
        }
        return false;
    }

    public static class Serializer implements IConditionSerializer<LuckySwordEnabledCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, LuckySwordEnabledCondition value) {
            json.addProperty("config", value.config);
        }

        @Override
        public LuckySwordEnabledCondition read(JsonObject json) {
            return new LuckySwordEnabledCondition(json.get("config").getAsString());
        }

        @Override
        public ResourceLocation getID() {
            return LuckySwordEnabledCondition.NAME;
        }
    }
} 