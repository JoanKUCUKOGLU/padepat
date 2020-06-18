package com.padepatfood.padepat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
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
        GlobalData g = GlobalData.getInstance();

        holder.recipeName.setText(recipe.getName());
        holder.setIsRecyclable(false);
        if(g.isConnected()) {
            Picasso.get().load(recipe.getImg()).fit().centerCrop()
                    .error(R.drawable.logopadepat)
                    .into(holder.recipeImage);
            Picasso.get().setLoggingEnabled(true);
        } else {
            File dir = g.getContext().getFilesDir();
            File imgDir = new File(dir, "recipesImages");
            File imgToDisplay = new File(imgDir, "IMG_RECIPE_" + recipe.getId() + ".png");

            holder.recipeImage.setImageDrawable(Drawable.createFromPath(imgToDisplay.getPath()));
        }

        holder.recipeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), RecipeActivity.class);
                intent.putExtra("recipe",recipe);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) holder.itemView.getContext(), holder.recipeImage, ViewCompat.getTransitionName(holder.recipeImage));
                holder.itemView.getContext().startActivity(intent, options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        final TextView recipeName;
        final ImageView recipeImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.recipeImage =  itemView.findViewById(R.id.recipeImg);
            this.recipeName = itemView.findViewById(R.id.recipeName);
        }
    }
}
