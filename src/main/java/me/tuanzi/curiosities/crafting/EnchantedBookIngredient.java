package me.tuanzi.curiosities.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import me.tuanzi.curiosities.Curiosities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 自定义魔法书配料类
 * 用于匹配特定附魔的魔法书
 */
public class EnchantedBookIngredient extends AbstractIngredient {
    // 类型ID
    public static final ResourceLocation TYPE = new ResourceLocation(Curiosities.MODID, "enchanted_book_ingredient");
    
    // 所需的附魔
    private final Enchantment enchantment;
    // 所需的附魔等级
    private final int level;
    // 匹配的物品堆栈缓存
    private ItemStack[] matchingStacks = null;

    /**
     * 构造函数
     * 
     * @param enchantment 所需的附魔
     * @param level 所需的附魔等级
     */
    public EnchantedBookIngredient(Enchantment enchantment, int level) {
        this.enchantment = enchantment;
        this.level = level;
    }

    /**
     * 获取匹配此配料的物品堆栈
     */
    @Override
    public ItemStack[] getItems() {
        if (this.matchingStacks == null) {
            ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantedBookItem.addEnchantment(stack, new EnchantmentInstance(enchantment, level));
            this.matchingStacks = new ItemStack[]{stack};
        }
        return this.matchingStacks;
    }

    /**
     * 检查物品堆栈是否匹配此配料
     */
    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty() || stack.getItem() != Items.ENCHANTED_BOOK) {
            return false;
        }

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        return enchantments.containsKey(enchantment) && enchantments.get(enchantment) >= level;
    }

    /**
     * 获取此配料的物品堆栈的JSON表示
     */
    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", TYPE.toString());
        json.addProperty("enchantment", BuiltInRegistries.ENCHANTMENT.getKey(enchantment).toString());
        json.addProperty("level", level);
        return json;
    }

    /**
     * 确定这个配料是否是简单类型
     * 带有NBT的配料不是简单类型
     */
    @Override
    public boolean isSimple() {
        return false;
    }

    /**
     * 获取此配料的序列化器
     */
    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    /**
     * 序列化器内部类
     */
    public static class Serializer implements IIngredientSerializer<EnchantedBookIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        /**
         * 从JSON解析配料
         */
        @Override
        public EnchantedBookIngredient parse(JsonObject json) {
            String enchantmentStr = GsonHelper.getAsString(json, "enchantment");
            ResourceLocation enchantmentId = new ResourceLocation(enchantmentStr);
            
            Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(enchantmentId);
            if (enchantment == null) {
                throw new JsonSyntaxException("无效的附魔: " + enchantmentId);
            }
            
            int level = GsonHelper.getAsInt(json, "level", 1);
            return new EnchantedBookIngredient(enchantment, level);
        }

        /**
         * 从网络读取配料
         */
        @Override
        public EnchantedBookIngredient parse(FriendlyByteBuf buffer) {
            ResourceLocation enchantmentId = buffer.readResourceLocation();
            Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(enchantmentId);
            int level = buffer.readVarInt();
            return new EnchantedBookIngredient(enchantment, level);
        }

        /**
         * 将配料写入网络
         */
        @Override
        public void write(FriendlyByteBuf buffer, EnchantedBookIngredient ingredient) {
            buffer.writeResourceLocation(BuiltInRegistries.ENCHANTMENT.getKey(ingredient.enchantment));
            buffer.writeVarInt(ingredient.level);
        }
    }
} 