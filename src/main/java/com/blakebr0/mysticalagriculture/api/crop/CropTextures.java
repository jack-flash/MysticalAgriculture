package com.blakebr0.mysticalagriculture.api.crop;

import net.minecraft.util.ResourceLocation;

/**
 * Helper class used to specify crop texture locations
 */
public class CropTextures {
    private ResourceLocation flowerTexture, essenceTexture, seedTexture;

    /**
     * Setup all crop related textures using a single name
     * @param modid the resource domain
     * @param name the name of this crop
     */
    public CropTextures(String modid, String name) {
        this.flowerTexture = new ResourceLocation(modid, name.concat("_flower"));
        this.essenceTexture = new ResourceLocation(modid, name.concat("_essence"));
        this.seedTexture = new ResourceLocation(modid, name.concat("_seeds"));
    }

    /**
     * Setup all crop related textures using their resource locations
     * @param flowerTexture flower texture location
     * @param essenceTexture essence texture location
     * @param seedTexture seed texture location
     */
    public CropTextures(ResourceLocation flowerTexture, ResourceLocation essenceTexture, ResourceLocation seedTexture) {
        this.flowerTexture = flowerTexture;
        this.essenceTexture = essenceTexture;
        this.seedTexture = seedTexture;
    }

    public ResourceLocation getFlowerTexture() {
        return this.flowerTexture;
    }

    public CropTextures setFlowerTexture(ResourceLocation location) {
        this.flowerTexture = location;
        return this;
    }

    public ResourceLocation getEssenceTexture() {
        return this.essenceTexture;
    }

    public CropTextures setEssenceTexture(ResourceLocation location) {
        this.essenceTexture = location;
        return this;
    }

    public ResourceLocation getSeedTexture() {
        return this.seedTexture;
    }

    public CropTextures setSeedTexture(ResourceLocation location) {
        this.seedTexture = location;
        return this;
    }
}