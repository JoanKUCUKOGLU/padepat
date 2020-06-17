package com.padepatfood.padepat;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JSONDecoder {
    static public List<Recipe> getRecipesFromJson(Context activityContext) {
        List<Recipe> recipesList = new ArrayList<>();
        JSONArray recipesJson = new JSONArray();

        try {
            File file = new File(activityContext.getFilesDir(), "recettes.json");
            if(!file.exists()) { return null; }

            InputStream is = activityContext.openFileInput("recettes.json");

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            recipesJson = new JSONArray(new String(buffer, "UTF-8"));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < recipesJson.length(); i++) {
            try {
                JSONObject recipeObject = recipesJson.getJSONObject(i);
                List<String> ingredientsList = new ArrayList<>();
                List<String> stepsList = new ArrayList<>();

                for (int j = 0; j < recipeObject.getJSONArray("ingredients").length(); j++) {
                    ingredientsList.add((String) recipeObject.getJSONArray("ingredients").get(j));
                }

                for (int k = 0; k < recipeObject.getJSONArray("steps").length(); k++) {
                    stepsList.add((String) recipeObject.getJSONArray("steps").get(k));
                }

                Recipe recipe = new Recipe(
                        recipeObject.getInt("id"),
                        recipeObject.getString("name"),
                        recipeObject.getInt("price"),
                        ingredientsList,
                        stepsList,
                        recipeObject.getString("img"),
                        recipeObject.getString("type"));
                recipesList.add(recipe);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return recipesList;
    }
}
