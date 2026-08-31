# Session 4 — Collect and Score

In this session, you will add several balls that disappear and increase a visible score when collected. After the balls are visible, the game must make its existing obstacle collision check work for every ball:

```text
Is the robot touching this object?
```

Instead of copying the collision code for every ball, you will give the collision check a method name and reuse it. Keep using this cycle:

```text
predict -> edit -> run -> observe
```

When surrounding code is shown, copy only the new lines from the copy-only block.

## 1. Start from your working game

Use Android Studio to pull any instructor updates, then open:

```text
core/src/main/java/org/ftcgame/RobotGame.java
```

Run the game and confirm that:

- W/A/S/D move the robot;
- movement stays smooth and uses delta time;
- one spiked obstacle appears;
- touching the obstacle stops the robot flush against the approached edge.

Stop the game before editing.

> **Checkpoint:** The completed Session 3 game runs correctly.

## 2. Prepare to draw balls and a score

The project now includes `assets/balls/ball.png`. Like the obstacle image, loading it will be your responsibility.

The score will be stored as a number, but the game must also draw that number on the screen. libGDX's `BitmapFont` uses the same `SpriteBatch` to draw text such as `Score: 3`; `new BitmapFont()` creates a simple built-in font.

Ball positions will use libGDX's `Vector2`, which keeps one `x` coordinate and one `y` coordinate together. The game will store those positions in Java's `ArrayList`, a collection that can grow when a ball is added and shrink when a ball is collected.

The game needs three new imports to use those classes:

- `BitmapFont` to draw the score;
- `Vector2` to keep each ball's coordinates together;
- `ArrayList` to manage all the ball positions as one collection.

Replace the existing import block:

```java
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
```

with this complete import block:

```java
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
```

Add these variables immediately below `obstacleTexture`:

```java
private Texture ballTexture;
private BitmapFont font;
```

After you paste them, the variables should look like this:

```java
private SpriteBatch batch;
private Texture robotTexture;
private Texture obstacleTexture;
private Texture ballTexture;
private BitmapFont font;
```

`BALL_SIZE` will control both the ball's drawn size and its collision-box size. The score will appear in the game's **heads-up display (HUD)**, which shows information over the play area. `HUD_X` will set the horizontal position of both the score and ammunition text, while `SCORE_TOP_MARGIN` will set the score's distance from the top of the window. Add these constants with the existing constants:

```java
private static final int BALL_SIZE = 16;
private static final float HUD_X = 20;
private static final float SCORE_TOP_MARGIN = 20;
```

The size and HUD constants should now include:

```java
private static final int ROBOT_SIZE = 64;
private static final int OBSTACLE_SIZE = 64;
private static final int BALL_SIZE = 16;
private static final float HUD_X = 20;
private static final float SCORE_TOP_MARGIN = 20;
```

Add these state variables below the obstacle coordinates:

```java
private ArrayList<Vector2> balls;
private int score = 0;
```

After you paste them, the state variables should look like this:

```java
private float obstacleX = OBSTACLE_START_X;
private float obstacleY = OBSTACLE_START_Y;

private ArrayList<Vector2> balls;
private int score = 0;
```

A **collection** groups related values so the program can manage them together. `ArrayList` keeps its values in order and can grow or shrink. The type between `<` and `>` says what it holds, so `ArrayList<Vector2>` is an ordered list of ball positions.

The score uses Java's `int` type, which stores a whole number such as `0`, `1`, or `3`. Unlike a `float`, an `int` does not store a decimal part. The robot's coordinates and speed are `float` values because movement can produce positions such as `250.5`, but the score only changes by whole points. The score is a member variable so its value remains available from one frame to the next.

## 3. Load the resources and create the collection

Add these lines at the end of `create()`:

```java
ballTexture = new Texture("balls/ball.png");
font = new BitmapFont();

balls = new ArrayList<>();
balls.add(new Vector2(250, 100));
balls.add(new Vector2(250, 350));
balls.add(new Vector2(700, 350));
```

