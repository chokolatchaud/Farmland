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
Statut : à faire
Certains messages envoyés à Discord contiennent du texte des joueurs (pseudo, chat relayé dans `GameCommands`) : un joueur peut y glisser `@everyone`. Bloquer les mentions (`allowed_mentions`) sauf pour les messages voulus (ex. `JoinAndleaveEvent` ligne 75).

### Calcul automatique : résultats négatifs
Statut : à faire
Le calcul automatique du chat propose parfois un résultat négatif (ex. "1 - 48"). Vérifier si c'est voulu ; sinon, ne proposer que des résultats positifs. Gain en $FB : zone sensible, test JUnit.

### /plot visit : skins des têtes redemandés à Mojang
Statut : à faire
À chaque ouverture, les têtes du menu redemandent les skins à Mojang ; si Mojang est lent, la console affiche "Couldn't look up profile properties ... Read timed out" (WARN, sans gravité). Éviter de redemander les profils à chaque ouverture (cache).

### Salons Discord `status` / `statut` : un seul nom
Statut : à faire
Ancienne proposition 1. `messagediscord` accepte les deux noms pour le même webhook (`webhook-status`) et le code utilise surtout `statut`. Garder un seul nom (peu urgent).

### Tests JUnit sur les récompenses et multiplicateurs
Statut : à faire
Ancienne proposition 2. Tests sur d'autres calculs (récompenses, multiplicateurs) : zone sensible, plan et validation avant.

## Propositions de l'agent

À recopier dans "À traiter" si je suis d'accord.

(aucune pour l'instant)
