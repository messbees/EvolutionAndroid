package com.gotowork.evolution;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    public MainActivity context;
    private RecyclerView gamesRecyclerView;
    private Button roomCreateButton, roomSearchButton;

    public interface VolleyCallback{
        void onSuccess(String code, String data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        roomCreateButton = findViewById(R.id.room_create_button);
        roomCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.create_room_button);
                final View vv = inflater.inflate(R.layout.dialog_create_room, null);
                builder.setView(vv);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String name = ((EditText) vv.findViewById(R.id.name)).getText().toString();
                        String password = ((EditText) vv.findViewById(R.id.password)).getText().toString();


                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        roomSearchButton = findViewById(R.id.room_search_button);
        gamesRecyclerView = findViewById(R.id.games_recycler_view);
    }

    public void roomCreate(String name, String password) {
        try {
            JSONObject data = new JSONObject().put("game", name).put("player", getActivity().getString(R.string.player));
            JSONObject requestJson = new JSONObject().put("action","ROOM_NEW").put("data", data).put("version", "v0.2");
            String requestString = requestJson.toString();

            postData(requestString, new VolleyCallback(){
                @Override
                public void onSuccess(String code, String data){
                    if (code == "200")
                        Toast.makeText(getActivity(), "Room created!", Toast.LENGTH_LONG).show();
                }
            });

        }
        catch (JSONException e) {
            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void roomSearch(View v) {

    }

    public void postData(String json, final VolleyCallback callback){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = getActivity().getString(R.string.default_url);
        final String requestBody = json;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("VOLLEY", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    String code = String.valueOf(response.statusCode);
                    String data = String.valueOf(response.data);
                    callback.onSuccess(code, data);
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        requestQueue.add(stringRequest);
    }

    public MainActivity getActivity() {
        return this;
    }
}
