package com.example.cw2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cw2.R;
import com.example.cw2.Reservation;

import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    public interface ReservationClickListener {
        void onReservationClick(Reservation reservation);
        void onCancelClick(Reservation reservation);
        void onConfirmClick(Reservation reservation);
    }

    private List<Reservation> reservations;
    private boolean isStaffMode;
    private final ReservationClickListener clickListener;

    public ReservationAdapter(List<Reservation> reservations, boolean isStaffMode,
                              ReservationClickListener clickListener) {
        this.reservations = reservations;
        this.isStaffMode = isStaffMode;
        this.clickListener = clickListener;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
        notifyDataSetChanged();
    }

    public void setStaffMode(boolean isStaffMode) {
        this.isStaffMode = isStaffMode;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_reservation, parent, false);
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

        private TextView tvCustomerName, tvDateTime, tvPartySize, tvStatus;
        private Button btnCancel, btnConfirm, btnDetails;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvDateTime = itemView.findViewById(R.id.tv_date_time);
            tvPartySize = itemView.findViewById(R.id.tv_party_size);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
            btnConfirm = itemView.findViewById(R.id.btn_confirm);
            btnDetails = itemView.findViewById(R.id.btn_details);
        }

        public void bind(Reservation reservation, boolean isStaffMode,
                         ReservationClickListener clickListener) {

            // Set reservation details
            tvCustomerName.setText(reservation.getCustomerName());
            tvDateTime.setText(reservation.getFormattedDate() + " • " + reservation.getFormattedTime());
            tvPartySize.setText(reservation.getNumberOfPeople() + " people");
            tvStatus.setText(reservation.getStatus().toUpperCase());

            // Show/hide buttons based on user role and status
            if (isStaffMode) {
                // Staff can confirm pending reservations and cancel any
                if ("pending".equalsIgnoreCase(reservation.getStatus())) {
                    btnConfirm.setVisibility(View.VISIBLE);
                    btnConfirm.setOnClickListener(v -> {
                        if (clickListener != null) {
                            clickListener.onConfirmClick(reservation);
                        }
                    });
                } else {
                    btnConfirm.setVisibility(View.GONE);
                }

                btnCancel.setVisibility(View.VISIBLE);
                btnCancel.setOnClickListener(v -> {
                    if (clickListener != null) {
                        clickListener.onCancelClick(reservation);
                    }
                });

            } else {
                // Guests can only cancel their own pending/confirmed reservations
                if ("cancelled".equalsIgnoreCase(reservation.getStatus())) {
                    btnCancel.setVisibility(View.GONE);
                } else {
                    btnCancel.setVisibility(View.VISIBLE);
                    btnCancel.setOnClickListener(v -> {
                        if (clickListener != null) {
                            clickListener.onCancelClick(reservation);
                        }
                    });
                }
                btnConfirm.setVisibility(View.GONE);
            }

            // Details button
            btnDetails.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onReservationClick(reservation);
                }
            });

            // Whole item click
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onReservationClick(reservation);
                }
            });
        }
    }
}
