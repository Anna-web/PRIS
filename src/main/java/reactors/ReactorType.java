package reactors;

public class ReactorType {
    private final String reactor_class;
    private final Double burnup;
    private final Double electrical_capacity;
    private final Double enrichment;
    private final Double first_load;
    private final Double efficiency_factor;
    private final Integer life_time;
    private final Double heat_capacity;
    private final String source;

    public ReactorType(
            String type, String reactor_class, Double burnup, Double efficiency_factor,
            Double enrichment, Double heat_capacity, Double electrical_capacity,
            Integer life_time, Double first_load, String source
    ) {
        this.reactor_class = reactor_class;
        this.burnup = burnup;
        this.electrical_capacity = electrical_capacity;
        this.enrichment = enrichment;
        this.first_load = first_load;
        this.efficiency_factor = efficiency_factor;
        this.life_time = life_time;
        this.heat_capacity = heat_capacity;
        this.source = source;
    }

    @Override
    public String toString() {
        return reactor_class;
    }
    public Double getBurnUp() {
        return burnup;
    }
    public String getFullDescription() {
        return "Reactor class: " + reactor_class + "\n"
                + "Burnup: " + burnup + "\n"
                + "KPD: " + efficiency_factor + "\n"
                + "Enrichment: " + enrichment + "\n"
                + "Heat capacity: " + heat_capacity + "\n"
                + "Electrical capacity: " + electrical_capacity + "\n"
                + "Life time:  " + life_time + "\n"
                + "First load:  " + first_load + "\n\n"
                + "Source: " + source;
    }
}