# Session 5 — Fire Projectiles

In this session, collected balls become ammunition that you can fire from the upward-facing robot. Each fired ball will track its own position and move independently. Keep using this cycle:

```text
predict -> edit -> run -> observe
```

## 1. Start from your working game

Use Android Studio to pull any instructor updates, then open:

```text
core/src/main/java/org/ftcgame/RobotGame.java
```

Run the game and confirm that:

- W/A/S/D move the robot with delta time;
- the obstacle stops the robot flush against the approached edge;
- every ball can be collected and increases the score once;
- `isRobotTouching(...)`, `collectBalls()`, and `drawBalls()` still work.

Stop the game before editing.

> **Checkpoint:** The completed Session 4 game runs correctly.

## 2. Turn collected balls into ammunition

At the moment, touching a ball immediately adds a point. In this session, collecting a ball will provide ammunition instead:

```text
collect a ball -> gain ammunition -> fire the ball
```

Scoring fired balls in a goal will come in a later session.

Add an ammunition variable immediately below `score`.

```java
private int ammo = 0;
```

**After adding it, your variables should look like this:**

```java
private ArrayList<Vector2> balls;
private int score = 0;
private int ammo = 0;
```

Add a layout constant for the ammunition text with the other HUD constants:

```java
private static final float AMMO_TOP_MARGIN = 45;
```

The HUD constants should now include:

```java
private static final float HUD_X = 20;
private static final float SCORE_TOP_MARGIN = 20;
private static final float AMMO_TOP_MARGIN = 45;
```

Inside `collectBalls()`, replace the score increase with an ammunition increase.

```java
ammo = ammo + 1;
```

**The finished collision block should look like this:**

```java
if (isRobotTouching(ball.x, ball.y, BALL_SIZE)) {
    balls.remove(i);
    ammo = ammo + 1;
}
```

Add a second line of visible text immediately after the existing score drawing line.

```java
font.draw(batch, "Ammo: " + ammo, HUD_X, Gdx.graphics.getHeight() - AMMO_TOP_MARGIN);
```

**After adding the line, the drawing code should look like this:**

```java
font.draw(batch, "Score: " + score, HUD_X, Gdx.graphics.getHeight() - SCORE_TOP_MARGIN);
font.draw(batch, "Ammo: " + ammo, HUD_X, Gdx.graphics.getHeight() - AMMO_TOP_MARGIN);
batch.end();
```

### Run it now

Run the game and collect one ball. Predict both displayed numbers before touching it.

The score should remain unchanged, while ammunition should increase by one. Collect the remaining balls and confirm that ammunition increases once for every ball.

The game can store ammunition, but it cannot fire anything yet.

> **Checkpoint:** Collecting balls increases ammunition instead of score.

## 3. Plan for multiple projectiles

Every fired ball needs its own:

- horizontal position;
- vertical position.

The game also needs code that moves every fired ball.

One set of variables could describe one projectile, but firing another would require another set. Several independently moving projectiles would lead to names such as `projectile1X`, `projectile2X`, and `projectile3X`, plus repeated movement code.

A **class** solves both problems by defining the variables and code for one kind of object in one place. A class is like a template for creating objects, and an **object** is one individual thing created from that template. The `Projectile` class defines the position variables and movement code a projectile needs. Each new `Projectile` object gets its own position, so the game can manage many projectiles without numbered variables or copied movement code.

## 4. Create the Projectile class

In Android Studio's Project window, find the package containing `RobotGame.java`. It may appear as either:

```text
core/src/main/java/org/ftcgame
```

or:

```text
core/src/main/java/org.ftcgame
```

A Java **package** groups related classes. Each dot in a package name separates one folder level, so the package `org.ftcgame` is stored in the nested folders `org/ftcgame` on disk. Android Studio may show those folders separately or combine them into `org.ftcgame`; both views refer to the same location.

Right-click `org.ftcgame`, choose **New -> Java Class**, enter `Projectile`, and press Enter. Android Studio should create `Projectile.java` in the same package as `RobotGame.java`.

The collected balls already use `BALL_SIZE` in `RobotGame`. A fired ball is the same size, so the game should not keep a second size value that could disagree with the first. We will move the ball-size value into `Projectile` and use it for both collected and fired balls.

The `Projectile` class will keep two settings as constants: `SIZE` and `SPEED`. Both use `static final` because they belong to the `Projectile` class and do not change while the game runs.

Replace the generated class contents with the following class.

**Copy this complete file:**

```java
package org.ftcgame;

public class Projectile {
    public static final int SIZE = 16;
    private static final float SPEED = 400;

    private float x;
    private float y;

    public Projectile(float startX, float startY) {
        x = startX;
        y = startY;
    }

    public void update(float deltaTime) {
        y = y + SPEED * deltaTime;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
```

Look at the first line of `Projectile.java`:

```java
package org.ftcgame;
```

