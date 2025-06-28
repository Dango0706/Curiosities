package me.tuanzi.curiosities.items.entity_compass;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 * 生物选择界面
 * 用于选择生物指南针要寻找的生物类型
 * <p>
 * 界面布局：
 * - 顶部：标题区域
 * - 中间：可滚动的生物列表（占主要区域）
 * - 底部：取消和完成按钮
 */
public class EntitySelectionScreen extends Screen {
    // 界面布局常量
    private static final int TITLE_HEIGHT = 30;
    private static final int SEARCH_BOX_HEIGHT = 20;
    private static final int SEARCH_BOX_WIDTH = 250;
    private static final int SEARCH_SPACING = 15;
    private static final int BUTTON_AREA_HEIGHT = 50;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_SPACING = 20;
    private static final int MARGIN = 20;
    private final ItemStack compass;
    private final List<EntityType<?>> entityTypes;
    private final List<EntityType<?>> filteredEntityTypes;
    private EntityType<?> selectedEntity;
    private String currentSearchText = "";
    // GUI组件
    private EntitySelectionList entityList;
    private EditBox searchBox;
    private Button confirmButton;
    private Button cancelButton;

    public EntitySelectionScreen(ItemStack compass) {
        super(Component.translatable("gui.curiosities.entity_compass.title"));
        this.compass = compass;
        this.entityTypes = new ArrayList<>();
        this.filteredEntityTypes = new ArrayList<>();

        // 收集所有已注册的生物类型
        for (EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES.getValues()) {
            if (entityType != EntityType.PLAYER) { // 排除玩家
                entityTypes.add(entityType);
            }
        }

        // 按名称排序
        entityTypes.sort((a, b) -> {
            String nameA = Component.translatable(a.getDescriptionId()).getString();
            String nameB = Component.translatable(b.getDescriptionId()).getString();
            return nameA.compareToIgnoreCase(nameB);
        });

        // 初始化过滤列表为所有生物
        filteredEntityTypes.addAll(entityTypes);
    }

