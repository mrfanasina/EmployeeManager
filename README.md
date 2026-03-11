# 📱 GestionEmployé — Application Android CRUD

<div align="center">

  <p>Application Android de gestion des employés avec base de données distante MySQL / PostgreSQL</p>

  ![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
  ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
  ![REST API](https://img.shields.io/badge/REST-API-FF6C37?style=for-the-badge&logo=postman&logoColor=white)

  ![License](https://img.shields.io/badge/license-MIT-blue?style=flat-square)
  ![Status](https://img.shields.io/badge/statut-en%20développement-yellow?style=flat-square)
  ![Platform](https://img.shields.io/badge/plateforme-Android-green?style=flat-square)

</div>

---

## 📖 Description

**EmployeeManager** est une application Android native permettant de gérer une liste d'employés via une **base de données distante MySQL ou PostgreSQL**. Elle implémente les opérations **CRUD complètes** (Créer, Lire, Mettre à jour, Supprimer), un système de **statistiques salariales** en temps réel, ainsi qu'une **visualisation graphique** des données sous forme d'histogramme ou de camembert.

---

## 🛠️ Stack Technique

| Couche | Technologie |
|---|---|
| Mobile | Android (Java / Kotlin) |
| Interface | XML Layouts + ListView |
| Graphiques | MPAndroidChart |
| Base de données | MySQL ou PostgreSQL (distante) |
| Communication réseau | Retrofit 2 / Volley |
| Backend *(requis)* | PHP / Node.js / Spring Boot |

---

## ✨ Fonctionnalités

### 1. ➕ Ajout d'employé
Formulaire de saisie pour enregistrer un nouvel employé avec son nom et son salaire.

### 2. 📋 Affichage de la liste
Visualisation de tous les employés dans un composant `ListView` avec les colonnes :
- **Nom**
- **Salaire**
- **Observation** — calculée automatiquement selon les règles suivantes :

  | Condition | Observation |
  |---|---|
  | Salaire `< 1 000` | 🔴 Médiocre |
  | `1 000 ≤` Salaire `≤ 5 000` | 🟡 Moyen |
  | Salaire `> 5 000` | 🟢 Grand |

### 3. ✏️ Modification & 🗑️ Suppression
- Mise à jour des informations d'un employé via un formulaire pré-rempli
- Suppression d'un enregistrement avec boîte de dialogue de confirmation

### 4. 📊 Statistiques salariales
Affichage en bas de la liste des indicateurs suivants, recalculés en temps réel :

| Indicateur | Description |
|---|---|
| 💰 Salaire total | Somme de tous les salaires |
| 📉 Salaire minimal | Valeur la plus basse |
| 📈 Salaire maximal | Valeur la plus haute |

### 5. 📉 Visualisation graphique
Représentation visuelle des salaires des employés au choix :
- **Histogramme** — Comparaison des salaires par employé (barres verticales)
- **Camembert** — Répartition proportionnelle des salaires (pie chart)

---

## 🗄️ Modèle de Données

### Table `Employe`

```sql
CREATE TABLE Employe (
    numEmp  INT PRIMARY KEY AUTO_INCREMENT,
    nom     VARCHAR(100)   NOT NULL,
    salaire DECIMAL(10, 2) NOT NULL
);
```

## 🚀 Installation & Configuration

### Prérequis

- [Android Studio](https://developer.android.com/studio) Hedgehog ou supérieur
- SDK Android API 21+ (Android 5.0 Lollipop minimum)
- Un serveur backend avec MySQL ou PostgreSQL configuré
- JDK 11 ou supérieur

### Étapes

1. **Cloner le dépôt**

```bash
git clone https://github.com/<votre-username>/gestion-employe.git
cd gestion-employe
```

2. **Ouvrir dans Android Studio**

```
File > Open > sélectionner le dossier du projet
```

3. **Ajouter les dépendances** dans `build.gradle (app)`

```groovy
dependencies {
    // Retrofit pour les requêtes HTTP
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

    // MPAndroidChart pour les graphiques
    implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
}
```

4. **Synchroniser et lancer**

```
Build > Sync Project with Gradle Files
Run > Run 'app'
```
---

## 🧠 Concepts Utilisés

- **Architecture MVC** — Séparation claire entre modèle, vue et contrôleur
- **Appels réseau asynchrones** — Requêtes HTTP non bloquantes avec Retrofit
- **Adaptateur personnalisé** — `BaseAdapter` pour personnaliser chaque ligne du `ListView`
- **Calcul dynamique** — Observation et statistiques calculées côté client à chaque chargement
- **Visualisation de données** — Intégration de MPAndroidChart pour les graphiques interactifs
- **Base de données distante** — Persistance via API REST connectée à MySQL / PostgreSQL

---

## 👤 Auteur

**mrfanasina**

- GitHub : [@mrfanasina](https://github.com/mrfanasina)

---

## 📄 Licence

Ce projet est distribué sous licence **MIT**.
Voir le fichier [LICENSE](./LICENSE) pour plus de détails.

---

<div align="center">
  <sub>Fait avec ❤️ pour l'apprentissage du développement Android et des bases de données distantes</sub>
</div>
