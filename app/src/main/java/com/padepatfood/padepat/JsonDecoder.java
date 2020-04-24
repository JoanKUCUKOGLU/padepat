package com.padepatfood.padepat;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JsonDecoder {

    static public List<Recipe> getRecipesFromJson(Context activityContext) {
        List<Recipe> recipesList = new ArrayList<>();
        JSONArray recipesJson = new JSONArray();

        try {
            InputStream is = activityContext.getResources().openRawResource(R.raw.recettes);

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

                for (int j = 0; j < recipeObject.getJSONArray("ingredients").length(); j++) {
                    ingredientsList.add((String) recipeObject.getJSONArray("ingredients").get(j));
                }

                List<String> steps = new ArrayList<>();

                for (int j = 0; j < recipeObject.getJSONArray("steps").length(); j++) {
                    steps.add((String) recipeObject.getJSONArray("steps").get(j));
                }

                Recipe recipe = new Recipe(
                        recipeObject.getString("name"),
                        recipeObject.getInt("price"),
                        ingredientsList,
                        steps,
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
