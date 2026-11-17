package fr.kevyn.farmland.doonees.marche;



public class Market {
	int moneyforcoefMineur;
	int moneyforcoefFarmeur;
	int moneyforcoefAgriculteur;
	int moneyforcoefPecheur;
	int moneyforcoefTueur;
	private String lastEventMetier = "";
	private String lastEventMessage = "";

	// Surplus de jetons sur le marche, par metier (offre - demande absorbee).
	// 0 = prix de base, positif = trop de jetons (prix bas), negatif = penurie (prix haut).
	// Double (objet) : null dans une ancienne sauvegarde -> initialise depuis le coefficient.
	private Double surplusMineur;
	private Double surplusFarmeur;
	private Double surplusAgriculteur;
	private Double surplusPecheur;
	private Double surplusTueur;

	// Volume normal appris par metier (jetons vendus par heure, moyenne glissante).
	// null dans une ancienne sauvegarde -> volume minimal de la config.
	private Double volumeMineur;
	private Double volumeFarmeur;
	private Double volumeAgriculteur;
	private Double volumePecheur;
	private Double volumeTueur;
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
	


	/** Surplus du metier ("Mineur", "Farmeur"...), null s'il n'a jamais ete initialise. */
	public Double getSurplus(String metier) {
		return switch (metier) {
			case "Mineur" -> surplusMineur;
			case "Farmeur" -> surplusFarmeur;
			case "Agriculteur" -> surplusAgriculteur;
			case "Pecheur" -> surplusPecheur;
			case "Tueur" -> surplusTueur;
			default -> null;
		};
	}

	public void setSurplus(String metier, double valeur) {
		switch (metier) {
			case "Mineur" -> surplusMineur = valeur;
			case "Farmeur" -> surplusFarmeur = valeur;
			case "Agriculteur" -> surplusAgriculteur = valeur;
			case "Pecheur" -> surplusPecheur = valeur;
			case "Tueur" -> surplusTueur = valeur;
			default -> { }
		}
	}

	/** Coefficient affiche (en %) du metier. */
	public int getCoefficient(String metier) {
		return switch (metier) {
			case "Mineur" -> moneyforcoefMineur;
			case "Farmeur" -> moneyforcoefFarmeur;
			case "Agriculteur" -> moneyforcoefAgriculteur;
			case "Pecheur" -> moneyforcoefPecheur;
			case "Tueur" -> moneyforcoefTueur;
			default -> 100;
		};
	}

	public void setCoefficient(String metier, int valeur) {
		switch (metier) {
			case "Mineur" -> moneyforcoefMineur = valeur;
			case "Farmeur" -> moneyforcoefFarmeur = valeur;
			case "Agriculteur" -> moneyforcoefAgriculteur = valeur;
			case "Pecheur" -> moneyforcoefPecheur = valeur;
			case "Tueur" -> moneyforcoefTueur = valeur;
			default -> { }
		}
	}

	/** Volume normal appris du metier (jetons/heure), null s'il n'a jamais ete initialise. */
	public Double getVolume(String metier) {
		return switch (metier) {
			case "Mineur" -> volumeMineur;
			case "Farmeur" -> volumeFarmeur;
			case "Agriculteur" -> volumeAgriculteur;
			case "Pecheur" -> volumePecheur;
			case "Tueur" -> volumeTueur;
			default -> null;
		};
	}

	public void setVolume(String metier, double valeur) {
		switch (metier) {
			case "Mineur" -> volumeMineur = valeur;
			case "Farmeur" -> volumeFarmeur = valeur;
			case "Agriculteur" -> volumeAgriculteur = valeur;
			case "Pecheur" -> volumePecheur = valeur;
			case "Tueur" -> volumeTueur = valeur;
			default -> { }
		}
	}
}
