package fr.kevyn.plot;

import java.util.HashMap;
import java.util.Map;

public class PlotHashmap {
    private static final PlotHashmap INSTANCE = new PlotHashmap();
    private final Map<String, Plot> plots = new HashMap<>();

    public static PlotHashmap getInstance() {
        return INSTANCE;
    }

    public Map<String, Plot> getPlots() {
        return plots;
    }

    public void addPlotHashMap(String uuid, Plot plotObj) {
        plots.put(uuid, plotObj);
    }
}
