package com.example.mitiendapro;

import android.Manifest;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.mitiendapro.category.Category;
import com.example.mitiendapro.category.CategoryAdapter;
import com.example.mitiendapro.category.CategoryFragment;
import com.example.mitiendapro.controller.ItemManager;
import com.example.mitiendapro.controller.StockManager;
import com.example.mitiendapro.dialog.AddStockDialog;
import com.example.mitiendapro.stocks.StockFragment;
import com.example.mitiendapro.stocks.StockItem;
import com.example.mitiendapro.stocks.StockItemId;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.view.View;

import androidx.appcompat.view.menu.MenuItemWrapperICS;
import androidx.appcompat.view.menu.MenuWrapperICS;
import androidx.core.app.ActivityCompat;



import com.example.mitiendapro.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity  extends AppCompatActivity implements CategoryFragment.TransactionFragmentReady,View.OnClickListener,StockFragment.DisplayImage,AddStockDialog.LoadImage{
    private StockFragment stockFragment;
    private ActivityMainBinding binding;
    private static final String STRING_KEY="string_key";
    private static final String STRING_KEY_LAUNCH="string_key_launch";
    private CategoryFragment fragment;
    private StockManager stockManager;
    private StockItem stockItem_display,stockItemOldCopyForStoreItemIdEdits;
    private ItemManager itemManager;
    private ArrayList<StockItem> arrayList=new ArrayList<>();
    private int newValue;
    private static ProgressBar progressBar;
    private static LinearLayout progress_layout;
    private static TextView progress_text,error_txt;
    private MenuItem menuItem;
    private ImageView myImageView;
    public static final String KEY="key";
    public static final String STOCK_KEY="stock_key";
    public boolean storeIsActive,run,goBack,fragmentNull;
    public int REQUEST_CODE=100;



    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.fab.setOnClickListener((view)->{});
        fragment=CategoryFragment.newInstance();
        stockManager=new StockManager(this);
        arrayList=matchStockItemToStockIds(stockManager.retrieveStockItemsFromMediaStore(),stockManager.retrieveStockItemId());
        fragmentNull=true;
        itemManager=new ItemManager(this);
        reQuestExternalStoragePermission();
        storeIsActive=false;
        run=false;
        error_txt=findViewById(R.id.error_text);
        progressBar=findViewById(R.id.progress_bar);
        progress_layout=findViewById(R.id.progress_circular);
        progress_text=findViewById(R.id.progress_text);
        changeProgressBarColor(progressBar);
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer,fragment).commit();
        binding.fab.setOnClickListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuItem= menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getString(R.string.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
             return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(fragment==null) {
                    ArrayList<StockItem> stockItems = searchStockItem(arrayList, newText);
                    if (stockItems.size() == 0) {
                        toggleLayoutVisibility(View.VISIBLE, error_txt);
                        stockItems=new ArrayList<>();
                        StockFragment stockFragment = StockFragment.newInstance(MainActivity.this, stockItems);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,stockFragment).commit();
                    } else {
                        toggleLayoutVisibility(View.GONE, error_txt);
                        StockFragment stockFragment = StockFragment.newInstance(MainActivity.this, stockItems);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, stockFragment).commit();

                    }}
                    return true;
                }});

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.store_menu){
            if(!storeIsActive){
            fragment=null;

            stockFragment = StockFragment.newInstance(this,arrayList);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,stockFragment).addToBackStack(STOCK_KEY).commit();

            setTitle("Store");
            dismissProgress();
            menuItem.setVisible(true);
            storeIsActive=true;}

        }if(item.getItemId()==R.id.app_bar_search){
            if(fragment!=null){
            }
        }


        return super.onOptionsItemSelected(item);

        }






    @Override
    public void onCategoryItemClicked(Category category) {
        Intent intent = new Intent(MainActivity.this,TransactionsActivity.class);
        intent.putExtra(KEY,category);
        startActivity(intent);
    }
    // request fro permission to use external storage to save media
    public void reQuestExternalStoragePermission(){
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode ==REQUEST_CODE){
            if (grantResults.length>0){

            }else{
                reQuestExternalStoragePermission();
            }
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:
                if(fragment!=null){
                    displayDialog();
                }else {
                    storeIsActive=true;
                    AddStockDialog addStockDialog =new AddStockDialog(this,this,true,true);
                    addStockDialog.show(getSupportFragmentManager(),STRING_KEY);
                }



        }

    }
    public  void displayDialog(){
        EditText editText =new EditText(this);
        editText.setHint(getString(R.string.edt_message));
        AlertDialog.Builder alertBuilder=new AlertDialog.Builder(this)
                .setTitle(getString(R.string.add_category))
                .setView(editText).setPositiveButton(getString(R.string.add), (dialog, which) -> {
                    //If name chosen is key return
                    if (editText.getText().toString().equals("ctxgytzxvt20r")){
                        showToast(getString(R.string.valid));
                        return;
                    }else if(!editText.getText().toString().equals("")){
                        Calendar calendar=Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        Category category=new Category(editText.getText().toString(),year);
                        itemManager.addCategory(category);
                        CategoryAdapter categoryAdapter= (CategoryAdapter) fragment.getRecyclerView().getAdapter();
                        categoryAdapter.onAddCategory(category);}
                    else{
                        dialog.dismiss();
                    }
                });
        alertBuilder.create();
        alertBuilder.show();
    }

    @Override
    public void displayImage(StockItem stockItem) {
        stockItem_display = stockItem;
        AddStockDialog addStockDialog=new AddStockDialog(MainActivity.this,this,false,false);
        addStockDialog.show(getSupportFragmentManager(),STRING_KEY_LAUNCH);

    }
    public void showToast(String message){
        Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void increaseQuantity(StockItem stockItem, TextView textView) {
        showToast(getString(R.string.effecting_changes));
        StockItem newStockItem=stockItem;

        stockItem.setQuantity(stockItem.getQuantity()+1);

        stockManager.UpdateStockItemInMediaStore(stockItem,"",stockItem.getQuantity());
        stockFragment.getStockItemAdapter().modifyStockItem(newStockItem,stockItem,false);
        dismissProgress();
    }




    @Override
    public void reduceQuantity(StockItem stockItem) {
        if(stockItem.getQuantity()>0){
            showToast(getString(R.string.effecting_changes));
            StockItem newStockItem=stockItem;
            stockItem.setQuantity(stockItem.getQuantity()-1);
            stockManager.UpdateStockItemInMediaStore(stockItem,"",stockItem.getQuantity());
            stockFragment.getStockItemAdapter().modifyStockItem(newStockItem,stockItem,false);
            dismissProgress();
        }
    }



    @Override
    public void setQuantityTo(StockItem stockItem) {
        AlertDialog.Builder alert=new AlertDialog.Builder(MainActivity.this);
        EditText editText=new EditText(this);
        StockItem newStockItem=stockItem;
        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        editText.setHint(getString(R.string.change_quantity));
        alert.setView(editText);
        alert.setPositiveButton(getString(R.string.save),(dialog,which)->{
            if(editText.getText().toString().equals("0")){
                //set to past value if value is 0
                newValue=stockItem.getQuantity();
            }else{
                // if value is not equal to 0
                newValue=Integer.parseInt(editText.getText().toString());
            }
            stockItem.setQuantity(newValue);
            //update media store
            showProgress(getString(R.string.saving_changes));
            stockManager.UpdateStockItemInMediaStore(stockItem,"",newStockItem.getQuantity());
            stockFragment.getStockItemAdapter().modifyStockItem(newStockItem,stockItem,false);
            dismissProgress();
            dialog.dismiss();
        });
        alert.setNegativeButton(getString(R.string.cancel),((dialog, which) -> dialog.dismiss()));
        alert.create();
        alert.show();

    }


    @Override
    public void showIntent() {
        run=true;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);
    }

    @Override
    public void addStockItem(StockItem stockItem)  {
        showProgress(getString(R.string.adding_item));
        stockFragment.getStockItemAdapter().addStockItem(stockItem);
        dismissProgress();
    }

    @Override
    //image is not being saved just displayed so the last argument takes false
    public void displayStockImage( ImageView imageView) {
        //The image uri of the current stock item is being updated
        imageView.setImageURI(stockItem_display.getUri());
        myImageView=imageView;
    }



    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void saveReplacedImage(Uri imageUri) throws IOException {
        // remove old copy of stockItem

        stockManager.deleteStockItemFromMediaStore(stockItem_display);
        stockItemOldCopyForStoreItemIdEdits=stockItem_display;
        // get new copy of stock item
        StockItem newStockItem =stockManager.saveStockItemToMediaStore(imageUri,stockItem_display);
        //change uri of old copy
        stockItem_display.setUri(newStockItem.getUri());
        stockManager.editStoreItemId(stockItemOldCopyForStoreItemIdEdits,stockItem_display.getUri(),"",0);
        // effect changes in recycler view
        stockFragment.getStockItemAdapter().modifyStockItem(stockItem_display,newStockItem,true);
        showToast(getString(R.string.changes_saved));
    }



    public ArrayList<StockItem> matchStockItemToStockIds(ArrayList<StockItem> stockItems, ArrayList<StockItemId> itemIds){
        ArrayList<StockItem> stockItemArrayList=new ArrayList<>();
        for (StockItem stockItem:stockItems){
            String fileName=stockItem.getStockItemFileName();
            for (StockItemId stockItemId:itemIds){
                if(fileName.equals(stockItemId.getStockName())){
                    String uriString=stockItem.getUri()+"/"+stockItemId.getStockId();
                    Uri uri = Uri.parse(uriString);
                    //set uri for later updates
                    stockItem.setUri(uri);
                    //set the id for updates later
                    stockItem.setStockItemIdKey(stockItemId.getStockId());
                    stockItemArrayList.add(stockItem);
                }
            }
        }
        return stockItemArrayList;
    }

    @Override
    public void onBackPressed() {
        if (fragment==null){
            fragment=CategoryFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,fragment).commit();
            setTitle(getString(R.string.app_name));
            menuItem.setVisible(false);
            storeIsActive=false;
        }else if (fragmentNull) {
            showToast(getString(R.string.back));
            goBack=true;
            fragmentNull=false;
            return;
        }else if(goBack){
            finish();
        }else{
        super.onBackPressed();}
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode()==RESULT_OK) {
                Uri imageUri = result.getData().getData();
                myImageView.setImageURI(imageUri);
                //to store latest snapshot of image uri
                //this variable controls execution of the callback
                if(run){
                    try {
                        saveReplacedImage(imageUri);
                        run=false;
                    } catch (IOException exception) {
                        exception.printStackTrace();
                        showToast(getString(R.string.failed));
                    }


                }
    }}
});

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void changeProgressBarColor(ProgressBar progressBar){
        Drawable progressDrawable= progressBar.getIndeterminateDrawable().mutate();
        progressDrawable.setColorFilter(getColor(R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
        progressBar.setIndeterminateDrawable(progressDrawable);
    }

  public static void showProgress(String message){
      progress_layout.setVisibility(View.VISIBLE);
        progress_text.setText(message);

  }

  public static void dismissProgress(){
        progress_layout.setVisibility(View.GONE);
  }

  public void toggleLayoutVisibility(int key,View view){
        view.setVisibility(key);
  }

  public  ArrayList<StockItem> searchStockItem(ArrayList<StockItem> stockItemArrayList,String search){

        String keyword=search.toLowerCase();
        ArrayList<StockItem> stockItems=new ArrayList<>();
        for(StockItem stockItem:stockItemArrayList){
            String stockName=stockItem.getNarration().toLowerCase();
            if(stockName.contains(keyword)){
                stockItems.add(stockItem);
            }
        }

        return stockItems;
  }

}



