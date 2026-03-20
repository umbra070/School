package org.skyschool.school.model;

import java.util.Objects;

public class Faculty {
    private long id;
    private String name, color;

    public Faculty(){

    }

    public Faculty(long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Faculty f = (Faculty) obj;
        return this.id == f.id && Objects.equals(this.name, f.name) && Objects.equals(this.color, f.color);
    }

    @Override
    public String toString() {
        return String.format("id:%d|name:%s|color:%s", id, name, color);
    }
}
