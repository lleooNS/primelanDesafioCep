package com.findcep;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.findcep.model.CEP;
import com.findcep.service.HTTPService;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Armazena o cep digitado
        final EditText cep = findViewById(R.id.campoCep);

        //Armazena a resposta
        final TextView resposta = findViewById(R.id.campoResposta);

        //Ações do botão
        Button botao = findViewById(R.id.botaoConsultarCep);
        botao.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Verificar se existe conexão com a internet
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();

                //Com conexão
                if(netInfo != null && netInfo.isConnectedOrConnecting())
                {
                    //Verificar se o CEP é válido
                    if(cep.getText().toString().length() == 8)
                    {
                        //Log.i("Teste","CEP válido");
                        HTTPService service = new HTTPService(cep.getText().toString());
                        try
                        {
                            CEP retorno = service.execute().get();

                            //Verificar se o CEP existe
                            if(retorno.getCep() != null)
                            {
                                resposta.setText(retorno.toString());
                                esconderTeclado(resposta);
                            }
                            else
                            {
                                resposta.setText("CEP Inexistente!");
                                esconderTeclado(resposta);
                            }

                        }
                        catch (ExecutionException e)
                        {
                            e.printStackTrace();
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        resposta.setText("CEP Inválido!");
                        esconderTeclado(resposta);
                    }
                }
                else
                {
                    //Sem conexão
                    Toast.makeText(MainActivity.this, "Dispositivo sem Internet", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    public void esconderTeclado(View v)
    {
        if(v != null)
        {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}
