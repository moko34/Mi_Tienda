package com.example.mitiendapro.transaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mitiendapro.R;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionViewHolder> {
    private ArrayList<Transaction> transactionArrayList;
    private int index;
    public interface EditTransaction{
        void editTransaction(Transaction transaction);
        void transactionSwiped(Transaction transaction);
    }
    private EditTransaction editTransaction;

    public TransactionAdapter(Context context, ArrayList<Transaction> transactionArrayList, EditTransaction editTransaction) {
        this.context = context;
        this.transactionArrayList=transactionArrayList;
        this.editTransaction=editTransaction;
    }

    private Context context;



    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view =layoutInflater.inflate(R.layout.transaction_item,parent,false);


        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        holder.getConstraintLayout().setAnimation(AnimationUtils.loadAnimation(context,R.anim.stock_translate));
        holder.getAmount().setText(String.format("GHS %d",transactionArrayList.get(position).getAmount()));
        holder.getDate().setText(String.format("%d \n %s",transactionArrayList.get(position).getDate(),transactionArrayList.get(position).getMonth().substring(0,3)));
        holder.getDescription().setText(transactionArrayList.get(position).getDescription());
        holder.getImageButton().setOnClickListener(view ->
        {editTransaction.editTransaction(transactionArrayList.get(position));});
        holder.getImgDelete().setOnClickListener((view)->{
            holder.getImgDelete().startAnimation(AnimationUtils.loadAnimation(context,R.anim.delete_translate));
            editTransaction.transactionSwiped(transactionArrayList.get(position));

        });
    }

    @Override
    public int getItemCount() {
        return transactionArrayList.size();
    }
    public  void addTransactionToArrayList (Transaction transaction){
        transactionArrayList.add(transaction);
        notifyItemInserted(transactionArrayList.size()-1);
    }
    public void addTransactionToArrayListOnEdit(Transaction transaction){
        transactionArrayList.add(getIndex(),transaction);
        notifyItemInserted(getIndex());
    }
    public Transaction removeOldTransactionCopy(Transaction transaction){
        int  index = transactionArrayList.indexOf(transaction);
        transactionArrayList.remove(index);
        Toast.makeText(context, index+"", Toast.LENGTH_SHORT).show();
        notifyItemRemoved(index);
        Toast.makeText(context, index+"", Toast.LENGTH_SHORT).show();


        return transaction;
    }

    public int getIndex() {
      return index;
}

    public ArrayList<Transaction> getTransactionArrayList() {
        return transactionArrayList;
    }
}


