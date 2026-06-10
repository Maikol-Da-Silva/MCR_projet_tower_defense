package heig.vd;

import heig.vd.map.GameMap;
import heig.vd.mob.MobManager;
import heig.vd.tower.Tower;
import heig.vd.utils.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Gère toute la logique du jeu (mobs, tours, physique, économie)
 * Séparé du rendu pour respecter le pattern Model-View-Controller
 */
public class GameManager {
    private final int INITIAL_HEALTH = 20;
    private final int INITIAL_MONEY = 100;
    private final int MONEY_PER_KILL = 10;

    private GameMap map;
    private MobManager mobManager;
    private Map<Position, Tower> placedTowers;

    private int health;
    private int money;
    private int currentWave;
    private boolean waveInProgress;
    private float waveSpawnTimer;
    private float waveDelay = 1f; // Délai avant spawn de la vague (en secondes)

    private boolean gameOver;
    private boolean gameWon;

    public GameManager(GameMap map, Map<Position, Tower> placedTowers) {
        this.map = map;
        this.placedTowers = placedTowers;
        this.health = INITIAL_HEALTH;
        this.money = INITIAL_MONEY;
        this.currentWave = 0;
        this.waveInProgress = false;
        this.waveSpawnTimer = 0;
        this.gameOver = false;
        this.gameWon = false;
    }

    /**
     * Met à jour la logique du jeu à chaque frame
     * @param deltaTime Temps écoulé depuis la dernière frame (en secondes)
     */
    public void update(float deltaTime) {
        if (gameOver || gameWon) {
            return;
        }

        // Gérer le spawn des vagues
        if (!waveInProgress) {
            waveSpawnTimer -= deltaTime;
            if (waveSpawnTimer <= 0) {
                startWave();
            }
        }

        // Mettre à jour les mobs en cours
        if (waveInProgress) {
            updateMobs(deltaTime);
        }

        // Mettre à jour les tours
        updateTowers(deltaTime);

        // Vérifier les conditions de fin
        checkGameState();
    }

    /**
     * Démarre une nouvelle vague de mobs
     */
    private void startWave() {
        currentWave++;
        mobManager = new MobManager(2 + currentWave * 2, 200 + currentWave * 50, map.getSpawnPoint());
        mobManager.createWave();
        waveInProgress = true;
        System.out.println("Wave " + currentWave + " started!");
    }

    /**
     * Met à jour la position et l'état des mobs
     */
    private void updateMobs(float deltaTime) {
        if (mobManager == null || mobManager.getMobs().isEmpty()) {
            waveInProgress = false;
            waveSpawnTimer = waveDelay;
            return;
        }

        List<Integer> mobsToRemove = new ArrayList<>();
        boolean[] mobNextCase = new boolean[map.getPath().size()];

        for (int i = 0; i < mobManager.getMobs().size(); i++) {
            var mob = mobManager.getMobs().get(i);

            // Mob tué : on le retire et on gagne de l'argent (avant même de le déplacer)
            if (mob.getCurrentHealth() <= 0) {
                money += MONEY_PER_KILL;
                mobsToRemove.add(i);
                System.out.println("Mob killed! Money: $" + money);
                continue;
            }

            if (!mob.canMove(deltaTime)) {
                continue;
            }

            // Calculer la position suivante
            Position currentPos = mob.getPosition();
            int currentIndex = getPathIndex(currentPos);
            int nextIndex = currentIndex + 1;


                // Vérifier si le mob a atteint la fin
                if (nextIndex >= map.getPath().size()) {
                    // Le mob a échappé
                    health--;
                    mobsToRemove.add(i);
                    System.out.println("Mob escaped! Health: " + health);
                } else {
                    //On vérifie si un monstre est sur la case d'après
                    if (!mobNextCase[nextIndex]) {
                        mobNextCase[currentIndex] = false;
                        mobNextCase[nextIndex] = true;

                        // Déplacer le mob vers la prochaine position
                        Position nextPosition = map.getPath().get(nextIndex);
                        mob.setPosition(nextPosition);
                    }
                }

        }

        // Supprimer les mobs qui ont échappé ou ont été tuésd
        for (int i = mobsToRemove.size() - 1; i >= 0; i--) {
            mobManager.getMobs().remove((int) mobsToRemove.get(i));
        }
    }

    /**
     * Obtient l'index du chemin où se trouve une position
     */
    private int getPathIndex(Position pos) {
        List<Position> path = map.getPath();
        for (int i = 0; i < path.size(); i++) {
            if (path.get(i).equals(pos)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Met à jour la logique des tours (tirs, recharge, etc.)
     */
    private void updateTowers(float deltaTime) {
        for (Tower tower : placedTowers.values()) {
            tower.update(deltaTime, mobManager != null ? mobManager.getMobs() : new ArrayList<>());
        }
    }

    /**
     * Vérifie les conditions de fin de jeu
     */
    private void checkGameState() {
        // Défaite : santé à 0
        if (health <= 0) {
            gameOver = true;
            System.out.println("Game Over! You lost!");
        }

        // Victoire : tous les mobs de la dernière vague sont morts et aucune vague supplémentaire
        if (waveInProgress && mobManager != null && mobManager.getMobs().isEmpty() && currentWave >= 10) {
            gameWon = true;
            System.out.println("You won!");
        }
    }

    // Getters
    public int getHealth() { return health; }
    public int getMoney() { return money; }
    public int getCurrentWave() { return currentWave; }
    public boolean isGameOver() { return gameOver; }
    public boolean isGameWon() { return gameWon; }
    public MobManager getMobManager() { return mobManager; }
    public boolean isWaveInProgress() { return waveInProgress; }

    // Setters
    public void addMoney(int amount) { this.money += amount; }
    public void spendMoney(int amount) { this.money -= amount; }
}

