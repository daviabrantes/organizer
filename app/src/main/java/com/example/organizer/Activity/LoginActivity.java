package com.example.organizer.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.organizer.Config.ConfiguracaoFirebase;
import com.example.organizer.Model.Usuario;
import com.example.organizer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private Button botaoEntrar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        campoSenha = findViewById(R.id.editSenha);
        campoEmail = findViewById(R.id.editEmail);
        botaoEntrar = findViewById(R.id.buttonEntrar);

        botaoEntrar.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                   String textoEmail = campoEmail.getText().toString();
                   String textoSenha = campoSenha.getText().toString();

                   if (!textoEmail.isEmpty()) {
                       if (!textoSenha.isEmpty()) {

                           usuario = new Usuario();
                           usuario.setEmail(textoEmail);
                           usuario.setSenha(textoSenha);
                           validarLogin();

                       } else {
                           Toast.makeText(LoginActivity.this,
                                   "Preencha a senha!",
                                   Toast.LENGTH_SHORT).show();
                       }
                   } else {
                       Toast.makeText(LoginActivity.this,
                               "Preencha o email!",
                               Toast.LENGTH_SHORT).show();
                   }
               }
           }
        );
    }

    public void validarLogin() {

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {

                       if (task.isSuccessful()) {

                           abrirTelaPrincipal();

                       } else {
                           String excecao = "";
                           try {
                               throw task.getException();
                           } catch (FirebaseAuthInvalidCredentialsException e) {
                               excecao = "Email e senha não correspondem a um usuário cadastrado!";
                           } catch (FirebaseAuthInvalidUserException e) {
                               excecao = "Usuário não está cadastrado!";
                           } catch (Exception e) {
                               excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                               e.printStackTrace();
                           }
                           Toast.makeText(LoginActivity.this,
                                   excecao,
                                   Toast.LENGTH_SHORT).show();
                       }
                   }
               }
        );
    }

    public void abrirTelaPrincipal() {
        Intent intent = new Intent(this, PrincipalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        Toast.makeText(LoginActivity.this,
                "Sucesso ao fazer Login!",
                Toast.LENGTH_SHORT).show();
    }
}
