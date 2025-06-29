package me.tuanzi.curiosities.mixin;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.util.DebugLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 村民交易界面Mixin
 * <p>
 * 在创造模式下，点击交易按钮时自动生成所需物品进行交易
 */
@Mixin(MerchantScreen.class)
public abstract class TradeScreenMixin extends AbstractContainerScreen<MerchantMenu> {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Shadow
    private int shopItem; // MerchantScreen中表示当前选中的交易索引

    @Shadow(aliases = {"f_99124_", "f_99123_"})
    private int scrollOff; // MerchantScreen中的滚动偏移量

    // 尝试访问MerchantScreen中可能与按钮位置相关的私有字段
    @Shadow(aliases = {"f_99126_"})
    private boolean isDragging; // MerchantScreen中表示是否正在拖拽

    // 这里编译器会抱怨缺少构造函数，但Mixin不需要实际实现它
    // 编译期间会被忽略，只有 Mixin 的注入代码会被应用
    public TradeScreenMixin(MerchantMenu menu, net.minecraft.world.entity.player.Inventory inventory, net.minecraft.network.chat.Component title) {
        super(menu, inventory, title);
    }

    /**
     * 尝试填充交易所需物品
     * 实现逻辑：
     * 1. 检测玩家点击的交易项目（在调用此方法前已完成）
     * 2. 清除交易格物品(发送数据包到服务器)
     * 3. 生成交易的物品,无需生成村民售卖的物品(发送数据包到服务器)
     * 4. 将生成的物品放在对应的交易格上(发送数据包到服务器)
     *
     * @param menu          交易菜单
     * @param selectedOffer 选中的交易
     */
    private boolean fillTradeItems(MerchantMenu menu, MerchantOffer selectedOffer) {
        try {
            // 只获取玩家需要提供的物品，不获取村民售卖的物品
            ItemStack firstItem = selectedOffer.getCostA().copy();  // 玩家需要提供的第一个物品
            ItemStack secondItem = selectedOffer.getCostB().copy();  // 玩家需要提供的第二个物品(可能为空)

            DebugLogger.debugDetail("[自动填充交易] 第一物品: {}, 数量: {}",
                    firstItem.getDisplayName().getString(), firstItem.getCount());
            if (!secondItem.isEmpty()) {
                DebugLogger.debugDetail("[自动填充交易] 第二物品: {}, 数量: {}",
                        secondItem.getDisplayName().getString(), secondItem.getCount());
            }

            // 获取玩家和网络连接
            Player player = Minecraft.getInstance().player;
            if (player == null || Minecraft.getInstance().getConnection() == null) {
                LOGGER.error("[自动填充交易] 无法获取玩家或网络连接");
                return false;
            }

            try {
                // 使用原生创造模式物品添加机制
                MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
                if (gameMode == null) {
                    LOGGER.error("[自动填充交易] 无法获取游戏模式");
                    return false;
                }

                // 获取交易菜单中交易槽的信息
                Slot firstTradeSlot = menu.getSlot(0);  // 第一个交易物品槽
                Slot secondTradeSlot = menu.getSlot(1); // 第二个交易物品槽

                LOGGER.debug("[自动填充交易] 交易槽位信息: 第一槽索引={}, 第二槽索引={}",
                        firstTradeSlot.index, secondTradeSlot.index);

                // 尝试计算正确的创造模式槽位索引
                // 这是基于Minecraft客户端创造模式物品栏索引规则的估计
                // 容器槽位通常从36+开始
                // (索引0-8是热键栏, 9-35是主物品栏, 36-39是装备栏, 40是副手)
                int firstTradeCreativeSlotIndex = firstTradeSlot.index + 36; // 估计的创造模式槽位索引
                int secondTradeCreativeSlotIndex = secondTradeSlot.index + 36; // 估计的创造模式槽位索引

                // 记录一些额外的信息来帮助调试
                LOGGER.debug("[自动填充交易] 交易槽位创造模式估计索引: 第一槽={}, 第二槽={}",
                        firstTradeCreativeSlotIndex, secondTradeCreativeSlotIndex);

                // 使用原版创造模式数据包直接清空交易槽
                if (!menu.getSlot(0).getItem().isEmpty()) {
                    LOGGER.debug("[自动填充交易] 使用创造模式数据包清空第一个交易槽");
                    ServerboundSetCreativeModeSlotPacket clearPacket1 = new ServerboundSetCreativeModeSlotPacket(firstTradeCreativeSlotIndex, ItemStack.EMPTY);
                    Minecraft.getInstance().getConnection().send(clearPacket1);
                }

                if (!menu.getSlot(1).getItem().isEmpty()) {
                    LOGGER.debug("[自动填充交易] 使用创造模式数据包清空第二个交易槽");
                    ServerboundSetCreativeModeSlotPacket clearPacket2 = new ServerboundSetCreativeModeSlotPacket(secondTradeCreativeSlotIndex, ItemStack.EMPTY);
                    Minecraft.getInstance().getConnection().send(clearPacket2);
                }

                // 短暂延迟确保清空操作完成
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // 备份快捷栏物品
                ItemStack hotbarItem1 = player.getInventory().getItem(0).copy();
                ItemStack hotbarItem2 = player.getInventory().getItem(1).copy();

                try {
                    // 将交易物品放入快捷栏对应位置 - 临时替换
                    ServerboundSetCreativeModeSlotPacket itemPacket1 = new ServerboundSetCreativeModeSlotPacket(36, firstItem);
                    Minecraft.getInstance().getConnection().send(itemPacket1);
                    LOGGER.debug("[自动填充交易] 已将第一个交易物品放入快捷栏第1格");

                    if (!secondItem.isEmpty()) {
                        ServerboundSetCreativeModeSlotPacket itemPacket2 = new ServerboundSetCreativeModeSlotPacket(37, secondItem);
                        Minecraft.getInstance().getConnection().send(itemPacket2);
                        LOGGER.debug("[自动填充交易] 已将第二个交易物品放入快捷栏第2格");
                    }

                    // 短暂延迟确保物品已放置
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    // 现在通过背包交互将物品从快捷栏移动到交易槽
                    // 使用SWAP操作，将快捷栏的物品与交易槽交换
                    int containerId = menu.containerId;
                    gameMode.handleInventoryMouseClick(containerId, 0, 0, ClickType.SWAP, player);
                    LOGGER.debug("[自动填充交易] 已将快捷栏第1格物品移动到交易槽");

                    if (!secondItem.isEmpty()) {
                        gameMode.handleInventoryMouseClick(containerId, 1, 1, ClickType.SWAP, player);
                        LOGGER.debug("[自动填充交易] 已将快捷栏第2格物品移动到交易槽");
                    }

                    // 恢复快捷栏物品
                    ServerboundSetCreativeModeSlotPacket restorePacket1 = new ServerboundSetCreativeModeSlotPacket(36, hotbarItem1);
                    Minecraft.getInstance().getConnection().send(restorePacket1);

                    ServerboundSetCreativeModeSlotPacket restorePacket2 = new ServerboundSetCreativeModeSlotPacket(37, hotbarItem2);
                    Minecraft.getInstance().getConnection().send(restorePacket2);
                    LOGGER.debug("[自动填充交易] 已恢复快捷栏物品");

                } catch (Exception e) {
                    LOGGER.error("[自动填充交易] 物品操作失败", e);

                    // 出错时也要恢复快捷栏物品
                    ServerboundSetCreativeModeSlotPacket restorePacket1 = new ServerboundSetCreativeModeSlotPacket(36, hotbarItem1);
                    Minecraft.getInstance().getConnection().send(restorePacket1);

                    ServerboundSetCreativeModeSlotPacket restorePacket2 = new ServerboundSetCreativeModeSlotPacket(37, hotbarItem2);
                    Minecraft.getInstance().getConnection().send(restorePacket2);
                    LOGGER.debug("[自动填充交易] 已恢复快捷栏物品（错误恢复）");

                    return false;
                }

                LOGGER.debug("[自动填充交易] 已完成物品放置");
                return true;

            } catch (Exception e) {
                LOGGER.error("[自动填充交易] 设置物品时出错", e);
                return false;
            }

        } catch (Exception e) {
            LOGGER.error("[自动填充交易] 填充物品错误", e);
            return false;
        }
    }

