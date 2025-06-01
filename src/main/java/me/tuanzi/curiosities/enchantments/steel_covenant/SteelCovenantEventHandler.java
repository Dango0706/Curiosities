package me.tuanzi.curiosities.enchantments.steel_covenant;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.enchantments.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

/**
 * 钢契附魔事件处理器
 * 处理钢契附魔的伤害限制效果
 */
@Mod.EventBusSubscriber(modid = Curiosities.MODID)
public class SteelCovenantEventHandler {
    // 日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();

    // 钢契附魔每次限制伤害时消耗的耐久度
    private static final int DURABILITY_DAMAGE = 3;

    /**
     * 处理生物受伤事件，应用钢契附魔效果
     *
     * @param event 生物受伤事件
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingHurt(LivingHurtEvent event) {
        // 检查是否启用附魔
        if (!ModConfigManager.STEEL_COVENANT_ENABLED.get()) {
            return;
        }

        LivingEntity entity = event.getEntity();

        // 获取胸甲
        ItemStack chestplate = entity.getItemBySlot(EquipmentSlot.CHEST);

        // 获取胸甲上的钢契附魔等级
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.STEEL_COVENANT.get(), chestplate);

        if (enchantLevel > 0) {
            float originalDamage = event.getAmount();

            // 计算伤害上限：20 - (level-1) * 2.5
            float maxDamage = 20.0f - (enchantLevel - 1) * 2.5f;

            // 如果原始伤害高于上限，则限制伤害值
            if (originalDamage > maxDamage) {
                event.setAmount(maxDamage);
                LOGGER.debug("钢契附魔限制玩家受到的伤害: {} -> {}", originalDamage, maxDamage);

                // 对胸甲造成耐久损耗
                if (!chestplate.isEmpty()) {
                    // 使用原版API进行耐久度损耗
                    chestplate.hurtAndBreak(DURABILITY_DAMAGE, entity, e ->
                            e.broadcastBreakEvent(EquipmentSlot.CHEST));
                    LOGGER.debug("钢契附魔对胸甲造成{}点耐久损耗", DURABILITY_DAMAGE);
                }
            }
        }
    }
} 