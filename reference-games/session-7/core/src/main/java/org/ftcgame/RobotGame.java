package org.ftcgame;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class RobotGame implements ApplicationListener {
    private SpriteBatch batch;
    private Texture robotTexture;
    private Texture obstacleTexture;
    private Texture ballTexture;
    private Texture goalTexture;
    private BitmapFont font;

    private static final int ROBOT_SIZE = 64;
    private static final int OBSTACLE_SIZE = 64;
    private static final int GOAL_SIZE = 128;
    private static final float GOAL_TOP_MARGIN = 140;
    private static final float HUD_X = 20;
    private static final float SCORE_TOP_MARGIN = 20;
    private static final float AMMO_TOP_MARGIN = 45;
    private static final float ROBOT_SPEED = 250;
    private static final float ROBOT_ROTATION_SPEED = 180;

    private static final float ROBOT_START_X = 100;
    private static final float ROBOT_START_Y = 100;
    private static final float OBSTACLE_START_X = 500;
    private static final float OBSTACLE_START_Y = 200;

    private float robotX = ROBOT_START_X;
    private float robotY = ROBOT_START_Y;
    private float robotRotation = 0;
    private float obstacleX = OBSTACLE_START_X;
    private float obstacleY = OBSTACLE_START_Y;
    private float goalX;
    private float goalY;

    private ArrayList<Vector2> balls;
    private ArrayList<Projectile> projectiles;
    private int score = 0;
    private int ammo = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();
        robotTexture = new Texture("robot.png");
        obstacleTexture = new Texture("obstacles/spikes.png");
        ballTexture = new Texture("balls/ball.png");
        goalTexture = new Texture("goals/goal.png");
        font = new BitmapFont();

        goalX = (Gdx.graphics.getWidth() - GOAL_SIZE) / 2;
        goalY = Gdx.graphics.getHeight() - GOAL_TOP_MARGIN;

        balls = new ArrayList<>();
        balls.add(new Vector2(250, 100));
        balls.add(new Vector2(250, 350));
        balls.add(new Vector2(700, 350));

        projectiles = new ArrayList<>();
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

        moveRobot(deltaTime);

        if (isRobotTouching(obstacleX, obstacleY, OBSTACLE_SIZE)) {
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && ammo > 0) {
            projectiles.add(new Projectile(
                robotX + (ROBOT_SIZE - Projectile.SIZE) / 2,
                robotY + ROBOT_SIZE
            ));
            ammo = ammo - 1;
        }

        collectBalls();
        updateProjectiles(deltaTime);

        batch.begin();
        drawBalls();
        drawProjectiles();
        batch.draw(goalTexture, goalX, goalY, GOAL_SIZE, GOAL_SIZE);
        batch.draw(obstacleTexture, obstacleX, obstacleY, OBSTACLE_SIZE, OBSTACLE_SIZE);
        batch.draw(
            robotTexture,
            robotX, robotY,
            ROBOT_SIZE / 2, ROBOT_SIZE / 2,
            ROBOT_SIZE, ROBOT_SIZE,
            1, 1,
            robotRotation,
            0, 0,
            robotTexture.getWidth(), robotTexture.getHeight(),
            false, false
        );
        font.draw(batch, "Score: " + score, HUD_X, Gdx.graphics.getHeight() - SCORE_TOP_MARGIN);
        font.draw(batch, "Ammo: " + ammo, HUD_X, Gdx.graphics.getHeight() - AMMO_TOP_MARGIN);
        batch.end();
    }

    private boolean isRobotTouching(float objectX, float objectY, float objectSize) {
        return robotX < objectX + objectSize &&
            robotX + ROBOT_SIZE > objectX &&
            robotY < objectY + objectSize &&
            robotY + ROBOT_SIZE > objectY;
    }

    private void collectBalls() {
        for (int i = balls.size() - 1; i >= 0; i = i - 1) {
            Vector2 ball = balls.get(i);

            if (isRobotTouching(ball.x, ball.y, Projectile.SIZE)) {
                balls.remove(i);
                ammo = ammo + 1;
            }
        }
    }

    private void drawBalls() {
        for (int i = 0; i < balls.size(); i = i + 1) {
            Vector2 ball = balls.get(i);
            batch.draw(ballTexture, ball.x, ball.y, Projectile.SIZE, Projectile.SIZE);
        }
    }

    private void updateProjectiles(float deltaTime) {
        for (int i = projectiles.size() - 1; i >= 0; i = i - 1) {
            Projectile projectile = projectiles.get(i);
            projectile.update(deltaTime);

            if (projectile.isTouching(goalX, goalY, GOAL_SIZE)) {
                projectiles.remove(i);
                score = score + 1;
            } else if (projectile.isAbove(Gdx.graphics.getHeight())) {
                projectiles.remove(i);
            }
        }
    }

    private void drawProjectiles() {
        for (int i = 0; i < projectiles.size(); i = i + 1) {
            Projectile projectile = projectiles.get(i);
            batch.draw(
                ballTexture,
                projectile.getX(), projectile.getY(),
                Projectile.SIZE, Projectile.SIZE
            );
        }
    }

    private void moveRobot(float deltaTime) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            robotRotation = robotRotation + ROBOT_ROTATION_SPEED * deltaTime;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            robotRotation = robotRotation - ROBOT_ROTATION_SPEED * deltaTime;
        }

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
        ballTexture.dispose();
        goalTexture.dispose();
        font.dispose();
    }
}
