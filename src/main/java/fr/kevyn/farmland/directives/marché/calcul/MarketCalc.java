package fr.kevyn.farmland.directives.marché.calcul;

import fr.kevyn.farmland.doonees.marche.Market;

import java.util.HashMap;
import java.util.Map;

/**
 * Coeur du marche par OFFRE et DEMANDE, par metier.
 *
 * OFFRE : chaque metier a un SURPLUS de jetons sur le marche. Chaque jeton vendu
 * l'augmente : le prix baisse PENDANT la vente et le joueur touche le prix moyen.
 * Vendre en une fois ou en plusieurs fois a la suite rapporte pareil : seul le fait
 * d'ATTENDRE entre deux ventes laisse le prix remonter.
 *
 * DEMANDE : le marche APPREND le volume normal vendu de chaque metier (jetons/heure,
 * moyenne glissante sur quelques jours). Chaque cycle, il absorbe ce volume normal
 * (et fait fondre un surplus positif avec une demi-vie). Le prix reagit donc aux
 * ECARTS par rapport au volume habituel, quel que soit le nombre de joueurs ou leur niveau.
 * Protection : une heure de ventes ne compte au plus que "plafond" fois le volume normal,
 * un gros dump ne fait pas gonfler la demande.
 *
 * PRIX : multiplicateur = 1 - 0,9 * tanh(surplus / echelle), echelle = volume normal * echelleHeures.
 *   surplus 0 -> prix de base ; gros surplus -> -90 % ; penurie (surplus negatif) -> jusqu'a +90 %.
 */
public class MarketCalc {

    public static final String MINEUR = "Mineur";
    public static final String FARMEUR = "Farmeur";
    public static final String PECHEUR = "Pecheur";
    public static final String AGRICULTEUR = "Agriculteur";
    public static final String TUEUR = "Tueur";

    public static final String[] METIERS = {MINEUR, FARMEUR, AGRICULTEUR, PECHEUR, TUEUR};

    // ===== PRIX DE BASE (calibres le 17/11 sur les mesures du serveur de test :
    // meme $FB/heure que le Mineur a effort egal ; Farmeur bas car son volume
    // depend de la taille du champ et le marche regule les gros volumes) =====
    private static final Map<String, Integer> PRIX_DE_BASE = new HashMap<>();
    static {
        PRIX_DE_BASE.put(MINEUR, 15);
        PRIX_DE_BASE.put(FARMEUR, 8);
        PRIX_DE_BASE.put(PECHEUR, 16);
        PRIX_DE_BASE.put(AGRICULTEUR, 23);
        PRIX_DE_BASE.put(TUEUR, 17);
    }

    /** Ecart maximal autour du prix de base : 0,9 = de -90 % a +90 %. */
    static final double AMPLITUDE = 0.9;

    // ===== REGLAGES (config.yml, section "marche") =====
    private static double volumeMin = 2000.0;        // volume normal minimal (jetons/h) : un metier peu joue reste reactif
    private static double fenetreHeures = 72.0;      // l'apprentissage suit la moyenne des ~3 derniers jours
    private static double plafondApprentissage = 3.0;// une heure compte au plus 3x le volume normal (anti-dump)
    private static double echelleHeures = 24.0;      // echelle du prix = 24 h de volume normal
    private static double demiVieHeures = 12.0;      // un surplus positif fond de moitie en 12 h
    private static double penurieMax = 4.0;          // surplus minimal = -4 echelles (~ +90 %)

    public static void configurer(double nouveauVolumeMin, double nouvelleFenetre, double nouveauPlafond,
                                  double nouvelleEchelleHeures, double nouvelleDemiVie, double nouvellePenurieMax) {
        volumeMin = Math.max(1.0, nouveauVolumeMin);
        fenetreHeures = Math.max(1.0, nouvelleFenetre);
        plafondApprentissage = Math.max(1.0, nouveauPlafond);
        echelleHeures = Math.max(0.1, nouvelleEchelleHeures);
        demiVieHeures = Math.max(0.01, nouvelleDemiVie);
        penurieMax = Math.max(0.0, nouvellePenurieMax);
    }

