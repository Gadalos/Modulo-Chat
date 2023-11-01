package com.gadalos.modulo_chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    TextView textView;
    private EditText escribirMensaje;
    private ImageButton btnEnviar;
    private List<Mensaje> mensajeList;
    private MensajeAdapter mensajeAdapter;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mensajeList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        escribirMensaje = (EditText) findViewById(R.id.escribirMensaje);
        btnEnviar = (ImageButton) findViewById(R.id.btnEnviar);

        //Configuracion del RecyclerView
        mensajeAdapter = new MensajeAdapter(mensajeList);
        recyclerView.setAdapter(mensajeAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pregunta = escribirMensaje.getText().toString();
                mensajeChat(pregunta, Mensaje.MENSAJE_ENVIADO_YO);
                escribirMensaje.setText("");
                llamarAPI(pregunta);
            }
        });
    }

    void mensajeChat(String mensaje, String enviadoPor){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mensajeList.add(new Mensaje(mensaje, enviadoPor));
                mensajeAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(mensajeAdapter.getItemCount());
            }
        });
    }
    void respondeGPT(String responde){
        mensajeList.remove(mensajeList.size()-1);
        mensajeChat(responde, Mensaje.MENSAJE_ENVIADO_GPT);
    }

    void llamarAPI(String pregunta) {
        //okhttp
        mensajeList.add(new Mensaje("Esperando ... ", Mensaje.MENSAJE_ENVIADO_GPT));

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("model", "gpt-3.5-turbo");
            jsonBody.put("prompt", pregunta);
            jsonBody.put("max_tokens", 150);
            jsonBody.put("temperature", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer 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")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                respondeGPT("Error al conectar con el servidor " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    String respuesta = response.body().string();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(respuesta);
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String resultado = jsonArray.getJSONObject(0).getString("text");
                        respondeGPT(resultado.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    String errorResponse = response.body().string(); // Obtener el contenido del error como cadena
                    respondeGPT("Error al conectar con el servidor " + errorResponse);
                }
            }
        });
    }
}