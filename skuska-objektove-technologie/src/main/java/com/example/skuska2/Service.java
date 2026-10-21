package com.example.skuska2;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

import java.time.LocalDate;

public class Service {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty inventoryNumber = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final StringProperty serviceSuccess = new SimpleStringProperty();

    public Service(int id, String inventoryNumber, LocalDate date, String serviceSuccess) {
        this.id.set(id);
        this.inventoryNumber.set(inventoryNumber);
        this.date.set(date);
        this.serviceSuccess.set(serviceSuccess);
    }

    public void  setId(int id) {
        this.id.set(id);
    }

    public void  setInventoryNumber(String inventoryNumber) {
        this.inventoryNumber.set(inventoryNumber);
    }

    public void  setDate(LocalDate date) {
        this.date.set(date);
    }

    public void  setServiceSuccess(String serviceSuccess) {
        this.serviceSuccess.set(serviceSuccess);
    }

    public String getServiceSuccess() {
        return serviceSuccess.get();
    }

    public StringProperty serviceSuccessProperty() {
        return serviceSuccess;
    }

    public LocalDate getDate() {
        return date.get();
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public String getInventoryNumber() {
        return inventoryNumber.get();
    }

    public StringProperty inventoryNumberProperty() {
        return inventoryNumber;
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public ObservableValue<LocalDate> serviceDateProperty() {
        return date;
    }
}
