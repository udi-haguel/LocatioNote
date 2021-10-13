package dev.haguel.moveotask.fragments.auth;

import android.content.Intent;
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

import dev.haguel.moveotask.DatabaseManager;
import dev.haguel.moveotask.R;
import dev.haguel.moveotask.activities.MainActivity;
import dev.haguel.moveotask.entities.UserEntity;

public class RegisterFragment extends Fragment {


    private EditText etRegisterName;
    private EditText etRegisterEmail;
    private EditText etRegisterPassword;
    private EditText etRegisterPassword2;
    private Button btnRegister;
    private TextView tvAlreadyMember;

    private FirebaseAuth mAuth;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_register, container, false);

        etRegisterName = root.findViewById(R.id.etRegisterName);
        etRegisterEmail = root.findViewById(R.id.etRegisterEmail);
        etRegisterPassword = root.findViewById(R.id.etRegisterPassword);
        etRegisterPassword2 = root.findViewById(R.id.etRegisterPassword2);
        btnRegister = root.findViewById(R.id.btnRegister);
        tvAlreadyMember = root.findViewById(R.id.tvAlreadyMember);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        tvAlreadyMember.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.auth_fragment_container, LoginFragment.newInstance());
            fragmentTransaction.commit();
        });

        btnRegister.setOnClickListener(v->{
            checkUserValidationAndRegister();
        });
    }

    private void checkUserValidationAndRegister(){

        String fullName = etRegisterName.getText().toString().trim();
        String email = etRegisterEmail.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();
        String password2 = etRegisterPassword2.getText().toString().trim();

        if (fullName.isEmpty()){
            etRegisterName.setError("Full name is required!");
            etRegisterName.requestFocus();
            return;
        }
        if (email.isEmpty()){
            etRegisterEmail.setError("Email is required!");
            etRegisterEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etRegisterEmail.setError("Please provide valid email");
            etRegisterEmail.requestFocus();
            return;
        }
        if (password.isEmpty()){
            etRegisterPassword.setError("Passowrd is required");
            etRegisterPassword.requestFocus();
            return;
        }
        if (password.length() < 6){
            etRegisterPassword.setError("Min password length should be 6 characters");
            etRegisterPassword.requestFocus();
            return;
        }
        if (password2.isEmpty()){
            etRegisterPassword2.setError("Required field");
            etRegisterPassword2.requestFocus();
            return;
        }
        if (!password.equals(password2)){
            etRegisterPassword2.setError("Password does not match");
            etRegisterPassword2.requestFocus();
            return;
        }

        registerUser(fullName, email, password);
    }

    public void registerUser(String fullName, String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(registerTask -> {
                    if (getActivity() == null) return;

                    if (registerTask.isSuccessful()){

                        // Registering Current User
                        if (mAuth.getCurrentUser() != null) {
                            UserEntity user = new UserEntity(fullName, email);
                            String userID = mAuth.getCurrentUser().getUid();
                            DatabaseManager.instance().registerUser(userID, user);
                        }

                        Toast.makeText(getActivity(), "register was successful", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), "Failed to register", Toast.LENGTH_LONG).show();
                    }
                });
    }

}