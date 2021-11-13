package com.example.mitiendapro.controller;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.mitiendapro.R;
import com.example.mitiendapro.stocks.StockItem;
import com.example.mitiendapro.stocks.StockItemId;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;

public class StockManager {

    private Context context;
    public StockManager(Context context){
        this.context=context;

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public StockItem saveStockItemToMediaStore(Uri uri, StockItem stockItem) throws IOException {
        showToast(context.getString(R.string.saving_please));

        ContentResolver resolver = context.getContentResolver();

        Bitmap bitmap =  MediaStore.Images.Media.getBitmap(resolver, uri);
        //saving image
        OutputStream outputStream;
        Uri collectionPath;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collectionPath = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            collectionPath = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, stockItem.getNarration() + "ytzr8r" + stockItem.getQuantity() + ".jpg");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name));
        Uri imageUri = resolver.insert(collectionPath, contentValues);

        outputStream = resolver.openOutputStream(imageUri);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        // Extract a file name for image pairing later
        String fileName=stockItem.getNarration() + "ytzr8r" + stockItem.getQuantity() + ".jpg";
        String[] uriString=imageUri.toString().split("images/");

        // extract an id key to aid retrieving image pairs
        String[] finalUri=uriString[1].split("a/");


        long stockIdKey =Long.parseLong(finalUri[1]);


        StockItem newStockItem=new StockItem(imageUri, stockItem.getNarration(), stockItem.getQuantity(),stockIdKey,fileName);
        saveStoreItemId(newStockItem);
        Toast.makeText(context, context.getString(R.string.image_saved), Toast.LENGTH_SHORT).show();
        return newStockItem;

    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ArrayList<StockItem> retrieveStockItemsFromMediaStore(){
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri= MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;}
        ContentResolver contentResolver=context.getContentResolver();
        ArrayList<StockItem> stockItems=new ArrayList<>();
        String[] projection=new String[]{
                MediaStore.MediaColumns.DISPLAY_NAME
        };
        String selection=MediaStore.MediaColumns.DISPLAY_NAME +  " like ? ";
        String[] selectionArgs=new String[]{Environment.DIRECTORY_PICTURES  + File.separator + context.getString(R.string.app_name)};
        //select where filename contains unique identifier
        String[] args=new String[]{"%ytzr8r%"};
        String order= MediaStore.MediaColumns.DATE_ADDED;
        try(Cursor cursor =contentResolver.query(uri,projection,selection,args,order)){
            int nameId = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
            String name= cursor.getColumnName(nameId);
            while (cursor.moveToNext()){
                String stringToManipulate= cursor.getString(nameId);
                String [] first=stringToManipulate.split("ytzr8r");
                String [] second=first[1].split(".jp");
                stockItems.add(new StockItem(uri,first[0],Integer.parseInt(second[0]),0,stringToManipulate));

            }


        }
        return stockItems;
    }

    public void deleteStockItemFromMediaStore(StockItem stockItem){
        ContentResolver contentResolver=context.getContentResolver();
        String selection = MediaStore.MediaColumns.DISPLAY_NAME + "=?";
        String[] selectionArgs=new String[]{ stockItem.getNarration() + "ytzr8r" + stockItem.getQuantity() + ".jpg"};
        contentResolver.delete(stockItem.getUri(),selection,selectionArgs);

    }
    public void UpdateStockItemInMediaStore(StockItem stockItem,String newStoreItemName,int newStoreItemQuantity){

        ContentResolver contentResolver=context.getContentResolver();
        String selection = MediaStore.MediaColumns.DISPLAY_NAME + " = ?";
        // Select old copy of storeItem
        String[] selectionArgs=new String[]{stockItem.getStockItemFileName()};
        ContentValues contentValues=new ContentValues();
        //Append new copy with new Store Item details
        if(!newStoreItemName.equals("")){
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, newStoreItemName + "ytzr8r" + stockItem.getQuantity() + ".jpg");
            stockItem.setStockItemFileName(newStoreItemName + "ytzr8r" + stockItem.getQuantity() + ".jpg");
            editStoreItemId(stockItem,null,newStoreItemName,0);
        }else if(newStoreItemQuantity!= 0){
            if(!newStoreItemName.equals("")){
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, newStoreItemName + "ytzr8r" + newStoreItemQuantity + ".jpg");
                stockItem.setStockItemFileName( newStoreItemName + "ytzr8r" + newStoreItemQuantity + ".jpg");
                editStoreItemId(stockItem,null,newStoreItemName,newStoreItemQuantity);

            }else{
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, stockItem.getNarration() + "ytzr8r" + newStoreItemQuantity + ".jpg");
                stockItem.setStockItemFileName( stockItem.getNarration() + "ytzr8r" + newStoreItemQuantity + ".jpg");
                editStoreItemId(stockItem,null,"",newStoreItemQuantity);
            }
        }

        contentResolver.update(stockItem.getUri(),contentValues,selection,selectionArgs);
        Toast.makeText(context, context.getString(R.string.image_updated), Toast.LENGTH_SHORT).show();

    }
    public Bitmap decodeBitmap(Uri uri) throws IOException {
        InputStream inputStream=context.getContentResolver().openInputStream(uri);
        BitmapFactory.Options bitmapOptions=new BitmapFactory.Options();
        //return only bounds of image
        bitmapOptions.inJustDecodeBounds=false;
        bitmapOptions.inPreferredConfig= Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeStream(inputStream,null,bitmapOptions);
        inputStream.close();
        int width= bitmapOptions.outWidth;
        int height= bitmapOptions.outHeight;
        float recommendedWidth=300f;
        float recommendedHeight= 500f;
        if(width==-1|| height==-1){
            return null;
        }
        // scale value because it's a fixed scale only one value will be used
        int scale=1;
        if(width > height && width > recommendedWidth){
            scale= (int) (width/recommendedWidth);
        }
        if(height<width && height>recommendedHeight){
            scale= (int) (height/recommendedHeight);
        }
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPreferredConfig= Bitmap.Config.ARGB_8888;
        options.inSampleSize=scale;
        inputStream=context.getContentResolver().openInputStream(uri);
        Bitmap bitmap= BitmapFactory.decodeStream(inputStream,null,options);
        return compressImage(bitmap);
    }

    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//Quality compression method, here 100 means no compression, store the compressed data in the BIOS
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //Cycle to determine if the compressed image is greater than 100kb, greater than continue compression
            baos.reset();//Reset the BIOS to clear it
            //First parameter: picture format, second parameter: picture quality, 100 is the highest, 0 is the worst, third parameter: save the compressed data stream
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//Here, the compression options are used to store the compressed data in the BIOS
            options -= 10;//10 less each time
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//Store the compressed data in ByteArrayInputStream
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//Generate image from ByteArrayInputStream data
        return bitmap;
    }
    public void showToast(String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
    //When saving a stock item image we save the id and the file name to sharedPreferences for later pairing
    public void saveStoreItemId(StockItem stockItem){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor=preferences.edit();
        String stockItemId=   stockItem.getStockItemIdKey()+"\n"+"ky8rzt6q";
        editor.putString(stockItemId,stockItem.getStockItemFileName());
        editor.commit();
    }

    public ArrayList<StockItemId> retrieveStockItemId(){
        ArrayList<StockItemId> stockItemIds=new ArrayList<>();
        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(context);
        Map<String,?> sharedPreferenceMap=sharedPreferences.getAll();
        for (Map.Entry<String,?> entry:sharedPreferenceMap.entrySet()){
            if(entry.getKey().contains("ky8rzt6q")){
                String[] splitKey=entry.getKey().split("\n");
                stockItemIds.add(new StockItemId(Long.parseLong(splitKey[0]),(String) entry.getValue()));

            }
        }
        return stockItemIds;
    }

    public void editStoreItemId(StockItem stockItem,Uri uri,String fileName,int quantity){
        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(context);


        SharedPreferences.Editor editor= sharedPreferences.edit();
        //remove store item id from shared preferences
        deleteStockItemId(stockItem);
        //store old copy in new stock item
        StockItemId stockItemId=new StockItemId(stockItem.getStockItemIdKey(),stockItem.getStockItemFileName());
        if(uri!=null){
            //replace uri
            String[] uriString=uri.toString().split("images/media/");

            stockItemId.setStockId(Long.parseLong(uriString[1]));

            stockItem.setUri(uri);
            //replace filename while uri is valid
            if(!fileName.equals("")){
                stockItemId.setStockName(fileName + "ytzr8r" + stockItem.getQuantity() + ".jpg");
                stockItem.setNarration(fileName);
                if (quantity!=0){
                    stockItemId.setStockName(fileName + "ytzr8r" + quantity + ".jpg");
                    stockItem.setQuantity(quantity);
                }
            }
        }else if(uri==null && !fileName.equals("")){
            stockItemId.setStockName(fileName + "ytzr8r" + stockItem.getQuantity() + ".jpg");
            stockItem.setNarration(fileName);
            if (quantity!=0){
                stockItemId.setStockName(fileName + "ytzr8r" + quantity + ".jpg");
                stockItem.setQuantity(quantity);
            }

        }else if(uri==null && fileName.equals("")&&quantity!=0){
            stockItemId.setStockName(stockItem.getNarration() + "ytzr8r" + quantity + ".jpg");

            stockItem.setQuantity(quantity);
        }
        saveStoreItemId(stockItem);

    }
    public  void deleteStockItemId(StockItem stockItem){
        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        //extract the key
        String stockItemIdKey= stockItem.getStockItemIdKey() +"\n"+"ky8rzt6q";
        //find stockItemId
        editor.remove(stockItemIdKey);
        editor.commit();
    }
}


