# Mode autonome : dérouler `docs/TACHES.md`

Procédure détaillée pour l'agent. Les interdits, les zones sensibles et les limites restent dans `CLAUDE.md` et passent avant ce fichier.

## Déroulé
1. Check-in, sans rien modifier : `git status` propre, branche actuelle, `webapi.enabled: false` sur le serveur de test, serveur arrêté. Si quelque chose ne va pas, arrête-toi et dis-le.
2. Crée la branche `agent/AAAA-MM-JJ` depuis `master` (ou reprends-la si elle existe). Note le hash de `master` : c'est le **commit de retour**.
3. Prends la première tâche `à faire` de `docs/TACHES.md` (l'ordre du fichier est la priorité) et passe-la en `en cours`.
4. Si elle touche une zone sensible (section 2 de `CLAUDE.md`) ou si elle est ambiguë : ne touche pas au code. Écris sous la tâche ton plan ou ta question, passe-la en `attend validation` et prends la suivante.
5. Sinon : lis le code concerné, modifie, ajoute les tests, vérifie (section "Vérification" de `CLAUDE.md`).
6. Tout est vert : passe la tâche en `fait` et fais **un commit pour cette tâche** (code, tests et `TACHES.md` ensemble).
7. Échec après 2 tentatives de correction : `git stash push -m "bloqué: <tâche>"`, passe la tâche en `bloqué : <raison en une ligne>` et prends la suivante.
8. Arrête-toi selon les limites de `CLAUDE.md`, puis fais le rapport ci-dessous.

## Rapport de fin
Dans cet ordre :
- **Tâches** : pour chacune, un bloc avec son statut, ce qui a changé (1 ou 2 lignes simples), son hash de commit, sa commande `git revert <hash>` et sa liste précise "à vérifier en jeu" (avec les commandes). Si un test en jeu échoue, je dois retrouver la tâche en cause.
- **Résultats réels** : `BUILD SUCCESS` ou non, ligne `Tests run`, ce que le serveur de test a montré.
- **Commits** : branche, commit de retour, hash du commit d'où vient le jar. Pour tout annuler : les `git revert` du plus récent au plus ancien.
- **Décisions prises seul** : ce que tu as choisi sans me demander.
- **Pour déployer** : `target/farmland-<version>.jar` (version du pom, jamais `original-*.jar`), construit contre 26.1.2 ; clés de `plugins/Farmland/config.yml` à ajouter ou modifier (sans valeur secrète) ; garder l'ancien jar en le renommant ; redémarrage nécessaire. Si le format Gson change : **sauvegarder `plugins/Farmland/` avant de déployer**.
- **Pour toi** : les tâches `attend validation` et `bloqué`, avec ce que j'attends de toi.
- **Surprises** : ce que tu as vu d'anormal, sans l'avoir corrigé.
