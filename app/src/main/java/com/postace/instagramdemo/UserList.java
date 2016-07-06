package com.postace.instagramdemo;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class UserList extends AppCompatActivity implements AdapterView.OnItemClickListener {

    static final int PICK_IMAGE_REQUEST = 1;
    ListView listUser;
    ArrayList<String> userArr;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        setTitle("User List");

        listUser = (ListView) findViewById(R.id.list_user);
        userArr = new ArrayList<>();
        adapter = new ArrayAdapter<>(UserList.this, android.R.layout.simple_list_item_1, userArr);
        listUser.setAdapter(adapter);
        listUser.setOnItemClickListener(this);

        Intent i = getIntent();
        displayListUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {                    // button log out tapped
            ParseUser.logOut();
            Intent i = new Intent(UserList.this, MainActivity.class);
            startActivity(i);
        }
        else if (id == R.id.share) {                // button share tapped
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, PICK_IMAGE_REQUEST);
        }
        return super.onOptionsItemSelected(item);
    }

    // displaying list of user register, except logged user
    public void displayListUser() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        for (ParseUser usr : list) {
                            userArr.add(usr.getUsername());
                        }
                        adapter.notifyDataSetChanged();
                    }
                } else  {
                    showToast("An error occurred!Please try again!");
                }
            }
        });
    }

    // displaying the Toast message
    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    // get result after pick an image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    // compress the image
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    ParseFile file = new ParseFile("image.png", byteArray);
                    uploadImg(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // uploading image to user account
    public void uploadImg(ParseFile file) {
        ParseObject object = new ParseObject("Images");
        object.put("username", ParseUser.getCurrentUser().getUsername());
        object.put("image", file);
        // set Access control
        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        object.setACL(acl);
        // send data to server
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    showToast("Share successful!");
                } else {
                    showToast("An error occurred!Please try again!");
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(UserList.this, UserFeed.class);
        i.putExtra("username", userArr.get(position));
        startActivity(i);
    }
}
