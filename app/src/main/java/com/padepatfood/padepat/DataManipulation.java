package com.padepatfood.padepat;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class DataManipulation {

    private Context context;
    public DataManipulation(Context context) {
        this.context = context;
    }

    public List<Recipe> getRecipesByType(String type){
        List<Recipe> filteredRecipe = new ArrayList<Recipe>();
        for( Recipe recipe : JsonDecoder.getRecipesFromJson(context)){
            if(recipe.getType().equals(type)){
                filteredRecipe.add(recipe);
            }
        }
        return filteredRecipe;
    }
}
