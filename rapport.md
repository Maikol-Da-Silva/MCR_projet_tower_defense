# Introduction

Ce projet est un jeu de tower defense développé en Java, conçu pour démontrer une implémentation du patron de chaîne de responsabilité. Le but du joueur est de défendre son château en plaçant des tours le long du chemin emprunté par des vagues d'ennemis (mobs), chaque tour infligeant des dégâts d'un type particulier aux créatures à sa portée.

# Instructions de compilation

# Choix d'implémentation

## Gestion des tours
La classe `TowerManager` centralise la gestion des tours posées sur la carte. Elle conserve l'ensemble des tours dans une `Map<Position, Tower>` et expose une méthode `placeTower` qui valide chaque placement : la position doit se trouver sur un emplacement de tour (`tower slot`) libre de la carte, sinon la pose est refusée. 

### Les tours (`Tower`)
Chaque tour posée est une instance de la classe `Tower`. Une tour connaît sa position sur la carte et son type, et à chaque mise à jour elle cherche un ennemi à sa portée (`isInRange`) puis l'attaque si son temps de rechargement le permet. La cadence de tir est gérée par un compteur de rechargement (`cooldownTimer`) dérivé de la cadence du type de tour : plus la cadence est élevée, plus l'intervalle entre deux tirs est court. Lorsqu'une tour tire, elle inflige les dégâts à la cible (via la chaîne de responsabilité) et engendre un projectile visuel se dirigeant vers l'ennemi.

### Les types de tours (`CombatTowerType`)
Les caractéristiques propres à chaque tour  sont regroupées dans l'énumération `CombatTowerType`, qui décrit les six tours disponibles : `ARCHER`, `CANNON`, `CROSSBOW`, `ICE_WIZARD`, `LIGHTNING` et `POISON_WIZARD`. Chaque valeur de l'énumération fixe les dégâts, la portée, la cadence de tir, le type de dégât infligé (`DmgType`), le prix d'achat, ainsi que les chemins vers le sprite de la tour et celui de son projectile. Cette approche permet d'ajouter ou d'ajuster une tour en un seul endroit, sans toucher à la logique de combat, et facilite l'affichage de ces informations dans l'interface (nom lisible, prix, etc.).

### Les projectiles (`Projectile`)
Lorsqu'une tour tire, elle crée un `Projectile` chargé de représenter visuellement le tir. Le projectile garde une copie de sa position de départ et de la position cible  et avance à chaque mise à jour vers sa cible à une vitesse proportionnelle à la cadence de la tour. Une fois la cible atteinte, il est marqué comme « arrivé » (`hasArrived`) et la tour le retire de la liste des projectiles en vol. 

## Dégats - Pattern chaîne de responsabilité
Le projet utilise le patron chaîne de responsabilité pour gérer l’application des dégâts aux enmies (mobs) de manière modulaire et extensible. Lorsqu’une créature subit un dégât, celui-ci traverse une chaîne de gestionnaires spécialisés, chacun effectuant sa responsabilité spécifique :
1. DMGShield : Vérifie et traite les dégâts bloqués par le bouclier d’un enmie
2. DMGStatus : Applique les effets de statut selon le type de dégât (ralentissement, poison, etc.)
3. DMGResistance : Réduit les dégâts si la créature possède une résistance au type infligé
4. DMGHealth : Applique finalement les dégâts à la santé de la créature
   Chaque gestionnaire (héritage de DMGHandler) peut traiter sa partie puis déléguer au suivant, formant une chaîne flexible où de nouveaux types de dégâts ou d’effets peuvent facilement être ajoutés sans modifier le code existant.

La chaîne est assemblé dans le constructeur de `TowerManager` , puis passé à tour au moment de sa pose. Les tours reçoivent ainsi une référence partagée vers la chaîne et se contentent de lui déléguer le calcul des dégâts lorsqu'un ennemi entre à leur portée. 
