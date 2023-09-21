package com.example.todomaster;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.todomaster.Adapter.TodoAdapter;
import com.example.todomaster.Model.TodoModel;
import com.example.todomaster.Utils.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MYTAG";
    RecyclerView recyclerView;

    private ArrayList<TodoModel> todoModelArrayList;

    private TodoAdapter todoAdapter;
    DatabaseHandler databaseHandler;

    @BindView(R.id.fabAdd)
    FloatingActionButton fabAdd;

    @BindView(R.id.imgSetting)
    ImageView imgSetting;

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        recyclerView = findViewById(R.id.rcvMain);

//      inflate database
        databaseHandler = new DatabaseHandler(MainActivity.this, "todo_db", null, 1);
        databaseHandler.QueryData("CREATE TABLE IF NOT EXISTS TodoList(Id INTEGER PRIMARY KEY AUTOINCREMENT, Status INTEGER, task VARCHAR(200))");
//        databaseHandler.QueryData("INSERT INTO TodoList VALUES(null, 1,'Do my homework')");
//        databaseHandler.QueryData("INSERT INTO TodoList VALUES(null, 2,'Do my homework 1')");
//        databaseHandler.QueryData("INSERT INTO TodoList VALUES(null, 3,'Do my homework 2')");

        todoModelArrayList = new ArrayList<>();
        todoAdapter = new TodoAdapter(MainActivity.this, todoModelArrayList);

        displayData();

        recyclerView.setAdapter(todoAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

//        fabCLick
        fabAdd.setOnClickListener(this);

//        imgSetting click
        imgSetting.setOnClickListener(this);

//        mediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.ting_sound);

//        swipe
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.Callback() {
                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        if (dX > 0) {
                            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                                    .addBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.orange))
                                    .addActionIcon(R.drawable.ic_edit)
                                    .addSwipeRightLabel("Edit")
                                    .setSwipeRightLabelColor(R.color.white)
                                    .create()
                                    .decorate();
                        } else if (dX < 0) {
                            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                                    .addBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red))
                                    .addActionIcon(R.drawable.ic_trash)
                                    .addSwipeLeftLabel("Delete")
                                    .setSwipeLeftLabelColor(R.color.white)
                                    .create()
                                    .decorate();
                        }
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }

                    // Hướng vuốt item
                    @Override
                    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                        return makeMovementFlags(
                                ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END
                                , ItemTouchHelper.END | ItemTouchHelper.START
                        );
                    }

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                        int fromPosition = viewHolder.getAdapterPosition();
//                        int toPosition = target.getAdapterPosition();
//
//                        TodoModel movedItem = todoModelArrayList.get(fromPosition);
//                        todoModelArrayList.remove(fromPosition);
//                        todoModelArrayList.add(toPosition, movedItem);
//
//                        int newPosition = (fromPosition + toPosition) / 2;
//
//                        TodoModel todoModel = todoModelArrayList.get(newPosition);
//                        int idItem = todoModel.getId();
//
//                        databaseHandler.QueryData("UPDATE TodoList SET order_field = " + newPosition + " WHERE Id = " + idItem);
//                        todoAdapter.notifyItemMoved(fromPosition, toPosition);
                        return true;
                    }

                    //Xử lý hàm vuốt
                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        switch (direction) {
                            case ItemTouchHelper.END:
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                                LayoutInflater layoutInflater = getLayoutInflater();
                                View dialogViewUpdate = layoutInflater.inflate(R.layout.custom_alertdialog_update_task, null);

                                builder1.setView(dialogViewUpdate);
                                AlertDialog alertDialogUpdate = builder1.create();
                                alertDialogUpdate.show();

//                                declare
                                Button buttonCancelUpdate = dialogViewUpdate.findViewById(R.id.btnCustomAlertUpdateCanel);
                                Button buttonUpdate = dialogViewUpdate.findViewById(R.id.btnCustomAlertUpdateDelete);
                                EditText edtCustomAlertUpdate = dialogViewUpdate.findViewById(R.id.edtCustomAlertUpdate);


//                               get id&task
                                int possittion = viewHolder.getAdapterPosition();
                                TodoModel todoModel1 = todoModelArrayList.get(possittion);
                                int idItem1 = todoModel1.getId();

                                edtCustomAlertUpdate.setText(todoModel1.getTask().toString());

                                buttonCancelUpdate.setOnClickListener(view -> {
                                    alertDialogUpdate.dismiss();
                                    displayData();
                                });

                                buttonUpdate.setOnClickListener(view -> {
                                    String newtask = edtCustomAlertUpdate.getText().toString();
                                    if (TextUtils.isEmpty(newtask)) {
                                        Toast.makeText(getApplicationContext(), "Field can not be empty!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    databaseHandler.QueryData("UPDATE TodoList SET task = '" + newtask + "'WHERE Id = '" + idItem1 + "'");
                                    Toast.makeText(getApplicationContext(), "Update completed!", Toast.LENGTH_SHORT).show();
                                    alertDialogUpdate.dismiss();
                                    displayData();
                                });
                                break;

                            case ItemTouchHelper.START:
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                LayoutInflater inflater = getLayoutInflater();
                                View dialogView = inflater.inflate(R.layout.custom_alertdialog_delete_task, null);

                                builder.setView(dialogView);
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();

//                              declare
                                Button buttonCancel = dialogView.findViewById(R.id.btnCustomAlertCanel1);
                                Button buttonDelete = dialogView.findViewById(R.id.btnCustomAlertDelete1);
                                buttonDelete.setOnClickListener(view -> {
//                                        lấy ra vị trí trong ds liên kết với RecycleView
                                    int possition = viewHolder.getAdapterPosition();
//                                        khởi tạo đối tượng sau đó get các thuộc tính cần thiết
                                    TodoModel todoModel = todoModelArrayList.get(possition);
                                    int idItem = todoModel.getId();

//                                        execute the command query
                                    databaseHandler.QueryData("DELETE FROM TodoList WHERE ID = '" + idItem + "'");

                                    //remove item in todoModelArrayList
                                    todoModelArrayList.remove(viewHolder.getAdapterPosition());
                                    todoAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                                    displayData();
                                    Toast.makeText(getApplicationContext(), "Delete success!", Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                });
                                buttonCancel.setOnClickListener(view -> {
                                    alertDialog.dismiss();
                                    displayData();
                                });
                                break;
                        }
                    }
                }

        );
        helper.attachToRecyclerView(recyclerView);

        sendNotifyCation();
    }

    private void displayData() {
        Cursor cursor = databaseHandler.getData("SELECT * FROM TodoList");
        todoModelArrayList.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            int status = cursor.getInt(1);
            String task = cursor.getString(2);
            todoModelArrayList.add(new TodoModel(id, status, task));
        }
        todoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabAdd:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater layoutInflater = this.getLayoutInflater();
                View dialogView = layoutInflater.inflate(R.layout.custom_alertdialog_add_task, null);

