package com.example.mitiendapro.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mitiendapro.R;

import java.util.Calendar;

public class CustomDialog extends DialogFragment {
   public interface DialogInterfaceEvent{
        void onPositiveButtonClicked(int date,int amount,String description);
        void editIconIsPressed(EditText date, EditText description, EditText amount);
        void onNegativeButtonClicked();
    }
    private View dialogView;
    private Context context;
    private LayoutInflater layoutInflater;
    private EditText date,description,amount;
    private DialogInterfaceEvent dialogInterfaceEvent;
    private String buttonText;
    private TextView textView;


    public CustomDialog(DialogInterfaceEvent dialogInterfaceEvent,String buttonText) {
        this.dialogInterfaceEvent = dialogInterfaceEvent;
        this.buttonText=buttonText;
    }

    public CustomDialog(int contentLayoutId, DialogInterfaceEvent dialogInterfaceEvent,Context context,String buttonText) {
        super(contentLayoutId);
        this.context=context;
        this.dialogInterfaceEvent = dialogInterfaceEvent;
        this.buttonText=buttonText;
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        //show dialog till cancel is tapped
        dialog.setCancelable(false);
        //set the view to the custom dialog
        dialog.setView(dialogView);
        //When editing a transaction the button text is save
        if(buttonText==getString(R.string.save)){
            // the title is also set to update transaction
            textView.setText(getText(R.string.update));
            //this callback triggers the removal of the transaction from the adapter arraylist
            dialogInterfaceEvent.editIconIsPressed(getDate(),getDescription(),getAmount());
        }else{
        //get the current date
        Calendar calendar=Calendar.getInstance();
        date.setText(calendar.get(Calendar.DATE)+"");}
        //set the negative button
        dialog.setNegativeButton(getString(R.string.cancel),(cancelDialog,direction)->{
            //if transaction is being edited cancel button should add old copy f transaction
            //to adapter arraylist
            if(getButtonText()==getString(R.string.save)){
                //this callback implements the above
                dialogInterfaceEvent.onNegativeButtonClicked();
            }
            //if transaction is  not being edited the cancel button should close the dialog
            cancelDialog.dismiss();
        });
        //set the positive button action
        dialog.setPositiveButton(buttonText, (dialog1, which) -> {
            //Extract the entered amount
            int transactionAmount=Integer.parseInt(amount.getText().toString());
            //Extract the entered date
            int   transactionDate =Integer.parseInt(date.getText().toString());
            //Extract the specified description
            String transactionDescription=description.getText().toString();
            if(String.valueOf(transactionDate).equals("0") || String.valueOf(transactionAmount).equals("0") || transactionDescription.equals("") || transactionDescription.equals("trxtn") ){
                Toast.makeText(context, getString(R.string.valid), Toast.LENGTH_SHORT).show();

            } else{
                //If inputs are valid add transaction to arraylist and itemManager
                dialogInterfaceEvent.onPositiveButtonClicked(transactionDate,transactionAmount,transactionDescription);
                // dismiss dialog
                dialog1.dismiss();
            }
        });

        return dialog.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof  DialogInterfaceEvent){
            dialogInterfaceEvent= (DialogInterfaceEvent) context;
            //inflate your custom layout
            layoutInflater= requireActivity().getLayoutInflater();
            dialogView=layoutInflater.inflate(R.layout.dcustom_dialog_view,null);
            this.context=context;
            //initialize views
            date=dialogView.findViewById(R.id.edtDate);
            amount=dialogView.findViewById(R.id.dialog_amount);
            description=dialogView.findViewById(R.id.dialog_description);
            textView=dialogView.findViewById(R.id.dialog_title);

        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dcustom_dialog_view,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public String getButtonText() {
        return buttonText;
    }

    public EditText getDate() {
        return date;
    }

    public EditText getDescription() {
        return description;
    }

    public EditText getAmount() {
        return amount;
    }
}


