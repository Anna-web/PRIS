package reactors;

public class ReactorType {
    private final String reactorClass;
    private final Double burn_up;
    private final Double electricalCapacity;
    private final Double enrichment;
    private final Double firstLoad;
    private final Double efficiency_factor;
    private final Integer lifeTime;
    private final Double heatCapacity;
    private final String source;

    public ReactorType(
            String type, String reactorClass, Double burn_up,
            Double efficiency_factor, Double enrichment, Double heatCapacity,
            Double electricalCapacity, Integer lifeTime, Double firstLoad,
            String source
    ) {
        this.reactorClass = reactorClass;
        this.burn_up = burn_up;
        this.electricalCapacity = electricalCapacity;
        this.enrichment = enrichment;
        this.firstLoad = firstLoad;
        this.efficiency_factor = efficiency_factor;
        this.lifeTime = lifeTime;
        this.heatCapacity = heatCapacity;
        this.source = source;
    }

    @Override
    public String toString() {
        return reactorClass;
    }
    public Double getBurnUp() {
        return burn_up;
    }

    public String getFullDescription() {
        return "Класс реактора " + reactorClass + "\n"
                + "Выгорание " + burn_up + "\n"
                + "КПД " + efficiency_factor + "\n"
                + "Обогащение " + enrichment + "\n"
                + "Теплоемкость " + heatCapacity + "\n"
                + "Электрическая мощность " + electricalCapacity + "\n"
                + "Продолжительность жизни " + lifeTime + "\n"
                + "Первая загрузка " + firstLoad + "\n\n"
                + "Ресурс: " + source;
    }
}