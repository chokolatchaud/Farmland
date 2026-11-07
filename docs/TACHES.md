# Tâches Farmland

Fichier de travail de l'agent. Le dépôt est public : aucun secret ici.

## Règles
- Seul le propriétaire du projet change les priorités. L'agent peut proposer, jamais réordonner ni supprimer.
- Une tâche = une session = une branche `agent/<sujet>`.
- À la clôture, l'agent met à jour le statut de la tâche dans le commit de la session (fait, ou bloqué avec la raison).
- Une dette repérée en cours de route va dans « À trier », pas dans la liste priorisée.
- Tâche sensible (argent, sauvegardes Gson, permissions, `WebApiClient`, pom/plugin.yml/workflows) : validation fichier par fichier, quelle que soit la priorité.

Statuts : `à faire`, `en cours`, `fait`, `bloqué`.
Priorité : 1 = en premier, 3 = confort.

## Priorité 1

### T1. Mettre à jour CLAUDE.md
- Statut : à faire. Sensible : non.
- Contexte : les sections Build et Dette disent « Aucun test » alors qu'il y a 22 tests JUnit. Ajouter le renvoi vers `docs/TACHES.md`. Ajouter la règle « ne jamais afficher les lignes de commande des processus ».
- Fin : plus aucune mention fausse sur les tests, renvoi vers ce fichier présent.

### T2. Aligner la version de plugin.yml
- Statut : à faire. Sensible : oui (plugin.yml, accord requis).
- Contexte : `plugin.yml` indique 1.0, `pom.xml` indique 1.1.0. Confirmer aussi `api-version`.
- Fin : versions cohérentes, plugin chargé sans erreur sur le serveur de test.

### T3. Brancher /session sur ce fichier
- Statut : à faire. Sensible : non.
- Contexte : à l'étape 2 de `/session`, lire `docs/TACHES.md` et proposer la tâche non faite de plus haute priorité. Mettre à jour `docs/PROTOCOLE-SESSION.md` et `.claude/commands/session.md`.
- Fin : `/session` sans sujet propose une tâche issue de ce fichier.

## Priorité 2

### T4. Lister les printStackTrace() (lecture seule)
- Statut : à faire. Sensible : non (aucune modification).
- Contexte : 17 appels dans 10 fichiers. Pour chacun : fichier, ligne, ce que fait le `catch`, sensible ou non.
- Fin : tableau remis au propriétaire, qui décide des lots.

### T5. Remplacer les printStackTrace() par lots
- Statut : à faire (après T4). Sensible : selon le lot.
- Contexte : remplacer par `plugin.getLogger().log(Level.SEVERE, "message clair", e)`. Un lot = une branche. Fichiers anodins d'abord ; économie, sauvegardes et `WebApiClient` un par un.
- Fin : plus de `printStackTrace()`, comportement identique, tests verts.

### T6. Enquêter sur les salons Discord `status` / `statut`
- Statut : à faire. Sensible : non (lecture seule).
- Contexte : incohérence possible entre les deux noms. Trouver d'où vient chaque nom dans le code et la config.
- Fin : rapport de ce qui est utilisé où, sans rien modifier.

### T7. Rendre le listener de protection indépendant de RoseStacker
- Statut : à faire. Sensible : oui (protection des plots).
- Contexte : sans RoseStacker, `EventBuildAndUse` ne se charge pas. Le listener ne doit pas disparaître si RoseStacker est absent.
- Fin : protection active avec et sans RoseStacker, testée en jeu sur le serveur de test.

## Priorité 3

### T8. Tests sur d'autres calculs
- Statut : à faire. Sensible : oui si argent ou récompenses.
- Contexte : récompenses, multiplicateurs. Ajouter des tests sans changer la logique.
- Fin : nouveaux tests verts, logique inchangée.

### T9. Faire tourner les tests dans la CI
- Statut : à faire. Sensible : oui (workflows).
- Contexte : `build.yml` utilise `-DskipTests`. Décider avec le propriétaire avant de toucher.
- Fin : la CI exécute les tests et reste verte.

### T10. API dépréciée dans ModerationCommands.java
- Statut : à faire. Sensible : oui (permissions, modération).
- Fin : plus d'avertissement de dépréciation, comportement identique.

### T11. Nettoyage du dépôt
- Statut : à faire. Sensible : non.
- Contexte : `dependency-reduced-pom.xml`, fichiers d'IDE et `lib/` versionnés. Proposer un `.gitignore` adapté avant de retirer quoi que ce soit.
- Fin : dépôt propre, build identique.

### T12. Décider de la version Paper du pom
- Statut : bloqué (décision du propriétaire).
- Contexte : prod en Paper 26.1.2 build 74, `pom.xml` compile contre 26.2.build.87. Aujourd'hui on construit avec `-Dpaper.version=26.1.2.build.74-stable`.
- Fin : version du pom alignée sur la prod, ou décision documentée.

## À trier
- Mettre à jour les plugins du serveur (FAWE 2.15.3 côté serveur contre 2.14.3 dans le pom, FancyNpcs). Hors code : à faire par le propriétaire.
- Variable `Market m` inutilisée dans `MarketSave.displayHistory`.
- `messagediscord` avale certaines exceptions.

## Fait
- Tests JUnit sur le calcul du marché (22 tests).
- Retrait des logs `[DEBUG-TP]` et des `System.out` (commit ca06da3).
- Guide agent et protocole de session (commit e8bc295).
