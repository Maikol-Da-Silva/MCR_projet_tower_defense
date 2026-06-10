package heig.vd;

import heig.vd.map.GameMap;
import heig.vd.utils.Position;

/**
 * Classe utilitaire pour les calculs de position sur le chemin
 * La logique principale du jeu est maintenant dans GameManager
 */
public class Game {
    private GameMap map;

    public Game(GameMap map) {
        this.map = map;
    }

    /**
     * Retourne l'index du chemin où se trouve une position
     */
    public int getPathIndex(Position pos) {
        int count = 0;
        for (Position p : map.getPath()) {
            if (p.equals(pos)) break;
            count++;
        }
        return count;
    }
}
