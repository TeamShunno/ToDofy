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
package teamshunno.todofy

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import teamshunno.todofy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        const val NOTIFICATION_CHANNEL_DEFAULT = "default"
    }

    /**
     * Object Declaration
     */
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var sharedPreferences: SharedPreferences

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(
                    this,
                    "Without notification permission some feature not work!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Object Initialization
        sharedPreferences = getSharedPreferences(packageName, MODE_PRIVATE)

        // Data source
        database = ArrayList()

        // Some Garbage Data for Testing
//        database.add("Some Important Task");
//        database.add("More Important Task");
//        database.add("Some Urgent Task");

        // Initialize Adapter
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, database)
        binding.listView.adapter = adapter

        initViews()
        checkNotificationPermission()
    }

    override fun onStart() {
        super.onStart()
        /**
         * Load Data
         */
        val data = sharedPreferences.getString("database", null)
        if (data != null && database.isEmpty()) {
            val gson = Gson()
            val tempData = gson.fromJson(data, Array<String>::class.java)
            database.addAll(listOf(*tempData))
        }
    }

    override fun onPause() {
        super.onPause()
        /**
         * Save Data
         */
        val gson = Gson()
        val data = gson.toJson(database)
        val spe = sharedPreferences.edit()
        spe.putString("database", data)
        spe.apply()
    }

    private fun initViews() {
        /**
         * Click Events Start Here :)
         */
        binding.fabAdd.setOnClickListener {
            val addAlertDialog = MaterialAlertDialogBuilder(this)
            addAlertDialog.setTitle("New To-Do")

            /**
             * Create The View
             */
            val layout = LinearLayout(this)
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layout.layoutParams = params
            layout.orientation = LinearLayout.VERTICAL
            layout.setPadding(dpToPx(20), 0, dpToPx(20), 0)

            val editText = TextInputEditText(this)
            editText.hint = "Write new To-Do here"
            editText.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
            editText.setLines(1)
            editText.maxLines = 1
            editText.setSingleLine()
            layout.addView(editText)

            // Add the view
            addAlertDialog.setView(layout)
            addAlertDialog.setNegativeButton("Cancel") { _, _ ->
                // Nothing will happened!
            }
            addAlertDialog.setPositiveButton("Add") { _, _ ->
                val inputText = editText.text.toString()
                if (!inputText.isEmpty()) {
                    database.add(inputText)
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "Please enter some text!", Toast.LENGTH_LONG)
                        .show()
                }
            }
            addAlertDialog.show()
        }

        /**
         * List View Events
         */
        binding.listView.onItemLongClickListener =
            OnItemLongClickListener { _, _, position, _ ->
                val alertDialog = MaterialAlertDialogBuilder(this)
                alertDialog.setTitle("What do you want?")
                alertDialog.setNegativeButton("Nothing") { _, _ ->
                    // Do Nothing!
                }
                alertDialog.setPositiveButton("Edit") { _, _ ->
                    val editAlertDialog = MaterialAlertDialogBuilder(this)
                    editAlertDialog.setTitle("Edit To-Do")

                    /**
                     * Create The View
                     */
                    val layout = LinearLayout(this)
                    val params = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    layout.layoutParams = params
                    layout.orientation = LinearLayout.VERTICAL
                    layout.setPadding(dpToPx(20), 0, dpToPx(20), 0)

                    val editText = EditText(this)
                    editText.hint = "Write updated To-Do here"
                    editText.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
                    editText.setLines(1)
                    editText.maxLines = 1
                    editText.setSingleLine()
                    editText.setText(database[position])
                    layout.addView(editText)

                    // Add the view
                    editAlertDialog.setView(layout)
                    editAlertDialog.setNegativeButton("Cancel") { _, _ ->
                        // Nothing will happened!
                    }
                    editAlertDialog.setPositiveButton("Update") { _, _ ->
                        val inputText = editText.text.toString()
                        if (!inputText.isEmpty()) {
                            database[position] = inputText
                            adapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(this, "Please enter some text!", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                    editAlertDialog.show()
                }
                alertDialog.setNeutralButton("Delete") { _, _ ->
                    database.removeAt(position)
                    adapter.notifyDataSetChanged()
                }
                alertDialog.show()
                true
            }

        binding.listView.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            val alertDialog = MaterialAlertDialogBuilder(this)
            alertDialog.setTitle("Show in Notification?")
            alertDialog.setNegativeButton("Cancel") { _, _ -> }
            alertDialog.setPositiveButton("Yes") { _, _ ->
                val notificationManager = NotificationManagerCompat.from(this)

                // Crate channel if not already created.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                    notificationManager
                        .getNotificationChannel(NOTIFICATION_CHANNEL_DEFAULT) == null
                ) {
                    val channel = NotificationChannel(
                        NOTIFICATION_CHANNEL_DEFAULT,
                        "General notification",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    channel.description = "All general notifications"
                    channel.setSound(null, null)
                    notificationManager.createNotificationChannel(channel)
                }

                // Initialize the notification.
                val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_DEFAULT)
                notification
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setContentTitle(title)
                    .setContentText(database[position])
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(database[position])
                    )

                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, database[position])
                sendIntent.type = "text/plain"

                val pendingShareIntent = PendingIntent.getActivity(
                    this,
                    0,
                    Intent.createChooser(sendIntent, "Share To Do..."),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val action = NotificationCompat.Action(
                    R.drawable.ic_share_black_24dp,
                    "Share",
                    pendingShareIntent
                )
                notification.addAction(action)

                // Show notification
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    checkNotificationPermission()

                    Toast.makeText(
                        this,
                        "Notification permission is not granted.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    notificationManager.notify(position, notification.build())
                }
            }

            alertDialog.show()
        }

        /**
         * About
         */
        binding.fabAbout.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(this)
            dialog.setTitle(title)
                .setMessage(
                    """
    Version ${BuildConfig.VERSION_NAME}
    
    A simple todo list app for the workshop
    "Make an Android App with Us"
    conducted by Team Shunno.
    
    facebook.com/TeamShunno
    Copyright © Team Shunno.
                    """.trimIndent()
                )
            dialog.setNegativeButton(getString(android.R.string.ok)) { _, _ -> }
            dialog.show()
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // You can use the API that requires the permission.
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // In an educational UI, explain to the user why your app requires this
                    // permission for a specific feature to behave as expected, and what
                    // features are disabled if it's declined. In this UI, include a
                    // "cancel" or "no thanks" button that lets the user continue
                    // using your app without granting the permission.
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("Notification permission")
                    builder.setMessage(
                        "Please grant notification permission to show notifications."
                    )
                    builder.setPositiveButton("Ok") { _, _ ->
                        requestNotificationPermissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    }
                    builder.setNegativeButton("Cancel") { _, _ ->
                        /* no-op */
                    }
                    val dialog = builder.create()
                    dialog.show()
                }

                else -> {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestNotificationPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                }
            }
        }
    }

    /**
     * Utility Functions
     * From:
     * https://stackoverflow.com/a/5255256/2263329
     */
    private fun dpToPx(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}
