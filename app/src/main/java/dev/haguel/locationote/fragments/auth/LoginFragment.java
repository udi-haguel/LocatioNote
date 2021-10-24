package dev.haguel.locationote.fragments.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import dev.haguel.locationote.R;
import dev.haguel.locationote.activities.MainActivity;


public class LoginFragment extends Fragment {


    private EditText etLoginEmail;
    private EditText etLoginPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private TextView tvForgotPassword;

    private FirebaseAuth mAuth;


    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        etLoginEmail = root.findViewById(R.id.etLoginEmail);
        etLoginPassword = root.findViewById(R.id.etLoginPassword);
        btnLogin = root.findViewById(R.id.btnLogin);
        tvRegister = root.findViewById(R.id.tvRegister);
        tvForgotPassword = root.findViewById(R.id.tvForgotPassword);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(v -> {
            inputValidateAndLoginUser();
        });

        tvRegister.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.auth_fragment_container, RegisterFragment.newInstance());
            fragmentTransaction.commit();
        });

        tvForgotPassword.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.auth_fragment_container, ForgotPasswordFragment.newInstance());
            fragmentTransaction.commit();
        });

    }

    private void inputValidateAndLoginUser() {
        if (etLoginEmail.getText() == null) return;
        if (etLoginPassword.getText() == null) return;

        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();


        if (TextUtils.isEmpty(email)){
            etLoginEmail.setError("Email is required");
            etLoginEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etLoginEmail.setError("Please enter a valid email");
            etLoginEmail.requestFocus();
            return;
        }
        if (password.isEmpty()){
            etLoginPassword.setError("Password is required");
            etLoginPassword.requestFocus();
            return;
        }
        if (password.length() < 6){
            etLoginPassword.setError("Min password length is 6 characters");
            etLoginPassword.requestFocus();
            return;
        }

        loginUser(email, password);
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (getActivity() == null) return;

                    if (task.isSuccessful()){
                        Toast.makeText(getActivity(), "Logged in successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), "Failed to log in", Toast.LENGTH_LONG).show();
                    }
                });
    }

}