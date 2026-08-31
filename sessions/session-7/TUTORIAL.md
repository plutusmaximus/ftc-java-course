# Session 7 — Rotate the Robot

In this session, you will rotate the robot. You will add its angle, turn it with the arrow keys, and draw it at the stored rotation.

Fired balls will keep traveling toward the top of the screen for now, even when the robot faces another direction. That mismatch will give us a clear problem to solve in a later session.

Keep using this cycle:

```text
predict -> edit -> run -> observe
```

When surrounding code is shown, copy only the code in the first, copy-only block. The second block shows where that code belongs.

## 1. Start from your working game

Use Android Studio to pull any instructor updates, then open:

```text
core/src/main/java/org/ftcgame/RobotGame.java
```

Run the game and confirm that:

- W/A/S/D move the robot in screen directions;
- touching the obstacle stops the robot flush against the approached edge;
- collecting balls increases ammunition;
- the Space key fires one ball per press when ammunition is available;
- fired balls travel upward and can score in the goal.

Stop the game before editing.

> **Checkpoint:** The completed Session 6 game runs correctly.

## 2. Move robot movement code into a method

`render()` currently checks W, S, A, and D directly. This method also handles firing, collisions, projectiles, and drawing. Before making movement more capable, move the four movement checks into a method with a clear name.

Add this entire method after `drawProjectiles()` and before `pause()`:

```java
private void moveRobot(float deltaTime) {
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
```

The new method should sit between the projectile drawing method and `pause()` like this:

```java
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
    // The four movement checks are here.
}

@Override
public void pause() {
```

Now remove the original four W/A/S/D `if` statements from `render()`. In their place, immediately after the two previous-position variables, add this method call:

```java
moveRobot(deltaTime);
```

The beginning of `render()` should now look like this:

```java
float deltaTime = Gdx.graphics.getDeltaTime();

ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1);

float previousRobotX = robotX;
float previousRobotY = robotY;

moveRobot(deltaTime);

if (isRobotTouching(obstacleX, obstacleY, OBSTACLE_SIZE)) {
```

The `deltaTime` parameter gives the new method the same frame-time value that the old movement statements used. The previous-position variables remain outside the method because the obstacle response uses them immediately after movement. This change should organize the code without changing what the game does.

### Run it now

Before running, predict whether the controls should feel different. Then test all four movement keys.

They should behave exactly as they did before. If they do not, compare the method and its call with the old code before continuing.

> **Checkpoint:** `moveRobot(deltaTime)` contains all four movement checks, and movement still behaves like Session 6.

## 3. Store the robot's angle

The robot already stores its position. It now needs to remember its angle, and the game needs a named setting for its turning speed.

Add this constant with the other robot constants:

```java
private static final float ROBOT_ROTATION_SPEED = 180;
```

Your robot constants should now include these settings:

```java
private static final float ROBOT_SPEED = 250;
private static final int ROBOT_SIZE = 64;
private static final float ROBOT_ROTATION_SPEED = 180;
```

Then add the robot's rotation immediately below `robotY`:

```java
private float robotRotation = 0;
```

The robot's position and rotation fields should now look like this:

```java
private float robotX = ROBOT_START_X;
private float robotY = ROBOT_START_Y;
private float robotRotation = 0;
```

The robot artwork points toward the top of the screen, so `0` degrees means that the robot is facing up. `ROBOT_ROTATION_SPEED` is measured in degrees per second. Its value makes the robot turn halfway around in one second.

The rotation speed is a constant because it stays fixed while the game runs. The angle remains a variable because it changes while the game runs.

## 4. Turn with the arrow keys

Add these two input checks at the beginning of `moveRobot(...)`, before the W-key check:

```java
if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
    robotRotation = robotRotation + ROBOT_ROTATION_SPEED * deltaTime;
}

if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
    robotRotation = robotRotation - ROBOT_ROTATION_SPEED * deltaTime;
}
```

The movement method should now begin like this:

