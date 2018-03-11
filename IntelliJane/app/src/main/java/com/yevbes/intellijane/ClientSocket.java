package com.yevbes.intellijane;

import android.os.AsyncTask;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Usuario on 24/07/2017.
 */

public class ClientSocket extends AsyncTask<Void, Void, String> {

    private String serverAdress;
    private int serverPort;
    private String response = "";
    private String textResponse;
    private TextView textResponseWidget;
    private String textRequest;
    private PrintWriter salida;
    private BufferedReader entrada;
    private long time;
    private TextToSpeech textToSpeech;

    ClientSocket(String addr, int port, TextView textResponseWidget, String textRequest, TextToSpeech textToSpeech) {
        this.serverAdress = addr;
        this.serverPort = port;
        this.textResponseWidget = textResponseWidget; // Texto recibir del servidor
        this.textRequest = textRequest; // Texto enviar a Servidor
        this.textToSpeech = textToSpeech;
    }

    @Override
    protected String doInBackground(Void... arg0) {
        Socket socket = null;
        entrada = null;
        try {
            socket = new Socket(serverAdress, serverPort);
            salida = new PrintWriter(socket.getOutputStream(), true);
            salida.println(textRequest.toUpperCase());
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            time = System.currentTimeMillis();
            textResponse = entrada.readLine();

            while (textResponse == null /*|| (System.currentTimeMillis() - time) < 10000*/) {
                textResponse = entrada.readLine();
            }
            /*if (!((System.currentTimeMillis() - time) < 10000))
                textResponse = "Server is not respound";*/
            response = textResponse;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            response += "; Server not found";
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        textResponseWidget.setText(response);
        if (Build.VERSION.SDK_INT >= 21) {
            textToSpeech.speak(response,TextToSpeech.QUEUE_FLUSH,null,null);
        } else {
            textToSpeech.speak(response,TextToSpeech.QUEUE_FLUSH,null);
        }
        super.onPostExecute(s);
    }
}
