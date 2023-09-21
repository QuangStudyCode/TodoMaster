package com.example.todomaster;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todomaster.Adapter.ExpanableListViewAdapter;
import com.example.todomaster.Utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "check_db";

    @BindView(R.id.tvLoginToSignUp)
    TextView tvLoginToSignUp;

    @BindView(R.id.edtUserLogin)
    EditText edtUserLogin;

    @BindView(R.id.edtPassLogin)
    EditText edtPassLogin;

    @BindView(R.id.btnLogin)
    Button btnLogin;

    @BindView(R.id.imgBackSplash)
    ImageView imgBackSplash;

    @BindView(R.id.cbLogin)
    CheckBox cbLogin;

    @BindView(R.id.tvHelpCenterLogin)
    TextView tvHelpCenterLogin;

    @BindView(R.id.imgLoginWithGoogle)
    ImageView imgLoginWithGoogle;

    DatabaseHandler databaseHandler;

    //  for center helper
    private List<String> questionsList;
    private HashMap<String, List<String>> questionGroup;
    private ExpanableListViewAdapter expanableListViewAdapter;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult()
            , new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 90) {
                        Intent intent = result.getData();
                        if (intent != null) {
                            String userEmail = intent.getStringExtra("userEmail");
//                            Log.d("check_db", userEmail.toString());
                            edtUserLogin.setText(userEmail);
                        }
                    }
                }
            });
    //    share preference
    private static final String SHARE_PREFER_NAME = "login_pref";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//       kết nối lại với user_db
        databaseHandler = new DatabaseHandler(LoginActivity.this, "user_db", null, 1);

        ButterKnife.bind(this);
        tvLoginToSignUp.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        imgBackSplash.setOnClickListener(this);
        tvHelpCenterLogin.setOnClickListener(this);
        imgLoginWithGoogle.setOnClickListener(this);
