package com.example.tanmay.spkrecvoiceit;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.voiceit.voiceit2.VoiceItAPI2;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    public VoiceItAPI2 myVoiceIt2;
    public Activity mActivity;

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private static final String TAG = "MainActivity";
    private boolean permissionAccepted = false;

    private Button mAddUserButton;
    private Button mVerifyUserButton;
    private TextView mJSONResponseTextView;
    private String userID;
    private String groupId = "grp_0dce92b2e3e141c194ef3fc2fc39f257";
    private String apiKey = "key_b030846efc8a4335912cad8efea9d539";
    private String apiToken = "tok_ead7e4b09e2b4b27b79280456008ef4c";
    private boolean groupExists = false;

    private int enrollmentCount = 0;
    private ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
            case PERMISSIONS_REQUEST_RECORD_AUDIO:
                permissionAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionAccepted) finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        myVoiceIt2 = new VoiceItAPI2(apiKey, apiToken);
        mActivity = this;
        mJSONResponseTextView = (TextView) findViewById(R.id.json_response_text_view);

        /*myVoiceIt2.groupExists(groupId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG,"JSONResult groupexists: "+ response.toString());
                try {
                    groupExists = (Boolean) response.get("exists");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) {
                    mJSONResponseTextView.setText("JSONResult groupExists: " + errorResponse.toString());
                }
            }
        });
*/
        mAddUserButton = (Button) findViewById(R.id.add_user_button);
        mAddUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddUserActivity.class);
                /*Gson gson = new Gson();
                intent.putExtra("myVoiceIt2", gson.toJson(myVoiceIt2));
                intent.putExtra("groupId", groupId);
                startActivity(intent);*/
                intent.putExtra("tokenKey", apiToken);
                intent.putExtra("apiKey", apiKey);
                intent.putExtra("groupId", groupId);
                startActivity(intent);
            }
        });
        /*mAddUserButton.setOnClickListener(new View.OnClickListener() {
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
                        enrollmentCount = 0;

                        myVoiceIt2.addUserToGroup(groupId, userID, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Log.d(TAG,"JSONResult addUserToGroup: "+ response.toString());

                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,50);
                                myVoiceIt2.createVoiceEnrollment(userID, "en-US", new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        Log.d(TAG,"JSONResult createVoiceEnrollment1: "+ response.toString());
                                        enrollmentCount++;

                                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,50);
                                        myVoiceIt2.createVoiceEnrollment(userID, "en-US", new JsonHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                Log.d(TAG,"JSONResult createVoiceEnrollment2: "+ response.toString());
                                                enrollmentCount++;

                                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,50);
                                                myVoiceIt2.createVoiceEnrollment(userID, "en-US", new JsonHttpResponseHandler() {
                                                    @Override
                                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                        Log.d(TAG,"JSONResult createVoiceEnrollment3: "+ response.toString());
                                                        enrollmentCount++;
                                                    }

                                                    @Override
                                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                                        if (errorResponse != null) {
                                                            mJSONResponseTextView.setText("JSONResult createVoiceEnrollment3: " + errorResponse.toString());
                                                        }
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                                if (errorResponse != null) {
                                                    mJSONResponseTextView.setText("JSONResult createVoiceEnrollment2: " + errorResponse.toString());
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        if (errorResponse != null) {
                                            mJSONResponseTextView.setText("JSONResult createVoiceEnrollment1: " + errorResponse.toString());
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                if (errorResponse != null) {
                                    mJSONResponseTextView.setText("JSONResult addUserToGroup: " + errorResponse.toString());
                                }
                            }
                        });

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        if (errorResponse != null) {
                            mJSONResponseTextView.setText("JSONResult createUser: " + errorResponse.toString());
                        }
                    }
                });
            }
        });
*/
        mVerifyUserButton = (Button) findViewById(R.id.verify_user_button);
        mVerifyUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,50);
                myVoiceIt2.voiceIdentification(groupId, "en-US", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d(TAG,"JSONResult voiceIdentification: "+ response.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        if (errorResponse != null) {
                            mJSONResponseTextView.setText("JSONResult voiceIdentification: " + errorResponse.toString());
                        }
                    }

                });
            }
        });
    }
}
