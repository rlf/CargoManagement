package com.github.rlf.cargomanagement.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CargoNodeRecipeFactory {
    public static ShapedRecipe createRecipe(JavaPlugin plugin, ConfigurationSection section, CargoNodeFactory nodeFactory) {
        String name = section.getName();
        ItemStack itemStack = nodeFactory.createCargoNode(name);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, name), itemStack);

        List<String> shape = section.getStringList("shape");
        recipe.shape(shape.toArray(new String[0]));

        ConfigurationSection ingredients = section.getConfigurationSection("ingredients");
        if (ingredients != null) {
            for (String key : ingredients.getKeys(false)) {
                String value = ingredients.getString(key);
                Material type = Material.matchMaterial(value);
                recipe.setIngredient(key.charAt(0), type);
            }
        }
        return recipe;
    }
}
