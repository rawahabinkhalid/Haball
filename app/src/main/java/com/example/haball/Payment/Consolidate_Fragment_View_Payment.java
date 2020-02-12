package com.example.haball.Payment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.haball.Distributor.ui.payments.MyJsonArrayRequest;
import com.example.haball.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Consolidate_Fragment_View_Payment extends Fragment {
    private TextView consolidated_view_invoice_id, consolidated_view_invoice_date, consolidated_view_invoice_amount, consolidated_view_company, consolidated_view_status;
    private String Token, DistributorId;
    private RecyclerView rv_payment_consolidate;
    private String URL_CONSOLIDATE_PAYMENTS_DETAILS = "http://175.107.203.97:4008/api/consolidatedinvoices/GetConsolidatedInvoiceByID/";
    private String ConsolidatedInvoiceId;
    private List<Consolidate_Fragment_Model> ConsolidatePaymentsDetailsList = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public Consolidate_Fragment_View_Payment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_consolidate__fragment__view__payment, container, false);

        ConsolidatedInvoiceId = getArguments().getString("ConsolidateInvoiceId");
        Toast.makeText(getContext(), ConsolidatedInvoiceId, Toast.LENGTH_LONG).show();
        consolidated_view_invoice_id = root.findViewById(R.id.consolidated_view_invoice_id);
        consolidated_view_invoice_date = root.findViewById(R.id.consolidated_view_invoice_date);
        consolidated_view_invoice_amount = root.findViewById(R.id.consolidated_view_invoice_amount);
        consolidated_view_company = root.findViewById(R.id.consolidated_view_company);
        consolidated_view_status = root.findViewById(R.id.consolidated_view_status);

        rv_payment_consolidate = root.findViewById(R.id.rv_payment_consolidate);
        rv_payment_consolidate.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        rv_payment_consolidate.setLayoutManager(layoutManager);

        fetchConsolidatePaymentData();

        return root;
    }

    private void fetchConsolidatePaymentData() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        Token = sharedPreferences.getString("Login_Token", "");

        SharedPreferences sharedPreferences1 = this.getActivity().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        DistributorId = sharedPreferences1.getString("Distributor_Id", "");
        Log.i("DistributorId ", DistributorId);
        Log.i("Token", Token);
        URL_CONSOLIDATE_PAYMENTS_DETAILS = URL_CONSOLIDATE_PAYMENTS_DETAILS+ConsolidatedInvoiceId;
        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.GET, URL_CONSOLIDATE_PAYMENTS_DETAILS,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                try {
                    consolidated_view_invoice_id.setText(result.getString("ConsolidatedInvoiceNumber"));
                    String date = result.getString("CreatedDate").split("T")[0];
                    consolidated_view_invoice_date.setText(date);
                    DecimalFormat formatter1 = new DecimalFormat("#,###,##0.00");
                    String yourFormattedString1 = formatter1.format(Integer.parseInt(result.getString("PaidAmount")));
                    consolidated_view_invoice_amount.setText(yourFormattedString1);
                    consolidated_view_company.setText(result.getString("CompanyName"));
                    if(result.getString("Status").equals("0"))
                        consolidated_view_status.setText("Pending");
                    else if(result.getString("Status").equals("1"))
                        consolidated_view_status.setText("Unpaid");
                    else if(result.getString("Status").equals("2"))
                        consolidated_view_status.setText("Partially Paid");
                    else if(result.getString("Status").equals("3"))
                        consolidated_view_status.setText("Paid");
                    else if(result.getString("Status").equals("-1"))
                        consolidated_view_status.setText("Payment Processing");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    Log.i("Consolidate Array", result.getJSONArray("InvoiceDetails").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                Type type = new TypeToken<List<Consolidate_Fragment_Model>>(){}.getType();
                try {
                    ConsolidatePaymentsDetailsList = gson.fromJson(result.getJSONArray("InvoiceDetails").toString(),type);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mAdapter = new Consolidate_Fragment_View_Adapter(getContext(),ConsolidatePaymentsDetailsList);
                rv_payment_consolidate.setAdapter(mAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
        Volley.newRequestQueue(getContext()).add(sr);
    }

}