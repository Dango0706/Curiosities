package me.tuanzi.curiosities.items.control_staff;

import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.util.DebugLogger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 控制之杖物品
 * 能够使两个生物相互攻击
 */
public class ControlStaffItem extends Item {
    // 使用耐久度
    private static final int MAX_DURABILITY = 300;
    // 黑名单附魔是否允许
    private static final boolean ALLOW_CURSE_ENCHANTMENT = true;
    // 搜索范围（单位：方块）
    private static final double SEARCH_RANGE = 64.0D;
    // 日志
    private static final Logger LOGGER = LoggerFactory.getLogger(ControlStaffItem.class);
    // NBT标签常量
    private static final String TARGET_UUID_TAG = "TargetEntityUUID";

    public ControlStaffItem() {
        super(new Item.Properties()
                .durability(MAX_DURABILITY) // 设置耐久度为300
                .fireResistant() // 防火
                .rarity(net.minecraft.world.item.Rarity.RARE) // 设置为稀有物品
        );
    }

    /**
     * 当玩家右键点击实体时触发
     */
    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        // 检查是否启用了此物品
        if (!ModConfigManager.CONTROL_STAFF_ENABLED.get()) {
            return InteractionResult.PASS;
        }

        Level level = player.level();

        // 客户端不处理逻辑，只返回成功
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        // 获取并使用正确的物品实例
        ItemStack currentStack = player.getItemInHand(hand);

        // 服务端调试日志
        boolean hasTarget = hasTargetEntity(currentStack);
        DebugLogger.debugInfo("当前物品状态：NBT: {}, 是否有目标: {}", currentStack.getTag(), hasTarget);

        // 如果玩家按住Shift键，清除第一个选中的实体
        if (player.isShiftKeyDown()) {
            boolean hadTarget = hasTargetEntity(currentStack);
            clearTargetEntity(currentStack);
            player.displayClientMessage(Component.translatable("message.curiosities.control_staff.target_cleared"), true);
            if (hadTarget) {
                DebugLogger.debugInfo("玩家 {} 清除了目标选择", player.getName().getString());
            }
            return InteractionResult.SUCCESS;
        }

        // 检查是否已经选择了第一个实体
        if (!hasTargetEntity(currentStack)) {
            // 设置第一个目标实体
            setTargetEntity(currentStack, target);

            // 检查设置后是否正确保存
            UUID savedId = getTargetEntityUUID(currentStack);

            player.displayClientMessage(Component.translatable("message.curiosities.control_staff.target_selected"), true);
            DebugLogger.debugInfo("玩家 {} 选择了第一个目标实体: {}, UUID: {}, 成功保存: {}",
                    player.getName().getString(),
                    target.getName().getString(),
                    target.getUUID(),
                    (savedId != null && savedId.equals(target.getUUID())));

            // 显示物品NBT数据
            DebugLogger.debugInfo("物品NBT数据: {}", currentStack.getTag());
        } else {
            // 获取第一个目标实体的UUID
            UUID targetId = getTargetEntityUUID(currentStack);

            if (targetId == null) {
                LOGGER.error("从物品栈中获取的目标实体UUID为null，当前NBT: {}", currentStack.getTag());
                clearTargetEntity(currentStack);
                setTargetEntity(currentStack, target);
                player.displayClientMessage(Component.translatable("message.curiosities.control_staff.target_error"), true);
                return InteractionResult.SUCCESS;
            }

            // 记录一些调试信息
            DebugLogger.debugInfo("正在查找目标实体UUID: {}, 当前实体: {}, 当前NBT: {}",
                    targetId, target.getName().getString(), currentStack.getTag());

            // 先检查第二个目标不是自身
            if (target.getUUID().equals(targetId)) {
                player.displayClientMessage(Component.translatable("message.curiosities.control_staff.same_target"), true);
                return InteractionResult.SUCCESS;
            }

            // 扩大搜索范围以确保能找到第一个实体
            boolean foundTarget = false;

            // 先尝试通过UUID直接从世界获取实体
            for (Entity entity : level.getEntities(player, player.getBoundingBox().inflate(SEARCH_RANGE))) {
                if (entity.getUUID().equals(targetId) && entity instanceof LivingEntity livingEntity) {
                    foundTarget = true;

                    // 让两个实体互相攻击
                    forceAttack(target, livingEntity);
                    forceAttack(livingEntity, target);

                    // 减少耐久度
                    if (!player.getAbilities().instabuild) {
                        currentStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                    }

                    player.displayClientMessage(Component.translatable("message.curiosities.control_staff.entities_controlled"), true);
                    DebugLogger.debugInfo("成功让实体 {} 和 {} 互相攻击", livingEntity.getName().getString(), target.getName().getString());

                    // 如果成功让实体互相攻击，清除第一个选中的实体
                    clearTargetEntity(currentStack);
                    DebugLogger.debugInfo("清除目标选择, 当前NBT: {}", currentStack.getTag());
                    break;
                }
            }

            // 如果没有找到第一个实体
            if (!foundTarget) {
                DebugLogger.debugWarn("未能找到UUID为 {} 的目标实体，重置选择", targetId);
                player.displayClientMessage(Component.translatable("message.curiosities.control_staff.target_not_found"), true);

                // 清除并重新选择
                clearTargetEntity(currentStack);
                setTargetEntity(currentStack, target);

                // 再次检查保存是否成功
                UUID savedId = getTargetEntityUUID(currentStack);
                DebugLogger.debugInfo("已重新选择目标: {}, UUID: {}, 保存成功: {}, NBT: {}",
                        target.getName().getString(),
                        target.getUUID(),
                        (savedId != null && savedId.equals(target.getUUID())),
                        currentStack.getTag());
            }
        }

