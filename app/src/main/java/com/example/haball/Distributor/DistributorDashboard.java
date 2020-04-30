package com.example.haball.Distributor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.haball.Distributor.ui.Fragment_Notification.FragmentNotification;
import com.example.haball.Distributor.ui.Fragment_Notification.NotificationAdapter;
import com.example.haball.Distributor.ui.Fragment_Notification.NotificationModel;
import com.example.haball.Distributor.ui.home.HomeFragment;
import com.example.haball.Distributor.ui.orders.OrdersTabsNew.Order_PlaceOrder;
import com.example.haball.Distributor.ui.payments.ConsolidatedPaymentsFragment;
import com.example.haball.Distributor.ui.payments.CreatePaymentRequestFragment;
import com.example.haball.Distributor.ui.payments.Payments_Fragment;
import com.example.haball.Distributor.ui.payments.ProofOfPaymentsDashboardFragment;
import com.example.haball.Distributor.ui.profile.Distributor_Profile;
import com.example.haball.Distributor.ui.retailer.Payment.RetailerPaymentDashboard;
import com.example.haball.Distributor.ui.retailer.RetailerFragment;
import com.example.haball.Distributor.ui.retailer.RetailerOrder.RetailerOrderDashboard;
import com.example.haball.Distributor.ui.shipments.Shipments_Fragments;
import com.example.haball.Distributor.ui.support.SupportFragment;
import com.example.haball.Distributor.ui.terms_and_conditions.TermsAndConditionsFragment;
import com.example.haball.R;
import com.example.haball.Select_User.Register_Activity;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.techatmosphere.expandablenavigation.model.ChildModel;
import com.techatmosphere.expandablenavigation.model.HeaderModel;
import com.techatmosphere.expandablenavigation.view.ExpandableNavigationListView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DistributorDashboard extends AppCompatActivity {

    private TextView tv_username, tv_user_company;
    private FragmentTransaction fragmentTransaction;
    private DrawerLayout drawer;
    private ExpandableNavigationListView navigationExpandableListView;
    private String username, companyname, Token, ID;
    private ImageButton notification_icon;
    private String URL_Notification = "http://175.107.203.97:4013/api/useralert/";
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SharedPreferences grossamount = getApplication().getSharedPreferences("grossamount",
//                Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = grossamount.edit();
//        editor.clear();
//        editor.apply();
//        SharedPreferences selectedProducts_distributor = getApplication().getSharedPreferences("selectedProducts_distributor",
//                Context.MODE_PRIVATE);
//        SharedPreferences.Editor selectedProducts_distributor_editor = selectedProducts_distributor.edit();
//        selectedProducts_distributor_editor.clear();
//        selectedProducts_distributor_editor.apply();

        setContentView(R.layout.activity_distributor_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        SharedPreferences sharedPreferences = this.getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        companyname = sharedPreferences.getString("CompanyName", "");
        Token = sharedPreferences.getString("Login_Token", "");
        ID = sharedPreferences.getString("ID", "");

        tv_username = toolbar.findViewById(R.id.tv_username);
        tv_user_company = toolbar.findViewById(R.id.tv_user_company);
        notification_icon = toolbar.findViewById(R.id.notification_icon);

        notification_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container, new FragmentNotification());
                fragmentTransaction.commit();
            }
        });

        getNotificationCount();

        tv_username.setText("Hi, " + username);
        tv_user_company.setText(companyname);

        drawer = findViewById(R.id.drawer_layout);

        final NavigationView navigationView = findViewById(R.id.nav_view);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container, new HomeFragment());
        fragmentTransaction.commit();

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, new HomeFragment());
        fragmentTransaction.commit();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
