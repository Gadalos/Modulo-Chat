package com.gadalos.modulo_chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    TextView textView;
    private EditText escribirMensaje;
    private ImageButton btnEnviar;
    private List<Mensaje> mensajeList;
    private MensajeAdapter mensajeAdapter;

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

}