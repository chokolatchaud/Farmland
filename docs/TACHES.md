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
Statut : à faire
Refaire le menu plot visit en mode plus stylisé.

## Propositions de l'agent

À recopier dans "À traiter" si je suis d'accord.

1. Chercher l'origine des salons Discord `status` / `statut` (lecture seule).
2. Remplacer les `printStackTrace()` par le logger du plugin : plusieurs fichiers sensibles (sauvegardes, WebAPI, WorldEdit), validation fichier par fichier.
3. Tests sur d'autres calculs (récompenses, multiplicateurs) : zone sensible, avec validation.
4. Corriger la dépendance du listener de protection des plots à RoseStacker : zone sensible.
