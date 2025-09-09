# HNR

## Configuration CORS

Les domaines autorisés peuvent être configurés via la propriété `app.cors.allowed-origins` dans `application.properties`.
Ajoutez de nouveaux domaines en les séparant par des virgules :

```
app.cors.allowed-origins=http://localhost:3000,http://mon-domaine.com
```

Chaque modification est prise en compte au redémarrage de l'application.
