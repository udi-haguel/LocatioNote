package dev.haguel.locationote.fragments.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

public class ForgotPasswordFragment extends Fragment {

    private EditText etEmail;
    private Button btnReset;
    private TextView tvLogin;
    private TextView tvRegister;


    private FirebaseAuth mAuth;

    public static ForgotPasswordFragment newInstance() {
        return new ForgotPasswordFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        etEmail = root.findViewById(R.id.etForgotPasswordEmail);
        btnReset = root.findViewById(R.id.btnResetPassword);
        tvLogin = root.findViewById(R.id.tvBackToLogin);
        tvRegister = root.findViewById(R.id.tvBackToRegister);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        btnReset.setOnClickListener(v->{
            resetPassword();
        });
        tvLogin.setOnClickListener(v->{
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.auth_fragment_container, LoginFragment.newInstance());
            fragmentTransaction.commit();
        });

        tvRegister.setOnClickListener(v->{
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.auth_fragment_container, RegisterFragment.newInstance());
            fragmentTransaction.commit();
        });

    }

    private void resetPassword(){
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()){
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Please provide a valid email");
            etEmail.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(getContext(), "Check your email to reset your password", Toast.LENGTH_LONG).show();
                etEmail.setText("");
            } else {
                Toast.makeText(getContext(), "Try again! something went wrong, or email does not exist", Toast.LENGTH_LONG).show();
            }
        });



    }
}