//                Phải set view và giá trị trước khi create
                builder.setView(dialogView);
                builder.setTitle("Add new task");
                builder.setIcon(R.drawable.ic_task1);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                EditText edtCustomAlert = dialogView.findViewById(R.id.edtCustomAlert);
                Button btnCustomAlertSave = dialogView.findViewById(R.id.btnCustomAlertSave);
                Button btnCustomAlertCancel = dialogView.findViewById(R.id.btnCustomAlertCanel);
                edtCustomAlert.requestFocus();

                btnCustomAlertSave.setOnClickListener(view1 -> {
                    String newTask = edtCustomAlert.getText().toString();

                    if (TextUtils.isEmpty(newTask)) {
                        Toast.makeText(MainActivity.this, "Không được để trống!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    databaseHandler.QueryData("INSERT INTO TodoList VALUES(null,0,'" + newTask + "')");
                    displayData();
                    alertDialog.dismiss();

                });
                btnCustomAlertCancel.setOnClickListener(view1 -> {
                    alertDialog.dismiss();
                });
                break;

            case R.id.imgSetting:
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, imgSetting);
                popupMenu.getMenuInflater().inflate(R.menu.custom_popup_main, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        databaseHandler.QueryData("DELETE FROM TodoList");
                        Toast.makeText(getApplicationContext(), "Deleted all tasks!", Toast.LENGTH_SHORT).show();
                        displayData();
                        return true;
                    }
                });
                popupMenu.show();
                break;
        }
    }

    private static final String CHANNEL_ID = "todo_channel";
    private static final int NOTIFICATION_ID = 1;

    @SuppressLint("MissingPermission")
    private void sendNotifyCation() {
        Intent intent4 = new Intent(this, MainActivity.class);
        intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent4, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Add your new tasks")
                .setContentText("Enjoy what you do")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        createNotificationChannel();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void UpdateStatusForTask(int id, int newStatus) {
        databaseHandler.QueryData("UPDATE TodoList SET status = '" + newStatus + "' WHERE Id = '" + id + "'");
    }

    public void MakeSoundForTask(boolean status) {
        if (status) {
            mediaPlayer.start();
        } else {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Exit");
        builder.setMessage("Do you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
            }
        });
        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.test);
            String description = getString(R.string.test);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}


