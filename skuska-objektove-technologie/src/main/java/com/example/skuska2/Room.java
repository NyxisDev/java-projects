package com.example.skuska2;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Room {
    private final IntegerProperty number = new SimpleIntegerProperty();
    private final IntegerProperty area = new SimpleIntegerProperty();

    public Room(int number, int area) {
        this.number.set(number);
        this.area.set(area);
    }

    public void setNumber(int number) {
        this.number.set(number);
    }

    public void setArea(int area) {
        this.area.set(area);
    }

    public int getArea() {
        return area.get();
    }

    public IntegerProperty areaProperty() {
        return area;
    }

    public int getNumber() {
        return number.get();
    }

    public IntegerProperty numberProperty() {
        return number;
    }

    @Override
    public String toString() {
        return Integer.toString(number.get());
    };
}
