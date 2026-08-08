package fr.kevyn.farmland.playerserver;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
    int blocposetotal;
    String grade;
    PlotData plotdata;
    int upgrade;
    long weTimeExpiry;
    Map<Material, Integer> ressources = new HashMap<>();
    int cobblestonegeneratorlevel = 1;
    


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
        


        PlayerserverHashMap.getInstance().AddplayerHaspMaps(uuid, this);
    }
    
    
    public int getCobblestonegeneratorlevel() {
		return cobblestonegeneratorlevel;
	}
    public void setCobblestonegeneratorlevel(int cobblestonegeneratorlevel) {
		this.cobblestonegeneratorlevel = cobblestonegeneratorlevel;
	}
    
    
    public int getRessource(Material material) {
        return ressources.getOrDefault(material, 0);
    }

    public void addRessource(Material material , int quantite) {
        ressources.put(material, getRessource(material) + quantite);
    }
    
    public void RemoveRessource(Material material , int quantite) {
        ressources.put(material, getRessource(material) - quantite);
    }

    // ===== GRAINES - stock SEPARE des ressources vendables du /bag =====
    Map<Material, Integer> graines = new HashMap<>();

    public int getGraine(Material material) {
        return graines.getOrDefault(material, 0);
    }

    public void addGraine(Material material, int quantite) {
        graines.put(material, getGraine(material) + quantite);
    }

    public void removeGraine(Material material, int quantite) {
        graines.put(material, getGraine(material) - quantite);
    }

    // ===== NIVEAUX D'AMELIORATION DES OUTILS - 0 a 10, cout croissant =====
    int piocheLevel = 0;
    public int getPiocheLevel() { return piocheLevel; }
    public void setPiocheLevel(int piocheLevel) { this.piocheLevel = piocheLevel; }

    int houeLevel = 0;
    public int getHoueLevel() { return houeLevel; }
    public void setHoueLevel(int houeLevel) { this.houeLevel = houeLevel; }

    int canneLevel = 0;
    public int getCanneLevel() { return canneLevel; }
    public void setCanneLevel(int canneLevel) { this.canneLevel = canneLevel; }

    // ===== XP PAR METIER - gratuit, fait monter le niveau automatiquement =====
    Map<String, Integer> xpMetiers = new HashMap<>();

    public int getXp(String metier) {
        return xpMetiers.getOrDefault(metier, 0);
    }

    public void setXp(String metier, int valeur) {
        xpMetiers.put(metier, valeur);
    }

    // niveaux d'amelioration Agriculteur/Tueur - separes, mais la formule de
    // degats est partagee (voir ArmesUtil.calculerDegats)
    int epeeLevel = 0;
    public int getEpeeLevel() { return epeeLevel; }
    public void setEpeeLevel(int epeeLevel) { this.epeeLevel = epeeLevel; }

    int hacheLevel = 0;
    public int getHacheLevel() { return hacheLevel; }
    public void setHacheLevel(int hacheLevel) { this.hacheLevel = hacheLevel; }


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
    public int getBlocposetotal() {
        return blocposetotal;
    }
    public void setBlocposetotal(int blocposetotal) {
        this.blocposetotal = blocposetotal;
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

    // cosmetiques possedes (IDs) - voir fr.kevyn.farmland.cosmetics.CosmeticShop
    java.util.Set<Integer> cosmeticsOwned = new java.util.HashSet<>();
    public java.util.Set<Integer> getCosmeticsOwned() {
        return cosmeticsOwned;
    }

    // WorldEdit temporaire
    public long getWeTimeExpiry() { return weTimeExpiry; }
    public void setWeTimeExpiry(long weTimeExpiry) { this.weTimeExpiry = weTimeExpiry; }
    public boolean isWeActive() { return weTimeExpiry > System.currentTimeMillis(); }
    public long getWeTimeRemaining() { return Math.max(0, weTimeExpiry - System.currentTimeMillis()); }
    public static Player getplayer(PlayerServer playerserver) {
    	Player player = Bukkit.getPlayer(playerserver.getUuid());
    	if(player == null) {
    		return null;	
    	}
    	return player;
    }

  

	

}
