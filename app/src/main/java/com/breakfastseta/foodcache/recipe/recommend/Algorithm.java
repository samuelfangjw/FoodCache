package com.breakfastseta.foodcache.recipe.recommend;

import android.util.Log;

import com.breakfastseta.foodcache.recipe.Ingredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Algorithm {
    private static final String TAG = "Algorithm";

    private static final int INGREDIENT_FACTOR = 172800; // time left (in seconds) / factor, lower values give higher scores
    private static final Double INGREDIENT_WEIGHT = 1.0;
    private static final Double RECIPE_COUNT_WEIGHT = 0.25;
    private static final int RESULT_SIZE = 6;

    public static ArrayList<RecommendSnippet> runAlgorithm(ArrayList<RecommendSnippet> arrRecipes, ArrayList<IngredientSnippet> arrIngredients, Map<String, Long> recipesPrepared) {
        HashMap<String, Double> ingredientScores = calculateIngredientScore(arrIngredients);
        ArrayList<RecommendSnippet> arrResults = calculateRecipeScore(ingredientScores, arrRecipes, recipesPrepared);
        Collections.sort(arrResults, (o1, o2) -> Double.compare(o2.getRecipeScore(), o1.getRecipeScore()));
        return new ArrayList<>(arrResults.subList(0, Math.min(arrResults.size(), RESULT_SIZE)));
    }

    public static HashMap<String, Double> calculateIngredientScore(ArrayList<IngredientSnippet> arrIngredients) {
        HashMap<String, Double> arr = new HashMap<>();
        for (IngredientSnippet snippet : arrIngredients) {
            long timeLeft = snippet.getTimeLeft() / INGREDIENT_FACTOR; // to make values smaller
            double score = 1 / (((double) timeLeft) + 1); // 1/(x + 1)
            arr.put(snippet.getName().toLowerCase(), score);
        }
        return arr;
    }

    public static ArrayList<RecommendSnippet> calculateRecipeScore(HashMap<String, Double> ingredientScores, ArrayList<RecommendSnippet> arrRecipes, Map<String, Long> recipesPrepared) {
        ArrayList<RecommendSnippet> arrResults = new ArrayList<>();
        for (RecommendSnippet snippet : arrRecipes) {
            double priorityScore = 0;

            // calculating ingredient score
            ArrayList<Ingredient> arrIngredients = snippet.getIngredients();
            for (Ingredient i: arrIngredients) {
                String name = i.getName().trim().toLowerCase();
                if (ingredientScores.containsKey(name)) {
                    priorityScore += ingredientScores.get(name) * INGREDIENT_WEIGHT;
                } // else let ingredient priority score be 0
            }

            // calculating recipesPrepared
            String path = snippet.getPath();
            if (recipesPrepared.containsKey(path)) {
                Double count = recipesPrepared.get(path).doubleValue();
                priorityScore -= count * RECIPE_COUNT_WEIGHT;
            }

            snippet.setRecipeScore(priorityScore);
            arrResults.add(snippet);
        }

        // Debugging
        StringBuilder logString = new StringBuilder();
        for (RecommendSnippet snippet : arrResults) {
            logString.append(snippet.toString());
        }
        Log.d(TAG, "calculateRecipeScore: " + logString.toString());

        return arrResults;
    }
}
