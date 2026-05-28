package heig.vd.map.generator;

import heig.vd.map.GameMap;
import heig.vd.map.MapGenerationConfig;
import heig.vd.map.TileType;
import heig.vd.utils.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class TowerSpotGenerator {
    private static final int[][] ADJACENT_OFFSETS = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
    };

    public void generate(GameMap map, MapGenerationConfig config, Random random, List<Position> roadTiles) {
        List<Position> candidates = collectCandidates(map, roadTiles);
        Collections.shuffle(candidates, random);

        List<Position> selected = new ArrayList<>();
        int maxSpots = Math.min(config.maxTowerSpots, candidates.size());

        for (Position candidate : candidates) {
            if (selected.size() >= maxSpots) {
                break;
            }
            if (!isFarEnough(candidate, selected, config.minTowerSpotSpacing)) {
                continue;
            }
            map.setTile(candidate, TileType.TOWER_SPOT);
            selected.add(candidate);
        }

        map.setTowerSlots(selected);
    }

    private List<Position> collectCandidates(GameMap map, List<Position> roadTiles) {
        Set<Position> seen = new HashSet<>();
        List<Position> candidates = new ArrayList<>();

        Position start = map.getSpawnPoint();
        Position end = map.getEndPoint();
        int exclusion = 1;

        for (Position road : roadTiles) {
            int baseX = (int) road.getCol();
            int baseY = (int) road.getRow();
            for (int[] offset : ADJACENT_OFFSETS) {
                Position candidate = new Position(baseX + offset[0], baseY + offset[1]);
                if (!map.isInside(candidate) || map.getTile(candidate) != TileType.GRASS) {
                    continue;
                }
                if (start != null && distanceChebyshev(candidate, start) <= exclusion) {
                    continue;
                }
                if (end != null && distanceChebyshev(candidate, end) <= exclusion) {
                    continue;
                }
                if (map.isCastleFootprint(candidate)) {
                    continue;
                }

                if (seen.add(candidate)) {
                    candidates.add(candidate);
                }
            }
        }

        return candidates;
    }

    private boolean isFarEnough(Position candidate, List<Position> selected, int minDistance) {
        for (Position other : selected) {
            if (distanceChebyshev(candidate, other) < minDistance) {
                return false;
            }
        }
        return true;
    }

    private int distanceChebyshev(Position a, Position b) {
        return Math.max(Math.abs((int) a.getCol() - (int) b.getCol()), Math.abs((int) a.getRow() - (int) b.getRow()));
    }


}
