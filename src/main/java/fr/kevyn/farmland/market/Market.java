package fr.kevyn.farmland.market;



public class Market {
	int moneyforcoefMineur;
	int moneyforcoefFarmeur;
	int moneyforcoefAgriculteur;
	int moneyforcoefPecheur;
	int moneyforcoefTueur;
	private String lastEventMetier = "";
	private String lastEventMessage = "";
	public Market(int moneyforcoefMineur,int moneyforcoefFarmeur,int moneyforcoefAgriculteur,int moneyforcoefPecheur,int moneyforcoefTueur) {
		this.moneyforcoefMineur = moneyforcoefMineur;
		this.moneyforcoefFarmeur = moneyforcoefFarmeur;
		this.moneyforcoefAgriculteur = moneyforcoefAgriculteur;
		this.moneyforcoefPecheur = moneyforcoefPecheur;
		this.moneyforcoefTueur = moneyforcoefTueur;
		// TODO Auto-generated constructor stub
	}
	public int getMoneyforcoefAgriculteur() {
		return moneyforcoefAgriculteur;
	}
	public int getMoneyforcoefFarmeur() {
		return moneyforcoefFarmeur;
	}
	public int getMoneyforcoefMineur() {
		return moneyforcoefMineur;
	}
	public int getMoneyforcoefPecheur() {
		return moneyforcoefPecheur;
	}
	public int getMoneyforcoefTueur() {
		return moneyforcoefTueur;
	}
	public void setMoneyforcoefAgriculteur(int moneyforcoefAgriculteur) {
		this.moneyforcoefAgriculteur = moneyforcoefAgriculteur;
	}
	public void setMoneyforcoefFarmeur(int moneyforcoefFarmeur) {
		this.moneyforcoefFarmeur = moneyforcoefFarmeur;
	}
	public void setMoneyforcoefMineur(int moneyforcoefMineur) {
		this.moneyforcoefMineur = moneyforcoefMineur;
	}
	public void setMoneyforcoefPecheur(int moneyforcoefPecheur) {
		this.moneyforcoefPecheur = moneyforcoefPecheur;
	}
	public void setMoneyforcoefTueur(int moneyforcoefTueur) {
		this.moneyforcoefTueur = moneyforcoefTueur;
	}

	public String getLastEventMetier() {
		return lastEventMetier;
	}

	public void setLastEventMetier(String lastEventMetier) {
		this.lastEventMetier = lastEventMetier;
	}

	public String getLastEventMessage() {
		return lastEventMessage;
	}

	public void setLastEventMessage(String lastEventMessage) {
		this.lastEventMessage = lastEventMessage;
	}
	

}
