package coupletones.pro.cse110.coupletones;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * The AddLocationDialog class extends the Dialog Fragment,which would show a dialog
 * when user add a favorite location. The dialog would prompt user to name the favorite location
 */
public class AddLocationDialog extends DialogFragment {
    public interface LocationDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    LocationDialogListener lListener;
    View view;
    EditText editLoc;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            lListener = (LocationDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LocationDialogListener");
        }
    }

    /**
     *
     * Create a dialog
     * @param savedInstanceState
     * @return Dialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.layout_dialog_add_location, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        lListener.onDialogPositiveClick(AddLocationDialog.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        lListener.onDialogNegativeClick(AddLocationDialog.this);
                    }
                });
        editLoc = (EditText)view.findViewById(R.id.location_name);
        return builder.create();
    }

    /**
     * this method get the name which user input when they add favorites
     *
     * @return
     *     name of the location which user input
     */
    public String getNameFromUser(){
        return editLoc.getText().toString();
    }
}
