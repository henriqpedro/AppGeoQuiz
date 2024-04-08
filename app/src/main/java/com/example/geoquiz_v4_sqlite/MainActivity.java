package com.example.geoquiz_v4_sqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

/*
  Modelo de projeto para a Atividade 1.
  Será preciso adicionar o cadastro das respostas do usuário ao Quiz, conforme
  definido no Canvas.

  GitHub: https://github.com/udofritzke/GeoQuiz
 */

public class MainActivity extends AppCompatActivity {
    private Button mBotaoVerdadeiro;
    private Button mBotaoFalso;
    private Button mBotaoProximo;
    private Button mBotaoMostra;
    private Button mBotaoDeleta;

    private Button mBotaoCola;

    private TextView mTextViewQuestao;
    private TextView mTextViewQuestoesArmazenadas;

    private static final String TAG = "QuizActivity";
    private static final String CHAVE_INDICE = "INDICE";
    private static final int CODIGO_REQUISICAO_COLA = 0;

    private Questao[] mBancoDeQuestoes = new Questao[]{
            new Questao(R.string.questao_suez, true),
            new Questao(R.string.questao_alemanha, false)
    };

    QuestaoDB mQuestoesDb;
    RespostaDB mRespostaDb;

    private int mIndiceAtual = 0;

    private boolean mEhColador;

    private void addQuestoes() {
        if (mQuestoesDb == null) {
            mQuestoesDb = new QuestaoDB(getBaseContext());
        }
        Cursor cursor = mQuestoesDb.queryQuestao(null, null);
        if (cursor != null) {
            int index = cursor.getCount();
            while (index < mBancoDeQuestoes.length)
                mQuestoesDb.addQuestao(mBancoDeQuestoes[index++]);
            cursor.close();
        }
    }

    private void proximaQuestao() {
        mIndiceAtual = (mIndiceAtual + 1) % mBancoDeQuestoes.length;
        mEhColador = false;
        atualizaQuestao();
    }

