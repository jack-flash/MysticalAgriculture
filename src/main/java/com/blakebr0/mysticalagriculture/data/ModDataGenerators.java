package com.blakebr0.mysticalagriculture.data;

import com.blakebr0.mysticalagriculture.MysticalAgriculture;
import com.blakebr0.mysticalagriculture.data.generator.BlockModelJsonGenerator;
import com.blakebr0.mysticalagriculture.data.generator.BlockTagsJsonGenerator;
import com.blakebr0.mysticalagriculture.data.generator.ItemModelJsonGenerator;
import com.blakebr0.mysticalagriculture.data.generator.ItemTagsJsonGenerator;
import com.blakebr0.mysticalagriculture.data.generator.RecipeJsonGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public final class ModDataGenerators {
    @SubscribeEvent
    public void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(new BlockModelJsonGenerator(generator, MysticalAgriculture.MOD_ID, existingFileHelper));
        generator.addProvider(new ItemModelJsonGenerator(generator, MysticalAgriculture.MOD_ID, existingFileHelper));
        generator.addProvider(new RecipeJsonGenerator(generator));
        generator.addProvider(new BlockTagsJsonGenerator(generator, MysticalAgriculture.MOD_ID, existingFileHelper));
        generator.addProvider(new ItemTagsJsonGenerator(generator, MysticalAgriculture.MOD_ID, existingFileHelper));
    }
}
