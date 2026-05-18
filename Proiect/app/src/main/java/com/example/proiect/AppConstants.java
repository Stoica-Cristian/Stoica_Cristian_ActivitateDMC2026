package com.example.proiect;

@SuppressWarnings("SpellCheckingInspection")
public final class AppConstants {
    public static final String EXTRA_EVENT_ID = "com.example.proiect.EXTRA_EVENT_ID";
    public static final String EXTRA_CASE_ID = "com.example.proiect.EXTRA_CASE_ID";

    public static final String[] SEVERITY_FILTERS = {"Toate", "Ridicat", "Mediu", "Scazut"};
    public static final String[] RISK_SORT_OPTIONS = {"Ordine initiala", "Risc descrescator", "Risc crescator"};
    public static final String[] CASE_STATUSES = {"Nou", "In analiza", "Necesita interventie", "Inchis"};

    private AppConstants() {
    }
}
