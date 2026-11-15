# Tâches Farmland

Liste remplie par le propriétaire du projet. Le dépôt est public : aucun secret ici.
L'ordre de la liste est la priorité : l'agent prend toujours la première tâche `à faire`.

Statuts : `à faire`, `en cours`, `attend validation`, `bloqué : <raison>`, `fait`.
L'agent ne change que le statut et les notes sous une tâche. Il n'ajoute, ne réordonne et ne supprime aucune tâche.

Modèle :

```
### <Titre court>
Statut : à faire
<Ce que je veux, en 1 à 3 lignes. Préciser si c'est une zone sensible.>
```

## À traiter

Tâches issues de l'audit complet du 2026-11-15 (version `b2fff2b`), classées par gravité. Détail : rapport d'audit du 15/11.

### [Critique] WorldEdit gratuit à vie après un redémarrage
Statut : à faire
`/buy worldedit` (et les votes) ajoutent les permissions WorldEdit dans LuckPerms (permanent) ; elles ne sont retirées que par une tâche planifiée, perdue au redémarrage. Confirmé sur le serveur de test : temps expiré + redémarrage → `farmland.worldedit` et `worldedit.region.*` restent à true. Retirer les permissions à l'expiration de façon fiable (au démarrage et à la connexion si le temps est écoulé). Zone sensible (permissions, WorldEdit sécurisé) : plan avant de modifier.

### [Critique] /bag : vente de jetons en cliquant dans son propre inventaire
Statut : à faire
Avec `/bag` ouvert, un clic dans l'inventaire du joueur (index 10, 12, 13, 14, 16) vend tout le stock d'un métier. Confirmé en jeu. Cause : `MenuListenerFarm` utilise `event.getSlot()` sans vérifier l'inventaire cliqué. Zone sensible (argent).

### [Critique] /buy cosmetic : achat en cliquant dans sa barre rapide
Statut : à faire
Avec `/buy cosmetic` ouvert, un clic dans la barre rapide (cases 0 à 6) achète le cosmétique correspondant. Confirmé en jeu (-300 $FB). Cause : `Plotinventory` (cosmétiques) utilise `event.getSlot()`. Vérifier aussi les autres menus qui utilisent `getSlot()`. Zone sensible (argent).

### [Critique] Pêcheur : aucun jeton donné
Statut : à faire
`EventPeche` tire le nombre de jetons sans jamais l'ajouter (`addJetonPecheur` n'est appelé nulle part), et la prise est annulée : le joueur n'a ni poisson ni jeton, seulement l'XP. Même bug que le Mineur. Vu dans le code (le bot ne sait pas pêcher) : à vérifier avec un vrai joueur. Zone sensible (économie).