    public static int getPrixDeBase(String metier) {
        return PRIX_DE_BASE.getOrDefault(metier, 0);
    }

    // ===== COURBE DU PRIX (echelle explicite) =====

    /** Multiplicateur du prix (0,1 a 1,9) pour un surplus et une echelle donnes. */
    public static double multiplicateur(double surplus, double echelle) {
        return 1.0 - AMPLITUDE * Math.tanh(surplus / echelle);
    }

    /** Coefficient affiche en % (arrondi). */
    public static int coefficient(double surplus, double echelle) {
        return (int) Math.round(multiplicateur(surplus, echelle) * 100.0);
    }

    /** Surplus qui donne le coefficient demande (inverse de coefficient), borne a 11..189 %. */
    public static double surplusPourCoefficient(int coefficient, double echelle) {
        int borne = Math.max(11, Math.min(189, coefficient));
        double t = (1.0 - borne / 100.0) / AMPLITUDE;
        return echelle * 0.5 * Math.log((1.0 + t) / (1.0 - t)); // atanh(t)
    }

    // ln(cosh(y)) sans debordement pour les grands y
    private static double logCosh(double y) {
        double a = Math.abs(y);
        return a + Math.log1p(Math.exp(-2.0 * a)) - Math.log(2.0);
    }

    // Primitive du multiplicateur : x - A * K * ln(cosh(x / K))
    private static double primitive(double x, double echelle) {
        return x - AMPLITUDE * echelle * logCosh(x / echelle);
    }

    /** Recette exacte (non arrondie) d'une vente de "quantite" jetons a partir de "surplus". */
    public static double recette(String metier, double surplus, long quantite, double echelle) {
        if (quantite <= 0) return 0.0;
        return getPrixDeBase(metier) * (primitive(surplus + quantite, echelle) - primitive(surplus, echelle));
    }

    /**
     * Demande pendant "heures" : un surplus positif fond (demi-vie), puis le volume normal
     * est absorbe. Sans vente, le surplus devient negatif (penurie), borne a -penurieMax echelles.
     */
    public static double appliquerDemande(double surplus, double heures, double volumeNormal) {
        if (heures <= 0) return surplus;
        double apres = surplus > 0 ? surplus * Math.pow(0.5, heures / demiVieHeures) : surplus;
        apres -= volumeNormal * heures;
        return Math.max(-penurieMax * volumeNormal * echelleHeures, apres);
    }

    /**
     * Apprentissage du volume normal apres "heures" de ventes : moyenne glissante,
     * l'heure observee etant plafonnee a plafondApprentissage x le volume actuel.
     */
    public static double apprendreVolume(double volumeActuel, double jetonsVendus, double heures) {
        if (heures <= 0) return Math.max(volumeMin, volumeActuel);
        double observe = Math.min(jetonsVendus / heures, plafondApprentissage * volumeActuel);
        double poids = Math.min(1.0, heures / fenetreHeures);
        return Math.max(volumeMin, volumeActuel + (observe - volumeActuel) * poids);
    }

    // ===== ETAT DU MARCHE =====

    /** Volume normal appris du metier (jetons/heure), au moins volumeMin. */
    public static double getVolume(String metier, Market market) {
        Double v = market.getVolume(metier);
        return v == null ? volumeMin : Math.max(volumeMin, v);
    }

    public static void setVolume(String metier, Market market, double volume) {
        market.setVolume(metier, Math.max(volumeMin, volume));
        // l'echelle change : le coefficient affiche suit
        Double s = market.getSurplus(metier);
        if (s != null) market.setCoefficient(metier, coefficient(s, echelle(metier, market)));
    }

    /** Echelle du prix du metier = volume normal * echelleHeures. */
    public static double echelle(String metier, Market market) {
        return getVolume(metier, market) * echelleHeures;
    }

    /** Surplus actuel du metier (initialise depuis le coefficient pour une ancienne sauvegarde). */
    public static double getSurplus(String metier, Market market) {
        Double s = market.getSurplus(metier);
        if (s == null) {
            double depuisCoef = surplusPourCoefficient(market.getCoefficient(metier), echelle(metier, market));
            market.setSurplus(metier, depuisCoef);
            return depuisCoef;
        }
        return s;
    }

