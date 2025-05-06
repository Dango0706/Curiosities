package me.tuanzi.curiosities.effect;

import me.tuanzi.curiosities.Curiosities;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
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

    /**
     * 尖叫效果
     * 每5秒发出恶魂尖叫声，吸引周围敌对生物
     */
    public static final RegistryObject<MobEffect> SCREAMING = MOB_EFFECTS.register(
            "screaming",
            ScreamingEffect::new
    );

    /**
     * 颠颠倒倒效果
     * 使玩家控制方向相反，增加游戏挑战性
     */
    public static final RegistryObject<MobEffect> DIZZY = MOB_EFFECTS.register(
            "dizzy",
            DizzyEffect::new
    );

    /**
     * 天旋地转效果
     * 使玩家视角随机转动，根据效果等级增加转动幅度
     */
    public static final RegistryObject<MobEffect> SPINNING = MOB_EFFECTS.register(
            "spinning",
            SpinningEffect::new
    );

    /**
     * 注册所有状态效果
     *
     * @param eventBus Forge事件总线
     */
    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
} 