package com.example.mitiendapro.transaction;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mitiendapro.R;

public class TransactionViewHolder extends RecyclerView.ViewHolder {
    private TextView date,amount,description;
    private ImageButton imageButton,imgDelete;
    private ConstraintLayout constraintLayout;
    public TransactionViewHolder(@NonNull View itemView) {
        super(itemView);
        
        constraintLayout=itemView.findViewById(R.id.transaction_constraint);
        amount=itemView.findViewById(R.id.txt_transaction_amount);
        date=itemView.findViewById(R.id.txt_transaction_date);
        description=itemView.findViewById(R.id.txt_transaction_description);
        imageButton=itemView.findViewById(R.id.btnEdit);
    }

    public ConstraintLayout getConstraintLayout() {
        return constraintLayout;
    }

    public ImageButton getImgDelete() {
        return imgDelete;
    }

    public TextView getDate() {
        return date;
    }

    public TextView getAmount() {
        return amount;
    }

    public TextView getDescription() {
        return description;
    }

    public ImageButton getImageButton() {
        return imageButton;
    }
}


