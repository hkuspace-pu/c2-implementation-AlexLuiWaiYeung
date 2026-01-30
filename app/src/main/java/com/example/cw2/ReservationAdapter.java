package com.example.cw2;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    public interface ReservationClickListener {
        void onReservationClick(Reservation reservation);
        void onCancelClick(Reservation reservation);
        void onConfirmClick(Reservation reservation);
    }

    private List<Reservation> reservations;
    private final boolean isStaffMode;
    private final ReservationClickListener clickListener;
    private final Context context;

    public ReservationAdapter(List<Reservation> reservations, boolean isStaffMode,
                              ReservationClickListener clickListener) {
        this.reservations = reservations;
        this.isStaffMode = isStaffMode;
        this.clickListener = clickListener;
        this.context = null;
    }

    public ReservationAdapter(List<Reservation> reservations, boolean isStaffMode,
                              ReservationClickListener clickListener, Context context) {
        this.reservations = reservations;
        this.isStaffMode = isStaffMode;
        this.clickListener = clickListener;
        this.context = context;
    }

    public void updateReservations(List<Reservation> newReservations) {
        this.reservations = newReservations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);
        holder.bind(reservation, isStaffMode, clickListener);
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    static class ReservationViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvCustomerName, tvDateTime, tvPartySize, tvStatus;
        private final Button btnDetails, btnCancel, btnConfirm;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvDateTime = itemView.findViewById(R.id.tv_date_time);
            tvPartySize = itemView.findViewById(R.id.tv_party_size);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnDetails = itemView.findViewById(R.id.btn_details);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
            btnConfirm = itemView.findViewById(R.id.btn_confirm);
        }

        public void bind(Reservation reservation, boolean isStaffMode,
                         ReservationClickListener clickListener) {

            // Set reservation details
            tvCustomerName.setText(reservation.getCustomerName());
            tvDateTime.setText(reservation.getFormattedDate() + " • " + reservation.getFormattedTime());
            tvPartySize.setText(reservation.getNumberOfPeople() + " people");
            tvStatus.setText(reservation.getStatus().toUpperCase());

            // Set status color
            String status = reservation.getStatus().toLowerCase();
            switch (status) {
                case "confirmed":
                    tvStatus.setBackgroundColor(Color.parseColor("#4CAF50")); // Green
                    break;
                case "pending":
                    tvStatus.setBackgroundColor(Color.parseColor("#FF9800")); // Orange
                    break;
                case "cancelled":
                    tvStatus.setBackgroundColor(Color.parseColor("#F44336")); // Red
                    break;
                default:
                    tvStatus.setBackgroundColor(Color.parseColor("#9E9E9E")); // Gray
            }

            // Button visibility based on role and status
            if (isStaffMode) {
                // Staff: Can confirm pending, cancel any, view details
                if ("pending".equalsIgnoreCase(reservation.getStatus())) {
                    btnConfirm.setVisibility(View.VISIBLE);
                    btnConfirm.setOnClickListener(v -> clickListener.onConfirmClick(reservation));
                } else {
                    btnConfirm.setVisibility(View.GONE);
                }

                btnCancel.setVisibility(View.VISIBLE);
                btnCancel.setOnClickListener(v -> clickListener.onCancelClick(reservation));

            } else {
                // Guest: Can only cancel their own pending/confirmed
                if ("cancelled".equalsIgnoreCase(reservation.getStatus())) {
                    btnCancel.setVisibility(View.GONE);
                } else {
                    btnCancel.setVisibility(View.VISIBLE);
                    btnCancel.setOnClickListener(v -> clickListener.onCancelClick(reservation));
                }
                btnConfirm.setVisibility(View.GONE);
            }

            // Details button for both
            btnDetails.setOnClickListener(v -> clickListener.onReservationClick(reservation));

            // Whole item click
            itemView.setOnClickListener(v -> clickListener.onReservationClick(reservation));
        }
    }
}