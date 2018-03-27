/*
 * Copyright (C) 2018 Team Shunno Open Source Project
 *
 * Licensed under the Mozilla Public License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.mozilla.org/en-US/MPL/2.0/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * - Team Shunno
 *   https://www.facebook.com/TeamShunno/
 */

package teamshunno.todofy;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    /**
     * Object Declaration
     */
    ListView listView;

    FloatingActionButton fabAdd, fabAbout;

    Context mContext;

    ArrayList<String> database;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Object Initialization
        mContext = MainActivity.this;

        listView = findViewById(R.id.listView);

        fabAdd = findViewById(R.id.fabAdd);
        fabAbout = findViewById(R.id.fabAbout);

        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        // Data source
        database = new ArrayList<>();

        // Some Garbage Data for Testing
//        database.add("Some Important Task");
//        database.add("More Important Task");
//        database.add("Some Urgent Task");

        // Initialize Adapter
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, database);

        listView.setAdapter(adapter);

        /**
         * Click Events Start Here :)
         */
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder adbAdd = new AlertDialog.Builder(mContext);
                adbAdd.setTitle("New To-Do");

                /**
                 * Create The View
                 */
                LinearLayout layout = new LinearLayout(mContext);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layout.setLayoutParams(params);

                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(dpToPx(20), 0, dpToPx(20), 0);

                final EditText editText = new EditText(mContext);
                editText.setHint("Write new To-Do here");
                editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                editText.setLines(1);
                editText.setMaxLines(1);
                editText.setSingleLine();

                layout.addView(editText);

                // Add the view
                adbAdd.setView(layout);

                adbAdd.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nothing will happened!
                    }
                });
                adbAdd.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputText = editText.getText().toString();

                        if (!inputText.isEmpty()) {
                            database.add(inputText);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(mContext, "Please enter some text!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                adbAdd.show();

            }
        });

        /**
         * List View Events
         */
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder adbItemLong = new AlertDialog.Builder(mContext);

                adbItemLong.setTitle("What do you want?");
                adbItemLong.setNegativeButton("Nothing", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do Nothing!
                    }
                });
                adbItemLong.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AlertDialog.Builder adbAdd = new AlertDialog.Builder(mContext);
                        adbAdd.setTitle("Edit To-Do");

                        /**
                         * Create The View
                         */
                        LinearLayout layout = new LinearLayout(mContext);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layout.setLayoutParams(params);

                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setPadding(dpToPx(20), 0, dpToPx(20), 0);

                        final EditText editText = new EditText(mContext);
                        editText.setHint("Write updated To-Do here");
                        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                        editText.setLines(1);
                        editText.setMaxLines(1);
                        editText.setSingleLine();

                        editText.setText(database.get(position));

                        layout.addView(editText);

                        // Add the view
                        adbAdd.setView(layout);

                        adbAdd.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Nothing will happened!
                            }
                        });
                        adbAdd.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String inputText = editText.getText().toString();

                                if (!inputText.isEmpty()) {
                                    database.set(position, inputText);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(mContext, "Please enter some text!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        adbAdd.show();

                    }
                });
                adbItemLong.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        database.remove(position);
                        adapter.notifyDataSetChanged();

                    }
                });
                adbItemLong.show();

                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder single_click_adb = new AlertDialog.Builder(mContext);
                single_click_adb.setTitle("Show in Notification?");
                single_click_adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                single_click_adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext, "default-channel");
                        notification
                                .setSmallIcon(R.drawable.ic_ts_todofy_white)
                                .setColor(getResources().getColor(R.color.colorPrimary))
                                .setContentTitle(getTitle())
                                .setContentText(database.get(position))
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(database.get(position)));

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, database.get(position));
                        sendIntent.setType("text/plain");

                        PendingIntent pendingShareIntent = PendingIntent.getActivity(mContext, 0, Intent.createChooser(sendIntent, "Share To Do..."),
                                PendingIntent.FLAG_UPDATE_CURRENT);


                        NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_share_black_24dp, "Share", pendingShareIntent);
                        notification.addAction(action);

                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);

                        notificationManagerCompat.notify(position, notification.build());

                    }
                });
                single_click_adb.show();
            }
        });

        /**
         * About
         */
        fabAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
                adb.setTitle(getTitle())
                        .setMessage("Version " + BuildConfig.VERSION_NAME + "\n\n" +
                                "A simple todo list app for the workshop\n\"Make an Android App with Us\"\n" +
                                "conducted by Team Shunno.\n\n" +
                                "facebook.com/TeamShunno\n" +
                                "Copyright © Team Shunno.");
                adb.setNegativeButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                adb.show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        /**
         * Load Data
         */
        String data = sharedPreferences.getString("database", null);

        if (data != null && database.isEmpty()) {
            Gson gson = new Gson();
            String[] tempData = gson.fromJson(data, String[].class);

            database.addAll(Arrays.asList(tempData));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * Save Data
         */
        Gson gson = new Gson();

        String data = gson.toJson(database);

        SharedPreferences.Editor spe = sharedPreferences.edit();

        spe.putString("database", data);
        spe.apply();

    }

    /**
     * Utility Functions
     * From:
     * https://stackoverflow.com/a/5255256/2263329
     */
    public int dpToPx(int dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
