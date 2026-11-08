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

## Propositions de l'agent

À recopier dans "À traiter" si je suis d'accord.

1. Chercher l'origine des salons Discord `status` / `statut` (lecture seule).
2. Remplacer les `printStackTrace()` par le logger du plugin : plusieurs fichiers sensibles (sauvegardes, WebAPI, WorldEdit), validation fichier par fichier.
3. Tests sur d'autres calculs (récompenses, multiplicateurs) : zone sensible, avec validation.
4. Corriger la dépendance du listener de protection des plots à RoseStacker : zone sensible.
5. Menu plot visit : remettre la page à 1 à chaque ouverture de `/plot visit`. Aujourd'hui `playerPageById` (`Plotinventory`) garde l'ancienne page, et la flèche "suivante" peut sauter une page.
6. Menu plot visit : un seul titre pour toutes les pages ("plotvisit" dans `Plotcommands`, "Visite" dans `Plotinventory`).
7. Menu plot visit : téléporter avec `teleportSafe` (`Plotcommands`) au lieu de `teleportToPlot` et `findSafeLocation`, recopiées dans `Plotinventory`.
8. Menu plot visit : laisser le propriétaire visiter son propre plot privé depuis le menu, comme avec `/plot visit <nom>`.
