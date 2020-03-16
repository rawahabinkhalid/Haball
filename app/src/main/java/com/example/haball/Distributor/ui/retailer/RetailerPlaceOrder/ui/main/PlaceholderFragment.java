package com.example.haball.Distributor.ui.retailer.RetailerPlaceOrder.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

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
import com.android.volley.toolbox.Volley;
import com.example.haball.Distributor.ui.payments.MyJsonArrayRequest;
import com.example.haball.Distributor.ui.retailer.RetailerPlaceOrder.ui.main.Adapters.RetailerFragmentAdapter;
import com.example.haball.Distributor.ui.retailer.RetailerPlaceOrder.ui.main.Models.Retailer_Fragment_Model;
import com.example.haball.Distributor.ui.retailer.RetailerPlaceOrder.ui.main.Tabs.Order_Item;
import com.example.haball.Distributor.ui.retailer.RetailerPlaceOrder.ui.main.Tabs.Order_Summary;
import com.example.haball.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    private RecyclerView recyclerView, recyclerView1;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mAdapter1;
    private RecyclerView.LayoutManager layoutManager, layoutManager1;
    private String Token, DistributorId;
    private String URL_Retailer = "TO BE DONE"; // To be done
    private List<Retailer_Fragment_Model> RetailerList;
    private Button btn_next;
    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = null;
        switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
            case 1: {
                rootView = inflater.inflate(R.layout.fragment_rpoid_place_order, container, false);
                final ViewPager pager = getActivity().findViewById(R.id.view_pager_rpoid);
                Holderorders(rootView, pager);
                break;
            }

            case 2: {
                rootView = inflater.inflate(R.layout.fragment_rpoid_order_summary, container, false);
                break;
            }

        }
        return rootView;
    }

    private void Holderorders(final View root, ViewPager pager) {
        btn_next = root.findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentTransaction fragmentTransaction = (getActivity()).getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.add(R.id.main_container, new Order_Item());
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();

            }
        });
//        recyclerView = root.findViewById(R.id.rv_order_ledger);
//        recyclerView.setHasFixedSize(true);
//
//        layoutManager = new LinearLayoutManager(getContext());
//        recyclerView.setLayoutManager(layoutManager);
//        try {
//            fetchRetailers(pager);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        mAdapter = new RetailerFragmentAdapter(getContext(), RetailerList, pager);
//        recyclerView.setAdapter(mAdapter);

    }

    private void fetchRetailers(final ViewPager pager) throws JSONException {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        Token = sharedPreferences.getString("Login_Token", "");
        Log.i("Token", Token);

        SharedPreferences sharedPreferences1 = this.getActivity().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        DistributorId = sharedPreferences1.getString("Distributor_Id", "");
        Log.i("DistributorId ", DistributorId);
        if (!URL_Retailer.contains(DistributorId))
            URL_Retailer = URL_Retailer + DistributorId;

        MyJsonArrayRequest sr = new MyJsonArrayRequest(Request.Method.GET, URL_Retailer, null, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(JSONArray result) {
                Log.i("Retailer List", result.toString());
                Gson gson = new Gson();
                Type type = new TypeToken<List<Retailer_Fragment_Model>>() {
                }.getType();
                RetailerList = gson.fromJson(result.toString(), type);
                Log.i("RetailerList", String.valueOf(RetailerList));
                mAdapter = new RetailerFragmentAdapter(getContext(), RetailerList, pager);
                recyclerView.setAdapter(mAdapter);
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
                params.put("Content-Type", "application/json; charset=UTF-8");
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