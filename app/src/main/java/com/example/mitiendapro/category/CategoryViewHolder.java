package com.example.mitiendapro.category;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mitiendapro.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    private TextView textView;
    private CardView cardView;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);

        textView=itemView.findViewById(R.id.category_textView);
        cardView=itemView.findViewById(R.id.category_card);
    }

    public TextView getTextView() {
        return textView;
    }

    public CardView getCardView() {
        return cardView;
    }
}

