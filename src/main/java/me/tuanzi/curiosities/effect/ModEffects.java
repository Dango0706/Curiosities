package me.tuanzi.curiosities.effect;

import me.tuanzi.curiosities.Curiosities;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 模组状态效果注册类
 * 负责注册所有模组自定义状态效果
 */
public class ModEffects {
    /**
     * 状态效果注册表
     */
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Curiosities.MODID);

    /**
     * 狼群领袖效果
     * 使半径30格内的狼/狗会协助攻击持有者锁定的目标，但会被熊猫敌视
     */
    public static final RegistryObject<MobEffect> WOLF_PACK_LEADER = MOB_EFFECTS.register(
            "wolf_pack_leader", 
            WolfPackLeaderEffect::new
    );
    
    /**
     * 罪恶效果
     * 当玩家拥有此效果时，村民交易价格会提高
     */
    public static final RegistryObject<MobEffect> GUILT = MOB_EFFECTS.register(
            "guilt", 
            GuiltEffect::new
    );
} 