//        drawer.setScrimColor(Color.parseColor("#33000000"));
        drawer.setScrimColor(getResources().getColor(android.R.color.transparent));

        new MyAsyncTask().execute();

        toggle.syncState();
        navigationExpandableListView = findViewById(R.id.expandable_navigation);
        navigationExpandableListView
                .init(this)
                .addHeaderModel(new HeaderModel("Dashboard"))
                .addHeaderModel(
                        new HeaderModel("Orders")
                                .addChildModel(new ChildModel("\tPlace Order")))
                .addHeaderModel(
                        new HeaderModel("Payment")
//                                  .addChildModel(new ChildModel("\tPayments Summary"))
                                .addChildModel(new ChildModel("\tConsolidate Payments"))
                                .addChildModel(new ChildModel("\tMake Payment"))
                                .addChildModel(new ChildModel("\tPayment Ledger"))
                                .addChildModel(new ChildModel("\tProof of Payments"))

                )
                .addHeaderModel(new HeaderModel("Shipment"))
                .addHeaderModel(new HeaderModel("Retailer Management")
                        .addChildModel(new ChildModel("\tRetailer"))
                        .addChildModel(new ChildModel("\tOrder on Behalf"))
                        .addChildModel(new ChildModel("\tRetailer Payments")))
                .addHeaderModel(new HeaderModel("Profile"))
                .addHeaderModel(new HeaderModel("Support"))
                .addHeaderModel(new HeaderModel("Logout"))
                .addHeaderModel(new HeaderModel("\n\n\n\nTerms And Conditions"))
                .build()
                .addOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                        navigationExpandableListView.setSelected(groupPosition);

                        if (id == 0) {
                            Log.i("Dashboard", "Dashboard Activity"); //DONE
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.main_container, new HomeFragment());
                            fragmentTransaction.commit();
                            drawer.closeDrawer(GravityCompat.START);
                        } else if (id == 1) {
                            Log.i("Orders", "Orders Activity");
                        } else if (id == 2) {
                            Log.i("Payments", "Payments Activity");//DONE
                            navigationView.setItemTextColor(ColorStateList.valueOf(Color.RED));
                        } else if (id == 3) {
                            Log.i("Shipment", "Shipment Activity");
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.main_container, new Shipments_Fragments()).addToBackStack("tag");
                            fragmentTransaction.commit();


                            //jsonObject.put("CompanyName", null);
//                            fragmentTransaction.commit();
                            //jsonObject.put("CompanyName", null);
//        jsonObject.put("CreateDateFrom", null);
//        jsonObject.put("CreateDateTo", null);
//        jsonObject.put("Status", null);
//        jsonObject.put("AmountMin", null);
//        jsonObject.put("AmountMax", null);
                            drawer.closeDrawer(GravityCompat.START);
                        } else if (id == 4) {
                            Log.i("Retailer", "Retailer Activity");


                        } else if (id == 5) {
                            Log.i("Profile", "Profile Activity");
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.main_container, new Distributor_Profile()).addToBackStack("tag");
                            ;
                            fragmentTransaction.commit();
                            drawer.closeDrawer(GravityCompat.START);
                        } else if (id == 6) {
                            Log.i("Suppport", "Support Activity"); //DONE
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.main_container, new SupportFragment()).addToBackStack("tag");
                            ;
                            fragmentTransaction.commit();
                            drawer.closeDrawer(GravityCompat.START);
                        } else if (id == 7) {
                            Log.i("Logout", "Logout Activity");
                            if (Token != null) {
                                SharedPreferences login_token = getSharedPreferences("LoginToken",
                                        Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = login_token.edit();
                                editor.remove("Login_Token");
                                editor.commit();
//                                Intent login = new Intent(DistributorDashboard.this, Distribution_Login.class);
//                                startActivity(login);
//                                finish();
                                Intent intent = new Intent(DistributorDashboard.this, Register_Activity.class);
                                startActivity(intent);
                                finish();
                            }
                            drawer.closeDrawer(GravityCompat.START);
                        } else if (id == 8) {
                            Log.i("terms and conditions", "terms and conditions");
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.main_container, new TermsAndConditionsFragment());
                            fragmentTransaction.commit();

                            drawer.closeDrawer(GravityCompat.START);
                        }

                        return false;
                    }
                })
                .addOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                        navigationExpandableListView.setSelected(groupPosition, childPosition);

