# Guide : travailler avec l'agent sur Farmland

Mis en place le 7 novembre 2026. Ce guide est pour moi. L'agent a son propre protocole dans `.claude/commands/session.md` et ses règles dans `CLAUDE.md`.

## 1. Démarrer une session

1. Ouvrir PowerShell :
   ```
   cd C:\Users\FlowUP\Desktop\WorkSpace\FarmLand
   claude
   ```
2. Donner l'accès au serveur de test : `/add-dir C:\farmland-test`
3. Vérifier le mode (ligne du bas) : doit être `manual mode`. `Shift+Tab` pour changer.
4. Lancer le guide : `/session` ou `/session <sujet>`, ou dire "on commence une session de dev".

L'agent conduit alors la session en 7 étapes : check-in, objectif, branche, plan, exécution, vérification, clôture.

## 2. Ce qu'est l'agent, et ce qu'il n'est pas

Un agent est Claude dans une boucle : il lit, agit avec des outils, observe le résultat et décide de la suite. Il lit mon code, modifie des fichiers, lance `mvn`, lit les erreurs.

Il n'a aucune mémoire entre deux sessions. Sa mémoire, c'est `CLAUDE.md` et le code. Il peut se tromper avec assurance : je relis les diffs et je contrôle les résultats moi-même.

Il ne peut pas : jouer au jeu (menus, téléportations, course de bateaux), se connecter à Minestator, pousser sur GitHub sans mon accord, décider de l'équilibrage de l'économie.

## 3. Le cycle de validation (toujours dans cet ordre)

1. L'agent modifie, compile, lance les tests, teste sur le serveur de test, commite en local.
2. Je teste en jeu sur le serveur de test.
3. Je déploie le jar en SFTP sur le vrai serveur et je teste là-bas.
4. Si tout est bon, je fusionne dans `master` et je pousse. Le push est la dernière étape.

## 4. Mes tests en jeu (serveur de test)

Lancer le serveur de test, se connecter avec un client 26.1.2.

- Connexion, scoreboard, `/plot home`, bordure, menu d'upgrade.
- Protection des plots : casser et poser chez un autre joueur, TNT, seaux, bateaux, cadres.
- Métiers : `/pioche`, `/houe`, `/peche`, `/epee`, `/hache`, `/metiers`, `/bag`, `/shop`, XP et argent.
- Course : `/joinboat` sur `circuitalbert`.
- WorldEdit : `//wand` fonctionne. Sans être opérateur, `//set stone` est refusé avant `/buy worldedit` et accepté après (200 $FB, 1 heure).

Pour tester en joueur normal : `deop MonPseudo` dans la console du serveur de test.

## 5. Déployer sur le vrai serveur

1. Prévenir les joueurs d'un redémarrage.
2. Sauvegarder l'ancien jar (le renommer, par exemple `farmland-AVANT.jar`) et le dossier `plugins/Farmland/`.
3. Le jar à envoyer : `target\farmland-1.1.0.jar`, jamais `original-farmland-1.1.0.jar`.
4. Le construire pour la version de la prod, tant qu'elle est en Paper 26.1.2 :
   ```
   mvn clean package -Dpaper.version=26.1.2.build.74-stable
   ```
5. Envoyer en SFTP dans `plugins/`, redémarrer, lire les logs : pas d'`ERROR` ni d'`Exception`.
6. En prod, le module WebAPI doit être actif (il est désactivé sur le serveur de test).

## 6. Revenir en arrière

| Problème | Solution |
|---|---|
| Le jar casse la prod | Arrêter, remettre l'ancien jar, redémarrer |
| Un commit local est mauvais | `git revert <hash>` (annule sans effacer l'historique) |
| La branche part mal | `git checkout master`, la branche peut être abandonnée |
| Le serveur de test est abîmé | Arrêter, supprimer `C:\farmland-test`, recopier `C:\farmland-test-backup` |

Ne jamais utiliser `git reset --hard` ni de force-push sans bien comprendre. L'agent n'y est pas autorisé.

## 7. Fusionner et pousser (après validation en prod)

```
git status
git checkout master
git merge agent/<sujet>
git log --oneline -6
git push origin master
```

Le push déclenche la CI et la notification Discord. Le dépôt est public : aucun secret dans le code, la config ou un commit.

## 8. Quand quelque chose tourne mal

- Je ne crois pas l'agent sur parole : je vérifie avec `git show --stat <hash>` et `mvn test`.
- Si le texte du terminal est tronqué, demander à l'agent d'écrire son rapport dans un fichier.
- Si l'agent refait la même erreur, je mets à jour `CLAUDE.md`.
- Si une demande de permission me paraît bizarre, je réponds non et je demande pourquoi.

## 9. Vocabulaire

- **Commit** : un point de sauvegarde dans l'historique Git.
- **Branche** : une ligne de travail isolée, créée à partir de `master`.
- **Merge fast-forward** : `master` avance simplement jusqu'au dernier commit de la branche.
- **Revert** : un nouveau commit qui annule un ancien.
- **Push** : envoyer mes commits sur GitHub.
- **Jar shaded** : le jar qui embarque OkHttp. C'est `farmland-1.1.0.jar`, pas `original-*`.

## 10. État du projet constaté (7 novembre 2026)

- Prod : Paper 26.1.2 build 74. Le `pom.xml` compile contre 26.2.build.87. Le projet compile aussi contre 26.1.2.
- 22 tests JUnit (marché), mais la CI les saute (`-DskipTests`).
- Sans RoseStacker, le listener de protection des plots ne se charge pas : à garder en tête, à corriger plus tard avec validation.
- Dette connue :
  - 4 logs `[DEBUG-TP]` dans `Plotinventory.java` ;
  - `System.out` dans `MarketSave`, `ConfigStartEndZone`, `boatgame`, `messagediscord`, et `printStackTrace()` dans 10 fichiers ;
  - noms de salon Discord `statut` et `status` à vérifier ;
  - API dépréciée dans `ModerationCommands.java` ;
  - `dependency-reduced-pom.xml`, fichiers d'IDE et `lib/` versionnés ;
  - version `pom.xml` 1.1.0 contre `plugin.yml` 1.0, et `api-version` à confirmer.
- Plugins du serveur à mettre à jour à l'occasion : FAWE, FancyNpcs.

## 11. Prochaines tâches possibles (par ordre de risque)

1. Chercher l'origine de `status` / `statut` (lecture seule).
2. Retirer les `[DEBUG-TP]`.
3. Remplacer les `System.out` par le logger du plugin.
4. Tests sur d'autres calculs (récompenses, multiplicateurs), zone sensible : avec validation.
5. Corriger la dépendance du listener de protection à RoseStacker : zone sensible.
