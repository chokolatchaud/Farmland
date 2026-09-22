package fr.kevyn.farmland.directives.marché.affichage;

import fr.kevyn.farmland.directives.marché.calcul.MarketCalc;

import java.util.List;
import java.util.Random;

/**
 * Messages RP du marché, affichés après chaque recalcul.
 */
public final class MarketFlavor {

    private static final Random RANDOM = new Random();

    private MarketFlavor() {
    }

    public static String getMessage(String metier, boolean hausse) {
        List<String> messages = switch (metier) {
            case MarketCalc.MINEUR -> hausse
                    ? List.of(
                            "§aLe minerai devient précieux.",
                            "§aLes forges réclament du minerai !",
                            "§aLes filons se font rares..."
                    )
                    : List.of(
                            "§cLes coffres débordent de minerai.",
                            "§cLes mineurs ont trop travaillé.",
                            "§cLe marché croule sous le minerai."
                    );

            case MarketCalc.FARMEUR -> hausse
                    ? List.of(
                            "§aLes récoltes se font rares !",
                            "§aLes villages cherchent du blé.",
                            "§aLa farine vaut presque de l'or."
                    )
                    : List.of(
                            "§cOn m'en a trop fait manger...",
                            "§cLes greniers sont pleins.",
                            "§cLe marché croule sous le blé."
                    );

            case MarketCalc.AGRICULTEUR -> hausse
                    ? List.of(
                            "§aLa viande devient un luxe.",
                            "§aLes boucheries paient le prix fort !",
                            "§aLes élevages se vident."
                    )
                    : List.of(
                            "§cLes étables sont pleines.",
                            "§cIl y a trop de bétail sur le marché.",
                            "§cPersonne n'arrive à écouler tout ce stock."
                    );

            case MarketCalc.PECHEUR -> hausse
                    ? List.of(
                            "§aLes tavernes réclament du poisson frais !",
                            "§aLes quais manquent de poissons.",
                            "§aLe poisson devient une denrée rare."
                    )
                    : List.of(
                            "§cLes marchés sont envahis de poissons.",
                            "§cLes pêcheurs rentrent avec trop de prises.",
                            "§cPlus personne ne veut autant de poisson..."
                    );

            case MarketCalc.TUEUR -> hausse
                    ? List.of(
                            "§aLes contrats affluent en ville.",
                            "§aLes chasseurs sont très recherchés.",
                            "§aLes primes explosent !"
                    )
                    : List.of(
                            "§cTrop de trophées circulent.",
                            "§cLes chasseurs cassent les prix.",
                            "§cLes primes ne valent plus grand-chose."
                    );

            default -> List.of("§7Le marché reste calme.");
        };

        return messages.get(RANDOM.nextInt(messages.size()));
    }
}
