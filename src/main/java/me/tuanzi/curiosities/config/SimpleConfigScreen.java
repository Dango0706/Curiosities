package me.tuanzi.curiosities.config;

import com.mojang.blaze3d.systems.RenderSystem;
import me.tuanzi.curiosities.Curiosities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置界面
 * 支持分类显示和编辑各种类型的配置项
 */
public class SimpleConfigScreen extends Screen {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleConfigScreen.class);

    // 默认标题
    private static final Component TITLE = Component.translatable("config.curiosities.title");

    // 按钮宽度
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;

    // 分类按钮宽度
    private static final int CATEGORY_BUTTON_WIDTH = 120;

    // 分类和配置项容器
    private final List<ConfigCategory> categories = new ArrayList<>();
    // 父级屏幕
    private final Screen parentScreen;
    private ConfigCategory currentCategory;

    // 滚动列表
    private CategoryList categoryList;
    private OptionList optionList;

    /**
     * 构造函数
     */
    public SimpleConfigScreen(Screen parentScreen) {
        super(TITLE);
        this.parentScreen = parentScreen;

        // 初始化配置分类
        initCategories();
    }

    /**
     * 创建配置屏幕的工厂方法
     */
    public static Screen create(Minecraft minecraft, Screen parentScreen) {
        // 确保配置已经加载
        if (!ModConfigManager.isConfigLoaded()) {
            LOGGER.error("配置未加载，无法创建配置界面");
            return parentScreen;
        }

        try {
            // 尝试访问一些配置值，确保配置已完全加载
            ModConfigManager.SCYTHE_ATTACK_SPEED.get();
            ModConfigManager.SCYTHE_DAMAGE_BONUS.get();
            return new SimpleConfigScreen(parentScreen);
        } catch (IllegalStateException e) {
            LOGGER.error("配置访问失败，无法创建配置界面", e);
            return parentScreen;
        }
    }

    /**
     * 初始化配置分类
     * 根据ModConfigManager中的配置项创建分类
     */
    private void initCategories() {
        try {
            // 主分类物品配置
            ConfigCategory itemsCategory = new ConfigCategory(
                    Component.translatable("config.curiosities.items_section"),
                    new ResourceLocation(Curiosities.MODID, "textures/item/wolf_fang_potato.png")
            );

            // 狼牙土豆配置
            itemsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.wolf_fang_potato_enabled"),
                    ModConfigManager.WOLF_FANG_POTATO_ENABLED
            );

            // 镰刀工具配置
            itemsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.scythe_enabled"),
                    ModConfigManager.SCYTHE_ENABLED
            );

            itemsCategory.addDoubleOption(
                    Component.translatable("config.curiosities.attack_speed_label"),
                    ModConfigManager.SCYTHE_ATTACK_SPEED
            );

            itemsCategory.addDoubleOption(
                    Component.translatable("item.curiosities.scythe.damage_bonus"),
                    ModConfigManager.SCYTHE_DAMAGE_BONUS
            );

            itemsCategory.addDoubleOption(
                    Component.translatable("item.curiosities.scythe.harvest_range"),
                    ModConfigManager.SCYTHE_HARVEST_RANGE
            );

            itemsCategory.addDoubleOption(
                    Component.translatable("item.curiosities.scythe.sweep_range_bonus"),
                    ModConfigManager.SCYTHE_SWEEP_RANGE_BONUS
            );

            itemsCategory.addDoubleOption(
                    Component.translatable("item.curiosities.scythe.harvest_dance_chance"),
                    ModConfigManager.SCYTHE_HARVEST_DANCE_CHANCE
            );

            itemsCategory.addDoubleOption(
                    Component.translatable("item.curiosities.scythe.harvest_dance_range"),
                    ModConfigManager.SCYTHE_HARVEST_DANCE_RANGE
            );

            // 火箭靴配置
            itemsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.rocket_boots_enabled"),
                    ModConfigManager.ROCKET_BOOTS_ENABLED
            );

            itemsCategory.addDoubleOption(
                    Component.translatable("item.curiosities.rocket_boots.boost_power"),
                    ModConfigManager.ROCKET_BOOTS_BOOST_POWER
            );

            itemsCategory.addDoubleOption(
                    Component.translatable("config.curiosities.max_jump_height_label"),
                    ModConfigManager.ROCKET_BOOTS_MAX_JUMP_HEIGHT
            );

            itemsCategory.addIntOption(
                    Component.translatable("config.curiosities.fuel_consumption_label"),
                    ModConfigManager.ROCKET_BOOTS_FUEL_CONSUMPTION
            );

            itemsCategory.addIntOption(
                    Component.translatable("config.curiosities.max_fuel_storage_label"),
                    ModConfigManager.ROCKET_BOOTS_MAX_FUEL
            );

            // 幸运剑配置
            itemsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.lucky_sword_enabled"),
                    ModConfigManager.LUCKY_SWORD_ENABLED
            );

            itemsCategory.addDoubleOption(
                    Component.translatable("config.curiosities.min_damage_label"),
                    ModConfigManager.LUCKY_SWORD_MIN_DAMAGE
            );

            itemsCategory.addDoubleOption(
                    Component.translatable("config.curiosities.max_damage_label"),
                    ModConfigManager.LUCKY_SWORD_MAX_DAMAGE
            );

            // 蝙蝠翅膀配置
            itemsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.bat_wing_enabled"),
                    ModConfigManager.BAT_WING_ENABLED
            );

            // 尖叫派配置
            itemsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.screaming_pie_enabled"),
                    ModConfigManager.SCREAMING_PIE_ENABLED
            );

            itemsCategory.addIntOption(
                    Component.translatable("config.curiosities.slow_falling_duration_label"),
                    ModConfigManager.SCREAMING_PIE_SLOW_FALLING_DURATION
            );

            itemsCategory.addIntOption(
                    Component.translatable("config.curiosities.screaming_duration_label"),
                    ModConfigManager.SCREAMING_PIE_SCREAMING_DURATION
            );

            // 蜜蜂手雷配置
            itemsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.bee_grenade_enabled"),
                    ModConfigManager.BEE_GRENADE_ENABLED
            );

            itemsCategory.addIntOption(
                    Component.translatable("config.curiosities.bee_count_label"),
                    ModConfigManager.BEE_GRENADE_BEE_COUNT
            );

            itemsCategory.addIntOption(
                    Component.translatable("config.curiosities.bee_lifetime_label"),
                    ModConfigManager.BEE_GRENADE_BEE_LIFETIME
            );

            itemsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.player_friendly_label"),
                    ModConfigManager.BEE_GRENADE_PLAYER_FRIENDLY
            );

            itemsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.honey_slowness_area_label"),
                    ModConfigManager.BEE_GRENADE_HONEY_SLOWNESS_AREA_ENABLED
            );

            itemsCategory.addDoubleOption(
                    Component.translatable("config.curiosities.honey_area_radius_label"),
                    ModConfigManager.BEE_GRENADE_HONEY_AREA_RADIUS
            );

            itemsCategory.addIntOption(
                    Component.translatable("config.curiosities.honey_area_duration_label"),
                    ModConfigManager.BEE_GRENADE_HONEY_AREA_DURATION
            );

            itemsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.destroy_blocks_label"),
                    ModConfigManager.BEE_GRENADE_DESTROY_BLOCKS
            );

            // 控制之杖配置
            itemsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.control_staff_enabled"),
                    ModConfigManager.CONTROL_STAFF_ENABLED
            );

            itemsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.control_staff_craftable"),
                    ModConfigManager.CONTROL_STAFF_CRAFTABLE
            );

            // 时空卷轴配置
            itemsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.scroll_of_spacetime_enabled"),
                    ModConfigManager.SCROLL_OF_SPACETIME_ENABLED
            );

            itemsCategory.addIntOption(
                    Component.translatable("config.curiosities.scroll_of_spacetime_max_distance_label"),
                    ModConfigManager.SCROLL_OF_SPACETIME_MAX_DISTANCE
            );

            itemsCategory.addIntOption(
                    Component.translatable("config.curiosities.scroll_of_spacetime_cooldown_label"),
                    ModConfigManager.SCROLL_OF_SPACETIME_COOLDOWN
            );

            itemsCategory.addIntOption(
                    Component.translatable("config.curiosities.scroll_of_spacetime_durability_cost_label"),
                    ModConfigManager.SCROLL_OF_SPACETIME_DURABILITY_COST
            );

            itemsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.scroll_of_spacetime_tradeable_label"),
                    ModConfigManager.SCROLL_OF_SPACETIME_TRADEABLE
            );

            // 无限水桶配置
            itemsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.infinite_water_bucket_enabled"),
                    ModConfigManager.INFINITE_WATER_BUCKET_ENABLED
            );
            
            // 虚空吞噬之剑配置
            itemsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.void_sword_enabled"),
                    ModConfigManager.VOID_SWORD_ENABLED
            );

            itemsCategory.addIntOption(
                    Component.translatable("config.curiosities.void_sword_max_energy_label"),
                    ModConfigManager.VOID_SWORD_MAX_ENERGY
            );

            itemsCategory.addDoubleOption(
                    Component.translatable("config.curiosities.void_sword_energy_percent_label"),
                    ModConfigManager.VOID_SWORD_ENERGY_PERCENT
            );

            itemsCategory.addDoubleOption(
                    Component.translatable("config.curiosities.void_sword_black_hole_range_label"),
                    ModConfigManager.VOID_SWORD_BLACK_HOLE_RANGE
            );

            itemsCategory.addDoubleOption(
                    Component.translatable("config.curiosities.void_sword_black_hole_damage_label"),
                    ModConfigManager.VOID_SWORD_BLACK_HOLE_DAMAGE
            );

            itemsCategory.addIntOption(
                    Component.translatable("config.curiosities.void_sword_black_hole_duration_label"),
                    ModConfigManager.VOID_SWORD_BLACK_HOLE_DURATION
            );

            itemsCategory.addIntOption(
                    Component.translatable("config.curiosities.void_sword_black_hole_damage_interval_label"),
                    ModConfigManager.VOID_SWORD_BLACK_HOLE_DAMAGE_INTERVAL
            );

            itemsCategory.addIntOption(
                    Component.translatable("config.curiosities.void_sword_cooldown_label"),
                    ModConfigManager.VOID_SWORD_COOLDOWN
            );

            itemsCategory.addIntOption(
                    Component.translatable("config.curiosities.void_sword_max_cast_distance_label"),
                    ModConfigManager.VOID_SWORD_MAX_CAST_DISTANCE
            );

            // 概率圣剑配置
            itemsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.probability_holy_sword_enabled"),
                    ModConfigManager.PROBABILITY_HOLY_SWORD_ENABLED
            );

            itemsCategory.addDoubleOption(
                    Component.translatable("config.curiosities.probability_holy_sword_chest_spawn_chance_label"),
                    ModConfigManager.PROBABILITY_HOLY_SWORD_CHEST_SPAWN_CHANCE
            );

            itemsCategory.addIntOption(
                    Component.translatable("config.curiosities.probability_holy_sword_base_damage_label"),
                    ModConfigManager.PROBABILITY_HOLY_SWORD_BASE_DAMAGE
            );

            itemsCategory.addDoubleOption(
                    Component.translatable("config.curiosities.probability_holy_sword_effect_chance_label"),
                    ModConfigManager.PROBABILITY_HOLY_SWORD_EFFECT_CHANCE
            );

            itemsCategory.addDoubleOption(
                    Component.translatable("config.curiosities.probability_holy_sword_lucky_strike_max_health_label"),
                    ModConfigManager.PROBABILITY_HOLY_SWORD_LUCKY_STRIKE_MAX_HEALTH
            );

            // 生物指南针配置
            itemsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.entity_compass_enabled"),
                    ModConfigManager.ENTITY_COMPASS_ENABLED
            );

            itemsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.entity_compass_craftable"),
                    ModConfigManager.ENTITY_COMPASS_CRAFTABLE
            );

            itemsCategory.addIntOption(
                    Component.translatable("config.curiosities.entity_compass_glow_range_label"),
                    ModConfigManager.ENTITY_COMPASS_GLOW_RANGE
            );

            // 原版修改配置
            ConfigCategory vanillaModificationsCategory = new ConfigCategory(
                    Component.translatable("config.curiosities.vanilla_modifications_section"),
                    new ResourceLocation("minecraft", "textures/block/crafting_table_top.png")
            );

            // 原版修改配置选项
            vanillaModificationsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.vanilla_modifications_enabled"),
                    ModConfigManager.VANILLA_MODIFICATIONS_ENABLED
            );

            vanillaModificationsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.improved_villager_trades_enabled"),
                    ModConfigManager.IMPROVED_VILLAGER_TRADES_ENABLED
            );

            vanillaModificationsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.enhanced_anvil_enabled"),
                    ModConfigManager.ENHANCED_ANVIL_ENABLED
            );

            vanillaModificationsCategory.addIntOption(
                    Component.translatable("config.curiosities.enhanced_anvil_max_repair_cost"),
                    ModConfigManager.ENHANCED_ANVIL_MAX_REPAIR_COST
            );

            vanillaModificationsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.glass_bottle_to_water_bottle_enabled"),
                    ModConfigManager.GLASS_BOTTLE_TO_WATER_BOTTLE_ENABLED
            );

            // 添加创造模式村民交易自动填充选项
            vanillaModificationsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.creative_trade_auto_fill_enabled"),
                    ModConfigManager.CREATIVE_TRADE_AUTO_FILL_ENABLED
            );

            // 状态效果配置
            ConfigCategory effectsCategory = new ConfigCategory(
                    Component.translatable("config.curiosities.effects_section"),
                    new ResourceLocation("minecraft", "textures/item/splash_potion.png")
            );

            // 尖叫效果配置
            effectsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.screaming_effect_enabled"),
                    ModConfigManager.SCREAMING_EFFECT_ENABLED
            );

            effectsCategory.addIntOption(
                    Component.translatable("config.curiosities.screaming_range_label"),
                    ModConfigManager.SCREAMING_EFFECT_RANGE
            );

            // 颠颠倒倒效果配置
            effectsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.dizzy_effect_enabled"),
                    ModConfigManager.DIZZY_EFFECT_ENABLED
            );

            // 天旋地转效果配置
            effectsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.spinning_effect_enabled"),
                    ModConfigManager.SPINNING_EFFECT_ENABLED
            );

            // 瓦解之躯效果配置
            effectsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.dissolving_body_effect_enabled"),
                    ModConfigManager.DISSOLVING_BODY_EFFECT_ENABLED
            );

            // 富有效果配置
            effectsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.rich_effect_enabled"),
                    ModConfigManager.RICH_EFFECT_ENABLED
            );
            effectsCategory.addIntOption(
                    Component.translatable("config.curiosities.rich_effect_range_per_level"),
                    ModConfigManager.RICH_EFFECT_RANGE_PER_LEVEL
            );

            // 混乱效果配置
            effectsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.confusion_effect_enabled"),
                    ModConfigManager.CONFUSION_EFFECT_ENABLED
            );
            effectsCategory.addDoubleOption(
                    Component.translatable("config.curiosities.confusion_chance_per_level"),
                    ModConfigManager.CONFUSION_CHANCE_PER_LEVEL
            );
            effectsCategory.addDoubleOption(
                    Component.translatable("config.curiosities.confusion_damage_percent_per_level"),
                    ModConfigManager.CONFUSION_DAMAGE_PERCENT_PER_LEVEL
            );
            effectsCategory.addDoubleOption(
                    Component.translatable("config.curiosities.confusion_damage_percent_max"),
                    ModConfigManager.CONFUSION_DAMAGE_PERCENT_MAX
            );

            // 不死效果配置
            effectsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.undying_effect_enabled"),
                    ModConfigManager.UNDYING_EFFECT_ENABLED
            );

            // 附魔配置主分类
            ConfigCategory enchantmentsCategory = new ConfigCategory(
                    Component.translatable("config.curiosities.enchantments_section"),
                    new ResourceLocation("minecraft", "textures/item/enchanted_book.png")
            );

            // 连锁挖矿配置
            enchantmentsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.chain_mining_enabled"),
                    ModConfigManager.CHAIN_MINING_ENABLED
            );

            enchantmentsCategory.addIntOption(
                    Component.translatable("config.curiosities.max_blocks_label"),
                    ModConfigManager.CHAIN_MINING_MAX_BLOCKS
            );

            enchantmentsCategory.addIntOption(
                    Component.translatable("config.curiosities.blocks_per_level_label"),
                    ModConfigManager.CHAIN_MINING_BLOCKS_PER_LEVEL
            );

            enchantmentsCategory.addIntOption(
                    Component.translatable("config.curiosities.harvest_range_label"),
                    ModConfigManager.CHAIN_MINING_HARVEST_RANGE
            );

            // 超级时运配置
            enchantmentsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.super_fortune_enabled"),
                    ModConfigManager.SUPER_FORTUNE_ENABLED
            );

            // 道德天平配置
            enchantmentsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.moral_balance_enabled"),
                    ModConfigManager.MORAL_BALANCE_ENABLED
            );

            // 钢契附魔配置
            enchantmentsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.steel_covenant_enabled"),
                    ModConfigManager.STEEL_COVENANT_ENABLED
            );
            
            enchantmentsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.steel_covenant_tradeable"),
                    ModConfigManager.STEEL_COVENANT_TRADEABLE
            );

            // 熟练附魔配置
            enchantmentsCategory.addBooleanOption(
                    Component.translatable("config.curiosities.proficiency_enabled"),
                    ModConfigManager.PROFICIENCY_ENABLED
            );

            enchantmentsCategory.addDoubleOption(
                    Component.translatable("config.curiosities.proficiency_attack_speed_percent_label"),
                    ModConfigManager.PROFICIENCY_ATTACK_SPEED_PERCENT
            );

            // 方块配置主分类
            ConfigCategory blocksCategory = new ConfigCategory(
                    Component.translatable("config.curiosities.blocks_section"),
                    new ResourceLocation("minecraft", "textures/item/tnt_minecart.png")
            );

            // 假TNT配置
            blocksCategory.addBooleanOption(
                    Component.translatable("config.curiosities.fake_tnt_enabled"),
                    ModConfigManager.FAKE_TNT_ENABLED
            );

            categories.add(itemsCategory);
            categories.add(effectsCategory);
            categories.add(enchantmentsCategory);
            categories.add(blocksCategory);
            categories.add(vanillaModificationsCategory);

            // 默认选择第一个分类
            if (!categories.isEmpty()) {
                currentCategory = categories.get(0);
            }

        } catch (IllegalStateException e) {
            LOGGER.error("配置初始化失败", e);
        }
    }

    @Override
    protected void init() {
        super.init();

        // 创建左侧分类滚动列表
        categoryList = new CategoryList(minecraft, CATEGORY_BUTTON_WIDTH, height, 30, height - 40, 25);
        this.addRenderableWidget(categoryList);

        // 创建右侧配置项滚动列表
        optionList = new OptionList(minecraft, width - CATEGORY_BUTTON_WIDTH - 20, height, 30, height - 40, 30);
        this.addRenderableWidget(optionList);

        // 填充分类列表
        categoryList.children().clear();
        for (int i = 0; i < categories.size(); i++) {
            ConfigCategory category = categories.get(i);
            categoryList.children().add(new CategoryEntry(category, i));
        }

        // 如果当前分类不为空，显示其配置项
        if (currentCategory != null) {
            refreshOptionList();
        }

        // 添加底部按钮
        // 保存按钮
        Button saveButton = Button.builder(Component.translatable("gui.done"), button -> {
                    // 关闭界面，返回父级屏幕
                    onClose();
                })
                .pos(width / 2 - BUTTON_WIDTH / 2, height - 30)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();

        addRenderableWidget(saveButton);
    }

    /**
     * 刷新右侧配置项列表
     *
     * @param resetScroll 是否重置滚动位置
     */
    private void refreshOptionList(boolean resetScroll) {
        optionList.children().clear();
        for (int i = 0; i < currentCategory.getOptions().size(); i++) {
            ConfigOption<?> option = currentCategory.getOptions().get(i);
            optionList.children().add(new OptionEntry(option, i));
        }

        // 根据参数决定是否重置滚动条位置
        if (resetScroll) {
            optionList.setScrollAmount(0);
        }
    }

    /**
     * 刷新右侧配置项列表（并重置滚动位置）
     */
    private void refreshOptionList() {
        refreshOptionList(true);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 渲染背景
        renderBackground(guiGraphics);

        // 渲染标题
        guiGraphics.drawCenteredString(font, title, width / 2, 10, 0xFFFFFF);

        // 渲染分类区域和配置区域的分隔线
        int leftPanelWidth = CATEGORY_BUTTON_WIDTH + 10;
        guiGraphics.fill(leftPanelWidth, 30, leftPanelWidth + 2, height - 40, 0xFFAAAAAA);

        // 渲染当前分类的标题
        if (currentCategory != null) {
            guiGraphics.drawString(font, currentCategory.getTitle(), leftPanelWidth + 20, 10, 0xFFFFFF);

            // 渲染分类图标
            ResourceLocation icon = currentCategory.getIcon();
            if (icon != null) {
                RenderSystem.setShaderTexture(0, icon);
                guiGraphics.blit(icon, leftPanelWidth + 50, 10, 0, 0, 16, 16, 16, 16);
            }
        }

        // 渲染滚动列表
        categoryList.render(guiGraphics, mouseX, mouseY, partialTick);
        optionList.render(guiGraphics, mouseX, mouseY, partialTick);

        // 渲染控件
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parentScreen);
    }

    /**
     * 配置分类
     * 包含一组相关的配置选项
     */
    private static class ConfigCategory {
        private final Component title;
        private final ResourceLocation icon;
        private final List<ConfigOption<?>> options = new ArrayList<>();

        public ConfigCategory(Component title, ResourceLocation icon) {
            this.title = title;
            this.icon = icon;
        }

        public Component getTitle() {
            return title;
        }

        public ResourceLocation getIcon() {
            return icon;
        }

        public List<ConfigOption<?>> getOptions() {
            return options;
        }

        public void addBooleanOption(Component label, ForgeConfigSpec.BooleanValue value) {
            options.add(new ConfigOption<>(label, value));
        }

        public void addIntOption(Component label, ForgeConfigSpec.IntValue value) {
            options.add(new ConfigOption<>(label, value));
        }

        public void addDoubleOption(Component label, ForgeConfigSpec.DoubleValue value) {
            options.add(new ConfigOption<>(label, value));
        }

        public <T> void addOption(Component label, ForgeConfigSpec.ConfigValue<T> value) {
            options.add(new ConfigOption<>(label, value));
        }
    }

    /**
     * 配置选项
     * 包含一个标签和一个配置值
     */
    private static class ConfigOption<T> {
        private final Component label;
        private final ForgeConfigSpec.ConfigValue<T> value;

        public ConfigOption(Component label, ForgeConfigSpec.ConfigValue<T> value) {
            this.label = label;
            this.value = value;
        }

        public Component getLabel() {
            return label;
        }

        public ForgeConfigSpec.ConfigValue<T> getValue() {
            return value;
        }
    }

    /**
     * 分类滚动列表
     */
    private class CategoryList extends ContainerObjectSelectionList<CategoryEntry> {
        public CategoryList(Minecraft minecraft, int width, int height, int top, int bottom, int itemHeight) {
            super(minecraft, width, height, top, bottom, itemHeight);
            this.setLeftPos(5);
        }

        @Override
        public int getRowWidth() {
            return CATEGORY_BUTTON_WIDTH - 10;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.getLeft() + this.width - 6;
        }
    }

    /**
     * 分类列表项
     */
    private class CategoryEntry extends ContainerObjectSelectionList.Entry<CategoryEntry> {
        private final ConfigCategory category;
        private final Button button;
        private final int index;

        public CategoryEntry(ConfigCategory category, int index) {
            this.category = category;
            this.index = index;
            this.button = Button.builder(category.getTitle(), b -> {
                        currentCategory = category;
                        refreshOptionList();
                    })
                    .size(CATEGORY_BUTTON_WIDTH - 10, 20)
                    .build();

            // 高亮当前选中的分类
            if (category == currentCategory) {
                button.active = false;
            }
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(this.button);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of(this.button);
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isHovered, float partialTick) {
            this.button.setPosition(left, top);
            // 更新按钮的激活状态
            this.button.active = (category != currentCategory);
            this.button.render(guiGraphics, mouseX, mouseY, partialTick);

            // 如果有图标，绘制图标
            ResourceLocation icon = category.getIcon();
            if (icon != null) {
                RenderSystem.setShaderTexture(0, icon);
                guiGraphics.blit(icon, left + 2, top + 2, 0, 0, 16, 16, 16, 16);
            }
        }
    }

    /**
     * 配置项滚动列表
     */
    private class OptionList extends ContainerObjectSelectionList<OptionEntry> {
        public OptionList(Minecraft minecraft, int width, int height, int top, int bottom, int itemHeight) {
            super(minecraft, width, height, top, bottom, itemHeight);
            this.setLeftPos(CATEGORY_BUTTON_WIDTH + 20);
        }

        @Override
        public int getRowWidth() {
            return width - 10;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.getLeft() + this.width - 6;
        }
    }

    /**
     * 配置项列表项
     */
    private class OptionEntry extends ContainerObjectSelectionList.Entry<OptionEntry> {
        private final ConfigOption<?> option;
        private final int index;
        private final List<GuiEventListener> widgets = new ArrayList<>();
        private final Button resetButton;
        private boolean isConfirming = false;

        public OptionEntry(@NotNull ConfigOption<?> option, int index) {
            this.option = option;
            this.index = index;

            // 添加重置按钮
            this.resetButton = Button.builder(
                            Component.translatable("config.curiosities.reset_default"),
                            button -> {
                                if (!isConfirming) {
                                    // 第一次点击，显示确认文本
                                    isConfirming = true;
                                    button.setMessage(Component.translatable("config.curiosities.reset_confirm"));
                                } else {
                                    // 第二次点击，执行重置
                                    try {
                                        // 获取配置值的默认值
                                        ForgeConfigSpec.ConfigValue<?> configValue = option.getValue();
                                        if (configValue instanceof ForgeConfigSpec.BooleanValue booleanValue) {
                                            booleanValue.set(booleanValue.getDefault());
                                        } else if (configValue instanceof ForgeConfigSpec.IntValue intValue) {
                                            intValue.set(intValue.getDefault());
                                        } else if (configValue instanceof ForgeConfigSpec.DoubleValue doubleValue) {
                                            doubleValue.set(doubleValue.getDefault());
                                        }
                                        // 刷新显示，但不重置滚动位置
                                        refreshOptionList(false);
                                    } catch (IllegalStateException e) {
                                        // 如果配置未加载，不执行任何操作
                                    }
                                    // 重置确认状态
                                    isConfirming = false;
                                    button.setMessage(Component.translatable("config.curiosities.reset_default"));
                                }
                            })
                    .size(100, 20)
                    .build();

            widgets.add(resetButton);

            // 根据配置类型创建不同的控件
            if (option.getValue() instanceof ForgeConfigSpec.BooleanValue booleanValue) {
                // 布尔值选项 - 使用开关按钮
                Button toggleButton = Button.builder(
                                // 根据实际配置值设置初始文本
                                Component.literal(booleanValue.get() ? "✓" : "✗"), button -> {
                                    try {
                                        // 切换布尔值
                                        booleanValue.set(!booleanValue.get());
                                        // 更新按钮文本
                                        button.setMessage(Component.literal(booleanValue.get() ? "✓" : "✗"));
                                    } catch (IllegalStateException e) {
                                        // 如果配置未加载，显示错误信息
                                        button.setMessage(Component.literal("✗"));
                                    }
                                })
                        .size(30, 20)
                        .build();
                widgets.add(toggleButton);
            } else if (option.getValue() instanceof ForgeConfigSpec.IntValue intValue) {
                // 整数值选项 - 使用加减按钮和文本框
                // 添加减号按钮
                Button decreaseButton = Button.builder(Component.literal("-"), button -> {
                            try {
                                int newValue = intValue.get() - 1;
                                intValue.set(newValue);
                                refreshOptionList(false); // 刷新显示但不重置滚动位置
                            } catch (IllegalStateException e) {
                                // 如果配置未加载，不执行任何操作
                            }
                        })
                        .size(20, 20)
                        .build();
                widgets.add(decreaseButton);

                // 添加显示当前值的文本
                EditBox valueBox = new EditBox(font, 0, 0, 70, 20, Component.empty());
                try {
                    valueBox.setValue(String.valueOf(intValue.get()));
                } catch (IllegalStateException e) {
                    valueBox.setValue("0");
                }
                valueBox.setResponder(s -> {
                    try {
                        int value = Integer.parseInt(s);
                        intValue.set(value);
                    } catch (NumberFormatException | IllegalStateException ignored) {
                        // 忽略非数字输入或配置未加载的情况
                    }
                });
                widgets.add(valueBox);

                // 添加加号按钮
                Button increaseButton = Button.builder(Component.literal("+"), button -> {
                            try {
                                int newValue = intValue.get() + 1;
                                intValue.set(newValue);
                                refreshOptionList(false); // 刷新显示但不重置滚动位置
                            } catch (IllegalStateException e) {
                                // 如果配置未加载，不执行任何操作
                            }
                        })
                        .size(20, 20)
                        .build();
                widgets.add(increaseButton);
            } else if (option.getValue() instanceof ForgeConfigSpec.DoubleValue doubleValue) {
                // 浮点数选项 - 使用加减按钮和文本框
                // 添加减号按钮
                Button decreaseButton = Button.builder(Component.literal("-"), button -> {
                            try {
                                double newValue = doubleValue.get() - 0.1;
                                doubleValue.set(newValue);
                                refreshOptionList(false); // 刷新显示但不重置滚动位置
                            } catch (IllegalStateException e) {
                                // 如果配置未加载，不执行任何操作
                            }
                        })
                        .size(20, 20)
                        .build();
                widgets.add(decreaseButton);

                // 添加显示当前值的文本
                EditBox valueBox = new EditBox(font, 0, 0, 70, 20, Component.empty());
                try {
                    valueBox.setValue(String.format("%.2f", doubleValue.get()));
                } catch (IllegalStateException e) {
                    valueBox.setValue("0.0");
                }
                valueBox.setResponder(s -> {
                    try {
                        double value = Double.parseDouble(s);
                        doubleValue.set(value);
                    } catch (NumberFormatException | IllegalStateException ignored) {
                        // 忽略非数字输入或配置未加载的情况
                    }
                });
                widgets.add(valueBox);

                // 添加加号按钮
                Button increaseButton = Button.builder(Component.literal("+"), button -> {
                            try {
                                double newValue = doubleValue.get() + 0.1;
                                doubleValue.set(newValue);
                                refreshOptionList(false); // 刷新显示但不重置滚动位置
                            } catch (IllegalStateException e) {
                                // 如果配置未加载，不执行任何操作
                            }
                        })
                        .size(20, 20)
                        .build();
                widgets.add(increaseButton);
            } else if (option.getValue() instanceof ForgeConfigSpec.ConfigValue<?>) {
                // 字符串选项 - 使用文本框
                if (option.getValue().get() instanceof String) {
                    String currentValue = "";
                    try {
                        currentValue = option.getValue().get().toString();
                    } catch (IllegalStateException e) {
                        // 如果配置未加载，使用空字符串
                    }

                    EditBox editBox = new EditBox(
                            font, 0, 0, 120, 20,
                            Component.empty());

                    editBox.setValue(currentValue);
                    editBox.setResponder(text -> {
                        try {
                            ((ForgeConfigSpec.ConfigValue<String>) option.getValue()).set(text);
                        } catch (IllegalStateException ignored) {
                            // 如果配置未加载，不执行任何操作
                        }
                    });

                    widgets.add(editBox);
                }
            }
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return widgets;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return widgets.stream()
                    .filter(w -> w instanceof NarratableEntry)
                    .map(w -> (NarratableEntry) w)
                    .toList();
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isHovered, float partialTick) {
            // 渲染配置项标签
            guiGraphics.drawString(font, option.getLabel(), left, top + 6, 0xFFFFFF);

            // 根据控件类型调整位置
            int controlX = left + 150;

            // 渲染重置按钮
            resetButton.setPosition(controlX + 120, top);
            resetButton.render(guiGraphics, mouseX, mouseY, partialTick);

            if (widgets.size() == 2) {
                // 单个控件（布尔值）
                GuiEventListener widget = widgets.get(1);
                if (widget instanceof Button button) {
                    button.setPosition(controlX, top);
                    button.render(guiGraphics, mouseX, mouseY, partialTick);
                }
            } else if (widgets.size() == 4) {
                // 三个控件（数值类型：减号、文本框、加号）
                Button decreaseButton = (Button) widgets.get(1);
                EditBox valueBox = (EditBox) widgets.get(2);
                Button increaseButton = (Button) widgets.get(3);

                decreaseButton.setPosition(controlX, top);
                valueBox.setPosition(controlX + 25, top);
                increaseButton.setPosition(controlX + 100, top);

                decreaseButton.render(guiGraphics, mouseX, mouseY, partialTick);
                valueBox.render(guiGraphics, mouseX, mouseY, partialTick);
                increaseButton.render(guiGraphics, mouseX, mouseY, partialTick);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            // 检查是否点击了重置按钮
            if (resetButton.isMouseOver(mouseX, mouseY)) {
                if (button == 1) { // 右键点击
                    if (isConfirming) {
                        // 右键点击取消确认
                        isConfirming = false;
                        resetButton.setMessage(Component.translatable("config.curiosities.reset_default"));
                        return true;
                    }
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }
}
