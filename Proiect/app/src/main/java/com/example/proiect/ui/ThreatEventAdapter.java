package com.example.proiect.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.proiect.R;
import com.example.proiect.data.SavedEventState;
import com.example.proiect.data.ThreatEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreatEventAdapter extends BaseAdapter {
    private static final int BACKGROUND_EVEN = Color.WHITE;
    private static final int BACKGROUND_ODD = Color.rgb(228, 241, 244);

    private final Context context;
    private final List<ThreatEvent> events = new ArrayList<>();
    private Map<String, SavedEventState> savedStates = new HashMap<>();
    private Map<String, Integer> caseCounts = new HashMap<>();

    public ThreatEventAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<ThreatEvent> newEvents, Map<String, SavedEventState> newStates, Map<String, Integer> newCaseCounts) {
        events.clear();
        if (newEvents != null) {
            events.addAll(newEvents);
        }
        savedStates = newStates == null ? new HashMap<>() : newStates;
        caseCounts = newCaseCounts == null ? new HashMap<>() : newCaseCounts;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public ThreatEvent getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_threat_event, parent, false);
            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.tvEventTitle);
            holder.meta = convertView.findViewById(R.id.tvEventMeta);
            holder.severity = convertView.findViewById(R.id.tvEventSeverity);
            holder.score = convertView.findViewById(R.id.tvEventScore);
            holder.saved = convertView.findViewById(R.id.tvEventSaved);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ThreatEvent event = getItem(position);
        convertView.setBackgroundColor(position % 2 == 0 ? BACKGROUND_EVEN : BACKGROUND_ODD);

        SavedEventState state = savedStates.get(event.getId());
        int caseCount = caseCounts.containsKey(event.getId()) ? caseCounts.get(event.getId()) : 0;
        String caseText = caseCount == 0 ? "Fara caz" : caseCount == 1 ? "1 caz" : caseCount + " cazuri";

        holder.title.setText(event.getTitle());
        holder.meta.setText(event.getLoaderFamily() + " | " + event.getLocationLabel() + " | " + event.getLastSeen());
        holder.severity.setText(event.getSeverity());
        holder.severity.setTextColor(UiFormat.severityColor(event.getSeverity()));
        holder.score.setText("Risc " + event.getRiskScore());
        if (state != null && state.isWatched()) {
            holder.saved.setText("Urmarit | Relevanta " + state.getRating() + "/5 | " + caseText);
        } else if (state != null) {
            holder.saved.setText("Salvat | Relevanta " + state.getRating() + "/5 | " + caseText);
        } else if (caseCount > 0) {
            holder.saved.setText("In investigatie | " + caseText);
        } else {
            holder.saved.setText("Incident nou | Fara caz");
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView title;
        TextView meta;
        TextView severity;
        TextView score;
        TextView saved;
    }
}
