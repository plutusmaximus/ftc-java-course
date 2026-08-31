package org.ftcgame;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class RobotGame implements ApplicationListener {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
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
    private float frontLeftPower = 0;
    private float frontRightPower = 0;
    private float backLeftPower = 0;
    private float backRightPower = 0;
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
        shapeRenderer = new ShapeRenderer();
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

        updateWheelPowers();

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
            double angleRadians = Math.toRadians(robotRotation);
            double sine = Math.sin(angleRadians);
            double cosine = Math.cos(angleRadians);

            double directionX = 0;
            double directionY = 0;

            directionX = directionX - sine;
            directionY = directionY + cosine;

            double projectileStartX = robotX + ROBOT_SIZE / 2
                + directionX * ROBOT_SIZE / 2
                - Projectile.SIZE / 2;
            double projectileStartY = robotY + ROBOT_SIZE / 2
                + directionY * ROBOT_SIZE / 2
                - Projectile.SIZE / 2;

            projectiles.add(new Projectile(
                (float) projectileStartX,
                (float) projectileStartY,
                directionX,
                directionY
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

        if (Gdx.input.isKeyPressed(Input.Keys.H)) {
            drawHitboxes();
        }
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

            if (projectile.isTouching(obstacleX, obstacleY, OBSTACLE_SIZE)) {
                projectiles.remove(i);
            } else if (projectile.isTouching(goalX, goalY, GOAL_SIZE)) {
                projectiles.remove(i);
                score = score + 1;
            } else if (projectile.isOutside(
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight()
            )) {
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

    private void updateWheelPowers() {
        float axial = 0;
        float lateral = 0;
        float yaw = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            axial += 1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            axial -= 1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            lateral -= 1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            lateral += 1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            yaw += 1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            yaw -= 1;
        }

        frontLeftPower = axial + lateral - yaw;
        frontRightPower = axial - lateral + yaw;
        backLeftPower = axial - lateral - yaw;
        backRightPower = axial + lateral + yaw;
    }

    private void moveRobot(float deltaTime) {
        double axialMovement = (
            frontLeftPower + frontRightPower
                + backLeftPower + backRightPower
        ) / 4;
        double lateralMovement = (
            frontLeftPower - frontRightPower
                - backLeftPower + backRightPower
        ) / 4;
        double yawMovement = (
            -frontLeftPower + frontRightPower
                - backLeftPower + backRightPower
        ) / 4;

        robotRotation = (float) (
            robotRotation + yawMovement * ROBOT_ROTATION_SPEED * deltaTime
        );

        double angleRadians = Math.toRadians(robotRotation);
        double sine = Math.sin(angleRadians);
        double cosine = Math.cos(angleRadians);

        double directionX = -sine * axialMovement
            + cosine * lateralMovement;
        double directionY = cosine * axialMovement
            + sine * lateralMovement;

        robotX = (float) (robotX + directionX * ROBOT_SPEED * deltaTime);
        robotY = (float) (robotY + directionY * ROBOT_SPEED * deltaTime);
    }

    private void drawHitboxes() {
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(robotX, robotY, ROBOT_SIZE, ROBOT_SIZE);

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(obstacleX, obstacleY, OBSTACLE_SIZE, OBSTACLE_SIZE);

        shapeRenderer.end();
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
        shapeRenderer.dispose();
        robotTexture.dispose();
        obstacleTexture.dispose();
        ballTexture.dispose();
        goalTexture.dispose();
        font.dispose();
    }
}
