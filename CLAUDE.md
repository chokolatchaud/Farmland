# Farmland — plugin Paper (jeu de métiers)

Plugin Minecraft du serveur Farmland (mine.farm-land.fr), monnaie $FB. Chaque joueur a un plot (monde Multiverse, bordure améliorable) et 5 métiers (Mineur, Farmeur, Agriculteur, Pêcheur, Tueur) avec niveaux et XP. Il y a aussi un marché par métier, une course de bateaux, des cosmétiques, un sac et un vote. Le site farm-land.fr (autre repo) reçoit des données via `WebApiClient`.

Ce fichier contient les règles. La file de travail est `docs/TACHES.md`. L'état du projet (dette, tests existants) est dans `docs/GUIDE-AGENT.md`.

## 1. Interdits
- Le repo est public : aucun secret (webhook, token, `api_key`, `base_url`, identifiants) dans le code, la config versionnée, un commit ou `docs/`. Dans le `config.yml` du repo, ces clés restent vides.
- Pas de `git push`, de pull request, de `git fetch` ni d'autre commande réseau, sauf si je le demande dans le message en cours. Pour un push demandé : montre `git status` et `git log --oneline` des commits concernés, puis attends mon "ok" (un push déclenche la CI et Discord).
- Jamais de force-push, `reset --hard`, rebase, `commit --amend` ni suppression de branche. Si une de mes demandes en implique un, nomme la commande et attends ma confirmation.
- Prod (Minestator) : aucun accès. Tu ne t'y connectes pas, tu ne demandes pas mes identifiants SFTP et tu ne les écris nulle part. Je déploie moi-même.
- Tu n'écris que dans le repo, dans `C:\farmland-test` et dans ton dossier temporaire Claude Code.
- Une autorisation vaut pour la tâche où je la donne, pas pour la suivante.

## 2. Ce qui demande mon "ok" avant de modifier
Écris le plan (fichiers, changement, risque) et attends mon accord :
- Argent : `PlayerServer.getMoney`, `/pay`, `/buy`, `RecompenseUtil`, `MultiplicateurUtil`, récompenses de bateaux.
- Valeurs d'équilibrage de l'économie (prix, coûts, gains, seuils).
- Permissions (`PermissionAttachment`, LuckPerms) et WorldEdit sécurisé (`WorldEditSecureListener`, `BuyCommands`).
- Format des sauvegardes Gson et `CorrecteurJson` : tout changement de format vient avec une migration.
- `WebApiClient` et les endpoints `/api/...` du site.
- `pom.xml`, `plugin.yml`, `.github/workflows/`.
- Renommer un fichier, dossier, package ou classe.

