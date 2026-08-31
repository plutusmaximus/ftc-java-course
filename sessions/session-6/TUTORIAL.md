# Session 6 — Shoot into a Goal

In this session, you will add a goal for the projectiles. Successful shots increase the score, while missed projectiles disappear after leaving the window.

Keep using this cycle:

```text
predict -> edit -> run -> observe
```

## 1. Start from your working game

Use Android Studio to pull any instructor updates, then open:

```text
core/src/main/java/org/ftcgame/RobotGame.java
```

Run the game and confirm that balls become ammunition and the Space key fires independently moving projectiles upward.

> **Checkpoint:** The completed Session 5 game runs correctly.

## 2. Prepare the goal

The project includes `assets/goals/goal.png`. The game will use constants for the goal's width and height and for its distance from the top of the window.

Add a goal texture immediately below `ballTexture`.

```java
private Texture goalTexture;
```

**The texture variables should now look like this:**

```java
private Texture obstacleTexture;
private Texture ballTexture;
private Texture goalTexture;
private BitmapFont font;
```

Add these constants with the other size and layout constants:

```java
private static final int GOAL_SIZE = 128;
private static final float GOAL_TOP_MARGIN = 140;
```

The size and layout constants should now include:

```java
private static final int ROBOT_SIZE = 64;
private static final int OBSTACLE_SIZE = 64;
private static final int GOAL_SIZE = 128;
private static final float GOAL_TOP_MARGIN = 140;
```

Add goal coordinate variables immediately below the obstacle coordinates.

```java
private float goalX;
private float goalY;
```

**After adding them, the coordinate variables should look like this:**

```java
private float obstacleX = OBSTACLE_START_X;
private float obstacleY = OBSTACLE_START_Y;
private float goalX;
private float goalY;

private ArrayList<Vector2> balls;
```

Load the texture in `create()` immediately after loading the ball texture.

```java
goalTexture = new Texture("goals/goal.png");
```

**The resource-loading lines should now look like this:**

```java
obstacleTexture = new Texture("obstacles/spikes.png");
ballTexture = new Texture("balls/ball.png");
goalTexture = new Texture("goals/goal.png");
font = new BitmapFont();
```

Set the goal position immediately after loading the font.

```java
goalX = (Gdx.graphics.getWidth() - GOAL_SIZE) / 2;
goalY = Gdx.graphics.getHeight() - GOAL_TOP_MARGIN;
```

**After setting the position, this part of `create()` should look like this:**

```java
goalTexture = new Texture("goals/goal.png");
font = new BitmapFont();

goalX = (Gdx.graphics.getWidth() - GOAL_SIZE) / 2;
goalY = Gdx.graphics.getHeight() - GOAL_TOP_MARGIN;

balls = new ArrayList<>();
```

`getWidth()` and `getHeight()` report the window dimensions. Subtracting `GOAL_SIZE` before dividing centers the goal. The second line uses `GOAL_TOP_MARGIN` to leave space below the top.

Draw the goal immediately before drawing the obstacle.

```java
batch.draw(goalTexture, goalX, goalY, GOAL_SIZE, GOAL_SIZE);
```

**The drawing section should now include these lines:**

```java
drawProjectiles();
batch.draw(goalTexture, goalX, goalY, GOAL_SIZE, GOAL_SIZE);
batch.draw(obstacleTexture, obstacleX, obstacleY, OBSTACLE_SIZE, OBSTACLE_SIZE);
batch.draw(robotTexture, robotX, robotY, ROBOT_SIZE, ROBOT_SIZE);
```

Add the goal cleanup call in `dispose()` immediately after disposing the ball texture.

```java
goalTexture.dispose();
```

**Your cleanup code should now look like this:**

```java
obstacleTexture.dispose();
ballTexture.dispose();
goalTexture.dispose();
font.dispose();
```

### Run it now

Run the game. A goal should appear centered near the top of the window. Collect and fire a ball toward it. The ball should pass over the goal without scoring because collision behavior has not been added yet.

> **Checkpoint:** The goal is centered near the top of the window.

## 3. Score shots and remove missed projectiles

To score a shot, the game must know when a projectile touches the goal. It must also know when a missed projectile has traveled beyond the top of the window so it can remove it. These checks use the projectile's own position, so `Projectile` will provide methods that answer both questions.

Open `Projectile.java`. Add these methods immediately after `getY()`:

```java
public boolean isTouching(float objectX, float objectY, float objectSize) {
    return x < objectX + objectSize &&
        x + SIZE > objectX &&
        y < objectY + objectSize &&
        y + SIZE > objectY;
}

public boolean isAbove(float height) {
    return y > height;
}
```