### [Important] Droits des modérateurs sur l'argent et les plots
Statut : à faire
`/psadmin money` (créer de l'argent), `/marketadmin set` (prix du marché) et `/plotadmin reset` (efface un plot) n'exigent que `farmland.moderation`. Décider si ces actions doivent être réservées à `farmland.admin`. Zone sensible (permissions, `plugin.yml`).

### [Important] Œuf d'apparition utilisable dans le hub
Statut : à faire
Un œuf d'apparition fait apparaître un mob au hub (confirmé en jeu). Bloquer les œufs hors du plot du joueur.

### [Important] Sauvegardes des joueurs plus sûres
Statut : à faire
`FileManager.savefile` réécrit le fichier directement, avec l'encodage par défaut du système : un arrêt brutal pendant l'écriture peut vider ou corrompre le fichier d'un joueur. La sauvegarde à la déconnexion tourne en parallèle du serveur. Écrire dans un fichier temporaire puis le renommer, en UTF-8. Zone sensible (sauvegardes Gson).

### [Important] Chat : erreur possible si les données ne sont pas chargées
Statut : à faire
`ChatListener.onChat` plante (NullPointerException) si le joueur n'a pas de données, et appelle `kickPlayer` depuis le fil du chat (interdit par Paper).

### [Important] /buy worldedit : une ancienne confirmation annule la nouvelle
Statut : à faire
Le délai de 30 s d'une demande précédente affiche « Confirmation expirée » et efface la nouvelle demande (vu en jeu). Annuler l'ancien délai ou vérifier l'heure de la demande.

### [Important] Outils de métier illimités
Statut : à faire
`/peche`, `/epee`, `/hache`, `/pioche`, `/houe` donnent un nouvel outil en netherite à chaque appel. Décider d'une limite (un seul, ou délai).

### [Important] Bannissement vérifié trop tard
Statut : à faire
Le ban est vérifié dans `PlayerJoinEvent` (le banni entre une fraction de seconde) ; le message de kick affiche « &c » en clair. Vérifier à la connexion (login) et convertir les couleurs.

### [Moyen] Messages de modération illisibles
Statut : à faire
`ModerationCommands` concatène `MessageColor.RED + "..."` : le joueur voit « REDSeul un joueur… » (confirmé). `/warnf` affiche « &cAvertissement ». Utiliser `MessageColor.X.apply(...)`.

### [Moyen] /shop ne fait rien
Statut : à faire
La commande est déclarée dans `plugin.yml` mais aucune classe ne la gère. La coder ou la retirer.

### [Moyen] Prix WorldEdit faux dans les messages
Statut : à faire
`/vote` et le message de blocage WorldEdit annoncent 15 $FB, alors que `/buy worldedit` coûte 200 $FB ; `/vote` annonce « +15 $FB par vote » alors que le vote donne 1 h de WorldEdit.

### [Moyen] Course de bateaux : messages et sortie
Statut : à faire
Pas de message de départ après « Départ dans 1 seconde » ; quitter avec `/joinboat` laisse le joueur sur place ; premier message 10 s après la commande.

### [Moyen] Flèche perdue quand le tir est refusé
Statut : à faire
Dans le hub ou sur le plot d'un autre, le tir est refusé mais la flèche disparaît.

### [Moyen] Agriculteur : jetons pour n'importe quel animal
Statut : à faire
Tuer n'importe quelle vache avec la hache donne des jetons, pas seulement les mobs des spawners (le commentaire du code dit le contraire). Décider du comportement voulu.

### [Moyen] /msgf et /reportmsg sur soi-même
Statut : à faire
Les deux commandes acceptent le joueur lui-même comme cible. Les refuser.

### [Moyen] /tpa et plots privés
Statut : à faire
Après `/tpaccept`, le joueur arrive là où est la cible, même dans le plot privé d'un tiers, sans recherche de position sûre.

### [Moyen] Dégâts max des armes : 18 au lieu de 20
Statut : à faire
`ArmesUtil` : le commentaire annonce 20 dégâts au niveau 15, le code plafonne à 18 (niveau 13). Corriger le code ou le commentaire (à voir avec le développeur).

### [Mineur] Textes et cohérence
Statut : à faire
- Fautes : « Chaque poisson pêché avec ira… » (mot manquant), « Canne à Péche », « Veuillez Pechez grace a la canne a péche », « Hache D'Agriculteur », « épée du Tueur », « Le joueur Xn'est plus en ligne ! », « Tes metiers », « Pecheur ».
- Nom du message différent de l'objet : « Pioche du Mineur » / « Pioche ».
- `/plot config` « Temps qui passe » répond « La météo se fige / se met à bouger ».
- `/money` sans « $FB » ; messages « joueur introuvable » différents selon les commandes.
- Usage `/reportmsg` différent entre `plugin.yml` et le chat ; `setspawn` non documenté.
- `/plot home` téléporte au coin du bloc, `/plot visit` au centre.
- Farmeur : pas de message « +10 XP | +1 Jeton » à la récolte.
- Emojis de `/market` à vérifier dans un vrai client.
- Avant la prod : retirer les messages « DEBUG path=… » et `/testreward` (code du développeur).

### Menu plot visit plus stylisé
Statut : fait
Validé le 2026-11-08 : plan accepté, plots privés grisés, 45 par page. Fait comme prévu ; le titre a aussi été unifié dans `Plotcommands.java` (une chaîne).
Refaire le menu plot visit en mode plus stylisé.

Notes de l'agent (2026-11-08) : "plus stylisé" est ambigu, aucun code modifié.
Aujourd'hui (`MenuPlotVisit.createmenuplotvisit`) : 54 cases, une tête de joueur par plot (nom seul, sans couleur ni description), flèches en 45 et 53, titre "plotvisit | Page 1" puis "Visite | Page 2".
Plan proposé (fichiers : `MenuPlotVisit.java`, et `Plotinventory.java` seulement pour le titre) :
1. Titre unique et coloré, par exemple "Visiter un plot | Page X".
2. Têtes : nom coloré via `MessageColor`, avec une description (propriétaire, public ou privé, taille de la bordure).
3. Dernière ligne en vitre teintée décorative, flèches gardées en 45 et 53, numéro de page au milieu (slot 49).
4. Les plots privés restent affichés mais grisés, avec "Privé" dans la description.
Risque : faible, affichage seulement. Le clic sur une tête utilise `SkullMeta.getOwningPlayer` : à garder intact. Pas de test JUnit (pas de calcul). Tout est "à vérifier en jeu".
Questions :
- Ce plan te va-t-il ? Sinon, que veux-tu voir (couleurs, ordre de tri, autres infos sur les plots) ?
- Les plots privés : affichés grisés, ou cachés ?
- Combien de plots par page : 45 comme aujourd'hui, ou 28 avec un cadre en vitre tout autour ?

### Logs : printStackTrace remplacés par le logger
Statut : fait
Ancienne proposition 2. Fait dans `e6e5192` (branche `agent/journal-logs`, fusionnée) : plus aucun `printStackTrace()` dans `src/main`.

### Menu plot visit : pagination, titre, téléportation, plot privé du propriétaire
Statut : fait
Anciennes propositions 5 à 8. Branche `agent/menu-plot-visit` :
- `2e0ef42` : page tenue par le menu ouvert (repart de la page 1), un seul titre "Visiter un plot", clic sur une tête via `Plotcommands.visiterPlot` (même règle "privé" que la commande, `teleportSafe`), le propriétaire visite son plot privé ("Ton plot (privé)").
- `96989e5` : `teleportSafe` pose le joueur au sol sur les mondes de plot plats (avant : arrivée à y=65 puis chute).
À vérifier en jeu : flèches de page avec plus de 45 plots, arrivée sans dégâts en survie.

### Protections des plots indépendantes de RoseStacker
Statut : fait
Ancienne proposition 4. `c5da790` (branche `agent/rosestacker`) : sans RoseStacker, Paper refusait toute la classe `EventBuildAndUse` (casse/pose permises sur les plots des autres, prouvé sur le serveur de test). La règle d'empilement passe dans `RoseStackerListener`, enregistré seulement si RoseStacker est présent.
À vérifier en prod : pas de "Failed to register events ... EventBuildAndUse" au démarrage.

### onSpawnMob hors des mondes de plot
Statut : fait
Dans `EventBuildAndUse.onSpawnMob`, `UUID.fromString(world.getName())` lève une erreur dans un monde dont le nom n'est pas un UUID (ex. hub `world`) : vérifier si ça arrive (console), puis ignorer ces mondes sans erreur. Touche aux protections des plots : plan avant de modifier.

Notes de l'agent (2026-11-11) : bug prouvé sur le serveur de test (`/summon` dans le hub → "Invalid UUID string: world"). Corrigé dans `beed5e3` (branche `agent/onspawnmob`) : ces mondes sont ignorés sans erreur. Règle des plots inchangée ; la reproduction reste bloquée sur les plots (choix du propriétaire, à revoir plus tard peut-être).

### messagediscord : envoi bloquant et erreurs avalées
Statut : fait
`messagediscord.sendmessage` fait l'appel HTTP vers Discord sur le fil appelant (souvent le fil principal : connexion, `/pay`...), sans délai maximal : si Discord est lent, le serveur peut geler. Et `catch (Exception _) {}` cache toute erreur réseau. À faire : envoi en tâche asynchrone, délais de connexion et de lecture, erreurs signalées via `AlerteDiscord` (rappel horaire). Jamais d'URL de webhook dans le dépôt.

Notes de l'agent (2026-11-11) : fait dans `ed183f5` (branche `agent/messagediscord`). Envoi asynchrone, délais 5 s / 5 s (3 s pendant l'arrêt), erreurs en WARN avec le type d'erreur seulement (jamais l'URL), test `MessageDiscordTest`. Testé avec un faux Discord local muet : démarrage normal, `/spawn` en ~70 ms, arrêt de Farmland en 3 s.
À vérifier en prod : un message arrive dans chaque salon. Non traité : les `@everyone` écrits par les joueurs passent toujours.

