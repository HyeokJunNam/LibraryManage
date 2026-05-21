package com.nhj.librarymanage.model.table;

public record SearchField(
        String name,
        String label
) {
    public static SearchField field(String name, String label) {
        return new SearchField(name, label);
    }
}