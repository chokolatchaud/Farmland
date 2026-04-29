package fr.kevyn.plot;

import java.util.ArrayList;

import org.bukkit.GameRule;
import org.bukkit.World;

public class PlotData {
    String PlotProprety;
    ArrayList<String> allplotadd = new ArrayList<String>();
    ArrayList<String> allplottrust = new ArrayList<String>();
    int locationspawnX;
    int locationspawnZ;
    int locationspawnY;
    boolean privateplot;
    int worldborder;
    int boost;
    boolean waterlava;
    String meteoActive;
    String meteoTime;
    String meteoRain;

    String NameWorld;
	
	
    
    public PlotData (String PlotProprety,ArrayList<String> allplotadd,ArrayList<String> allplottrust, String NameWorld,int worldborder,int boost,String meteoActive,String meteoTime,String meteoRain) {
        this.PlotProprety = PlotProprety;
        this.allplotadd = allplotadd;
        this.allplottrust = allplottrust;
        this.NameWorld = NameWorld;
        this.worldborder = 50;
        this.boost = 0;
        this.waterlava = true;
        this.locationspawnX = 0;
        this.locationspawnY = 0;
        this.locationspawnZ = 0;
        this.privateplot = false;
        this.meteoActive = "minecraftActive";
        this.meteoTime = "day";
        this.meteoRain ="weatherclear";
    }
    
    public String getMeteoActive() {
        return meteoActive;
    }
    public String getMeteoTime() {
		return meteoTime;
	}
    public String getMeteoRain() {
		return meteoRain;
	}
    public void setMeteoRain(String meteoRain, World plotworld) {
		this.meteoRain = meteoRain;
		if(meteoRain.equalsIgnoreCase("weatherain")) {
            plotworld.setStorm(true);
            plotworld.setWeatherDuration(Integer.MAX_VALUE);
        }
        else if(meteoRain.equalsIgnoreCase("weatherclear")) {
            plotworld.setStorm(false);
            plotworld.setClearWeatherDuration(Integer.MAX_VALUE);
        }
		
	}
    public void setMeteoTime(String meteoTime,World plotworld) {
    	this.meteoTime = meteoTime;
    	if(meteoTime.equalsIgnoreCase("Day")){
            plotworld.setTime(1000);
        }
        else if(meteoTime.equalsIgnoreCase("Night")) {
            plotworld.setTime(13000);
        }
	}
    
    public void setMeteoActive(String meteo, World plotworld) {
        this.meteoActive = meteo;
        if(meteo.equalsIgnoreCase("minecraftActive")) {
            plotworld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        }
        else if(meteo.equalsIgnoreCase("minecraftDeactive")){
            plotworld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        }
    }
    
    public int getBoost() {
        return boost;
    }
    public void setBoost(int boost) {
        this.boost = boost;
    }
    public boolean getPrivateplot() {
        return privateplot;
    }
    public void setPrivateplot(boolean privateplot) {
        this.privateplot = privateplot;
    }
    public boolean getwaterlava() {
        return waterlava;
    }
    public int getLocationspawnX() {
        return locationspawnX;
    }
    public int getLocationspawnY() {
        return locationspawnY;
    }
    public int getLocationspawnZ() {
        return locationspawnZ;
    }
    public void setLocationspawnX(int locationspawnX) {
        this.locationspawnX = locationspawnX;
    }
    public void setLocationspawnY(int locationspawnY) {
        this.locationspawnY = locationspawnY;
    }
    public void setLocationspawnZ(int locationspawnZ) {
        this.locationspawnZ = locationspawnZ;
    }
    
    public ArrayList<String> getAllplotadd() {
        return allplotadd;
    }
    public ArrayList<String> getAllplottrust() {
        return allplottrust;
    }
    public String getPlotProprety() {
        return PlotProprety;
    }
    public String getNameWorld() {
        return NameWorld;
    }
    public void setNameWorld(String nameWorld) {
        NameWorld = nameWorld;
    }
    public void setPlotProprety(String plotProprety) {
        PlotProprety = plotProprety;
    }
    public void AddAllplotadd(String uuid) {
        allplotadd.add(uuid);
    }
    public void AddAllplottrust(String uuid) {
        allplottrust.add(uuid);
    }
    public int getWorldborder() {
        return worldborder;
    }

    public void setwaterlava(boolean waterlava) {
        this.waterlava = waterlava;
    }    
    
    public void setWorldborder(int worldborder) {
        this.worldborder = worldborder;
        World world = Plot.getWorldforname(NameWorld);
        if (world != null) {
            world.getWorldBorder().setSize(worldborder);
        }
    }
    
    public void RemoveAllplotadd(String ownerUuid) {
        allplotadd.remove(ownerUuid);
    }
    public void RemoveAllplottrust(String ownerUuid) {
        allplottrust.remove(ownerUuid);
    }
    
    public boolean searchplotadd(String worldName) {
        return allplotadd.contains(worldName);
    }
    
    public boolean searchplottrust(String worldName) {
        return allplottrust.contains(worldName);
    }
    public void setAllplotadd(ArrayList<String> allplotadd) {
	this.allplotadd = allplotadd;
}
    public void setAllplottrust(ArrayList<String> allplottrust) {
		this.allplottrust = allplottrust;
	}
}