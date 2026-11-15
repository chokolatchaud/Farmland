package fr.kevyn.farmland.directives.gameplay.recompense;

import fr.kevyn.farmland.directives.infrastructure.JobType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Vérifie le rewards.yml livré avec le plugin, lu par LevelRewardManager. */
class LevelRewardManagerTest {

    private static LevelRewardManager manager;
    private static YamlConfiguration config;

    @BeforeAll
    static void charger() throws Exception {
        try (InputStream in = LevelRewardManagerTest.class.getClassLoader().getResourceAsStream("rewards.yml")) {
            assertNotNull(in, "rewards.yml doit être dans le jar");
            config = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
        }
        manager = new LevelRewardManager(config);
    }

    @Test
    void argentAuxNiveaux10_20_40PourTousLesMetiers() {
        for (JobType job : JobType.values()) {
            assertEquals(List.of("§6Récompenses :", "§e+1000 $FB"), manager.getRewardLore(job, 10), job.name());
            assertEquals(List.of("§6Récompenses :", "§e+2500 $FB"), manager.getRewardLore(job, 20), job.name());
            assertEquals(List.of("§6Récompenses :", "§e+5000 $FB"), manager.getRewardLore(job, 40), job.name());
        }
    }

    @Test
    void beaconAuNiveau50() {
        assertEquals(List.of("§6Récompenses :", "§b1x beacon"), manager.getRewardLore(JobType.MINEUR, 50));
    }

    @Test
    void objetSpecialAuNiveau100PourChaqueMetier() {
        assertEquals("§dObjet spécial du Farmeur", manager.getRewardLore(JobType.FARMEUR, 100).get(1));
        assertEquals("§dObjet spécial du Mineur", manager.getRewardLore(JobType.MINEUR, 100).get(1));
        assertEquals("§dObjet spécial du Pêcheur", manager.getRewardLore(JobType.PECHEUR, 100).get(1));
        assertEquals("§dObjet spécial de l'Agriculteur", manager.getRewardLore(JobType.AGRICULTEUR, 100).get(1));
        assertEquals("§dObjet spécial du Tueur", manager.getRewardLore(JobType.TUEUR, 100).get(1));
    }

    @Test
    void aucunRecompenseAuxAutresNiveaux() {
        for (int niveau : new int[] { 1, 5, 11, 30, 99, 101 }) {
            assertEquals(List.of("§6Récompenses :", "§7Aucune récompense spéciale"),
                    manager.getRewardLore(JobType.FARMEUR, niveau), "niveau " + niveau);
        }
    }

    @Test
    void montantsPositifsDansRewardsYml() {
        for (String niveau : config.getConfigurationSection("rewards.all").getKeys(false)) {
            for (var r : config.getMapList("rewards.all." + niveau)) {
                if ("MONEY".equals(String.valueOf(r.get("type")))) {
                    assertTrue(((Number) r.get("amount")).intValue() > 0, "niveau " + niveau);
                }
            }
        }
    }
}
