package com.example.jvhe.petus.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jvhe.petus.Class.HttpAsync;
import com.example.jvhe.petus.R;
import com.example.jvhe.petus.Class.StaticData;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "LoginActivity";

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private TextView mStatusTextView;
    private TextView mDetailTextView;


    Button btn_find_password, btn_login, btn_register;
    private static final int REQUEST_CODE_SIGN_UP = 12;

    // 비밀번호 보기 체크박스, 비밀번호 보기 텍스트뷰 선언
    CheckBox chk_show_password;
    TextInputEditText et_email, et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        mStatusTextView = findViewById(R.id.title);
        mDetailTextView = findViewById(R.id.detail);


        // Button listeners
        findViewById(R.id.google_sign_in_button).setOnClickListener(this);
        findViewById(R.id.google_sign_out_button).setOnClickListener(this);
//        findViewById(R.id.disconnect_button).setOnClickListener(this);


        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]


        // 액션바 감추기
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // BtnOnClickListener 객체 생성
        BtnOnClickListener btnOnClickListener = new BtnOnClickListener();
        // ChkOnClickListener 객체 생성
        ChkOnClickListener chkOnClickListener = new ChkOnClickListener();

        // 버튼들과 레이아웃 매칭 그리고 리스너 설정
        btn_find_password = (Button) findViewById(R.id.btn_find_password);
        btn_find_password.setOnClickListener(btnOnClickListener);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(btnOnClickListener);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(btnOnClickListener);

        // email, pw 입력란 레이아웃 매칭
        et_email = (TextInputEditText) findViewById(R.id.et_email);
        et_password = (TextInputEditText) findViewById(R.id.et_password);

        // 체크박스와 텍스트뷰 매칭
        chk_show_password = (CheckBox) findViewById(R.id.chk_show_password);
        //  tv_show_password = (TextView) findViewById(R.id.tv_show_password);
        // 체크박스 리스너 설정
        chk_show_password.setOnClickListener(chkOnClickListener);

    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI();
    }
    // [END on_start_check_user]


    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                SharedPreferences pref = getSharedPreferences("current_user", MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean("is_login", false);
                edit.putInt("id_db", -1);
                edit.putString("email", "");
                edit.putString("name", "");
                edit.putString("password", "");
                edit.apply();
                updateUI();
                // [END_EXCLUDE]
            }
        }
        // 회원가입으로부터 로그인 요청을 받을 경우
        else if (requestCode == REQUEST_CODE_SIGN_UP) {
            if (resultCode == RESULT_OK) {
                // 회원가입으로부터 받아온 결과
                if (data.getBooleanExtra("flag_login", false)) {
                    et_email.setText(data.getStringExtra("email"));
                    et_password.setText(data.getStringExtra("password"));
                    btn_login.callOnClick();
                }
            }
        }
    }
    // [END onactivityresult]


    //버튼 처리를 담당하는 BtnOnClickListener 클래스 선언
    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(android.view.View view) {
            switch (view.getId()) {
                // 비밀번호 찾기 버튼
                case R.id.btn_find_password:
                    Toast.makeText(getApplicationContext(), "비밀번호 찾기 버튼 눌림!", Toast.LENGTH_SHORT).show();
                    //    startActivityForResult(new Intent(getApplicationContext(), FindPassword.class), 1);
                    break;
                // 로그인 버튼
                case R.id.btn_login:
                    Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                    if (et_email.length() == 0) {
                        et_email.startAnimation(shake);
                        Toast.makeText(getApplicationContext(), "아이디를 입력해 주세요.", Toast.LENGTH_SHORT).show();

                    } else if (et_password.length() == 0) {
                        et_password.startAnimation(shake);
                        Toast.makeText(getApplicationContext(), "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();

                    } else {
//                        mySQLiteOpenHelper = new MySQLiteOpenHelper(getApplicationContext());
//                        sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
//                        query = "SELECT * FROM user WHERE email = '" + et_email.getText().toString() + "' AND password = '" + et_password.getText().toString() + "'";
//                        cursor = sqLiteDatabase.rawQuery(query, null);
//
//                        // 로그인 성공
//                        if (cursor.moveToFirst()) {
//                            Intent data_arr = new Intent();
//
//                            data_arr.putExtra("user_id", cursor.getInt(0));
//                            data_arr.putExtra("user_email", et_email.getText().toString());
//                            data_arr.putExtra("user_nickname", cursor.getString(3));
//                            setResult(RESULT_OK, data_arr);
//                            finish();
//                        }
//                        // 아이디 또는 비밀번호가 맞지 않을 경우
//                        else {
//                            Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show();
//                        }

                        String data = "email=" + et_email.getText().toString() + "&password=" + et_password.getText().toString();
                        String url = StaticData.url + "login_android.php";
                        HttpAsync httpasync = new HttpAsync(data, url, handler);
                        httpasync.execute();
                    }
                    break;
                // 회원가입 버튼
                case R.id.btn_register:
                    //Toast.makeText(getApplicationContext(), "회원가입 버튼 눌림!", Toast.LENGTH_SHORT).show();
                    startActivityForResult(new Intent(getApplicationContext(), SignUpActivity.class), REQUEST_CODE_SIGN_UP);
                    break;
            }
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Toast.makeText(getApplicationContext(), "서버와 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {
                String data = msg.getData().getString("data_arr");
                // Log.e("!!!!!!!!!!!!!", data_arr);
                if (data.contains("!!fail!!")) {
//                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject obj = new JSONObject(data);
                        SharedPreferences pref = getSharedPreferences("current_user", 0);
                        SharedPreferences.Editor edit = pref.edit();
                        edit.putBoolean("is_login", true);
                        edit.putInt("id_db", Integer.parseInt(obj.getString("id")));
                        edit.putString("email", obj.getString("email"));
                        edit.putString("name", obj.getString("name"));
                        edit.putString("password", obj.getString("password"));
                        edit.putString("link_profile", obj.getString("link_profile"));
                        edit.apply();
                        updateUI();
                        Log.e(TAG, "json 로그인 결과: " + data);
                        Toast.makeText(getApplicationContext(), "결과 " + data, Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };


    // 체크박스 처리를 담당하는 ChkOnClickListener 클래스 선언
    class ChkOnClickListener implements CheckBox.OnClickListener {
        @Override
        public void onClick(android.view.View view) {
            switch (view.getId()) {
                // 비밀번호 보기 체크박스
                case R.id.chk_show_password:
                    // 체크박스가 체크되어 있을 경우 입력한 비밀번호를 볼 수 있다.
                    if (((CheckBox) view).isChecked()) {
                        et_password.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                    // 체크박스가 체크되어 있지 않을 경우 패스워드 입력칸의 비밀번호를 볼 수 없다.
                    else {
                        et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    break;
            }
        }
    }


    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog(this);
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();


                            SharedPreferences pref = getSharedPreferences("current_user", 0);
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putBoolean("is_login", true);
                            edit.putInt("id_db", -100);
                            edit.putString("email", user.getEmail());
                            edit.putString("name", user.getDisplayName());
                            edit.putString("password", "구글 토큰");
                            edit.apply();

                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            // 스낵바로 상태 표시.
//                            Snackbar.make(getWindow().getDecorView().getRootView(), "인증 실패", Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(LoginActivity.this, "인증 실패", Toast.LENGTH_SHORT).show();

                            SharedPreferences pref = getSharedPreferences("current_user", MODE_PRIVATE);
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putBoolean("is_login", false);
                            edit.putInt("id_db", -1);
                            edit.putString("email", "");
                            edit.putString("name", "");
                            edit.putString("password", "");
                            edit.apply();
                            updateUI();
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]


    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        SharedPreferences pref = getSharedPreferences("current_user", MODE_PRIVATE);
                        SharedPreferences.Editor edit = pref.edit();
                        edit.putBoolean("is_login", false);
                        edit.putInt("id_db", -1);
                        edit.putString("email", "");
                        edit.putString("name", "");
                        edit.putString("password", "");
                        edit.apply();
                        updateUI();
                    }
                });
    }

    // 회원 탈퇴 기능? 일단은 넣지 말자.
//    private void revokeAccess() {
//        // Firebase sign out
//        mAuth.signOut();
//
//        // Google revoke access
//        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
//                new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        updateUI(null);
//                    }
//                });
//    }

//    private void updateUI(FirebaseUser user) {
//        hideProgressDialog();
//        if (user != null) {
//
//
//            SharedPreferences pref = getSharedPreferences("current_user", 0);
//            mStatusTextView.setText("회원 정보");
//            String text = "이메일: " + pref.getString("email", "") + "\n닉네임: " + pref.getString("name", "");
//            mDetailTextView.setText(text);
////            mDetailTextView.setText(user.getUid());
//            findViewById(R.id.ll_1).setVisibility(View.GONE);
//            findViewById(R.id.ll_2).setVisibility(View.GONE);
////            findViewById(R.id.google_sign_in_button).setVisibility(View.GONE);
//            findViewById(R.id.google_sign_out_button).setVisibility(View.VISIBLE);
//            //            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
//        } else {
//            mStatusTextView.setText(R.string.action_login);
//            mDetailTextView.setText(null);
//
//            SharedPreferences pref = getSharedPreferences("current_user", 0);
//            SharedPreferences.Editor edit = pref.edit();
//            edit.putBoolean("is_login", false);
//            edit.putInt("id_db", -1);
//            edit.putString("email", "");
//            edit.putString("name", "");
//            edit.putString("password", "");
//            edit.apply();
//
////            findViewById(R.id.google_sign_in_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.google_sign_out_button).setVisibility(View.GONE);
//            findViewById(R.id.ll_1).setVisibility(View.VISIBLE);
//            findViewById(R.id.ll_2).setVisibility(View.VISIBLE);
////            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
//        }
//    }

    private void updateUI() {
        hideProgressDialog();

        SharedPreferences pref = getSharedPreferences("current_user", 0);
        if (pref.getBoolean("is_login", false)) {

            mStatusTextView.setText("회원 정보");
            String text = "이메일: " + pref.getString("email", "") + "\n닉네임: " + pref.getString("name", "");
            mDetailTextView.setText(text);
//            mDetailTextView.setText(user.getUid());
            findViewById(R.id.ll_1).setVisibility(View.GONE);
            findViewById(R.id.ll_2).setVisibility(View.GONE);
//            findViewById(R.id.google_sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.google_sign_out_button).setVisibility(View.VISIBLE);
            //            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.action_login);
            mDetailTextView.setText(null);

            SharedPreferences.Editor edit = pref.edit();
            edit.putBoolean("is_login", false);
            edit.putInt("id_db", -1);
            edit.putString("email", "");
            edit.putString("name", "");
            edit.putString("password", "");
            edit.apply();

//            findViewById(R.id.google_sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.google_sign_out_button).setVisibility(View.GONE);
            findViewById(R.id.ll_1).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_2).setVisibility(View.VISIBLE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.google_sign_in_button) {
            signIn();
        } else if (i == R.id.google_sign_out_button) {
            signOut();
        }
//        else if (i == R.id.disconnect_button) {
//            revokeAccess();
//        }
    }


    // 프로그레스 다이얼로그 보여주고 숨기는 메소드

    public static ProgressDialog mProgressDialog = null;


    public static void showProgressDialog(Context context) {

        try {
            Log.d(TAG, "showProgressDialog call!");
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setTitle("로그인 중입니다.");
                mProgressDialog.setCancelable(false);
                mProgressDialog.setIndeterminate(true);
            }
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
                Log.d(TAG, "progressDialog.show()");
            }
        } catch (Exception e) {
            Log.e(TAG, "showProgressDialog Exception.. e = " + e.toString());
        }
    }

    public static void hideProgressDialog() {
        try {
            Log.d(TAG, "closeProgressDialog call!");
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
                Log.d(TAG, "progressDialog.dissmiss()");
            }
        } catch (Exception e) {
            Log.e(TAG, "closeProgressDialog Exception.. e = " + e.toString());
        }
    }

}
