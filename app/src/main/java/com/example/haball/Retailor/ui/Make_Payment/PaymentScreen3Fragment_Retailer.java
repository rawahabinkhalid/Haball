package com.example.haball.Retailor.ui.Make_Payment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.haball.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class PaymentScreen3Fragment_Retailer extends Fragment {

    private TextView tv_banking_channel, payment_id ,btn_newpayment;
    private String PrePaidNumber = "", PrePaidId = "", CompanyName = "", Amount = "", CompanyId = "";
    private Button btn_voucher,  btn_update;
    private Spinner spinner_companyName;
    private EditText txt_amount;
    private ArrayAdapter<String> arrayAdapterPayments;
    private List<String> CompanyNames = new ArrayList<>();
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (checkAndRequestPermissions()) {

        }

        View root = inflater.inflate(R.layout.activity_payment__screen3, container, false);

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("PrePaidNumber",
                Context.MODE_PRIVATE);
        PrePaidNumber = sharedPreferences.getString("PrePaidNumber", "");
        PrePaidId = sharedPreferences.getString("PrePaidId", "");
        CompanyName = sharedPreferences.getString("CompanyName", "");
        CompanyId = sharedPreferences.getString("CompanyId", "");
        Amount = sharedPreferences.getString("Amount", "");

        payment_id = root.findViewById(R.id.payment_id);
        spinner_companyName = root.findViewById(R.id.spinner_companyName);
        txt_amount = root.findViewById(R.id.txt_amount);
        btn_newpayment = root.findViewById(R.id.btn_addpayment);
        btn_update = root.findViewById(R.id.btn_update);
        btn_voucher = root.findViewById(R.id.btn_voucher);

        payment_id.setText(PrePaidNumber);
     //   spinner_companyName.setText(CompanyName);
        CompanyNames.add(CompanyName);
        arrayAdapterPayments = new ArrayAdapter<>(root.getContext(),
                android.R.layout.simple_spinner_dropdown_item, CompanyNames);
        spinner_companyName.setAdapter(arrayAdapterPayments);
        spinner_companyName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.textcolor));
                ((TextView) adapterView.getChildAt(0)).setTextSize((float) 13.6);
                ((TextView) adapterView.getChildAt(0)).setPadding(50,0 ,50 ,0);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        txt_amount.setText("PKR " + Amount);
        txt_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { ;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                txt_amount.setBoxStrokeColor(getResources().getColor(R.color.box_stroke));
//                //   layout_password.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.green_color)));
//                txt_amount.setTextColor(getResources().getColor(R.color.textcolor));
//                txt_amount.setPasswordVisibilityToggleTintList(ColorStateList.valueOf(getResources().getColor(R.color.textcolorhint)));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });




        btn_newpayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container_ret, new CreatePaymentRequestFragment());
                fragmentTransaction.commit();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.main_container_ret, new EditPaymentRequestFragment());
//                fragmentTransaction.commit();

            }
        });

        btn_voucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAndRequestPermissions()) {
                    try {
                        viewPDF(getContext(), PrePaidId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        tv_banking_channel = root.findViewById(R.id.tv_banking_channel);
        tv_banking_channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog alertDialog2 = new AlertDialog.Builder(getContext()).create();
                LayoutInflater inflater2 = LayoutInflater.from(getContext());
                View view_popup2 = inflater2.inflate(R.layout.payment_request_details, null);
                alertDialog2.setView(view_popup2);
                alertDialog2.show();
                ImageButton img_close = view_popup2.findViewById(R.id.image_button_close);
                TextView payment_information_txt3 = view_popup2.findViewById(R.id.payment_information_txt3);
                payment_information_txt3.setText(PrePaidNumber);

                img_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog2.dismiss();
                    }
                });
            }
        });

        return root;
    }

    private void viewPDF(Context context, String ID) throws JSONException {
        ViewVoucherRequest viewPDFRequest = new ViewVoucherRequest();
        viewPDFRequest.viewPDF(context, ID);
    }

    private boolean checkAndRequestPermissions() {
        int permissionRead = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWrite = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionRead != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

}

