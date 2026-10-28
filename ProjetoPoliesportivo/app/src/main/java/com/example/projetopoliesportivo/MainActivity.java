package com.example.projetopoliesportivo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private static final int CODIGO_PERMISSAO_REDE = 1;

    private EditText primeiroNome;
    private EditText sobrenome;
    private EditText username;
    private EditText email;
    private EditText cpf;
    private EditText idade;
    private EditText senha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        primeiroNome = findViewById(R.id.editPrimeiroNome);
        sobrenome = findViewById(R.id.editSobrenome);
        username = findViewById(R.id.editUsername);
        email = findViewById(R.id.editEmail);
        cpf = findViewById(R.id.editCpf);
        idade = findViewById(R.id.editIdade);
        senha = findViewById(R.id.editSenha);

        Button botaoCadastro = findViewById(R.id.botaoCadastro);
        botaoCadastro.setOnClickListener(v -> cadastrar());

        TextView linkLogin = findViewById(R.id.textoLinkLogin);
        linkLogin.setOnClickListener(v ->
                startActivity(new Intent(this, Tela_login.class))
        );
    }

    private void cadastrar() {
        if (Build.VERSION.SDK_INT >= 37
                && checkSelfPermission(Manifest.permission.ACCESS_LOCAL_NETWORK)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_LOCAL_NETWORK},
                    CODIGO_PERMISSAO_REDE
            );
            return;
        }

        try {
            JSONObject dados = new JSONObject();
            dados.put("username", username.getText().toString().trim());
            dados.put("password", senha.getText().toString());
            dados.put("user_cpf", cpf.getText().toString().trim());
            dados.put("first_name", primeiroNome.getText().toString().trim());
            dados.put("last_name", sobrenome.getText().toString().trim());
            dados.put("email", email.getText().toString().trim());
            dados.put("user_age", Integer.parseInt(idade.getText().toString().trim()));

            new Thread(() -> enviarCadastro(dados)).start();

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao preparar os dados.", Toast.LENGTH_SHORT).show();
        }
    }

    private void enviarCadastro(JSONObject dados) {
        HttpURLConnection conexao = null;

        try {
            URL url = new URL("http://10.0.2.2:8000/auth/cadastro/");
            conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("POST");
            conexao.setDoOutput(true);
            conexao.setRequestProperty(
                    "Content-Type",
                    "application/json; charset=UTF-8"
            );

            conexao.getOutputStream().write(
                    dados.toString().getBytes(StandardCharsets.UTF_8)
            );

            int codigo = conexao.getResponseCode();

            runOnUiThread(() -> {
                String mensagem = codigo == 201
                        ? "Conta criada com sucesso!"
                        : "Erro no cadastro. Código: " + codigo;

                Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
            });

        } catch (Exception e) {
            runOnUiThread(() ->
                    Toast.makeText(
                            this,
                            "Falha ao conectar com a API: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show()
            );
        } finally {
            if (conexao != null) {
                conexao.disconnect();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CODIGO_PERMISSAO_REDE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            cadastrar();
        }
    }
}