## 3. Mode autonome : dérouler `docs/TACHES.md`
Quand je dis "lance les tâches", "tourne" ou équivalent :
1. Check-in, sans rien modifier : `git status` propre, branche actuelle, `webapi.enabled: false` sur le serveur de test, serveur arrêté. Si quelque chose ne va pas, arrête-toi et dis-le.
2. Crée la branche `agent/AAAA-MM-JJ` depuis `master` (ou reprends-la si elle existe). Note le hash de `master` : c'est le **commit de retour**.
3. Prends la première tâche `à faire` de `docs/TACHES.md` (l'ordre du fichier est la priorité) et passe-la en `en cours`.
4. Si elle touche la section 2 ou si elle est ambiguë : ne touche pas au code. Écris sous la tâche ton plan ou ta question, passe-la en `attend validation` et prends la suivante.
5. Sinon : lis le code concerné, modifie, ajoute les tests (section 6), vérifie (section 5).
6. Tout est vert : passe la tâche en `fait` et fais **un commit pour cette tâche** (code, tests et `TACHES.md` ensemble).
7. Échec après 2 tentatives de correction : `git stash push -m "bloqué: <tâche>"`, passe la tâche en `bloqué : <raison en une ligne>` et prends la suivante.
8. Arrête-toi quand il n'y a plus de tâche `à faire`, après 5 tâches `fait`, ou immédiatement si WebAPI est actif sur le serveur de test ou si tu trouves un secret. Termine par le rapport (section 7).

Dans `TACHES.md`, tu ne changes que le statut et les notes sous une tâche. Tu n'ajoutes, ne réordonnes et ne supprimes aucune tâche : tes idées vont dans la section "Propositions de l'agent".

## 4. Mode guidé : `/session`
Quand je tape `/session` ou dis "on commence une session de dev" : suis `docs/PROTOCOLE-SESSION.md` étape par étape, avec mon "ok" à chaque étape.
Si je dis "rappelle-moi le guide" : résume en 10 lignes le cycle de validation et le protocole, et indique `docs/GUIDE-AGENT.md`.

## 5. Vérification d'une tâche
1. `mvn clean package` : compile contre Paper 26.2 (pom) et lance les tests.
2. `mvn clean package -Dpaper.version=26.1.2.build.74-stable` : construit le jar pour le serveur de test et la prod (en 26.1.2). C'est ce jar qui reste dans `target/`.
3. Les deux finissent en `BUILD SUCCESS`. Recopie la ligne `Tests run: ...` exacte. 0 test alors qu'il en existe = échec.
4. Serveur de test (section 8).
5. Ne modifie ni `src/` ni le `pom.xml` entre ce build et le commit : le jar doit correspondre exactement au commit.

## 6. Tests JUnit
- Obligatoires pour toute logique de calcul, de prix ou d'argent.
- Dans `src/test/java`, dans le même package que la classe testée (modèles : `MarketCalcTaskTest`, `MarketCalcTest`).
- Si tu modifies un calcul déjà testé, mets ses tests à jour dans le même commit.
- Pour rendre une méthode testable : retire `private` (visibilité package) plutôt que de restructurer.

## 7. Rapport de fin
Dans cet ordre :
- **Tâches** : pour chacune, son statut et ce qui a changé en 1 ou 2 lignes simples.
- **Résultats réels** : `BUILD SUCCESS` ou non, ligne `Tests run`, ce que le serveur de test a montré.
- **Commits** : commit de retour, hash de chaque commit, hash du commit d'où vient le jar, branche, et `git revert <hash>` à lancer du plus récent au plus ancien.
- **Pour déployer** : `target/farmland-<version>.jar` (version du pom, jamais `original-*.jar`), construit contre 26.1.2 ; clés de `plugins/Farmland/config.yml` à ajouter ou modifier (sans valeur secrète) ; garder l'ancien jar en le renommant ; redémarrage nécessaire. Si le format Gson change : **sauvegarder `plugins/Farmland/` avant de déployer**.
- **À vérifier en jeu** : liste précise, avec les commandes.
- **Pour toi** : les tâches `attend validation` et `bloqué`, avec ce que j'attends de toi.
- **Surprises** : ce que tu as vu d'anormal, sans l'avoir corrigé.

Rappel du cycle : je teste en jeu, je déploie en SFTP, je teste en prod, puis je pousse (ou je te le demande). Si je dis que ça casse en prod : ne pousse rien, aide-moi à corriger ou à revenir au commit de retour.

## 8. Serveur de test `C:\farmland-test`
Tu as tous les droits dessus, sans me demander : démarrer, arrêter, configurer, remettre à zéro (supprimer puis recopier `C:\farmland-test-backup`). Il tourne en Paper 26.1.2 build 74 comme la prod, avec les plugins de la prod et une copie de mondes réels (plots, course `circuitalbert`).
Si le dossier manque ou est incomplet : dis-le, écris "serveur de test : non fait" et n'invente aucun résultat.

Avant chaque démarrage :
- `webapi.enabled: false` dans `plugins\Farmland\config.yml`. Sinon corrige-le et préviens-moi.
- Webhooks Discord, `api_key` et tokens (dont DiscordSRV) vides ou absents. Ne les remplis jamais.

Déroulé :
1. Copie `target/farmland-<version>.jar` dans `C:\farmland-test\plugins\` en remplaçant l'ancien.
2. Démarre et attends la ligne `Done (`.
3. Dans `logs/latest.log` : Farmland activé avec ses modules ; cherche `WARN`, `ERROR`, `Exception`, `NoClassDefFoundError`.
4. Lance les commandes console utiles à la tâche, puis relis le log.
5. `stop`, et vérifie que le processus est terminé.

Messages normaux au démarrage (config vidée exprès) :
- `[Farmland] ⚠ Webhook Discord non configuré pour le salon "statut"` (ou `"status"`), plusieurs fois.
- `[WebAPI] Module désactivé`. Si WebAPI est actif : arrête le serveur, arrête la boucle et préviens-moi.

Le même message pour un autre salon est probablement normal : cite-le la première fois. Tout autre `WARN`, `ERROR` ou `Exception` doit m'être signalé.
Sans joueur, tu ne peux pas vérifier les menus, téléportations, la bordure, le scoreboard, les hologrammes ni la course : écris "à vérifier en jeu", jamais "testé".

## 9. Règles de code
- Lis le code avant de le modifier. Ne devine jamais un nom de classe ou de méthode.
- Code simple dans le style existant : boucles, `if`, méthodes courtes bien nommées. Pas d'abstraction, de pattern ni de librairie nouvelle, sauf nécessité : explique alors pourquoi en 2 lignes avec un exemple.
- Noms en français. Ne renomme rien : dossiers accentués (`marché`, `régions`), package `doonees`.
- Messages joueurs en français via `MessageColor`. Logs via `getLogger()` : pas de `System.out`, de `printStackTrace()` ni de log de debug.
- Toute nouvelle fonctionnalité passe par un module enregistré dans `ModuleLoader`.
- Téléportation vers un plot : `teleportSafe` (`Plotcommands`).
- Petits diffs, pas de refactor hors tâche. Après chaque tâche, explique en langage simple ce qui a changé et pourquoi.
- En cas de doute, dis-le plutôt que d'inventer.

## 10. Repères techniques
- Java 25, Maven, package `fr.kevyn.farmland`. Le pom vise Paper `26.2.build.87-stable` et reste ainsi (ne rétrograde ni Java ni Paper). Prod et serveur de test : 26.1.2 build 74.
- `plugin.yml` : `depend` PlaceholderAPI, Multiverse-Core ; `softdepend` Votifier, NuVotifier, RoseStacker. `provided` : FAWE, LuckPerms API, VaultAPI. OkHttp 4.12.0 est embarqué par shade dans `farmland-<version>.jar` (`original-*` ne l'a pas).
- CI `build.yml` : `mvn -B clean package -DskipTests` (tests pas lancés en CI). `discord-notify.yml` envoie les commits poussés sur Discord.
- Démarrage : `FarmlandMain` charge `PlayerSave`, puis `ModuleLoader.load()` enregistre les modules 20 ticks plus tard.
- Dossiers : `modules/` (un module par domaine, avec `register()`), `directives/` (commandes, listeners, menus), `doonees/` (données en mémoire), `persistance/` (Gson et `webapi/WebApiClient`).
- Marché (`MarketCalc`, `MarketCalcTask`, `Market`) :
  - prix de base Farmeur 8, Mineur 15, Agriculteur 12, Pecheur 10, Tueur 20 ; prix actuel = base × coefficient / 100, minimum 0,01 ;
  - toutes les 30 min, `recalculer()` : plus de 20 ventes → coefficient − 2 % ; moins de 5 ventes et coefficient < 100 → + 2 % ; calcul en entiers (tronqué), bornes 50 à 150 ;
  - ensuite : annonce de l'événement du marché, remise à zéro des ventes, sauvegarde, hologrammes, envoi au site si `webapi.enabled` ;
  - `ventesParMetier` est une `HashMap` statique non thread-safe.
- WorldEdit : `/buy worldedit` coûte `WE_COST` (`BuyCommands`), confirmé par un second appel ; le vote donne du temps gratuit. `farmland.worldedit` n'est jamais donnée au groupe joueur. `//wand`, `//pos1`, `//pos2`, `//hpos` restent autorisés.
- Upgrades de plot (`MenuPlotUpgrade`) : 50 / 65 / 100 par blocs de 7, 21 upgrades par page, +20 par page supplémentaire.
- Le code est la seule référence. Les anciens documents (`/define`, 5 coefficients, WorldEdit à 15 $FB) sont périmés. `/define` et les structures sont abandonnés : ne pas les recréer. Pour l'économie hors marché, lis le code.
