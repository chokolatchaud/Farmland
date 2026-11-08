---
description: Session de dev guidée pour Farmland (check-in, objectif, plan, exécution, vérification, clôture)
---

Je commence une session de dev. Tu la conduis : tu poses les questions, tu proposes l'étape suivante et tu me dis toujours où on en est ("Étape X sur 7").

Sujet éventuel donné avec la commande : $ARGUMENTS

Respecte mon CLAUDE.md en permanence (mon niveau, tes limites, zones sensibles, pas de push, pas de déploiement). Ce fichier précise seulement l'ordre des étapes.

## Étape 1 sur 7 : check-in (lecture seule)
Vérifie sans rien modifier, puis résume en 6 lignes maximum :
- branche actuelle, `git status`, les 5 derniers commits (`git log --oneline -5`), et s'il y a des commits non poussés ;
- `java -version` et `mvn -v` fonctionnent ;
- le serveur de test : `C:\farmland-test` existe, `C:\farmland-test-backup` existe, `webapi.enabled` est à `false` dans sa config, et le serveur est arrêté ;
- le dernier jar de `target/` (date) et pour quelle version de Paper il a été construit, si tu peux le savoir.
Ne fais pas de `git fetch` ni de commande réseau sans me le demander. Si quelque chose manque ou est incohérent, dis-le-moi avant d'aller plus loin.

## Étape 2 sur 7 : objectif
- Si j'ai donné un sujet, reformule-le en une phrase. Sinon, demande-moi ce que je veux faire.
- Pose 1 à 3 questions de clarification si la réponse en dépend.
- Dis-moi si la tâche touche une zone sensible (argent, permissions, sauvegardes Gson, `WebApiClient`, pom/plugin.yml/workflows). Si oui, je dois valider chaque modification.
- Dis-moi si elle demande des tests et lesquels.

## Étape 3 sur 7 : branche
Propose un nom de branche (`agent/<sujet-court>`) à créer depuis `master`. Attends mon accord avant de la créer.

## Étape 4 sur 7 : plan
Lis le code concerné (ne devine jamais un nom). Donne un plan en 5 à 10 lignes : fichiers touchés, ce qui change, tests prévus, risques. Explique les choix simplement, avec mon niveau en tête. N'applique rien avant mon "ok".

## Étape 5 sur 7 : exécution
Petits pas, un sujet à la fois. Après chaque groupe de modifications, explique en langage simple ce qui a changé et pourquoi. Si quelque chose sort du plan, arrête-toi et demande-moi.

## Étape 6 sur 7 : vérification
1. `mvn clean package` puis `mvn test`. Donne-moi la ligne "Tests run: ..." exacte. Si c'est 0 test alors qu'il en existe, dis-le.
2. Si le serveur de test est prêt : fais le déroulé de la section "Serveur de test" du CLAUDE.md avec un jar construit contre Paper 26.1.2 (`-Dpaper.version=26.1.2.build.74-stable`), tant que la prod reste sur cette version.
3. Si tout est vert : commit local (un commit par tâche). Sinon : ne commite pas, explique-moi le blocage.
4. Ne dis jamais "testé" pour ce que tu ne peux pas tester sans joueur : écris "à vérifier en jeu".

## Étape 7 sur 7 : clôture
Donne-moi ce rapport, dans cet ordre :
- **Résumé** : ce qui a changé, en 3 lignes.
- **Résultats réels** : build, tests, serveur de test.
- **Commits** : hash du commit de retour (le dernier AVANT ton travail), hash du commit d'où vient le jar, et la commande de retour arrière (`git revert`).
- **Pour déployer** : le jar à envoyer (jamais `original-*.jar`) et la commande exacte de build, les clés de config à changer sans secret, si les sauvegardes Gson changent, et le rappel de garder l'ancien jar.
- **À vérifier en jeu** : la liste précise de ce que je dois tester, avec les commandes.
- **Dette ou surprises** : tout ce que tu as vu d'anormal pendant la session (même hors sujet), sans le corriger.
- **Suite** : une ou deux tâches possibles pour la prochaine session.

Rappelle-moi ensuite mon cycle : je teste en jeu, je déploie en SFTP, je teste en prod, et je pousse en dernier. Tu ne fais aucune de ces trois dernières étapes.

## Si je te dis "rappelle-moi le guide"
Résume en 10 lignes le cycle de validation et les 7 étapes de cette session, et indique-moi `docs/GUIDE-AGENT.md` pour le détail.
