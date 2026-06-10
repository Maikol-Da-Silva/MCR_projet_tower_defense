package heig.vd.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import heig.vd.tower.CombatTowerType;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages texture loading, disposal, and frame splitting.
 * Centralizes all texture resource handling.
 */
public class TextureManager {
    private Texture grassTexture;
    private Texture roadTexture;
    private TextureRegion[] roadVariants;

    private Texture castleTexture;
    private TextureRegion[] castleFrames;

    private Texture towerTexture;
    private TextureRegion[] towerFrames;

    private Texture[] decorationTextures;
    private Texture[] mobsTextures;
    private TextureRegion[][] mobsFrames;

    // Combat tower textures (loaded on demand)
    private Map<CombatTowerType, Texture> combatTowerTextures = new HashMap<>();

    // Projectile textures, one per tower type (loaded on demand)
    private Map<CombatTowerType, Texture> projectileTextures = new HashMap<>();

    /**
     * Loads all textures and splits animated sheets.
     * Called once during Screen.show().
     */
    public void loadAll() {
        grassTexture = loadTexture("Environment/Grass/spr_grass_02.png");

        roadTexture = loadTexture("Environment/Tile Set/spr_tile_set_ground.png");
        splitRoadTileset();

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

        mobsTextures = loadTextures(
            "Enemies/spr_bat.png",
            "Enemies/spr_big_slime.png",
            "Enemies/spr_demon.png",
            "Enemies/spr_ghost.png",
            "Enemies/spr_goblin.png",
            "Enemies/spr_king_slime.png",
            "Enemies/spr_normal_slime.png",
            "Enemies/spr_skeleton.png",
            "Enemies/spr_zombie.png"
        );

        //On initialise le tableau de frames pour tous les mobs
        mobsFrames = new TextureRegion[mobsTextures.length][];

        for (int i = 0; i < mobsTextures.length; i++) {

            mobsFrames[i] = new TextureRegion[4];
            mobsFrames[i] = splitAnimatedSheet(mobsTextures[i]);
        }
    }

    /**
     * Disposes all loaded textures.
     * Called during Screen.dispose().
     */
    public void disposeAll() {
        disposeTexture(grassTexture);
        disposeTexture(roadTexture);
        disposeTexture(castleTexture);
        disposeTexture(towerTexture);
        disposeTextures(decorationTextures);
        disposeTextures(mobsTextures);
        for (Texture texture : combatTowerTextures.values()) {
            disposeTexture(texture);
        }
        combatTowerTextures.clear();
        for (Texture texture : projectileTextures.values()) {
            disposeTexture(texture);
        }
        projectileTextures.clear();
    }

    // --- Getters for renderer use ---

    public Texture getGrassTexture() {
        return grassTexture;
    }

    public TextureRegion[] getRoadVariants() {
        return roadVariants;
    }

    public TextureRegion[] getCastleFrames() {
        return castleFrames;
    }

    public TextureRegion[] getTowerFrames() {
        return towerFrames;
    }

    public Texture[] getDecorationTextures() {
        return decorationTextures;
    }
    public Texture[] getMobsTextures() {return mobsTextures;}
    public TextureRegion[][] getMobsFrames() {return mobsFrames;}

    /**
     * Get the frames for a specific combat tower type.
     * Loads the texture on first access, then caches it.
     */
    public Texture getCombatTowerTexture(CombatTowerType towerType) {
        if (!combatTowerTextures.containsKey(towerType)) {
            loadCombatTowerTexture(towerType);
        }
        return combatTowerTextures.get(towerType);
    }

    /**
     * Loads and caches a combat tower texture.
     */
    private void loadCombatTowerTexture(CombatTowerType towerType) {
        try {
            Texture texture = loadTexture(towerType.getAssetPath());
            combatTowerTextures.put(towerType, texture);
        } catch (Exception e) {
            System.err.println("Failed to load combat tower texture: " + towerType.getAssetPath());
        }
    }

    /**
     * Get the projectile texture for a specific combat tower type.
     * Loads the texture on first access, then caches it.
     */
    public Texture getProjectileTexture(CombatTowerType towerType) {
        if (!projectileTextures.containsKey(towerType)) {
            loadProjectileTexture(towerType);
        }
        return projectileTextures.get(towerType);
    }

    /**
     * Loads and caches a projectile texture.
     */
    private void loadProjectileTexture(CombatTowerType towerType) {
        try {
            Texture texture = loadTexture(towerType.getProjectileAssetPath());
            projectileTextures.put(towerType, texture);
        } catch (Exception e) {
            System.err.println("Failed to load projectile texture: " + towerType.getProjectileAssetPath());
        }
    }

    // --- Private helpers ---

    private void splitRoadTileset() {
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
}

