package com.example.pip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    TextView messagesTextView;
    EditText inputEditText;
    Button sendButton;
    Context context;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        messagesTextView = findViewById(R.id.messagesTextView);
        inputEditText = findViewById(R.id.inputEditText);
        sendButton = findViewById(R.id.sendButton);
        scrollView = findViewById(R.id.messageView);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mandar mensaje al TextView luego de apretar enviar
                String input = inputEditText.getText().toString();
                messagesTextView.append(Html.fromHtml("<p><b>Yo:</b> " + input + "</p>"));
                inputEditText.setText("");
                // Hacer scroll
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                // Añadir respuesta del bot al input
                getResponse(input);
                // Hacer scroll
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });

    }

    private void getResponse(String input) {
        // Credenciales para acceder al API
        String urlAssistant = "https://api.us-south.assistant.watson.cloud.ibm.com/instances/fd1ec105-bcce-4d4a-a8b9-9ad15381beba/v1/workspaces/f83d2771-1e85-432d-a5e5-09f1207bd53f/message?version=2021-06-14";
        String authentication = "YXBpa2V5OkpaaXZOOVJUZ1B5aVR6TjJGdWRWWm54cEJtX213cnpjcTFNOGJKd2wyY25X";

        // Encapsular el texto en un objeto JSON
        JSONObject inputJsonObject = new JSONObject();
        try {
            inputJsonObject.put("text", input);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Encapsular el objeto JSON en otro objeto JSON
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("input", inputJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Mandar POST request al chatbot
        AndroidNetworking.post(urlAssistant)
                // Añadir headers (Como en Postman)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", "Basic " + authentication)
                // Añadir el objeto de input
                .addJSONObjectBody(jsonBody)
                .setPriority(Priority.HIGH)
                .setTag(getString(R.string.app_name))
                // Mandar request
                .build()
                // Recibir response
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parsear la respuesta, obteniendo el array de textos
                            JSONArray outputJsonObject = response.getJSONObject("output").getJSONArray("text");

                            // Respuesta sin texto
                            if (outputJsonObject.length() == 0) {
                                messagesTextView.append(Html.fromHtml("<p><b>Pip:</b> " + "No soporto esa funcionalidad en la aplicacion movil." + "</p>"));
                            }

                            // Añadir todos los textos de respuesta al text view
                            for (int i = 0; i < outputJsonObject.length(); i++) {
                                messagesTextView.append(Html.fromHtml("<p><b>Pip:</b> " + outputJsonObject.getString(i) + "</p>"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(context,"Error de conexión", Toast.LENGTH_LONG).show();
                    }
                });
    }

}