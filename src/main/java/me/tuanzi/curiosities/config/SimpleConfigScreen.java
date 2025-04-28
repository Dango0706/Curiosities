package me.tuanzi.curiosities.config;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.enchantments.chain_mining.ChainMiningConfig;
import me.tuanzi.curiosities.enchantments.moral_balance.MoralBalanceConfig;
import me.tuanzi.curiosities.enchantments.super_fortune.SuperFortuneConfig;
import me.tuanzi.curiosities.items.WolfFangPotatoConfig;
import me.tuanzi.curiosities.items.rocket_boots.RocketBootsConfig;
import me.tuanzi.curiosities.items.scythe.ScytheConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * 连锁挖掘模组的自定义配置界面
 * 提供直观的UI界面用于修改模组配置
 */
public class SimpleConfigScreen extends Screen {
    // 日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();
    
    // 父界面，用于返回
    private final Screen parentScreen;
    
    // 连锁挖掘配置参数
    private boolean chainMiningEnabled;
    private int maxChainBlocks;
    private int blocksPerLevel;
    
    // 超级时运配置参数
    private boolean superFortuneEnabled;
    
    // 狼牙土豆配置参数
    private boolean wolfFangPotatoEnabled;
    
    // 镰刀配置参数
    private boolean scytheEnabled;
    private int harvestRange;
    private double attackSpeed;
    private double damageBonus;
    private double sweepRangeBonus;
    private double harvestDanceChance;
    private int harvestDanceRange;
    
    // 火箭靴配置参数
    private boolean rocketBootsEnabled;
    private float maxJumpHeight;
    private int fuelConsumption;
    private int maxFuelStorage;
    
    // 道德天平配置参数
    private boolean moralBalanceEnabled;
    
    // UI组件
    private EditBox maxBlocksField;
    private EditBox blocksPerLevelField;
    private EditBox harvestRangeField;
    private EditBox attackSpeedField;
    private EditBox damageBonusField;
    private EditBox sweepRangeBonusField;
    private EditBox harvestDanceChanceField;
    private EditBox harvestDanceRangeField;
    private EditBox maxJumpHeightField;
    private EditBox fuelConsumptionField;
    private EditBox maxFuelStorageField;
    private Button chainMiningToggleButton;
    private Button superFortuneToggleButton;
    private Button wolfFangPotatoToggleButton;
    private Button scytheToggleButton;
    private Button rocketBootsToggleButton;
    private Button moralBalanceToggleButton;
    
    // 滚动面板
    private ConfigList configList;
    
    // UI布局常量 - 减小了间距以使界面更紧凑
    private static final int SECTION_SPACING = 8;   // 从15减小到8
    private static final int ITEM_SPACING = 4;      // 从5减小到4
    private static final int TITLE_SPACING = 6;     // 从10减小到6
    private static final int LABEL_OFFSET = 10;     // 从12减小到10
    
    /**
     * 构造函数
     * 
     * @param parentScreen 父界面，用于返回
     */
    public SimpleConfigScreen(Screen parentScreen) {
        super(Component.translatable("config.curiosities.title"));
        this.parentScreen = parentScreen;
        // 从配置获取当前值
        this.chainMiningEnabled = ChainMiningConfig.isChainMiningEnabled();
        this.maxChainBlocks = ChainMiningConfig.getMaxChainBlocks();
        this.blocksPerLevel = ChainMiningConfig.getBlocksPerLevel();
        this.superFortuneEnabled = SuperFortuneConfig.isSuperFortuneEnabled();
        this.wolfFangPotatoEnabled = WolfFangPotatoConfig.isWolfFangPotatoEnabled();
        this.scytheEnabled = ScytheConfig.isScytheEnabled();
        this.harvestRange = ScytheConfig.getHarvestRange();
        this.attackSpeed = ScytheConfig.getAttackSpeed();
        this.damageBonus = ScytheConfig.getDamageBonus();
        this.sweepRangeBonus = ScytheConfig.getSweepRangeBonus();
        this.harvestDanceChance = ScytheConfig.getHarvestDanceChance();
        this.harvestDanceRange = ScytheConfig.getHarvestDanceRange();
        this.rocketBootsEnabled = RocketBootsConfig.isRocketBootsEnabled();
        this.maxJumpHeight = RocketBootsConfig.getMaxJumpHeight();
        this.fuelConsumption = RocketBootsConfig.getFuelConsumption();
        this.maxFuelStorage = RocketBootsConfig.getMaxFuelStorage();
        this.moralBalanceEnabled = MoralBalanceConfig.isMoralBalanceEnabled();
    }
    
