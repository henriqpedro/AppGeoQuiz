package com.example.geoquiz_v4_sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class QuestaoDB {

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public QuestaoDB(Context contexto) {
        mContext = contexto.getApplicationContext();
        mDatabase = new QuestoesDBHelper(mContext).getWritableDatabase();
    }

    private ContentValues getValoresConteudo(Questao q) {
        ContentValues valores = new ContentValues();

        // pares chave-valor: nomes das colunas - valores
        valores.put(QuestoesDbSchema.QuestoesTbl.Cols.UUID, q.getId().toString());
        // recupera valor do recurso string pelo id
        valores.put(QuestoesDbSchema.QuestoesTbl.Cols.TEXTO_QUESTAO, mContext.getString(q.getTextoRespostaId()));
        valores.put(QuestoesDbSchema.QuestoesTbl.Cols.QUESTAO_CORRETA, q.isRespostaCorreta());
        return valores;
    }

    public void addQuestao(Questao q) {
        ContentValues valores = getValoresConteudo(q);
        mDatabase.insert(QuestoesDbSchema.QuestoesTbl.NOME, null, valores);
    }

    public Cursor queryQuestao(String clausulaWhere, String[] argsWhere) {
        return mDatabase.query(
                QuestoesDbSchema.QuestoesTbl.NOME,
                null,  // todas as colunas
                clausulaWhere,
                argsWhere,
                null, // sem group by
                null, // sem having
                null  // sem order by
        );
    }

    void removeBanco() {
        mDatabase.delete(QuestoesDbSchema.QuestoesTbl.NOME, null, null);
    }
}
