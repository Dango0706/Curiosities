package me.tuanzi.curiosities.crafting;

import com.google.gson.JsonObject;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

/**
 * 生物指南针启用条件
 * 用于控制生物指南针相关配方是否可用
 */
public class EntityCompassEnabledCondition implements ICondition {

    private static final ResourceLocation NAME = new ResourceLocation(Curiosities.MODID, "entity_compass_enabled");

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        return ModConfigManager.ENTITY_COMPASS_ENABLED.get() && ModConfigManager.ENTITY_COMPASS_CRAFTABLE.get();
    }

    public static class Serializer implements IConditionSerializer<EntityCompassEnabledCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, EntityCompassEnabledCondition value) {
            // 不需要写入额外数据
        }

        @Override
        public EntityCompassEnabledCondition read(JsonObject json) {
            return new EntityCompassEnabledCondition();
        }

        @Override
        public ResourceLocation getID() {
            return NAME;
        }
    }
}
