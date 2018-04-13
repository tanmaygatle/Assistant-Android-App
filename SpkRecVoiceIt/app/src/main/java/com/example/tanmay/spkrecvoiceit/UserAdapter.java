package com.example.tanmay.spkrecvoiceit;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.voiceit.voiceit2.VoiceItAPI2;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<User> mUserList;
    private Context mContext;
    private RecyclerView mRecyclerV;
    private VoiceItAPI2 myVoiceIt2 = new VoiceItAPI2("key_b030846efc8a4335912cad8efea9d539","tok_ead7e4b09e2b4b27b79280456008ef4c");

    private static final String TAG = "UserAdapter";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView UserNameTextView;
        public TextView UserIdTextView;
        public Button DeleteUserButton;

        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            UserNameTextView = (TextView) v.findViewById(R.id.username_text_view);
            UserIdTextView = (TextView) v.findViewById(R.id.userid_text_view);
            DeleteUserButton = (Button) v.findViewById(R.id.list_delete_user_button);
        }
    }

    public void add(int position, User user) {
        mUserList.add(position, user);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        mUserList.remove(position);
        notifyItemRemoved(position);
    }



    // Provide a suitable constructor (depends on the kind of dataset)
    public UserAdapter(List<User> myDataset, Context context, RecyclerView recyclerView) {
        mUserList = myDataset;
        mContext = context;
        mRecyclerV = recyclerView;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.single_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final User user = mUserList.get(position);
        holder.UserNameTextView.setText(" "+ user.getUserName());
        holder.UserIdTextView.setText(" " + user.getId());

        holder.DeleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper dbHelper = new DBHelper(mContext);
                dbHelper.deleteUser(user.getId());
                dbHelper.deleteUserPermissions(user.getId());

                deleteUser(user.getId());

                mUserList.remove(position);
                mRecyclerV.removeViewAt(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mUserList.size());
                notifyDataSetChanged();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public void deleteUser(String userId) {
        myVoiceIt2.deleteUser(userId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG,"JSONResult deleteduser: "+ response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) {
//                    Toast.makeText(,"JSONResult deleteduser: " + errorResponse.toString(),Toast.LENGTH_LONG).show();
                    Log.d(TAG,"JSONResult deleteduser: "+ errorResponse.toString());
                }
            }
        });
    }
}