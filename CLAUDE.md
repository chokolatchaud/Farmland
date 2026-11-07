# Farmland — plugin Paper (jeu de métiers)

Plugin Minecraft du serveur Farmland (mine.farm-land.fr). Monnaie : $FB.
Chaque joueur a un plot (monde Multiverse, bordure améliorable) et 5 métiers (Mineur, Farmeur, Agriculteur, Pêcheur, Tueur) avec niveaux et XP. Il y a un marché offre/demande par métier, une course de bateaux, des cosmétiques, un sac, un système de vote.
Le site (farm-land.fr) est dans un autre repo : le plugin lui envoie des données via `WebApiClient`.
**Ce repo est public : jamais de secret dans le code, la config versionnée ou un commit.**

## Stack
- Java 25 (pom.xml et CI), Maven, package racine `fr.kevyn.farmland`.
- Paper API `26.2.build.87-stable` (propriété `paper.version` du pom). Ne pas rétrograder Java ni Paper.
- `plugin.yml` : `depend` PlaceholderAPI + Multiverse-Core ; `softdepend` Votifier, NuVotifier, RoseStacker.
- Autres dépendances `provided` : FAWE, LuckPerms API, VaultAPI. OkHttp 4.12.0 est embarqué par le plugin shade (aucune relocation configurée).

## Build et CI
- Compiler : `mvn clean package` (jar dans `target/`).
- CI `build.yml` : Java 25, `mvn -B clean package -DskipTests`. Les tests sont donc ignorés.
- `discord-notify.yml` envoie les commits sur Discord via le secret GitHub `DISCORD_WEBHOOK`.
- Tests : JUnit 5 (5.13.4, scope `test`, non embarqué dans le jar) et Surefire 3.5.3 dans le pom. 22 tests dans `src/test/java` (marché : `MarketCalcTaskTest`, `MarketCalcTest`). Ils tournent avec `mvn test` en local, pas en CI.

## Cycle de validation (toujours dans cet ordre)
1. Toi : tu modifies, tu compiles, tu lances les tests, tu testes sur le serveur de test, tu commites en local.
2. Moi : je teste en jeu sur le serveur de test.
3. Moi : j'envoie le jar sur le vrai serveur en SFTP et je teste là-bas.
4. Moi : si tout est bon, je te demande de pousser sur GitHub (ou je pousse moi-même).

Conséquences pour toi :
- Les commits restent locaux jusqu'à ma validation sur le vrai serveur. Ne pousse jamais avant, même si le test local est vert.
- Le jar que je déploie doit venir d'un commit précis. À la fin de chaque tâche, donne-moi le hash du commit à partir duquel le jar a été construit, pour que je sache quelle version tourne en prod.
- Si je te dis que ça casse en prod, ne pousse rien : aide-moi à corriger ou à revenir au commit de retour, puis on recommence le cycle.

## Déploiement (je le fais moi-même)
- Le serveur est hébergé chez Minestator, accessible par SFTP et par console uniquement.
- C'est moi qui envoie le jar en SFTP. Tu ne déploies jamais, tu ne te connectes jamais au serveur, tu ne demandes jamais mes identifiants SFTP et tu ne les écris dans aucun fichier.
- À la fin d'une tâche prête à déployer, dis-moi :
  - quel fichier envoyer : `target/farmland-<version>.jar` (version lue dans le `pom.xml`), et surtout pas le fichier `original-*.jar` ;
  - si la config du serveur (`plugins/Farmland/config.yml`) doit changer, avec les clés exactes à ajouter ou modifier, sans valeur secrète ;
  - qu'un redémarrage du serveur est nécessaire pour charger le nouveau jar.
- Rappelle-moi de garder l'ancien jar avant de le remplacer sur le serveur (par exemple en le renommant), pour pouvoir revenir en arrière vite si ça casse en prod.
- Si un changement modifie le format des sauvegardes Gson, préviens-moi avant que je déploie : je dois sauvegarder les fichiers du serveur d'abord.

## Serveur de test (ton serveur à toi)
Un serveur Paper local, copie approximative du vrai serveur, sert à tester la course de bateaux, les plots, les modules, etc. Tu as tous les droits dessus : tu le démarres, l'arrêtes, le configures et le remets à zéro sans me demander.

