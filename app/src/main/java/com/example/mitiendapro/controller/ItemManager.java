package com.example.mitiendapro.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.mitiendapro.category.Category;
import com.example.mitiendapro.transaction.Transaction;

import java.util.ArrayList;
import java.util.Map;

public class ItemManager {
    private Context context;
    private SharedPreferences sharedPreferences;

    public ItemManager(Context context) {
        this.context = context;
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void addCategory (Category category){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        String id = category.getName() + "\n" + "ctxgytzxvt20r" + "\n" + category.getYear();
        editor.putString(id, category.getName());
        editor.apply();
    }

    public void deleteCategory(Category category){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String categoryKey= category.getName() + "\n" + "ctxgytzxvt20r"+ "\n" + category.getYear();
        editor.remove(categoryKey);
        editor.commit();
    }

    public ArrayList<Category> retrieveCategories(){
        Map<String , ?> categoryMap= sharedPreferences.getAll();
        ArrayList<Category> categories=new ArrayList<>();
        for (Map.Entry<String,?> category:categoryMap.entrySet()){
            if (category.getKey().contains("ctxgytzxvt20r")){
                String[] strings= category.getKey().split("\n",3);
                Category categoryObj=new Category((String) (category.getValue()),Integer.parseInt(strings[2]));

                categories.add(categoryObj);
            }

        }
        return categories;
    }


    public void editTransaction(Transaction  transaction, int date,String description,float amount,boolean isPurchase,String month){
        Transaction editedTransaction;
        boolean transactionType;
        String key;
        if(isPurchase){
            key = transaction.getDate() + "\n" + "trxtnPur" + "\n" +  transaction.getMonth() + "\n" + transaction.getYear() + "\n" + transaction.getAmount();
            transactionType=true;
        }
        else {
            key =  transaction.getDate() + "\n" + "trxtnSls" + "\n" + transaction.getMonth() + "\n" + transaction.getYear() +  "\n" + transaction.getAmount();
            transactionType=false;
        }
        String transactionToEdit=sharedPreferences.getString(key,transaction.getDescription());
        if (String.valueOf(date).equals("")&& description.equals("")&& String.valueOf(amount).equals("")){
            editedTransaction=new Transaction(transaction.getDate(),transaction.getDescription(),transaction.getAmount(),transaction.getMonth(),transaction.getYear());
        }else{
            editedTransaction=new Transaction(date,description,amount,month,transaction.getYear());
        }
        //remove old copy
        deleteTransaction(transaction,transactionType);
        saveTransaction(editedTransaction,transactionType);
    }

    public  void saveTransaction(Transaction transaction,boolean isPurchases){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        String transactionKey;
        if (isPurchases==true){
            transactionKey = transaction.getDate() + "\n" + "trxtnPur" + "\n" +  transaction.getMonth() + "\n" + transaction.getYear() + "\n" + transaction.getAmount();

        }else{
            transactionKey = transaction.getDate() + "\n" + "trxtnSls" + "\n" + transaction.getMonth() + "\n" + transaction.getYear() +  "\n" + transaction.getAmount();
        }
        editor.putString(transactionKey,transaction.getDescription());
        editor.commit();

    }

    public  void deleteTransaction(Transaction transaction,boolean isPurchase){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String transactionDeleteKey;
        if(isPurchase){
            transactionDeleteKey=transaction.getDate() + "\n" + "trxtnPur" + "\n" +  transaction.getMonth() + "\n" + transaction.getYear() + "\n" + transaction.getAmount();
        }else {
            transactionDeleteKey= transaction.getDate() + "\n" + "trxtnSls" + "\n" +  transaction.getMonth() + "\n" + transaction.getYear() +  "\n" + transaction.getAmount();
        }
        editor.remove(transactionDeleteKey);
        editor.commit();
    }
    public ArrayList<Transaction> retrieveTransactions(Category category,boolean isPurchase){
        ArrayList<Transaction> myTransactions=new ArrayList<>();
        String keyType=isPurchase==true?"trxtnPur":"trxtnSls";
        Map<String, ?> transactions= sharedPreferences.getAll();
        for(Map.Entry<String,?> transaction:transactions.entrySet()){
            String[] strings=transaction.getKey().split("\n");
            if(transaction.getKey().contains(keyType + "\n" + category.getName() + "\n" + category.getYear() )){
                myTransactions.add(new Transaction(Integer.parseInt(strings[0]), (String) transaction.getValue(),Float.parseFloat(strings[4]),strings[2],Integer.parseInt(strings[3])));
            }
        }
        return myTransactions;
    }
}


