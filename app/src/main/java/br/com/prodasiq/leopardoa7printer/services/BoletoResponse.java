package br.com.prodasiq.leopardoa7printer.services;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.POST;

/**
 * Created by crsproda on 14/12/2016.
 */

public class BoletoResponse extends StringRequest {

    private static final String BOLETO_REQUEST_URL = "http://localhost:8080/PROJETOS/service_nfe/php/boletos-exec.php";

    private Map<String,String> params;

    public BoletoResponse(Response.Listener<String> listener){


        super(POST,BOLETO_REQUEST_URL,listener,null);
        params = new HashMap<>();

        params.put("act","busca_impressao");

    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
