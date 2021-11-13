package com.example.mitiendapro.transaction;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mitiendapro.R;
import com.example.mitiendapro.controller.ItemManager;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransactionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionFragment  extends Fragment implements TransactionAdapter.EditTransaction {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    public interface TransactionIsEditing{
        void editingTransaction(Transaction transaction);
        void generateTotalTransaction(ArrayList<Transaction> mTransactions);
    }
    private static TransactionIsEditing editing;


    // TODO: Rename and change types of parameters
    private static   ArrayList<Transaction> mTransactions;
    private String mParam2;
    private RecyclerView recyclerView;
    private ItemManager itemManager;
    private NewTransactionAdapter adapter;
    private static boolean misPurchase;

    public TransactionFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static TransactionFragment newInstance(ArrayList<Transaction> transactions,TransactionIsEditing transactionIsEditing,boolean isPurchase) {
        TransactionFragment fragment = new TransactionFragment();
        mTransactions=transactions;
        misPurchase=isPurchase;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TransactionIsEditing){
            editing= (TransactionIsEditing) context;
            itemManager=new ItemManager(context);
            adapter=new NewTransactionAdapter(context,mTransactions,this);
            editing.generateTotalTransaction(mTransactions);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView=view.findViewById(R.id.transaction_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper= new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override
    public void editTransaction(Transaction transaction) {
        editing.editingTransaction(transaction);
    }

    @Override
    public void transactionSwiped(Transaction transaction) {
        int index=mTransactions.indexOf(transaction);
        itemManager.deleteTransaction(transaction,misPurchase);
        mTransactions.remove(transaction);
        editing.generateTotalTransaction(mTransactions);


    }

    ItemTouchHelper.SimpleCallback simpleCallback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int itemPosition=viewHolder.getAdapterPosition();
            switch (direction){
                case ItemTouchHelper.LEFT:
                case ItemTouchHelper.RIGHT:

                    Transaction deletedTransaction = mTransactions.get(itemPosition);
                    adapter.removeOldTransactionCopy(deletedTransaction);
                    editing.generateTotalTransaction(mTransactions);
                    itemManager.deleteTransaction(deletedTransaction,misPurchase);

                    Snackbar.make(recyclerView,getString(R.string.transaction_deleted), BaseTransientBottomBar.LENGTH_LONG).
                            setAction(getString(R.string.undo),(view)->{
                                itemManager.saveTransaction(deletedTransaction,misPurchase);
                                mTransactions.add(itemPosition,deletedTransaction);
                                editing.generateTotalTransaction(mTransactions);
                                adapter.notifyItemInserted(itemPosition);
                            }).show();
            }
        }
    };

    public NewTransactionAdapter getAdapter (){
        return adapter;
    }}

