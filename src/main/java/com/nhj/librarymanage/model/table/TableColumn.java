package com.nhj.librarymanage.model.table;

public record TableColumn(
        String label,
        boolean leading
) {
    public static TableColumn leading(String label) {
        return new TableColumn(label, true);
    }

    public static TableColumn column(String label) {
        return new TableColumn(label, false);
    }
}