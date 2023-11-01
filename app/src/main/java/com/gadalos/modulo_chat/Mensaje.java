package com.gadalos.modulo_chat;

public class Mensaje {
    public static String MENSAJE_ENVIADO_YO = "yo";
    public static String MENSAJE_ENVIADO_GPT = "gpt";

    String mensaje;
    String enviadoPor;

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getEnviadoPor() {
        return enviadoPor;
    }

    public void setEnviadoPor(String enviadoPor) {
        this.enviadoPor = enviadoPor;
    }

    public Mensaje(String mensaje, String enviadoPor) {
        this.mensaje = mensaje;
        this.enviadoPor = enviadoPor;
    }
}
