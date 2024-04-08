package com.example.geoquiz_v4_sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RespostaDB {
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public RespostaDB(Context contexto) {
        mContext = contexto.getApplicationContext();
        mDatabase = new RespostasDBHelper(mContext).getWritableDatabase();
    }

    private ContentValues getValoresConteudo(Resposta r) {
        ContentValues valores = new ContentValues();

        // pares chave-valor: nomes das colunas - valores
        valores.put(RespostasDBSchema.RespostasTbl.Cols.UUID, r.getId().toString());
        valores.put(RespostasDBSchema.RespostasTbl.Cols.RESPOSTA_OFERECIDA, r.getRespostaCorreta());
        valores.put(RespostasDBSchema.RespostasTbl.Cols.RESPOSTA_CORRETA, r.isRespostaOferecida());
        valores.put(RespostasDBSchema.RespostasTbl.Cols.COLOU, r.isColou());
        return valores;
    }

    public void addResposta(Resposta r) {
        ContentValues valores = getValoresConteudo(r);
        mDatabase.insert(RespostasDBSchema.RespostasTbl.NOME, null, valores);
    }

    public Cursor queryResposta(String clausulaWhere, String[] argsWhere) {
        return mDatabase.query(
                RespostasDBSchema.RespostasTbl.NOME,
                null,  // todas as colunas
                clausulaWhere,
                argsWhere,
                null, // sem group by
                null, // sem having
                null  // sem order by
        );
    }

    void removeBanco() {
        mDatabase.delete(RespostasDBSchema.RespostasTbl.NOME, null, null);
    }
}
