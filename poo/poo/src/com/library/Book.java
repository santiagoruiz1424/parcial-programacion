package com.library;
public class Book extends Material {
    private String author; private int year;
    public Book(String id, String title, String author, int year) {
        super(id, title); this.author = author; this.year = year;
    }
    @Override public String getType() { return "Libro"; }
    @Override public String toString() {
        return String.format("%s | %s - %s (%d) [%s]", id, getType(), title, year, isAvailable() ? "Disponible" : "Prestado");
    }
}