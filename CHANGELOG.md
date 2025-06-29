# 变更日志 (ChangeLog)

## 版本 2025.6 (2025年6月更新)

### 优化

- 优化了debuglog输出模式,新增加开发模式
- 只有当开发模式为True时才会输出debuglog.
- 优化README文件.

### 游戏内容更新

#### 物品

- **玫瑰金工具套装 (Rose Gold Tools)**
  - 新增5种玫瑰金工具：镐子、斧子、铲子、锄头、剑
  - 使用锻造台合成：铜锭 + 对应的金质工具（基础） + 铜锭（升级材料） = 玫瑰金工具
  - 工具属性：耐久度256、铁工具挖掘等级、黄金工具挖掘速度、黄金工具附魔能力
  - 可使用金锭修复
  - 配置选项：
    - `roseGoldToolsEnabled` - 是否启用玫瑰金工具（禁用时无法合成）

- **生物指南针 (Entity Compass)**
  - 新增特殊工具，用于寻找和标记生物
  - 右键使用：在50x50范围内搜索当前选定的生物类型，为找到的生物添加发光效果（仅使用者可见）
  - Shift+右键使用：打开GUI界面选择生物类型，支持所有已注册生物（包括其他模组的生物）
  - 使用后进入5秒物品冷却时间
  - 可在岩浆中漂浮（类似下界合金物品）
  - 合成配方：骷髅头+苦力怕头+龙头（上排），皮革+指南针+羽毛（中排），猪灵头+凋零骷髅头+僵尸头（下排）
  - 配置选项：
    - `enableEntityCompass` - 是否启用此物品（禁用时在tooltip中显示提示信息）
    - `allowEntityCompassCrafting` - 是否允许合成此物品
    - `entityGlowRange` - 生物发光效果的搜索范围（默认50格）

- **概率圣剑 (Probability Holy Sword)**
    - 新增神秘武器，拥有不可预测的特殊能力
    - 基础攻击伤害6点，耐久度300点
    - 只能用铁锭修复，不能附魔（除了诅咒）
    - 20%概率触发以下随机特殊效果之一：
        - "幸运斩"：瞬间击杀低血量生物（僵尸、骷髅等）
        - "厄运突刺"：对使用者造成3点伤害，但对敌人造成20点暴击伤害
        - "时空错乱"：将敌人传送到20格范围内的随机位置
    - 在沙漠神殿隐藏宝箱中以可配置概率生成（无合成配方）
    - 可通过配置文件启用/禁用及调整各项参数

- **控制之杖 (Staff of Control)**
  - 新增强力工具，可以控制生物互相攻击
  - 右键点击第一个生物，再右键点击第二个生物，让它们互相攻击
  - Shift+右键清除第一个选择的生物
  - 拥有300点耐久度，使用下界之星修复
  - 不可以被除诅咒外的任何附魔所影响
  - 合成配方：下界之星+下界合金锭（上排），下界合金块+下界之星（中排），木棍（下排左侧）
  - 可通过配置文件启用/禁用及控制是否可合成

- **因果怀表 (Causal Pocket Watch)**
  - 新增神秘时间物品，可以储存和回溯玩家状态
  - 右键使用：储存玩家当前完整状态（位置、血量、饱食度、氧气、经验值、经验等级、物品栏等所有信息）
  - 再次右键：回溯到储存的状态，消耗一点耐久度，并触发300秒冷却时间
  - 储存的信息仅保存15秒，超时后自动清除
  - 有储存信息时物品显示附魔光泽效果
  - 耐久度25点，可在岩浆中漂浮，使用下界之星修复，不可被附魔
  - 合成配方：铁粒+铁块（上排），钟（中排），下界之星×3（下排）
  - 配置选项：
    - `enableCausalPocketWatch` - 是否启用此物品（禁用时在tooltip中显示提示）
    - `allowCausalPocketWatchCrafting` - 是否可以合成此物品
    - `causalPocketWatchCooldownTime` - 冷却时间（默认300秒）
    - `causalPocketWatchStorageTime` - 信息存储时间（默认15秒）

#### 原版修改

- **玻璃瓶转水瓶 (Glass Bottle to Water Bottle)**
  - 新增功能：当玻璃瓶被投掷到水中时，自动转换为水瓶
  - 支持所有类型的水源（静止水、流动水）
  - 可通过配置文件启用/禁用（默认启用）
  - 在服务器和客户端之间正确同步

