/**
 * Created by danielmacario on 14-11-05.
 */
package GameObject;

import javax.swing.*;
import java.io.Serializable;

/**
 * Used to create the powerUp object hidden in every non-bonus stage during gameplay. We group the entire
 * functionality of all powerUps within a single class instead of having 8 different classes with very similar
 * attributes. We decide on the image that will be rendered and whether the powerUp is kept after death based on
 * the PowerUpType passed. The type is stored so that we can enable the specific functionality of the powerUp once
 * the player has collided with it.
 */
public class PowerUp extends StaticObject implements Serializable {

    private PowerUpType powerUpType;
    private boolean keptAfterDeath;

    /**
     * Initialize a new powerUp object. The image to be rendered depends on the
     * powerUpType passed.
     * @param powerUpType The type specifying which powerUp the constructor must create.
     */
    public PowerUp(PowerUpType powerUpType, int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        this.visible = true;
        this.firstCollision = true;
        this.powerUpType = powerUpType;

        switch (powerUpType) {
            case BOMBPASS:
                imagePath = "/res/image/PowerUps/bombpass.png";
                keptAfterDeath = false;
                break;
            case BOMBS:
                imagePath = "/res/image/PowerUps/bombs.png";
                keptAfterDeath = true;
                break;
            case DETONATOR:
                imagePath = "/res/image/PowerUps/detonator.png";
                keptAfterDeath = false;
                break;
            case FLAMEPASS:
                imagePath = "/res/image/PowerUps/flamepass.png";
                keptAfterDeath = false;
                break;
            case FLAMES:
                imagePath = "/res/image/PowerUps/flames.png";
                keptAfterDeath = true;
                break;
            case MYSTERY:
                imagePath = "/res/image/PowerUps/mystery.png";
                //kept after death doesn't apply to this powerup
                break;
            case SPEED:
                imagePath = "/res/image/PowerUps/speed.png";
                keptAfterDeath = true;
                break;
            case WALLPASS:
                imagePath = "/res/image/PowerUps/wallpass.png";
                keptAfterDeath = false;
                break;
        }
        this.image = new ImageIcon(this.getClass().getResource(imagePath)).getImage();
        this.width = image.getWidth(null);
        this.height = image.getHeight(null);
    }

    /**
     * Retrieve the powerUp type used to create this instance of powerUp object. Used when the player collides
     * with the powerUp object to enable the particular logic associated with it.
     * @return PowerUpType is returned.
     */
    public PowerUpType getPowerUpType() {
        return powerUpType;
    }
}
