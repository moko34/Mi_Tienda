package com.example.mitiendapro.stocks;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.mitiendapro.R;
import com.example.mitiendapro.controller.StockManager;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StockFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StockFragment extends Fragment implements StockItemAdapter.StockItemIsClicked {

    private NewStockAdapter stockItemAdapter;
    private  Context mContext;
    private StockManager stockManager;
    private static DisplayImage mDisplayImage;
    private RecyclerView recyclerView;
    private static ArrayList<StockItem> mStockItems;

    @Override
    public void increaseStock(StockItem stockItem, TextView textView) {
        mDisplayImage.increaseQuantity(stockItem, textView);
    }

    @Override
    public void reduceStock(StockItem stockItem) {
        mDisplayImage.reduceQuantity(stockItem);
    }

    @Override
    public void setStockImage(StockItem stockItem) {
        mDisplayImage.displayImage(stockItem);

    }

    @Override
    public void editStockQuantity(StockItem stockItem) {
        mDisplayImage.setQuantityTo(stockItem);
    }

   public interface DisplayImage {
        // launch fragment responsible for zooming out image
        void displayImage(StockItem stockItem);

        void increaseQuantity(StockItem stockItem, TextView textView);

        void reduceQuantity(StockItem stockItem);

        void setQuantityTo(StockItem stockItem);
    }
    // TODO: Rename and change types of parameters


    public StockFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DisplayImage) {
            mDisplayImage = (DisplayImage) context;
            stockItemAdapter = new NewStockAdapter(mStockItems, context, this);
            mContext=context;
            stockManager=new StockManager(context);
        }
    }

    // TODO: Rename and change types and number of parameters
    public static StockFragment newInstance(DisplayImage displayImage, ArrayList<StockItem> stockItems) {
        StockFragment fragment = new StockFragment();
        mDisplayImage = displayImage;
        mStockItems = stockItems;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.stock_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(stockItemAdapter);
        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stock, container, false);
    }


    public void showToast(String toastMessage) {
        Toast.makeText(mContext, toastMessage, Toast.LENGTH_LONG).show();
    }


    public NewStockAdapter getStockItemAdapter() {
        return stockItemAdapter;
    }

    ItemTouchHelper.SimpleCallback simpleCallback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position=viewHolder.getAdapterPosition();
        switch (direction){
            case ItemTouchHelper.RIGHT:
            case ItemTouchHelper.LEFT:
                StockItem deletedStockItem=mStockItems.get(position);
                stockManager.deleteStockItemFromMediaStore(deletedStockItem);
                mStockItems.remove(position);
                stockItemAdapter.notifyItemRemoved(position);
                showToast(getString(R.string.stockItem_deleted));

        }
        }
    };
}
