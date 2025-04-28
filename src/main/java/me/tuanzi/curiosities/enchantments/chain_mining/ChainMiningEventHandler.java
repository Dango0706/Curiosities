package me.tuanzi.curiosities.enchantments.chain_mining;

import me.tuanzi.curiosities.Curiosities;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import me.tuanzi.curiosities.network.PacketHandler;
import me.tuanzi.curiosities.network.PacketTriggerChainMining;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.lwjgl.glfw.GLFW;

/**
 * 连锁挖掘事件处理器
 * 处理方块破坏事件和按键事件，实现连锁挖掘功能
 */
@Mod.EventBusSubscriber(modid = Curiosities.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChainMiningEventHandler {
    // 日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();
    
    // 连锁挖掘热键
    public static KeyMapping chainMiningKey;
    
    // 按键状态追踪
    private static boolean keyWasPressed = false;
    private static boolean chainMiningActive = false;

    /**
     * 处理方块破坏事件
     * 当玩家激活连锁挖掘模式时，触发连锁挖掘功能
     * 
     * @param event 方块破坏事件
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        // 首先检查配置是否启用连锁挖掘
        if (!ChainMiningConfig.isChainMiningEnabled()) {
            return;
        }
        
        Player player = event.getPlayer();
        
        // 只在服务端处理，避免重复处理
        if (event.getLevel().isClientSide()) {
            return;
        }
        
        // 如果正在使用连锁挖掘模式，并且是玩家破坏的方块
        if (isPlayerChainMiningActive(player)) {
            LOGGER.info("[连锁挖掘] 玩家 {} 破坏了方块 {}，触发连锁挖掘", 
                    player.getName().getString(), event.getPos());
            
            // 直接在服务端调用连锁挖掘逻辑，这是在方块已经被破坏后执行的
            ChainMiningLogic.triggerChainMining(player, event.getPos(), (Level) event.getLevel());
        }
    }
    
    /**
     * 检查玩家是否激活了连锁挖掘模式
     * 
     * @param player 玩家实例
     * @return 玩家是否激活了连锁挖掘模式
     */
    private static boolean isPlayerChainMiningActive(Player player) {
        // 检查配置是否启用连锁挖掘
        if (!ChainMiningConfig.isChainMiningEnabled()) {
            return false;
        }
        
        // 在服务端，通过玩家ID来确认是否是按住了连锁挖掘键的玩家
        return ChainMiningState.isPlayerChainMiningActive(player.getUUID());
    }
    
    /**
     * 客户端事件处理器
     * 处理按键输入和连锁挖掘状态变化
     */
    @Mod.EventBusSubscriber(modid = Curiosities.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientEvents {
        /**
         * 处理按键输入事件
         * 当连锁挖掘热键被按下或释放时，更新连锁挖掘状态
         * 
         * @param event 按键输入事件
         */
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            // 如果连锁挖掘被禁用，或者按键未注册，则跳过
            if (!ChainMiningConfig.isChainMiningEnabled() || chainMiningKey == null) {
                // 如果当前有激活状态但配置已禁用，则强制重置状态
                if (chainMiningActive) {
                    resetChainMiningState();
                }
                return;
            }
            
            boolean isKeyDown = chainMiningKey.isDown();
            handleKeyStateChange(isKeyDown);
        }
        
        /**
         * 处理按键状态变化
         * 
         * @param isKeyDown 按键是否被按下
         */
        private static void handleKeyStateChange(boolean isKeyDown) {
            // 当按键状态发生变化时
            if (isKeyDown && !keyWasPressed) {
                // 按键从未按下变为按下
                activateChainMining();
            } else if (!isKeyDown && keyWasPressed) {
                // 按键从按下变为未按下
                deactivateChainMining();
            }
        }
        
        /**
         * 激活连锁挖掘模式
         */
        private static void activateChainMining() {
            chainMiningActive = true;
            keyWasPressed = true;
            LOGGER.info("[连锁挖掘] 按键被按下，已激活连锁挖掘模式");
            
            // 向服务端发送激活状态
            sendChainMiningState(true);
        }
        
        /**
         * 停用连锁挖掘模式
         */
        private static void deactivateChainMining() {
            chainMiningActive = false;
            keyWasPressed = false;
            LOGGER.info("[连锁挖掘] 按键被释放，已停用连锁挖掘模式");
            
            // 向服务端发送停用状态
            sendChainMiningState(false);
        }
        
        /**
         * 强制重置连锁挖掘状态
         * 当配置被禁用时使用
         */
        private static void resetChainMiningState() {
            chainMiningActive = false;
            keyWasPressed = false;
            
            // 向服务端发送停用状态
            sendChainMiningState(false);
            LOGGER.info("[连锁挖掘] 由于配置禁用，强制停用连锁挖掘模式");
        }
        
        /**
         * 向服务端发送连锁挖掘状态
         * 
         * @param active 连锁挖掘是否激活
         */
        private static void sendChainMiningState(boolean active) {
            if (Minecraft.getInstance().player != null) {
                PacketHandler.INSTANCE.sendToServer(new PacketTriggerChainMining.ChainMiningActivation(active));
            }
        }
    }

    /**
     * 模组事件处理器
     * 用于注册按键映射等模组初始化事件
     */
    @Mod.EventBusSubscriber(modid = Curiosities.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModEvents {
        /**
         * 注册按键映射
         * 
         * @param event 按键映射注册事件
         */
        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            // 使用默认的反引号键
            int keyCode = GLFW.GLFW_KEY_GRAVE_ACCENT;
            
            // 创建按键映射并注册
            chainMiningKey = new KeyMapping("key.curiosities.chain_mining", keyCode, "key.categories.gameplay");
            event.register(chainMiningKey);
            LOGGER.info("[连锁挖掘] 按键注册完成，使用默认键：反引号(`)");
        }
    }
}