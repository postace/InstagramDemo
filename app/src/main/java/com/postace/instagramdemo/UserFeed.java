package com.postace.instagramdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class UserFeed extends AppCompatActivity {

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);

        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        Intent i = getIntent();

        String username = i.getStringExtra("username");
        setTitle(username + "'s Feed");
        getUserImage(username);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // retrieve user's image info
    public void getUserImage(String username) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Images");
        query.whereEqualTo("username", username);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e== null) {
                    if (list.size() > 0) {
                        displayImage(list);
                    } else {
                        showToast("No image found!");
                    }
                } else {
                    showToast("An error occurred!Please refresh the page!");
                }
            }
        });
    }

    // downloading image and showing in UI
    public void displayImage(List<ParseObject> list) {
        for (ParseObject obj : list) {
            ParseFile file = (ParseFile) obj.get("image");
            file.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e==null) {
                        Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        ImageView imageView = new ImageView(getApplicationContext());
                        imageView.setImageBitmap(image);
                        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
                        linearLayout.addView(imageView);
                    }
                }
            });
        }
    }

    // displaying the Toast message
    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
