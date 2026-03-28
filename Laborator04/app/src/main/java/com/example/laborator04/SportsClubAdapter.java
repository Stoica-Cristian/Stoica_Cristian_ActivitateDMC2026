package com.example.laborator04;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SportsClubAdapter extends ArrayAdapter<SportsClub> {

    public SportsClubAdapter(@NonNull Context context, @NonNull List<SportsClub> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_sports_club, parent, false);
        }

        SportsClub club = getItem(position);

        if (club != null) {
            TextView tvName = convertView.findViewById(R.id.tvName);
            TextView tvType = convertView.findViewById(R.id.tvType);
            TextView tvRating = convertView.findViewById(R.id.tvRating);
            TextView tvDate = convertView.findViewById(R.id.tvDate);

            tvName.setText(club.getName());
            tvType.setText(club.getSportType().toString());
            tvRating.setText(String.format(Locale.getDefault(), "%.1f ★", club.getRating()));
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String dateStr = club.getEstablishmentDate() != null ? sdf.format(club.getEstablishmentDate()) : "N/A";
            tvDate.setText("Established: " + dateStr);
        }

        return convertView;
    }
}
