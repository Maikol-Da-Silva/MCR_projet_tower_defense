package heig.vd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import heig.vd.map.GameMap;
import heig.vd.map.MapDifficulty;
import heig.vd.map.MapGenerator;
import heig.vd.map.TileType;
import heig.vd.utils.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    private static final int UI_HEIGHT = 56;
    private static final int SCREEN_PADDING = 16;
    private static final int MIN_TILE_SIZE = 8;
    private static final int MAX_TILE_SIZE = 96;
    private static final int CASTLE_WIDTH_TILES = 3;
    private static final int CASTLE_HEIGHT_TILES = 2;
    private static final int DECORATION_TARGET_COUNT = 12;

    private SpriteBatch batch;
    private BitmapFont font;

    private Texture grassTexture;
    private Texture roadTexture;
    // roadVariants contains the 3x3 ground tileset flattened as row-major regions.
    private TextureRegion[] roadVariants;

    private Texture castleTexture;
    private TextureRegion[] castleFrames;

    private Texture towerTexture;
    private TextureRegion[] towerFrames;

    private Texture[] decorationTextures;
    private final List<DecorationPlacement> decorationPlacements = new ArrayList<>();

    private final MapGenerator mapGenerator = new MapGenerator();
    private MapDifficulty currentDifficulty = MapDifficulty.EASY;
    private GameMap map;

    private int tileSize;
    private int offsetX;
    private int offsetY;

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();

        grassTexture = loadTexture("Environment/Grass/spr_grass_02.png");
        roadTexture = loadTexture("Environment/Tile Set/spr_tile_set_ground.png");
        int cols = 3;
        int rows = 3;
        int cellWidth = roadTexture.getWidth() / cols;
        int cellHeight = roadTexture.getHeight() / rows;
        TextureRegion[][] split = TextureRegion.split(roadTexture, cellWidth, cellHeight);
        roadVariants = new TextureRegion[cols * rows];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                roadVariants[row * cols + col] = split[row][col];
            }
        }

        castleTexture = loadTexture("Towers/Castle/spr_castle_blue.png");
        castleFrames = splitAnimatedSheet(castleTexture);

        towerTexture = loadTexture("Towers/Non-Combat Towers/spr_normal_tower_01_blue.png");
        towerFrames = splitAnimatedSheet(towerTexture);

        decorationTextures = loadTextures(
                "Environment/Decoration/spr_mushroom_01.png",
                "Environment/Decoration/spr_mushroom_02.png",
                "Environment/Decoration/spr_rock_01.png",
                "Environment/Decoration/spr_rock_02.png",
                "Environment/Decoration/spr_rock_03.png",
                "Environment/Decoration/spr_tree_01_autumn.png",
                "Environment/Decoration/spr_tree_01_cherry_blossom.png",
                "Environment/Decoration/spr_tree_01_normal.png",
                "Environment/Decoration/spr_tree_02_autumn.png",
                "Environment/Decoration/spr_tree_02_normal.png",
                "Environment/Decoration/spr_tree_02_spruce.png"
        );

        regenerateMap();
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClearColor(0.08f, 0.10f, 0.13f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        drawMapBase();
        drawCastle();

        String info = "1 EASY  2 NORMAL  3 HARD  R REGEN | Difficulty: " + currentDifficulty
                + " | Roads: " + map.getRoadTileCount()
                + " | Spots: " + map.countTiles(TileType.TOWER_SPOT);
        font.draw(batch, info, SCREEN_PADDING, Gdx.graphics.getHeight() - 18);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        updateLayout();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        disposeTexture(grassTexture);
        disposeTexture(roadTexture);
        disposeTexture(castleTexture);
        disposeTexture(towerTexture);
        disposeTextures(decorationTextures);
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            currentDifficulty = MapDifficulty.EASY;
            regenerateMap();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            currentDifficulty = MapDifficulty.NORMAL;
            regenerateMap();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            currentDifficulty = MapDifficulty.HARD;
            regenerateMap();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            regenerateMap();
        }
    }

    private void regenerateMap() {
        map = mapGenerator.generate(currentDifficulty);
        regenerateDecorations();
        updateLayout();
    }

    private void regenerateDecorations() {
        decorationPlacements.clear();
        if (map == null || decorationTextures == null || decorationTextures.length == 0) {
            return;
        }

        Random random = new Random();

        List<Position> candidates = new ArrayList<>();
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Position pos = new Position(x, y);
                if (map.getTile(pos) == TileType.GRASS && !isCastleFootprint(x, y)) {
                    candidates.add(pos);
                }
            }
        }

        Collections.shuffle(candidates, random);
        int count = Math.min(DECORATION_TARGET_COUNT, candidates.size());
        for (int i = 0; i < count; i++) {
            Position pos = candidates.get(i);
            int variant = random.nextInt(decorationTextures.length);
            decorationPlacements.add(new DecorationPlacement(pos, variant));
        }
    }

    private void updateLayout() {
        if (map == null) {
            return;
        }

        int availableWidth = Math.max(1, Gdx.graphics.getWidth() - SCREEN_PADDING * 2);
        int availableHeight = Math.max(1, Gdx.graphics.getHeight() - UI_HEIGHT - SCREEN_PADDING * 2);
        tileSize = Math.min(MAX_TILE_SIZE, Math.max(MIN_TILE_SIZE,
                Math.min(availableWidth / map.getWidth(), availableHeight / map.getHeight())));

        int mapWidthPixels = map.getWidth() * tileSize;
        int mapHeightPixels = map.getHeight() * tileSize;
        offsetX = Math.max(SCREEN_PADDING, (Gdx.graphics.getWidth() - mapWidthPixels) / 2);
        offsetY = Math.max(SCREEN_PADDING, SCREEN_PADDING + (availableHeight - mapHeightPixels) / 2);
    }

    private Texture loadTexture(String path) {
        Texture texture = new Texture(path);
        texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        return texture;
    }

    private Texture[] loadTextures(String... paths) {
        Texture[] textures = new Texture[paths.length];
        for (int i = 0; i < paths.length; i++) {
            textures[i] = loadTexture(paths[i]);
        }
        return textures;
    }

    private TextureRegion[] splitAnimatedSheet(Texture sheet) {
        int frameWidth = sheet.getWidth() / 4;
        return TextureRegion.split(sheet, frameWidth, sheet.getHeight())[0];
    }

    private void drawMapBase() {
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                float drawX = offsetX + x * tileSize;
                float drawY = offsetY + y * tileSize;
                Position pos = new Position(x, y);
                // Always draw grass first so tiles that are later overlaid (castle/tower) have ground behind
                batch.draw(grassTexture, drawX, drawY, tileSize, tileSize);

                if (isCastleFootprint(x, y)) {
                    // skip other decorations/road drawing for castle footprint (castle drawn later)
                    continue;
                }

                TileType tileType = map.getTile(pos);
                switch (tileType) {
                    case GRASS -> drawDecoration(pos, drawX, drawY);
                    case ROAD, START -> drawRoadVariant(x, y, drawX, drawY, tileSize);
                    case TOWER_SPOT -> drawAnimatedFrames(towerFrames, x, y, drawX, drawY, tileSize, tileSize);
                    case END -> {
                    }
                }
            }
        }
    }

    private void drawCastle() {
        Position end = map == null ? null : map.getEndPoint();
        if (end == null) {
            return;
        }

        int castleLeftX = castleLeftX(end);
        int castleBaseY = castleBaseY(end);
        float drawX = offsetX + castleLeftX * tileSize;
        float drawY = offsetY + castleBaseY * tileSize;

        drawAnimatedFrames(castleFrames, (int) end.getCol(), castleBaseY, drawX, drawY, tileSize * CASTLE_WIDTH_TILES, tileSize * CASTLE_HEIGHT_TILES);
    }

    private void drawDecoration(Position pos, float drawX, float drawY) {
        for (DecorationPlacement placement : decorationPlacements) {
            if (!placement.position.equals(pos)) {
                continue;
            }

            float scale = 0.6f;
            float w = tileSize * scale;
            float h = tileSize * scale;
            float xOff = drawX + (tileSize - w) / 2f;
            float yOff = drawY + (tileSize - h) / 2f;
            batch.draw(decorationTextures[placement.variant], xOff, yOff, w, h);
            return;
        }
    }

    private void drawRoadVariant(int x, int y, float drawX, float drawY, float tileSize) {
        if (roadVariants == null || roadVariants.length == 0) {
            batch.draw(roadTexture, drawX, drawY, tileSize, tileSize);
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
        Position pos = new Position(x, y);
        if (!map.isInside(pos)) return false;
        TileType t = map.getTile(pos);
        return t == TileType.ROAD || t == TileType.START || t == TileType.END;
    }

    private TextureRegion roadRegionFromNeighbors(int x, int y, boolean n, boolean e, boolean s, boolean w) {
        int connections = (n ? 1 : 0) + (e ? 1 : 0) + (s ? 1 : 0) + (w ? 1 : 0);

        // User-provided mapping on the 3x3 tileset:
        // UL(0,0) UR(2,0) DL(0,2) DR(2,2)
        // horizontal -> (1,0) or (1,2)
        // vertical   -> (0,1) or (2,1)
        if (connections == 2) {
            if (n && s && !e && !w) return verticalVariant(x, y);
            if (e && w && !n && !s) return horizontalVariant(x, y);
            // Alternative inversion direction for this tileset orientation.
            if (n && e) return roadTile(0, 2); // visual corner_DL
            if (e && s) return roadTile(0, 0); // visual corner_UL
            if (s && w) return roadTile(2, 0); // visual corner_UR
            if (w && n) return roadTile(2, 2); // visual corner_DR
        }

        // Start/end cells can have one connection: reuse straight tiles.
        if (connections == 1) {
            if (n || s) return verticalVariant(x, y);
            return horizontalVariant(x, y);
        }

        // Fallback for unexpected topology.
        if (n || s) return verticalVariant(x, y);
        return horizontalVariant(x, y);
    }

    private TextureRegion roadTile(int col, int row) {
        return roadVariants[row * 3 + col];
    }

    private void drawAnimatedFrames(TextureRegion[] frames, int x, int y, float drawX, float drawY, float width, float height) {
        if (frames == null || frames.length == 0) {
            return;
        }

        int frame = Math.floorMod((int) (System.currentTimeMillis() / 180L), frames.length);
        batch.draw(frames[frame], drawX, drawY, width, height);
    }

    private TextureRegion horizontalVariant(int x, int y) {
        long hash = map.getSeed() + x * 41L + y * 17L;
        return (Math.floorMod(hash, 2L) == 0) ? roadTile(1, 0) : roadTile(1, 2);
    }

    private TextureRegion verticalVariant(int x, int y) {
        long hash = map.getSeed() + x * 29L + y * 23L;
        return (Math.floorMod(hash, 2L) == 0) ? roadTile(0, 1) : roadTile(2, 1);
    }

    private boolean isCastleFootprint(int x, int y) {
        Position end = map.getEndPoint();
        return end != null
                && x >= castleLeftX(end)
                && x < castleLeftX(end) + CASTLE_WIDTH_TILES
                && y >= castleBaseY(end)
                && y < castleBaseY(end) + CASTLE_HEIGHT_TILES;
    }

    private int castleLeftX(Position end) {
        return Math.max(0, Math.min((int) end.getCol() - 1, map.getWidth() - CASTLE_WIDTH_TILES));
    }

    private int castleBaseY(Position end) {
        int maxBaseY = Math.max(0, map.getHeight() - CASTLE_HEIGHT_TILES);
        return Math.max(0, Math.min((int) end.getRow(), maxBaseY));
    }

    private void disposeTextures(Texture[] textures) {
        if (textures == null) {
            return;
        }
        for (Texture texture : textures) {
            disposeTexture(texture);
        }
    }

    private void disposeTexture(Texture texture) {
        if (texture != null) {
            texture.dispose();
        }
    }

    private static final class DecorationPlacement {
        private final Position position;
        private final int variant;

        private DecorationPlacement(Position position, int variant) {
            this.position = position;
            this.variant = variant;
        }
    }
}
