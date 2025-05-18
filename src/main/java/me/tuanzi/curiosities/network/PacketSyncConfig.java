package me.tuanzi.curiosities.network;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 配置同步数据包
 * 用于服务端向客户端同步配置值
 */
public class PacketSyncConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<String, Boolean> booleanConfigs = new HashMap<>();
    private final Map<String, Integer> intConfigs = new HashMap<>();
    private final Map<String, Double> doubleConfigs = new HashMap<>();

    /**
     * 默认构造函数，用于接收数据
     */
    public PacketSyncConfig(FriendlyByteBuf buffer) {
        // 读取布尔值配置
        int booleanSize = buffer.readInt();
        for (int i = 0; i < booleanSize; i++) {
            String key = buffer.readUtf();
            boolean value = buffer.readBoolean();
            booleanConfigs.put(key, value);
        }

        // 读取整数配置
        int intSize = buffer.readInt();
        for (int i = 0; i < intSize; i++) {
            String key = buffer.readUtf();
            int value = buffer.readInt();
            intConfigs.put(key, value);
        }

        // 读取浮点数配置
        int doubleSize = buffer.readInt();
        for (int i = 0; i < doubleSize; i++) {
            String key = buffer.readUtf();
            double value = buffer.readDouble();
            doubleConfigs.put(key, value);
        }
    }

    /**
     * 创建数据包实例，包含当前所有配置
     */
    public PacketSyncConfig() {
        // 填充布尔值配置
        booleanConfigs.put("chain_mining_enabled", ModConfigManager.CHAIN_MINING_ENABLED.get());
        booleanConfigs.put("super_fortune_enabled", ModConfigManager.SUPER_FORTUNE_ENABLED.get());
        booleanConfigs.put("wolf_fang_potato_enabled", ModConfigManager.WOLF_FANG_POTATO_ENABLED.get());
        booleanConfigs.put("scythe_enabled", ModConfigManager.SCYTHE_ENABLED.get());
        booleanConfigs.put("rocket_boots_enabled", ModConfigManager.ROCKET_BOOTS_ENABLED.get());
        booleanConfigs.put("moral_balance_enabled", ModConfigManager.MORAL_BALANCE_ENABLED.get());
        booleanConfigs.put("fake_tnt_enabled", ModConfigManager.FAKE_TNT_ENABLED.get());
        booleanConfigs.put("lucky_sword_enabled", ModConfigManager.LUCKY_SWORD_ENABLED.get());
        booleanConfigs.put("screaming_pie_enabled", ModConfigManager.SCREAMING_PIE_ENABLED.get());
        booleanConfigs.put("bat_wing_enabled", ModConfigManager.BAT_WING_ENABLED.get());
        booleanConfigs.put("bee_grenade_enabled", ModConfigManager.BEE_GRENADE_ENABLED.get());
        booleanConfigs.put("bee_grenade_player_friendly", ModConfigManager.BEE_GRENADE_PLAYER_FRIENDLY.get());
        booleanConfigs.put("bee_grenade_honey_slowness_area_enabled", ModConfigManager.BEE_GRENADE_HONEY_SLOWNESS_AREA_ENABLED.get());
        booleanConfigs.put("bee_grenade_destroy_blocks", ModConfigManager.BEE_GRENADE_DESTROY_BLOCKS.get());
        booleanConfigs.put("proficiency_enabled", ModConfigManager.PROFICIENCY_ENABLED.get());
        booleanConfigs.put("scroll_of_spacetime_enabled", ModConfigManager.SCROLL_OF_SPACETIME_ENABLED.get());
        booleanConfigs.put("scroll_of_spacetime_tradeable", ModConfigManager.SCROLL_OF_SPACETIME_TRADEABLE.get());
        booleanConfigs.put("void_sword_enabled", ModConfigManager.VOID_SWORD_ENABLED.get());
        booleanConfigs.put("infinite_water_bucket_enabled", ModConfigManager.INFINITE_WATER_BUCKET_ENABLED.get());

        // 填充整数配置
        intConfigs.put("chain_mining_max_blocks", ModConfigManager.CHAIN_MINING_MAX_BLOCKS.get());
        intConfigs.put("chain_mining_blocks_per_level", ModConfigManager.CHAIN_MINING_BLOCKS_PER_LEVEL.get());
        intConfigs.put("chain_mining_harvest_range", ModConfigManager.CHAIN_MINING_HARVEST_RANGE.get());
        intConfigs.put("rocket_boots_fuel_consumption", ModConfigManager.ROCKET_BOOTS_FUEL_CONSUMPTION.get());
        intConfigs.put("rocket_boots_max_fuel", ModConfigManager.ROCKET_BOOTS_MAX_FUEL.get());
        intConfigs.put("screaming_pie_slow_falling_duration", ModConfigManager.SCREAMING_PIE_SLOW_FALLING_DURATION.get());
        intConfigs.put("screaming_pie_screaming_duration", ModConfigManager.SCREAMING_PIE_SCREAMING_DURATION.get());
        intConfigs.put("bee_grenade_bee_count", ModConfigManager.BEE_GRENADE_BEE_COUNT.get());
        intConfigs.put("bee_grenade_bee_lifetime", ModConfigManager.BEE_GRENADE_BEE_LIFETIME.get());
        intConfigs.put("bee_grenade_honey_area_duration", ModConfigManager.BEE_GRENADE_HONEY_AREA_DURATION.get());
        intConfigs.put("scroll_of_spacetime_max_distance", ModConfigManager.SCROLL_OF_SPACETIME_MAX_DISTANCE.get());
        intConfigs.put("scroll_of_spacetime_cooldown", ModConfigManager.SCROLL_OF_SPACETIME_COOLDOWN.get());
        intConfigs.put("scroll_of_spacetime_durability_cost", ModConfigManager.SCROLL_OF_SPACETIME_DURABILITY_COST.get());
        intConfigs.put("void_sword_max_energy", ModConfigManager.VOID_SWORD_MAX_ENERGY.get());
        intConfigs.put("void_sword_black_hole_duration", ModConfigManager.VOID_SWORD_BLACK_HOLE_DURATION.get());
        intConfigs.put("void_sword_black_hole_damage_interval", ModConfigManager.VOID_SWORD_BLACK_HOLE_DAMAGE_INTERVAL.get());
        intConfigs.put("void_sword_cooldown", ModConfigManager.VOID_SWORD_COOLDOWN.get());
        intConfigs.put("void_sword_max_cast_distance", ModConfigManager.VOID_SWORD_MAX_CAST_DISTANCE.get());

        // 填充浮点数配置
        doubleConfigs.put("scythe_attack_speed", ModConfigManager.SCYTHE_ATTACK_SPEED.get());
        doubleConfigs.put("scythe_damage_bonus", ModConfigManager.SCYTHE_DAMAGE_BONUS.get());
        doubleConfigs.put("scythe_harvest_range", ModConfigManager.SCYTHE_HARVEST_RANGE.get());
        doubleConfigs.put("scythe_sweep_range_bonus", ModConfigManager.SCYTHE_SWEEP_RANGE_BONUS.get());
        doubleConfigs.put("scythe_harvest_dance_chance", ModConfigManager.SCYTHE_HARVEST_DANCE_CHANCE.get());
        doubleConfigs.put("scythe_harvest_dance_range", ModConfigManager.SCYTHE_HARVEST_DANCE_RANGE.get());
        doubleConfigs.put("rocket_boots_boost_power", ModConfigManager.ROCKET_BOOTS_BOOST_POWER.get());
        doubleConfigs.put("rocket_boots_max_jump_height", ModConfigManager.ROCKET_BOOTS_MAX_JUMP_HEIGHT.get());
        doubleConfigs.put("lucky_sword_min_damage", ModConfigManager.LUCKY_SWORD_MIN_DAMAGE.get());
        doubleConfigs.put("lucky_sword_max_damage", ModConfigManager.LUCKY_SWORD_MAX_DAMAGE.get());
        doubleConfigs.put("bee_grenade_honey_area_radius", ModConfigManager.BEE_GRENADE_HONEY_AREA_RADIUS.get());
        doubleConfigs.put("proficiency_attack_speed_percent", ModConfigManager.PROFICIENCY_ATTACK_SPEED_PERCENT.get());
        doubleConfigs.put("void_sword_energy_percent", ModConfigManager.VOID_SWORD_ENERGY_PERCENT.get());
        doubleConfigs.put("void_sword_black_hole_range", ModConfigManager.VOID_SWORD_BLACK_HOLE_RANGE.get());
        doubleConfigs.put("void_sword_black_hole_damage", ModConfigManager.VOID_SWORD_BLACK_HOLE_DAMAGE.get());
    }

    /**
     * 将数据包写入缓冲区
     */
    public void toBytes(FriendlyByteBuf buffer) {
        // 写入布尔值配置
        buffer.writeInt(booleanConfigs.size());
        for (Map.Entry<String, Boolean> entry : booleanConfigs.entrySet()) {
            buffer.writeUtf(entry.getKey());
            buffer.writeBoolean(entry.getValue());
        }

        // 写入整数配置
        buffer.writeInt(intConfigs.size());
        for (Map.Entry<String, Integer> entry : intConfigs.entrySet()) {
            buffer.writeUtf(entry.getKey());
            buffer.writeInt(entry.getValue());
        }

        // 写入浮点数配置
        buffer.writeInt(doubleConfigs.size());
        for (Map.Entry<String, Double> entry : doubleConfigs.entrySet()) {
            buffer.writeUtf(entry.getKey());
            buffer.writeDouble(entry.getValue());
        }
    }

    /**
     * 处理接收到的数据包
     */
    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // 确保在客户端处理
            if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                LOGGER.info("接收到服务端配置同步数据包，共 {} 个配置",
                        booleanConfigs.size() + intConfigs.size() + doubleConfigs.size());

                // 在客户端应用接收到的配置
                ModConfigManager.applyServerConfig(booleanConfigs, intConfigs, doubleConfigs);
            }
        });
        context.setPacketHandled(true);
        return true;
    }
} 