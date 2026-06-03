package heig.vd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import heig.vd.map.GameMap;
import heig.vd.map.MapDifficulty;
import heig.vd.map.MapGenerator;
import heig.vd.map.TileType;
import heig.vd.render.MapRenderer;
import heig.vd.render.TextureManager;
import heig.vd.render.MapRenderer.DecorationPlacement;
import heig.vd.Tower.CombatTowerType;
import heig.vd.Tower.Tower;
import heig.vd.ui.TowerUIManager;
import heig.vd.utils.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    private static final int DECORATION_TARGET_COUNT = 12;

    private SpriteBatch batch;
    private BitmapFont font;

    private TextureManager textureManager;
    private MapRenderer mapRenderer;
    private TowerUIManager towerUIManager;

    private final MapGenerator mapGenerator = new MapGenerator();
    private MapDifficulty currentDifficulty = MapDifficulty.EASY;
    private GameMap map;

    private final List<DecorationPlacement> decorationPlacements = new ArrayList<>();
    private final Map<Position, Tower> placedTowers = new HashMap<>(); // towers placed on map

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();

        textureManager = new TextureManager();
        textureManager.loadAll();

        mapRenderer = new MapRenderer(textureManager);
        towerUIManager = new TowerUIManager();

        regenerateMap();

        // Setup input handler for mouse clicks
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                handleMapClick(screenX, screenY);
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClearColor(0.08f, 0.10f, 0.13f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        mapRenderer.drawMapBase(batch, map, decorationPlacements, placedTowers);
        mapRenderer.drawCastle(batch, map);
        mapRenderer.drawPlacedTowers(batch, placedTowers);

        // Draw tower selection UI if visible
        towerUIManager.drawTowerMenu(batch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), textureManager);

        String info = "1 EASY  2 NORMAL  3 HARD  R REGEN | Difficulty: " + currentDifficulty
                + " | Roads: " + map.getRoadTileCount()
                + " | Spots: " + map.countTiles(TileType.TOWER_SPOT);
        font.draw(batch, info, 16, Gdx.graphics.getHeight() - 18);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        mapRenderer.updateLayout(width, height, map);
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
        textureManager.disposeAll();
        towerUIManager.dispose();
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
        placedTowers.clear(); // reset towers when generating new map
        regenerateDecorations();
        mapRenderer.updateLayout(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), map);
    }

    private void regenerateDecorations() {
        decorationPlacements.clear();
        if (map == null) {
            return;
        }

        Random random = new Random();

        List<Position> candidates = new ArrayList<>();
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Position pos = new Position(x, y);
                if (map.getTile(pos) == TileType.GRASS && !map.isCastleFootprint(pos)) {
                    candidates.add(pos);
                }
            }
        }

        Collections.shuffle(candidates, random);
        int count = Math.min(DECORATION_TARGET_COUNT, candidates.size());
        for (int i = 0; i < count; i++) {
            Position pos = candidates.get(i);
            int variant = random.nextInt(textureManager.getDecorationTextures().length);
            decorationPlacements.add(new DecorationPlacement(pos, variant));
        }
    }

    /**
     * Converts screen coordinates to map grid position.
     * Used for input handling (click detection on towers).
     */
    public Position screenToMapPosition(int screenX, int screenY) {
        return mapRenderer.screenToMapPosition(screenX, screenY, Gdx.graphics.getHeight());
    }

    /**
     * Handles clicks on the map and tower UI.
     */
    private void handleMapClick(int screenX, int screenY) {
        // First, check if click is on the tower UI menu
        if (towerUIManager.isUIVisible()) {
            CombatTowerType selectedType = towerUIManager.handleUIClick(screenX, screenY, Gdx.graphics.getHeight(), Gdx.graphics.getWidth());
            if (selectedType != null) {
                // Place the selected tower on the map
                Position towerPos = towerUIManager.getSelectedTowerPosition();
                if (towerPos != null) {
                    Tower tower = new Tower(towerPos, selectedType);
                    placedTowers.put(towerPos, tower);
                }
            }
            towerUIManager.closeTowerMenu();
            return;
        }

        // Otherwise, check if click is on a tower spot on the map
        Position clickPos = screenToMapPosition(screenX, screenY);
        if (clickPos != null && map.isInside(clickPos)) {
            if (map.getTile(clickPos) == TileType.TOWER_SPOT) {
                // Open tower selection menu
                towerUIManager.openTowerMenu(clickPos);
            }
        }
    }
}
