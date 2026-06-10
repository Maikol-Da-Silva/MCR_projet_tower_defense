package heig.vd.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import heig.vd.map.GameMap;
import heig.vd.map.TileType;
import heig.vd.tower.Tower;
import heig.vd.utils.Position;

import java.util.Map;

import java.util.List;

/**
 * Handles all map rendering logic.
 * Draws grass, roads, castle, towers, and decorations onto a SpriteBatch.
 */
public class MapRenderer {
    private final TextureManager textureManager;
    private int tileSize;
    private int offsetX;
    private int offsetY;

    public MapRenderer(TextureManager textureManager) {
        this.textureManager = textureManager;
    }

    /**
     * Updates layout based on screen size and map dimensions.
     * Should be called on resize or after map generation.
     */
    public void updateLayout(int screenWidth, int screenHeight, GameMap map) {
        if (map == null || screenWidth <= 0 || screenHeight <= 0) {
            return;
        }

        final int UI_HEIGHT = 30;
        final int SCREEN_PADDING = 16;
        final int MIN_TILE_SIZE = 8;
        final int MAX_TILE_SIZE = 96;

        int availableWidth = Math.max(1, screenWidth - SCREEN_PADDING * 2);
        int availableHeight = Math.max(1, screenHeight - UI_HEIGHT - SCREEN_PADDING * 2);
        tileSize = Math.min(MAX_TILE_SIZE, Math.max(MIN_TILE_SIZE,
                Math.min(availableWidth / map.getWidth(), availableHeight / map.getHeight())));

        int mapWidthPixels = map.getWidth() * tileSize;
        int mapHeightPixels = map.getHeight() * tileSize;
        offsetX = Math.max(SCREEN_PADDING, (screenWidth - mapWidthPixels) / 2);
        offsetY = Math.max(SCREEN_PADDING, SCREEN_PADDING + (availableHeight - mapHeightPixels) / 2);
    }

