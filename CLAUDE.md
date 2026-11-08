# Farmland — plugin Paper (jeu de métiers)

Plugin Minecraft du serveur Farmland (mine.farm-land.fr), monnaie $FB. Chaque joueur a un plot (monde Multiverse, bordure améliorable) et 5 métiers (Mineur, Farmeur, Agriculteur, Pêcheur, Tueur) avec niveaux et XP. Il y a aussi un marché par métier, une course de bateaux, des cosmétiques, un sac et un vote. Le site farm-land.fr (autre repo) reçoit des données via `WebApiClient`.

Ce fichier contient les règles et passe avant tout autre document. La file de travail est `docs/TACHES.md`. Procédures détaillées : `docs/MODE-AUTONOME.md`, `docs/SERVEUR-TEST.md`. L'état du projet (dette, tests existants) est dans `docs/GUIDE-AGENT.md`.

## 1. Interdits
- Le repo est public : aucun secret (webhook, token, `api_key`, `base_url`, identifiants) dans le code, la config versionnée, un commit ou `docs/`. Dans le `config.yml` du repo, ces clés restent vides.
- Tu ne commites jamais sur `master`, même pour de la doc : toute modification passe par une branche `agent/<sujet>` (en mode autonome, `agent/AAAA-MM-JJ`).
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
Quand je dis "lance les tâches", "tourne" ou équivalent : lis d'abord `docs/MODE-AUTONOME.md` (déroulé et format du rapport) et suis-le. Ce qui suit passe avant ce fichier :
- Une tâche qui touche la section 2 ou qui est ambiguë : pas de code, seulement ton plan ou ta question sous la tâche, statut `attend validation`.
- Un commit par tâche, sur `agent/AAAA-MM-JJ` ; le hash de `master` au départ est le **commit de retour**.
- Arrête-toi quand il n'y a plus de tâche `à faire`, après 3 tâches `fait` (ou le nombre que je donne : "lance 5 tâches"), ou immédiatement si WebAPI est actif sur le serveur de test ou si tu trouves un secret.
- Premier lancement (aucune branche `agent/AAAA-MM-JJ` n'existe encore) : 2 tâches maximum, et dis-le-moi.

Dans `TACHES.md`, tu ne changes que le statut et les notes sous une tâche. Tu n'ajoutes, ne réordonnes et ne supprimes aucune tâche : tes idées vont dans la section "Propositions de l'agent".
Note : je règle moi-même les commandes autorisées avec `/permissions` (`mvn` et `git` en lecture et commit autorisés, `git push` jamais autorisé d'office). Tu ne modifies pas ces réglages.

## 4. Mode guidé : `/session`
Quand je tape `/session` ou dis "on commence une session de dev" : suis `docs/PROTOCOLE-SESSION.md` étape par étape, avec mon "ok" à chaque étape.
Si je dis "rappelle-moi le guide" : résume en 10 lignes le cycle de validation et le protocole, et indique `docs/GUIDE-AGENT.md`.

## 5. Vérification d'une tâche
1. `mvn clean package` : compile contre Paper 26.2 (pom) et lance les tests.
2. `mvn clean package -Dpaper.version=26.1.2.build.74-stable` : construit le jar pour le serveur de test et la prod (en 26.1.2). C'est ce jar qui reste dans `target/`.
3. Les deux finissent en `BUILD SUCCESS`. Recopie la ligne `Tests run: ...` exacte. 0 test alors qu'il en existe = échec.
4. Serveur de test (section 8 et `docs/SERVEUR-TEST.md`).
5. Ne modifie ni `src/` ni le `pom.xml` entre ce build et le commit : le jar doit correspondre exactement au commit.

## 6. Tests JUnit
- Obligatoires pour toute logique de calcul, de prix ou d'argent.
- Dans `src/test/java`, dans le même package que la classe testée (modèles : `MarketCalcTaskTest`, `MarketCalcTest`).
- Si tu modifies un calcul déjà testé, mets ses tests à jour dans le même commit.
- Pour rendre une méthode testable : retire `private` (visibilité package) plutôt que de restructurer.

## 7. Rapport de fin
Format complet dans `docs/MODE-AUTONOME.md`. Toujours : commit de retour, hash de chaque commit avec son `git revert`, hash du commit d'où vient le jar, liste "à vérifier en jeu" par tâche, et décisions prises seul.
Pour déployer : `target/farmland-<version>.jar` (jamais `original-*.jar`), construit contre 26.1.2 ; garder l'ancien jar ; redémarrage nécessaire. Si le format Gson change : **sauvegarder `plugins/Farmland/` avant de déployer**.

Rappel du cycle : je teste en jeu, je déploie en SFTP, je teste en prod, puis je pousse (ou je te le demande). Si je dis que ça casse en prod : ne pousse rien, aide-moi à corriger ou à revenir au commit de retour.

## 8. Serveur de test `C:\farmland-test`
Tu as tous les droits dessus, sans me demander : démarrer, arrêter, configurer, remettre à zéro. Déroulé et messages normaux : `docs/SERVEUR-TEST.md`.
Si le dossier manque ou est incomplet : dis-le, écris "serveur de test : non fait" et n'invente aucun résultat.

Avant chaque démarrage :
- `webapi.enabled: false` dans `plugins\Farmland\config.yml`. Sinon corrige-le et préviens-moi.
- Webhooks Discord, `api_key` et tokens (dont DiscordSRV) vides ou absents. Ne les remplis jamais, même pour faire taire un avertissement.
- Si WebAPI apparaît actif dans les logs : arrête le serveur, arrête la boucle et préviens-moi.
- Tout `WARN`, `ERROR` ou `Exception` absent de la liste des messages normaux doit m'être signalé.

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
- Le pom vise Paper `26.2.build.87-stable` et reste ainsi (ne rétrograde ni Java ni Paper). Prod et serveur de test : 26.1.2 build 74.
- Les règles du marché sont dans `MarketCalc`, `MarketCalcTask` et leurs tests : ce sont eux qui font foi.
- WorldEdit : `farmland.worldedit` n'est jamais donnée au groupe joueur. `//wand`, `//pos1`, `//pos2`, `//hpos` restent autorisés.
- Le code est la seule référence. Les anciens documents (`/define`, 5 coefficients, WorldEdit à 15 $FB) sont périmés. `/define` et les structures sont abandonnés : ne pas les recréer. Pour l'économie hors marché, lis le code.
