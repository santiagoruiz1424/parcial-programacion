package com.library;
public abstract class Material {
    protected String id;
    protected String title;
    protected boolean available = true;
    public Material(String id, String title) { this.id = id; this.title = title; }
    public String getId() { return id; }
    public String getTitle() { return title; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public abstract String getType();
    @Override
    public String toString() { return String.format("%s - %s [%s]", id, title, available ? "Disponible" : "Prestado"); }
}