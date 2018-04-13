package com.example.tanmay.spkrecvoiceit;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.voiceit.voiceit2.VoiceItAPI2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    public VoiceItAPI2 myVoiceIt2;
    public Activity mActivity;

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private static final int PERMISSIONS_REQUEST_CAMERA = 2;
    private static final String TAG = "MainActivity";
    private boolean permissionAccepted = false;

    private Button mAddUserButton;
    private Button mVerifyUserVoiceButton;
    private Button mDeleteUsersButton;
    private Button mHelpButton;
    private String userID;
    private String groupId = "grp_0dce92b2e3e141c194ef3fc2fc39f257";
    private String apiKey = "key_b030846efc8a4335912cad8efea9d539";
    private String apiToken = "tok_ead7e4b09e2b4b27b79280456008ef4c";

    private Dialog popupDialog;

    private ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
    DBHelper dbHelper = new DBHelper(this);

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
            case PERMISSIONS_REQUEST_RECORD_AUDIO:
            case PERMISSIONS_REQUEST_CAMERA:
                permissionAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionAccepted) finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // dbHelper.deleteAllUsers();
       // dbHelper.deleteAllUserPermissions();
        /*dbHelper.dropPermissionsTable();
        dbHelper.addPermissions(1,"call");
        dbHelper.addPermissions(2,"camera");
        dbHelper.addPermissions(3,"mail");
*/
        Cursor res = dbHelper.getAllPermissions();
        Cursor res2 = dbHelper.getAllUsersData();
        res.moveToFirst();
        res2.moveToFirst();
       // Cursor res3 = dbHelper.getAllUsersData();
        for (int i =0; i< res.getCount();i++){
            Log.d(TAG, "res"+ res.getString(res.getColumnIndex("pId")));
            Log.d(TAG, "res"+ res.getString(res.getColumnIndex("permission_name")));
            res.moveToNext();
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSIONS_REQUEST_RECORD_AUDIO);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CAMERA);
        }

        myVoiceIt2 = new VoiceItAPI2(apiKey, apiToken);
        mActivity = this;

        popupDialog = new Dialog(this);

        mAddUserButton = (Button) findViewById(R.id.add_user_button);
        mAddUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPasswordPopup(v,1);
            }
        });

        mHelpButton = (Button) findViewById(R.id.help_button);
        mHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowHelpPopup(v);
            }
        });

        mVerifyUserVoiceButton = (Button) findViewById(R.id.verify_user_voice_button);
        mVerifyUserVoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,50);
                myVoiceIt2.voiceIdentification(groupId, "en-US", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d(TAG,"JSONResult voiceIdentification: "+ response.toString());
                        try {
                            Cursor res1 = dbHelper.getUsers_PermissionsData();
                            Cursor res2 = dbHelper.getAllUsersData();
                            res1.moveToFirst();
                            res2.moveToFirst();
                            for(int i = 0 ; i < res1.getCount(); i++){
                                Log.e(TAG, res1.getString(res1.getColumnIndex("userId"))+":"+res1.getString(res1.getColumnIndex("pId")));
                                res1.moveToNext();
                            }
                            for(int i = 0 ; i < res2.getCount(); i++){
                                Log.e(TAG, res2.getString(res2.getColumnIndex("userId"))+":"+res2.getString(res2.getColumnIndex("user_name")));
                                res2.moveToNext();
                            }
                            userID = response.getString("userId");
                            Cursor res = dbHelper.getUserData(response.getString("userId"));
                            res.moveToFirst();
                            Snackbar.make(findViewById(R.id.main_layout),"User Identified:" + res.getString(res.getColumnIndex("user_name")),Snackbar.LENGTH_LONG).show();

                            Log.d(TAG, userID);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(MainActivity.this,MainChatBotActivity.class);
                                    intent.putExtra("userId", userID);
                                    startActivity(intent);
                                }
                            },3000);
                        }
                        catch(Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        if (errorResponse != null) {
                            Toast.makeText(MainActivity.this, "JSONResult voiceIdentification: " + errorResponse.toString(),Toast.LENGTH_LONG).show();
                        }
                    }

                });
            }
        });

        mDeleteUsersButton = (Button) findViewById(R.id.delete_user_button);
        mDeleteUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               ShowPasswordPopup(v,2);
                /*myVoiceIt2.getAllUsers(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        dbHelper.deleteAllUserPermissions();
                        dbHelper.deleteAllUsers();
                        Log.d(TAG,"JSONResult getallusers: "+ response.toString());
                        try {
                            JSONArray arr = response.getJSONArray("users");
                            ArrayList<String> strarr = new ArrayList<String>();
                            for(int i = 0; i < arr.length(); i++) {
                                strarr.add(arr.getJSONObject(i).getString("userId"));
                            }
                            Log.d(TAG,Integer.toString(strarr.size()));
                            for(int i = 0; i < arr.length(); i++)
                                myVoiceIt2.deleteUser(strarr.get(i), new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        Log.d(TAG,"JSONResult deleteduser: "+ response.toString());
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        if (errorResponse != null) {
                                            Toast.makeText(MainActivity.this,"JSONResult deleteduser: " + errorResponse.toString(),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        if (errorResponse != null) {
                            Toast.makeText(MainActivity.this,"JSONResult getallusers: " + errorResponse.toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                });*/
            }
        });
    }

    public void ShowHelpPopup(View v) {
        popupDialog.setContentView(R.layout.popup_help);

        popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupDialog.show();
    }

    public void ShowPasswordPopup(View v, int buttonPressed) {
        popupDialog.setContentView(R.layout.popup_password);

        final EditText password = (EditText) popupDialog.findViewById(R.id.password_text);
        Button mEnterButton = (Button) popupDialog.findViewById(R.id.enter_button);

        if(buttonPressed == 1) {
            mEnterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "hello   "+password.getText().toString());
                    if(password.getText().toString().equals("1234")) {
                        Intent intent = new Intent(MainActivity.this, AddUserActivity.class);
                        intent.putExtra("tokenKey", apiToken);
                        intent.putExtra("apiKey", apiKey);
                        intent.putExtra("groupId", groupId);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(v.getContext(),"Incorrect Password",Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
        else if(buttonPressed == 2) {
            mEnterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(password.getText().toString().equals("1234")) {
                        Intent intent = new Intent(MainActivity.this, DeleteUserActivity.class);
                        intent.putExtra("tokenKey", apiToken);
                        intent.putExtra("apiKey", apiKey);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(v.getContext(),"Incorrect Password",Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
        popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupDialog.show();
    }
}
