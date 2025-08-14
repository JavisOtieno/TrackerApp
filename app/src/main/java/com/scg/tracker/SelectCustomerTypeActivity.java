package com.scg.tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SelectCustomerTypeActivity extends AppCompatActivity {

    private Button proceedWithoutButton;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private Button selectExistingButton;
    private Button addCustomerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_customer_type);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Select Customer Type");

        proceedWithoutButton = findViewById(R.id.proceedWithoutButton);
        addCustomerButton = findViewById(R.id.addNewButton);
        selectExistingButton = findViewById(R.id.selectExistingButton);

        proceedWithoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(SelectCustomerTypeActivity.this, AddTripActivity.class);
            startActivity(intent);
        });

        addCustomerButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(SelectCustomerTypeActivity.this,
                         AddCustomerActivity.class);
                 startActivity(intent);
             }
         });

        selectExistingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectCustomerTypeActivity.this, SelectCustomerActivity.class);
                startActivity(intent);
            }
        });

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }
}