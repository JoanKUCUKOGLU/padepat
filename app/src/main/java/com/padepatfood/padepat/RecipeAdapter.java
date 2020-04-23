package com.padepatfood.padepat;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

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
    public void onBindViewHolder(@NonNull final RecipeAdapter.ViewHolder holder, int position) {
        final Recipe recipe = recipes.get(position);

        holder.recipeName.setText(recipe.getName());
        Picasso.get().load(recipe.getImg()).fit().centerCrop().error(R.drawable.logopadepat).into(holder.recipeImage);
        Picasso.get().setLoggingEnabled(true);

        holder.recipeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), RecipeActivity.class);
                intent.putExtra("recipe",recipe);
                holder.itemView.getContext().startActivity(intent);
            }
        });
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