//        checkbox
        cbLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                saveLoginCredentials(edtUserLogin, edtPassLogin);
            }
        });

        checkLoginStatus();
    }

    private void checkLoginStatus() {
        sharedPreferences = getSharedPreferences(SHARE_PREFER_NAME, MODE_PRIVATE);
        boolean isRemember = sharedPreferences.getBoolean("isRemembered", false);
        if (isRemember) {
            String userEmail = sharedPreferences.getString(KEY_EMAIL, "");
            String userPass = sharedPreferences.getString(KEY_PASSWORD, "");

            edtUserLogin.setText(userEmail);
            edtPassLogin.setText(userPass);
            cbLogin.setChecked(true);
        }
    }

    private void saveLoginCredentials(EditText edtUserLogin, EditText edtPassLogin) {
        sharedPreferences = getSharedPreferences(SHARE_PREFER_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String userEmail = edtUserLogin.getText().toString();
        String userPass = edtPassLogin.getText().toString();
//        put to shapreperences
        editor.putString(KEY_EMAIL, userEmail);
        editor.putString(KEY_PASSWORD, userPass);
        editor.putBoolean("isRemembered", cbLogin.isChecked());

        editor.apply();
    }


    //    validate đầu vào
    //    Check tài khoản đã tồn tại chưa. nếu đã tồn tại thì đăng nhập vào hệ thống
    @SuppressLint("ResourceType")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                boolean checkInfo = validateInforLogin(edtUserLogin, edtPassLogin);
                String emailUser = edtUserLogin.getText().toString();
                String pass = edtPassLogin.getText().toString();

                if (checkInfo) {
//                  truy vấn trả về kqua SELECT
                    String query = "SELECT * FROM AccountUser WHERE Gmail = '" + emailUser + "' AND PassWord = '" + pass + "'";
                    Cursor cursor = databaseHandler.getData(query);

                    if (cursor != null && cursor.moveToFirst()) {
                        String name = cursor.getString(1);
                        String pass2 = cursor.getString(2);
                        Toast.makeText(LoginActivity.this, name + "/" + pass2, Toast.LENGTH_SHORT).show();

                        if (emailUser.equals(name) && pass.equals(pass2)) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                        cursor.close(); // Đóng con trỏ sau khi sử dụng
                    } else {
                        Toast.makeText(LoginActivity.this, "Login fail", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.imgBackSplash:
                finish();
                break;

            case R.id.tvLoginToSignUp:
                Intent intentToSignUp = new Intent(LoginActivity.this, SignUpActivity.class);
                activityResultLauncher.launch(intentToSignUp);
                break;
            case R.id.tvHelpCenterLogin:
                PopupMenu popupMenu = new PopupMenu(LoginActivity.this, tvHelpCenterLogin);
                popupMenu.getMenuInflater().inflate(R.menu.custom_popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.itemFaqPopUp:
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                                LayoutInflater inflater = getLayoutInflater();
                                View dialogView1 = inflater.inflate(R.layout.custom_alertdialog_faq, null);
                                builder1.setView(dialogView1);

                                ExpandableListView expandableListView = dialogView1.findViewById(R.id.expandableLvFAQ);
                                questionsList = new ArrayList<String>();
                                questionGroup = new HashMap<String, List<String>>();
                                inflateData(questionsList, questionGroup);

                                expanableListViewAdapter = new ExpanableListViewAdapter(LoginActivity.this, questionsList, questionGroup);
                                expandableListView.setAdapter(expanableListViewAdapter);

                                AlertDialog dialog = builder1.create();
                                dialog.show();
                                break;
                            case R.id.itemSendFbPopUp:
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                LayoutInflater layoutInflater = getLayoutInflater();
                                View dialogView = layoutInflater.inflate(R.layout.custom_alertd_dialog_sendfeedback, null);
                                builder.setView(dialogView);
                                AlertDialog alertDialog = builder.create();

                                Button btnCanel = dialogView.findViewById(R.id.btnCanelSendFeedBack);
                                Button btnSend = dialogView.findViewById(R.id.btnSendFeedBack);

                                btnCanel.setOnClickListener(view1 -> {
                                    alertDialog.dismiss();
                                });
                                btnSend.setOnClickListener(view1 -> {
                                    alertDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Cảm ơn bạn đã gửi feedback cho chúng tôi !", Toast.LENGTH_SHORT).show();
                                });

                                alertDialog.show();
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
                break;

            case R.id.imgLoginWithGoogle:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater layoutInflater = getLayoutInflater();
                View viewGoogle = layoutInflater.inflate(R.layout.layout_login_google, null);
                builder.setView(viewGoogle);
                AlertDialog alertDialog = builder.create();

                TextView tvDismiss = viewGoogle.findViewById(R.id.tvDismissGoogle);
                tvDismiss.setOnClickListener(view1 -> {
                    alertDialog.dismiss();
                });
                alertDialog.show();
                break;
        }
    }

    private void inflateData(List<String> questionsList, HashMap<String, List<String>> questionGroup) {
        questionsList.add("1.TodoMaster là gì?");
        questionsList.add("2.Tại sao nên sử dụng TodoMaster?");
        questionsList.add("3.Làm thế nào để sử dụng TodoMaster?");


        List<String> detailQuestion1 = new ArrayList<>();
        detailQuestion1.add("- Là một ứng dụng giúp bạn quản lý danh sách công việc, nhiệm vụ, hay việc cần hoàn thành trong cuộc sống hàng ngày.");


        List<String> detailQuestion2 = new ArrayList<>();
        detailQuestion2.add("- Sử dụng TodoMaster giúp bạn tổ chức công việc, theo dõi tiến độ và đảm bảo không bỏ sót các nhiệm vụ quan trọng.");


        List<String> detailQuestion3 = new ArrayList<>();
        detailQuestion3.add("- Đầu tiên, tải và cài đặt ứng dụng trên CH Play. Sau đó mở ứng dụng và bắt đầu tạo danh sách nhiệm vụ hoặc công việc của bạn. Nhập công việc cần phải làm và khi hoàn thành hãy đánh dấu là đã hoàn thành hoặc xóa bỏ nhiệm vụ.");

        questionGroup.put(questionsList.get(0), detailQuestion1);
        questionGroup.put(questionsList.get(1), detailQuestion2);
        questionGroup.put(questionsList.get(2), detailQuestion3);

    }

    private boolean validateInforLogin(EditText edtUserLogin, EditText edtPassLogin) {
        String emailUser = edtUserLogin.getText().toString();
        String pass = edtPassLogin.getText().toString();
        String regex = "^[a-zA-Z0-9._%+-]+@gmail.com$";

        if (TextUtils.isEmpty(emailUser)) {
            edtUserLogin.setError("Filed is not empty!");
            return false;
        } else if (TextUtils.isEmpty(pass)) {
            edtPassLogin.setError("Filed is not empty!");
            return false;
        } else if (!emailUser.matches(regex)) {
            edtUserLogin.setError("Invalid Email!");
            return false;
        } else if (pass.length() < 6) {
            edtPassLogin.setError("Password at least 6 characters!");
            return false;
        } else {
            return true;
        }

    }
}