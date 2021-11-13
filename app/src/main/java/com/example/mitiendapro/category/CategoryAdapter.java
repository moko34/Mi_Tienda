package com.example.mitiendapro.category;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mitiendapro.R;

import java.security.SecureRandom;
import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
    public interface LaunchTransactions{
        void launchTransactionsActivity(Category category);

    }
    private Context context;
    private ArrayList<Category> categoryArrayList;
    private LaunchTransactions launchTransactions;

    public CategoryAdapter(Context context,ArrayList<Category> categoryArrayList,LaunchTransactions launchTransactions) {
        this.context = context;
        this.categoryArrayList=categoryArrayList;
        this.launchTransactions=launchTransactions;
    }
    public CategoryAdapter(){};

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.category_item,parent,false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        SecureRandom secureRandom=new SecureRandom();
        int x =secureRandom.nextInt(256);
        int y =secureRandom.nextInt(256);
        int z =secureRandom.nextInt(256);
        int s =secureRandom.nextInt(256);
        holder.getCardView().setBackgroundColor(Color.argb(x,y,z,s));
        holder.getCardView().setCardElevation(4.0f);
        holder.getTextView().setText(String.format("%s \n  %d",categoryArrayList.get(position).getName(),categoryArrayList.get(position).getYear()));
        holder.getCardView().setOnClickListener(v -> {
            launchTransactions.launchTransactionsActivity(categoryArrayList.get(position));
        });



    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    public ArrayList<Category> getCategoryArrayList() {
        return categoryArrayList;
    }
    public void onAddCategory(Category category){
        categoryArrayList.add(category);
        notifyItemInserted(categoryArrayList.size()-1);
    }
}


