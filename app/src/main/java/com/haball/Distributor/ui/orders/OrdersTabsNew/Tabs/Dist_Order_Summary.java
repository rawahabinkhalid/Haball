package com.haball.Distributor.ui.orders.OrdersTabsNew.Tabs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
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
import com.haball.Distributor.DistributorDashboard;
import com.haball.Distributor.ui.home.HomeFragment;
import com.haball.Distributor.ui.orders.OrdersTabsNew.Adapters.Order_Summary_Adapter_DistOrder;
import com.haball.Distributor.ui.orders.OrdersTabsNew.Models.OrderChildlist_Model_DistOrder;
import com.haball.Loader;
import com.haball.NonSwipeableViewPager;
import com.haball.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Dist_Order_Summary extends Fragment {

    private FragmentTransaction fragmentTransaction;
    private RecyclerView.Adapter mAdapter1;
    private RecyclerView.LayoutManager layoutManager1;
    private RecyclerView recyclerView1;
    private List<OrderChildlist_Model_DistOrder> selectedProductsDataList = new ArrayList<>();
    private List<String> selectedProductsQuantityList = new ArrayList<>();
    private String object_string, object_stringqty, Token, DistributorId, CompanyId, DealerCode;
    private String URL_CONFIRM_ORDERS = "http://175.107.203.97:4013/api/Orders/save";
    private String URL_SAVE_TEMPLATE = "http://175.107.203.97:4013/api/ordertemplate/save";
    private String URL_SAVE_DRAFT = "http://175.107.203.97:4013/api/Orders/savedraft";
    private Button btn_confirm, btn_draft, btn_add_product;
    private TextView discount_amount, total_amount;
    private float totalAmount;
    private ViewPager viewpager;
    private List<OrderChildlist_Model_DistOrder> temp_list = new ArrayList<>();
    private List<String> temp_listqty = new ArrayList<>();
    private Loader loader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_dist_order__summary, container, false);
        // gross_amount = view.findViewById(R.id.gross_amount);
        discount_amount = view.findViewById(R.id.discount_amount);
        // gst_amount = view.findViewById(R.id.gst_amount);
        total_amount = view.findViewById(R.id.total_amount);
        btn_draft = view.findViewById(R.id.btn_draft);
        btn_confirm = view.findViewById(R.id.btn_confirm);


        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

        SharedPreferences add_more_product = getContext().getSharedPreferences("add_more_product",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = add_more_product.edit();
        editor1.putString("add_more_product", "");
        editor1.apply();

        loader = new Loader(getContext());

        InputMethodManager imm = (InputMethodManager) (getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        btn_add_product = view.findViewById(R.id.btn_add_product);


        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                loader.showLoader();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loader.hideLoader();
                                try {
                                    requestConfirmOrder();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                SharedPreferences selectedProducts = getContext().getSharedPreferences("selectedProducts_distributor",
                                        Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = selectedProducts.edit();
                                editor.putString("selected_products", "");
                                editor.putString("selected_products_qty", "");
                                editor.apply();

                            }
                        }, 3000);
                    }
                });
            }
        });

//    }
//        });
//        btn_template.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("NewApi")
//            @Override
//            public void onClick(View view) {
//
//                try {
//                    requestSaveTemplate();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                SharedPreferences selectedProducts = getContext().getSharedPreferences("selectedProducts_distributor",
//                        Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = selectedProducts.edit();
//                editor.putString("selected_products", "");
//                editor.putString("selected_products_qty", "");
//                editor.apply();
//            }
//        });
        btn_draft.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                loader.showLoader();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loader.hideLoader();
                                try {
                                    requestSaveDraft();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                SharedPreferences selectedProducts = getContext().getSharedPreferences("selectedProducts_distributor",
                                        Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = selectedProducts.edit();
                                editor.putString("selected_products", "");
                                editor.putString("selected_products_qty", "");
                                editor.apply();


                            }
                        }, 3000);
                    }
                });
            }
        });


        btn_add_product.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                loader.showLoader();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loader.hideLoader();
                                NonSwipeableViewPager viewPager = getActivity().findViewById(R.id.view_pager5);
