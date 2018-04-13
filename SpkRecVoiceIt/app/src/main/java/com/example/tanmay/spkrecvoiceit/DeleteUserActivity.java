package com.example.tanmay.spkrecvoiceit;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.voiceit.voiceit2.VoiceItAPI2;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class DeleteUserActivity extends AppCompatActivity {
    private static final String TAG = "DeleteUserActivity";
    public VoiceItAPI2 myVoiceIt2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);

        Bundle extras = getIntent().getExtras();
        myVoiceIt2 = new VoiceItAPI2(extras.getString("apiKey"), extras.getString("tokenKey"));

        List<User> data = fill_with_data();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        UserAdapter adapter = new UserAdapter(data, getApplication(), recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public List<User> fill_with_data() {
        List<User> data = new ArrayList<User>();
        DBHelper dbHelper = new DBHelper(DeleteUserActivity.this);

        Cursor res = dbHelper.getAllUsersData();
        res.moveToFirst();

        User temp;
        for(int i = 0 ; i < res.getCount(); i++){
            temp = new User(res.getString(res.getColumnIndex("userId")), res.getString(res.getColumnIndex("user_name")));
            data.add(temp);
            res.moveToNext();
        }

        return data;
    }
}
