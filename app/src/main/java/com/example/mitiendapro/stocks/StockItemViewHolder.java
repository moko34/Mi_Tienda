package com.example.mitiendapro.stocks;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mitiendapro.R;

public class StockItemViewHolder  extends RecyclerView.ViewHolder  {
    private ImageView photo;
    private ImageButton add,minus,edit;
    private TextView stock_quantity,narration;
    private ConstraintLayout constraintLayout;


    public StockItemViewHolder(@NonNull View itemView) {
        super(itemView);
        constraintLayout=itemView.findViewById(R.id.constraint_layout);
        photo=itemView.findViewById(R.id.imageView);
        add=itemView.findViewById(R.id.add_stock);
        minus=itemView.findViewById(R.id.stock_minus);
        stock_quantity=itemView.findViewById(R.id.relative);
        edit=itemView.findViewById(R.id.editQuantity);
        narration=itemView.findViewById(R.id.stock_description);


    }

    public ConstraintLayout getConstraintLayout() {
        return constraintLayout;
    }

    public ImageView getPhoto() {
        return photo;
    }

    public ImageButton getAdd() {
        return add;
    }

    public ImageButton getMinus() {
        return minus;
    }

    public TextView getStock_quantity() {
        return stock_quantity;
    }

    public TextView getNarration() {
        return narration;
    }

    public ImageButton getEdit() {
        return edit;
    }


}