    /**
     * 计算交易列表中每个交易项的位置和宽高
     *
     * @param tradeIndex 交易索引
     * @return 包含x, y, width, height的区域信息
     */
    private int[] getTradeItemArea(int tradeIndex) {
        int leftPos = this.leftPos;
        int topPos = this.topPos;

        // 交易列表起始位置和每项高度 (根据MerchantScreen实际布局调整)
        final int TRADE_LIST_START_X = 5;
        final int TRADE_LIST_START_Y = 19; // 交易列表开始的Y坐标
        final int TRADE_ITEM_HEIGHT = 20;  // 每个交易项的高度
        final int VISIBLE_TRADES = 7;      // 可见的交易数量

        // 计算实际可见的交易项索引
        int visibleIndex = tradeIndex - scrollOff;

        // 确保交易项在可见范围内
        if (visibleIndex >= 0 && visibleIndex < VISIBLE_TRADES) {
            int x = leftPos + TRADE_LIST_START_X;
            int y = topPos + TRADE_LIST_START_Y + visibleIndex * TRADE_ITEM_HEIGHT;
            int width = 70; // 交易项的宽度
            int height = TRADE_ITEM_HEIGHT;

            return new int[]{x, y, width, height};
        }

        return null; // 该交易项不在可见范围内
    }

    /**
     * 根据点击坐标确定用户点击的是哪个交易项
     *
     * @param mouseX 鼠标X坐标
     * @param mouseY 鼠标Y坐标
     * @param offers 所有可用的交易选项
     * @return 点击的交易项索引，如果没有点中任何交易项则返回-1
     */
    private int getClickedTradeIndex(double mouseX, double mouseY, MerchantOffers offers) {
        // 遍历所有可能在视图中的交易项
        int totalOffers = offers.size();
        for (int i = 0; i < totalOffers; i++) {
            int[] area = getTradeItemArea(i);
            if (area != null) {
                int x = area[0], y = area[1], width = area[2], height = area[3];

                // 检查点击是否在这个交易项区域内
                if (mouseX >= x && mouseX <= x + width &&
                        mouseY >= y && mouseY <= y + height) {
                    LOGGER.debug("[自动填充交易] 检测到点击交易项 {}", i);
                    return i;
                }
            }
        }
        return -1; // 没有点击任何交易项
    }

