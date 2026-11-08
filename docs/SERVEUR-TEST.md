# Serveur de test `C:\farmland-test` : déroulé

Procédure détaillée pour l'agent. Les règles de sécurité du serveur de test (`webapi.enabled: false`, webhooks vides) restent dans `CLAUDE.md` et se vérifient avant chaque démarrage.

Le serveur tourne en Paper 26.1.2 build 74 comme la prod, avec les plugins de la prod et une copie de mondes réels (plots, course `circuitalbert`). Remise à zéro : supprimer `C:\farmland-test` puis recopier `C:\farmland-test-backup`.

## Déroulé
1. Copie `target/farmland-<version>.jar` dans `C:\farmland-test\plugins\` en remplaçant l'ancien.
2. Démarre et attends la ligne `Done (`.
3. Dans `logs/latest.log` : Farmland activé avec ses modules ; cherche `WARN`, `ERROR`, `Exception`, `NoClassDefFoundError`.
4. Lance les commandes console utiles à la tâche, puis relis le log.
5. `stop`, et vérifie que le processus est terminé.

## Messages normaux au démarrage (config vidée exprès)
- `[Farmland] ⚠ Webhook Discord non configuré pour le salon "statut"` (ou `"status"`), plusieurs fois.
- `[WebAPI] Module désactivé`.

Le même message pour un autre salon est probablement normal : cite-le la première fois. Tout autre `WARN`, `ERROR` ou `Exception` doit être signalé.
