package com.vternal.vfexampleandroid;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vternal.android.vternalframework.VternalFramework;
import com.vternal.android.vternalframework.VternalStatistics;

import java.util.UUID;

public class MainActivity extends Activity
{
    //  request codes
    public static final int FILE_CHOOSER_CODE = 1;
    public volatile static MainActivity myActivity = null;
    static final int WRITE_REQUEST_CODE = 1;

    TextView messageView = null;
    Button storeButton = null;
    Button findButton = null;
    Button statsButton = null;
    Button allocButton = null;
    TextView statsBirthdayText = null;
    TextView statsRealtimeText = null;
    TextView statsLastConjugationTimeText = null;
    TextView statsTotalConjugationsText = null;
    TextView statsAverageTimePerConjugationText = null;
    TextView statsAverageSpacingPerConjugationText = null;
    TextView statsSpaceAllocatedText = null;
    TextView statsSpaceUsedText = null;
    TextView statsVternalizedBytesText = null;
    TextView statsVternalizationsText = null;
    TextView statsNumberOfAssetsText = null;

    TextView maxAssetSizeText = null;
    TextView maxFileSizeText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        myActivity = this;

        setContentView(R.layout.activity_main);

        statsBirthdayText = (TextView) findViewById(R.id.stats_BirthDay);
        statsRealtimeText = (TextView) findViewById(R.id.stats_RealtimeElapsed);
        statsLastConjugationTimeText = (TextView) findViewById(R.id.stats_LastConjugationTime);
        statsTotalConjugationsText = (TextView) findViewById(R.id.stats_TotalConjugetions);
        statsAverageTimePerConjugationText = (TextView) findViewById(R.id.stats_AverageTimePerConjugation);
        statsAverageSpacingPerConjugationText = (TextView) findViewById(R.id.stats_AverageSpacingPerConjugation);
        statsSpaceAllocatedText = (TextView) findViewById(R.id.stats_SpaceAllocated);
        statsSpaceUsedText = (TextView) findViewById(R.id.stats_SpaceUsed);
        statsVternalizedBytesText = (TextView) findViewById(R.id.stats_VternalizedBytes);
        statsVternalizationsText = (TextView) findViewById(R.id.stats_Vternalizations);
        statsNumberOfAssetsText = (TextView) findViewById(R.id.stats_NumberOfAssets);

        maxAssetSizeText = (TextView) findViewById(R.id.stats_maxAssetSize);
        maxFileSizeText = (TextView) findViewById(R.id.stats_maxFileSize);

        maxAssetSizeText.setText( String.format("%d bytes", VternalFramework.maxAssetSize() ) );
        maxFileSizeText.setText( String.format("%d bytes", VternalFramework.maxFileSize() ) );

        messageView = (TextView) findViewById(R.id.message);
        storeButton = (Button) findViewById(R.id.store_button);
        storeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try
                {
                    MainActivity.this.startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_CHOOSER_CODE);
                } catch (android.content.ActivityNotFoundException ex)
                {
                    // Potentially direct the user to the Market with a Dialog
                    Toast.makeText(MainActivity.this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        findButton = (Button) findViewById(R.id.find_button);
        findButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                FragmentManager fm = MainActivity.myActivity.getFragmentManager();
                FindDialog findDialog = new FindDialog();
                findDialog.show(fm, "find_asset__dialog");
            }
        });

        statsButton = (Button) findViewById(R.id.stats_button);
        statsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                VternalStatistics results = VternalFramework.getStatistics();

                if( results != null )
                {
                    statsBirthdayText.setText( String.format("%d", results.serverBirthday) );
                    statsRealtimeText.setText( String.format("%d", results.serverRealtimeCumulative) );
                    statsLastConjugationTimeText.setText( String.format("%d", results.serverLastConjugationTime) );
                    statsTotalConjugationsText.setText( String.format("%d", results.serverTotalConjugations) );
                    statsAverageTimePerConjugationText.setText( String.format("%d", results.serverAverageTimePerConjugation) );;
                    statsAverageSpacingPerConjugationText.setText( String.format("%d", results.serverAverageSpacingPerConjugation) );
                    statsSpaceAllocatedText.setText( String.format("%d", results.serverSpaceAllocated) );
                    statsSpaceUsedText.setText( String.format("%d", results.serverSpaceUsed) );
                    statsVternalizedBytesText.setText( String.format("%d", results.serverVternalizedBytes) );
                    statsVternalizationsText.setText( String.format("%d", results.serverVternalizations) );
                    statsNumberOfAssetsText.setText( String.format("%d", results.serverNumberOfAssets) );

                    System.out.format("Vternal Framework Api : getStats done \n");

                }
            }
        });

        allocButton = (Button) findViewById(R.id.alloc_button);
        allocButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            }
        });


        if( checkSelfPermission( Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED )
        {
            //if you dont have required permissions ask for it (only required for API 23+)
            requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE);
        }
        else
        {
            VternalFramework.registerApplication(this, "219a47618f91cb69216882a4028318e6de8c1f906ad499547a75a1579d285a4d",
                    new VternalFramework.VternalRegister()
                    {
                        @Override
                        public void registered(boolean result)
                        {
                            if( result )
                            {
                                messageView.setText( getPackageName() );
                            }
                            else
                            {
                                messageView.setText( "FAILED TO REGISTER" );
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case WRITE_REQUEST_CODE:
            {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d("permission", "granted");
                    VternalFramework.registerApplication(this,
                            "219a47618f91cb69216882a4028318e6de8c1f906ad499547a75a1579d285a4d", new VternalFramework.VternalRegister()
                            {
                                @Override
                                public void registered(boolean result)
                                {
                                    if( result )
                                    {
                                        messageView.setText( getPackageName() );
                                    }
                                    else
                                    {
                                        messageView.setText( "FAILED TO REGISTER" );
                                    }
                                }
                            });

                }
                else
                {

                    // permission denied
                    Toast.makeText(MainActivity.this, "You cannot Vternalize any Files", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode)
        {
            case FILE_CHOOSER_CODE:
                if ( resultCode == Activity.RESULT_OK ) {
                    // Get the Uri of the selected file
                    final Uri uri = data.getData();

                    //  this line, takes the read permission and permissability granted by the chooser (see uploadButton.OnClick() )
                    //
                    this.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    VternalFramework.storeUriToVternal(uri, new VternalFramework.VternalStore()
                    {
                        @Override
                        public void stored( String newKey)
                        {
                            System.out.format("Vternal Framework Api : stored file %s for key %s\n", getFileName(uri), newKey);
                        }
                    });
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void showMessage( final String title, final String message )
    {
        if( myActivity == null )
        {
            System.out.format("showMessage : called in background, message='%s'\n", message );
            return;
        }

        new Handler(myActivity.getMainLooper())
                .post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        new AlertDialog.Builder(myActivity)
                                .setTitle(title)
                                .setMessage(message)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                    }
                                })
                                .create()
                                .show();
                    }
                });
    }

    public static String makeUUID()
    {
        return UUID.randomUUID().toString().replace("-", "" );
    }

    public String getFileName( Uri uri )
    {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            Cursor cursor;

            try {
                cursor = this.getContentResolver().query(uri, null, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow( DocumentsContract.Document.COLUMN_DISPLAY_NAME);

                if (cursor.moveToFirst()) {
                    String displayName = cursor.getString(column_index);

                    cursor.close();

                    return displayName;
                }

                cursor.close();

            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getLastPathSegment();
        }

        return null;
    }


}
