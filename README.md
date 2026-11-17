# MediAssist

Application Android de gestion médicale personnelle : suivi des traitements avec rappels, rendez-vous, ordonnances et contacts d'urgence, regroupés dans une seule application et stockés localement sur l'appareil.

## Fonctionnalités

- **Médicaments** : enregistrement des traitements et rappels par notification aux heures de prise
- **Rendez-vous** : création et suivi des rendez-vous médicaux
- **Planning** : vue calendrier regroupant prises de médicaments et rendez-vous
- **Ordonnances** : archivage et consultation
- **Contacts d'urgence** : accès rapide aux personnes à prévenir
- **Compte utilisateur** : inscription, connexion et profil

## Stack technique

| Élément | Choix |
|---|---|
| Langage | Java, mises en page XML |
| Base de données | SQLite (`SQLiteOpenHelper`) |
| Interface | RecyclerView, CardView, NavigationView, ViewPager (écran d'accueil) |
| Notifications | AlarmManager, BroadcastReceiver |
| Compatibilité | Android 7.0 (API 24) et plus récent |

## Modèle de données

| Table | Contenu |
|---|---|
| `users` | Comptes et informations personnelles |
| `medications` | Médicaments, posologie et horaires de rappel |
| `appointments` | Rendez-vous médicaux |
| `prescriptions` | Ordonnances |
| `emergency_contacts` | Contacts d'urgence |

## Structure du code

```
app/src/main/java/com/example/mediassist/
  activities/      Écrans de l'application
  adapters/        Adaptateurs des listes (RecyclerView, écran d'accueil)
  database/        Accès SQLite
  models/          Entités métier
  notifications/   Réception des alarmes et actions sur les notifications
  utils/           Création des notifications
```

## Installation

1. Cloner le dépôt :

   ```bash
   git clone https://github.com/eyazouch/MediAssist_App.git
   ```

2. Ouvrir le projet dans Android Studio et laisser Gradle synchroniser les dépendances.
3. Lancer l'application sur un émulateur ou un appareil Android.

## Équipe

Eya Zouch, Oumayma Khlif
