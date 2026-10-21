package com.example.skuska2;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Asset {
    private final StringProperty inventoryNumber =  new SimpleStringProperty();
    private final StringProperty status =  new SimpleStringProperty();
    private final StringProperty type =  new SimpleStringProperty();
    private final IntegerProperty room =  new SimpleIntegerProperty();
    private final StringProperty employee =  new SimpleStringProperty();

    public Asset(String inventoryNumber, String status, String type, int room, String employee) {
        this.inventoryNumber.set(inventoryNumber);
        this.status.set(status);
        this.type.set(type);
        this.room.set(room);
        this.employee.set(employee);
    }

    public String getEmployee() {
        return employee.get();
    }

    public StringProperty employeeProperty() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee.set(employee);
    }

    public int getRoom() {
        return room.get();
    }

    public IntegerProperty roomProperty() {
        return room;
    }

    public void setRoom(int room) {
        this.room.set(room);
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getInventoryNumber() {
        return inventoryNumber.get();
    }

    public StringProperty inventoryNumberProperty() {
        return inventoryNumber;
    }

    public void setInventoryNumber(String inventoryNumber) {
        this.inventoryNumber.set(inventoryNumber);
    }
}