    /**
     * 初始化UI组件
     */
    @Override
    protected void init() {
        int buttonWidth = Math.min(200, this.width - 40);
        
        // 创建滚动列表，增加滚动区域高度
        this.configList = new ConfigList(this.minecraft, this.width, this.height, 32, this.height - 36, 20); // 项目高度从25减小到20
        this.addWidget(this.configList);
        
        // 添加连锁挖掘配置区域
        this.configList.addConfigEntry(new SectionTitleEntry(Component.translatable("config.curiosities.chain_mining_section")));
        
        // 添加连锁挖掘开关
        chainMiningToggleButton = Button.builder(
            Component.translatable("config.curiosities.chain_mining_enabled")
                .append(chainMiningEnabled ? 
                    Component.translatable("config.curiosities.enabled") : 
                    Component.translatable("config.curiosities.disabled")),
            button -> {
                chainMiningEnabled = !chainMiningEnabled;
                button.setMessage(Component.translatable("config.curiosities.chain_mining_enabled")
                    .append(chainMiningEnabled ? 
                        Component.translatable("config.curiosities.enabled") : 
                        Component.translatable("config.curiosities.disabled")));
            }).build();
        this.configList.addConfigEntry(new ButtonEntry(chainMiningToggleButton, buttonWidth));
        
        // 添加最大方块数输入框
        this.configList.addConfigEntry(new LabelEntry(Component.translatable("config.curiosities.max_blocks_label")));
        maxBlocksField = new EditBox(this.font, 0, 0, buttonWidth, 20, Component.empty());
        maxBlocksField.setValue(String.valueOf(maxChainBlocks));
        maxBlocksField.setFilter(s -> s.isEmpty() || s.matches("\\d+"));
        this.configList.addConfigEntry(new EditBoxEntry(maxBlocksField));
        
        // 添加每级方块数输入框
        this.configList.addConfigEntry(new LabelEntry(Component.translatable("config.curiosities.blocks_per_level_label")));
        blocksPerLevelField = new EditBox(this.font, 0, 0, buttonWidth, 20, Component.empty());
        blocksPerLevelField.setValue(String.valueOf(blocksPerLevel));
        blocksPerLevelField.setFilter(s -> s.isEmpty() || s.matches("\\d+"));
        this.configList.addConfigEntry(new EditBoxEntry(blocksPerLevelField));
        
        // 添加间隔
        this.configList.addConfigEntry(new SpacerEntry(SECTION_SPACING));
        
        // 添加超级时运配置区域
        this.configList.addConfigEntry(new SectionTitleEntry(Component.translatable("config.curiosities.super_fortune_section")));
        
        // 添加超级时运开关
        superFortuneToggleButton = Button.builder(
            Component.translatable("config.curiosities.super_fortune_enabled")
                .append(superFortuneEnabled ? 
                    Component.translatable("config.curiosities.enabled") : 
                    Component.translatable("config.curiosities.disabled")),
            button -> {
                superFortuneEnabled = !superFortuneEnabled;
                button.setMessage(Component.translatable("config.curiosities.super_fortune_enabled")
                    .append(superFortuneEnabled ? 
                        Component.translatable("config.curiosities.enabled") : 
                        Component.translatable("config.curiosities.disabled")));
            }).build();
        this.configList.addConfigEntry(new ButtonEntry(superFortuneToggleButton, buttonWidth));
        
        // 添加间隔
        this.configList.addConfigEntry(new SpacerEntry(SECTION_SPACING));
        
        // 添加狼牙土豆配置区域
        this.configList.addConfigEntry(new SectionTitleEntry(Component.translatable("config.curiosities.wolf_fang_potato_section")));
        
        // 添加狼牙土豆开关
        wolfFangPotatoToggleButton = Button.builder(
            Component.translatable("config.curiosities.wolf_fang_potato_enabled")
                .append(wolfFangPotatoEnabled ? 
                    Component.translatable("config.curiosities.enabled") : 
                    Component.translatable("config.curiosities.disabled")),
            button -> {
                wolfFangPotatoEnabled = !wolfFangPotatoEnabled;
                button.setMessage(Component.translatable("config.curiosities.wolf_fang_potato_enabled")
                    .append(wolfFangPotatoEnabled ? 
                        Component.translatable("config.curiosities.enabled") : 
                        Component.translatable("config.curiosities.disabled")));
            }).build();
        this.configList.addConfigEntry(new ButtonEntry(wolfFangPotatoToggleButton, buttonWidth));
        
        // 添加间隔
        this.configList.addConfigEntry(new SpacerEntry(SECTION_SPACING));
        
        // 添加镰刀配置区域
        this.configList.addConfigEntry(new SectionTitleEntry(Component.translatable("config.curiosities.scythe_section")));
        
        // 添加镰刀开关
        scytheToggleButton = Button.builder(
            Component.translatable("config.curiosities.scythe_enabled")
                .append(scytheEnabled ? 
                    Component.translatable("config.curiosities.enabled") : 
                    Component.translatable("config.curiosities.disabled")),
            button -> {
                scytheEnabled = !scytheEnabled;
                button.setMessage(Component.translatable("config.curiosities.scythe_enabled")
                    .append(scytheEnabled ? 
                        Component.translatable("config.curiosities.enabled") : 
                        Component.translatable("config.curiosities.disabled")));
            }).build();
        this.configList.addConfigEntry(new ButtonEntry(scytheToggleButton, buttonWidth));
        
        // 添加收获范围输入框
        this.configList.addConfigEntry(new LabelEntry(Component.translatable("config.curiosities.harvest_range_label")));
        harvestRangeField = new EditBox(this.font, 0, 0, buttonWidth, 20, Component.empty());
        harvestRangeField.setValue(String.valueOf(harvestRange));
        harvestRangeField.setFilter(s -> s.isEmpty() || s.matches("\\d+"));
        this.configList.addConfigEntry(new EditBoxEntry(harvestRangeField));
        
        // 添加攻击速度输入框
        this.configList.addConfigEntry(new LabelEntry(Component.translatable("config.curiosities.attack_speed_label")));
        attackSpeedField = new EditBox(this.font, 0, 0, buttonWidth, 20, Component.empty());
        attackSpeedField.setValue(String.valueOf(attackSpeed));
        attackSpeedField.setFilter(s -> s.isEmpty() || s.matches("^\\d*\\.?\\d*$"));
        this.configList.addConfigEntry(new EditBoxEntry(attackSpeedField));
        
        // 添加攻击伤害加成输入框
        this.configList.addConfigEntry(new LabelEntry(Component.translatable("config.curiosities.damage_bonus_label")));
        damageBonusField = new EditBox(this.font, 0, 0, buttonWidth, 20, Component.empty());
        damageBonusField.setValue(String.valueOf(damageBonus));
        damageBonusField.setFilter(s -> s.isEmpty() || s.matches("^\\d*\\.?\\d*$"));
        this.configList.addConfigEntry(new EditBoxEntry(damageBonusField));
        
        // 添加横扫范围加成输入框
        this.configList.addConfigEntry(new LabelEntry(Component.translatable("config.curiosities.sweep_range_bonus_label")));
        sweepRangeBonusField = new EditBox(this.font, 0, 0, buttonWidth, 20, Component.empty());
        sweepRangeBonusField.setValue(String.valueOf(sweepRangeBonus));
        sweepRangeBonusField.setFilter(s -> s.isEmpty() || s.matches("^\\d*\\.?\\d*$"));
        this.configList.addConfigEntry(new EditBoxEntry(sweepRangeBonusField));
        
        // 添加丰收之舞触发概率输入框
        this.configList.addConfigEntry(new LabelEntry(Component.translatable("config.curiosities.harvest_dance_chance_label")));
        harvestDanceChanceField = new EditBox(this.font, 0, 0, buttonWidth, 20, Component.empty());
        harvestDanceChanceField.setValue(String.valueOf(harvestDanceChance));
        harvestDanceChanceField.setFilter(s -> s.isEmpty() || s.matches("^\\d*\\.?\\d*$"));
        this.configList.addConfigEntry(new EditBoxEntry(harvestDanceChanceField));
        
        // 添加丰收之舞范围输入框
        this.configList.addConfigEntry(new LabelEntry(Component.translatable("config.curiosities.harvest_dance_range_label")));
        harvestDanceRangeField = new EditBox(this.font, 0, 0, buttonWidth, 20, Component.empty());
        harvestDanceRangeField.setValue(String.valueOf(harvestDanceRange));
        harvestDanceRangeField.setFilter(s -> s.isEmpty() || s.matches("\\d+"));
        this.configList.addConfigEntry(new EditBoxEntry(harvestDanceRangeField));
        
        // 添加间隔
        this.configList.addConfigEntry(new SpacerEntry(SECTION_SPACING));
        
        // 添加火箭靴配置区域
        this.configList.addConfigEntry(new SectionTitleEntry(Component.translatable("config.curiosities.rocket_boots_section")));
        
        // 添加火箭靴开关
        rocketBootsToggleButton = Button.builder(
            Component.translatable("config.curiosities.rocket_boots_enabled")
                .append(rocketBootsEnabled ? 
                    Component.translatable("config.curiosities.enabled") : 
                    Component.translatable("config.curiosities.disabled")),
            button -> {
                rocketBootsEnabled = !rocketBootsEnabled;
                button.setMessage(Component.translatable("config.curiosities.rocket_boots_enabled")
                    .append(rocketBootsEnabled ? 
                        Component.translatable("config.curiosities.enabled") : 
                        Component.translatable("config.curiosities.disabled")));
            }).build();
        this.configList.addConfigEntry(new ButtonEntry(rocketBootsToggleButton, buttonWidth));
        
        // 添加最大跳跃高度输入框
        this.configList.addConfigEntry(new LabelEntry(Component.translatable("config.curiosities.max_jump_height_label")));
        maxJumpHeightField = new EditBox(this.font, 0, 0, buttonWidth, 20, Component.empty());
        maxJumpHeightField.setValue(String.valueOf(maxJumpHeight));
        maxJumpHeightField.setFilter(s -> s.isEmpty() || s.matches("\\d+"));
        this.configList.addConfigEntry(new EditBoxEntry(maxJumpHeightField));
        
        // 添加燃料消耗输入框
        this.configList.addConfigEntry(new LabelEntry(Component.translatable("config.curiosities.fuel_consumption_label")));
        fuelConsumptionField = new EditBox(this.font, 0, 0, buttonWidth, 20, Component.empty());
        fuelConsumptionField.setValue(String.valueOf(fuelConsumption));
        fuelConsumptionField.setFilter(s -> s.isEmpty() || s.matches("\\d+"));
        this.configList.addConfigEntry(new EditBoxEntry(fuelConsumptionField));
        
        // 添加最大燃料存储输入框
        this.configList.addConfigEntry(new LabelEntry(Component.translatable("config.curiosities.max_fuel_storage_label")));
        maxFuelStorageField = new EditBox(this.font, 0, 0, buttonWidth, 20, Component.empty());
        maxFuelStorageField.setValue(String.valueOf(maxFuelStorage));
        maxFuelStorageField.setFilter(s -> s.isEmpty() || s.matches("\\d+"));
        this.configList.addConfigEntry(new EditBoxEntry(maxFuelStorageField));
        
        // 添加道德天平配置区域
        this.configList.addConfigEntry(new SectionTitleEntry(Component.translatable("config.curiosities.moral_balance_section")));
        
        // 添加道德天平开关
        moralBalanceToggleButton = Button.builder(
            Component.translatable("config.curiosities.moral_balance_enabled")
                .append(moralBalanceEnabled ? 
                    Component.translatable("config.curiosities.enabled") : 
                    Component.translatable("config.curiosities.disabled")),
            button -> {
                moralBalanceEnabled = !moralBalanceEnabled;
                button.setMessage(Component.translatable("config.curiosities.moral_balance_enabled")
                    .append(moralBalanceEnabled ? 
                        Component.translatable("config.curiosities.enabled") : 
                        Component.translatable("config.curiosities.disabled")));
            }).build();
        this.configList.addConfigEntry(new ButtonEntry(moralBalanceToggleButton, buttonWidth));
        
        // 添加间隔
        this.configList.addConfigEntry(new SpacerEntry(SECTION_SPACING));
        
        // 底部按钮不放在滚动列表中
        int bottomButtonY = this.height - 30;
        
        // 保存按钮
        this.addRenderableWidget(Button.builder(
            Component.translatable("config.curiosities.save"),
            button -> this.saveAndClose()
        ).pos(this.width / 2 - buttonWidth / 2, bottomButtonY).size(buttonWidth / 2 - 5, 20).build());
        
        // 取消按钮
        this.addRenderableWidget(Button.builder(
            CommonComponents.GUI_CANCEL,
            button -> this.onClose()
        ).pos(this.width / 2 + 5, bottomButtonY).size(buttonWidth / 2 - 5, 20).build());
    }
    
