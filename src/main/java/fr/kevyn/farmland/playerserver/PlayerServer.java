package fr.kevyn.farmland.playerserver;

import fr.kevyn.farmland.utils.BanData;
import fr.kevyn.farmland.utils.JobType;
import fr.kevyn.farmland.utils.ToolType;
import fr.kevyn.plot.PlotData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerServer {

    // -- Identité du joueur-- //
    UUID uuid;
    String Name;
    String grade;
    Boolean lastjoin;
    final BanData banData = new BanData();

    // -- Statistiques & Économie -- //
    int money;
    int blocPose;
    int blocPoseTotal;
    int upgrade;
    int cobblestonegeneratorlevel = 1;
    long weTimeExpiry;
    PlotData plotdata;

    // -- Outils & Jetons -- //
    private final Map<ToolType, Integer> toolLevels = new EnumMap<>(ToolType.class);
    private final Map<JobType, Integer> jobTokens = new EnumMap<>(JobType.class);


    public PlayerServer() {}

    public PlayerServer(UUID uuid,String Name, Boolean lastjoin, boolean ban,String raison,int money,PlotData plotdata,int blocpose, String grade,int upgrade) {
        this.uuid = uuid;
        this.Name = Name;
        this.lastjoin = lastjoin;
        this.banData.setBanned(ban);
        this.grade = grade;
        this.banData.setReason(raison);
        this.money = money;
        this.plotdata = plotdata;
        this.blocPose = blocpose;
        this.upgrade = upgrade;
        


        PlayerserverHashMap.getInstance().AddplayerHaspMaps(uuid, this);
    }
    
    
    public int getCobblestonegeneratorlevel() {
		return cobblestonegeneratorlevel;
	}
    public void setCobblestonegeneratorlevel(int cobblestonegeneratorlevel) {
		this.cobblestonegeneratorlevel = cobblestonegeneratorlevel;
	}


    // -- Accès aux outils -- //

    public int getToolLevel(ToolType tool) {
        return toolLevels.getOrDefault(tool, 0);
    }

    public void setToolLevel(ToolType tool, int level) {
        toolLevels.put(tool, level);
    }

    // -- Accès aux jetons -- //
    public int getJobTokens(JobType job) {
        return jobTokens.getOrDefault(job, 0);
    }

    public void setJobTokens(JobType job, int amount) {
        jobTokens.put(job, amount);
    }

    public void addJobTokens(JobType job, int amount) {
        setJobTokens(job, getJobTokens(job) + amount);
    }


    // ===== GRAINES - stock SEPARE des ressources vendables du /bag =====
    Map<Material, Integer> graines = new HashMap<>();

    public int getHoueLevel() { return getToolLevel(ToolType.HOUE); }
    public void setHoueLevel(int level) { setToolLevel(ToolType.HOUE, level); }

    public int getCanneLevel() { return getToolLevel(ToolType.CANNE); }
    public void setCanneLevel(int level) { setToolLevel(ToolType.CANNE, level); }

    // ===== XP PAR METIER - gratuit, fait monter le niveau automatiquement =====
    Map<String, Integer> xpMetiers = new HashMap<>();

    public int getXp(String metier) {
        return xpMetiers.getOrDefault(metier, 0);
    }

    public void setXp(String metier, int valeur) {
        xpMetiers.put(metier, valeur);
    }


    public int getJetonMineur() { return getJobTokens(JobType.MINEUR); }
    public void setJetonMineur(int valeur) { setJobTokens(JobType.MINEUR, valeur); }
    public void addJetonMineur(int quantite) { addJobTokens(JobType.MINEUR, quantite); }

    public int getJetonFarmeur() { return getJobTokens(JobType.FARMEUR); }
    public void setJetonFarmeur(int valeur) { setJobTokens(JobType.FARMEUR, valeur); }
    public void addJetonFarmeur(int quantite) { addJobTokens(JobType.FARMEUR, quantite); }

    public int getJetonPecheur() { return getJobTokens(JobType.PECHEUR); }
    public void setJetonPecheur(int valeur) { setJobTokens(JobType.PECHEUR, valeur); }
    public void addJetonPecheur(int quantite) { addJobTokens(JobType.PECHEUR, quantite); }

    public int getJetonAgriculteur() { return getJobTokens(JobType.AGRICULTEUR); }
    public void setJetonAgriculteur(int valeur) { setJobTokens(JobType.AGRICULTEUR, valeur); }
    public void addJetonAgriculteur(int quantite) { addJobTokens(JobType.AGRICULTEUR, quantite); }

    public int getJetonTueur() { return getJobTokens(JobType.TUEUR); }
    public void setJetonTueur(int valeur) { setJobTokens(JobType.TUEUR, valeur); }
    public void addJetonTueur(int quantite) { addJobTokens(JobType.TUEUR, quantite); }

    // niveaux d'amelioration Agriculteur/Tueur - separes, mais la formule de
    // degats est partagee (voir ArmesUtil.calculerDegats)
    public int getEpeeLevel() { return getToolLevel(ToolType.EPEE); }
    public void setEpeeLevel(int level) { setToolLevel(ToolType.EPEE, level); }

    public int getHacheLevel() { return getToolLevel(ToolType.HACHE); }
    public void setHacheLevel(int level) { setToolLevel(ToolType.EPEE, level); }


    public Boolean getLastjoin() {
        return lastjoin;
    }
    public int getMoney() {
        return money;
    }
    public String getName() {
        return Name;
    }
    public String getRaison() { return banData.getReason(); }
    public UUID getUuid() {
        return uuid;
    }
    public boolean getBan() { return banData.isBanned(); }
    public void setBan(boolean ban) { this.banData.setBanned(ban); }
    public void setLastjoin(Boolean lastjoin) {
        this.lastjoin = lastjoin;
    }
    public void setMoney(int money) {
        this.money = money;
    }
    public void setName(String name) {
        Name = name;
    }
    public void setRaison(String raison) { this.banData.setReason(raison); }
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
    public PlotData getPlotdata() {
        return plotdata;
    }
    public int getBlocpose() {
        return blocPose;
    }
    public void setBlocpose(int blocpose) {
        this.blocPose = blocpose;
    }
    public int getBlocposetotal() {
        return blocPoseTotal;
    }
    public void setBlocposetotal(int blocposetotal) {
        this.blocPoseTotal = blocposetotal;
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
