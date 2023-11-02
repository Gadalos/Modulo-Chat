package com.gadalos.modulo_chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.emoji.Emoji;

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
    private EditText escribirMensaje;
    private ImageButton btnEnviar, emojiButton;
    private List<Mensaje> mensajeList;
    private MensajeAdapter mensajeAdapter;
    private LinearLayout messageLayout;
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
        emojiButton = (ImageButton) findViewById(R.id.emojiButton);
        messageLayout = (LinearLayout) findViewById(R.id.messageLayout);

        //Configuracion del RecyclerView
        mensajeAdapter = new MensajeAdapter(mensajeList);
        recyclerView.setAdapter(mensajeAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(findViewById(R.id.messageLayout)).build(escribirMensaje);
        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emojiPopup.toggle();
            }
        });
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pregunta = escribirMensaje.getText().toString();
                mensajeChat(pregunta, Mensaje.MENSAJE_ENVIADO_YO);
                EmojiTextView emojiTextView = (EmojiTextView) LayoutInflater
                        .from(view.getContext())
                        .inflate(R.layout.emoji_text_view, messageLayout, false);
                messageLayout.addView(emojiTextView);
                escribirMensaje.setEnabled(true);
                escribirMensaje.setText("");
                llamarAPI(pregunta);
            }
        });
    }

    void mensajeChat(String mensaje, String enviadoPor) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mensajeList.add(new Mensaje(mensaje, enviadoPor));
                mensajeAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(mensajeAdapter.getItemCount());
            }
        });
    }

    void respondeGPT(String responde) {
        mensajeList.remove(mensajeList.size() - 1);
        mensajeChat(responde, Mensaje.MENSAJE_ENVIADO_GPT);
    }

    void llamarAPI(String pregunta) {
        //okhttp
        mensajeList.add(new Mensaje("Esperando ... ", Mensaje.MENSAJE_ENVIADO_GPT));

        JSONObject jsonBody = new JSONObject();

        JSONArray promptMessages = new JSONArray();


        try {
            jsonBody.put("model", "gpt-3.5-turbo");
            jsonBody.put("max_tokens", 150);
            jsonBody.put("temperature", 0);

            JSONObject initialPrompt = new JSONObject();
            initialPrompt.put("role", "system");
            initialPrompt.put("content", "Hola, eres un asistente virtual");

            mensajeList.forEach(mensaje -> {
                try {
                    JSONObject promptMessage = new JSONObject();
                    promptMessage.put("role", mensaje.getEnviadoPor().equals(Mensaje.MENSAJE_ENVIADO_GPT) ? "system" : "user");
                    promptMessage.put("content", mensaje.getMensaje());
                    promptMessages.put(promptMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

            promptMessages.put(initialPrompt);

            jsonBody.put("messages", promptMessages);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        String OPEN_IA_API_KEY = "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";
        String OPEN_IA_URL = "https://api.openai.com/v1/chat/completions";
        Request request = new Request.Builder()
                .url(OPEN_IA_URL)
                .header("Authorization", "Bearer ".concat(OPEN_IA_API_KEY))
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                respondeGPT("Error al conectar con el servidor " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String respuesta = response.body().string();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(respuesta);
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String resultado = jsonArray.getJSONObject(0).getJSONObject("message").getString("content");
                        Log.d("RESPUESTA", resultado);
                        respondeGPT(resultado.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    String errorResponse = response.body().string(); // Obtener el contenido del error como cadena
                    respondeGPT("Error al conectar con el servidor " + errorResponse);
                }
            }
        });
    }
}