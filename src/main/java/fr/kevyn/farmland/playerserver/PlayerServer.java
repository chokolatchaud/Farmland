package fr.kevyn.farmland.playerserver;

import fr.kevyn.plot.PlotData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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




    // ===== GRAINES - stock SEPARE des ressources vendables du /bag =====
    Map<Material, Integer> graines = new HashMap<>();


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


    int jetonMineur = 0;
    public int getJetonMineur() { return jetonMineur; }
    public void setJetonMineur(int valeur) { this.jetonMineur = valeur; }
    public void addJetonMineur(int quantite) { this.jetonMineur += quantite; }

    int jetonFarmeur = 0;
    public int getJetonFarmeur() { return jetonFarmeur; }
    public void setJetonFarmeur(int valeur) { this.jetonFarmeur = valeur; }
    public void addJetonFarmeur(int quantite) { this.jetonFarmeur += quantite; }

    int jetonPecheur = 0;
    public int getJetonPecheur() { return jetonPecheur; }
    public void setJetonPecheur(int valeur) { this.jetonPecheur = valeur; }
    public void addJetonPecheur(int quantite) { this.jetonPecheur += quantite; }

    int jetonAgriculteur = 0;
    public int getJetonAgriculteur() { return jetonAgriculteur; }
    public void setJetonAgriculteur(int valeur) { this.jetonAgriculteur = valeur; }
    public void addJetonAgriculteur(int quantite) { this.jetonAgriculteur += quantite; }

    int jetonTueur = 0;
    public int getJetonTueur() { return jetonTueur; }
    public void setJetonTueur(int valeur) { this.jetonTueur = valeur; }
    public void addJetonTueur(int quantite) { this.jetonTueur += quantite; }

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
