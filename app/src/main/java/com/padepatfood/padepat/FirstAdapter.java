package com.padepatfood.padepat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FirstAdapter extends RecyclerView.Adapter<FirstAdapter.ViewHolder>  {
    private List<Recipe> recipes;
    public FirstAdapter(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public FirstAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.second_recycler_view,parent,false);
        return new FirstAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FirstAdapter.ViewHolder holder, int position) {
        //final Recipe recipe = recipes.get(position);
        RecipeAdapter adapter;
        RecyclerView recyclerView;

        adapter = new RecipeAdapter(recipes);//Get a list of questions for the adaptater
        recyclerView = holder.itemView.findViewById(R.id.secondRecyclerView);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(),LinearLayoutManager.HORIZONTAL,false));

        /*holder.recipeName.setText(recipe.getName());
        //holder.recipeImage.setImageResource(recipe.getImgUrl());*/

    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    class ViewHolder extends  RecyclerView.ViewHolder{
        /*final TextView recipeName;
        final ImageView recipeImage;*/

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            /*this.recipeImage =  itemView.findViewById(R.id.recipeImg);
            this.recipeName = itemView.findViewById(R.id.recipeName);*/
        }
    }
}
