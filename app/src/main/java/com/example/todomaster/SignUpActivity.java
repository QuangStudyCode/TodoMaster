package com.example.todomaster;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todomaster.Utils.DatabaseHandler;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "test_db_signup";

    @BindView(R.id.tvBackToLogin)
    TextView tvBackToLogin;

    @BindView(R.id.edtUserSignUp)
    EditText edtUserSignUp;

    @BindView(R.id.edtPassSignUp)
    EditText edtPassSignUp;

    @BindView(R.id.edtComfirmPassSignUp)
    EditText edtComfirmPassSignUp;

    @BindView(R.id.btnSignUp)
    Button btnSignUp;

    @BindView(R.id.imgBackToLogin)
    ImageView imgBackToLogin;


    @BindView(R.id.imgSignUpWithGoogle)
    ImageView imgSignUpWithGoogle;

    DatabaseHandler databaseHandlerUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        databaseHandlerUser = new DatabaseHandler(SignUpActivity.this, "user_db", null, 1);
        databaseHandlerUser.QueryData("CREATE TABLE IF NOT EXISTS AccountUser(Id INTEGER PRIMARY KEY AUTOINCREMENT,Gmail VARCHAR(200),PassWord VARCHAR(200))");
//        databaseHandlerUser.QueryData("INSERT INTO AccountUser VALUES (null,'quangcutee@gmail.com','123456')");
//        databaseHandlerUser.QueryData("INSERT INTO AccountUser VALUES (null,'baquang0411@gmail.com','abcdef')");

        ButterKnife.bind(this);
        tvBackToLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        imgBackToLogin.setOnClickListener(this);
        imgSignUpWithGoogle.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignUp:
                boolean checkInfo = validateInfo(edtUserSignUp, edtPassSignUp, edtComfirmPassSignUp);
                if (checkInfo) {
                    String emailUser = edtUserSignUp.getText().toString();
                    String pass = edtPassSignUp.getText().toString();
//                  lấy các tài khoản trong csdl ra
                    String querryData = "SELECT * FROM AccountUser WHERE Gmail = '" + emailUser + "'";
                    Cursor cursor = databaseHandlerUser.getData(querryData);

                    Log.d(TAG, cursor.toString());

                    if (cursor.getCount() > 0) {
                        Toast.makeText(SignUpActivity.this, "Account already existed!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Register Success!", Toast.LENGTH_SHORT).show();
                        databaseHandlerUser.QueryData("INSERT INTO AccountUser VALUES(null,'" + emailUser + "','" + pass + "')");

//                        put data to extra
                        Intent intent = new Intent();
                        intent.putExtra("userEmail", emailUser);
//                        intent.putExtra("pass", pass);
                        setResult(90, intent);

                    }
                    cursor.close();
                    finish();
                }
                break;
            case R.id.tvBackToLogin:
                finish();
                break;
            case R.id.imgBackToLogin:
                finish();
                break;
            case R.id.imgSignUpWithGoogle:
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

    private boolean validateInfo(EditText edtUserSignUp, EditText edtPassSignUp, EditText edtComfirmPassSignUp) {
        String emailUser = edtUserSignUp.getText().toString();
        String pass = edtPassSignUp.getText().toString();
        String edtComFirm = edtComfirmPassSignUp.getText().toString();
        String regex = "^[a-zA-Z0-9._%+-]+@gmail.com$";

        if (TextUtils.isEmpty(emailUser)) {
            edtUserSignUp.setError("Filed is not empty!");
            return false;
        } else if (TextUtils.isEmpty(pass)) {
            edtPassSignUp.setError("Filed is not empty!");
            return false;
        } else if (TextUtils.isEmpty(edtComFirm)) {
            edtComfirmPassSignUp.setError("Filed is not empty!");
            return false;
        } else if (!emailUser.matches(regex)) {
            edtUserSignUp.setError("Email Invalid!");
            return false;
        } else if (!edtComFirm.equals(pass)) {
            edtComfirmPassSignUp.setError("The filed do not match!");
            return false;
        } else {
            return true;
        }
    }
}