This **package statement** tells Java that `Projectile` belongs to the `org.ftcgame` group. It must match the package used by `RobotGame`. Because both classes are in the same package, `RobotGame` can refer to `Projectile` by its short name without an `import`.

`SIZE` is `public` because `RobotGame` needs it for drawing collected balls and projectiles, checking collisions, and placing new projectiles. `SPEED` is `private` because only code inside `Projectile` needs it. In `public static final int SIZE`, `public` allows another class to use the constant, `static` makes it belong to the `Projectile` class, and `final` prevents its value from being reassigned. `RobotGame` refers to this constant as `Projectile.SIZE`, meaning "the `SIZE` constant that belongs to the `Projectile` class."

The `x` and `y` fields are **member variables** that belong to each `Projectile` object. Each object gets its own position, so one projectile can move without changing another. The fields are `private`, so outside code uses the class's methods instead of changing them directly:

- `update(...)` moves the projectile;
- `getX()` and `getY()` report its position for drawing.

The method named `Projectile(...)` is the **constructor**. It runs when `new Projectile(...)` creates an object, and its parameters set that object's starting position. A constructor has the same name as its class and no return type.

`public` lets `RobotGame` use the class, constructor, and methods. The `float` return type says that `getX()` and `getY()` send back decimal position values. Keeping fields private and providing controlled operations through methods is called **encapsulation**.

Do not run yet. The class exists, but `RobotGame` does not create any projectile objects.

## 5. Store projectile objects

Return to `RobotGame.java`.

### Use the moved size in RobotGame

Change the ball collision line from:

```java
if (isRobotTouching(ball.x, ball.y, BALL_SIZE)) {
```

to:

```java
if (isRobotTouching(ball.x, ball.y, Projectile.SIZE)) {
```

The collision check inside `collectBalls()` should now look like this:

```java
if (isRobotTouching(ball.x, ball.y, Projectile.SIZE)) {
    balls.remove(i);
    ammo = ammo + 1;
}
```

Change the ball drawing line from:

```java
batch.draw(ballTexture, ball.x, ball.y, BALL_SIZE, BALL_SIZE);
```

to:

```java
batch.draw(ballTexture, ball.x, ball.y, Projectile.SIZE, Projectile.SIZE);
```

The drawing line inside `drawBalls()` should now look like this:

```java
Vector2 ball = balls.get(i);
batch.draw(ballTexture, ball.x, ball.y, Projectile.SIZE, Projectile.SIZE);
```

Both uses of `BALL_SIZE` now use `Projectile.SIZE`, so remove the old constant from `RobotGame`:

```java
private static final int BALL_SIZE = 16;
```

The ball-size value now belongs to `Projectile`. Code inside `RobotGame` uses the class name in `Projectile.SIZE`.

### Store the projectiles

Add a projectile collection immediately below the existing ball collection.

```java
private ArrayList<Projectile> projectiles;
```

**Your state variables should now look like this:**

```java
private ArrayList<Vector2> balls;
private ArrayList<Projectile> projectiles;
private int score = 0;
private int ammo = 0;
```

The type between `<` and `>` says this collection stores `Projectile` objects. Collectible balls still use `Vector2` because they need only a position; projectiles use a class because each one must store and update its own position.

Create the empty collection at the end of `create()`.

```java
projectiles = new ArrayList<>();
```

**The end of `create()` should now look like this:**

```java
balls.add(new Vector2(250, 100));
balls.add(new Vector2(250, 350));
balls.add(new Vector2(700, 350));

projectiles = new ArrayList<>();
```

As in Session 4, the field declaration does not create the collection. `new ArrayList<>()` creates it, starting empty because the robot has not fired yet.

## 6. Fire one available ball

The robot artwork faces upward, so every projectile in this session travels upward. Moving the robot horizontally changes where the next projectile begins.

Add this firing condition after the obstacle collision check and before `collectBalls();`. Resolving robot movement first ensures a projectile starts from the robot's final, non-overlapping position.

```java
if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && ammo > 0) {
    projectiles.add(new Projectile(
        robotX + (ROBOT_SIZE - Projectile.SIZE) / 2,
        robotY + ROBOT_SIZE
    ));
    ammo = ammo - 1;
}
```

The firing condition should sit between obstacle collision and ball collection like this:

```java
if (isRobotTouching(obstacleX, obstacleY, OBSTACLE_SIZE)) {
    // The existing edge-snapping response remains here.
}

if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && ammo > 0) {
    projectiles.add(new Projectile(
        robotX + (ROBOT_SIZE - Projectile.SIZE) / 2,
        robotY + ROBOT_SIZE
    ));
    ammo = ammo - 1;
}

collectBalls();
```

`isKeyJustPressed(...)` is true only on the first frame of a key press, so holding the Space key does not fire every frame. `&& ammo > 0` prevents firing without ammunition, and the line inside the block spends one ball only when a projectile is created.

`new Projectile(...)` creates one object and calls its constructor.

