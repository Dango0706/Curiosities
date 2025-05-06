# 变更日志 (ChangeLog)

## 版本 2025.5 (2025年5月更新)

### 游戏内容更新

#### 状态效果
- **颠颠倒倒效果 (Dizzy)**
  - 使玩家控制方向相反，WASD按键效果反转
  - 所有效果等级都同时反转前后和左右方向
  - 配置文件可启用或禁用该效果
- **天旋地转效果 (Spinning)**
  - 使玩家视角随机旋转，而玩家仍然可以操控视角
  - 效果等级越高，旋转幅度越大
  - 使用余弦和正弦函数计算旋转角度，保持平滑旋转
  - 配置文件可启用或禁用该效果
- 待补充

#### 附魔
- 待补充

#### 物品
- **幸运剑 (Lucky Sword)**
  - 新增特殊武器，每次攻击造成随机伤害(-15~30点可配置)
  - 负数伤害会治疗目标而非造成伤害
  - 伤害会受到攻击冷却恢复程度的影响，需要完全冷却才能发挥最大效果
  - 耐久度为555点
  - 禁止使用增加攻击力的附魔(锋利、亡灵杀手等)，但可以有抢夺、耐久等功能性附魔
  - 名字有随机颜色变化效果
  - 可通过配置文件调整伤害范围和启用/禁用功能
  - 合成配方：绿宝石+兔子脚+金粒+木棍

- **蜜蜂手雷 (Bee Grenade)**
  - 新增特殊投掷物，右键投掷后与方块接触会爆炸
  - 爆炸产生小范围减速效果，持续时间可配置（默认15秒）
  - 释放数量可配置的愤怒蜜蜂（默认5只）攻击周围生物，不会攻击投掷者
  - 蜜蜂存活时间可配置（默认30秒后自动消失）
  - 增强的配置选项：
    - 蜜蜂数量可调整
    - 蜜蜂存活时间可调整（以秒为单位）
    - 蜂蜜减速区域半径可调整（默认2.5方块）
    - 蜂蜜减速区域持续时间可调整（以秒为单位）
    - 设置是否对玩家友好
    - 启用/禁用蜂蜜减速区域
    - 控制是否破坏方块
  - 合成配方：蜂蜜块+火药+蜂蜜块（上排），蜂巢+铁锭+蜂巢（中排），空+线+空（下排）

#### 物品与方块
- **假TNT (Fake TNT)**
  - 全新的方块，外观与普通TNT相似
  - 左键点击会点燃并生成真正的TNT实体
  - 右键点击会直接破坏方块而不爆炸
  - 受到爆炸时不会连锁爆炸，只会掉落物品
  - 合成配方：一圈普通TNT围绕一个拌线勾，可制作8个假TNT
  - 配置文件中可以启用或禁用此功能

#### 界面优化
- **全新配置界面**
  - 实现了两列式布局：左侧为配置分类目录，右侧为具体配置项
  - 优化了配置项标题显示，使标题文字居中显示在上下分割线之间
  - 各配置组之间添加了合理间距，使界面更加清晰
  - 所有配置项按功能分为四类：物品设置、状态效果设置、附魔设置和方块设置
  - 为每个配置选项添加了"恢复默认值"按钮，支持双重确认以防误操作
  - 切换配置类别时自动滚动到顶部，提升用户体验
  - 优化了配置界面的整体视觉效果，使其更加美观易用

---

# Changelog

## Version 2025.5 (May 2025 Update)

### Game Content Updates

#### Status Effects
- **Dizzy**
  - Makes player controls reversed, inverting WASD key effects
  - All effect levels simultaneously invert both forward/backward and left/right directions
  - Can be enabled or disabled in config file
- **Spinning**
  - Makes player camera randomly rotate while still allowing player control
  - Higher effect levels increase rotation amplitude
  - Uses cosine and sine functions for smooth rotation calculations
  - Can be enabled or disabled in config file
- To be added

#### Enchantments
- To be added

#### Items
- **Lucky Sword**
  - New special weapon with random damage output (-15 to 30 points, configurable)
  - Negative damage values heal the target instead of causing damage
  - Damage is affected by attack cooldown recovery - full cooldown needed for maximum effect
  - 555 durability points
  - Cannot receive attack-enhancing enchantments (Sharpness, Smite, etc.) but can have utility enchantments like Looting and Unbreaking
  - Name features random color changes
  - Damage range and enable/disable options available in config file
  - Crafting recipe: Emerald + Rabbit's Foot + Gold Nugget + Stick
  
- **Bee Grenade**
  - New throwable item that explodes upon impact with blocks
  - Explosion causes a configurable area slowness effect (default: 2.5 block radius, 15 seconds duration)
  - Releases configurable number of angry bees (default: 5) that attack nearby entities but not the thrower
  - Bees automatically disappear after configurable time (default: 30 seconds)
  - Enhanced configuration options:
    - Customizable bee count
    - Adjustable bee lifetime (in seconds)
    - Configurable honey slowness area radius (default: 2.5 blocks)
    - Adjustable honey slowness area duration (in seconds)
    - Player friendliness settings
    - Honey slowness area toggle
    - Block damage control
  - Crafting recipe: Honey Blocks + Gunpowder + Honey Blocks (top row), Beehives + Iron Ingot + Beehives (middle row), Empty + String + Empty (bottom row)

#### Items and Blocks
- **Fake TNT**
  - New block that looks similar to regular TNT
  - Left-clicking ignites and spawns a real TNT entity
  - Right-clicking breaks the block without explosion
  - Does not chain explode when caught in explosions, drops as an item instead
  - Crafting recipe: Regular TNT surrounding a tripwire hook, yields 8 Fake TNT blocks
  - Can be enabled or disabled in the config file

