package com.example.organizer.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizer.Config.ConfiguracaoFirebase;
import com.example.organizer.Helper.Base64Custom;
import com.example.organizer.Helper.DateCustom;
import com.example.organizer.Model.Movimentacao;
import com.example.organizer.Model.Usuario;
import com.example.organizer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ReceitasActivity extends AppCompatActivity {

    private EditText campoValor, campoCategoria, campoData, campoDescricao;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double receitaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        campoValor = findViewById(R.id.editValor);
        campoCategoria = findViewById(R.id.editCategoria);
        campoData = findViewById(R.id.editData);
        campoDescricao = findViewById(R.id.editDescricao);
        campoData.setText(DateCustom.dataAtual());
        recuperarReceitaTotal();
    }

    public void salvarReceita(View view) {

        if (validarCamposReceita()) {
            movimentacao = new Movimentacao();

            String data = campoData.getText().toString();
            Double valorRecuperado = (Double.parseDouble(campoValor.getText().toString()));

            movimentacao.setValor(Double.parseDouble(campoValor.getText().toString()));
            movimentacao.setCategoria(campoCategoria.getText().toString());
            movimentacao.setDescricao(campoDescricao.getText().toString());
            movimentacao.setData(data);
            movimentacao.setTipo("Receita");

            Double receitaAtualizada = receitaTotal + valorRecuperado;
            atualizarReceita(receitaAtualizada);

            movimentacao.salvar(data);

            finish();
        }
    }

    public Boolean validarCamposReceita() {

        String textoValor = campoValor.getText().toString();
        String textoData = campoData.getText().toString();
        String textoCategoria = campoCategoria.getText().toString();
        String textoDescricao = campoDescricao.getText().toString();

        if (!textoValor.isEmpty()) {
            if (!textoData.isEmpty()) {
                if (!textoCategoria.isEmpty()) {
                    if (!textoDescricao.isEmpty()) {
                        return true;
                    } else {
                        Toast.makeText(ReceitasActivity.this,
                                "Preencha a descrição",
                                Toast.LENGTH_LONG)
                                .show();
                        return false;
                    }

                } else {
                    Toast.makeText(ReceitasActivity.this,
                            "Preencha a categoria",
                            Toast.LENGTH_LONG)
                            .show();
                    return false;
                }

            } else {
                Toast.makeText(ReceitasActivity.this,
                        "Preencha a data",
                        Toast.LENGTH_LONG)
                        .show();
                return false;
            }

        } else {
            Toast.makeText(ReceitasActivity.this,
                    "Preencha o valor",
                    Toast.LENGTH_LONG)
                    .show();
            return false;
        }
    }

    public void recuperarReceitaTotal() {
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void atualizarReceita(double receita) {
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.child("receitaTotal").setValue(receita);

    }

}
