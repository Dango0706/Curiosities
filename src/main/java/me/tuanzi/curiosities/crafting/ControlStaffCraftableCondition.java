package me.tuanzi.curiosities.crafting;

import com.google.gson.JsonObject;
import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

/**
 * 控制之杖合成配方条件
 * 只有当控制之杖功能在配置中启用且可合成时，才会启用相关合成配方
 */
public class ControlStaffCraftableCondition implements ICondition {
    private static final ResourceLocation NAME = new ResourceLocation("curiosities", "control_staff_craftable");

    public ControlStaffCraftableCondition() {
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        // 检查控制之杖是否启用且可合成
        return ModConfigManager.CONTROL_STAFF_ENABLED.get() && ModConfigManager.CONTROL_STAFF_CRAFTABLE.get();
    }

    public static class Serializer implements IConditionSerializer<ControlStaffCraftableCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, ControlStaffCraftableCondition value) {
            // 无需额外参数
        }

        @Override
        public ControlStaffCraftableCondition read(JsonObject json) {
            return new ControlStaffCraftableCondition();
        }

        @Override
        public ResourceLocation getID() {
            return ControlStaffCraftableCondition.NAME;
        }
    }
} 