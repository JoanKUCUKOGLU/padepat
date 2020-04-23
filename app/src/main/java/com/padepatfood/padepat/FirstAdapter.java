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
    private List<MainActivity.RecipeType> recipeTypes;
    public FirstAdapter(List<MainActivity.RecipeType> recipeTypes) {
        this.recipeTypes = recipeTypes;
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
        final MainActivity.RecipeType recipeType = recipeTypes.get(position);
        RecipeAdapter adapter;
        RecyclerView recyclerView;
        DataManipulation dm = new DataManipulation(holder.itemView.getContext());
        List<Recipe> recipes = dm.getRecipesByType(recipeType.stringType);

        adapter = new RecipeAdapter(recipes);//Get a list of questions for the adaptater
        recyclerView = holder.itemView.findViewById(R.id.secondRecyclerView);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(),LinearLayoutManager.HORIZONTAL,false));

        holder.recipeTypeName.setText(recipeType.stringType);

    }

    @Override
    public int getItemCount() {
        return recipeTypes.size();
    }

    class ViewHolder extends  RecyclerView.ViewHolder{
        final TextView recipeTypeName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.recipeTypeName =  itemView.findViewById(R.id.recipeTypeGroupText);
        }
    }
}
