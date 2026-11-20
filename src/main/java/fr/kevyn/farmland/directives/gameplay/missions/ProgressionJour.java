package fr.kevyn.farmland.directives.gameplay.missions;

/**
 * Avancement d'un joueur sur ses 3 missions d'un jour (enregistré dans missions.json).
 * Les missions elles-mêmes ne sont pas enregistrées : on les retrouve par le tirage
 * (même joueur + même jour = mêmes missions).
 */
public class ProgressionJour {

    String date;
    int[] progression = new int[MissionsCalc.NB_MISSIONS];
    boolean[] faites = new boolean[MissionsCalc.NB_MISSIONS];
    boolean bonus;

    public ProgressionJour() {
    }

    public ProgressionJour(String date) {
        this.date = date;
    }

    public String getDate() { return date; }

    public int getProgression(int i) { return progression[i]; }

    public boolean estFaite(int i) { return faites[i]; }

    public boolean bonusDonne() { return bonus; }

    public int nombreFaites() {
        int n = 0;
        for (boolean f : faites) if (f) n++;
        return n;
    }

    /** Faux pour une entrée lue d'un fichier abîmé : on repart alors de zéro. */
    boolean estValide() {
        return date != null && progression != null && faites != null
                && progression.length == MissionsCalc.NB_MISSIONS && faites.length == MissionsCalc.NB_MISSIONS;
    }
}
