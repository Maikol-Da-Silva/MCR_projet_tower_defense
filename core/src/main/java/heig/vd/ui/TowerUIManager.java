package heig.vd.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import heig.vd.Tower.CombatTowerType;
import heig.vd.render.TextureManager;
import heig.vd.utils.Position;


/**
 * Manages the tower selection UI.
 * Displays available combat towers and handles tower placement on click.
 */
public class TowerUIManager {
    private static final int GRID_COLS = 3;
    private static final int GRID_ROWS = 2;
    private static final int CELL_SIZE = 90;
    private static final int PADDING = 50;
    private static final int UI_WIDTH = GRID_COLS * CELL_SIZE + (GRID_COLS + 1) * PADDING;
    private static final int UI_HEIGHT = GRID_ROWS * CELL_SIZE + (GRID_ROWS + 1) * PADDING + 30;

    private Texture bgTexture; // simple white texture for background
    private BitmapFont font;

    private Position selectedTowerPosition;
    private CombatTowerType selectedTowerType;
    private boolean isUIVisible = false;

    public TowerUIManager() {
        createBackgroundTexture();
        font = new BitmapFont();
    }

    /**
     * Creates a simple white 1x1 texture for rectangle drawing.
     */
    private void createBackgroundTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        bgTexture = new Texture(pixmap);
        bgTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        pixmap.dispose();
    }

    /**
     * Opens the tower selection UI for the given map position.
     */
    public void openTowerMenu(Position towerPosition) {
        this.selectedTowerPosition = towerPosition;
        this.selectedTowerType = null;
        this.isUIVisible = true;
    }

    /**
     * Closes the tower selection UI.
     */
    public void closeTowerMenu() {
        this.isUIVisible = false;
        this.selectedTowerPosition = null;
    }

    /**
     * Handles click on the UI (screen coordinates).
     * Returns the selected CombatTowerType if a tower was clicked, null otherwise.
     */
    public CombatTowerType handleUIClick(int screenX, int screenY, int screenHeight, int screenWidth) {
        if (!isUIVisible) {
            return null;
        }

        // Convert screen coordinates to UI-local coordinates
        int uiStartX = (screenWidth - UI_WIDTH) / 2;
        int uiStartY = (screenHeight - UI_HEIGHT) / 2;
        int localX = screenX - uiStartX;
        int localY = screenY - uiStartY;

        // If click is outside UI bounds, close menu
        if (localX < 0 || localX >= UI_WIDTH || localY < 0 || localY >= UI_HEIGHT) {
            closeTowerMenu();
            return null;
        }

        // Determine which cell was clicked
        CombatTowerType[] towers = CombatTowerType.values();
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                int towerIndex = row * GRID_COLS + col;
                if (towerIndex >= towers.length) {
                    break;
                }

                int cellX = PADDING + col * (CELL_SIZE + PADDING);
                int invertedRow = (GRID_ROWS - 1) - row;
                int cellY = PADDING + invertedRow * (CELL_SIZE + PADDING);

                if (localX >= cellX && localX < cellX + CELL_SIZE &&
                    localY >= cellY && localY < cellY + CELL_SIZE) {
                    selectedTowerType = towers[towerIndex];
                    return selectedTowerType;
                }
            }
        }

        return null;
    }

    /**
     * Draws the tower selection UI.
     */
    public void drawTowerMenu(SpriteBatch batch, int screenWidth, int screenHeight, TextureManager textureManager) {
        if (!isUIVisible) {
            return;
        }

        // Center the UI on screen
        int startX = (screenWidth - UI_WIDTH) / 2;
        int startY = (screenHeight - UI_HEIGHT) / 2;

        // Draw semi-transparent background
        batch.setColor(0.1f, 0.1f, 0.1f, 0.9f);
        batch.draw(bgTexture, startX, startY, UI_WIDTH, UI_HEIGHT);
        batch.setColor(1, 1, 1, 1);

        // Draw title
        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Select Tower Type", startX + PADDING, startY + UI_HEIGHT - 10);

        // Draw tower cells
        CombatTowerType[] towers = CombatTowerType.values();
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                int towerIndex = row * GRID_COLS + col;
                if (towerIndex >= towers.length) {
                    break;
                }

                int cellX = startX + PADDING + col * (CELL_SIZE + PADDING);
                int cellY = startY + PADDING + row * (CELL_SIZE + PADDING) + 30;

                CombatTowerType towerType = towers[towerIndex];
                Texture texture = textureManager.getCombatTowerTexture(towerType);

                // Draw cell border
                batch.setColor(0.5f, 0.5f, 0.5f, 0.8f);
                batch.draw(bgTexture, cellX, cellY, CELL_SIZE, CELL_SIZE);
                batch.setColor(1, 1, 1, 1);

                int spriteSize = CELL_SIZE - 20; // Leave more padding
                int spriteX = cellX + (CELL_SIZE - spriteSize) / 2;
                int spriteY = cellY + (CELL_SIZE - spriteSize) / 2;
                batch.draw(texture, spriteX, spriteY, spriteSize, spriteSize);


                // Draw tower name below the sprite
                font.setColor(1, 1, 1, 1);
                font.draw(batch, towerType.getDisplayName(), cellX, cellY - 5);
            }
        }
    }

    /**
     * Get the selected tower position (where to place the tower on map).
     */
    public Position getSelectedTowerPosition() {
        return selectedTowerPosition;
    }

    /**
     * Get the selected tower type.
     */
    public CombatTowerType getSelectedTowerType() {
        return selectedTowerType;
    }

    /**
     * Check if UI is currently visible.
     */
    public boolean isUIVisible() {
        return isUIVisible;
    }

    /**
     * Dispose all loaded textures.
     */
    public void dispose() {
        if (bgTexture != null) {
            bgTexture.dispose();
        }
        if (font != null) {
            font.dispose();
        }
    }
}