The end of `Projectile` should now look like this:

```java
public float getY() {
    return y;
}

public boolean isTouching(float objectX, float objectY, float objectSize) {
    return x < objectX + objectSize &&
        x + SIZE > objectX &&
        y < objectY + objectSize &&
        y + SIZE > objectY;
}

public boolean isAbove(float height) {
    return y > height;
}
}
```

`isTouching(...)` reports whether the projectile's collision box overlaps an object. `isAbove(...)` reports whether the projectile has passed a height.

The `boolean` return type says that each method sends back either `true` or `false`.

Return to `RobotGame.java`.

`updateProjectiles(...)` must now remove projectiles that score or leave the window. Change it to the backward-loop pattern from `collectBalls()` so removal cannot skip an unchecked projectile. After moving each projectile, check the goal and then the top of the window.

Replace the complete `updateProjectiles(...)` method with this version:

```java
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
```

**The update method should remain separate from the drawing method:**

```java
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
```

`else if` means “otherwise, check this condition.” The window-height condition runs only when the projectile did not score, preventing a second removal after the collection has changed.

This loop moves backward for the same reason as the Session 4 collection loop: removing one object cannot cause an unchecked object to be skipped.

`updateProjectiles(...)` changes or removes objects; `drawProjectiles()` draws those that remain.

The projectile asks whether its collision box touches the goal's collision box. A successful projectile is removed and adds one point.

A missed projectile is removed after its lower edge passes above the current window height. Removing off-screen objects prevents the collection from growing forever.

### Run it now

Collect a ball and fire from beneath the goal. It should disappear and raise the score to `1`. Fire another away from the goal; it should leave the window without scoring.

Line up a shot so the ball travels through the obstacle. Predict what will happen, then press the Space key and observe it.

The projectile should continue through the obstacle because this session checks projectiles only against the goal and window edge. `isRobotTouching(...)` affects only the robot.

> **Checkpoint:** Successful shots score once; missed shots disappear without scoring.

## 4. Test the complete game

Finish by testing the remaining cases:

1. Press the Space key with zero ammunition. No projectile should appear.
2. Collect one ball. Ammunition should increase by one while score remains unchanged.
3. Hold the Space key after collecting a ball. Only one projectile should be created.
4. Fire two collected balls from different horizontal positions. Both should move independently.
5. Touch the obstacle from several sides. The robot should still stop flush against each approached edge without changing score or ammunition.

If projectile behavior is difficult to follow, add this temporary log inside `updateProjectiles(...)`, immediately after retrieving a projectile from the collection.

```java
Gdx.app.log("PROJECTILE", "index=" + i + " x=" + projectile.getX() + " y=" + projectile.getY());
```

**While testing, the loop should include the log like this:**

```java
Projectile projectile = projectiles.get(i);
Gdx.app.log("PROJECTILE", "index=" + i + " x=" + projectile.getX() + " y=" + projectile.getY());
projectile.update(deltaTime);
```

Run again and watch Android Studio's Run window. Compare the changing coordinates with the movement you observe. Different indexes identify different projectile objects.

Remove the temporary logging line after the experiment so the loop again retrieves the projectile and then calls `projectile.update(deltaTime);`.

## 5. Check the finished game

Before saving your work, confirm that:

- W/A/S/D movement still uses delta time;
- the obstacle still stops the robot flush against the approached edge;
- collecting a ball increases ammunition rather than score;
- collected and fired balls both use `Projectile.SIZE`, and `BALL_SIZE` has been removed from `RobotGame`;
- the Space key fires once per press only when ammunition is available;
- `Projectile` has private fields, a constructor, and public behavior methods;
- an `ArrayList<Projectile>` stores all active projectiles;
- `updateProjectiles(...)` moves, scores, and removes; `drawProjectiles()` draws;
- fired balls appear at the robot's top center and travel upward;
- the goal is centered near the top of the window;
- scoring and missed projectiles are removed by a backward loop;
- the ball, goal, obstacle, robot, font, and batch resources are disposed;
- no temporary logging line remains.

Ask for help if any check fails. Fix the game and run the checklist again before committing.

## 6. Commit and push

Use Android Studio's Git tools:

1. Open the Commit window.
2. Review both changed Java files.
3. Enter this commit message:

   ```text
   Add goal scoring
   ```

4. Commit the changes.
5. Push the commit to your assigned GitHub repository.

> **Final checkpoint:** The finished game works, and the `Add goal scoring` commit has been pushed.

## Optional customizations

If you finish early, make one change at a time:

- move the goal to another horizontal position near the top;
- award more than one point for a successful shot;
- replace `goals/goal.png` with your own 128×128 transparent image at the same path.
