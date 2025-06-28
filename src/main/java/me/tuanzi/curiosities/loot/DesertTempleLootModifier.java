package me.tuanzi.curiosities.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.items.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

/**
 * 沙漠神殿战利品修改器
 * 在沙漠神殿宝箱中添加概率圣剑
 */
public class DesertTempleLootModifier extends LootModifier {

    public static final Codec<DesertTempleLootModifier> CODEC = RecordCodecBuilder.create(
            inst -> codecStart(inst).apply(inst, DesertTempleLootModifier::new)
    );

    private static final ResourceLocation DESERT_TEMPLE_LOOT_TABLE =
            new ResourceLocation("minecraft", "chests/desert_pyramid");

    public DesertTempleLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // 检查是否启用概率圣剑
        if (!ModConfigManager.PROBABILITY_HOLY_SWORD_ENABLED.get()) {
            return generatedLoot;
        }

        // 检查是否为沙漠神殿宝箱
        ResourceLocation lootTableId = context.getQueriedLootTableId();
        if (lootTableId != null && lootTableId.equals(DESERT_TEMPLE_LOOT_TABLE)) {
            // 根据配置的概率决定是否添加概率圣剑
            RandomSource random = context.getRandom();
            double spawnChance = ModConfigManager.PROBABILITY_HOLY_SWORD_CHEST_SPAWN_CHANCE.get();

            if (random.nextDouble() < spawnChance) {
                generatedLoot.add(new ItemStack(ModItems.PROBABILITY_HOLY_SWORD.get()));
            }
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends DesertTempleLootModifier> codec() {
        return CODEC;
    }
}
