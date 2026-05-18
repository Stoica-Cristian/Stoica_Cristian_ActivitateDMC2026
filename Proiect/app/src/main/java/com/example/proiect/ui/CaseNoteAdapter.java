package com.example.proiect.ui;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.proiect.R;
import com.example.proiect.data.CaseNote;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class CaseNoteAdapter extends BaseAdapter {
    private final Context context;
    private final List<CaseNote> cases = new ArrayList<>();

    public CaseNoteAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<CaseNote> newCases) {
        cases.clear();
        if (newCases != null) {
            cases.addAll(newCases);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return cases.size();
    }

    @Override
    public CaseNote getItem(int position) {
        return cases.get(position);
    }

    @Override
    public long getItemId(int position) {
        return cases.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_case_note, parent, false);
            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.tvCaseTitle);
            holder.status = convertView.findViewById(R.id.tvCaseStatus);
            holder.meta = convertView.findViewById(R.id.tvCaseMeta);
            holder.notes = convertView.findViewById(R.id.tvCaseNotes);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CaseNote note = getItem(position);
        holder.title.setText(note.getTitle());
        holder.status.setText(note.getStatus());
        holder.status.setBackground(createStatusBackground(note.getStatus()));

        String followUp = note.isNotify() ? " | urmarire" : "";
        String closedText = isClosed(note) && note.getClosedAt() != null && !note.getClosedAt().trim().isEmpty()
                ? "Inchis " + note.getClosedAt()
                : "";
        String metaPrefix = closedText.isEmpty() ? "" : closedText + " | ";
        holder.meta.setText(metaPrefix
                + "Termen " + UiFormat.formatDate(note.getDueDate())
                + " | Scor " + note.getRating() + "/5"
                + followUp);
        if (isClosed(note)
                && note.getResolution() != null
                && !note.getResolution().trim().isEmpty()) {
            holder.notes.setText("Concluzie: " + note.getResolution());
        } else {
            holder.notes.setText(note.getNotes());
        }
        return convertView;
    }

    private boolean isClosed(CaseNote note) {
        return "Inchis".equalsIgnoreCase(note.getStatus());
    }

    private GradientDrawable createStatusBackground(String status) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(statusColor(status));
        drawable.setCornerRadius(dp(8));
        return drawable;
    }

    private int statusColor(String status) {
        if ("Inchis".equalsIgnoreCase(status)) {
            return context.getColor(R.color.risk_low);
        }
        if ("Necesita interventie".equalsIgnoreCase(status)) {
            return context.getColor(R.color.risk_high);
        }
        if ("In analiza".equalsIgnoreCase(status)) {
            return context.getColor(R.color.accent_blue);
        }
        return context.getColor(R.color.accent_primary);
    }

    private float dp(int value) {
        return value * context.getResources().getDisplayMetrics().density;
    }

    private static class ViewHolder {
        TextView title;
        TextView status;
        TextView meta;
        TextView notes;
    }
}
