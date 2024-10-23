package readers;

import reactors.Reactor;
import reactors.ReactorType;
import reactors.ReactorManager;
import regions.Regions;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBReader {
    public static Map<String, List<Reactor>> importReactors(File file) throws SQLException {
        String DB_URL = "jdbc:sqlite:" + file.getAbsolutePath();
        ReactorManager typesOwner = new ReactorManager();
        Map<String, ReactorType> types = typesOwner.getReactorMap();
        Map<String, List<Reactor>> reactorsByOperator = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                String operatorQuery = "SELECT DISTINCT operator FROM reactors";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(operatorQuery)) {

                    while (rs.next()) {
                        String operator = rs.getString("operator");
                        List<Reactor> operatorReactors = new ArrayList<>();
                        String reactorQuery = "SELECT * FROM reactors WHERE operator = ?";
                        try (PreparedStatement reactorStmt = conn.prepareStatement(reactorQuery)) {
                            reactorStmt.setString(1, operator);
                            try (ResultSet reactorRs = reactorStmt.executeQuery()) {
                                while (reactorRs.next()) {
                                    String name = reactorRs.getString("name");
                                    String country = reactorRs.getString("country");
                                    ReactorType reactorType = types.get(reactorRs.getString("type"));
                                    String owner = reactorRs.getString("owner");
                                    String status = reactorRs.getString("status");
                                    Integer thermalCapacity = reactorRs.getInt("thermalCapacity");
                                    Integer firstGridConnection = reactorRs.getInt("firstGridConnection");
                                    Integer suspendedDate = reactorRs.getInt("suspendedDate");
                                    Integer permanentShutdownDate = reactorRs.getInt("permanentShutdownDate");
                                    Reactor reactor = new Reactor(name, country, reactorType, owner, operator, status,
                                            thermalCapacity, firstGridConnection, suspendedDate, permanentShutdownDate);
                                    operatorReactors.add(reactor);
                                }
                            }
                        }
                        reactorsByOperator.put(operator, operatorReactors);
                    }
                }
                String loadFactorQuery = "SELECT * FROM load_factors";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(loadFactorQuery)) {
                    while (rs.next()) {
                        String name = rs.getString("name");
                        Integer year = rs.getInt("year");
                        Double loadFactor = rs.getDouble("loadFactor");

                        reactorsByOperator.values().stream()
                                .flatMap(List::stream)
                                .filter(reactor -> reactor.getName().equals(name))
                                .findFirst()
                                .ifPresent(reactor -> reactor.addLoadFactor(year, loadFactor));
                    }
                }
            }
            reactorsByOperator.values().stream()
                    .flatMap(List::stream)
                    .forEach(Reactor::fixLoadFactors);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException(e);
        }
        return reactorsByOperator;
    }

    public static Regions importRegions(File file) throws SQLException {
        Regions regions = new Regions();
        String DB_URL = "jdbc:sqlite:" + file.getAbsolutePath();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                String regionsQuery = "SELECT * FROM countries";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(regionsQuery)) {
                    while (rs.next()) {
                        String country = rs.getString("country");
                        String region = rs.getString("region");
                        regions.addCountry(region, country);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException(e);
        }
        return regions;
    }
}