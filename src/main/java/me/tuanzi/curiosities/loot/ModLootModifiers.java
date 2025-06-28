package me.tuanzi.curiosities.loot;

import com.mojang.serialization.Codec;
import me.tuanzi.curiosities.Curiosities;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 模组战利品修改器注册类
 */
public class ModLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Curiosities.MODID);

    public static final RegistryObject<Codec<DesertTempleLootModifier>> DESERT_TEMPLE_LOOT_MODIFIER =
            LOOT_MODIFIERS.register("desert_temple_loot_modifier", () -> DesertTempleLootModifier.CODEC);
}
