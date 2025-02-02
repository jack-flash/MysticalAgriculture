package com.blakebr0.mysticalagriculture.registry;

import com.blakebr0.mysticalagriculture.MysticalAgriculture;
import com.blakebr0.mysticalagriculture.api.crop.Crop;
import com.blakebr0.mysticalagriculture.api.crop.CropTier;
import com.blakebr0.mysticalagriculture.api.crop.CropType;
import com.blakebr0.mysticalagriculture.api.lib.PluginConfig;
import com.blakebr0.mysticalagriculture.api.registry.ICropRegistry;
import com.blakebr0.mysticalagriculture.block.MysticalCropBlock;
import com.blakebr0.mysticalagriculture.item.MysticalEssenceItem;
import com.blakebr0.mysticalagriculture.item.MysticalSeedsItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CropRegistry implements ICropRegistry {
    private static final CropRegistry INSTANCE = new CropRegistry();

    private Map<ResourceLocation, Crop> crops = new LinkedHashMap<>();
    private Map<ResourceLocation, CropTier> tiers = new LinkedHashMap<>();
    private Map<ResourceLocation, CropType> types = new LinkedHashMap<>();
    private boolean allowRegistration = false;
    private PluginConfig currentPluginConfig = null;

    @Override
    public void register(Crop crop) {
        if (this.allowRegistration) {
            if (this.crops.values().stream().noneMatch(c -> c.getName().equals(crop.getName()))) {
                this.crops.put(crop.getId(), crop);

                this.loadRecipeConfig(crop);
            } else {
                MysticalAgriculture.LOGGER.info("{} tried to register a duplicate crop with name {}, skipping", crop.getModId(), crop.getName());
            }
        } else {
            MysticalAgriculture.LOGGER.error("{} tried to register crop {} outside of onRegisterCrops, skipping", crop.getModId(), crop.getName());
        }
    }

    @Override
    public void registerTier(CropTier tier) {
        if (!this.tiers.containsKey(tier.getId())) {
            this.tiers.put(tier.getId(), tier);
        } else {
            MysticalAgriculture.LOGGER.info("{} tried to register a duplicate crop tier with id {}, skipping", tier.getModId(), tier.getId());
        }
    }

    @Override
    public void registerType(CropType type) {
        if (!this.types.containsKey(type.getId())) {
            this.types.put(type.getId(), type);
        } else {
            MysticalAgriculture.LOGGER.info("{} tried to register a duplicate crop type with id {}, skipping", type.getModId(), type.getId());
        }
    }

    @Override
    public List<Crop> getCrops() {
        return List.copyOf(this.crops.values());
    }

    @Override
    public Crop getCropById(ResourceLocation id) {
        return this.crops.get(id);
    }

    @Override
    public Crop getCropByName(String name) {
        return this.crops.values().stream().filter(c -> name.equals(c.getName())).findFirst().orElse(null);
    }

    @Override
    public List<CropTier> getTiers() {
        return List.copyOf(this.tiers.values());
    }

    @Override
    public CropTier getTierById(ResourceLocation id) {
        return this.tiers.get(id);
    }

    @Override
    public List<CropType> getTypes() {
        return List.copyOf(this.types.values());
    }

    @Override
    public CropType getTypeById(ResourceLocation id) {
        return this.types.get(id);
    }

    public static CropRegistry getInstance() {
        return INSTANCE;
    }

    public void setAllowRegistration(boolean allowed) {
        this.allowRegistration = allowed;
    }

    public void onRegisterBlocks(IForgeRegistry<Block> registry) {
        PluginRegistry.getInstance().forEach((plugin, config) -> {
            this.currentPluginConfig = config;

            plugin.onRegisterCrops(this);
        });

        var crops = this.crops.values();

        crops.stream().filter(Crop::shouldRegisterCropBlock).forEach(c -> {
            var crop = c.getCropBlock();
            if (crop == null) {
                var defaultCrop = new MysticalCropBlock(c);
                crop = defaultCrop;
                c.setCropBlock(() -> defaultCrop, true);
            }

            if (crop.getRegistryName() == null)
                crop.setRegistryName(c.getNameWithSuffix("crop"));

            registry.register(crop);
        });

        this.crops = getSortedCropsMap(crops);
    }

    public void onRegisterItems(IForgeRegistry<Item> registry) {
        var crops = this.crops.values();

        crops.stream().filter(Crop::shouldRegisterEssenceItem).forEach(c -> {
            var essence = c.getEssenceItem();
            if (essence == null) {
                var defaultEssence = new MysticalEssenceItem(c, p -> p.tab(MysticalAgriculture.CREATIVE_TAB));
                essence = defaultEssence;
                c.setEssenceItem(() -> defaultEssence, true);
            }

            if (essence.getRegistryName() == null)
                essence.setRegistryName(c.getNameWithSuffix("essence"));

            registry.register(essence);
        });

        crops.stream().filter(Crop::shouldRegisterSeedsItem).forEach(c -> {
            var seeds = c.getSeedsItem();
            if (seeds == null) {
                var defaultSeeds = new MysticalSeedsItem(c, p -> p.tab(MysticalAgriculture.CREATIVE_TAB));
                seeds = defaultSeeds;
                c.setSeedsItem(() -> defaultSeeds, true);
            }

            if (seeds.getRegistryName() == null)
                seeds.setRegistryName(c.getNameWithSuffix("seeds"));

            registry.register(seeds);
        });

        PluginRegistry.getInstance().forEach((plugin, config) -> plugin.onPostRegisterCrops(this));

        this.currentPluginConfig = null;
    }

    private void loadRecipeConfig(Crop crop) {
        var recipes = crop.getRecipeConfig();
        var config = this.currentPluginConfig;

        recipes.setSeedCraftingRecipeEnabled(config.isDynamicSeedCraftingRecipesEnabled());
        recipes.setSeedInfusionRecipeEnabled(config.isDynamicSeedInfusionRecipesEnabled());
        recipes.setSeedReprocessorRecipeEnabled(config.isDynamicSeedReprocessorRecipesEnabled());
    }

    private Map<ResourceLocation, Crop> getSortedCropsMap(Collection<Crop> crops) {
        var sorted = new LinkedHashMap<ResourceLocation, Crop>();

        crops.stream().sorted(Comparator.comparingInt(c -> c.getTier().getValue())).forEach(c -> {
            sorted.put(c.getId(), c);
        });

        return sorted;
    }
}
