package com.example.skuska2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDate;
import java.util.Objects;

public class HelloController {
    @FXML private TableView<Asset> assets;
    @FXML private TableColumn<Asset, String> inventoryNumberCol;
    @FXML private TableColumn<Asset, String> statusCol;
    @FXML private TableColumn<Asset, String> typeCol;
    @FXML private TableColumn<Asset, Number> roomCol;
    @FXML private TableColumn<Asset, String> employeeCol;
    @FXML private TextField inputInventoryNumber, newType, newEmployee, inputNumber, inputArea;
    @FXML private ComboBox<String> inputStatus;
    @FXML private ComboBox<Type> inputType;
    @FXML private ComboBox<Room> inputRoom;
    @FXML private ComboBox<Employee> inputEmployee;
    @FXML private ComboBox<String> filterCombo;

    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, Number> numberCol;
    @FXML private TableColumn<Room, Number> areaCol;

    @FXML private TableView<Service> services;
    @FXML private TableColumn<Service, String> invNumberCol;
    @FXML private TableColumn<Service, LocalDate> serviceDateCol;
    @FXML private TableColumn<Service, String> serviceSuccessCol;

    private final String URL = "jdbc:mysql://localhost:3306/skuska2.2";
    private final String USERNAME = "root";
    private final String PASSWORD = "";

    private final ObservableList<Asset> dataAssets = FXCollections.observableArrayList();
    private final ObservableList<Type> dataTypes = FXCollections.observableArrayList();
    private final ObservableList<Room> dataRooms = FXCollections.observableArrayList();
    private final ObservableList<Employee> dataEmployees = FXCollections.observableArrayList();
    ObservableList<Service> dataServices = FXCollections.observableArrayList();
    private Asset selectedUpdateAsset;


    @FXML public void initialize() throws SQLException {
        inventoryNumberCol.setCellValueFactory(c -> c.getValue().inventoryNumberProperty());
        statusCol.setCellValueFactory(c -> c.getValue().statusProperty());
        typeCol.setCellValueFactory(c -> c.getValue().typeProperty());
        roomCol.setCellValueFactory(c -> c.getValue().roomProperty());
        employeeCol.setCellValueFactory(c -> c.getValue().employeeProperty());
        assets.setItems(dataAssets);

        numberCol.setCellValueFactory(c -> c.getValue().numberProperty());
        areaCol.setCellValueFactory(c -> c.getValue().areaProperty());
        roomsTable.setItems(dataRooms);

        invNumberCol.setCellValueFactory(c -> c.getValue().inventoryNumberProperty());
        serviceDateCol.setCellValueFactory(c -> c.getValue().serviceDateProperty());
        serviceSuccessCol.setCellValueFactory(c -> c.getValue().serviceSuccessProperty());
        services.setItems(dataServices);

        loadTypes();
        loadRooms();
        loadEmploes();
        loadServices();

        inputStatus.getItems().addAll(
                "IN_USE",
                "BROKEN",
                "LOST",
                "AVAILABLE"
        );

        filterCombo.getItems().addAll(
                "ALL",
                "IN_USE",
                "BROKEN",
                "LOST",
                "AVAILABLE"
        );
        filterCombo.setValue("ALL");

        loadAssets();
        inputType.setItems(dataTypes);
        inputRoom.setItems(dataRooms);
        inputEmployee.setItems(dataEmployees);
    }

    @FXML void onFilter() throws SQLException {
        loadAssets();
    }