#### 附魔

- **钢契 (Steel Covenant)**
  - 新增防护类附魔，可以应用于胸甲
  - 限制玩家受到的最大伤害值，公式为：20-(等级-1)*2.5
  - 最高支持5级附魔，一级限制伤害上限为20点，五级限制伤害上限为10点
  - 默认无法从村民处交易获得
  - 可通过配置文件启用/禁用及控制是否可交易
  - 每当限制伤害时，对盔甲造成3点耐久度损耗

#### 状态效果

- **不死 (Undying)**
  - 新增状态效果，当受到致命伤害时，触发不死图腾的效果，并移除本效果
  - 提供对应的药水和药箭，基础版持续3分钟，长效版持续8分钟
  - 可通过配置文件启用/禁用
  - 酿造配方：粗制的药水 + 不死图腾 -> 不死药水

## 版本 2025.5 (2025年5月更新)

### bug修复:

- 修复config在服务器与客户端不同步的问题
- 修复火箭靴纹理消失bug
- 修复连锁挖矿可以被附魔到工具以外的问题
- 修复镰刀范围不正确的问题
- 修复镰刀在未收割作物时也能触发丰收之舞的问题
- 修复镰刀在未达到Attack Speed时也能触发横扫的问题
- 修复镰刀触发横扫时粒子效果过多的问题
- 修复道德天平附魔无法在铁砧中应用到武器上的问题
- 修复将连锁挖矿绑定到鼠标侧边键（如鼠标按键5）时响应不灵敏的bug

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
- **瓦解之躯效果 (Dissolving Body)**
  - 每等级减少2游戏刻（0.1秒）受击后伤害免疫时间
  - 使生物更容易受到连续伤害
  - 配置文件可启用或禁用该效果
- **富有效果 (Rich)**
  - 使附近的村民跟随玩家，并在头上显示爱心特效
  - 影响范围根据效果等级提升，基础范围为16格，每级增加16格
  - 村民会主动靠近并跟随拥有此效果的玩家
  - 配置文件中可以调整每级效果的影响范围和启用/禁用该效果
- **混乱效果 (Confusion)**
  - 攻击时有等级*15%（可配置），最高100%的概率将攻击目标转移为自己
  - 转移时对自己造成原本伤害的30%*等级（可配置）
  - 配置选项包括是否启用、每级目标转移概率、每级伤害百分比和伤害百分比上限
  - 通过混乱药水获得，基础时长为1分30秒

#### 附魔

- **熟练 (Proficiency)**
  - 提高玩家武器的攻击速度，每级增加15%（可在配置中调整）
  - 最高支持4级附魔
  - 作为超级稀有附魔出现，30级附魔台最高只能附3级
  - 可通过村民交易获得
  - 配置选项包括是否启用以及每级攻速提升百分比

#### 物品

- **富有药水 (Potion of Richness)**
  - 新增药水系列，包括普通版本、长效版本和强效版本
  - 提供富有效果，使村民跟随并显示爱心和绿宝石粒子
  - 普通版本持续3分钟，长效版本持续8分钟，强效版本持续1分30秒但效果更强
  - 支持喷溅型和滞留型变种，以及富有之箭
  - 酿造配方：粗制的药水 + 绿宝石块 -> 富有药水，以及标准的时长和强度转换

- **无限水桶 (Infinite Water Bucket)**
  - 新增特殊水桶，可以无限倒出水
  - 右键点击水源时，会收集3x3范围内的所有水源
  - 与原版水桶使用逻辑相同，但永远不会消耗
  - 合成配方：海晶碎片+海绵围绕一个水桶
  - 可在配置文件中启用或禁用该功能

- **涡毒腺体 (Toxic Gland)**
  - 新增危险食物，由发酵蜘蛛眼与河豚合成
  - 食用后补充4格饱食度与2的饱和度
  - 给予玩家多种负面效果：1分钟中毒IV，1分钟反胃，1分钟虚弱II，15秒失明
  - 这种高浓度毒素在海洋生物体内形成，具有极强的毒性

