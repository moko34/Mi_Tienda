package com.example.mitiendapro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mitiendapro.category.Category;
import com.example.mitiendapro.controller.ItemManager;
import com.example.mitiendapro.dialog.CustomDialog;
import com.example.mitiendapro.transaction.Transaction;
import com.example.mitiendapro.transaction.TransactionAdapter;
import com.example.mitiendapro.transaction.TransactionFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TransactionsActivity extends AppCompatActivity  implements CustomDialog.DialogInterfaceEvent,TransactionFragment.TransactionIsEditing, View.OnClickListener {
    private TextView total,type;
    private FloatingActionButton fab;
    private CustomDialog customDialog;
    private ItemManager itemManager;
    private int totalValue;
    private Transaction transactionToEdit;
    private TransactionAdapter transactionAdapter;
    private Category category;
    private String TRANSACTION_KEY="transaction";
    private String TRANSACTION_KEY_SALES_LAUNCH="transaction_launch";
    private String TRANSACTION_KEY_SALES="transaction_sales";
    private TransactionFragment transactionFragment;
    private ArrayList<Transaction> transactions=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        category= (Category) getIntent().getSerializableExtra(MainActivity.KEY);
        setTitle(category.getName());
        total=findViewById(R.id.total);
        type=findViewById(R.id.txtType);
        fab=findViewById(R.id.add_transaction);
        itemManager=new ItemManager(this);
        // if(type.getText().toString().equals(getString(R.string.purchases))){
        transactions=itemManager.retrieveTransactions(category,true);

        transactionFragment=TransactionFragment.newInstance(transactions,this,true);
        getSupportFragmentManager().beginTransaction().add(R.id.transactionContainer,transactionFragment).commit();
        fab.setOnClickListener(this);

    }
    @Override
    public void onPositiveButtonClicked(int date, int amount, String description) {
        boolean key=type.getText().toString()==getString(R.string.purchases)?true:false;
        Transaction transaction=new Transaction(date,description,amount,category.getName(),category.getYear());
        if(customDialog.getButtonText()==getString(R.string.add)){
           //add new  transaction to the item manager
            itemManager.saveTransaction(transaction,key);
            //add new transaction to the arraylist
            transactionFragment.getAdapter().addTransactionToArrayList(transaction);
            //update the total
            generateTotalTransaction(transactionFragment.getAdapter().getTransactionArrayList());
        }else if(customDialog.getButtonText()==getString(R.string.save)){
            //Edit transaction in the item manager
            itemManager.editTransaction(transactionToEdit,date,description,amount,key, category.getName());
            //Add new copy of the transaction to the arraylist
            transactionFragment.getAdapter().addTransactionToArrayListOnEdit(transaction);
            //update the total value
            generateTotalTransaction(transactionFragment.getAdapter().getTransactionArrayList());
        }

    }

    @Override
    public void editIconIsPressed(EditText date, EditText description, EditText amount) {
        //fill the editing dialog with initial transaction values
        date.setText(String.valueOf(transactionToEdit.getDate()));
        description.setText((transactionToEdit.getDescription()));
        amount.setText(String.valueOf(transactionToEdit.getAmount()));

    }

    @Override
    public void onNegativeButtonClicked() {
        //add back old copy if transaction wasn't edited
        transactionFragment.getAdapter().addTransactionToArrayListOnEdit(transactionToEdit);
        //update the total value
        generateTotalTransaction(transactionFragment.getAdapter().getTransactionArrayList());
    }

    @Override
    public void editingTransaction(Transaction transaction) {
        //Launch editing dialog
        openDialogFragment(getString(R.string.save));
        //store copy of transaction to be edited
        transactionToEdit=transaction;
        //remove copy of transaction to be edited
        transactionFragment.getAdapter().removeOldTransactionCopy(transaction);
    }

    @Override
    public void generateTotalTransaction(ArrayList<Transaction> mTransactions) {
       //compute the total value
        totalValue = getTotalTransactions(mTransactions,totalValue);
        total.setText(String.format(getString(
                R.string.total_values),getString(R.string.transaction_total)
                ,getString(R.string.currency),totalValue));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_transaction:
                //launch dialog to add a transaction
                openDialogFragment(getString(R.string.add));

        }
    }

    public  void openDialogFragment(String operationType){
        //Launch the custom dialog
        customDialog=new CustomDialog(R.layout.dcustom_dialog_view,this,this,operationType);
        customDialog.show(getSupportFragmentManager(),TRANSACTION_KEY);
    }
    public int getTotalTransactions(ArrayList<Transaction> arrayList,int value){
        value=0;
        //compute the total from the arraylist on initial run
        for(int index =0;index<arrayList.size();index++){
            value += arrayList.get(index).getAmount();
        }
        return value;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sales_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sales:
                //launch the sales transaction
                transactions=itemManager.retrieveTransactions(category,false);
                transactionFragment=TransactionFragment.newInstance(transactions,this,false);
                getSupportFragmentManager().beginTransaction().replace(R.id.transactionContainer,transactionFragment).commit();
                type.setText(getString(R.string.sales));
                break;
            case R.id.menu_purchases:
                //launch the expense transactions
                transactions=itemManager.retrieveTransactions(category,true);
                transactionFragment=TransactionFragment.newInstance(transactions,this,true);
                getSupportFragmentManager().beginTransaction().replace(R.id.transactionContainer,transactionFragment).commit();
                type.setText(getString(R.string.purchases));

        }


        return super.onOptionsItemSelected(item);
    }

}