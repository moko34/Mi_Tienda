package com.example.mitiendapro.category;

import androidx.fragment.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mitiendapro.R;
import com.example.mitiendapro.controller.ItemManager;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class CategoryFragment extends Fragment implements CategoryAdapter.LaunchTransactions {
    @Override
    public void launchTransactionsActivity(Category category) {
        fragmentReadyInterface.onCategoryItemClicked(category);
    }

  public  interface TransactionFragmentReady {
        void onCategoryItemClicked(Category category);
    }
    private  TransactionFragmentReady fragmentReadyInterface;
    private ArrayList<Category> categoryArrayList;
    private RecyclerView recyclerView;
    private Context context;
    private ItemManager itemManager;
    private CategoryAdapter categoryAdapter;
    public CategoryFragment() {

    }





    public static CategoryFragment newInstance() {
        CategoryFragment fragment = new CategoryFragment();

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TransactionFragmentReady){
            fragmentReadyInterface= (TransactionFragmentReady) context;
            itemManager=new ItemManager(context);
            categoryArrayList=itemManager.retrieveCategories();
            categoryAdapter=new CategoryAdapter(context,categoryArrayList,this);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView=view.findViewById(R.id.category_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setAdapter(categoryAdapter);
        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
    public void showToast(String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    ItemTouchHelper.SimpleCallback simpleCallback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.UP|ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            switch (direction){
                case ItemTouchHelper.UP:
                case    ItemTouchHelper.RIGHT:
                    int itemPosition=viewHolder.getAdapterPosition();
                    Category deletedCategory = categoryArrayList.get(itemPosition);
                    itemManager.deleteCategory(deletedCategory);
                    categoryArrayList.remove(itemPosition);
                    categoryAdapter.notifyItemRemoved(itemPosition);
                    Snackbar.make(recyclerView,getString(R.string.delete_category), BaseTransientBottomBar.LENGTH_LONG)
                            .setAction(getString(R.string.undo),(view)->{
                                itemManager.addCategory(deletedCategory);
                                categoryArrayList.add(deletedCategory);
                                categoryAdapter.notifyItemInserted(itemPosition);
                            }).show();

            }
        }

            };



}