After you paste them, `create()` should look like this:

```java
@Override
public void create() {
    batch = new SpriteBatch();
    robotTexture = new Texture("robot.png");
    obstacleTexture = new Texture("obstacles/spikes.png");
    ballTexture = new Texture("balls/ball.png");
    font = new BitmapFont();

    balls = new ArrayList<>();
    balls.add(new Vector2(250, 100));
    balls.add(new Vector2(250, 350));
    balls.add(new Vector2(700, 350));
}
```

`private ArrayList<Vector2> balls;` declares a variable for the collection. `balls = new ArrayList<>();` creates the empty collection; both steps are needed before `balls.add(...)`. The empty `<>` is shorthand because Java already knows this collection holds `Vector2` values.

Each `add(...)` call puts one ball position into the collection. `new Vector2(250, 100)` creates one value whose `x` is `250` and whose `y` is `100`.

Textures, `SpriteBatch`, and `BitmapFont` use graphics memory, so the game must dispose them when it closes. Java manages the `int`, `ArrayList`, and `Vector2` values automatically, so they do not need to be disposed.

Add these cleanup lines to `dispose()`:

```java
ballTexture.dispose();
font.dispose();
```

After you paste them, `dispose()` should look like this:

```java
@Override
public void dispose() {
    batch.dispose();
    robotTexture.dispose();
    obstacleTexture.dispose();
    ballTexture.dispose();
    font.dispose();
}
```

Do not run yet. The resources and positions exist, but nothing draws the balls or score.

## 4. Draw every ball with a loop

Inside the drawing section, add this loop immediately after `batch.begin()` and before the obstacle is drawn:

```java
for (int i = 0; i < balls.size(); i = i + 1) {
    Vector2 ball = balls.get(i);
    batch.draw(ballTexture, ball.x, ball.y, BALL_SIZE, BALL_SIZE);
}
```

After you paste it, the drawing section should look like this:

```java
batch.begin();

for (int i = 0; i < balls.size(); i = i + 1) {
    Vector2 ball = balls.get(i);
    batch.draw(ballTexture, ball.x, ball.y, BALL_SIZE, BALL_SIZE);
}

batch.draw(obstacleTexture, obstacleX, obstacleY, OBSTACLE_SIZE, OBSTACLE_SIZE);
batch.draw(robotTexture, robotX, robotY, ROBOT_SIZE, ROBOT_SIZE);
batch.end();
```

The first line of the `for` loop has three parts, separated by semicolons:

- `int i = 0` creates an `int` variable named `i` and gives it a starting value. It is a **local variable**, meaning it can be used only within this loop.
- `i < balls.size()` compares `i` to the number of balls in the collection. If `i` is less than that number, the code inside the braces runs. If `i` is greater than or equal to that number, the loop stops and execution continues after the braces.
- `i = i + 1` runs after each trip through the loop, adding one to the `i` variable.

The loop uses `i` as the collection **index**. Indexes begin at zero, so three balls use `0`, `1`, and `2`. `balls.size()` reports the number of values, and `balls.get(i)` retrieves the current one. During that trip through the loop, the local variable `ball` provides its coordinates as `ball.x` and `ball.y`.

The ball image file has the standard runtime asset size, but the final two arguments in `batch.draw(...)` tell libGDX to display it at `BALL_SIZE`. The texture file's dimensions do not have to match the width and height passed to `batch.draw(...)`. Drawing the ball smaller than the robot makes it look like something the robot could collect and later carry or shoot.

### Run it now

Run the game. You should see all configured balls drawn smaller than the robot. The robot and obstacle should still work, but driving over a ball should not remove it yet.

Before continuing, predict which ball corresponds to index `0`. Compare your prediction with the first `balls.add(...)` line in `create()`.

> **Checkpoint:** One loop draws every ball position stored in the collection.

## 5. Prepare to collect the balls

The balls are now visible, but driving over one does not remove it. The collision condition checks whether the robot touches one obstacle. The game now needs the same four comparisons for every ball:

```java
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
```

We will move the comparisons into a **method** that receives an object's position and answers either `true` or `false`.

## 6. Create a collision method

A **method** is a named section of code that performs one task. Other code can **call** the method by name whenever that task needs to be performed.

Our method will answer the question "Is the robot touching this object?" The game can call it for the obstacle and for every ball instead of keeping a separate copy of the four collision comparisons for each object. If the collision calculation later needs to change, there will be only one place to update it.

The method will be named `isRobotTouching`. The values inside its parentheses describe the object to check, and the code inside its braces performs the collision calculation.

Add this entire method after the closing brace of `render()` and before `pause()`:

```java
private boolean isRobotTouching(float objectX, float objectY, float objectSize) {
    return robotX < objectX + objectSize &&
        robotX + ROBOT_SIZE > objectX &&
        robotY < objectY + objectSize &&
        robotY + ROBOT_SIZE > objectY;
}
```

The new method should sit between `render()` and `pause()` like this:

```java
        batch.draw(robotTexture, robotX, robotY, ROBOT_SIZE, ROBOT_SIZE);
        batch.end();
    }

    private boolean isRobotTouching(float objectX, float objectY, float objectSize) {
        return robotX < objectX + objectSize &&
            robotX + ROBOT_SIZE > objectX &&
            robotY < objectY + objectSize &&
            robotY + ROBOT_SIZE > objectY;
    }

    @Override
    public void pause() {
```

The method has three **parameters**. `objectX` and `objectY` describe where an object is located, while `objectSize` describes its width and height of the object's hitbox. A caller supplies values for all three each time it asks about an object. The robot side of each comparison uses `ROBOT_SIZE` because it compares the object's hitbox to the robot's hitbox.

The word `boolean` says that the method sends back one of two answers: `true` or `false`. The `return` statement sends the result back to the code that called the method. When `isRobotTouching(...)` is used as an `if` condition, its returned answer becomes the condition that the `if` checks.

The method is `private` because it is a helper used only inside `RobotGame`. The obstacle still uses the old comparisons until the next step.

## 7. Use the method for the obstacle

Find this existing obstacle condition:

```java
if (robotX < obstacleX + OBSTACLE_SIZE &&
    robotX + ROBOT_SIZE > obstacleX &&
    robotY < obstacleY + OBSTACLE_SIZE &&
    robotY + ROBOT_SIZE > obstacleY) {
```

Replace it with:

```java
if (isRobotTouching(obstacleX, obstacleY, OBSTACLE_SIZE)) {
```

After the replacement, the complete obstacle check should look like this:

```java
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
```

The call supplies the obstacle's position and size as three arguments. During this call, `obstacleX`, `obstacleY`, and `OBSTACLE_SIZE` become `objectX`, `objectY`, and `objectSize` inside the method. The method detects overlap. The nested conditions still stop the robot against the side it approached.

### Run it now

Run the game and drive into the obstacle from at least two sides. It should still stop flush against each approached edge.

If the game behaves differently, compare the helper method with the original four comparisons. Refactoring should change how the code is organized without changing its behavior.

> **Checkpoint:** One call to `isRobotTouching(obstacleX, obstacleY, OBSTACLE_SIZE)` detects obstacle overlap, and the existing response stops the robot at the approached edge.

## 8. Collect the balls

To collect a ball, the game must detect when the robot touches it, remove it, and increase the score. Add this loop after the obstacle collision check and before `batch.begin()`:

```java
for (int i = balls.size() - 1; i >= 0; i = i - 1) {
    Vector2 ball = balls.get(i);

    if (isRobotTouching(ball.x, ball.y, BALL_SIZE)) {
        balls.remove(i);
        score = score + 1;
    }
}
```

The ball loop should sit between obstacle collision and drawing like this:

