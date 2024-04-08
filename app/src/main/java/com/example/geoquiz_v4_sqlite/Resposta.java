package com.example.geoquiz_v4_sqlite;

import java.util.UUID;

public class Resposta {
    private UUID id;
    private int respostaCorreta;
    private boolean respostaOferecida;
    private boolean colou;

    public Resposta() { }

    public Resposta(UUID id, boolean respostaCorreta, boolean respostaOferecida, boolean colou) {
        this.id = id;
        this.respostaCorreta = respostaCorreta ? 1 : 0;
        this.respostaOferecida = respostaOferecida;
        this.colou = colou;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getRespostaCorreta() {
        return respostaCorreta;
    }

    public void setRespostaCorreta(int respostaCorreta) {
        this.respostaCorreta = respostaCorreta;
    }

    public boolean isRespostaOferecida() {
        return respostaOferecida;
    }

    public void setRespostaOferecida(boolean respostaOferecida) {
        this.respostaOferecida = respostaOferecida;
    }

    public boolean isColou() {
        return colou;
    }

    public void setColou(boolean colou) {
        this.colou = colou;
    }
}