    private void loadAssets() throws SQLException {
        dataAssets.clear();
        String status = filterCombo.getValue();
        if (status == null) {
            showAlert("Please select a valid filter");
            return;
        }

        if (filterCombo.getValue().equals("ALL")) {
                String sql = """
            
                        SELECT inventory_number, asset_status, t.name AS t_name, r.number AS r_num, e.full_name AS e_full\s
                    FROM assets a
                    JOIN employees e ON e.id = a.responsible_employee_id
                    JOIN rooms r ON r.number = a.room_number
                    JOIN asset_types t ON t.type_id = a.type_id
            """;

            try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Asset asset = new Asset(
                            rs.getString("inventory_number"),
                            rs.getString("asset_status"),
                            rs.getString("t_name"),
                            rs.getInt("r_num"),
                            rs.getString("e_full")
                    );

                    dataAssets.add(asset);
                }
            }
        }
        else  {
            String sql = """
                        SELECT inventory_number, asset_status, t.name AS t_name, r.number AS r_num, e.full_name AS e_full\s
                    FROM assets a
                    JOIN employees e ON e.id = a.responsible_employee_id
                    JOIN rooms r ON r.number = a.room_number
                    JOIN asset_types t ON t.type_id = a.type_id
                    WHERE a.asset_status = ?
            """;

            try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                    ps.setString(1, status);

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            Asset asset = new Asset(
                                    rs.getString("inventory_number"),
                                    rs.getString("asset_status"),
                                    rs.getString("t_name"),
                                    rs.getInt("r_num"),
                                    rs.getString("e_full")
                            );

                            dataAssets.add(asset);
                        }
                    }
            }
        }
    }

    private void loadTypes() throws SQLException {
        dataTypes.clear();

        String sql = """
        SELECT * FROM asset_types      
        """;

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Type type = new Type(
                        rs.getInt("type_id"),
                        rs.getString("name")
                );

                dataTypes.add(type);
            }
        }
    }

    private void loadRooms() throws SQLException {
        dataRooms.clear();

        String sql = """
        SELECT * FROM rooms      
        """;

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Room room = new Room(
                        rs.getInt("number"),
                        rs.getInt("area")
                );

                dataRooms.add(room);
            }
        }
    }

    private void loadEmploes() throws SQLException {
        dataEmployees.clear();

        String sql = """
        SELECT * FROM employees      
        """;

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Employee employee = new Employee(
                        rs.getInt("id"),
                        rs.getString("full_name")
                );

                dataEmployees.add(employee);
            }
        }
    }

    private void loadServices() throws SQLException {
        dataServices.clear();

        String sql = """
        SELECT service_id, inventory_number, service_date, service_success
        FROM services
        """;

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Service service = new Service(
                        rs.getInt("service_id"),
                        rs.getString("inventory_number"),
                        rs.getDate("service_date").toLocalDate(),
                        rs.getBoolean("service_success") ? "Success" : "Failure"
                );

                dataServices.add(service);
            }
        }
    }

    @FXML void addAsset () throws SQLException {
        String inventoryNumber = inputInventoryNumber.getText();

        if (inventoryNumber.isEmpty() || inventoryNumber.isBlank()) {
            showAlert("The inventory number field is empty.");
            return;
        }

        if (inputStatus.getValue() == null) {
            showAlert("The status combo is empty.");
            return;
        }

        if (inputType.getValue() == null) {
            showAlert("The type combo is empty.");
            return;
        }

        if (inputRoom.getValue() == null) {
            showAlert("The room combo is empty.");
            return;
        }

        if (inputEmployee.getValue() == null) {
            showAlert("The employee combo is empty.");
            return;
        }

        String status = inputStatus.getValue();
        int type = inputType.getValue().getId();
        int room = inputRoom.getValue().getNumber();
        int employee = inputEmployee.getValue().getId();

        try {
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            conn.setAutoCommit(false);

            String sql = """
                    INSERT INTO assets (
                      inventory_number,
                      asset_status,
                      type_id,
                      room_number,
                      responsible_employee_id)
                      VALUES (?, ?, ?, ?, ?)
                    """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, inventoryNumber);
                ps.setString(2, status);
                ps.setInt(3, type);
                ps.setInt(4, room);
                ps.setInt(5, employee);

                ps.executeUpdate();
                conn.commit();
                loadAssets();
                clearAssets();
            } catch (SQLException e) {
                conn.rollback();
                showAlert("This inventory number already exists.");
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            showAlert(e.getMessage());
        }
    }

    @FXML void deleteAsset () {
        Asset selected = assets.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Please select asset.");
            return;
        }

        String sql = """
            DELETE FROM assets WHERE inventory_number = ?;
        """;

        try {
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, selected.getInventoryNumber());
                ps.executeUpdate();
                conn.commit();
                loadAssets();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML void updateAsset () {
        selectedUpdateAsset = assets.getSelectionModel().getSelectedItem();
        if (selectedUpdateAsset == null) {
            showAlert("Please select asset.");
            return;
        }

        inputInventoryNumber.setText(selectedUpdateAsset.getInventoryNumber());
        inputStatus.setValue(selectedUpdateAsset.getStatus());
        for (Type type : dataTypes) {
            if (Objects.equals(type.getName(), selectedUpdateAsset.getType())){
                inputType.setValue(type);
            }
        }
        for (Room room : dataRooms) {
            if (room.getNumber() == selectedUpdateAsset.getRoom()){
                inputRoom.setValue(room);
            }
        }
        for (Employee employee : dataEmployees) {
            if (Objects.equals(employee.getName(), selectedUpdateAsset.getEmployee())){
                inputEmployee.setValue(employee);
            }
        }
    }

    @FXML void confirmAsset () {
        if (selectedUpdateAsset == null) {
            showAlert("Please select asset.");
            return;
        }

        String inventoryNumber = inputInventoryNumber.getText();

        if (inventoryNumber.isEmpty() || inventoryNumber.isBlank()) {
            showAlert("The inventory number field is empty.");
            return;
        }

        if (inputStatus.getValue() == null) {
            showAlert("The status combo is empty.");
            return;
        }

        if (inputType.getValue() == null) {
            showAlert("The type combo is empty.");
            return;
        }

        if (inputRoom.getValue() == null) {
            showAlert("The room combo is empty.");
            return;
        }

        if (inputEmployee.getValue() == null) {
            showAlert("The employee combo is empty.");
            return;
        }

        String status = inputStatus.getValue();
        int type = inputType.getValue().getId();
        int room = inputRoom.getValue().getNumber();
        int employee = inputEmployee.getValue().getId();

        try {
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            conn.setAutoCommit(false);

            String sql = """
                    UPDATE assets
                    SET inventory_number = ?,
                    	asset_status = ?,
                        type_id = ?,
                        room_number = ?,
                        responsible_employee_id = ?
                    WHERE inventory_number = ?
                    """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, inventoryNumber);
                ps.setString(2, status);
                ps.setInt(3, type);
                ps.setInt(4, room);
                ps.setInt(5, employee);
                ps.setString(6, selectedUpdateAsset.getInventoryNumber());

                ps.executeUpdate();
                conn.commit();
                loadAssets();
                clearAssets();
            } catch (SQLException e) {
                conn.rollback();
                showAlert("This inventory number already exists.");
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            showAlert("Error in confirming asset.");
            throw new RuntimeException(e);
        }
    }

    @FXML void addType () {
        String type = newType.getText();

        if (type.isEmpty() || type.isBlank()) {
            showAlert("The type field is empty.");
            return;
        }

        try {
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            conn.setAutoCommit(false);

            String sql = """
                    INSERT INTO asset_types (
                      name)
                      VALUES (?)
                    """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, type);

                ps.executeUpdate();
                conn.commit();
                loadTypes();
                newType.clear();
            } catch (SQLException e) {
                conn.rollback();
                showAlert("Error in adding type.");
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            showAlert(e.getMessage());
        }
    }

    @FXML void addEmployee () {
        String employee = newEmployee.getText();

        if (employee.isEmpty() || employee.isBlank()) {
            showAlert("The empoyee field is empty.");
            return;
        }

        try {
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            conn.setAutoCommit(false);

            String sql = """
                    INSERT INTO employees (
                      full_name)
                      VALUES (?)
                    """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, employee);

                ps.executeUpdate();
                conn.commit();
                loadEmploes();
                newEmployee.clear();
            } catch (SQLException e) {
                conn.rollback();
                showAlert("Error in adding employee.");
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            showAlert(e.getMessage());
        }
    }

    @FXML void addRoom () {
        if (inputNumber.getText().isEmpty() || inputNumber.getText().isBlank()) {
            showAlert("The number field is empty.");
            return;
        }

        if (inputArea.getText().isEmpty() || inputArea.getText().isBlank()) {
            showAlert("The area field is empty.");
            return;
        }

        int number = Integer.parseInt(inputNumber.getText());
        int area = Integer.parseInt(inputArea.getText());

        try {
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            conn.setAutoCommit(false);

            String sql = """
                    INSERT INTO rooms (
                      number,
                      area)
                      VALUES (?, ?)
                    """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, number);
                ps.setInt(2, area);

                ps.executeUpdate();
                conn.commit();
                loadRooms();
                inputNumber.clear();
                inputArea.clear();
            } catch (SQLException e) {
                conn.rollback();
                showAlert("This number already exists.");
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            showAlert(e.getMessage());
        }
    }

    @FXML void goService () {
        selectedUpdateAsset = assets.getSelectionModel().getSelectedItem();
        if (selectedUpdateAsset == null) {
            showAlert("Please select asset.");
            return;
        }

        if (!selectedUpdateAsset.getStatus().equals("BROKEN")) {
            showAlert("Asset is not broken.");
            return;
        }

        try {
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            conn.setAutoCommit(false);

            String sqlUpdate = """
                UPDATE assets
                SET asset_status = 'AVAILABLE'
                WHERE inventory_number = ?
                """;

            String sqlInsert = """
                INSERT INTO services (
                  inventory_number,
                  service_date,
                  service_success)
                  VALUES (?, ?, ?)
                """;

            try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
                 PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {

                psUpdate.setString(1, selectedUpdateAsset.getInventoryNumber());
                psUpdate.executeUpdate();

                psInsert.setString(1, selectedUpdateAsset.getInventoryNumber());
                psInsert.setDate(2, new java.sql.Date(System.currentTimeMillis()));
                psInsert.setBoolean(3, true);
                psInsert.executeUpdate();

                conn.commit();
                loadAssets();
                loadServices();
                showAlert("Asset repaired successfully.");
            } catch (SQLException e) {
                conn.rollback();
                showAlert("Error in repairing asset.");
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            showAlert(e.getMessage());
        }
    }

    @FXML void exportAssetsToCSV() {
        try (PrintWriter writer = new PrintWriter("assets.csv")) {
            writer.println("Inventory,Status,Type,Room,Employee");

            for (Asset a : dataAssets) {
                writer.println(
                        a.getInventoryNumber() + "," +
                                a.getStatus() + "," +
                                a.getType() + "," +
                                a.getRoom() + "," +
                                a.getEmployee()
                );
            }

            showAlert("Exported to assets.csv");
        } catch (Exception e) {
            showAlert("Error exporting CSV");
        }
    }

    private void clearAssets() {
        inputInventoryNumber.clear();
        inputStatus.setValue(null);
        inputType.setValue(null);
        inputRoom.setValue(null);
        inputEmployee.setValue(null);
    }

    public void showAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }
}