//
                        if (groupPosition == 2 && childPosition == 0) {
                            Log.i("Consolidate Payments", "Child");//DONE
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.main_container, new ConsolidatedPaymentsFragment()).addToBackStack("tag");
                            ;
                            fragmentTransaction.commit();
                            drawer.closeDrawer(GravityCompat.START);
                        } else if (groupPosition == 2 && childPosition == 1) {
                            Log.i("Make Payment", "Child");//DONE
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.main_container, new CreatePaymentRequestFragment()).addToBackStack(null);
                            fragmentTransaction.commit();
                            drawer.closeDrawer(GravityCompat.START);
                        } else if (groupPosition == 2 && childPosition == 2) {
                            Log.i("Payment Ledger", "Child"); //DONE
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.main_container, new Payments_Fragment()).addToBackStack("tag");
                            ;
                            fragmentTransaction.commit();
                            drawer.closeDrawer(GravityCompat.START);
                        } else if (groupPosition == 2 && childPosition == 3) {
                            Log.i("Proof of Payments", "Child"); //DONE
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.main_container, new ProofOfPaymentsDashboardFragment()).addToBackStack("tag");
                            ;
                            fragmentTransaction.commit();
                            drawer.closeDrawer(GravityCompat.START);
                        } else if (groupPosition == 1 && childPosition == 0) {
                            Log.i("Place order", "Child"); //DONE
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.main_container, new Order_PlaceOrder()).addToBackStack("tag");
                            ;
                            fragmentTransaction.commit();
                            drawer.closeDrawer(GravityCompat.START);
                        } else if (groupPosition == 4 && childPosition == 0) {
//                            Toast.makeText(DistributorDashboard.this, "retialer Managment", Toast.LENGTH_SHORT).show();
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.main_container, new RetailerFragment()).addToBackStack("tag");
                            ;
                            fragmentTransaction.commit();
                            drawer.closeDrawer(GravityCompat.START);
                        } else if (groupPosition == 4 && childPosition == 1) {
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.main_container, new RetailerOrderDashboard()).addToBackStack("tag");
                            ;
                            fragmentTransaction.commit();
                            drawer.closeDrawer(GravityCompat.START);
                        } else if (groupPosition == 4 && childPosition == 2) {
//                            Toast.makeText(DistributorDashboard.this, "Retailer Payment", Toast.LENGTH_SHORT).show();
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.main_container, new RetailerPaymentDashboard()).addToBackStack("tag");
                            ;
                            fragmentTransaction.commit();
                            drawer.closeDrawer(GravityCompat.START);
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }
                });
        navigationExpandableListView.expandGroup(1);

    }

    private void getNotificationCount() {
        if (!URL_Notification.contains("/" + ID))
            URL_Notification = URL_Notification + ID;
        Log.i("URL_NOTIFICATION", URL_Notification);

        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.GET, URL_Notification, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                try {

                    int count = Integer.parseInt(String.valueOf(result.get("count")));
                    Log.i("DistributorDashboard", String.valueOf(count));
                    if (count == 0) {
                        notification_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_no_notifications_black_24dp));
                    } else {
                        notification_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_notifications_black_24dp));
                    }
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
        sr.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 1000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        Volley.newRequestQueue(DistributorDashboard.this).add(sr);
    }


    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(2500);
                getNotificationCount();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            new MyAsyncTask().execute();
        }
    }

    @Override
    public void onBackPressed() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            super.onBackPressed();
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                finishAffinity();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 1500);
        } else {
//            super.onBackPressed();
            fm.popBackStack();
        }

    }

    private void printErrorMessage(VolleyError error) {
        if (error instanceof NetworkError) {
            Toast.makeText(DistributorDashboard.this, "Network Error !", Toast.LENGTH_LONG).show();
        } else if (error instanceof ServerError) {
            Toast.makeText(DistributorDashboard.this, "Server Error !", Toast.LENGTH_LONG).show();
        } else if (error instanceof AuthFailureError) {
            Toast.makeText(DistributorDashboard.this, "Auth Failure Error !", Toast.LENGTH_LONG).show();
        } else if (error instanceof ParseError) {
            Toast.makeText(DistributorDashboard.this, "Parse Error !", Toast.LENGTH_LONG).show();
        } else if (error instanceof NoConnectionError) {
            Toast.makeText(DistributorDashboard.this, "No Connection Error !", Toast.LENGTH_LONG).show();
        } else if (error instanceof TimeoutError) {
            Toast.makeText(DistributorDashboard.this, "Timeout Error !", Toast.LENGTH_LONG).show();
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
                Toast.makeText(DistributorDashboard.this, message, Toast.LENGTH_LONG).show();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}