    @Override
    protected void onCreate(Bundle instanciaSalva) {
        super.onCreate(instanciaSalva);
        setContentView(R.layout.activity_main);

        //Log.d(TAG, "onCreate()");
        if (instanciaSalva != null) {
            mIndiceAtual = instanciaSalva.getInt(CHAVE_INDICE, 0);
        }

        addQuestoes();
        mTextViewQuestao = (TextView) findViewById(R.id.view_texto_da_questao);
        atualizaQuestao();

        mBotaoVerdadeiro = (Button) findViewById(R.id.botao_verdadeiro);
        // utilização de classe anônima interna
        mBotaoVerdadeiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificaResposta(true);
            }
        });

        mBotaoFalso = (Button) findViewById(R.id.botao_falso);
        mBotaoFalso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificaResposta(false);
            }
        });

        mBotaoProximo = (Button) findViewById(R.id.botao_proximo);
        mBotaoProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proximaQuestao();
            }
        });

        mBotaoCola = (Button) findViewById(R.id.botao_cola);
        mBotaoCola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // inicia ColaActivity
                // Intent intent = new Intent(MainActivity.this, ColaActivity.class);
                boolean respostaEVerdadeira = mBancoDeQuestoes[mIndiceAtual].isRespostaCorreta();
                Intent intent = ColaActivity.novoIntent(MainActivity.this, respostaEVerdadeira);
                //startActivity(intent);
                startActivityForResult(intent, CODIGO_REQUISICAO_COLA);
            }
        });

        //Cursor cur = mQuestoesDb.queryQuestao ("_id = ?", val);////(null, null);
        //String [] val = {"1"};
        mBotaoMostra = (Button) findViewById(R.id.botao_mostra_questoes);
        mBotaoMostra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                  Acesso ao SQLite
                */
                if (mRespostaDb == null)
                    mRespostaDb = new RespostaDB(getBaseContext());

                if (mTextViewQuestoesArmazenadas == null) {
                    mTextViewQuestoesArmazenadas = (TextView) findViewById(R.id.texto_questoes_a_apresentar);
                } else {
                    mTextViewQuestoesArmazenadas.setText("");
                }

                Cursor cursor = mRespostaDb.queryResposta(null, null);
                if (cursor != null) {
                    if (cursor.getCount() == 0) {
                        mTextViewQuestoesArmazenadas.setText("Nada a apresentar");
                        Log.i("MSGS", "Nenhum resultado");
                    }
                    //Log.i("MSGS", Integer.toString(cursor.getCount()));
                    //Log.i("MSGS", "cursor não nulo!");
                    try {
                        int index = 1;
                        cursor.moveToFirst();
                        while (!cursor.isAfterLast()) {
                            String texto = "Resposta " + index++ + ": ";
                            if (cursor.getInt(cursor.getColumnIndex(RespostasDBSchema.RespostasTbl.Cols.RESPOSTA_OFERECIDA)) == 1)
                                texto += "Verdadeira ";
                            else
                                texto += "Falsa ";

                            if (cursor.getInt(cursor.getColumnIndex(RespostasDBSchema.RespostasTbl.Cols.RESPOSTA_CORRETA)) == 1)
                                texto += "(Correta)";
                            else
                                texto += "(Incorreta)";

                            if (cursor.getInt(cursor.getColumnIndex(RespostasDBSchema.RespostasTbl.Cols.COLOU)) == 1)
                                texto += " - COLADA";

                            mTextViewQuestoesArmazenadas.setText(texto);
                            Log.i("MSGS", texto);

                            mTextViewQuestoesArmazenadas.append(texto + "\n");
                            cursor.moveToNext();
                        }
                    } finally {
                        cursor.close();
                    }
                } else
                    Log.i("MSGS", "cursor nulo!");
            }
        });

        mBotaoDeleta = (Button) findViewById(R.id.botao_deleta);
        mBotaoDeleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                  Acesso ao SQLite
                */
                if (mRespostaDb != null) {
                    mRespostaDb.removeBanco();
                    if (mTextViewQuestoesArmazenadas == null) {
                        mTextViewQuestoesArmazenadas = (TextView) findViewById(R.id.texto_questoes_a_apresentar);
                    }
                    mTextViewQuestoesArmazenadas.setText("");
                }
            }
        });
    }

    private void atualizaQuestao() {
        int questao = mBancoDeQuestoes[mIndiceAtual].getTextoRespostaId();
        mTextViewQuestao.setText(questao);
    }

    private void verificaResposta(boolean respostaPressionada) {
        boolean respostaCorreta = mBancoDeQuestoes[mIndiceAtual].isRespostaCorreta();
        int idMensagemResposta = 0;

        Resposta resposta = new Resposta(UUID.randomUUID(), respostaCorreta == respostaPressionada,
                respostaPressionada, mEhColador);

        if (mRespostaDb == null) {
            mRespostaDb = new RespostaDB(getBaseContext());
        }
        mRespostaDb.addResposta(resposta);

        if (mEhColador) {
            idMensagemResposta = R.string.toast_julgamento;
        } else {
            if (respostaPressionada == respostaCorreta) {
                idMensagemResposta = R.string.toast_correto;
            } else
                idMensagemResposta = R.string.toast_incorreto;
        }

        Toast.makeText(this, idMensagemResposta, Toast.LENGTH_SHORT).show();

        proximaQuestao();
    }

    @Override
    public void onSaveInstanceState(Bundle instanciaSalva) {
        super.onSaveInstanceState(instanciaSalva);
        Log.i(TAG, "onSaveInstanceState()");
        instanciaSalva.putInt(CHAVE_INDICE, mIndiceAtual);
    }

    @Override
    protected void onActivityResult(int codigoRequisicao, int codigoResultado, Intent dados) {
        super.onActivityResult(codigoRequisicao, codigoResultado, dados);
        if (codigoResultado != Activity.RESULT_OK) {
            return;
        }
        if (codigoRequisicao == CODIGO_REQUISICAO_COLA) {
            if (dados == null) {
                return;
            }
            mEhColador = ColaActivity.foiMostradaResposta(dados);
        }
    }
}