    /**
     * 检查点击是否是交易按钮点击
     *
     * @param mouseX 鼠标X坐标
     * @param mouseY 鼠标Y坐标
     * @param offers 所有可用的交易选项
     * @return 如果是交易按钮点击，返回交易索引；否则返回-1
     */
    private int checkTradeButtonClick(double mouseX, double mouseY, MerchantOffers offers) {
        // 先检查是否点击了交易列表中的某个交易项
        int clickedTradeIndex = getClickedTradeIndex(mouseX, mouseY, offers);

        // 如果点击了某个交易项
        if (clickedTradeIndex != -1) {
            LOGGER.debug("[自动填充交易] 用户点击了交易列表中的第 {} 项交易", clickedTradeIndex);

            // 检查是否在交易按钮区域(这里主要是左侧区域的点击，精确到交易项)
            return clickedTradeIndex;
        }

        // 如果不是交易列表的点击，也可能是其他交易按钮区域
        MerchantScreen screen = (MerchantScreen) (Object) this;
        MerchantMenu menu = screen.getMenu();

        // 获取屏幕坐标和交易区域信息
        int leftPos = this.leftPos;
        int topPos = this.topPos;

        // 获取交易输出槽位信息
        Slot inputSlot0 = menu.getSlot(0);
        Slot inputSlot1 = menu.getSlot(1);
        Slot outputSlot = menu.getSlot(2);

        // 输入槽和输出槽之间的箭头区域
        int arrowX = leftPos + inputSlot1.x + 20;
        int arrowY = topPos + inputSlot1.y;
        int arrowWidth = outputSlot.x - inputSlot1.x - 25;
        int arrowHeight = 20;

        boolean isArrowClick = arrowWidth > 0 && mouseX >= arrowX && mouseX <= arrowX + arrowWidth &&
                mouseY >= arrowY && mouseY <= arrowY + arrowHeight;

        if (isArrowClick) {
            LOGGER.debug("[自动填充交易] 用户点击了箭头区域，使用当前选中的交易项 {}", shopItem);
            return shopItem; // 使用当前选中的交易索引
        }

        return -1; // 不是交易按钮点击
    }

    /**
     * 拦截交易界面的鼠标点击事件
     * 当在创造模式下点击交易按钮时，自动在交易框中放入所需物品
     * <p>
     * 处理逻辑：
     * 1. 检测点击事件是否在交易区域
     * 2. 确定具体点击的交易项目
     * 3. 调用fillTradeItems方法处理自动填充
     *
     * @param mouseX 鼠标X坐标
     * @param mouseY 鼠标Y坐标
     * @param button 鼠标按钮
     * @param cir    回调信息
     */
    @Inject(
            method = "mouseClicked",
            at = @At("HEAD")
    )
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        DebugLogger.debugDetail("[自动填充交易] Mixin被触发，检测点击事件 x={}, y={}, button={}", mouseX, mouseY, button);

        // 检查配置是否启用了这个功能
        if (!ModConfigManager.CREATIVE_TRADE_AUTO_FILL_ENABLED.get()) {
            DebugLogger.debugDetail("[自动填充交易] 功能已在配置中禁用");
            return;
        }

        // 获取玩家实例
        Player player = Minecraft.getInstance().player;
        if (player == null || !player.isCreative()) {
            DebugLogger.debugDetail("[自动填充交易] 玩家不存在或不是创造模式");
            return;
        }

        LOGGER.debug("[自动填充交易] 玩家是创造模式，继续处理");

        // 获取交易界面和交易列表
        MerchantScreen screen = (MerchantScreen) (Object) this;
        MerchantMenu menu = screen.getMenu();
        MerchantOffers offers = menu.getOffers();

        if (offers == null || offers.isEmpty()) {
            LOGGER.debug("[自动填充交易] 交易列表为空");
            return;
        }

        // 检查点击是否是交易按钮点击，并获取对应的交易索引
        int clickedTradeIndex = checkTradeButtonClick(mouseX, mouseY, offers);

        if (clickedTradeIndex != -1 && clickedTradeIndex < offers.size()) {
            LOGGER.debug("[自动填充交易] 检测到交易按钮点击，交易索引: {}", clickedTradeIndex);

            MerchantOffer selectedOffer = offers.get(clickedTradeIndex);
            if (selectedOffer == null) {
                LOGGER.debug("[自动填充交易] 选中的交易为null");
                return;
            }

            LOGGER.debug("[自动填充交易] 已选择交易: {}", selectedOffer.getCostA().getDisplayName().getString());

            // 自动填充交易物品
            fillTradeItems(menu, selectedOffer);
        }
    }
} 