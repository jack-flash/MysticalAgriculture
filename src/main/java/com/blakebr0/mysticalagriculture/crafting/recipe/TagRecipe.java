package com.blakebr0.mysticalagriculture.crafting.recipe;

import com.blakebr0.cucumber.crafting.TagMapper;
import com.blakebr0.cucumber.crafting.recipe.ShapedNoMirrorRecipe;
import com.blakebr0.mysticalagriculture.init.ModRecipeSerializers;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class TagRecipe extends ShapedNoMirrorRecipe {
    public TagRecipe(ResourceLocation id, String group, int width, int height, NonNullList<Ingredient> inputs, ItemStack output) {
        super(id, group, width, height, inputs, output);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CRAFTING_TAG;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<TagRecipe> {
        @Override
        public TagRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            var group = GsonHelper.getAsString(json, "group", "");
            var map = ShapedRecipe.keyFromJson(GsonHelper.getAsJsonObject(json, "key"));
            var pattern = ShapedRecipe.shrink(ShapedRecipe.patternFromJson(GsonHelper.getAsJsonArray(json, "pattern")));
            int width = pattern[0].length();
            int height = pattern.length;
            var ingredients = ShapedRecipe.dissolvePattern(pattern, map, width, height);

            var result = GsonHelper.getAsJsonObject(json, "result");
            var tag = GsonHelper.getAsString(result, "tag");
            int count = GsonHelper.getAsInt(result, "count", 1);
            var item = TagMapper.getItemForTag(tag);

            if (item == Items.AIR)
                return null;

            var output = new ItemStack(item, count);

            return new TagRecipe(recipeId, group, width, height, ingredients, output);
        }

        @Override
        public TagRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int width = buffer.readVarInt();
            int height = buffer.readVarInt();
            var group = buffer.readUtf(32767);
            var ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);

            for (int k = 0; k < ingredients.size(); k++) {
                ingredients.set(k, Ingredient.fromNetwork(buffer));
            }

            var output = buffer.readItem();

            return new TagRecipe(recipeId, group, width, height, ingredients, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, TagRecipe recipe) {
            buffer.writeVarInt(recipe.getWidth());
            buffer.writeVarInt(recipe.getHeight());
            buffer.writeUtf(recipe.getGroup());

            for (var ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(recipe.getResultItem());
        }
    }
}
