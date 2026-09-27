package fr.kevyn.farmland.directives.plot.gestion;

import fr.kevyn.farmland.doonees.joueurs.PlayerServer;
import fr.kevyn.farmland.doonees.joueurs.PlayerserverHashMap;
import fr.kevyn.farmland.doonees.plots.PlotData;
import org.bukkit.GameRules;
import org.bukkit.World;

public class PlotInit {

    public static void init(World world) {
            PlayerServer playerServer = PlayerserverHashMap.getInstance().getplayerHaspMaps(world.getName());
            if(playerServer == null) {
                return;
            }
            PlotData plotData = playerServer.getPlotdata();
            if(plotData == null) {
                return;
            }

            if(plotData.getMeteoActive() == Meteo.TIMETRUE){
                world.setGameRule(GameRules.ADVANCE_TIME, true);
            }else if(plotData.getMeteoActive() == Meteo.TIMEFALSE){
                world.setGameRule(GameRules.ADVANCE_TIME, false);
            }

            if(plotData.getMeteoRain() == Meteo.RAINSTOP){
                world.setClearWeatherDuration(Integer.MAX_VALUE);
            }else if(plotData.getMeteoRain() == Meteo.RAIN){
                world.setWeatherDuration(Integer.MAX_VALUE);
        }
            if(plotData.getMeteoTime() == Meteo.DAY){
                world.setTime(1000);
            }else if (plotData.getMeteoTime() == Meteo.NIGHT) {
                world.setTime(13000);  // Pleine nuit
            }
        world.getWorldBorder().setSize(plotData.getWorldborder());

    }
}