- **虚空吞噬之剑 (Void Devourer Sword)**
  - 新增强力武器，拥有10点基础攻击伤害和1.6的攻击速度
  - 特殊能力：每次击杀生物时，收集生物最大生命值的10%作为虚空能量（可在配置中调整）
  - 积累100点虚空能量后，右键可释放黑洞效果，吸附15格内的所有生物并造成伤害
  - 黑洞效果会使敌人悬浮、减速，并强制向玩家移动
  - 虚空能量最大储存量为1000点（可在配置中调整）
  - 高度可配置：能量获取百分比、黑洞范围、黑洞伤害值（设为0则使用玩家攻击力）等
  - 所有配置均为通用配置，服务器与客户端共享
  - 合成配方：黑曜石+末地水晶+黑曜石（上排），紫颂果+下界之星+紫颂果（中排），下届合金剑+灵魂沙+下届合金剑（下排）

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

- **时空卷轴 (Scroll of Spacetime)**
    - 由远古时空法师创造的魔法卷轴，表面流转着星云纹路
    - 右键使用可在当前位置创建时空锚点，再次使用可瞬间传送回锚点位置
    - Shift+右键可清除已设置的锚点
    - 传送需要1.5秒的蓄力时间，中断使用会取消传送
    - 传送时会消耗耐久度，默认每次消耗10点
    - 传送有距离限制，默认最大1000格
    - 传送后有冷却时间，默认60秒
    - 耐久度上限为300点
    - 增强功能：
        - 传送不再清除锚点，可重复使用同一个锚点多次传送
        - 当设置了锚点时，卷轴会显示附魔光泽，便于识别
        - 传送过程中会生成多种炫酷的粒子效果，包括玩家周围和目标位置的粒子以及连接两点的粒子线
    - 可通过配置文件调整最大传送距离、冷却时间、耐久消耗等
    - 可设置是否能从高级牧师村民处购买获得
    - 合成配方：末影珍珠+紫颂果+末影珍珠（上排），金锭+下界之星+金锭（中排），烈焰棒+钻石+烈焰棒（下排）

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

## Version 2025.6 (June 2025 Update)

### Game Content Updates

#### Items

- **Rose Gold Tools**
  - Added 5 new rose gold tools: pickaxe, axe, shovel, hoe, and sword
  - Crafted using smithing table: corresponding golden tool + copper ingot = rose gold tool
  - Tool properties: 256 durability, iron tool mining level, gold tool mining speed, gold tool enchantability
  - Can be repaired with gold ingots
  - Configuration options:
    - `roseGoldToolsEnabled` - Whether to enable rose gold tools (disables crafting when disabled)

- **Staff of Control**
    - New powerful tool that can control entities to attack each other
    - Right-click on one entity, then right-click another to make them fight each other
    - Shift+Right-click to clear the first selected entity
    - Has 300 durability points and can be repaired with Nether Stars
    - Cannot receive any enchantments except curses
    - Crafting recipe: Nether Star + Netherite Ingot (top row), Netherite Block + Nether Star (middle row), Stick (
      bottom left)
    - Can be enabled/disabled and craft control via config file

- **Causal Pocket Watch**
  - New mysterious time item that can store and revert player states
  - Right-click to use: Store player's current complete state (position, health, hunger, air, experience, experience
    level, inventory, etc.)
  - Right-click again: Revert to stored state, consume one durability point, and trigger 300-second cooldown
  - Stored information is only preserved for 15 seconds, automatically cleared after timeout
  - Item displays enchantment glint effect when it has stored information
  - 25 durability points, can float in lava, repaired with Nether Stars, cannot be enchanted
  - Crafting recipe: Iron Nugget + Iron Block (top row), Clock (middle row), Nether Star×3 (bottom row)
  - Configuration options:
    - `enableCausalPocketWatch` - Whether to enable this item (shows tooltip when disabled)
    - `allowCausalPocketWatchCrafting` - Whether this item can be crafted
    - `causalPocketWatchCooldownTime` - Cooldown time (default 300 seconds)
    - `causalPocketWatchStorageTime` - Information storage time (default 15 seconds)

#### Enchantments

- **Steel Covenant**
  - New protective enchantment applicable to helmets and chestplates
  - Limits the maximum damage a player can receive using the formula: 20-(level-1)*2.5
  - Supports up to level 5, with level 1 capping damage at 20 and level 5 capping at 10
  - By default cannot be obtained from villager trades
  - Can be enabled/disabled and trade ability controlled via config file
  - Now only applicable to chestplates (no longer works on helmets)
  - Causes 3 points of durability damage to armor whenever damage is limited

