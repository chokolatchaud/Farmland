package fr.kevyn.farmland.doonees.plots;

import fr.kevyn.farmland.directives.plot.gestion.Meteo;

import java.util.ArrayList;

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
    boolean mobSpawn;
    Meteo meteoActive;
    Meteo meteoTime;
    Meteo meteoRain;
    String NameWorld;
	
	
    
    public PlotData (String PlotProprety,ArrayList<String> allplotadd,ArrayList<String> allplottrust, String NameWorld,int worldborder,int boost,Meteo meteoActive,Meteo meteoTime,Meteo meteoRain) {
        this.PlotProprety = PlotProprety;
        this.allplotadd = allplotadd;
        this.allplottrust = allplottrust;
        this.NameWorld = NameWorld;
        this.worldborder = worldborder;
        this.boost = boost;
        this.waterlava = true;
        this.locationspawnX = 0;
        this.locationspawnY = 0;
        this.locationspawnZ = 0;
        this.privateplot = false;
        this.meteoActive = meteoActive;
        this.meteoTime = meteoTime;
        this.meteoRain = meteoRain;
        this.mobSpawn = true;
    }
    
    public Meteo getMeteoActive() {
        return meteoActive;
    }
    public Meteo getMeteoTime() {
		return meteoTime;
	}
    public Meteo getMeteoRain() {
		return meteoRain;
	}
    public void setMeteoRain(Meteo meteoRain) {this.meteoRain = meteoRain;}
    public void setMeteoTime(Meteo meteoTime) {this.meteoTime = meteoTime;
        }



    
    public void setMeteoActive(Meteo meteo) {this.meteoActive = meteo;}
    
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
    public boolean getMobSpawn() {return mobSpawn;}
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
    public void setMobSpawn(boolean mobSpawn) {this.mobSpawn = mobSpawn;}
    
    public void setWorldborder(int worldborder) {this.worldborder = worldborder;}
    
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