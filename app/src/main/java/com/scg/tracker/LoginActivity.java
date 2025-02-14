package com.scg.tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.textfield.TextInputEditText;import com.scg.tracker.util.EncryptedPrefsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;

public class LoginActivity extends AppCompatActivity implements OnSuccessListener {
    private com.google.android.material.textfield.TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private Button signInButton;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        emailEditText = (TextInputEditText) findViewById(R.id.emailField);
        passwordEditText = (TextInputEditText) findViewById(R.id.passwordField);
        signInButton = (Button) findViewById(R.id.signInButton);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();

                //Toast.makeText(getBaseContext(),"email: "+email+"password: "+password,Toast.LENGTH_SHORT).show();

//                new JsonTask().execute(Constants.BASE_URL + "api/login","POST",email,password);

                EncryptedPrefsUtil.saveString("email",email);
                EncryptedPrefsUtil.saveString("password",password);

                JSONObject requestBody = new JSONObject();
                try {
                    requestBody.put("email", email);
                    requestBody.put("password", password);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                NetworkUtils.
                        fetchDataPost("login",
                                "", requestBody,LoginActivity.this,
                                LoginActivity.this);

            }
        });

    }

    @Override
    public void onSuccess(ResponseBody responseBody) {

        try {

            String result = responseBody.string();
            System.out.println("Result: "+result);

            JSONObject jsonObject = null;

            jsonObject = new JSONObject(result);


            if (
                    jsonObject.has("message") &&
                            jsonObject.get("message") instanceof String &&
                            jsonObject.has("status") &&
                            jsonObject.get("status") instanceof String
            ) {
                String message = jsonObject.getString("message");
                String status = jsonObject.getString("status");

                if (status.equals("success")) {
                    //Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


                    EncryptedPrefsUtil.saveString("authToken", jsonObject.getString("authToken"));
                    EncryptedPrefsUtil.saveString("userId", jsonObject.getString("userId"));
                    EncryptedPrefsUtil.saveString("email", email);
                    EncryptedPrefsUtil.saveString("password",password);

                    long currentTimeMillis = System.currentTimeMillis();
                    long expiryMillis = Constants.EXPIRY_MILLIS+currentTimeMillis;

                    EncryptedPrefsUtil.saveString("tokenExpiry", expiryMillis+"");




                    startActivity(intent);

                } else if(status.equals("failed")) {
//                    Toasts.toastIconError(LoginActivity.this, message);
                    Toast.makeText(LoginActivity.this,message,Toast.LENGTH_SHORT).show();

                } else{

                    Toast.makeText(LoginActivity.this,"Login Failed! Please Try Again",Toast.LENGTH_SHORT).show();

//                FirebaseCrashlytics.getInstance().recordException(new Exception("Login  Error: "+result));
                }
            }else{

                Toast.makeText(LoginActivity.this,"Login Failed! Please Try Again",Toast.LENGTH_SHORT).show();

//            FirebaseCrashlytics.getInstance().recordException(new Exception("Login Error JSON Object: "+result));

            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onComplete() {

    }
}