package org.ftcgame;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class RobotGame implements ApplicationListener {
    private SpriteBatch batch;
    private Texture robotTexture;
    private Texture obstacleTexture;

    private static final int ROBOT_SIZE = 64;
    private static final int OBSTACLE_SIZE = 64;
    private static final float ROBOT_SPEED = 250;

    private static final float ROBOT_START_X = 100;
    private static final float ROBOT_START_Y = 100;
    private static final float OBSTACLE_START_X = 500;
    private static final float OBSTACLE_START_Y = 200;

    private float robotX = ROBOT_START_X;
    private float robotY = ROBOT_START_Y;
    private float obstacleX = OBSTACLE_START_X;
    private float obstacleY = OBSTACLE_START_Y;

    @Override
    public void create() {
        batch = new SpriteBatch();
        robotTexture = new Texture("robot.png");
        obstacleTexture = new Texture("obstacles/spikes.png");
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your application here. The parameters represent the new window size.
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1);

        float previousRobotX = robotX;
        float previousRobotY = robotY;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            robotY = robotY + ROBOT_SPEED * deltaTime;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            robotY = robotY - ROBOT_SPEED * deltaTime;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            robotX = robotX - ROBOT_SPEED * deltaTime;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            robotX = robotX + ROBOT_SPEED * deltaTime;
        }

        if (robotX < obstacleX + OBSTACLE_SIZE &&
            robotX + ROBOT_SIZE > obstacleX &&
            robotY < obstacleY + OBSTACLE_SIZE &&
            robotY + ROBOT_SIZE > obstacleY) {
            if (previousRobotX + ROBOT_SIZE <= obstacleX) {
                robotX = obstacleX - ROBOT_SIZE;
            } else if (previousRobotX >= obstacleX + OBSTACLE_SIZE) {
                robotX = obstacleX + OBSTACLE_SIZE;
            } else if (previousRobotY + ROBOT_SIZE <= obstacleY) {
                robotY = obstacleY - ROBOT_SIZE;
            } else if (previousRobotY >= obstacleY + OBSTACLE_SIZE) {
                robotY = obstacleY + OBSTACLE_SIZE;
            }
        }

        batch.begin();
        batch.draw(obstacleTexture, obstacleX, obstacleY, OBSTACLE_SIZE, OBSTACLE_SIZE);
        batch.draw(robotTexture, robotX, robotY, ROBOT_SIZE, ROBOT_SIZE);
        batch.end();
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        batch.dispose();
        robotTexture.dispose();
        obstacleTexture.dispose();
    }
}