The projectile should appear centered above the robot when it is fired. Both `robotX` and the projectile's starting x-coordinate mark the left edge of their objects. If we passed `robotX` directly to the constructor, the projectile's left edge would line up with the robot's left edge instead of its center.

Because the projectile is narrower than the robot, centering it leaves an equal gap on both sides. First subtract `Projectile.SIZE` from `ROBOT_SIZE` to find the horizontal space left over. Dividing that space by `2` finds the gap on one side. Adding that gap to `robotX` gives the projectile's starting x-coordinate:

```text
space left over = ROBOT_SIZE - Projectile.SIZE
gap on each side = space left over / 2
projectile x = robotX + gap on each side
```

Adding `ROBOT_SIZE` to `robotY` places the projectile immediately above the robot, with the projectile's bottom edge touching the robot's top edge.

Do not run yet. Pressing the Space key can create a projectile and spend ammunition, but nothing updates or draws the object.

## 7. Update every projectile

Follow Session 4's named-method pattern for both projectile tasks. First, add this update method after `drawBalls()`:

```java
private void updateProjectiles(float deltaTime) {
    for (int i = 0; i < projectiles.size(); i = i + 1) {
        Projectile projectile = projectiles.get(i);
        projectile.update(deltaTime);
    }
}
```

**The new method should sit after `drawBalls()` like this:**

```java
private void drawBalls() {
    for (int i = 0; i < balls.size(); i = i + 1) {
        Vector2 ball = balls.get(i);
        batch.draw(ballTexture, ball.x, ball.y, Projectile.SIZE, Projectile.SIZE);
    }
}

private void updateProjectiles(float deltaTime) {
    for (int i = 0; i < projectiles.size(); i = i + 1) {
        Projectile projectile = projectiles.get(i);
        projectile.update(deltaTime);
    }
}
```

The `deltaTime` parameter lets every projectile use the time since the previous frame. Each object changes its own private `y` field when the loop calls `update(...)`.

Call the method immediately after `collectBalls()` and before `batch.begin()`:

```java
updateProjectiles(deltaTime);
```

**The code before drawing should now look like this:**

```java
collectBalls();
updateProjectiles(deltaTime);

batch.begin();
```

## 8. Draw every projectile

Use the existing ball texture to draw fired balls. Add this method immediately after `updateProjectiles(...)`:

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
```

**The two projectile methods should look like this:**

```java
private void updateProjectiles(float deltaTime) {
    for (int i = 0; i < projectiles.size(); i = i + 1) {
        Projectile projectile = projectiles.get(i);
        projectile.update(deltaTime);
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

Call `drawProjectiles()` immediately after `drawBalls()` and before the obstacle is drawn:

```java
drawProjectiles();
```

**The drawing section should now begin like this:**

```java
batch.begin();
drawBalls();
drawProjectiles();
batch.draw(obstacleTexture, obstacleX, obstacleY, OBSTACLE_SIZE, OBSTACLE_SIZE);
```

`RobotGame` cannot use `projectile.x` or `projectile.y` because those fields are private. It calls `getX()` and `getY()` to ask the object for the values needed to draw it.

### Run it now

Run the game and press the Space key before collecting a ball. Nothing should fire, and ammunition should remain `0`.

Collect one ball, then tap the Space key. Ammunition should return to `0`, and one ball should travel upward from the center of the robot.

Collect at least two more balls. Tap the Space key twice quickly. You should see two objects moving independently, even if they were created at different robot positions.

The projectiles currently continue beyond the window, and there is no goal. We will add both behaviors in a later session.

> **Checkpoint:** The Space key spends one ammunition and creates one independently moving projectile.

## 9. Check the finished game

Before saving your work, confirm that:

- W/A/S/D movement still uses delta time;
- the obstacle still stops the robot flush against the approached edge;
- collecting a ball increases ammunition rather than score;
- collected and fired balls both use `Projectile.SIZE`, and `BALL_SIZE` has been removed from `RobotGame`;
- the Space key fires once per press only when ammunition is available;
- `Projectile` has private fields, a constructor, and public behavior methods;
- an `ArrayList<Projectile>` stores all active projectiles;
- `updateProjectiles(...)` moves every projectile; `drawProjectiles()` draws them;
- fired balls appear at the robot's top center and travel upward;
- the ball, obstacle, robot, font, and batch resources are disposed.

Ask for help if any check fails. Fix the game and run the checklist again before committing.

## 10. Commit and push

Use Android Studio's Git tools:

1. Open the Commit window.
2. Review both changed Java files.
3. Enter this commit message:

   ```text
   Add projectile shooting
   ```

4. Commit the changes.
5. Push the commit to your assigned GitHub repository.

> **Final checkpoint:** The finished game works, and the `Add projectile shooting` commit has been pushed.

## Optional customizations

If you finish early, make one change at a time:

- change projectile `SPEED` while keeping delta-time movement;
- add another collectible ball with another `balls.add(...)` line;
- replace `balls/ball.png` with your own 128×128 transparent image at the same path.