- Dossier : `C:\farmland-test` (hors du repo, jamais versionné).
- Copie de référence propre : `C:\farmland-test-backup`. Pour remettre le serveur à zéro, restaure-la.
- Il contient la même version de Paper que la prod : 26.1.2 build 74 (le `pom.xml` vise 26.2 : ne le modifie pas sans me le dire), les mêmes plugins que la prod (PlaceholderAPI, Multiverse-Core, NuVotifier, RoseStacker, FAWE, LuckPerms...) et une copie de quelques mondes réels (plots, course de bateaux).
- **Si ce dossier n'existe pas ou est incomplet : dis-le-moi et n'invente aucun résultat de test.**

### Sécurité du serveur de test (à vérifier avant chaque démarrage)
- Dans `C:\farmland-test\plugins\Farmland\config.yml`, `webapi.enabled` doit être à `false`. Sinon le plugin enverrait des données de test au vrai site farm-land.fr. Corrige-le toi-même si besoin et préviens-moi.
- Webhooks Discord, `api_key` et tokens (dont DiscordSRV) restent vides ou absents. Ne les remplis jamais.
- Tu ne te connectes jamais à Minestator et tu n'as aucun accès SFTP.
- Reste dans `C:\farmland-test` et dans le repo : n'écris nulle part ailleurs.
- Tu peux écrire des fichiers de travail (liste de commandes, message de commit) dans ton dossier temporaire Claude Code, nulle part ailleurs.

### Messages attendus au démarrage (ce ne sont pas des bugs)
Ces lignes apparaissent à chaque démarrage du serveur de test. Elles viennent de la configuration volontairement vidée, pas de ton code :
- `⚠ Webhook Discord non configuré pour le salon "statut" (voir config.yml)`, précédée du préfixe `[Farmland]` dans les logs : les webhooks Discord sont vides dans le serveur de test. Elle apparaît plusieurs fois par démarrage (le nombre varie). Le même message existe pour le salon `"status"`, à l'activation et à l'arrêt.
- `[WebAPI] Module désactivé` : normal, `webapi.enabled` est à `false`. Si tu vois plutôt un module WebAPI actif, arrête-toi et préviens-moi.

Règles :
- Ignore uniquement ces lignes. Un message du même type pour un autre salon Discord est probablement normal aussi, mais cite-le-moi la première fois.
- Tout autre `WARN`, `ERROR` ou `Exception` doit m'être signalé, même s'il te semble lié à la config.
- Ne remplis jamais un webhook pour faire disparaître ces avertissements.

