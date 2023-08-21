package dev.stormwatch.vegvisir.capabilities;

import dev.stormwatch.vegvisir.environment.Nutrition;
import net.minecraft.nbt.CompoundTag;

import java.util.EnumMap;

public class PlayerNutrition {

    public static PlayerNutrition EMPTY = new PlayerNutrition();

    // full value in a food group lasts 3 days
    private final int MIN_NUTRITION = 0;
    private final int MAX_NUTRITION = 100;
    private final int DEFAULT_NUTRITION = 30;
    private EnumMap<Nutrition.NutritionGroup, Integer> nutritionValues = new EnumMap<>(Nutrition.NutritionGroup.class);

    public int getPlayerNutrition(Nutrition.NutritionGroup group) {
        return this.nutritionValues.getOrDefault(group, DEFAULT_NUTRITION);
    }

    public void increasePlayerNutrition(Nutrition.NutritionGroup group, int amount) {
        int previous = this.nutritionValues.getOrDefault(group, DEFAULT_NUTRITION);
        this.nutritionValues.put(group, Math.min(previous + amount, MAX_NUTRITION));
    }

    public void decreasePlayerNutrition(Nutrition.NutritionGroup group, int amount) {
        int previous = this.nutritionValues.getOrDefault(group, DEFAULT_NUTRITION);
        this.nutritionValues.put(group, Math.max(previous - amount, MIN_NUTRITION));
    }

    public double getTotalNutritionQuotient() {
        int nutritionTotal = 0;
        int nutritionCapacity = 0;
        for (Nutrition.NutritionGroup group : Nutrition.NutritionGroup.values()) {
            nutritionTotal += this.nutritionValues.getOrDefault(group, DEFAULT_NUTRITION);
            nutritionCapacity += MAX_NUTRITION;
        }
        return (double) nutritionTotal / nutritionCapacity;
    }

    public void saveNBT(CompoundTag nbt) {
        for (Nutrition.NutritionGroup group : Nutrition.NutritionGroup.values()) {
            nbt.putInt(group.name(), this.nutritionValues.getOrDefault(group, DEFAULT_NUTRITION));
        }
    }

    public void loadNBT(CompoundTag nbt) {
        for (Nutrition.NutritionGroup group : Nutrition.NutritionGroup.values()) {
            this.nutritionValues.put(group, nbt.getInt(group.name()));
        }
    }

    public void copyFrom(PlayerNutrition playerNutrition) {
        for (Nutrition.NutritionGroup group : Nutrition.NutritionGroup.values()) {
            this.nutritionValues.put(group, playerNutrition.getPlayerNutrition(group));
        }
    }

}
