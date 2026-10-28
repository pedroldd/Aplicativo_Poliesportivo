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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Tela_login extends AppCompatActivity {

    private static final int CODIGO_PERMISSAO_REDE = 1;

    private EditText campoUsername;
    private EditText campoSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });

        campoUsername = findViewById(R.id.editUsernameLogin);
        campoSenha = findViewById(R.id.editSenhaLogin);

        Button botaoLogin = findViewById(R.id.botaoLogin);
        botaoLogin.setOnClickListener(v -> fazerLogin());

        TextView linkCadastro = findViewById(R.id.textoLinkCadastro);
        linkCadastro.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class))
        );
    }

    private void fazerLogin() {
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
            dados.put("username", campoUsername.getText().toString().trim());
            dados.put("password", campoSenha.getText().toString());

            new Thread(() -> enviarLogin(dados)).start();
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao preparar o login.", Toast.LENGTH_SHORT).show();
        }
    }

    private void enviarLogin(JSONObject dados) {
        HttpURLConnection conexao = null;

        try {
            URL url = new URL("http://10.0.2.2:8000/auth/login/");
            conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("POST");
            conexao.setDoOutput(true);
            conexao.setRequestProperty(
                    "Content-Type",
                    "application/json; charset=UTF-8"
            );

            try (OutputStream saida = conexao.getOutputStream()) {
                saida.write(dados.toString().getBytes(StandardCharsets.UTF_8));
            }

            int codigo = conexao.getResponseCode();
            InputStream fluxo = codigo < 400
                    ? conexao.getInputStream()
                    : conexao.getErrorStream();

            String resposta = lerResposta(fluxo);

            runOnUiThread(() -> {
                if (codigo == 200) {
                    try {
                        String token = new JSONObject(resposta).getString("token");

                        getSharedPreferences("sessao", MODE_PRIVATE)
                                .edit()
                                .putString("token", token)
                                .apply();

                        Toast.makeText(this, "Login realizado!", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "Não foi possível ler a resposta da API.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(
                            this,
                            "Falha no login: " + resposta,
                            Toast.LENGTH_LONG
                    ).show();
                }
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

    private String lerResposta(InputStream fluxo) throws Exception {
        if (fluxo == null) {
            return "";
        }

        StringBuilder resposta = new StringBuilder();

        try (BufferedReader leitor = new BufferedReader(
                new InputStreamReader(fluxo, StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                resposta.append(linha);
            }
        }

        return resposta.toString();
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
            fazerLogin();
        }
    }
}