    /**
     * Draws the complete map (grass, roads, tower spots, decorations).
     */
    public void drawMapBase(SpriteBatch batch, GameMap map, List<DecorationPlacement> decorations, Map<Position, ?> placedTowers) {
        if (map == null || tileSize <= 0) {
            return;
        }

        currentMap = map; // store for road drawing logic

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                float drawX = offsetX + x * tileSize;
                float drawY = offsetY + y * tileSize;
                Position pos = new Position(x, y);

                // Always draw grass first
                batch.draw(textureManager.getGrassTexture(), drawX, drawY, tileSize, tileSize);

                if (map.isCastleFootprint(pos)) {
                    continue; // castle drawn later
                }

                TileType tileType = map.getTile(pos);
                switch (tileType) {
                    case GRASS -> drawDecoration(batch, pos, drawX, drawY, decorations);
                    case ROAD, START -> drawRoadVariant(batch, x, y, drawX, drawY);
                    case TOWER_SPOT -> {
                        // Only draw placeholder tower if no tower is placed here
                        if (!placedTowers.containsKey(pos)) {
                            drawAnimatedFrames(batch, textureManager.getTowerFrames(), drawX, drawY, tileSize, tileSize);
                        }
                    }
                    case END -> {
                    }
                }
            }
        }

        currentMap = null; // cleanup
    }

    /**
     * Draws the castle at the end position.
     */
    public void drawCastle(SpriteBatch batch, GameMap map) {
        Position end = map == null ? null : map.getEndPoint();
        if (end == null) {
            return;
        }

        int castleLeftX = map.getCastleLeftX();
        int castleBaseY = map.getCastleBaseY();
        float drawX = offsetX + castleLeftX * tileSize;
        float drawY = offsetY + castleBaseY * tileSize;

        drawAnimatedFrames(batch, textureManager.getCastleFrames(), drawX, drawY,
                tileSize * map.getCastleWidth(), tileSize * map.getCastleHeight());
    }

    /**
     * Draws all placed towers on the map.
     */
    public void drawPlacedTowers(SpriteBatch batch, Map<Position, Tower> placedTowers) {
        for (Tower tower : placedTowers.values()) {
            Position pos = tower.getPosition();
            if (pos == null) continue;

            float drawX = offsetX + (int) pos.getCol() * tileSize;
            float drawY = offsetY + (int) pos.getRow() * tileSize;

            // Get the texture frames specific to this tower's type
            Texture texture = textureManager.getCombatTowerTexture(tower.getType());
            if (texture == null) {
                continue;
            }

            // Draw tower
            batch.draw(texture, drawX, drawY, tileSize, tileSize);
        }
    }



    /**
     * Converts screen coordinates to map grid coordinates.
     * Returns null if click is outside the map.
     */
    public Position screenToMapPosition(int screenX, int screenY, int screenHeight) {
        if (tileSize <= 0) {
            return null;
        }

        float worldX = screenX;
        float worldY = screenHeight - 1 - screenY; // Invert Y for LibGDX coordinate system

        int col = (int) ((worldX - offsetX) / tileSize);
        int row = (int) ((worldY - offsetY) / tileSize);

        return new Position(col, row);
    }

    // --- Getters ---

    public int getTileSize() {
        return tileSize;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    // --- Private rendering helpers ---

    private void drawDecoration(SpriteBatch batch, Position pos, float drawX, float drawY,
                             List<DecorationPlacement> decorations) {
        for (DecorationPlacement placement : decorations) {
            if (!placement.position.equals(pos)) {
                continue;
            }

            float scale = 0.6f;
            float w = tileSize * scale;
            float h = tileSize * scale;
            float xOff = drawX + (tileSize - w) / 2f;
            float yOff = drawY + (tileSize - h) / 2f;
            batch.draw(textureManager.getDecorationTextures()[placement.variant], xOff, yOff, w, h);
            return;
        }
    }

    private GameMap currentMap; // temporary storage for road drawing

    private void drawRoadVariant(SpriteBatch batch, int x, int y, float drawX, float drawY) {
        TextureRegion[] variants = textureManager.getRoadVariants();
        if (variants == null || variants.length == 0) {
            return;
        }

        boolean n = isRoadLike(x, y + 1);
        boolean e = isRoadLike(x + 1, y);
        boolean s = isRoadLike(x, y - 1);
        boolean w = isRoadLike(x - 1, y);

        TextureRegion region = roadRegionFromNeighbors(x, y, n, e, s, w);
        batch.draw(region, drawX, drawY, tileSize, tileSize);
    }

    private boolean isRoadLike(int x, int y) {
        if (currentMap == null) return false;
        Position pos = new Position(x, y);
        if (!currentMap.isInside(pos)) return false;
        TileType t = currentMap.getTile(pos);
        return t == TileType.ROAD || t == TileType.START || t == TileType.END;
    }

    private TextureRegion roadRegionFromNeighbors(int x, int y, boolean n, boolean e, boolean s, boolean w) {
        int connections = (n ? 1 : 0) + (e ? 1 : 0) + (s ? 1 : 0) + (w ? 1 : 0);

        if (connections == 2) {
            if (n && s && !e && !w) return roadTile(0, 1);
            if (e && w && !n && !s) return roadTile(1, 0);
            if (n && e) return roadTile(0, 2);
            if (e && s) return roadTile(0, 0);
            if (s && w) return roadTile(2, 0);
            if (w && n) return roadTile(2, 2);
        }

        if (n || s) return roadTile(0, 1);
        return roadTile(1, 0);
    }

    private TextureRegion roadTile(int col, int row) {
        return textureManager.getRoadVariants()[row * 3 + col];
    }

    private void drawAnimatedFrames(SpriteBatch batch, TextureRegion[] frames, float drawX, float drawY,
                                    float width, float height) {
        if (frames == null || frames.length == 0) {
            return;
        }

        int frame = Math.floorMod((int) (System.currentTimeMillis() / 180L), frames.length);
        batch.draw(frames[frame], drawX, drawY, width, height);
    }

    /**
     * Simple data class to hold decoration placements.
     */
    public static final class DecorationPlacement {
        public final Position position;
        public final int variant;

        public DecorationPlacement(Position position, int variant) {
            this.position = position;
            this.variant = variant;
        }
    }
}