    /** Fixe le surplus (borne a la penurie max) et met a jour le coefficient affiche. */
    public static void setSurplus(String metier, Market market, double surplus) {
        double echelle = echelle(metier, market);
        double borne = Math.max(-penurieMax * echelle, surplus);
        market.setSurplus(metier, borne);
        market.setCoefficient(metier, coefficient(borne, echelle));
    }

    /** Ancienne sauvegarde (sans volume ni surplus) : volume minimal, surplus deduit des coefficients. */
    public static void initialiser(Market market) {
        for (String metier : METIERS) {
            if (market.getVolume(metier) == null) market.setVolume(metier, volumeMin);
            setSurplus(metier, market, getSurplus(metier, market));
        }
    }

    /** Prix actuel d'UN jeton, minimum 0,01. */
    public static double getPrixActuel(String metier, Market market) {
        Integer base = PRIX_DE_BASE.get(metier);
        if (base == null) return 0.0;
        Double surplus = market.getSurplus(metier);
        if (surplus == null) {
            // marche sans surplus (ancien format, ex. ancien point de l'historique) : coefficient seul
            return Math.max(0.01, base * (market.getCoefficient(metier) / 100.0));
        }
        return Math.max(0.01, base * multiplicateur(surplus, echelle(metier, market)));
    }

    /** Estimation d'une vente sans la faire (pour le menu de quantite). */
    public static Vente estimer(String metier, Market market, long quantite) {
        double surplus = getSurplus(metier, market);
        double echelle = echelle(metier, market);
        double recette = recette(metier, surplus, quantite, echelle);
        double prixAvant = getPrixDeBase(metier) * multiplicateur(surplus, echelle);
        double prixApres = getPrixDeBase(metier) * multiplicateur(surplus + Math.max(0, quantite), echelle);
        return new Vente(Math.round(recette), quantite > 0 ? recette / quantite : 0.0, prixAvant, prixApres);
    }

    /** Resultat d'une vente. */
    public record Vente(long gain, double prixMoyen, double prixAvant, double prixApres) {}

    /** Vend "quantite" jetons : calcule le gain, augmente le surplus, met a jour le coefficient. */
    public static Vente vendre(String metier, Market market, long quantite) {
        Vente vente = estimer(metier, market, quantite);
        if (quantite <= 0) return vente;
        setSurplus(metier, market, getSurplus(metier, market) + quantite);
        enregistrerVentes(metier, quantite);
        return vente;
    }

    // ===== JETONS VENDUS (en memoire) : pour le bulletin (30 min) et l'apprentissage (1 h) =====
    private static final Map<String, Long> ventesBulletin = new HashMap<>();
    private static final Map<String, Long> ventesApprentissage = new HashMap<>();

    public static void enregistrerVentes(String metier, long quantite) {
        if (quantite <= 0) return;
        ventesBulletin.merge(metier, quantite, Long::sum);
        ventesApprentissage.merge(metier, quantite, Long::sum);
    }

    public static Map<String, Long> getVentesParMetier() {
        return ventesBulletin;
    }

    public static void resetVentes() {
        ventesBulletin.clear();
    }

    /** Jetons vendus depuis le dernier apprentissage, puis remise a zero. */
    public static long prendreVentesApprentissage(String metier) {
        Long v = ventesApprentissage.remove(metier);
        return v == null ? 0L : v;
    }

    public static void resetVentesApprentissage() {
        ventesApprentissage.clear();
    }

    /** Portions proposees dans le menu de vente du /bag (en % du stock). */
    public static final int[] PORTIONS = {10, 25, 50, 100};

    /** Jetons vendus pour un pourcentage du stock : arrondi en dessous, au moins 1 s'il reste du stock. */
    public static int quantitePourcentage(int stock, int pourcentage) {
        if (stock <= 0) return 0;
        if (pourcentage >= 100) return stock;
        long q = (long) stock * Math.max(0, pourcentage) / 100;
        return (int) Math.max(1, q);
    }

    public static String nomAffiche(String metier) {
        return PECHEUR.equals(metier) ? "Pêcheur" : metier;
    }
}