    @Override
    protected void init() {
        super.init();

        // 创建搜索框
        int searchBoxX = (this.width - SEARCH_BOX_WIDTH) / 2;
        int searchBoxY = TITLE_HEIGHT + MARGIN;

        this.searchBox = new EditBox(this.font, searchBoxX, searchBoxY, SEARCH_BOX_WIDTH, SEARCH_BOX_HEIGHT,
                Component.translatable("gui.curiosities.entity_compass.search"));

        // 设置搜索框样式以确保可见性
        this.searchBox.setHint(Component.translatable("gui.curiosities.entity_compass.search_placeholder"));
        this.searchBox.setValue(currentSearchText);
        this.searchBox.setResponder(this::onSearchTextChanged);
        this.searchBox.setBordered(true);
        this.searchBox.setMaxLength(50);
        this.searchBox.setTextColor(0xFFFFFF); // 白色文字
        this.searchBox.setTextColorUneditable(0xA0A0A0); // 灰色文字（不可编辑状态）
        this.searchBox.setEditable(true);
        this.searchBox.setCanLoseFocus(true);

        this.addRenderableWidget(this.searchBox);

        // 计算响应式布局尺寸（考虑搜索框）
        int listTop = TITLE_HEIGHT + MARGIN + SEARCH_BOX_HEIGHT + SEARCH_SPACING;
        int listBottom = this.height - BUTTON_AREA_HEIGHT - MARGIN;
        int listHeight = listBottom - listTop;

        // 创建生物选择列表
        this.entityList = new EntitySelectionList(this.minecraft, this.width - 2 * MARGIN, listHeight, listTop, 25);
        this.entityList.setLeftPos(MARGIN);

        // 刷新列表内容
        this.refreshEntityList();

        this.addWidget(this.entityList);

        // 计算按钮位置（底部居中）
        int buttonY = this.height - BUTTON_AREA_HEIGHT + 15;
        int totalButtonWidth = 2 * BUTTON_WIDTH + BUTTON_SPACING;
        int buttonStartX = (this.width - totalButtonWidth) / 2;

        // 取消按钮（左侧）
        this.cancelButton = Button.builder(
                        Component.translatable("gui.curiosities.entity_compass.cancel"),
                        button -> this.onClose()
                )
                .bounds(buttonStartX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();
        this.addRenderableWidget(this.cancelButton);

        // 完成按钮（右侧）
        this.confirmButton = Button.builder(
                        Component.translatable("gui.curiosities.entity_compass.confirm"),
                        button -> this.confirmSelection()
                )
                .bounds(buttonStartX + BUTTON_WIDTH + BUTTON_SPACING, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();

        // 初始状态下完成按钮禁用
        this.confirmButton.active = false;
        this.addRenderableWidget(this.confirmButton);
    }

    /**
     * 确认选择
     */
    private void confirmSelection() {
        if (selectedEntity != null) {
            // 设置选定的生物类型
            EntityCompassItem compassItem = (EntityCompassItem) compass.getItem();
            ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(selectedEntity);
            if (entityId != null) {
                compassItem.setSelectedEntityType(compass, entityId.toString());
            }
        }
        this.onClose();
    }

    /**
     * 更新按钮状态
     */
    private void updateButtonStates() {
        if (this.confirmButton != null) {
            this.confirmButton.active = (selectedEntity != null);
        }
    }

    /**
     * 设置选中的生物
     */
    public void setSelectedEntity(EntityType<?> entity) {
        this.selectedEntity = entity;
        updateButtonStates();
    }

    /**
     * 搜索文本变化处理
     */
    private void onSearchTextChanged(String searchText) {
        this.currentSearchText = searchText;
        this.filterEntityList(searchText);
        this.refreshEntityList();

        // 如果当前选中的生物不在过滤结果中，清除选择
        if (selectedEntity != null && !filteredEntityTypes.contains(selectedEntity)) {
            setSelectedEntity(null);
        }
    }

    /**
     * 根据搜索文本过滤生物列表
     */
    private void filterEntityList(String searchText) {
        filteredEntityTypes.clear();

        if (searchText == null || searchText.trim().isEmpty()) {
            // 搜索框为空时显示所有生物
            filteredEntityTypes.addAll(entityTypes);
        } else {
            String lowerSearchText = searchText.toLowerCase();

            for (EntityType<?> entityType : entityTypes) {
                // 获取生物的本地化名称
                String entityName = Component.translatable(entityType.getDescriptionId()).getString().toLowerCase();

                // 检查是否匹配搜索文本
                if (entityName.contains(lowerSearchText)) {
                    filteredEntityTypes.add(entityType);
                }
            }
        }
    }

    /**
     * 刷新生物列表显示
     */
    private void refreshEntityList() {
        if (this.entityList != null) {
            this.entityList.clearEntries();

            for (EntityType<?> entityType : filteredEntityTypes) {
                this.entityList.addEntityEntry(this.entityList.new EntityEntry(entityType));
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 渲染背景
        this.renderBackground(guiGraphics);

        // 绘制标题（使用更大的字体）
        Component title = Component.translatable("gui.curiosities.entity_compass.title");
        int titleWidth = this.font.width(title);
        int titleX = (this.width - titleWidth) / 2;
        int titleY = MARGIN;

        // 绘制标题背景
        guiGraphics.fill(titleX - 5, titleY - 2, titleX + titleWidth + 5, titleY + this.font.lineHeight + 2, 0x80000000);

        // 绘制标题文字
        guiGraphics.drawString(this.font, title, titleX, titleY, 0xFFFFFF);

        // 绘制搜索框背景以增强可见性
        if (this.searchBox != null) {
            int searchBoxX = this.searchBox.getX();
            int searchBoxY = this.searchBox.getY();
            int searchBoxWidth = this.searchBox.getWidth();
            int searchBoxHeight = this.searchBox.getHeight();

            // 绘制搜索框外边框
            guiGraphics.fill(searchBoxX - 1, searchBoxY - 1,
                    searchBoxX + searchBoxWidth + 1, searchBoxY + searchBoxHeight + 1,
                    0xFF000000); // 黑色边框

            // 绘制搜索框背景
            guiGraphics.fill(searchBoxX, searchBoxY,
                    searchBoxX + searchBoxWidth, searchBoxY + searchBoxHeight,
                    this.searchBox.isFocused() ? 0xFF404040 : 0xFF202020); // 焦点时更亮
        }

        // 渲染生物列表
        this.entityList.render(guiGraphics, mouseX, mouseY, partialTick);

        // 如果搜索结果为空，显示提示信息
        if (filteredEntityTypes.isEmpty() && !currentSearchText.trim().isEmpty()) {
            Component noResultsText = Component.translatable("gui.curiosities.entity_compass.no_results");
            int listCenterY = (TITLE_HEIGHT + MARGIN + SEARCH_BOX_HEIGHT + SEARCH_SPACING +
                    this.height - BUTTON_AREA_HEIGHT - MARGIN) / 2;
            guiGraphics.drawCenteredString(this.font, noResultsText, this.width / 2, listCenterY, 0xAAAAAA);
        }

        // 绘制当前选择的生物信息
        if (selectedEntity != null) {
            Component selectedText = Component.translatable("gui.curiosities.entity_compass.selected",
                    Component.translatable(selectedEntity.getDescriptionId()));
            int selectedY = this.height - BUTTON_AREA_HEIGHT - 5;
            guiGraphics.drawCenteredString(this.font, selectedText, this.width / 2, selectedY, 0x00FF00);
        }

        // 渲染按钮和其他组件（包括搜索框）
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /**
     * 自定义生物选择列表
     */
    private class EntitySelectionList extends ObjectSelectionList<EntitySelectionList.EntityEntry> {

        public EntitySelectionList(net.minecraft.client.Minecraft minecraft, int width, int height, int top, int itemHeight) {
            super(minecraft, width, height, top, top + height, itemHeight);
        }

        @Override
        public int getRowWidth() {
            return this.width - 20; // 留出滚动条空间
        }

        @Override
        protected int getScrollbarPosition() {
            return this.getRight() - 6;
        }

        /**
         * 公共的添加条目方法
         */
        public void addEntityEntry(EntityEntry entry) {
            this.addEntry(entry);
        }

        /**
         * 清空所有条目
         */
        public void clearEntries() {
            this.children().clear();
        }

        /**
         * 生物条目
         */
        public class EntityEntry extends ObjectSelectionList.Entry<EntityEntry> {
            private final EntityType<?> entityType;
            private final Component displayName;

            public EntityEntry(EntityType<?> entityType) {
                this.entityType = entityType;
                this.displayName = Component.translatable(entityType.getDescriptionId());
            }

            @Override
            public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height,
                               int mouseX, int mouseY, boolean isMouseOver, float partialTick) {

                // 判断是否为选中状态
                boolean isSelected = (selectedEntity == this.entityType);

                // 绘制背景
                int backgroundColor;
                if (isSelected) {
                    backgroundColor = 0x80FFFFFF; // 选中状态：半透明白色
                } else if (isMouseOver) {
                    backgroundColor = 0x40FFFFFF; // 悬停状态：更淡的白色
                } else {
                    backgroundColor = 0x20000000; // 默认状态：半透明黑色
                }

                guiGraphics.fill(left, top, left + width, top + height, backgroundColor);

                // 绘制生物名称
                int textColor = isSelected ? 0xFFFF00 : 0xFFFFFF; // 选中时黄色，否则白色
                int textY = top + (height - EntitySelectionScreen.this.font.lineHeight) / 2;

                guiGraphics.drawString(EntitySelectionScreen.this.font, this.displayName,
                        left + 5, textY, textColor);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) { // 左键点击
                    EntitySelectionScreen.this.setSelectedEntity(this.entityType);
                    return true;
                }
                return false;
            }

            @Override
            public Component getNarration() {
                return Component.translatable("narrator.select", this.displayName);
            }
        }
    }
}
