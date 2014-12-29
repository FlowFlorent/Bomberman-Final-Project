package GameObject;

import GamePlay.GamePlayState;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class PlayerTest {

    private Player player;

    @Before
    public void makePlayer() {
        player = new Player(35, 35, true, MovableObject.NORMALSPEED);
    }

    @Test
    public void testDeath() throws Exception {
        assertTrue("Player is Visible upon creation", player.isVisible());
        player.death();
        assertFalse("Player is no longer visible after dying", player.isVisible());
    }

    @Test
    public void testDetonateLastBomb() throws Exception {
        ArrayList<Bomb> bombsPlaced = new ArrayList<Bomb>();
        Bomb oldBomb = new Bomb(32, 32);
        bombsPlaced.add(oldBomb);
        Bomb newBomb = new Bomb(64, 64);
        bombsPlaced.add(newBomb);
        player.setBombsPlaced(bombsPlaced);
        assertTrue("Bombs created have visibility set to true", oldBomb.isVisible() && newBomb.isVisible());
        player.detonateLastBomb();
        assertFalse("Visibility of the oldest bomb placed is set to false upon calling detonateLastBomb", bombsPlaced.get(0).isVisible());
        assertTrue("Remaining bombs are still visible", bombsPlaced.get(1).isVisible());
        //If the player has an empty ArrayList, then we expect that it will not throw an exception.
        player.setBombsPlaced(new ArrayList<Bomb>());
        player.detonateLastBomb();
    }

    @Test
    public void testIncrementSpeed() throws Exception {
        assertEquals("Upon spawning, the player speed is set to NORMALSPEED", MovableObject.NORMALSPEED, player.getSpeed());
        player.incrementSpeed();
        assertEquals("Calling increment speed sets the speed of the player to FASTSPEED", MovableObject.FASTSPEED, player.getSpeed());
        player.incrementSpeed();
        assertEquals("Calling increment speed again does not modify the attribute", MovableObject.FASTSPEED, player.getSpeed());
    }

    @Test
    public void testDecrementLifesRemaining() throws Exception {
        int livesRemaining = player.getLivesRemaining();
        GamePlayState gamePlayState = player.getCurrentGamePlayState();

        assertEquals("Upon spawning, the player has 3 lives", 3, livesRemaining);
        assertEquals("Upon spawning, the current state is set to in game", GamePlayState.INGAME, gamePlayState);

        player.decrementLivesRemaining();

        assertEquals("Decrement lives remaining reduces lives by one", livesRemaining - 1, player.getLivesRemaining());

        player.decrementLivesRemaining();
        player.decrementLivesRemaining();
        player.decrementLivesRemaining();

        gamePlayState = player.getCurrentGamePlayState();
        assertEquals("Negative lives remaining sets the current state to game over", GamePlayState.GAMEOVER, gamePlayState);
    }

    @Test
    public void testEnablePowerUp() throws Exception {
        TileMap tileMap = new TileMap(player, 1, "test name");
        player.setTileMap(tileMap);
        assertFalse("BombPass is set to false when the player spawns", player.hasBombPass());
        assertFalse("BombP{ass is set to false when the player spawns", player.hasWallPass());
        assertFalse("FlamePass is set to false when the player spawns", player.hasFlamePass());
        assertFalse("Player does not have the detonator upon spawning", player.isDetonatorEnabled());
        assertFalse("Player does not have the detonator upon spawning", player.isDetonatorEnabled());
        assertFalse("Player is not invincible upon spawning", player.isInvincibilityEnabled());
        assertEquals("Player can only place one bomb at a time upon spawning", 1, player.getBombsAllowed());
        assertEquals("Radius of explosion is 1", 1, tileMap.getBombRadius());
        player.enablePowerUp(PowerUpType.BOMBPASS);
        assertTrue("BombPass is enabled", player.hasBombPass());
        player.enablePowerUp(PowerUpType.FLAMEPASS);
        assertTrue("FlamePass is enabled", player.hasFlamePass());
        player.enablePowerUp(PowerUpType.DETONATOR);
        assertTrue("Detonator is enabled", player.isDetonatorEnabled());
        player.enablePowerUp(PowerUpType.WALLPASS);
        assertTrue("WallPass is enabled", player.hasWallPass());
        player.enablePowerUp(PowerUpType.SPEED);
        assertEquals("Speed is enabled", MovableObject.FASTSPEED, player.getSpeed());
        player.enablePowerUp(PowerUpType.FLAMES);
        assertEquals("Bomb radius increases by one", 2, tileMap.getBombRadius());
        player.enablePowerUp(PowerUpType.BOMBS);
        assertEquals("Bombs allowed increases by 1", 2, player.getBombsAllowed());
        player.enablePowerUp(PowerUpType.MYSTERY);
        assertTrue("Invincibility enabled", player.isInvincibilityEnabled());
    }

    @Test
    public void testDisablePowerUpsOnDeath() throws Exception {
        player.setWallPass(true);
        player.setFlamePass(true);
        player.setBombPass(true);
        player.setDetonatorEnabled(true);
        player.disablePowerUpsOnDeath();
        assertEquals("Wall pass should be disabled", false, player.hasWallPass());
        assertEquals("Flame pass should be disabled", false, player.hasFlamePass());
        assertEquals("Bomb pass should be disabled", false, player.hasBombPass());
        assertEquals("Detonator should be disabled", false, player.isDetonatorEnabled());
    }

    @Test
    public void testNextStage() throws Exception {
        player.setPosX(100);
        player.setPosY(50);
        player.nextStage();
        assertEquals("X Position of the player is reset to 32", 32, player.getPosX());
        assertEquals("Y Position of the player is reset to 32", 32, player.getPosY());
    }

    @Test
    public void testKeyPressed() throws Exception {
        //disable the controls if the player is dead
        player.setVisible(false);
        player.keyPressed(KeyEvent.VK_UP);
        assertEquals("Delta Y value should not be modified if player is not visible", 0, player.getDeltaY());
        player.keyPressed(KeyEvent.VK_DOWN);
        assertEquals("Delta Y value should not be modified if player is not visible", 0, player.getDeltaY());
        player.keyPressed(KeyEvent.VK_LEFT);
        assertEquals("Delta X value should not be modified if player is not visible", 0, player.getDeltaX());
        player.keyPressed(KeyEvent.VK_RIGHT);
        assertEquals("Delta X value should not be modified if player is not visible", 0, player.getDeltaX());
        player.keyPressed(KeyEvent.VK_SPACE);
        assertEquals("Pause should not be allowed if player is not visible", GamePlayState.INGAME, player.getCurrentGamePlayState());
        player.setVisible(true);
        player.keyPressed(KeyEvent.VK_UP);
        assertEquals("Delta Y value should be modified if player is visible", -MovableObject.NORMALSPEED, player.getDeltaY());
        player.keyPressed(KeyEvent.VK_DOWN);
        assertEquals("Delta Y value should not be modified if player is not visible", MovableObject.NORMALSPEED, player.getDeltaY());
        player.keyPressed(KeyEvent.VK_LEFT);
        assertEquals("Delta X value should not be modified if player is not visible", -MovableObject.NORMALSPEED, player.getDeltaX());
        player.keyPressed(KeyEvent.VK_RIGHT);
        assertEquals("Delta X value should not be modified if player is not visible", MovableObject.NORMALSPEED, player.getDeltaX());
        player.keyPressed(KeyEvent.VK_SPACE);
        assertEquals("Pause should not be allowed if player is not visible", GamePlayState.PAUSE, player.getCurrentGamePlayState());

    }

    @Test
    public void testKeyReleased() throws Exception {
        player.setVisible(false);
        player.setDeltaX(2);
        player.setDeltaY(2);
        player.keyReleased(KeyEvent.VK_UP);
        assertEquals("Delta Y should be set to 0 upon release the up arrow", 0, player.getDeltaY());
        player.setDeltaY(2);
        player.keyReleased(KeyEvent.VK_DOWN);
        assertEquals("Delta Y value should be set to 0 upon releasing the down arrow", 0, player.getDeltaY());
        player.keyReleased(KeyEvent.VK_LEFT);
        assertEquals("Delta X value should be set to 0 upon releasing the left arrow", 0, player.getDeltaX());
        player.setDeltaX(2);
        player.keyReleased(KeyEvent.VK_RIGHT);
        assertEquals("Delta X value should be set to 0 upon releasing the right arrow", 0, player.getDeltaX());
    }

    @Test
    public void testIncrementBombsAllowed() throws Exception {
        int bombsAllowed = player.getBombsAllowed();
        player.incrementBombsAllowed();
        assertEquals("Bombs allowed increases by 1 upon calling the method", bombsAllowed + 1, player.getBombsAllowed());
        player.setBombsAllowed(10);
        player.incrementBombsAllowed();
        assertEquals("Bombs allowed will not increase beyond 10", 10, player.getBombsAllowed());
    }

    @Test
    public void testAddToScore() throws Exception {
        int score = player.getScore();
        int increment = 100;
        player.addToScore(increment);
        assertEquals("Score increases by specified amount", score + increment, player.getScore());
    }
}