```java
private void moveRobot(float deltaTime) {
    if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
        robotRotation = robotRotation + ROBOT_ROTATION_SPEED * deltaTime;
    }

    if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
        robotRotation = robotRotation - ROBOT_ROTATION_SPEED * deltaTime;
    }

    if (Gdx.input.isKeyPressed(Input.Keys.W)) {
```

libGDX treats positive drawing angles as counterclockwise. Adding to the angle turns left, while subtracting turns right.

Multiplying by `deltaTime` makes turning frame-rate independent, just like translation. The stored angle does not need to stay between 0 and 360. After one complete counterclockwise turn, `360` points in the same direction as `0`; after a clockwise turn, `-360` does too.

Do not run yet. The keys can change `robotRotation`, but the current drawing call does not use it.

## 5. Draw the robot at its stored angle

Find this line in the drawing section:

```java
batch.draw(robotTexture, robotX, robotY, ROBOT_SIZE, ROBOT_SIZE);
```

Replace that one line with this complete drawing call:

```java
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
```

The robot drawing should remain between the obstacle and the score like this:

```java
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
```

This is a longer version of `batch.draw(...)` that can rotate an image.

The libGDX method is declared with a type and name for each argument. This declaration is shown only as a reference; do not copy it into `RobotGame`:

```java
public void draw(
    Texture texture,
    float x, float y,
    float originX, float originY,
    float width, float height,
    float scaleX, float scaleY,
    float rotation,
    int srcX, int srcY,
    int srcWidth, int srcHeight,
    boolean flipX, boolean flipY
) {
    // libGDX's drawing code is inside this method.
}
```

The arguments in the robot's drawing call tell libGDX:

- draw `robotTexture` at `(robotX, robotY)`;
- rotate around the center (origin) of the image;
- display the image using `ROBOT_SIZE` for its width and height;
- keep its horizontal and vertical scale at `1`;
- rotate by `robotRotation` degrees;
- select the complete source image using `0, 0`, `robotTexture.getWidth()`, and `robotTexture.getHeight()`;
- do not flip the image.

`src` means **source**. `srcX` and `srcY` identify where the selected area begins inside the texture, while `srcWidth` and `srcHeight` specify how many texture pixels to select. Starting at `(0, 0)` and using the texture's complete width and height selects the entire robot image. libGDX then scales that selected image to the displayed width and height set by `ROBOT_SIZE`.

The origin coordinates are measured within the robot's drawing area. Dividing `ROBOT_SIZE` by `2` finds the center on each axis. Rotating around that point keeps the robot centered in the same place while it turns.

### Run it now

Run the game and hold Left Arrow. The robot should turn counterclockwise. Hold Right Arrow, and it should turn clockwise.

W/A/S/D still move in screen directions. Turn the robot sideways and press W. Predict what will happen before pressing it.

The image turns, but W still moves toward the top of the screen. Drawing rotation and movement direction are separate parts of the program. We have fixed the first one and will fix the second one next.

> **Checkpoint:** The arrow keys rotate the robot image smoothly around its center, but W/A/S/D are still screen-aligned.

## 6. Test the complete game

Before saving your work, verify every required behavior:

1. Left Arrow rotates the robot counterclockwise.
2. Right Arrow rotates the robot clockwise.
3. Rotation remains smooth and uses `ROBOT_ROTATION_SPEED * deltaTime`.
4. The robot image turns around its center.
5. W/A/S/D remain screen-aligned.
6. Movement still uses `ROBOT_SPEED * deltaTime`.
7. `moveRobot(deltaTime)` contains the robot input and movement calculations.
8. The obstacle still stops the robot flush against the approached edge.
9. Collecting balls, ammunition, scoring, and projectile removal still work.
10. Fired balls still start as if the robot were facing up and travel screen-upward.

Ask for help if any check fails. Fix the game and repeat the checklist before committing.

## 7. Commit and push

Use Android Studio's Git tools:

1. Open the Commit window.
2. Confirm that `RobotGame.java` is the only Java file changed.
3. Review the highlighted changes.
4. Enter this commit message:

   ```text
   Add robot rotation
   ```

5. Commit the changes.
6. Push the commit to your assigned GitHub repository.

> **Final checkpoint:** The finished game works, and the `Add robot rotation` commit has been pushed.
