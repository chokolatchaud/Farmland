package fr.kevyn.farmland.playerserver;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.kevyn.plot.PlotData;

public class PlayerServer {
    UUID uuid;
    String Name;
    Boolean lastjoin;
    boolean ban;
    String raison;
    int money;
    int blocpose;
    String grade;
    PlotData plotdata;
    int upgrade;
    int structure;
    int racepoint;


    public PlayerServer() {}

    public PlayerServer(UUID uuid,String Name, Boolean lastjoin, boolean ban,String raison,int money,PlotData plotdata,int blocpose, String grade,int upgrade) {
        this.uuid = uuid;
        this.Name = Name;
        this.lastjoin = lastjoin;
        this.ban = ban;
        this.grade = grade;
        this.raison = raison;
        this.money = money;
        this.plotdata = plotdata;
        this.blocpose = blocpose;
        this.upgrade = upgrade;
        this.structure = 0;
        this.racepoint = 0;


        PlayerserverHashMap.getInstance().AddplayerHaspMaps(uuid, this);
    }
    

    public Boolean getLastjoin() {
        return lastjoin;
    }
    public int getMoney() {
        return money;
    }
    public String getName() {
        return Name;
    }
    public String getRaison() {
        return raison;
    }
    public UUID getUuid() {
        return uuid;
    }
    public boolean getBan() {
        return ban;    
    }
    public void setBan(boolean ban) {
        this.ban = ban;
    }
    public void setLastjoin(Boolean lastjoin) {
        this.lastjoin = lastjoin;
    }
    public void setMoney(int money) {
        this.money = money;
    }
    public void setName(String name) {
        Name = name;
    }
    public void setRaison(String raison) {
        this.raison = raison;
    }
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
    public PlotData getPlotdata() {
        return plotdata;
    }
    public void setPlotdata(PlotData plotdata) {
        this.plotdata = plotdata;
    }
    public int getBlocpose() {
        return blocpose;
    }
    public void setBlocpose(int blocpose) {
        this.blocpose = blocpose;
    }
    public String getGrade() {
        return grade;
    }
    public void setGrade(String grade) {
        this.grade = grade;
    }
    public void setUpgrade(int upgrade) {
        this.upgrade = upgrade;
    }
    public int getUpgrade() {
        return upgrade;
    }
    public int getStructure() {
		return structure;
	}
    public void setStructure(int structure) {
		this.structure = structure;
	}
    
    public int getRacePoints() {
		return racepoint;
	}
    public void setRacePoints(int racepoint) {
		this.racepoint = racepoint;
	}
    public static Player getplayer(PlayerServer playerserver) {
    	Player player = Bukkit.getPlayer(playerserver.getUuid());
    	if(player == null) {
    		return null;	
    	}
    	return player;
    }

  

	

}
