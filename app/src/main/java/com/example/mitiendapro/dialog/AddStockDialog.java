package com.example.mitiendapro.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.example.mitiendapro.R;
import com.example.mitiendapro.controller.StockManager;
import com.example.mitiendapro.stocks.StockItem;

import java.io.IOException;

public class AddStockDialog extends DialogFragment {
    private ActivityResultLauncher<Intent> resultLauncher;
    public interface LoadImage {
        void showIntent();

        //Add stock item to arraylist
        void addStockItem(StockItem stockItem) throws IOException;

        //Open a large view of stock item
        void displayStockImage( ImageView imageView);

        // Add replaced image to stockItem info
        void saveReplacedImage(Uri imageUri) throws IOException;
    }

    private View addStockDialog;
    private Context context;
    //the second stock item is used to store a current snapshot of a stock item;
    private StockItem  stockItem;
    private LoadImage loadImage;
    private Uri imageToUri,savedImageUri;
    private String buttonText;
    private long stockIdKey;
    private ImageView unLoadedImage, loadedImage, imageView;
    private boolean imageIsNotLoaded, saveImage, fragmentIsCreated;
    private EditText stockQuantity, stockNarration;
    private StockManager stockManager;
    private int dialogId;

    public AddStockDialog(Context context, LoadImage loadImage, boolean imageIsNotLoaded, boolean saveImage) {
        this.loadImage = loadImage;
        this.context = context;
        this.saveImage = saveImage;
        stockItem = new StockItem(null, "", 0, 0, "");
        this.imageIsNotLoaded = imageIsNotLoaded;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder stockDialog = new AlertDialog.Builder(context);
        if (imageIsNotLoaded==true) {
            stockDialog.setCancelable(false);

            //set view to custom layout
            stockDialog.setView(addStockDialog);
            stockDialog.setPositiveButton(buttonText, (dialog, which) -> {
                //get chosen description
                String narrationText = stockNarration.getText().toString();
                //get chosen quantity
                int quantityText = Integer.parseInt(stockQuantity.getText().toString());
                //get scaleType to check if image was chosen
                ImageView.ScaleType scaleType = unLoadedImage.getScaleType();
                //Extract stock file name
                if (!narrationText.equals("") && quantityText != 0) {
                    String fileName = narrationText + "ytzr8r" + quantityText + ".jpg";
                    // Extract stock id key
                    //String stockIdKey=imageToUri.toString().split("images/")[1];
                    stockItem = new StockItem(savedImageUri, narrationText, quantityText, stockIdKey, fileName);
                    if (scaleType == ImageView.ScaleType.FIT_XY) {
                        Toast.makeText(context, getString(R.string.please_add_image), Toast.LENGTH_LONG).show();
                        return;
                    } else {

                        try {
                            loadImage.addStockItem(stockItem);
                        } catch (IOException exception) {
                            exception.printStackTrace();
                            showToast("failed to save image");
                        }

                    }
                } else {
                    showToast(getString(R.string.fill_all));
                }

            });

        } else {
            // the interface sets the background of the image to the StockItem image uri
            loadImage.displayStockImage( loadedImage);
            stockDialog.setView(addStockDialog);
            stockDialog.setPositiveButton(buttonText, (dialog, which) -> {
                //launch gallery to pick an image and change background uri
                //the interface changes the image uri in the adapter as well
                loadImage.showIntent();

            });


            stockDialog.setCancelable(false);

        }

        return stockDialog.create();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //dialog form image view

        //image view holds one of the two image views because in all case an intent
        //may be sent and the image previewed
        imageView = unLoadedImage == null ? loadedImage : unLoadedImage;

        if(imageIsNotLoaded==true){
            showToast("false");
            unLoadedImage.setOnClickListener((view) -> {
                //extract description
                String narrationValue = stockNarration.getText().toString();
                //extract quantity
                int quantityValue = stockQuantity.getText().toString() == "" ? 0 : Integer.parseInt(stockQuantity.getText().toString());
                stockItem = new StockItem(null, narrationValue, quantityValue, 0, "");
                if (narrationValue.equals("") || String.valueOf(quantityValue).equals("0")) {
                    Toast.makeText(context, getString(R.string.set_values), Toast.LENGTH_LONG).show();
                    return;
                }else if(narrationValue.equals("ytzr8r")){
                    //if chosen name contains key  return
                    showToast(getString(R.string.valid));
                    return;
                }
                else {
                    // save the image to the mediaStore
                    //add stock item to arraylist
                    if(fragmentIsCreated){
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        resultLauncher.launch(intent);
                    }
                }});}
        //Register an activity launcher to launch the open gallery intent
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == -1) {

                    Uri imageUri = result.getData().getData();
                    imageView.setImageURI(imageUri);
                    //to store latest snapshot of image uri
                    imageToUri = imageUri;
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    // unLoadedImage.setClickable(false);
                    if (saveImage) {
                        try {
                            //saving image permanently

                            StockItem newStock = stockManager.saveStockItemToMediaStore(imageUri, stockItem);
                            //set the uri
                            stockItem.setUri(newStock.getUri());
                            //set the stockId
                            stockIdKey = newStock.getStockItemIdKey();
                            savedImageUri = newStock.getUri();
                            stockItem.setId(newStock.getId());
                            unLoadedImage.setClickable(false);
                        } catch (IOException exception) {
                            exception.printStackTrace();
                            showToast(getString(R.string.images_unsaved));
                        }

                    }
                }



            }});





        fragmentIsCreated=true;

    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof LoadImage) {
            loadImage = (LoadImage) context;
            stockManager = new StockManager(context);
            this.context = context;
            if (imageIsNotLoaded) {
                dialogId = R.layout.add_stock_dialog;
                buttonText = getString(R.string.add);
            } else {
                dialogId = R.layout.dialog_display_image;
                buttonText = getString(R.string.replace);
            }
            addStockDialog = requireActivity().getLayoutInflater().inflate(dialogId, null);
            if (imageIsNotLoaded) {
                unLoadedImage = addStockDialog.findViewById(R.id.stock_dialog_photo);
                stockNarration = addStockDialog.findViewById(R.id.edt_dialog_narration);
                stockQuantity = addStockDialog.findViewById(R.id.edt_stock_quantity);
            } else {
                loadedImage = addStockDialog.findViewById(R.id.dialog_display_photo);
            }

        }


    }



    public void showToast(String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();}}





