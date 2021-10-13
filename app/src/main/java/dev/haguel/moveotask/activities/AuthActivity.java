package dev.haguel.moveotask.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;


import dev.haguel.moveotask.fragments.auth.LoginFragment;
import dev.haguel.moveotask.R;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.auth_fragment_container, LoginFragment.newInstance());
        fragmentTransaction.commit();
    }
}