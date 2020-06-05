package com.example.haball.Retailor.ui.Dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.haball.R;
import com.example.haball.Retailer_Login.RetailerLogin;
import com.example.haball.Retailor.RetailorDashboard;
import com.example.haball.Retailor.ui.Make_Payment.CreatePaymentRequestFragment;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class View_Payment_Fragment extends Fragment {

    private String PaymentsRequestId;
    private String PAYMENT_REQUEST_URL = "http://175.107.203.97:4014/api/prepaidrequests/";
    private String Token;
    private TextInputEditText txt_heading, txt_paymentid, txt_created_date, txt_transaction_date, txt_bname, txt_authorization, txt_settlement, txt_amount, txt_status, txt_transaction_charges;
    private Button btn_make_payment, btn_vreciept, btn_back;
    private FragmentTransaction fragmentTransaction;

    public View_Payment_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = null;
        root = inflater.inflate(R.layout.fragment_view__payment_, container, false);
        SharedPreferences sharedPreferences3 = getContext().getSharedPreferences("paymentsRequestListID",
                Context.MODE_PRIVATE);
        PaymentsRequestId = sharedPreferences3.getString("paymentsRequestListID", "");
//        Log.i("PaymentsRequestId12", PaymentsRequestId);
        if (!PAYMENT_REQUEST_URL.contains(PaymentsRequestId))
            PAYMENT_REQUEST_URL = PAYMENT_REQUEST_URL + PaymentsRequestId;


        txt_heading = root.findViewById(R.id.txt_heading);
        txt_paymentid = root.findViewById(R.id.txt_paymentid);
        txt_created_date = root.findViewById(R.id.txt_created_date);
        txt_transaction_date = root.findViewById(R.id.txt_transaction_date);
        txt_bname = root.findViewById(R.id.txt_bname);
        txt_authorization = root.findViewById(R.id.txt_authorization);
        txt_settlement = root.findViewById(R.id.txt_settlement);
        txt_amount = root.findViewById(R.id.txt_amount);
        txt_status = root.findViewById(R.id.txt_status);
        txt_transaction_charges = root.findViewById(R.id.txt_transaction_charges);

        btn_make_payment = root.findViewById(R.id.btn_make_payment);
        btn_vreciept = root.findViewById(R.id.btn_vreciept);
        btn_back = root.findViewById(R.id.btn_back);

        txt_heading.setEnabled(false);
        txt_paymentid.setEnabled(false);
        txt_created_date.setEnabled(false);
        txt_transaction_date.setEnabled(false);
        txt_bname.setEnabled(false);
        txt_authorization.setEnabled(false);
        txt_settlement.setEnabled(false);
        txt_amount.setEnabled(false);
        txt_status.setEnabled(false);
        txt_transaction_charges.setEnabled(false);

        btn_make_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container_ret, new CreatePaymentRequestFragment()).addToBackStack("tag");
                ;
                fragmentTransaction.commit();

            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login_intent = new Intent(getActivity(), RetailorDashboard.class);
                startActivity(login_intent);
                getActivity().finish();
            }
        });

        btn_vreciept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewReceeiptPDFRequest viewPDFRequest = new ViewReceeiptPDFRequest();
                try {
                    viewPDFRequest.viewPDF(getContext(), PaymentsRequestId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            fetchPaymentData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        PaymentsRequestId = getArguments().getString("PaymentsRequestId");
        return root;

    }

    private void fetchPaymentData() throws JSONException {

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        Token = sharedPreferences.getString("Login_Token", "");

        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.GET, PAYMENT_REQUEST_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                Log.i("result", String.valueOf(result));
                try {
                    txt_heading.setText(String.valueOf(result.get("CompanyName")));
                    txt_paymentid.setText(String.valueOf(result.get("PrePaidNumber")));
                    txt_created_date.setText(String.valueOf(result.get("CreatedDate")));
                    txt_transaction_date.setText(String.valueOf(result.get("TransactionDate")));
                    txt_bname.setText(String.valueOf(result.get("BankName")));
                    txt_authorization.setText(String.valueOf(result.get("AuthID")));
                    txt_settlement.setText(String.valueOf(result.get("SettlementID")));
                    txt_amount.setText(String.valueOf(result.get("PaidAmount")));
                    txt_status.setText(String.valueOf(result.get("Status")));
                    txt_transaction_charges.setText(String.valueOf(result.get("TransactionCharges")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                printErrorMessage(error);
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "bearer " + Token);
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getContext()).add(sr);
    }

    private void printErrorMessage(VolleyError error) {
        if (getContext() != null) {
            if (error instanceof NetworkError) {
                Toast.makeText(getContext(), "Network Error !", Toast.LENGTH_LONG).show();
            } else if (error instanceof ServerError) {
                Toast.makeText(getContext(), "Server Error !", Toast.LENGTH_LONG).show();
            } else if (error instanceof AuthFailureError) {
                Toast.makeText(getContext(), "Auth Failure Error !", Toast.LENGTH_LONG).show();
            } else if (error instanceof ParseError) {
                Toast.makeText(getContext(), "Parse Error !", Toast.LENGTH_LONG).show();
            } else if (error instanceof NoConnectionError) {
                Toast.makeText(getContext(), "No Connection Error !", Toast.LENGTH_LONG).show();
            } else if (error instanceof TimeoutError) {
                Toast.makeText(getContext(), "Timeout Error !", Toast.LENGTH_LONG).show();
            }

            if (error.networkResponse != null && error.networkResponse.data != null) {
                try {
                    String message = "";
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    Log.i("responseBody", responseBody);
                    JSONObject data = new JSONObject(responseBody);
                    Log.i("data", String.valueOf(data));
                    Iterator<String> keys = data.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        message = message + data.get(key) + "\n";
                    }
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