### Outils : erreur "Unexpected value: BOW" avec un objet hors métier
Statut : fait
Erreur vue en prod : `Outils.CleOutils` levait une erreur pour tout objet enchanté, renommé ou usé qui n'est pas un outil de métier (clic droit, casse, kill, pêche).
Notes de l'agent (2026-11-11) : corrigé dans `9f6cbb7` (branche `agent/outils-non-metier`) : le type est comparé d'abord, `isOutils` répond "non" sans erreur. Sur le serveur de test : 7 erreurs avec l'ancien jar, 0 avec le nouveau.

### Houe du Farmeur : choisir et planter des graines
Statut : à faire
Le message de la houe annonce "clic droit dans le vide pour choisir une graine, clic droit sur de la terre labourée pour planter", mais `HarvestFarmEvent.onClicHoue` est vide : la fonction n'existe pas. La coder, ou retirer le message en attendant.

### Reproduction des animaux sur les plots
Statut : à faire
Aujourd'hui `onSpawnMob` bloque toute apparition non naturelle sur les plots (reproduction, œufs, spawners), même si "spawn des mobs" est activé. Réfléchir à autoriser la reproduction. Touche aux protections des plots : plan avant de modifier.

### Discord : bloquer les @everyone venant des joueurs
Statut : fait
Certains messages envoyés à Discord contiennent du texte des joueurs (pseudo, chat relayé dans `GameCommands`) : un joueur peut y glisser `@everyone`. Bloquer les mentions (`allowed_mentions`) sauf pour les messages voulus (ex. `JoinAndleaveEvent` ligne 75).

