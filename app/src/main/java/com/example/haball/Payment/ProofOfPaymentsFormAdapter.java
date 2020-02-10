package com.example.haball.Payment;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haball.R;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

public class ProofOfPaymentsFormAdapter extends RecyclerView.Adapter<ProofOfPaymentsFormAdapter.ViewHolder> {
    private Context mContext;
    private String deposit,slip;

    public ProofOfPaymentsFormAdapter(Proof_Of_Payment_Form proof_of_payment_form, String deposit, String slip) {
        this.mContext = proof_of_payment_form;
        this.deposit = deposit;
        this.slip = slip;
    }

    @NonNull
    @Override
    public ProofOfPaymentsFormAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view_inflate = LayoutInflater.from(mContext).inflate(R.layout.proof_of_payment_form_recycler,parent,false);
        return new ProofOfPaymentsFormAdapter.ViewHolder(view_inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ProofOfPaymentsFormAdapter.ViewHolder holder, int position) {
        holder.txt_doc1.setText(deposit);
        holder.txt_slip.setText(slip);
    }


    @Override
    public int getItemCount() {
        return 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_doc1,txt_slip;
        public ImageButton menu_btn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_doc1 = itemView.findViewById(R.id.txt_doc1);
            txt_slip = itemView.findViewById(R.id.txt_slip);
            menu_btn = itemView.findViewById(R.id.menu_btn);
        }
    }
}
