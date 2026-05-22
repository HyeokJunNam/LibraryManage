package com.nhj.librarymanage.model.table;

public record SearchField(
        String name,
        String label
) {
    public static SearchField of(String name, String label) {
        return new SearchField(name, label);
    }
}