    /**
     * 渲染界面
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 渲染灰色背景
        this.renderBackground(graphics);
        
        // 渲染滚动列表
        this.configList.render(graphics, mouseX, mouseY, partialTick);
        
        // 渲染标题
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        
        // 渲染其他UI组件
        super.render(graphics, mouseX, mouseY, partialTick);
    }
    
    /**
     * 鼠标滚轮事件处理，提高滚动速度
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaY) {
        // 增加滚动速度
        return this.configList.mouseScrolled(mouseX, mouseY, deltaY * 1.5);
    }
    
    /**
     * 关闭界面时的处理
     */
    @Override
    public void onClose() {
        this.minecraft.setScreen(parentScreen);
    }
    
    /**
     * 保存配置并关闭界面
     */
    private void saveAndClose() {
        // 保存连锁挖掘启用状态
        ChainMiningConfig.CHAIN_MINING_ENABLED.set(chainMiningEnabled);
        
        // 保存最大方块数
        try {
            int blocks = Integer.parseInt(maxBlocksField.getValue());
            // 限制在有效范围内
            blocks = Math.max(16, Math.min(128, blocks));
            ChainMiningConfig.MAX_BLOCKS.set(blocks);
        } catch (NumberFormatException e) {
            // 无效输入，使用默认值
            ChainMiningConfig.MAX_BLOCKS.set(64);
        }
        
        // 保存每级方块数
        try {
            int blocksPerLevel = Integer.parseInt(blocksPerLevelField.getValue());
            // 限制在有效范围内
            blocksPerLevel = Math.max(1, Math.min(64, blocksPerLevel));
            ChainMiningConfig.BLOCKS_PER_LEVEL.set(blocksPerLevel);
        } catch (NumberFormatException e) {
            // 无效输入，使用默认值
            ChainMiningConfig.BLOCKS_PER_LEVEL.set(16);
        }
        
        // 保存超级时运启用状态
        SuperFortuneConfig.SUPER_FORTUNE_ENABLED.set(superFortuneEnabled);
        
        // 保存狼牙土豆启用状态
        WolfFangPotatoConfig.WOLF_FANG_POTATO_ENABLED.set(wolfFangPotatoEnabled);
        
        // 保存镰刀启用状态
        ScytheConfig.SCYTHE_ENABLED.set(scytheEnabled);
        
        // 保存收获范围
        try {
            int range = Integer.parseInt(harvestRangeField.getValue());
            // 限制在有效范围内
            range = Math.max(1, Math.min(5, range));
            ScytheConfig.HARVEST_RANGE.set(range);
        } catch (NumberFormatException e) {
            // 无效输入，使用默认值
            ScytheConfig.HARVEST_RANGE.set(1);
        }
        
        // 保存攻击速度
        try {
            double speed = Double.parseDouble(attackSpeedField.getValue());
            // 限制在有效范围内
            speed = Math.max(0.1, Math.min(3.0, speed));
            ScytheConfig.ATTACK_SPEED.set(speed);
        } catch (NumberFormatException e) {
            // 无效输入，使用默认值
            ScytheConfig.ATTACK_SPEED.set(1.0);
        }
        
        // 保存攻击伤害加成
        try {
            double bonus = Double.parseDouble(damageBonusField.getValue());
            // 限制在有效范围内
            bonus = Math.max(0.0, Math.min(5.0, bonus));
            ScytheConfig.DAMAGE_BONUS.set(bonus);
        } catch (NumberFormatException e) {
            // 无效输入，使用默认值
            ScytheConfig.DAMAGE_BONUS.set(1.0);
        }
        
        // 保存横扫范围加成
        try {
            double bonus = Double.parseDouble(sweepRangeBonusField.getValue());
            // 限制在有效范围内
            bonus = Math.max(0.0, Math.min(2.0, bonus));
            ScytheConfig.SWEEP_RANGE_BONUS.set(bonus);
        } catch (NumberFormatException e) {
            // 无效输入，使用默认值
            ScytheConfig.SWEEP_RANGE_BONUS.set(0.5);
        }
        
        // 保存丰收之舞触发概率
        try {
            double chance = Double.parseDouble(harvestDanceChanceField.getValue());
            // 限制在有效范围内
            chance = Math.max(0.0, Math.min(1.0, chance));
            ScytheConfig.HARVEST_DANCE_CHANCE.set(chance);
        } catch (NumberFormatException e) {
            // 无效输入，使用默认值
            ScytheConfig.HARVEST_DANCE_CHANCE.set(0.05);
        }
        
        // 保存丰收之舞范围
        try {
            int range = Integer.parseInt(harvestDanceRangeField.getValue());
            // 限制在有效范围内
            range = Math.max(1, Math.min(10, range));
            ScytheConfig.HARVEST_DANCE_RANGE.set(range);
        } catch (NumberFormatException e) {
            // 无效输入，使用默认值
            ScytheConfig.HARVEST_DANCE_RANGE.set(2);
        }
        
        // 保存火箭靴配置
        try {
            RocketBootsConfig.ROCKET_BOOTS_ENABLED.set(rocketBootsEnabled);
            RocketBootsConfig.MAX_JUMP_HEIGHT.set(Double.valueOf(maxJumpHeightField.getValue()));
            RocketBootsConfig.FUEL_CONSUMPTION.set(Integer.parseInt(fuelConsumptionField.getValue()));
            RocketBootsConfig.MAX_FUEL_STORAGE.set(Integer.parseInt(maxFuelStorageField.getValue()));
        } catch (NumberFormatException e) {
            LOGGER.warn("保存火箭靴配置时发生错误", e);
        }
        
        // 保存道德天平配置
        MoralBalanceConfig.MORAL_BALANCE_ENABLED.set(moralBalanceEnabled);
        
        // 返回上一个屏幕
        this.onClose();
    }
    
