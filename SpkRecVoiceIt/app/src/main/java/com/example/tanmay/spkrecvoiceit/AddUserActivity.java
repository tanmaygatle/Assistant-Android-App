package com.example.tanmay.spkrecvoiceit;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
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
    private
    Dialog enrolDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        enrolDialog = new Dialog(this);

        //Gson gson = new Gson();
        Bundle extras = getIntent().getExtras();
        myVoiceIt2 = new VoiceItAPI2(extras.getString("apiKey"), extras.getString("tokenKey"));
        groupId = extras.getString("groupId");


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
                        Log.d(TAG,"Inside CreateUser : ");
                        Log.d(TAG,"JSONResult createUser: "+ response.toString());
                        try {
                            userID = response.getString("userId");
                            Log.d(TAG,userID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        myVoiceIt2.addUserToGroup(groupId, userID, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Log.d(TAG,"JSONResult addUserToGroup: "+ response.toString());

                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,50);
                                myVoiceIt2.createVoiceEnrollment(userID, "en-US", new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        Log.d(TAG,"JSONResult createVoiceEnrollment1: "+ response.toString());

                                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,50);
                                        myVoiceIt2.createVoiceEnrollment(userID, "en-US", new JsonHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                Log.d(TAG,"JSONResult createVoiceEnrollment2: "+ response.toString());

                                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,50);
                                                myVoiceIt2.createVoiceEnrollment(userID, "en-US", new JsonHttpResponseHandler() {
                                                    @Override
                                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                        Log.d(TAG,"JSONResult createVoiceEnrollment3: "+ response.toString());
                                                        showEnrollmentStatus(true, "Enrollment Successful", mEnrolButton);
                                                    }

                                                    @Override
                                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                                        if (errorResponse != null) {
//                                                            mJSONResponseTextView.setText("JSONResult createVoiceEnrollment3: " + errorResponse.toString());
                                                            showEnrollmentStatus(false, "Enrollment failed", mEnrolButton);
                                                        }
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                                if (errorResponse != null) {
//                                                    mJSONResponseTextView.setText("JSONResult createVoiceEnrollment2: " + errorResponse.toString());
                                                    showEnrollmentStatus(false, "Enrollment failed", mEnrolButton);
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        if (errorResponse != null) {
//                                            mJSONResponseTextView.setText("JSONResult createVoiceEnrollment1: " + errorResponse.toString());
                                            showEnrollmentStatus(false, "Enrollment failed", mEnrolButton);
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                if (errorResponse != null) {
//                                    mJSONResponseTextView.setText("JSONResult addUserToGroup: " + errorResponse.toString());
                                    showEnrollmentStatus(false, "Enrollment failed", mEnrolButton);
                                }
                            }
                        });

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        if (errorResponse != null) {
//                            mJSONResponseTextView.setText("JSONResult createUser: " + errorResponse.toString());
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
//        Button mEnrolButton = findViewById(R.id.enrol_button);
        ViewGroup parent = (ViewGroup) mEnrolButton.getParent();
        int index = parent.indexOfChild(mEnrolButton);
        parent.removeView(mEnrolButton);
//        C = getLayoutInflater().inflate(optionId, parent, false);
        TextView statusTextView = (TextView) findViewById(R.id.enrollment_status_text_view);
        statusTextView.setText(statusText);
        if(isSuccess)
            statusTextView.setBackgroundColor(getResources().getColor(R.color.green));
        else
            statusTextView.setBackgroundColor(getResources().getColor(R.color.white));

        parent.addView(statusTextView, index);
    }
}