Notes de l'agent (2026-11-15) : fait dans `16ace8c` (branche `agent/discord-mentions`). Chaque message part avec `"allowed_mentions":{"parse":[]}` (le texte reste visible, personne n'est notifié) ; seule l'alerte de `JoinAndleaveEvent` garde `@everyone`. Serveur de test, faux Discord local : `/reportmsg` avec `@everyone @here <@&123> <@456>` envoyé avec `parse` vide. 3 tests JUnit. À vérifier en prod : un `/reportmsg` avec `@everyone` ne notifie personne. Repéré : espace manquante dans "Le joueur Xn'est plus en ligne !".

### Calcul automatique : résultats négatifs
Statut : fait
Le calcul automatique du chat propose parfois un résultat négatif (ex. "1 - 48"). Vérifier si c'est voulu ; sinon, ne proposer que des résultats positifs. Gain en $FB : zone sensible, test JUnit.

Notes de l'agent (2026-11-15) : fait dans `c6bde03` (branche `agent/calcul-chat`). Décision du propriétaire : résultats négatifs voulus, multiplications négatives ajoutées ("7 × (-8)"). Bug corrigé au passage : deux joueurs répondant en même temps pouvaient être payés tous les deux (chat traité en parallèle) ; vérification + fermeture atomiques, paiement de 75 $FB sur le fil principal. 6 tests JUnit (20 joueurs simultanés × 200 essais : un seul gagnant). Serveur de test : +75 $FB une seule fois, journal `gain_calcul` cohérent.

