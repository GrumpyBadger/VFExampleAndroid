package com.vternal.vfexampleandroid;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.vternal.android.vternalframework.VternalFindResult;
import com.vternal.android.vternalframework.VternalFramework;

/**
 * Created by mdunsmuir on 8/30/17.
 */

public class FindDialog extends DialogFragment
{
    public FindDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dialog_find, container);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        final EditText l_key = (EditText) view.findViewById(R.id.key_name);

        ImageView l_cancelbutton = (ImageView) view.findViewById(R.id.cancelbutton);
        l_cancelbutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismiss();
            }
        });

        ImageView l_findbutton =  (ImageView) view.findViewById(R.id.proceedbutton);
        l_findbutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Thread networkThread = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(l_key.getWindowToken(), 0);

                        final String keyString = l_key.getText().toString();

                        // keys must be 64 chars long
                        if( keyString.length() != 64 )
                        {
                            MainActivity.showMessage("Bad Key", "Valid keys are 64 characters long" );
                            return;
                        }

                        System.out.format("Vternal Framework Api : fetching file for key %s\n", keyString);

                        VternalFramework.fetchFileFromVternal(keyString, new VternalFramework.VternalFind()
                        {
                            @Override
                            public void found(VternalFindResult result)
                            {
                                if( result != null )
                                {
                                    if( result.state == VternalFindResult.FindState.Done )
                                        System.out.format("Vternal Framework Api : found file %s/%s for key %s\n", result.displayName, result.filePath, keyString);
                                    else
                                        System.out.format("Vternal Framework Api : error finding key %s\n", keyString);
                                }
                                else
                                    System.out.format("Vternal Framework Api : file not found for key %s\n", keyString);
                            }
                        });

                        dismiss();

                    }
                });

                networkThread.start();
            }
        });


        return view;
    }

 }
