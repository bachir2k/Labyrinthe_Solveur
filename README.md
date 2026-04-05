# 🧩 Résolution de Labyrinthe — ESP Dakar Master 1



> **Cours** : Programmation et Algorithmique Avancée  
> **Professeur** : Dr Mouhamed DIOP — École Supérieure Polytechnique de Dakar  
> **Deadline** : 12 avril 2026  
> **Langage** : Java 21 | **Build** : Maven  

---

## 📋 Présentation

Programme Java résolvant automatiquement un labyrinthe en utilisant deux algorithmes de parcours de graphe :

| Algorithme | Structure | Optimalité | Mémoire |
|------------|-----------|------------|---------|
| **DFS** — Depth-First Search | Pile (LIFO) | ❌ Non garantie | 🟢 Faible |
| **BFS** — Breadth-First Search | File (FIFO) | ✅ Chemin le plus court | 🔴 Plus élevée |

Le programme fonctionne en **mode console interactif** et avec une **interface graphique Swing** (bonus).

---




## Mais les gars , vous voyez , les fichiers markdown c'est dingue , c'est tellement pratique , c'est pour cette raion que j'vous disais que fallait maitriser cela Toi Bachir et ABdou Latif 







## 🗂️ Structure du projet

```
labyrinthe/
├── src/
│   ├── main/java/com/labyrinthe/
│   │   ├── Main.java                        # Point d'entrée (GUI ou console)
│   │   ├── model/
│   │   │   ├── Cell.java                    # Enum : MUR, PASSAGE, DEPART, SORTIE, CHEMIN, EXPLORE
│   │   │   ├── Maze.java                    # Modèle principal (matrice 2D)
│   │   │   └── Position.java                # Coordonnées (ligne, colonne)
│   │   ├── solver/
│   │   │   ├── MazeSolver.java              # Interface commune
│   │   │   ├── DFSSolver.java               # DFS avec pile ArrayDeque
│   │   │   ├── BFSSolver.java               # BFS avec file ArrayDeque
│   │   │   └── SolverResult.java            # Résultat + statistiques
│   │   ├── generator/
│   │   │   └── MazeGenerator.java           # Génération par Recursive Backtracking
│   │   ├── io/
│   │   │   ├── MazeFileReader.java          # Chargement depuis fichier .txt
│   │   │   └── MazeFileWriter.java          # Sauvegarde vers fichier .txt
│   │   └── ui/
│   │       ├── ConsoleDisplay.java          # Affichage console coloré (ANSI)
│   │       ├── MazePanel.java               # Panneau Swing de rendu
│   │       └── GraphicalDisplay.java        # Fenêtre principale Swing
│   ├── test/java/com/labyrinthe/
│   │   ├── solver/
│   │   │   ├── DFSSolverTest.java
│   │   │   └── BFSSolverTest.java
│   │   └── model/
│   │       └── MazeTest.java
│   └── main/resources/mazes/
│       ├── simple.txt                       # 7×5 — démo sujet
│       ├── medium.txt                       # labyrinthe moyen
│       └── complex.txt                      # labyrinthe complexe
├── pom.xml
├── .gitignore
└── README.md
```

---

## Lancer le programme

### Prérequis
- **Java 21+** — `java -version`
- **Maven 3.8+** — `mvn -version`

### Compilation
```bash
mvn compile
```

### Mode graphique (interface Swing)
```bash
mvn exec:java -Dexec.mainClass="com.labyrinthe.Main"
# ou après mvn package :
java -jar target/labyrinthe.jar
```

### Mode console interactif
```bash
mvn exec:java -Dexec.mainClass="com.labyrinthe.Main" -Dexec.args="console"
```

### Lancer les tests JUnit
```bash
mvn test
```

### Compiler manuellement (sans Maven)
```bash
# Compiler
find src/main/java -name "*.java" > sources.txt
javac -d out @sources.txt

# Lancer
java -cp out com.labyrinthe.Main          # GUI
java -cp out com.labyrinthe.Main console  # Console
```

---

## 📄 Format du fichier labyrinthe

```
#######
#S===E#
#=###=#
#=====#
#######
```

| Symbole | Signification |
|---------|---------------|
| `#` | Mur |
| `=` | Passage libre |
| `S` | Point de départ |
| `E` | Point d'arrivée (sortie) |
| `+` | Chemin solution (après résolution) |

Les fichiers `.txt` de test se trouvent dans `src/main/resources/mazes/`.

---

## 🧠 Algorithmes

### DFS — Depth-First Search
```
Initialiser : pile ← {départ}, parent[départ] ← null
Tant que pile non vide :
    current ← pile.pop()
    Si current = sortie → reconstruire chemin via parent[]
    Pour chaque voisin non visité de current :
        parent[voisin] ← current
        pile.push(voisin)
```

### BFS — Breadth-First Search
```
Initialiser : file ← {départ}, parent[départ] ← null
Tant que file non vide :
    current ← file.poll()
    Si current = sortie → reconstruire chemin via parent[]
    Pour chaque voisin non visité de current :
        parent[voisin] ← current
        file.add(voisin)
```

### Génération aléatoire (Recursive Backtracking)
1. Grille initialement pleine de murs
2. Creusage récursif des passages en visitant les voisins dans un ordre aléatoire
3. Chaque passage relie deux cellules distantes de 2 cases (les murs intermédiaires sont abattus)
4. Résultat : labyrinthe **parfait** (sans cycles, une seule solution)

---

## 📊 Comparaison DFS vs BFS

| Métrique | DFS | BFS |
|----------|-----|-----|
| Structure | Pile (LIFO) | File (FIFO) |
| Chemin optimal | ❌ | ✅ |
| Mémoire | O(profondeur) | O(largeur) |
| Complexité | O(V + E) | O(V + E) |

> BFS garantit toujours le chemin le plus court en nombre de cases.
> C'est pour cela que les gars , moi j'préffère l'algo BFS , si j'suis coincé dans un labyrinthe nafa gueine reik hahahaha
> DFS est souvent plus rapide mais peut trouver un chemin sous-optimal.

---

## 🖥️ Interface graphique

L'interface Swing permet de :
- Charger un labyrinthe depuis un fichier `.txt`
- Générer un labyrinthe aléatoire (taille paramétrable)
- Sauvegarder le labyrinthe généré
- Choisir l'algorithme : DFS, BFS, ou comparaison
- **Visualiser l'animation pas-à-pas** de la résolution
- Contrôler la vitesse d'animation
- Consulter les statistiques (longueur, nœuds explorés, temps)

---

## 👥 Membres du groupe

| Membre | Rôle | Responsabilités |
|--------|------|-----------------|
| **[ABDOU LATIF SAADOU]** | 🏗️ Architecture & Modèle | `model/`, `generator/`, `io/`, structure Maven |
| **[MOUHAMADOUL BACHIR SALL]** | 🧠 Algorithmes & Tests | `solver/`, tests JUnit, comparaison performances |
| **[MOUSSA SOW]** | 🎨 Interface & Intégration | `ui/`, console colorée, Swing, animation, `Main.java` |

---

## 📦 Livrables

- [x] Code source documenté sur GitHub
- [ ] Vidéo YouTube de présentation (≤ 10 min) QUi va vraiment me donner du fil à retordre halalala 
- [ ] Mail envoyé à Monsieur Diop

---

Mais bon j'espère qu'on aura fini avant le 12 Avril 