//                SharedPreferences selectedProducts = getContext().getSharedPreferences("selectedProducts_distributor",
//                        Context.MODE_PRIVATE);
//                Gson gson = new Gson();
//                object_stringqty = selectedProducts.getString("selected_products_qty", "");
//                object_string = selectedProducts.getString("selected_products", "");
//                Type type = new TypeToken<List<OrderChildlist_Model>>() {
//                }.getType();
//                Type typeString = new TypeToken<List<String>>() {
//                }.getType();
//                selectedProductsDataList = gson.fromJson(object_string, type);
//                selectedProductsQuantityList = gson.fromJson(object_stringqty, typeString);
//                        if (selectedProductsDataList.size() > 0) {
//                            for (int i = 0; i < selectedProductsDataList.size(); i++) {
//                                Log.i("unit price", selectedProductsDataList.get(i).getProductUnitPrice());
//                                Log.i("qty", selectedProductsQuantityList.get(i));
//                                if (!selectedProductsDataList.get(i).getProductUnitPrice().equals("") && !selectedProductsQuantityList.get(i).equals(""))
//                                    grossAmount += Float.parseFloat(selectedProductsDataList.get(i).getProductUnitPrice()) * Float.parseFloat(selectedProductsQuantityList.get(i));
//                            }
                                float grossAmount = 0;
                                if (selectedProductsDataList == null) {
                                    Log.i("debugOrder_ListIsNull", "selected product list is null");
                                    SharedPreferences selectedProducts = getContext().getSharedPreferences("selectedProducts_distributor",
                                            Context.MODE_PRIVATE);
                                    Gson gson = new Gson();
                                    object_string = selectedProducts.getString("selected_products", "");
                                    object_stringqty = selectedProducts.getString("selected_products_qty", "");
                                    Log.i("object_string", object_string);
                                    Log.i("object_stringqty", object_stringqty);
                                    Type type = new TypeToken<List<OrderChildlist_Model_DistOrder>>() {
                                    }.getType();
                                    Type typeQty = new TypeToken<List<String>>() {
                                    }.getType();
                                    selectedProductsDataList = gson.fromJson(object_string, type);
                                    selectedProductsQuantityList = gson.fromJson(object_stringqty, typeQty);
                                }
                                if (selectedProductsDataList.size() > 0) {
                                    for (int i = 0; i < selectedProductsDataList.size(); i++) {
//                        Log.i("unit price", selectedProductsDataList.get(i).getProductUnitPrice());
//                        Log.i("qty", selectedProductsQuantityList.get(i));
                                        if (!selectedProductsDataList.get(i).getUnitPrice().equals("") && !selectedProductsQuantityList.get(i).equals(""))
                                            grossAmount += Float.parseFloat(selectedProductsDataList.get(i).getUnitPrice()) * Float.parseFloat(selectedProductsQuantityList.get(i));
                                    }
                                    SharedPreferences add_more_product = getContext().getSharedPreferences("add_more_product",
                                            Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor1 = add_more_product.edit();
                                    editor1.putString("add_more_product", "fromAddMore");
                                    editor1.apply();

                                    SharedPreferences grossamount = getContext().getSharedPreferences("grossamount",
                                            Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = grossamount.edit();
                                    editor.putString("grossamount", String.valueOf(grossAmount));
                                    editor.apply();
//                    Toast.makeText(getContext(), "Total Amount: " + grossAmount, Toast.LENGTH_SHORT).show();
                                    grossAmount = 0;
                                    viewPager.setCurrentItem(0);
                                    FragmentTransaction fragmentTransaction = (getActivity()).getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.add(R.id.main_container, new Dist_OrderPlace());
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
//                try {
//                    requestSaveTemplate();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                SharedPreferences selectedProducts = getContext().getSharedPreferences("selectedProducts_distributor",
//                        Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = selectedProducts.edit();
//                editor.putString("selected_products", "");
//                editor.putString("selected_products_qty", "");
//                editor.apply();
                                }
                            }
                        }, 3000);
                    }
                });

            }
        });


        qtyChanged();
        new MyAsyncTask().execute();

        recyclerView1 = view.findViewById(R.id.rv_orders_summary);
        recyclerView1.setHasFixedSize(false);
        layoutManager1 = new LinearLayoutManager(getContext());
        recyclerView1.setLayoutManager(layoutManager1);

        mAdapter1 = new Order_Summary_Adapter_DistOrder(getActivity(), getContext(), selectedProductsDataList, selectedProductsQuantityList, btn_confirm, btn_draft);
        recyclerView1.setAdapter(mAdapter1);
        recyclerView1.setNestedScrollingEnabled(false);

        Log.i("aaaaaa", String.valueOf(mAdapter1));

        return view;

    }

    private void requestConfirmOrder() throws JSONException {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        Token = sharedPreferences.getString("Login_Token", "");

        SharedPreferences sharedPreferences1 = this.getActivity().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        DistributorId = sharedPreferences1.getString("Distributor_Id", "");

        SharedPreferences sharedPreferences4 = this.getActivity().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        DealerCode = sharedPreferences4.getString("DealerCode", "");

        SharedPreferences sharedPreferences2 = this.getActivity().getSharedPreferences("CompanyInfo",
                Context.MODE_PRIVATE);
        CompanyId = sharedPreferences2.getString("CompanyId", "");

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < selectedProductsDataList.size(); i++) {
            JSONObject obj = new JSONObject();

            if (!selectedProductsQuantityList.get(i).equals("0") && !selectedProductsQuantityList.get(i).equals("")) {
                float tempAmount = Float.parseFloat(selectedProductsDataList.get(i).getUnitPrice());
                if (selectedProductsDataList.get(i).getDiscountAmount() != null)
                    tempAmount = Float.parseFloat(selectedProductsDataList.get(i).getDiscountAmount());
                tempAmount *= Float.parseFloat(selectedProductsQuantityList.get(i));

                float totalProductAmount = Float.parseFloat(selectedProductsDataList.get(i).getUnitPrice());
                totalProductAmount *= Float.parseFloat(selectedProductsQuantityList.get(i));

                obj.put("ID", 0);
                obj.put("ProductId", selectedProductsDataList.get(i).getID());
                obj.put("ProductCode", selectedProductsDataList.get(i).getCode());
                obj.put("ProductName", selectedProductsDataList.get(i).getTitle());
                obj.put("ProductShortDescription", selectedProductsDataList.get(i).getShortDescription());
                obj.put("UnitPrice", selectedProductsDataList.get(i).getUnitPrice());
                obj.put("IsSelected", true);
                obj.put("DiscountedAmount", tempAmount);
                obj.put("OrderQty", selectedProductsQuantityList.get(i));
                obj.put("DiscountAmount", selectedProductsDataList.get(i).getDiscountAmount());
                obj.put("UOM", selectedProductsDataList.get(i).getUOMId());
                obj.put("UOMTitle", selectedProductsDataList.get(i).getUOMTitle());
                obj.put("Discount", selectedProductsDataList.get(i).getDiscountId());
                obj.put("TotalPrice", totalProductAmount);
                jsonArray.put(obj);
            }
        }
        Log.i("Array", String.valueOf(jsonArray));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("DiscountAmount", 0);
        jsonObject.put("ID", 0);
        jsonObject.put("Status", 0);
        jsonObject.put("OrderDetails", jsonArray);
        jsonObject.put("CompanyId", CompanyId);
        jsonObject.put("DistributorId", DistributorId);
        jsonObject.put("TransportTypeIdFreightCharges", 0);
        jsonObject.put("parentCategory", 0);
        jsonObject.put("ProductName", "");
        jsonObject.put("NetPrice", totalAmount);
        jsonObject.put("Discount", 0);
        jsonObject.put("TotalPrice", totalAmount);
        //  jsonObject.put("TotalGST", gst_amount);
        jsonObject.put("TotalDiscountAmount", 0);
        jsonObject.put("BillingAddressId", 638);
        jsonObject.put("BillingAddress1", "66565");
        jsonObject.put("BillingCountryId", 1);
        jsonObject.put("BillingProvinceId", 1);
        jsonObject.put("BillingCityId", 1);
        jsonObject.put("BillingPostCode", "00000");
        jsonObject.put("ShippingAddressId", 637);
        jsonObject.put("ShippingAddress1", "fsdfsf");
        jsonObject.put("ShippingCountryId", 1);
        jsonObject.put("ShippingProvinceId", 1);
        jsonObject.put("ShippingCityId", 1);
        jsonObject.put("ShippingPostCode", "00000");
        jsonObject.put("TransportTypeId", 1);
        jsonObject.put("PaymentTermId", 1);
        jsonObject.put("DistributorDealerCode", DealerCode);
        loader.showLoader();
        Log.i("jsonObject", String.valueOf(jsonObject));
        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST, URL_CONFIRM_ORDERS, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject result) {
                loader.hideLoader();
                Log.i("RESPONSE ORDER .. ", result.toString());
                try {
                    SharedPreferences grossamount = getContext().getSharedPreferences("grossamount",
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = grossamount.edit();
                    editor.clear();
                    editor.apply();
                    SharedPreferences selectedProducts_distributor = getContext().getSharedPreferences("selectedProducts_distributor",
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor selectedProducts_distributor_editor = selectedProducts_distributor.edit();
                    selectedProducts_distributor_editor.clear();
                    selectedProducts_distributor_editor.apply();

                    Toast.makeText(getContext(), "Order Request ID " + result.get("OrderNumber") + " has been submitted successfully and sent for approval.", Toast.LENGTH_LONG).show();
                    Intent login_intent = new Intent(getActivity(), DistributorDashboard.class);
                    startActivity(login_intent);
                    getActivity().finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                refreshRetailerInfo();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                printErrorMessage(error);
                error.printStackTrace();
                loader.hideLoader();
                refreshRetailerInfo();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "bearer " + Token);
                params.put("Content-Type", "application/json");
                return params;
            }
        };

        Volley.newRequestQueue(getContext()).add(sr);
    }

    private void requestSaveTemplate() throws JSONException {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        Token = sharedPreferences.getString("Login_Token", "");

        SharedPreferences sharedPreferences1 = this.getActivity().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        DistributorId = sharedPreferences1.getString("Distributor_Id", "");

        SharedPreferences sharedPreferences4 = this.getActivity().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        DealerCode = sharedPreferences4.getString("DealerCode", "");

        SharedPreferences sharedPreferences2 = this.getActivity().getSharedPreferences("CompanyInfo",
                Context.MODE_PRIVATE);
        CompanyId = sharedPreferences2.getString("CompanyId", "");

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < selectedProductsDataList.size(); i++) {
            JSONObject obj = new JSONObject();

            if (!selectedProductsQuantityList.get(i).equals("0") && !selectedProductsQuantityList.get(i).equals("")) {
                float tempAmount = Float.parseFloat(selectedProductsDataList.get(i).getUnitPrice());
                if (selectedProductsDataList.get(i).getDiscountAmount() != null)
                    tempAmount = Float.parseFloat(selectedProductsDataList.get(i).getDiscountAmount());
                tempAmount *= Float.parseFloat(selectedProductsQuantityList.get(i));

                float totalProductAmount = Float.parseFloat(selectedProductsDataList.get(i).getUnitPrice());
                totalProductAmount *= Float.parseFloat(selectedProductsQuantityList.get(i));

                obj.put("ID", 0);
                obj.put("ProductId", selectedProductsDataList.get(i).getID());
                obj.put("ProductCode", selectedProductsDataList.get(i).getCode());
                obj.put("ProductName", selectedProductsDataList.get(i).getTitle());
                obj.put("ProductShortDescription", selectedProductsDataList.get(i).getShortDescription());
                obj.put("UnitPrice", selectedProductsDataList.get(i).getUnitPrice());
                obj.put("IsSelected", true);
                obj.put("DiscountedAmount", tempAmount);
                obj.put("OrderQty", selectedProductsQuantityList.get(i));
                obj.put("DiscountAmount", selectedProductsDataList.get(i).getDiscountAmount());
                obj.put("UOM", selectedProductsDataList.get(i).getUOMId());
                obj.put("UOMTitle", selectedProductsDataList.get(i).getUOMTitle());
                obj.put("Discount", selectedProductsDataList.get(i).getDiscountId());
                obj.put("TotalPrice", totalProductAmount);
                jsonArray.put(obj);
            }
        }
        Log.i("Array", String.valueOf(jsonArray));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("DistributorId", DistributorId);
        jsonObject.put("CompanyId", CompanyId);
        jsonObject.put("Name", "name");
        jsonObject.put("Status", 1);
        jsonObject.put("OrderTemplateDetails", jsonArray);
        loader.showLoader();

        Log.i("jsonObject", String.valueOf(jsonObject));
        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST, URL_SAVE_TEMPLATE, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject result) {
                loader.hideLoader();
                Log.i("RESPONSE ORDER .. ", result.toString());
                SharedPreferences grossamount = getContext().getSharedPreferences("grossamount",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = grossamount.edit();
                editor.clear();
                editor.apply();
                SharedPreferences selectedProducts_distributor = getContext().getSharedPreferences("selectedProducts_distributor",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor selectedProducts_distributor_editor = selectedProducts_distributor.edit();
                selectedProducts_distributor_editor.clear();
                selectedProducts_distributor_editor.apply();

                Toast.makeText(getContext(), "Order has been saved as template successfully", Toast.LENGTH_LONG).show();
                SharedPreferences tabsFromDraft = getContext().getSharedPreferences("OrderTabsFromDraft",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editorOrderTabsFromDraft = tabsFromDraft.edit();
                editorOrderTabsFromDraft.putString("TabNo", "1");
                editorOrderTabsFromDraft.apply();

                Intent login_intent = new Intent(getActivity(), DistributorDashboard.class);
                startActivity(login_intent);
                getActivity().finish();

                refreshRetailerInfo();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                printErrorMessage(error);
                error.printStackTrace();
                loader.hideLoader();
                refreshRetailerInfo();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "bearer " + Token);
                params.put("Content-Type", "application/json");
                return params;
            }
        };

        Volley.newRequestQueue(getContext()).add(sr);
    }

    private void requestSaveDraft() throws JSONException {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        Token = sharedPreferences.getString("Login_Token", "");

        SharedPreferences sharedPreferences1 = this.getActivity().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        DistributorId = sharedPreferences1.getString("Distributor_Id", "");

        SharedPreferences sharedPreferences4 = this.getActivity().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        DealerCode = sharedPreferences4.getString("DealerCode", "");

        SharedPreferences sharedPreferences2 = this.getActivity().getSharedPreferences("CompanyInfo",
                Context.MODE_PRIVATE);
        CompanyId = sharedPreferences2.getString("CompanyId", "");

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < selectedProductsDataList.size(); i++) {
            JSONObject obj = new JSONObject();

            if (!selectedProductsQuantityList.get(i).equals("0") && !selectedProductsQuantityList.get(i).equals("")) {
                float tempAmount = Float.parseFloat(selectedProductsDataList.get(i).getUnitPrice());
                if (selectedProductsDataList.get(i).getDiscountAmount() != null)
                    tempAmount = Float.parseFloat(selectedProductsDataList.get(i).getDiscountAmount());
                tempAmount *= Float.parseFloat(selectedProductsQuantityList.get(i));

                float totalProductAmount = Float.parseFloat(selectedProductsDataList.get(i).getUnitPrice());
                totalProductAmount *= Float.parseFloat(selectedProductsQuantityList.get(i));

                obj.put("ID", 0);
                obj.put("ProductId", selectedProductsDataList.get(i).getID());
                obj.put("ProductCode", selectedProductsDataList.get(i).getCode());
                obj.put("ProductName", selectedProductsDataList.get(i).getTitle());
                obj.put("ProductShortDescription", selectedProductsDataList.get(i).getShortDescription());
                obj.put("UnitPrice", selectedProductsDataList.get(i).getUnitPrice());
                obj.put("IsSelected", true);
                obj.put("DiscountedAmount", tempAmount);
                obj.put("OrderQty", selectedProductsQuantityList.get(i));
                obj.put("DiscountAmount", selectedProductsDataList.get(i).getDiscountAmount());
                obj.put("UOM", selectedProductsDataList.get(i).getUOMId());
                obj.put("UOMTitle", selectedProductsDataList.get(i).getUOMTitle());
                obj.put("Discount", selectedProductsDataList.get(i).getDiscountId());
                obj.put("TotalPrice", totalProductAmount);
                jsonArray.put(obj);
            }
        }
        Log.i("Array", String.valueOf(jsonArray));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("DiscountAmount", 0);
        jsonObject.put("ID", 0);
        jsonObject.put("Status", 3);
        jsonObject.put("OrderDetails", jsonArray);
        jsonObject.put("CompanyId", CompanyId);
        jsonObject.put("DistributorId", DistributorId);
        jsonObject.put("TransportTypeIdFreightCharges", 0);
        jsonObject.put("parentCategory", 0);
        jsonObject.put("ProductName", "");
        jsonObject.put("NetPrice", totalAmount);
        jsonObject.put("Discount", totalAmount);
        jsonObject.put("TotalPrice", totalAmount);
        // jsonObject.put("TotalGST", gst_amount);
        jsonObject.put("TotalDiscountAmount", 0);
        jsonObject.put("ShippingAddressId", 637);
        jsonObject.put("ShippingAddress1", "fsdfsf");
        jsonObject.put("ShippingCountryId", 1);
        jsonObject.put("ShippingProvinceId", 1);
        jsonObject.put("ShippingCityId", 1);
        jsonObject.put("ShippingPostCode", "00000");
        jsonObject.put("BillingAddressId", 638);
        jsonObject.put("BillingAddress1", "66565");
        jsonObject.put("BillingCountryId", 1);
        jsonObject.put("BillingProvinceId", 1);
        jsonObject.put("BillingCityId", 1);
        jsonObject.put("BillingPostCode", "00000");
        loader.showLoader();

        Log.i("jsonObject", String.valueOf(jsonObject));
        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST, URL_SAVE_DRAFT, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject result) {
                loader.hideLoader();
                Log.i("RESPONSE ORDER .. ", result.toString());
                try {
                    SharedPreferences grossamount = getContext().getSharedPreferences("grossamount",
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = grossamount.edit();
                    editor.clear();
                    editor.apply();
                    SharedPreferences selectedProducts_distributor = getContext().getSharedPreferences("selectedProducts_distributor",
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor selectedProducts_distributor_editor = selectedProducts_distributor.edit();
                    selectedProducts_distributor_editor.clear();
                    selectedProducts_distributor_editor.apply();

                    Toast.makeText(getContext(), "Order Request ID " + result.get("OrderNumber") + " has been saved as draft successfully.", Toast.LENGTH_LONG).show();
                    SharedPreferences tabsFromDraft = getContext().getSharedPreferences("OrderTabsFromDraft",
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorOrderTabsFromDraft = tabsFromDraft.edit();
                    editorOrderTabsFromDraft.putString("TabNo", "1");
                    editorOrderTabsFromDraft.apply();

                    Intent login_intent = new Intent(getActivity(), DistributorDashboard.class);
                    startActivity(login_intent);
                    getActivity().finish();
//                    HomeFragment homeFragment = new HomeFragment();
//
//                    Bundle args = new Bundle();
//                    args.putInt("section_number", 2);
//                    homeFragment.setArguments(args);
//                    fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction.add(R.id.main_container, homeFragment);
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                refreshRetailerInfo();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                printErrorMessage(error);
                loader.hideLoader();
                error.printStackTrace();
                refreshRetailerInfo();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "bearer " + Token);
                params.put("Content-Type", "application/json");
                return params;
            }
        };

        Volley.newRequestQueue(getContext()).add(sr);
    }

    private void refreshRetailerInfo() {
        SharedPreferences retailerInfo = getContext().getSharedPreferences("RetailerInfo",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = retailerInfo.edit();
        editor.putString("RetailerID", "");
        editor.apply();

//        fragmentTransaction = (getActivity()).getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.add(R.id.main_container, new Distributor());
//        fragmentTransaction.commit();
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            while (getContext() != null) {
//                Log.i("async", "in async");
                SharedPreferences selectedProducts = getContext().getSharedPreferences("selectedProducts_distributor",
                        Context.MODE_PRIVATE);
                object_string = selectedProducts.getString("selected_products", "");
                Gson gson = new Gson();
                Type type = new TypeToken<List<OrderChildlist_Model_DistOrder>>() {
                }.getType();
                temp_list = gson.fromJson(object_string, type);
                object_stringqty = selectedProducts.getString("selected_products_qty", "");
//                Log.i("qty_async", object_stringqty);
                Type typestr = new TypeToken<List<String>>() {
                }.getType();
                temp_listqty = gson.fromJson(object_stringqty, typestr);
                if (!object_stringqty.equals("")) {
                    if (selectedProductsQuantityList != null) {
                        if (temp_listqty != selectedProductsQuantityList) {
                            selectedProductsQuantityList = temp_listqty;
                            break;
                        }
                    }
//                    break;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (getContext() != null) {
//                Log.i("async", "in async else");
                qtyChanged();
                new MyAsyncTask().execute();
            }
//            mAdapter1 = new OrdersItemsAdapter(getContext(), ProductsDataList);
//            itemsSelect_Rv.setAdapter(mAdapter1);

//            ParentListAdapter adapter = new ParentListAdapter(getActivity(), initData());
//            adapter.setParentClickableViewAnimationDefaultDuration();
//            adapter.setParentAndIconExpandOnClick(true);
//            recyclerView.setAdapter(adapter);
        }
    }

    private void qtyChanged() {
        SharedPreferences selectedProducts = getContext().getSharedPreferences("selectedProducts_distributor",
                Context.MODE_PRIVATE);
        Gson gson = new Gson();
        object_string = selectedProducts.getString("selected_products", "");
        object_stringqty = selectedProducts.getString("selected_products_qty", "");
//        Log.i("object_string", object_string);
//        Log.i("object_stringqty", object_stringqty);
        Type type = new TypeToken<List<OrderChildlist_Model_DistOrder>>() {
        }.getType();
        Type typeQty = new TypeToken<List<String>>() {
        }.getType();
        selectedProductsDataList = gson.fromJson(object_string, type);
        selectedProductsQuantityList = gson.fromJson(object_stringqty, typeQty);

        SharedPreferences grossamount = getContext().getSharedPreferences("grossamount",
                Context.MODE_PRIVATE);
//        gross_amount.setText(grossamount.getString("grossamount", "0"));
        float temp_grossAmount = Float.parseFloat(grossamount.getString("grossamount", "0"));
        // gross_amount.setText(String.format("%.0f", temp_grossAmount));
        discount_amount.setText("0.00");

//        float gstAmount = (Float.parseFloat(grossamount.getString("grossamount", "")) * 17) / 100;
        float gstAmount = 0;
        totalAmount = Float.parseFloat(grossamount.getString("grossamount", "0")) + gstAmount;

//        gst_amount.setText(String.valueOf(gstAmount));
        total_amount.setText(String.format("%.0f", totalAmount));


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
