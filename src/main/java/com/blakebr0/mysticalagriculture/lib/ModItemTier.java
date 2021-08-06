package com.blakebr0.mysticalagriculture.lib;

import com.blakebr0.mysticalagriculture.init.ModItems;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.util.LazyLoadedValue;

import java.util.function.Supplier;

public enum ModItemTier implements Tier {
    INFERIUM(3, 2000, 9.0F, 4.0F, 12, () -> {
        return Ingredient.of(ModItems.INFERIUM_INGOT.get());
    }),
    PRUDENTIUM(3, 2800, 11.0F, 6.0F, 14, () -> {
        return Ingredient.of(ModItems.PRUDENTIUM_INGOT.get());
    }),
    TERTIUM(4, 4000, 14.0F, 9.0F, 16, () -> {
        return Ingredient.of(ModItems.TERTIUM_INGOT.get());
    }),
    IMPERIUM(4, 6000, 19.0F, 13.0F, 18, () -> {
        return Ingredient.of(ModItems.IMPERIUM_INGOT.get());
    }),
    SUPREMIUM(5, -1, 25.0F, 20.0F, 20, () -> {
        return Ingredient.of(ModItems.SUPREMIUM_INGOT.get());
    }),
    SOULIUM(0, 400, 5.0F, 3.0F, 15, () -> {
        return Ingredient.of(ModItems.SOULIUM_INGOT.get());
    });

    private final int harvestLevel;
    private final int maxUses;
    private final float efficiency;
    private final float attackDamage;
    private final int enchantability;
    private final LazyLoadedValue<Ingredient> repairMaterial;

    ModItemTier(int harvestLevel, int maxUses, float efficiency, float attackDamage, int enchantability, Supplier<Ingredient> repairMaterial) {
        this.harvestLevel = harvestLevel;
        this.maxUses = maxUses;
        this.efficiency = efficiency;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairMaterial = new LazyLoadedValue<>(repairMaterial);
    }

    @Override
    public int getUses() {
        return this.maxUses;
    }

    @Override
    public float getSpeed() {
        return this.efficiency;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.attackDamage;
    }

    @Override
    public int getLevel() {
        return this.harvestLevel;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairMaterial.get();
    }
}