#### Status Effects

- **Undying**
  - New status effect that triggers the Totem of Undying effect and removes itself when taking fatal damage
  - Provides corresponding potions and tipped arrows, with base duration of 3 minutes and long version of 8 minutes
  - Can be enabled/disabled via config file
  - Brewing recipe: Awkward Potion + Totem of Undying -> Potion of Undying

#### Items

- **Potion of Richness**
  - New potion series including regular, long, and strong variants
  - Provides Rich effect causing villagers to follow and display heart and emerald particles
  - Regular version lasts 3 minutes, long version lasts 8 minutes, strong version lasts 1:30 but with stronger effect
  - Supports splash and lingering variants, as well as tipped arrows
  - Brewing recipe: Awkward Potion + Emerald Block -> Potion of Richness, plus standard duration and strength
    conversions

- **Infinite Water Bucket**
  - New special bucket that provides an unlimited water source
  - Right-clicking on water collects all water sources in a 3x3 range
  - Works with the same logic as the vanilla bucket, but never gets consumed

- **Toxic Gland**
  - New dangerous food item, crafted from fermented spider eye and pufferfish
  - Restores 4 hunger points and 2 saturation when consumed
  - Applies multiple negative effects: Poison IV for 1 minute, Nausea for 1 minute, Weakness II for 1 minute, and
    Blindness for 15 seconds
  - Special toxin concentrated in marine creatures with extremely potent effects
  - Crafting recipe: Prismarine Shards + Sponges surrounding a Water Bucket
  - Can be disabled in the config file

- **Void Devourer Sword**
  - New powerful weapon with 10 base attack damage and 1.6 attack speed
  - Special ability: Collects 10% of a mob's maximum health as void energy upon killing it (configurable)
  - After accumulating 100 void energy, right-click to release a black hole effect that pulls and damages enemies within
    15 blocks
  - The black hole effect causes enemies to levitate, slow down, and be forcibly pulled toward the player
  - Maximum void energy storage of 1000 points (configurable)
  - Highly customizable: energy gain percentage, black hole range, and black hole damage (set to 0 to use player's
    attack damage)
  - All configurations are common configs shared between server and client
  - Crafting recipe: Obsidian + End Crystal + Obsidian (top row), Chorus Fruit + Nether Star + Chorus Fruit (middle
    row), Netherite Sword + Soul Sand + Netherite Sword (bottom row)

- **Lucky Sword**
    - New special weapon with random damage output (-15 to 30 points, configurable)
    - Negative damage values heal the target instead of causing damage
    - Damage is affected by attack cooldown recovery - full cooldown needed for maximum effect
    - 555 durability points
    - Cannot receive attack-enhancing enchantments (Sharpness, Smite, etc.) but can have utility enchantments like
      Looting and Unbreaking
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
    - Crafting recipe: Honey Blocks + Gunpowder + Honey Blocks (top row), Beehives + Iron Ingot + Beehives (middle row),
      Empty + String + Empty (bottom row)

- **Scroll of Spacetime**
    - Magical scroll created by ancient spacetime wizards, with cosmic nebula patterns on its surface
    - Right-click to create a spacetime anchor at your current position, use again to teleport back
    - Shift+Right-click to clear the set anchor
    - Teleportation requires 1.5 second charging time, interrupting cancels the teleport
    - Uses durability when teleporting, default cost of 10 durability points per use
    - Has teleport distance limit, default maximum of 1000 blocks
    - Teleportation has cooldown time, default 60 seconds
    - Maximum durability of 300 points
    - Enhanced features:
        - Teleportation no longer clears the anchor, allowing repeated use of the same anchor
        - Scrolls with anchors set display enchantment glint for easy identification
        - Teleportation process generates various particle effects around the player, at the destination, and connecting
          the two points
    - Configurable maximum teleport distance, cooldown time, durability cost, etc.
    - Can be set to be purchasable from master-level cleric villagers
    - Crafting recipe: Ender Pearl + Chorus Fruit + Ender Pearl (top row), Gold Ingot + Nether Star + Gold Ingot (middle
      row), Blaze Rod + Diamond + Blaze Rod (bottom row)

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
    - Added "Restore Default Value" buttons for each configuration option with double confirmation to prevent accidental
      resets
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
  - Each amplifier level adds 75% price increase
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