    /**
     * 创建配置界面的工厂方法
     * 
     * @param minecraft Minecraft实例
     * @param parent 父界面
     * @return 配置界面实例
     */
    public static Screen create(Minecraft minecraft, Screen parent) {
        return new SimpleConfigScreen(parent);
    }
    
    /**
     * 配置滚动列表类
     */
    private class ConfigList extends ContainerObjectSelectionList<ConfigList.Entry> {
        public ConfigList(Minecraft minecraft, int width, int height, int top, int bottom, int itemHeight) {
            super(minecraft, width, height, top, bottom, itemHeight);
            this.setRenderBackground(false);
            this.setRenderTopAndBottom(false);
        }
        
        // 添加公开方法用于添加条目
        public void addConfigEntry(Entry entry) {
            this.addEntry(entry);
        }
        
        @Override
        public int getRowWidth() {
            return Math.min(400, width - 40);
        }
        
        @Override
        protected int getScrollbarPosition() {
            return this.width / 2 + getRowWidth() / 2 + 5;
        }
        
        /**
         * 列表条目基类
         */
        public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
            public boolean changeFocus(boolean focus) {
                return false;
            }
        }
    }
    
    /**
     * 空白间隔条目
     */
    private class SpacerEntry extends ConfigList.Entry {
        private final int height;
        
        public SpacerEntry(int height) {
            this.height = height;
        }
        
        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isHovered, float partialTick) {
            // 空白间隔，不需要渲染任何内容
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }
        
        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }
        
        public int getHeight() {
            return height;
        }
    }
    
    /**
     * 分区标题条目
     */
    private class SectionTitleEntry extends ConfigList.Entry {
        private final Component title;
        
        public SectionTitleEntry(Component title) {
            this.title = title;
        }
        
        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isHovered, float partialTick) {
            Font font = Minecraft.getInstance().font;
            int x = SimpleConfigScreen.this.width / 2;
            int y = top + 5;
            graphics.drawCenteredString(font, title, x, y, 0xFFFF55);
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }
        
        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }
        
        public int getHeight() {
            return 18; // 从20减小到18
        }
    }
    
    /**
     * 标签条目
     */
    private class LabelEntry extends ConfigList.Entry {
        private final Component label;
        
        public LabelEntry(Component label) {
            this.label = label;
        }
        
        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isHovered, float partialTick) {
            Font font = Minecraft.getInstance().font;
            int x = SimpleConfigScreen.this.width / 2 - configList.getRowWidth() / 2;
            int y = top + 3; // 从5减小到3
            graphics.drawString(font, label, x, y, 0xFFFFFF);
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }
        
        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }
        
        public int getHeight() {
            return 13; // 从15减小到13
        }
    }
    
    /**
     * 按钮条目
     */
    private class ButtonEntry extends ConfigList.Entry {
        private final Button button;
        private final int buttonWidth;
        private final List<Button> buttons = new ArrayList<>();
        
        public ButtonEntry(Button button, int buttonWidth) {
            this.button = button;
            this.buttonWidth = buttonWidth;
            buttons.add(button);
        }
        
        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isHovered, float partialTick) {
            int x = SimpleConfigScreen.this.width / 2 - buttonWidth / 2;
            button.setX(x);
            button.setY(top);
            button.setWidth(buttonWidth);
            button.render(graphics, mouseX, mouseY, partialTick);
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            return buttons;
        }
        
        @Override
        public List<? extends NarratableEntry> narratables() {
            return buttons;
        }
        
        public int getHeight() {
            return 22; // 从24减小到22
        }
    }
    
    /**
     * 输入框条目
     */
    private class EditBoxEntry extends ConfigList.Entry {
        private final EditBox editBox;
        private final List<EditBox> editBoxes = new ArrayList<>();
        
        public EditBoxEntry(EditBox editBox) {
            this.editBox = editBox;
            editBoxes.add(editBox);
        }
        
        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isHovered, float partialTick) {
            int x = SimpleConfigScreen.this.width / 2 - configList.getRowWidth() / 2;
            editBox.setX(x);
            editBox.setY(top);
            editBox.setWidth(configList.getRowWidth());
            editBox.render(graphics, mouseX, mouseY, partialTick);
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            return editBoxes;
        }
        
        @Override
        public List<? extends NarratableEntry> narratables() {
            return editBoxes;
        }
        
        public int getHeight() {
            return 22; // 从24减小到22
        }
    }
}