### Déroulé d'un test
1. `mvn clean package` puis `mvn test` (les deux doivent passer).
2. Copie `target/farmland-<version>.jar` (pas le fichier `original-*.jar`) dans `C:\farmland-test\plugins\`, en remplaçant l'ancien jar.
3. Démarre le serveur et attends le message de fin de démarrage.
4. Lis `logs/latest.log` : cherche `ERROR`, `Exception`, `NoClassDefFoundError`, et vérifie que Farmland est bien activé, avec ses modules.
5. Envoie des commandes console pour ce qui peut l'être, puis relis les logs.
6. Arrête le serveur proprement (`stop`) à la fin.
7. Donne-moi le résultat réel : ce qui est vérifié, et la liste de ce que je dois tester moi-même en jeu (je me connecte au serveur local).

### Ce que tu ne peux pas vérifier
Les clics dans les menus, les téléportations, la bordure, le scoreboard, les hologrammes et le déroulement réel de la course de bateaux demandent un joueur. Ne dis jamais que c'est "testé" : écris "à vérifier en jeu".

## Architecture
- `FarmlandMain` : charge les données joueurs (`PlayerSave`), puis `ModuleLoader.load()` enregistre les modules 20 ticks plus tard.
- `modules/` : un module par domaine (Moderation, Plot, Placeholder, Save, Game, SecureWorldEdit, WebApi, Metier), chacun avec `register()`. Toute nouvelle fonctionnalité passe par un module enregistré dans `ModuleLoader`.
- `directives/` : la logique par fonctionnalité (commandes, listeners, menus) : administration, bateau, cosmetic, gameplay, infrastructure, marché, metiers, plot.
- `doonees/` : classes de données et HashMaps en mémoire (`PlayerServer`, `PlotData`, `Market`, ...).
- `persistance/` : sauvegardes Gson (`FileManager`, `PlayerSave`, `MarketSave`, `RegionSave`, ...) et `webapi/WebApiClient`.
- Noms existants à respecter : dossiers avec accents (`marché`, `régions`), package `doonees`. Ne rien renommer sans demande.

## Marché par métier (MarketCalc, MarketCalcTask, Market)
- Prix de base : Farmeur 8, Mineur 15, Agriculteur 12, Pêcheur 10, Tueur 20.
- Prix actuel = base × coefficient / 100, minimum 0,01.
- Recalcul toutes les 30 min : plus de 20 ventes → coefficient −2 % ; moins de 5 ventes et coefficient < 100 → +2 % ; bornes 50 à 150.
- Après recalcul : ventes remises à zéro, sauvegarde, hologrammes, envoi au site si `webapi.enabled`.
- `ventesParMetier` est un état statique en mémoire (`HashMap` non thread-safe).

## Autres règles confirmées dans le code
- `/buy worldedit` : coût dans la constante `WE_COST` de `BuyCommands`, confirmation par un second appel. Le vote donne du temps WorldEdit gratuit.
- Upgrades de plot (`MenuPlotUpgrade`) : coûts 50 / 65 / 100 par blocs de 7, 21 upgrades par page, +20 par page supplémentaire.
- Permissions : `PermissionAttachment` + LuckPerms. `farmland.worldedit` ne doit pas être donnée au groupe joueur.
- `//wand`, `//pos1`, `//pos2`, `//hpos` restent autorisés (`WorldEditSecureListener`).

## Zones sensibles (demande-moi avant de modifier)
- Tout ce qui touche à l'argent : `PlayerServer.getMoney`, `/pay`, `/buy`, `RecompenseUtil`, `MultiplicateurUtil`, récompenses de bateaux.
- Permissions et WorldEdit sécurisé.
- Sauvegardes Gson et `CorrecteurJson` : pas de changement de format sans migration.
- `WebApiClient` et les endpoints `/api/...` du site.
- Téléportation des plots : utiliser `teleportSafe`.

## Règles de code
- Logs : `getLogger()`. Pas de `System.out`, pas de `printStackTrace()`, pas de logs de debug commités.
- Messages joueurs en français, via `MessageColor`.
- `config.yml` du repo : webhooks, `base_url` et `api_key` restent vides. Les vraies valeurs vivent dans `plugins/Farmland/config.yml` sur le serveur.
- Petits diffs, une tâche à la fois, pas de refactor non demandé.

## Dette connue (vérifiée)
- `printStackTrace()` dans 10 fichiers (17 appels). Les logs de debug et les `System.out` ont été retirés.
- La CI saute les tests (`-DskipTests`) : seuls les tests du marché existent, et ils ne tournent qu'en local.
- `dependency-reduced-pom.xml` versionné alors que `createDependencyReducedPom` est à false.
- Fichiers d'IDE versionnés (`.idea`, `.classpath`, `.project`, `.settings`) alors que `.gitignore` ne contient que `/target/`.
- `lib/` contient des jars versionnés (FAWE 2.14.0 alors que le pom utilise 2.14.3).
- Versions incohérentes : pom 1.1.0 / `plugin.yml` 1.0, et `api-version: 1.21.11` à confirmer par rapport à Paper 26.2.

