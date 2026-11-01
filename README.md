# Java Projects

Two Java applications I built during my Applied Informatics studies at Constantine the Philosopher University (UKF) in Nitra, Slovakia: an Android app and a desktop app. Their interfaces and data are partly in Slovak.

| Project | Type | Main technologies |
|---------|------|-------------------|
| [Meal and Ingredients App](skuska-android-studio) | Android | Java, Room, Retrofit |
| [Asset Management App](skuska-objektove-technologie) | Desktop | Java, JavaFX, JDBC, MySQL, Maven |

## Meal and Ingredients App

An Android app for managing meals and their ingredients, with a local database and data import from a REST API.

**Features**
- Download meals from a server and store them locally
- Add and edit meals with their ingredients, including price and weight
- Filter meals by type and sort them A-Z or Z-A
- Show the total price of each meal, calculated from its ingredients
- Browse meals by ingredient

**Technical details**
- Room entities and DAOs, with a foreign key linking ingredients to meals
- Retrofit for the HTTP request to the REST API
- Database work runs on a background thread through `ExecutorService`
- Minimum Android version: API 24

**Run it**
1. Open `skuska-android-studio` in Android Studio.
2. Let Gradle sync, then run the app on an emulator or device.

Server import uses a university API, so it may not be available outside the course. The rest of the app works with data stored locally.

## Asset Management App

A JavaFX desktop app for tracking company assets, with data stored in a MySQL database.

**Features**
- Add, edit and delete assets, with input validation and a check for duplicate inventory numbers
- Assign each asset a type, a room and a responsible employee
- Add new asset types, rooms and employees
- Filter assets and view their service records
- Export assets to a CSV file

**Technical details**
- JDBC with SQL queries, including joins across related tables
- JavaFX `TableView` with FXML and observable properties
- Object-oriented model classes: `Asset`, `Employee`, `Room`, `Service`, `Type`
- Built with Maven on JavaFX 21

**Run it**
1. Start MySQL, for example through XAMPP, on `localhost:3306`.
2. Import `schema.sql` from the project folder, which creates the `skuska2.2` database with sample data. The original schema was not saved, so this file is reconstructed from the application's SQL queries.
3. Check the database username and password in `HelloController.java`.
4. Run the app with `mvn javafx:run`.

## Author

**Nikita Vladymyrov**: [GitHub](https://github.com/NyxisDev) | [LinkedIn](https://www.linkedin.com/in/nikita-vladymyrov/)
