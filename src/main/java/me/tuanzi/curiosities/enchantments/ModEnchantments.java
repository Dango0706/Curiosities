package me.tuanzi.curiosities.enchantments;

import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.enchantments.chain_mining.ChainMiningEnchantment;
import me.tuanzi.curiosities.enchantments.moral_balance.MoralBalanceEnchantment;
import me.tuanzi.curiosities.enchantments.proficiency.ProficiencyEnchantment;
import me.tuanzi.curiosities.enchantments.steel_covenant.SteelCovenantEnchantment;
import me.tuanzi.curiosities.enchantments.super_fortune.SuperFortuneEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 模组附魔注册类
 * 负责注册所有模组附魔
 */
public class ModEnchantments {
    /**
     * 附魔注册表
     */
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Curiosities.MODID);

    /**
     * 连锁挖掘附魔
     * 用于工具，使其能够连锁挖掘相同类型的方块
     */
    public static final RegistryObject<Enchantment> CHAIN_MINING = ENCHANTMENTS.register("chain_mining", ChainMiningEnchantment::new);

    /**
     * 超级时运附魔
     * 效果是原版时运的1.5倍，可以与精准采集兼容
     */
    public static final RegistryObject<Enchantment> SUPER_FORTUNE = ENCHANTMENTS.register("super_fortune", SuperFortuneEnchantment::new);

    /**
     * 道德天平附魔
     * 用于武器，随机增加或减少伤害，并可能对攻击者施加罪恶效果
     */
    public static final RegistryObject<Enchantment> MORAL_BALANCE = ENCHANTMENTS.register("moral_balance", MoralBalanceEnchantment::new);

    /**
     * 熟练附魔
     * 用于武器，每级增加一定百分比的攻击速度
     */
    public static final RegistryObject<Enchantment> PROFICIENCY = ENCHANTMENTS.register("proficiency", ProficiencyEnchantment::new);
    
    /**
     * 钢契附魔
     * 用于胸甲和头盔，限制玩家受到的最大伤害
     */
    public static final RegistryObject<Enchantment> STEEL_COVENANT = ENCHANTMENTS.register("steel_covenant", SteelCovenantEnchantment::new);
}