## Source de vérité
- Le code de ce repo est la seule référence. Les anciens documents (inventaire avec `/define`, 5 coefficients, WorldEdit à 15 $FB) sont périmés : ne pas s'y fier.
- `/define` et le système de structures sont abandonnés (il ne reste qu'un commentaire dans `WorldEditSecureListener`). Ne pas les recréer.
- Règles d'économie hors marché (revenus des métiers, niveaux, récompenses) : lis le code, ne suppose rien.

## Mon niveau et ma logique de code
- Je dois comprendre chaque modification. Écris du code simple et lisible, dans le style existant du projet.
- Pas d'abstractions, de patterns ou de librairies que je ne connais pas, sauf si c'est vraiment nécessaire : dans ce cas explique pourquoi en 2 lignes, avec un exemple.
- Préfère des boucles, des `if` et des méthodes courtes et nommées clairement plutôt que du code compact ou "malin".
- Garde les conventions du projet : noms en français, dossiers accentués, `MessageColor`, `getLogger()`.
- Après chaque changement, explique en langage simple ce qui a changé et pourquoi.

## Tests obligatoires
- Toute logique de calcul, de prix ou d'argent doit venir avec un test JUnit.
- JUnit 5 est déjà dans le `pom.xml` : écris les nouveaux tests dans `src/test/java`, dans le même package que la classe testée (comme les tests du marché).
- Un changement n'est terminé que si `mvn clean package` et `mvn test` passent. Donne-moi le résultat réel, sans l'arrondir.

## Façon de travailler avec moi
Je veux une aide constante sur le code. Tu modifies, tu testes, tu commits si tout est vert.

1. Avant de modifier : lis le code concerné (ne devine jamais un nom de classe ou de méthode) et résume ton plan en quelques lignes.
2. Modifie le code et ajoute les tests nécessaires.
3. Lance `mvn clean package` puis `mvn test`.
3b. Si le serveur de test est prêt : fais le déroulé de la section "Serveur de test" (copie du jar, démarrage, lecture des logs).
4. Si tout passe : commit. Si quelque chose échoue : ne commite pas, corrige ou explique-moi le blocage.
5. Un commit par tâche, avec un message clair qui dit ce qui a changé.
6. À la fin de chaque tâche, dis-moi toujours :
   - le hash du commit de retour (le dernier commit AVANT ton travail), à restaurer en cas de problème ;
   - la commande pour revenir en arrière (`git revert` sur tes commits) ;
   - ce que je dois vérifier à la main en jeu, car tu ne peux pas tester sur le serveur.

### Limites
- Rien ne part vers l'extérieur sans mon accord explicite, donné dans le message de la tâche :
  - GitHub : pas de `git push`, pas de pull request. Un push déclenche la CI et les notifications Discord. Si je te demande de pousser, montre-moi d'abord `git status` et `git log` des commits concernés.
  - Serveur réel (Minestator) : tu n'y as aucun accès et tu n'y envoies rien. Je déploie moi-même.
  - Un accord donné pour une tâche ne vaut pas pour la suivante.
- Jamais de force-push, de `reset --hard` ni de réécriture d'historique, même si je te le demande à moitié : dans ce cas, demande-moi confirmation.
- Pour les zones sensibles (argent, permissions, sauvegardes Gson, `WebApiClient`) : explique le plan et attends ma validation avant de modifier.
- Ne modifie jamais `pom.xml`, `plugin.yml` ou les workflows sans me le dire d'abord.
- Si tu n'es pas sûr de quelque chose, dis-le plutôt que d'inventer.

## Tests du marché (fait)
- `MarketCalcTask.recalculer()` est en visibilité package (`static`, sans `private`) pour être testable. `MarketCalc.getPrixActuel()` est publique.
- Cas couverts : plus de 20 ventes, moins de 5 ventes à coefficient < 100, moins de 5 ventes à coefficient ≥ 100 (inchangé), entre 5 et 20 ventes, bornes 50 et 150, troncature des entiers.
- Si tu modifies le calcul du marché, mets ces tests à jour dans le même commit.

## Session de dev guidée
Quand je dis "on commence une session de dev" ou que je tape `/session`, lis `docs/PROTOCOLE-SESSION.md` et suis-le étape par étape. Tu conduis la session : tu poses les questions, tu proposes la suite et tu me dis où on en est.
Si je dis "rappelle-moi le guide", résume en 10 lignes le cycle de validation et le protocole, et indique-moi `docs/GUIDE-AGENT.md` pour le détail.

## Liste des tâches (`docs/TACHES.md`)
- C'est moi qui remplis ce fichier et qui fixe les priorités.
- Si je lance une session sans sujet, lis-le et propose-moi la tâche suivante.
- N'y ajoute, n'y réordonne et n'y supprime rien sans mon accord. Si tu repères une tâche ou une dette, propose-la-moi.