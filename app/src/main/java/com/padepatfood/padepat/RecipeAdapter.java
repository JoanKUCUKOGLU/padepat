package com.padepatfood.padepat;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private List<Recipe> recipes;
    public RecipeAdapter(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.ViewHolder holder, int position) {
        final Recipe recipe = recipes.get(position);

        holder.recipeName.setText(recipe.getName());
        //holder.recipeImage.setImageResource(recipe.getImgUrl());

    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    class ViewHolder extends  RecyclerView.ViewHolder{
        final TextView recipeName;
        final ImageView recipeImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.recipeImage =  itemView.findViewById(R.id.recipeImg);
            this.recipeName = itemView.findViewById(R.id.recipeName);

        }
    }
}
