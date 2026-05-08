# SyndiCare — Plateforme de gestion de copropriété

> Application web full-stack (Spring Boot + React) pour la gestion d'immeubles en copropriété : suivi des charges, helpdesk, documentation et communication entre syndic, propriétaires et résidents.

**Projet de Fin d'Études (PFE)** — démonstration d'une stack moderne Java/JavaScript sur un cas métier concret.

---

## ✨ Fonctionnalités

### 👥 Trois rôles
- **Syndic (ADMIN)** — gestion complète : immeubles, lots, utilisateurs, génération des charges, validation des paiements, traitement des incidents, publication d'annonces.
- **Propriétaire (OWNER)** — consultation des charges, téléchargement des quittances, déclaration d'incidents, accès aux annonces et documents.
- **Résident / Locataire (RESIDENT)** — déclaration d'incidents avec photo, accès aux annonces et documents.

### 🏢 Modules
- **Immeubles & appartements** — CRUD complet, rattachement propriétaire / résident, tarif par m².
- **Charges** — génération automatique mensuelle (`montant = surface × tarif/m²`), validation de paiement, **export PDF de quittance**.
- **Réclamations (helpdesk)** — 7 catégories (plomberie, électricité, ascenseur, propreté, sécurité, bruit, autre), 4 niveaux de priorité, upload de photo, suivi de statut, notes du syndic.
- **Documents** — bibliothèque catégorisée (PV d'AG, règlement, contrats, factures, budgets), upload/téléchargement sécurisé.
- **Annonces** — mur d'annonces avec niveau de gravité (info / avertissement / urgent), épinglage.
- **Tableaux de bord** — vue Syndic avec graphique de recettes 6 mois, vue Propriétaire personnalisée.

### 🔐 Sécurité
- Authentification **JWT** (24h) avec stockage local côté front.
- Hashage **BCrypt** des mots de passe.
- Autorisation par rôle au niveau des contrôleurs (`@PreAuthorize`).
- Filtre CORS configuré pour le dev local.

---

## 🛠️ Stack technique

### Backend
- **Java 17** · **Spring Boot 3.2.5**
- Spring Web · Spring Data JPA · Spring Security
- **JWT** (jjwt 0.12.5)
- **MySQL** (production) / **H2** (développement, embarqué)
- Lombok pour réduire le boilerplate
- Génération PDF maison (sans dépendance externe)
- Maven

### Frontend
- **React 18** · **Vite 5**
- **Tailwind CSS 3** (design system custom : palette crème / ink / sage / ochre, fonts Bricolage Grotesque + Inter)
- **React Router 6** pour le routage
- **Axios** avec intercepteur JWT
- **Recharts** pour les graphiques
- **Lucide React** pour les icônes
- **react-hot-toast** pour les notifications

---

## 🚀 Démarrage rapide

### Pré-requis
- **Java 17+** (`java -version`)
- **Maven 3.8+** (`mvn -v`) — ou utilisez l'IDE
- **Node 18+** et **npm** (`node -v`)
- **MySQL 8+** (uniquement pour le profil `prod` ; le profil `dev` utilise H2 en mémoire)

### 1️⃣ Backend (Spring Boot)

```bash
cd backend
mvn spring-boot:run
```

Au premier démarrage :
- Le profil `dev` est actif par défaut → **base H2 en mémoire**.
- Le `DataSeeder` peuple automatiquement la base avec 4 utilisateurs, 2 immeubles, 4 appartements, 8 charges, 3 réclamations et 3 annonces.
- L'API est disponible sur **http://localhost:8080/api**.
- Console H2 : http://localhost:8080/api/h2-console (JDBC URL: `jdbc:h2:mem:syndicare`, user `sa`, password vide).

#### Bascule en MySQL (profil `prod`)

```bash
# 1. Créer la base (le serveur la créera s'il a les droits, mais autant l'avoir)
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS syndicare CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 2. Lancer avec le profil prod
DB_USER=root DB_PASSWORD=monMotDePasse mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

(Sur Windows PowerShell : `$env:DB_USER="root"; $env:DB_PASSWORD="..."; mvn spring-boot:run -D"spring-boot.run.profiles=prod"`)

### 2️⃣ Frontend (React + Vite)

Dans un **autre terminal** :

```bash
cd frontend
npm install
npm run dev
```

L'application est servie sur **http://localhost:5173**.

> Vite proxy `/api/*` vers `http://localhost:8080` automatiquement (voir `vite.config.js`). Vous n'avez rien à configurer.

### 3️⃣ Connexion

Trois comptes de démo sont créés au seed. Le bouton "Comptes de démonstration" sur l'écran de login les pré-remplit en un clic.

| Rôle         | Email                       | Mot de passe   |
|--------------|------------------------------|----------------|
| Syndic       | `admin@syndicare.ma`         | `admin123`     |
| Propriétaire | `owner@syndicare.ma`         | `owner123`     |
| Résident     | `resident@syndicare.ma`      | `resident123`  |

Un second propriétaire (`karim@syndicare.ma` / `owner123`) est aussi disponible.

---

## 📁 Structure du projet

```
syndicare/
├── backend/                         # API Spring Boot
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/syndicare/
│       │   ├── SyndiCareApplication.java
│       │   ├── domain/              # Entités JPA (User, Building, Apartment, Charge, Ticket, Document, Announcement)
│       │   ├── repository/          # Spring Data repositories
│       │   ├── dto/                 # Data Transfer Objects
│       │   ├── service/             # Logique métier (Auth, Charge, Ticket, Pdf...)
│       │   ├── controller/          # REST controllers
│       │   ├── security/            # JWT + Spring Security config
│       │   └── config/              # GlobalExceptionHandler + DataSeeder
│       └── resources/
│           ├── application.properties
│           ├── application-dev.properties     (H2)
│           └── application-prod.properties    (MySQL)
│
└── frontend/                        # SPA React
    ├── package.json
    ├── vite.config.js
    ├── tailwind.config.js
    ├── index.html
    └── src/
        ├── main.jsx
        ├── App.jsx                  # Routes + protection par rôle
        ├── index.css                # Tailwind + design system
        ├── lib/api.js               # Client axios + tous les endpoints
        ├── context/AuthContext.jsx
        ├── components/
        │   ├── Layout.jsx           # Sidebar + responsive mobile
        │   └── UI.jsx               # PageHeader, Modal, Badge, StatCard, helpers...
        └── pages/
            ├── Login.jsx            ├── Register.jsx
            ├── AdminDashboard.jsx   ├── OwnerDashboard.jsx
            ├── Buildings.jsx        ├── Apartments.jsx        ├── Users.jsx
            ├── Charges.jsx          ├── Tickets.jsx
            ├── Documents.jsx        └── Announcements.jsx
```

---

## 🔌 API — endpoints principaux

Toutes les routes sont préfixées par `/api`. Authentification : header `Authorization: Bearer <token>`.

### Auth (public)
| Méthode | Route             | Description                     |
|---------|-------------------|---------------------------------|
| POST    | `/auth/login`     | Login → JWT                     |
| POST    | `/auth/register`  | Inscription                     |
| GET     | `/auth/me`        | Utilisateur courant             |

### Buildings
| Méthode | Route                | Rôles requis |
|---------|----------------------|--------------|
| GET     | `/buildings`         | tous         |
| POST    | `/buildings`         | ADMIN        |
| PUT     | `/buildings/{id}`    | ADMIN        |
| DELETE  | `/buildings/{id}`    | ADMIN        |

### Apartments
| Méthode | Route                          | Rôles requis |
|---------|--------------------------------|--------------|
| GET     | `/apartments` (filtre `?buildingId=`, `?ownerId=`) | tous |
| GET     | `/apartments/mine`             | tous (lots du user)         |
| POST/PUT/DELETE | `/apartments[/{id}]`   | ADMIN        |

### Charges
| Méthode | Route                              | Rôles requis |
|---------|------------------------------------|--------------|
| GET     | `/charges` (filtres `buildingId`, `apartmentId`) | ADMIN |
| GET     | `/charges/mine`                    | tous         |
| POST    | `/charges/generate`                | ADMIN — génère les appels du mois pour un immeuble |
| POST    | `/charges/validate-payment`        | ADMIN        |
| GET     | `/charges/{id}/receipt`            | tous — PDF   |
| DELETE  | `/charges/{id}`                    | ADMIN        |

### Tickets (helpdesk)
| Méthode | Route             | Rôles requis | Notes                          |
|---------|-------------------|--------------|--------------------------------|
| GET     | `/tickets`        | tous         | ADMIN voit tout, autres voient leurs propres tickets |
| POST    | `/tickets` (multipart) | tous   | `data` (JSON) + `photo` (file) optionnel |
| POST    | `/tickets` (json) | tous         | sans photo                     |
| PATCH   | `/tickets/{id}`   | ADMIN        | statut, priorité, notes        |
| DELETE  | `/tickets/{id}`   | ADMIN        |                                |

### Documents
| Méthode | Route                       | Rôles requis |
|---------|-----------------------------|--------------|
| GET     | `/documents` (filtre `?category=`) | tous |
| POST    | `/documents` (multipart)    | ADMIN        |
| GET     | `/documents/{id}/download`  | tous         |
| DELETE  | `/documents/{id}`           | ADMIN        |

### Annonces
| Méthode | Route                  | Rôles requis |
|---------|------------------------|--------------|
| GET     | `/announcements`       | tous         |
| POST/PUT/DELETE | `/announcements[/{id}]` | ADMIN |

### Dashboard
| Méthode | Route                | Rôles requis |
|---------|----------------------|--------------|
| GET     | `/dashboard/admin`   | ADMIN        |
| GET     | `/dashboard/owner`   | tous         |

### Files (servi public)
- `GET /files/{path}` — sert les fichiers uploadés (photos de tickets).

---

## 🧮 Cœur métier : génération des charges

L'algorithme implémenté dans `ChargeService.generateForBuilding(...)` :

1. Récupère l'immeuble et tous ses appartements.
2. Pour chaque appartement, calcule `montant = surface (m²) × tarif/m²`. Le tarif est celui de l'immeuble par défaut, ou un tarif personnalisé passé dans la requête.
3. Si un appel de fonds existe déjà pour cet appartement à cette période, il est ignoré (idempotence).
4. Crée la charge avec statut `PENDING`, échéance = dernier jour du mois.

C'est une distribution proportionnelle simple à la surface — équivalente aux **tantièmes** dans la jurisprudence française. L'extension naturelle (clés de répartition par poste : ascenseur, chauffage, eau...) se branche dans le même service.

---

## 📋 Matrice des fonctionnalités (MoSCoW)

| # | Module                         | Statut | Catégorie  |
|---|--------------------------------|--------|------------|
| 1 | Authentification JWT + 3 rôles | ✅      | Must Have  |
| 2 | Gestion immeubles & appartements | ✅    | Must Have  |
| 3 | Calcul automatique des charges | ✅      | Must Have  |
| 4 | Validation des paiements       | ✅      | Must Have  |
| 5 | Quittance PDF                  | ✅      | Should Have |
| 6 | Helpdesk avec photo            | ✅      | Must Have  |
| 7 | Bibliothèque de documents      | ✅      | Should Have |
| 8 | Mur d'annonces                 | ✅      | Should Have |
| 9 | Tableaux de bord               | ✅      | Should Have |
| 10 | Notifications email           | ⬜      | Could Have (extension) |
| 11 | Paiement en ligne (Stripe/CMI)| ⬜      | Could Have (extension) |
| 12 | Application mobile native     | ⬜      | Won't Have |

---

## 🧪 Tests rapides

Une fois les deux services lancés :

1. **Login admin** → tableau de bord syndic avec graphique des recettes.
2. **Onglet Immeubles** → l'immeuble *Résidence Al Andalous* (Tétouan) et *Immeuble Atlas* (Tanger) sont déjà présents.
3. **Onglet Charges** → cliquer sur **"Générer les charges du mois"** pour un immeuble → toutes les charges du mois en cours sont créées (ou ignorées si déjà existantes).
4. **Valider un paiement** → la charge passe en `PAID`, et le bouton "Quittance" apparaît pour télécharger le PDF.
5. **Logout → login propriétaire** (`owner@syndicare.ma`) → vue personnalisée avec le solde dû.
6. **Onglet Réclamations → "Déclarer un incident"** → formulaire avec upload de photo.
7. **Re-login admin → Réclamations → "Mettre à jour"** → changer le statut en *En traitement* et ajouter une note.

---

## 🎨 Design

Le design system se veut éditorial et raffiné, à mi-chemin entre l'élégance d'un site presse et la sobriété d'un produit fintech :

- **Palette** — fond crème (`#f7f4ee`), encre profonde (`#0a0e1a`), accents sage (vert) et ochre (jaune-orange).
- **Typographie** — `Bricolage Grotesque` (display, gras, étiré) + `Inter` (corps, lisibilité).
- **Texture** — fine grille pointillée en fond, scrollbars custom, focus states accessibles.
- **Animations** — entrées subtiles (slide-up, fade-in), pas de gimmicks.

C'est volontairement éloigné du design "AI generic" (gradients violets sur blanc, cartes flottantes vides). Chaque écran a une raison d'être, une hiérarchie claire et des microcopies en français.

---

## 📦 Build de production

### Backend
```bash
cd backend
mvn clean package -DskipTests
java -jar target/syndicare-backend-1.0.0.jar --spring.profiles.active=prod
```

### Frontend
```bash
cd frontend
npm run build
# Sortie dans frontend/dist/, à servir derrière nginx ou comme assets statiques par Spring.
```

---

## 📝 Licence

Projet pédagogique, libre d'utilisation pour démonstration et apprentissage.

---

## ✍️ À propos

Réalisé dans le cadre d'un Projet de Fin d'Études. Les données de démo (Tétouan, Tanger, MAD) sont contextualisées pour le marché marocain, mais la logique reste internationale.
