package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {

    public static final File DATABASE_FILE = new File(System.getProperty("user.home") + "/AppData/Local/Swooosh/Evolution/database.db");
    private Connection con;

    public DatabaseHandler() {
        connectToDB();
    }

    private void connectToDB() {
        try {
            Boolean createDatabase = false;
            if (!DATABASE_FILE.exists()) {
                createDatabase = true;
            }
            con = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE.getAbsolutePath());
            if (createDatabase) {
                Statement stmt = con.createStatement();
                stmt.execute("CREATE TABLE OrganismTemp (" +
                        "ID INTEGER PRIMARY KEY, " +
                        "FoodCount INTEGER, " +
                        "Distance NUMERIC, " +
                        "Generation INTEGER, " +
                        "Time NUMERIC, " +
                        "Organism BLOB)");
                stmt.execute("CREATE TABLE Organism (" +
                        "ID INTEGER PRIMARY KEY, " +
                        "FoodCount INTEGER, " +
                        "Distance NUMERIC, " +
                        "Generation INTEGER, " +
                        "Time NUMERIC, " +
                        "Organism BLOB)");
                stmt.execute("CREATE TABLE OrganismStats (" +
                        "ID INTEGER PRIMARY KEY, " +
                        "Generation INTEGER, " +
                        "FoodCount INTEGER, " +
                        "Time NUMERIC, " +
                        "Distance NUMERIC)");
            }
            System.out.println("Server> Connected to database");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    public void purge() {
        try {
            int keepNum = (int) (Constants.SAMPLE_SIZE * Constants.TOP_PERC);
            PreparedStatement preparedStatement = con.prepareStatement("SELECT ID FROM OrganismTemp ORDER BY FoodCount DESC, Time ASC, Distance ASC");
            ResultSet resultSet = preparedStatement.executeQuery();
            for (int i = 0; i < keepNum; i++) {
                if (resultSet.next()) {
                    saveToDatabase(resultSet.getInt("ID"));
                }
            }
            preparedStatement.close();
            preparedStatement = con.prepareStatement("DELETE FROM OrganismTemp");
            preparedStatement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveToDatabase(Organism organism, int foodCount, double distance, double time) {
        try {
            saveToDatabase(organism.getID(), organism.getGeneration(), foodCount, distance, time);
            Boolean save = true;
            if (getNumOrganism(organism.getGeneration()) >= Constants.SAMPLE_SIZE * Constants.TOP_PERC) {
                PreparedStatement preparedStatement = con.prepareStatement("SELECT ID, FoodCount, Time, Distance FROM Organism ORDER BY FoodCount ASC, Time DESC, Distance DESC"); //Last one
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    if (foodCount < resultSet.getInt("FoodCount")) {
                        save = false;
                    } else if (foodCount == resultSet.getInt("FoodCount")) {
                        if (distance > resultSet.getDouble("Time")) {
                            save = false;
                        } else if (distance == resultSet.getDouble("Time")) {
                            if (distance < resultSet.getDouble("Distance")) {
                                save = false;
                            }
                        }
                    }
                    if (save) {
                        deleteOrganism(resultSet.getInt("ID"));
                    }
                }
            }
            if (save) {
                PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO OrganismTemp(ID, FoodCount, Distance, Generation, Organism, Time) VALUES (?, ?, ?, ?, ?, ?);");
                preparedStatement.setInt(1, organism.getID());
                preparedStatement.setInt(2, foodCount);
                preparedStatement.setDouble(3, distance);
                preparedStatement.setInt(4, organism.getGeneration());
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(organism);
                oos.flush();
                oos.close();
                preparedStatement.setBytes(5, bos.toByteArray());
                bos.close();
                preparedStatement.setDouble(6, time);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveToDatabase(int id, int generation, int foodCount, double distance, double time) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO OrganismStats(ID, Generation, FoodCount, Distance, Time) VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, generation);
            preparedStatement.setInt(3, foodCount);
            preparedStatement.setDouble(4, distance);
            preparedStatement.setDouble(5, time);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteOrganism(int id) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("DELETE FROM OrganismTemp WHERE ID = ?");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getNumOrganism(int generation) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT Count(ID) AS 'NumOrganisms' FROM OrganismTemp WHERE Generation = ?");
            preparedStatement.setInt(1, generation);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("NumOrganisms");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public ObservableList<XYChart.Series> getGenerationData(int generation) {
        XYChart.Series foodSeries = new XYChart.Series();
        foodSeries.setName("Food Count");
        XYChart.Series timeSeries = new XYChart.Series();
        timeSeries.setName("Time");
        XYChart.Series distanceSeries = new XYChart.Series();
        distanceSeries.setName("Distance");
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT ID AS 'x', FoodCount, Time, Distance FROM OrganismStats WHERE Generation = ? ORDER BY FoodCount DESC, TIME ASC ,Distance ASC");
            preparedStatement.setInt(1, generation);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                foodSeries.getData().add(new XYChart.Data<>(resultSet.getInt("x"), resultSet.getInt("FoodCount")));
                timeSeries.getData().add(new XYChart.Data<>(resultSet.getInt("x"), resultSet.getInt("Time") / Math.max(1, resultSet.getInt("FoodCount"))));
                distanceSeries.getData().add(new XYChart.Data<>(resultSet.getInt("x"), resultSet.getDouble("Distance") / 500));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return FXCollections.observableArrayList(foodSeries, timeSeries, distanceSeries);
    }

    public void saveToDatabase(int id) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO Organism(ID, FoodCount, Distance, Generation, Organism, Time) SELECT ID, FoodCount, Distance, Generation, Organism, Time FROM OrganismTemp WHERE ID = ?");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getNumGenerations() {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT Generation FROM Organism ORDER BY Generation DESC");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("Generation");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public List<OrganismResult> getOrganisms(int generation) {
        List<OrganismResult> out = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT ID, FoodCount, Distance, Time FROM Organism WHERE Generation = ?");
            preparedStatement.setInt(1, generation);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                out.add(new OrganismResult(resultSet.getInt("ID"), resultSet.getInt("FoodCount"), resultSet.getDouble("Distance"), resultSet.getDouble("Time")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return out;
    }

    public List<Integer> getOrganismIDs(int generation) {
        List<Integer> ids = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT ID FROM Organism WHERE Generation = ?");
            preparedStatement.setInt(1, generation);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ids.add(resultSet.getInt("ID"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ids;
    }

    public Organism getOrganism(int id) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT Organism FROM Organism WHERE ID = ?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(resultSet.getBytes("Organism")));
                return (Organism) ois.readObject();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
