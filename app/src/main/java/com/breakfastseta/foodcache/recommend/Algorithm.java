package com.breakfastseta.foodcache.recommend;

import com.breakfastseta.foodcache.recipe.Ingredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Algorithm {
    private static final String TAG = "Algorithm";

    private static final int INGREDIENT_FACTOR = 172800; // time left (in seconds) / factor, lower values give higher scores
    private static final Double INGREDIENT_WEIGHT = 1.0;
    private static final int RESULT_SIZE = 6;

    public static ArrayList<RecommendSnippet> runAlgorithm(ArrayList<RecommendSnippet> arrRecipes, ArrayList<IngredientSnippet> arrIngredients) {
        HashMap<String, Double> ingredientScores = calculateIngredientScore(arrIngredients);
        ArrayList<RecommendSnippet> arrResults = calculateRecipeScore(ingredientScores, arrRecipes);
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

    public static ArrayList<RecommendSnippet> calculateRecipeScore(HashMap<String, Double> ingredientScores, ArrayList<RecommendSnippet> arrRecipes) {
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

            snippet.setRecipeScore(priorityScore);
            arrResults.add(snippet);
        }
        return arrResults;
    }
}
