package com.example.tanmay.spkrecvoiceit;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.voiceit.voiceit2.VoiceItAPI2;

import org.json.JSONException;
import org.json.JSONObject;


import cz.msebera.android.httpclient.Header;


public class AddUserActivity extends AppCompatActivity{
    public VoiceItAPI2 myVoiceIt2;

    private String userID;
    private String groupId;
    private static final String TAG = "AddUserActivity";
    private ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
    private Dialog enrolDialog;
    DBHelper dbHelper = new DBHelper(this);
    private Button mCreateUserButton;
    private EditText nameEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        enrolDialog = new Dialog(this);

        Bundle extras = getIntent().getExtras();
        myVoiceIt2 = new VoiceItAPI2(extras.getString("apiKey"), extras.getString("tokenKey"));
        groupId = extras.getString("groupId");

        mCreateUserButton = (Button) findViewById(R.id.create_user_button);
        nameEditText = (EditText) findViewById(R.id.name_edit_text);
        mCreateUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameEditText.getText().toString().matches(".*\\w.*"))
                    ShowPopup(v);
                else
                    Snackbar.make(findViewById(R.id.ScrollView01), "The Username must contain only alphanumeric characters", Snackbar.LENGTH_LONG).show();
            }
        });

    }

    public void ShowPopup(View v) {
        TextView mTxtClose;
        final Button mEnrolButton;
        enrolDialog.setContentView(R.layout.popup_user_enrol);
        mTxtClose =(TextView) enrolDialog.findViewById(R.id.txtclose);
        mEnrolButton = (Button) enrolDialog.findViewById(R.id.enrol_button);

        mTxtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enrolDialog.dismiss();
            }
        });

        mEnrolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myVoiceIt2.createUser(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            Log.d(TAG,"Inside CreateUser : ");
                            Log.d(TAG,"JSONResult createUser: "+ response.toString());
                            userID = response.getString("userId");
                            Log.d(TAG,userID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        myVoiceIt2.addUserToGroup(groupId, userID, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                                Log.d(TAG,"JSONResult addUserToGroup: "+ response.toString());
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,50);

                                myVoiceIt2.createVoiceEnrollment(userID, "en-US", new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        try {
                                            Log.d(TAG,"JSONResult createVoiceEnrollment1: "+ response.toString());

                                            showEnrollmentStatus(true, "Enrollment Successful",mEnrolButton);
                                            String name = nameEditText.getText().toString();
                                            dbHelper.addUser(userID,name);

                                            int id;
                                            CheckBox checkBox;
                                            for(int i = 1;i <= 3; i++) {
                                                id = getResources().getIdentifier("checkbox_"+i,"id",getPackageName());
                                                checkBox = (CheckBox) findViewById(id);
                                                if(checkBox.isChecked())
                                                    dbHelper.addUsers_Permissions(userID,i);
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        if (errorResponse != null) {
                                            try {
                                                Log.d(TAG,"JSONResult createVoiceEnrollment1: " + errorResponse.toString());
                                                showEnrollmentStatus(false, "Enrollment failed", mEnrolButton);

                                                myVoiceIt2.deleteUser(userID, new JsonHttpResponseHandler() {
                                                    @Override
                                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                        Log.d(TAG,"JSONResult deleteduser: "+ response.toString());
                                                    }

                                                    @Override
                                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                                        if (errorResponse != null) {
                                                            Log.d(TAG,"JSONResult deleteduser: " + errorResponse.toString());
                                                        }
                                                    }
                                                });
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                if (errorResponse != null) {
                                    try{
                                        Log.d(TAG,"JSONResult addUserToGroup: " + errorResponse.toString());
                                        showEnrollmentStatus(false, "Enrollment failed", mEnrolButton);

                                        //dbHelper.deleteUser(userID);
                                        myVoiceIt2.deleteUser(userID, new JsonHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                Log.d(TAG,"JSONResult deleteduser: "+ response.toString());
                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                                if (errorResponse != null) {
                                                    Log.d(TAG,"JSONResult deleteduser: " + errorResponse.toString());
                                                }
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        if (errorResponse != null) {
                            Log.d(TAG,"JSONResult createUser: " + errorResponse.toString());
                            showEnrollmentStatus(false, "Enrollment failed",mEnrolButton);
                        }
                    }
                });
            }
        });
        enrolDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        enrolDialog.show();
    }

    public void showEnrollmentStatus(boolean isSuccess, String statusText, Button mEnrolButton) {
        mEnrolButton.setEnabled(false);
        Toast toast = Toast.makeText(AddUserActivity.this, statusText, Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setPadding(40,40,40,40);
        if(isSuccess)
            view.setBackgroundColor(getColor(R.color.green));
        else
            view.setBackgroundColor(getColor(R.color.red));
        toast.show();
    }
}
