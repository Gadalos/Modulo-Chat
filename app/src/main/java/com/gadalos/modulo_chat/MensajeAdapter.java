package com.gadalos.modulo_chat;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.MyViewHolder>{

    List<Mensaje> mensajeList;
    public MensajeAdapter(List<Mensaje> mensajeList) {
        this.mensajeList = mensajeList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = View.inflate(parent.getContext(), R.layout.chat_mensaje, null);
        MyViewHolder myViewHolder = new MyViewHolder(chatView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Mensaje mensaje = mensajeList.get(position);
        if (mensaje.getEnviadoPor().equals(Mensaje.MENSAJE_ENVIADO_YO)){
            holder.chat_derecho_vista.setVisibility(View.VISIBLE);
            holder.chat_izquierdo_vista.setVisibility(View.GONE);
            holder.chat_derecho_vista_texto.setText(mensaje.getMensaje());
        }else {
            holder.chat_derecho_vista.setVisibility(View.GONE);
            holder.chat_izquierdo_vista.setVisibility(View.VISIBLE);
            holder.chat_izquierdo_vista_texto.setText(mensaje.getMensaje());
        }
    }

    @Override
    public int getItemCount() {
        return mensajeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        LinearLayout chat_izquierdo_vista, chat_derecho_vista;
        TextView chat_izquierdo_vista_texto, chat_derecho_vista_texto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            chat_izquierdo_vista = itemView.findViewById(R.id.chat_izquierdo_vista);
            chat_derecho_vista = itemView.findViewById(R.id.chat_derecho_vista);
            chat_izquierdo_vista_texto = itemView.findViewById(R.id.chat_izquierdo_vista_texto);
            chat_derecho_vista_texto = itemView.findViewById(R.id.chat_derecho_vista_texto);
        }
    }

}
