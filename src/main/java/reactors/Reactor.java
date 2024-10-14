package reactors;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Reactor {
    private final String name;
    private final String country;
    private final ReactorType reactor_type;
    private final String owner;
    private final String operator;
    private final String status;
    private final Integer thermal_capacity;
    private final Map<Integer, Double> load_factors;
    private final Integer firstGridConnection;
    private final Integer suspended_date;
    private final Integer permanentShutdownDate;

    public Reactor(String name, String country, ReactorType reactor_type, String owner, String operator, String status, Integer thermal_capacity, Integer firstGridConnection, Integer suspended_date, Integer permanentShutdownDate) {
        this.name = name;
        this.country = country;
        this.reactor_type = reactor_type;
        this.owner = owner;
        this.operator = operator;
        this.status = status;
        this.thermal_capacity = thermal_capacity;
        this.load_factors = new HashMap<>();
        this.firstGridConnection = firstGridConnection;
        this.suspended_date = suspended_date;
        this.permanentShutdownDate = permanentShutdownDate;
    }

    public String getName() {
        return name;
    }
    public String getCountry() {
        return country;
    }
    public ReactorType getReactorType() {
        return reactor_type;
    }
    public String getOwner() {
        return owner;
    }
    public String getOperator() {
        return operator;
    }
    public String getStatus() {
        return status;
    }
    public Integer getThermalCapacity() {
        return thermal_capacity;
    }

    public void addLoadFactor(Integer year, Double load_factor) {
        if (Objects.equals(year, this.getFirstGridConnection())) {
            load_factor *= 3;
        }
        load_factors.put(year, load_factor);
    }

    public void fixLoadFactors() {
        for (Integer year : load_factors.keySet()) {
            if ((year >= this.getSuspendedDate() && this.getSuspendedDate() != 0) || (year >= this.getPermanentShutdownDate() && this.getPermanentShutdownDate() != 0)) {
                load_factors.put(year, 0.0);
            } else if (load_factors.get(year) == 0 && year > this.getFirstGridConnection()) {
                load_factors.put(year, 85.0);
            }
        }
    }

    public Map<Integer, Double> getLoadFactors() {
        return new HashMap<>(load_factors);
    }
    public Integer getFirstGridConnection() {
        return firstGridConnection;
    }
    public Integer getSuspendedDate() {
        return suspended_date;
    }
    public Integer getPermanentShutdownDate() {
        return permanentShutdownDate;
    }

    @Override
    public String toString() {
        return this.getName();
    }
    public String getFullDescription() {
        StringBuilder description = new StringBuilder();
        description.append("Страна: ").append(this.getCountry()).append("\n");
        if (this.getReactorType() != null) {
            description.append("Тип: ").append(this.getReactorType().toString()).append("\n");
        } else {
            description.append("Тип: Неизвестно\n");
        }
        description.append("Владелец: ").append(this.getOwner()).append("\n");
        description.append("Оператор: ").append(this.getOperator()).append("\n");
        description.append("Статус: ").append(this.getStatus()).append("\n");
        description.append("Тепловая мощность: ").append(this.getThermalCapacity()).append("\n");
        description.append("Первое подключение к сети: ").append(this.getFirstGridConnection()).append("\n");
        if (this.getSuspendedDate() != 0) {
            description.append("Дата приостановки: ").append(this.getSuspendedDate()).append("\n");
        }
        if (this.getPermanentShutdownDate() != 0) {
            description.append("Дата окончательного закрытия: ").append(this.getPermanentShutdownDate()).append("\n");
        }
        return description.toString();
    }
}