```java
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

for (int i = balls.size() - 1; i >= 0; i = i - 1) {
    Vector2 ball = balls.get(i);

    if (isRobotTouching(ball.x, ball.y, BALL_SIZE)) {
        balls.remove(i);
        score = score + 1;
    }
}

batch.begin();
```

This loop begins at the last valid index, `balls.size() - 1`, and moves backward to zero. Removing a ball shifts the balls after it toward the beginning of the collection. Because the loop has already checked those balls, it can continue backward without skipping an unchecked ball.

`>=` means "greater than or equal to," so the loop continues while `i` is positive or zero. Including zero matters because index `0` contains the first ball.

Here is what could go wrong if the game moved **forward** through the collection. Suppose it removes ball B at index `1`.

Before removal, the loop is checking B:

```text
index:     0       1       2
ball:     [A]     [B]     [C]
                   ^
                  i = 1
```

During removal, C shifts left to fill the empty index:

```text
remove B

index:     0       1
ball:     [A]     [C]
                   ^
             C moved to index 1
```

After removal, a forward loop increases `i` to `2`. The collection's size is now `2`, so the loop stops and never checks C:

```text
index:     0       1        2
ball:     [A]     [C]      i -> stop
                   ^
                skipped
```

The backward loop avoids that problem. Before removing B, it has already checked C at index `2`:

```text
index:     0       1       2
ball:     [A]     [B]     [C]
                           ^ checked first

then i moves backward to index 1
```

During removal, C still shifts to index `1`, but it does not need to be checked again:

```text
remove B

index:     0       1
ball:     [A]     [C]
                   ^ already checked
```

After removal, the backward loop decreases `i` to `0` and checks A. Every ball that existed before the removal has been checked exactly once:

```text
index:     0       1
ball:     [A]     [C]
           ^       ^
       check next  already checked
```

### Run it now

Run the game and collect one ball. It should disappear. Return to the same location; nothing should happen because that position is no longer in the collection. Collect the other balls and confirm that they disappear independently.

The score changes in memory, but it is not visible yet. The next step draws it.

> **Checkpoint:** Each ball disappears once when the robot touches it.

## 9. Draw the score

Add this line after drawing the robot and before `batch.end()`:

```java
font.draw(batch, "Score: " + score, HUD_X, Gdx.graphics.getHeight() - SCORE_TOP_MARGIN);
```

After you paste it, the end of the drawing section should look like this:

```java
batch.draw(obstacleTexture, obstacleX, obstacleY, OBSTACLE_SIZE, OBSTACLE_SIZE);
batch.draw(robotTexture, robotX, robotY, ROBOT_SIZE, ROBOT_SIZE);
font.draw(batch, "Score: " + score, HUD_X, Gdx.graphics.getHeight() - SCORE_TOP_MARGIN);
batch.end();
```

`"Score: " + score` combines the label with the current value of `score`, producing text such as `Score: 3`. `HUD_X` positions the text from the left edge. Subtracting `SCORE_TOP_MARGIN` from the window height positions it down from the top edge, even if the window height changes.

### Run it now

Run the game. Confirm that the score begins at `0`, then collect the balls one at a time. Predict the score before each collision.

The visible score should increase by one for each collected ball. A collected ball should never award points again.

> **Checkpoint:** The score is visible and increases exactly once for each collected ball.

## 10. Give the ball loops clear names

The game works, but `render()` now contains movement, obstacle collision, ball collection, and drawing. Methods can do more than answer a question such as `isRobotTouching(...)`. A method can also give one complete game task a clear name.

First, add this method after `isRobotTouching(...)` and before `pause()`:

```java
private void collectBalls() {
    for (int i = balls.size() - 1; i >= 0; i = i - 1) {
        Vector2 ball = balls.get(i);

        if (isRobotTouching(ball.x, ball.y, BALL_SIZE)) {
            balls.remove(i);
            score = score + 1;
        }
    }
}
```

The two collision methods should now sit together like this:

```java
private boolean isRobotTouching(float objectX, float objectY, float objectSize) {
    return robotX < objectX + objectSize &&
        robotX + ROBOT_SIZE > objectX &&
        robotY < objectY + objectSize &&
        robotY + ROBOT_SIZE > objectY;
}

private void collectBalls() {
    for (int i = balls.size() - 1; i >= 0; i = i - 1) {
        Vector2 ball = balls.get(i);

        if (isRobotTouching(ball.x, ball.y, BALL_SIZE)) {
            balls.remove(i);
            score = score + 1;
        }
    }
}

@Override
public void pause() {
```

`void` means `collectBalls()` performs a task without sending a value back. It can use `balls` and `score` directly because they are member variables in the same class.

The backward loop is temporarily in two places. In `render()`, replace the original backward ball loop with this method call:

```java
collectBalls();
```

The code between obstacle collision and drawing should now be short:

```java
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

collectBalls();

batch.begin();
```

Next, add this method immediately after `collectBalls()`:

```java
private void drawBalls() {
    for (int i = 0; i < balls.size(); i = i + 1) {
        Vector2 ball = balls.get(i);
        batch.draw(ballTexture, ball.x, ball.y, BALL_SIZE, BALL_SIZE);
    }
}
```

The two ball-task methods should now look like this:

```java
private void collectBalls() {
    for (int i = balls.size() - 1; i >= 0; i = i - 1) {
        Vector2 ball = balls.get(i);

        if (isRobotTouching(ball.x, ball.y, BALL_SIZE)) {
            balls.remove(i);
            score = score + 1;
        }
    }
}

private void drawBalls() {
    for (int i = 0; i < balls.size(); i = i + 1) {
        Vector2 ball = balls.get(i);
        batch.draw(ballTexture, ball.x, ball.y, BALL_SIZE, BALL_SIZE);
    }
}
```

In `render()`, replace the original forward ball drawing loop with this call:

```java
drawBalls();
```

The drawing section should now begin like this:

```java
batch.begin();
drawBalls();
batch.draw(obstacleTexture, obstacleX, obstacleY, OBSTACLE_SIZE, OBSTACLE_SIZE);
```

The call to `drawBalls()` remains between `batch.begin()` and `batch.end()` because its loop uses `batch.draw(...)`.

### Run it now

Run the game and collect every ball. Everything should behave as before, while the two named methods make `render()` easier to read.

> **Checkpoint:** `render()` calls `collectBalls()` and `drawBalls()`, and the game behaves as it did before the refactor.

## 11. Check the finished game

Test the two kinds of collision, then confirm the final structure:

- W/A/S/D movement still uses delta time;
- touch the obstacle before collecting a ball: the robot stops at its edge and the score does not change;
- drive near a ball without touching it: the ball remains visible;
- collect every ball from different directions: each disappears once and increases the score once;
- collision comparisons exist only inside `isRobotTouching(...)`;
- `isRobotTouching(...)` has three parameters and returns a `boolean`;
- `balls` is an `ArrayList<Vector2>`;
- `drawBalls()` contains the loop that draws all balls;
- `collectBalls()` contains the collision loop, which moves backward through the collection;
- each ball's collision box matches the width and height used to draw the ball;
- the ball texture and font are disposed in `dispose()`;
- `render()` calls both ball methods instead of containing their loops.

Ask for help if any check fails. Fix the game and run the checklist again before committing.

## 12. Commit and push

Use Android Studio's Git tools:

1. Open the Commit window.
2. Review the changed Java file.
3. Enter this commit message:

   ```text
   Add collectible balls and scoring
   ```

4. Commit the changes.
5. Push the commit to your assigned GitHub repository.

> **Final checkpoint:** The finished game works, and the `Add collectible balls and scoring` commit has been pushed.

## Optional customizations

If you finish early, make one change at a time:

- change a ball's starting coordinates;
- add one more ball with another `balls.add(...)` line;
- move the score to another part of the window;
- award 5 points instead of 1 for each ball;
- replace `balls/ball.png` with your own 128×128 transparent image at the same path.