#### Interface Improvements
- **Redesigned Configuration Screen**
  - Implemented a two-column layout: categories list on the left, specific configuration items on the right
  - Optimized title display with centered text between divider lines
  - Added appropriate spacing between configuration groups for better clarity
  - Organized all configuration options into four categories: Items, Status Effects, Enchantments, and Blocks
  - Added "Restore Default Value" buttons for each configuration option with double confirmation to prevent accidental resets
  - Auto-scrolls to top when switching configuration categories for improved user experience
  - Enhanced overall visual aesthetics of the configuration interface for better usability

---

## 版本 2025.4 (2025年4月更新)

### 游戏内容更新

#### 状态效果

- **狼群领袖效果 (Wolf Pack Leader)**
  - 使半径30格内的狼/狗会协助攻击持有者锁定的目标
  - 同时会被熊猫敌视
  - 效果持续2分钟(2400刻)
  - 通过食用狼牙土豆获得

- **罪恶效果 (Guilt)**
  - 增加村民交易价格，基础价格提高75%
  - 随效果等级提升增加额外75%的价格
  - 与村庄英雄效果相反
  - 通过道德天平附魔触发

#### 附魔

- **连锁挖掘 (Chain Mining)**
  - 使工具能够连锁挖掘相同类型的方块
  - 最高4级
  - 适用于挖掘类工具
  - 可在附魔台获得

- **超级时运 (Super Fortune)**
  - 效果是原版时运的1.5倍
  - 最高3级
  - 可与精准采集兼容（可同时获得更多掉落物和完整方块）
  - 不能与原版时运共存
  - 可在附魔台获得

- **道德天平 (Moral Balance)**
  - 对和平生物造成伤害时，武器攻击力x2但玩家获得"罪恶"debuff
  - 对敌对生物造成伤害时，攻击力减半但掉落物×3
  - 只能通过合成获得，无法在附魔台获取

#### 物品

- **狼牙土豆 (Wolf Fang Potato)**
  - 食用后获得"狼群领袖"效果
  - 提供8点饥饿值，0.8点饱食度
  - 合成材料：烤土豆×1 + 骨头×2 + 岩浆膏×1

- **火箭靴 (Rocket Boots)**
  - 允许玩家蹲下后蓄力跳跃，实现高跳和缓降效果
  - 长按跳跃键蓄力，松开后起跳
  - 蓄力时间影响跳跃高度
  - 降落时减少75%的摔落伤害
  - 使用燃料系统，可通过煤炭、木炭等补充

- **镰刀 (Scythes)**
  - 修正了所有镰刀的合成配方，将最下方的木棍从最左侧位置移动到中间位置
  - 提供多种材质：木质、石质、铁质、金质、钻石和下界合金
  - 能够批量收获成熟的农作物并自动补种
  - 具有丰收之舞功能，有概率在收获时触发大范围收获效果
  - 可作为近战武器使用，但攻击速度较慢

### 界面更改
- 更改了创造模式标签的图标，从附魔书更改为狼牙土豆
  - 更好地代表了模组的特色物品

---

# Changelog

## Version 2025.4 (April 2025 Update)

### Game Content Updates

#### Status Effects

- **Wolf Pack Leader**
  - Wolves/dogs within a 30-block radius will assist in attacking the player's targeted enemies
  - Makes pandas hostile toward the player
  - Effect lasts for 2 minutes (2400 ticks)
  - Obtained by consuming a Wolf Fang Potato

- **Guilt**
  - Increases villager trade prices by 75% at base level
  - Each amplifier level adds an additional 75% price increase
  - Opposite effect of Hero of the Village
  - Triggered by the Moral Balance enchantment

#### Enchantments

- **Chain Mining**
  - Allows tools to chain-mine blocks of the same type
  - Maximum level 4
  - Applicable to digging tools
  - Can be obtained from an enchanting table

- **Super Fortune**
  - Provides 1.5x the effect of vanilla Fortune
  - Maximum level 3
  - Compatible with Silk Touch (can get both increased drops and intact blocks)
  - Cannot coexist with vanilla Fortune
  - Can be obtained from an enchanting table

- **Moral Balance**
  - When attacking peaceful creatures: 2x weapon damage but player gets the "Guilt" debuff
  - When attacking hostile creatures: half damage but 3x drop multiplier
  - Only obtainable through crafting, not available in enchanting tables

#### Items

- **Wolf Fang Potato**
  - Grants the "Wolf Pack Leader" effect when consumed
  - Provides 8 hunger points and 0.8 saturation
  - Crafting ingredients: 1 Baked Potato + 2 Bones + 1 Magma Cream

- **Rocket Boots**
  - Allows players to crouch and charge jumps for high jumps and slow descent
  - Hold jump key to charge, release to jump
  - Charge time affects jump height
  - Reduces fall damage by 75% when landing
  - Uses a fuel system that can be replenished with coal, charcoal, etc.

- **Scythes**
  - Fixed all scythe crafting recipes by moving the bottom stick from leftmost to center position
  - Available in multiple materials: wooden, stone, iron, golden, diamond, and netherite
  - Can harvest mature crops in bulk and automatically replant
  - Features a Harvest Dance function with a chance to trigger large-area harvesting
  - Can be used as melee weapons, though with slower attack speed

### UI Changes
- Changed the creative mode tab icon from an enchanted book to the Wolf Fang Potato
  - Better represents the mod's featured items 