### /plot visit : skins des têtes redemandés à Mojang
Statut : fait
À chaque ouverture, les têtes du menu redemandent les skins à Mojang ; si Mojang est lent, la console affiche "Couldn't look up profile properties ... Read timed out" (WARN, sans gravité). Éviter de redemander les profils à chaque ouverture (cache).

Notes de l'agent (2026-11-15) : fait dans `9385ae9` (branche `agent/skins-plot-visit`). `CacheProfils` garde le profil avec skin : celui des joueurs connectés sans demande à Mojang, les autres demandés une fois en arrière-plan (6 h ; 30 min d'attente après un échec). Serveur de test : 7 têtes sur 7 avec skin dès la 2e ouverture, 0 "Couldn't look up profile properties", clic sur une tête inchangé.

### Bordure des plots appliquée après achat et commandes admin
Statut : fait
Demande du propriétaire : tester les bordures des plots et `/plot buy`, avec des logs de debug.
Notes de l'agent (2026-11-15) : fait dans `9ee5106` (branche `agent/bordure-plot`). Avant : l'achat d'un upgrade, `/plotadmin border` et `/plotadmin upgrade` ne changeaient que la bordure enregistrée, la vraie bordure ne bougeait qu'à la reconnexion. Maintenant `PlotBordure.appliquer` la règle tout de suite (prix et paiement inchangés). Garde-fou : bordure < 1 refusée. Menu "Agrandir le plot | Palier N", fautes du message corrigées, logs `[PlotBuy]` / `[Bordure]` si `debug: true`. Serveur de test : 160 -> 165 -> 170 sans reconnexion.

### Salons Discord `status` / `statut` : un seul nom
Statut : à faire
Ancienne proposition 1. `messagediscord` accepte les deux noms pour le même webhook (`webhook-status`) et le code utilise surtout `statut`. Garder un seul nom (peu urgent).

### Tests JUnit sur les récompenses et multiplicateurs
Statut : fait
Ancienne proposition 2. Tests sur d'autres calculs (récompenses, multiplicateurs) : zone sensible, plan et validation avant.

Notes de l'agent (2026-11-15) : fait dans `7ab050e` (branche `agent/tests-recompenses`), sans changer le code de production. 19 tests : `MultiplicateurUtil`, `ArmesUtil`, `RecompenseUtil.getXpNeeded`, `LevelRewardManager` + `rewards.yml` livré, prix `/plot buy`. Pour le développeur : dans `ArmesUtil`, le commentaire annonce un plafond de 20 dégâts au niveau 15, mais le code plafonne au niveau 13, soit 18 dégâts (le test vérifie 18). Non couverts (code privé ou joueur réel nécessaire) : `ajouterXp`, `giveRewards`.

### Mineur : plus de jetons donnés
Statut : fait
Problème signalé par le propriétaire : casser un minerai avec la pioche de mineur ne donnait plus de jetons.
Notes de l'agent (2026-11-15) : cause : depuis `ef13f09` (27/09), `RecompenseUtil.donnerRecompenseMineur` n'ajoutait plus les jetons, et `EventMineSpawn.onMine` tirait le nombre de jetons sans jamais l'ajouter. Corrigé dans `c121b08` (branche `agent/jetons-mineur`) : jeton ajouté dans `onMine` (comme le Farmeur), message "+10 XP | +N Jeton Mineur" remis ; tirage, XP et niveaux inchangés. Serveur de test : 3 minerais -> +3 jetons, +30 XP. Les jetons non reçus depuis le 27/09 ne peuvent pas être rendus automatiquement.

## Propositions de l'agent

À recopier dans "À traiter" si je suis d'accord.

(aucune pour l'instant)
