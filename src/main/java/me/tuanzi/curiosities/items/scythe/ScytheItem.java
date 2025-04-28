package me.tuanzi.curiosities.items.scythe;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.Curiosities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.IPlantable;
import org.slf4j.Logger;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * 镰刀工具类
 * 可以收获农作物，有几率触发丰收之舞效果
 */
public class ScytheItem extends SwordItem {
    
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Random RANDOM = new Random();
    private final float attackDamage;
    private final float attackSpeed;
    private final Tier tier;

    /**
     * 构造函数
     * @param tier 工具材质等级
     * @param attackDamage 基础攻击伤害
     * @param attackSpeed 基础攻击速度
     * @param properties 物品属性
     */
    public ScytheItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        // 基础值，实际值会在getAttributeModifiers方法中计算
        super(tier, attackDamage, attackSpeed, properties);
        this.tier = tier;
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        
        // 记录初始化信息
        LOGGER.info("初始化镰刀: 材质={}, 基础攻击力={}, 基础攻速={}", 
            tier, 
            attackDamage,
            attackSpeed);
    }

    /**
     * 右键点击方块时触发
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        // 如果镰刀功能被禁用，则不处理右键操作
        if (!ScytheConfig.isScytheEnabled()) {
            return super.useOn(context);
        }
        
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        
        if (player == null) {
            return InteractionResult.PASS;
        }
        
        // 在客户端直接返回成功，实际逻辑在服务器端执行
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        // 服务器端逻辑
        ServerLevel serverLevel = (ServerLevel) level;
        
        // 获取配置的收获范围
        int harvestRange = ScytheConfig.getHarvestRange();
        int harvestedCount = 0;
        
        // 检查是否触发丰收之舞（先检查，再收获）
        double harvestDanceChance = ScytheConfig.getHarvestDanceChance();
        double randomValue = RANDOM.nextDouble();
        boolean triggerDance = randomValue < harvestDanceChance;
        
        LOGGER.info("尝试触发丰收之舞: 概率={}, 随机值={}, 结果={}", 
            harvestDanceChance, randomValue, triggerDance ? "成功" : "失败");
        
        // 计算范围内的方块
        int actualRange = harvestRange - 1;
        for (int x = -actualRange; x <= actualRange; x++) {
            for (int z = -actualRange; z <= actualRange; z++) {
                BlockPos harvestPos = pos.offset(x, 0, z);
                if (harvestCrop(serverLevel, harvestPos, player)) {
                    harvestedCount++;
                }
            }
        }
        
        // 如果收获了作物，则消耗耐久度
        if (harvestedCount > 0) {
            ItemStack stack = context.getItemInHand();
            // 消耗耐久度，根据收获数量比例计算
            if (!player.getAbilities().instabuild) {
                stack.hurtAndBreak(Math.max(1, harvestedCount / 3), player, (entity) -> 
                    entity.broadcastBreakEvent(context.getHand()));
            }
            
            // 播放音效
            level.playSound(player, player.getX(), player.getY(), player.getZ(), 
                SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
            
            // 触发丰收之舞
            if (triggerDance) {
                LOGGER.info("丰收之舞触发成功!");
                triggerHarvestDance(serverLevel, player, pos);
            }
            
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }
    
    /**
     * 收获单个位置的作物
     * @param level 游戏世界
     * @param pos 方块位置
     * @param player 玩家
     * @return 是否成功收获
     */
    private boolean harvestCrop(ServerLevel level, BlockPos pos, Player player) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        
        // 检查是否为成熟的作物
        if (block instanceof CropBlock cropBlock) {
            if (!cropBlock.isMaxAge(state)) {
                return false;
            }
            
            // 获取作物掉落物
            List<ItemStack> drops = Block.getDrops(state, level, pos, null, player, player.getMainHandItem());
            
            // 将掉落物给予玩家或掉落在地上
            for (ItemStack drop : drops) {
                if (!player.addItem(drop)) {
                    Block.popResource(level, pos, drop);
                }
            }
            
            // 寻找种子类物品用于补种
            ItemStack seedStack = findSeed(drops, player);
            
            // 重置作物状态 (从初始状态开始生长)
            level.setBlock(pos, cropBlock.getStateForAge(0), 2);
            
            // 如果有种子并且不是创造模式，则消耗一个种子
            if (!seedStack.isEmpty() && !player.getAbilities().instabuild) {
                seedStack.shrink(1);
            }
            
            // 生成粒子效果
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, 
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 
                5, 0.5, 0.5, 0.5, 0.0);
            
            return true;
        }
        
        return false;
    }
    
    /**
     * 寻找可用于补种的种子
     * @param drops 作物掉落物
     * @param player 玩家
     * @return 找到的种子物品堆叠
     */
    private ItemStack findSeed(List<ItemStack> drops, Player player) {
        // 标签键，用于识别种子物品
        TagKey<Item> seedsTag = ItemTags.create(new ResourceLocation("curiosities", "seeds"));
        
        // 首先尝试从掉落物中寻找种子
        for (ItemStack drop : drops) {
            Item item = drop.getItem();
            if (item instanceof IPlantable || item.builtInRegistryHolder().is(seedsTag)) {
                // 在玩家物品栏中查找相同物品
                for (ItemStack stack : player.getInventory().items) {
                    if (!stack.isEmpty() && stack.getItem() == item) {
                        return stack;
                    }
                }
            }
        }
        
        // 从玩家物品栏中查找任何种子
        for (ItemStack stack : player.getInventory().items) {
            if (!stack.isEmpty()) {
                Item item = stack.getItem();
                if (item instanceof IPlantable || item.builtInRegistryHolder().is(seedsTag)) {
                    return stack;
                }
            }
        }
        
        return ItemStack.EMPTY;
    }
    
    /**
     * 触发丰收之舞效果
     * @param level 服务器世界
     * @param player 玩家
     * @param center 中心位置
     */
    private void triggerHarvestDance(ServerLevel level, Player player, BlockPos center) {
        // 获取丰收之舞范围
        int range = ScytheConfig.getHarvestDanceRange();
        LOGGER.info("执行丰收之舞: 玩家={}, 中心坐标={}, 范围={}", 
            player.getName().getString(), center, range);
        
        // 播放特殊音效 - 音量更大，音调略高
        level.playSound(null, player.getX(), player.getY(), player.getZ(), 
            SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 2.0F, 1.2F);
        
        // 添加额外的胜利音效
        level.playSound(null, player.getX(), player.getY(), player.getZ(), 
            SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.7F, 1.0F);
        
        // 向玩家发送消息 - 使用金色并添加特效图标
        player.displayClientMessage(
            Component.literal("✨ ")
                .append(Component.translatable("message.curiosities.harvest_dance"))
                .append(Component.literal(" ✨"))
                .withStyle(style -> style.withColor(ChatFormatting.GOLD).withBold(true)), 
            true);
        
        // 在范围内生成大量粒子效果
        int actualRange = range - 1;
        
        // 创建一个向上旋转的粒子螺旋
        for (int i = 0; i < 3; i++) { // 做3次螺旋
            for (int angle = 0; angle < 360; angle += 5) {
                double radians = Math.toRadians(angle);
                double radius = actualRange * 0.8 * (i + 1) / 3.0;
                double x = center.getX() + 0.5 + Math.cos(radians) * radius;
                double z = center.getZ() + 0.5 + Math.sin(radians) * radius;
                
                // 创建螺旋上升的效果
                for (int y = 0; y < 3; y++) {
                    double yPos = center.getY() + y * 0.3 + (angle / 360.0) * 2;
                    
                    // 使用不同的粒子
                    if (angle % 15 == 0) {
                        level.sendParticles(ParticleTypes.END_ROD, 
                            x, yPos, z, 1, 0, 0, 0, 0.02);
                    } else {
                        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, 
                            x, yPos, z, 1, 0, 0, 0, 0);
                    }
                }
            }
        }
        
        int growthCounter = 0;
        
        // 使周围农作物生长多个阶段
        for (int x = -actualRange; x <= actualRange; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -actualRange; z <= actualRange; z++) {
                    BlockPos growPos = center.offset(x, y, z);
                    BlockState state = level.getBlockState(growPos);
                    Block block = state.getBlock();
                    
                    if (block instanceof CropBlock cropBlock && !cropBlock.isMaxAge(state)) {
                        try {
                            // 使用CropBlock的AGE属性
                            IntegerProperty ageProperty = CropBlock.AGE;
                            int currentAge = state.getValue(ageProperty);
                            int maxAge = cropBlock.getMaxAge();
                            
                            // 计算增长的阶段数 - 更多阶段，最少2个，最多4个
                            int growthStages = 2 + RANDOM.nextInt(3); // 2到4个阶段
                            int newAge = Math.min(currentAge + growthStages, maxAge);
                            
                            LOGGER.info("农作物: 位置={}, 类型={}, 当前生长阶段={}, 促进阶段={}, 新阶段={}, 最大阶段={}", 
                                growPos, block.getDescriptionId(), currentAge, growthStages, newAge, maxAge);
                            
                            if (newAge > currentAge) {
                                // 设置为新的生长阶段
                                level.setBlock(growPos, state.setValue(ageProperty, newAge), 3);
                                
                                // 生成更多的粒子效果表示强力生长
                                level.sendParticles(ParticleTypes.COMPOSTER, 
                                    growPos.getX() + 0.5, growPos.getY() + 0.5, growPos.getZ() + 0.5, 
                                    10, 0.4, 0.2, 0.4, 0.0);
                                
                                // 额外添加骨粉效果粒子
                                level.sendParticles(ParticleTypes.HAPPY_VILLAGER, 
                                    growPos.getX() + 0.5, growPos.getY() + 0.8, growPos.getZ() + 0.5, 
                                    3, 0.2, 0.1, 0.2, 0.0);
                                
                                // 为每个成功生长的作物播放单独的声音效果
                                if (growthCounter % 5 == 0) { // 限制声音效果数量
                                    level.playSound(null, growPos, 
                                        SoundEvents.CHORUS_FLOWER_GROW, SoundSource.BLOCKS, 
                                        0.5F, 0.8F + RANDOM.nextFloat() * 0.4F);
                                }
                                
                                growthCounter++;
                            }
                        } catch (Exception e) {
                            LOGGER.error("无法获取作物生长阶段: {}", e.getMessage());
                        }
                    }
                }
            }
        }
        
        LOGGER.info("丰收之舞完成: 共促进了{}个作物生长", growthCounter);
        
        // 在完成时播放最终音效
        if (growthCounter > 0) {
            level.playSound(null, center, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }
    
    /**
     * 处理攻击生物的事件
     */
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // 基础处理逻辑（消耗耐久等）
        stack.hurtAndBreak(1, attacker, (entity) -> entity.broadcastBreakEvent(attacker instanceof Player ? ((Player)attacker).getUsedItemHand() : null));
        return true;
    }
    
    /**
     * 处理挖掘方块的事件
     */
    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        if (state.getDestroySpeed(level, pos) != 0.0F) {
            stack.hurtAndBreak(2, miner, (entity) -> entity.broadcastBreakEvent(miner instanceof Player ? ((Player)miner).getUsedItemHand() : null));
        }
        return true;
    }
    
    /**
     * 检查工具是否适合挖掘指定方块
     */
    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        // 对植物类方块有更高的挖掘速度
        if (state.is(BlockTags.CROPS) || state.is(BlockTags.LEAVES)) {
            return this.tier.getSpeed() * 2.0F;
        }
        return super.getDestroySpeed(stack, state);
    }
    
    /**
     * 处理实体攻击，增加镰刀的横扫范围
     */
    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        // 横扫范围增幅
        float sweepRangeBonus = (float) ScytheConfig.getSweepRangeBonus();
        LOGGER.info("横扫攻击: 配置范围加成={}", sweepRangeBonus);
        
        // 如果有横扫攻击附魔，执行横扫攻击
        int sweepingLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SWEEPING_EDGE, stack);
        if (sweepingLevel > 0 || ScytheConfig.isScytheEnabled()) {
            // 获取范围内的所有生物
            float baseRange = 1.0f;
            float configRange = sweepRangeBonus;
            float totalRange = baseRange + configRange;
            
            LOGGER.info("横扫范围计算: 基础范围={}, 配置加成={}, 总范围={}", 
                baseRange, configRange, totalRange);
            
            Level level = player.level();
            
            // 计算攻击范围 - 使用总范围
            AABB attackBox = entity.getBoundingBox().inflate(totalRange, 0.25, totalRange);
            
            // 获取范围内的生物并执行额外攻击
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, attackBox,
                    e -> e != player && e != entity && !player.isAlliedTo(e) && player.distanceToSqr(e) < totalRange * totalRange * 4);
            
            LOGGER.info("横扫攻击: 找到{}个范围内的目标", entities.size());
            
            if (!entities.isEmpty()) {
                // 执行横扫效果
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(),
                        1.0F, 1.0F);
                
                // 计算实际伤害 - 基于玩家的攻击力和横扫等级
                float damage = 1.0f;
                if (sweepingLevel > 0) {
                    damage = 1.0f + sweepingLevel * 0.5f;
                }
                
                // 对范围内的所有目标施加伤害
                for (LivingEntity target : entities) {
                    // 计算基于距离的伤害衰减
                    float distanceFactor = (float)Math.max(0.0, 1.0 - Math.sqrt(player.distanceToSqr(target)) / (totalRange * 2));
                    float finalDamage = damage * distanceFactor;
                    
                    LOGGER.info("横扫目标: {}, 距离因子={}, 伤害={}", 
                        target.getType().getDescriptionId(), distanceFactor, finalDamage);
                    
                    // 应用伤害
                    target.hurt(target.damageSources().playerAttack(player), finalDamage);
                }
                
                // 生成横扫粒子效果 - 扩大范围以匹配配置
                int particleCount = (int)(Math.PI * totalRange * totalRange * 2);
                LOGGER.info("生成横扫粒子: 数量={}", particleCount);
                
                for(int i = 0; i < particleCount; ++i) {
                    float angle = (float)(i * Math.PI * 2 / particleCount);
                    float distance = (float) (Math.random() * totalRange);
                    float dx = (float)Math.cos(angle) * distance;
                    float dz = (float)Math.sin(angle) * distance;
                    
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                            player.getX() + dx, player.getY() + 0.1f, player.getZ() + dz,
                            1, 0, 0, 0, 0);
                    }
                }
            }
        }
        
        return super.onLeftClickEntity(stack, player, entity);
    }
    
    /**
     * 添加物品信息
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.curiosities.scythe.tooltip.1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.curiosities.scythe.tooltip.2").withStyle(ChatFormatting.GRAY));
        
        // 如果镰刀功能被禁用，添加禁用提示
        if (!ScytheConfig.isScytheEnabled()) {
            tooltip.add(Component.translatable("item.curiosities.disabled").withStyle(ChatFormatting.RED));
        }
    }

    /**
     * 获取攻击伤害
     * @return 攻击伤害值
     */
    @Override
    public float getDamage() {
        float baseDamage = this.tier.getAttackDamageBonus();
        float damageBonus = (float) ScytheConfig.getDamageBonus();
        LOGGER.info("获取攻击伤害: 基础伤害 = {}, 加成 = {}, 总伤害 = {}", baseDamage, damageBonus, baseDamage + damageBonus);
        return baseDamage + damageBonus;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();
        
        if (slot == EquipmentSlot.MAINHAND) {
            // 动态获取最新的伤害加成值
            float damageBonus = (float) ScytheConfig.getDamageBonus();
            float attackSpeed = (float) ScytheConfig.getAttackSpeed();
            
            // 基础攻击力 + 材质加成 + 配置加成
            float totalDamage = 3 + tier.getAttackDamageBonus() + damageBonus;
            
            // Minecraft基础攻击速度是-4.0，配置中的攻击速度是正值，表示比基础速度快多少
            float totalSpeed = -4.0f + attackSpeed;
            
            LOGGER.info("计算镰刀属性: 材质={}, 基础伤害=3, 材质加成={}, 配置加成={}, 总伤害={}, 配置攻速={}, 最终攻速={}", 
                tier, tier.getAttackDamageBonus(), damageBonus, totalDamage, attackSpeed, totalSpeed);
            
            multimap.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, 
                "Weapon modifier", (double)totalDamage, AttributeModifier.Operation.ADDITION));
            multimap.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, 
                "Weapon modifier", (double)totalSpeed, AttributeModifier.Operation.ADDITION));
        }
        
        return multimap;
    }
} 