        return InteractionResult.SUCCESS;
    }

    /**
     * 强制一个实体攻击另一个实体
     */
    private void forceAttack(LivingEntity attacker, LivingEntity target) {
        // 设置上一次受伤来源
        attacker.setLastHurtByMob(target);

        // 如果是Mob类型，直接设置攻击目标
        if (attacker instanceof Mob mob) {
            // 设置为积极状态
            mob.setAggressive(true);
            // 设置目标
            mob.setTarget(target);
            // 确保仇恨值足够高
            mob.setLastHurtByMob(target);

            // 记录调试信息
            DebugLogger.debugInfo("将Mob {} 的攻击目标设置为 {}", mob.getName().getString(), target.getName().getString());
        }
    }

    /**
     * 检查物品是否可以被附魔
     * 只允许诅咒类附魔
     */
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        // 只允许诅咒类附魔
        return ALLOW_CURSE_ENCHANTMENT && enchantment.isCurse();
    }

    /**
     * 是否可以被附魔台附魔
     */
    @Override
    public int getEnchantmentValue() {
        return 0; // 不能在附魔台上附魔
    }

    /**
     * 检查物品是否可以接受特定附魔
     */
    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true; // 可以附魔，但在其他方法中限制了只能接受诅咒附魔
    }

    /**
     * 检查物品是否适用于特定附魔类别
     */
    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, ItemStack repair) {
        // 用下界之星修复
        return repair.getItem() == net.minecraft.world.item.Items.NETHER_STAR;
    }

    /**
     * 添加物品描述信息
     */
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (!ModConfigManager.CONTROL_STAFF_ENABLED.get()) {
            tooltip.add(Component.translatable("item.curiosities.disabled"));
            return;
        }

        tooltip.add(Component.translatable("item.curiosities.control_staff.tooltip.1"));
        tooltip.add(Component.translatable("item.curiosities.control_staff.tooltip.2"));
        tooltip.add(Component.translatable("item.curiosities.control_staff.tooltip.3"));

        // 如果已经选择了一个实体，显示提示
        if (hasTargetEntity(stack)) {
            tooltip.add(Component.translatable("item.curiosities.control_staff.tooltip.target_selected"));

            // 在创造模式下显示更多信息
            if (flag.isAdvanced()) {
                UUID targetId = getTargetEntityUUID(stack);
                if (targetId != null) {
                    tooltip.add(Component.literal("§7UUID: " + targetId));
                }
            }
        }
    }

    // 用于存储目标实体的NBT数据
    private void setTargetEntity(ItemStack stack, LivingEntity entity) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putUUID(TARGET_UUID_TAG, entity.getUUID());
        // 确保数据写入
        stack.setTag(tag);

        // 添加额外日志验证
        if (!entity.level().isClientSide) {
            CompoundTag checkTag = stack.getTag();
            DebugLogger.debugInfo("设置目标后立即验证NBT: {}, 是否包含目标: {}",
                    checkTag,
                    checkTag != null && checkTag.contains(TARGET_UUID_TAG, 11));
        }
    }

    private boolean hasTargetEntity(ItemStack stack) {
        return hasTargetEntity(stack, false);
    }

    private boolean hasTargetEntity(ItemStack stack, Boolean output) {
        CompoundTag tag = stack.getTag();
        // 使用contains方法替代hasUUID方法进行更可靠的检查
        boolean hasTarget = tag != null && tag.contains(TARGET_UUID_TAG, 11); // 11是CompoundTag.TAG_INT_ARRAY的类型ID

        // 添加详细日志，避免使用getEntityRepresentation()
        if (tag != null) {
            if (output) {
                DebugLogger.debugDetail("检查目标: NBT={}, 包含目标标签={}", tag, hasTarget);

            }
        }

        return hasTarget;
    }

    private UUID getTargetEntityUUID(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TARGET_UUID_TAG, 11)) {
            try {
                return tag.getUUID(TARGET_UUID_TAG);
            } catch (Exception e) {
                LOGGER.error("获取UUID时发生错误: {}", e.getMessage());
                return null;
            }
        }
        return null;
    }

    private void clearTargetEntity(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            tag.remove(TARGET_UUID_TAG);
            // 确保数据写入
            stack.setTag(tag);

            // 添加验证日志，避免使用getEntityRepresentation
            CompoundTag checkTag = stack.getTag();
            DebugLogger.debugInfo("清除目标后验证NBT: {}", checkTag);
        }
    }
}
