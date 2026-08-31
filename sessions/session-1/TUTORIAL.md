# Session 1 — Make the Robot Move

In this session, you will make a robot appear on screen and control it with the keyboard. You will work in small steps, run the game often, and learn new Java ideas when you need them.

It is normal to use examples, make mistakes, and ask for help. You are not expected to memorize the code today.

## 1. Open the game

Open the repository's `game` directory in Android Studio. Wait for any Gradle activity to finish before continuing.

In the Project panel, open:

```text
core/src/main/java/org/ftcgame/RobotGame.java
```

This is the main Java file you will change today. Other files make the game work on different computers, but you do not need to understand them yet.

Java code is organized into **classes**. The `RobotGame` class contains the code and information used by this game.

## 2. Run the starter

In Android Studio's toolbar, open the run-configuration menu and select **RobotGame**. You normally need to select it only once for this project.

Click the green Run button. A dark game window should open. The robot is not visible yet.

Return to Android Studio and click the red Stop button.

> **Checkpoint:** The blank starter game opens and stops without an error.

If the game does not open, make sure **RobotGame** is selected and ask for help before editing the code.

## 3. Find the important parts

Near the top of `RobotGame`, you will see two variables:

```java
private SpriteBatch batch;
private Texture robotTexture;
```

These are **variables**: named places where the program stores information. Every variable has a **type** that tells Java what kind of information it can hold. `SpriteBatch` and `Texture` are types supplied by **libGDX**, the game-programming library used to build this project.

The game uses `batch` to draw images. `robotTexture` holds the robot image that the starter loads for you. The word `private` means this class uses the variable for its own work.

You will also see these methods:

- `create()` runs once when the game starts;
- `render()` runs once for every **frame**—each picture the game draws while it is running;
- `dispose()` cleans up resources when the game closes.

Games draw many frames each second. This rate is called **frames per second (FPS)**, a term you may recognize from game settings or performance displays.

You do not need to understand the other supplied methods yet.

## 4. Give the robot a position

The game needs to remember where the robot is. Add these variables immediately below `robotTexture`:

```java
private float robotX = 100;
private float robotY = 100;
private float robotSpeed = 3;
```

After adding them, the variables at the top of the class should look like this:

```java
private SpriteBatch batch;
private Texture robotTexture;

private float robotX = 100;
private float robotY = 100;
private float robotSpeed = 3;
```

`float` is a Java type for numbers, including numbers with decimal parts. The `=` assigns each variable its starting value, and the semicolon ends the instruction.

`robotX` is the horizontal position, and `robotY` is the vertical position. `robotSpeed` will control how far the robot moves during each frame.

These variables are outside the methods because their values must remain available from one frame to the next.

## 5. Draw the robot

Find `render()`. It already contains the line that clears the screen:

```java
ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1);
```

The first three numbers set the background's red, green, and blue amounts. Each ranges from `0` (none of that color) to `1` (the full amount). The fourth number is alpha, which controls transparency; `1` means fully opaque. These values produce a very dark blue-gray background.

Immediately after that line, add:

```java
batch.begin();
batch.draw(robotTexture, robotX, robotY);
batch.end();
```

The drawing code should now sit directly after the screen is cleared:

```java
ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1);

batch.begin();
batch.draw(robotTexture, robotX, robotY);
batch.end();
```

These lines are **method calls**: they ask existing code to perform an action. `begin()` tells libGDX that drawing is about to start. `draw()` places the robot image at the stored coordinates. `end()` finishes the drawing for this frame.

### Run it now

Run the game. The robot should appear near the lower-left corner.

Stop the game, change `robotX` to a noticeably different value, and run it again. Predict where the robot will appear before the window opens.

After the experiment, restore the original `robotX` value.

> **Checkpoint:** The robot appears, and changing `robotX` changes its horizontal starting position.

The point `(0, 0)` is at the lower-left corner of the game window. Increasing `x` moves right, and increasing `y` moves up.

## 6. Read keyboard input

The game needs two more libGDX tools. Add these lines below the existing `ApplicationListener` import at the top of the file:

```java
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
```

The lines at the top of the file should now begin like this:

```java
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
```

An `import` makes code from another location available in this file. These imports give the game access to libGDX keyboard input.

Find `render()` again. Immediately after the `ScreenUtils.clear(...)` line and before `batch.begin()`, add:

```java
if (Gdx.input.isKeyPressed(Input.Keys.W)) {
    robotY = robotY + robotSpeed;
}
```

The W-key check should appear between clearing and drawing:

```java
ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1);

if (Gdx.input.isKeyPressed(Input.Keys.W)) {
    robotY = robotY + robotSpeed;
}

batch.begin();
```

An `if` statement runs its code only when its condition is true. Here, the condition asks whether the W key is pressed. While it is pressed, the game adds `robotSpeed` to the vertical position.

The braces `{` and `}` group the instructions controlled by the `if`. When the condition is true, Java runs the code between them. When it is false, Java skips that code.

### Run it now

Run the game and hold W. The robot should move upward.

> **Checkpoint:** W moves the robot up.

If the robot does not move, compare spelling, capitalization, parentheses, and braces with the example.

## 7. Move down

Add this second `if` statement immediately after the W-key statement:

```java
if (Gdx.input.isKeyPressed(Input.Keys.S)) {
    robotY = robotY - robotSpeed;
}
```

The two vertical movement checks should now look like this:

```java
if (Gdx.input.isKeyPressed(Input.Keys.W)) {
    robotY = robotY + robotSpeed;
}

if (Gdx.input.isKeyPressed(Input.Keys.S)) {
    robotY = robotY - robotSpeed;
}
```

Subtracting from `robotY` moves the robot in the opposite direction.

### Run it now

Run the game and confirm that W moves up and S moves down.

## 8. Move left and right

Add two more `if` statements after the S-key statement:

```java
if (Gdx.input.isKeyPressed(Input.Keys.A)) {
    robotX = robotX - robotSpeed;
}

if (Gdx.input.isKeyPressed(Input.Keys.D)) {
    robotX = robotX + robotSpeed;
}
```

All four movement checks should now appear in this order:

```java
if (Gdx.input.isKeyPressed(Input.Keys.W)) {
    robotY = robotY + robotSpeed;
}

if (Gdx.input.isKeyPressed(Input.Keys.S)) {
    robotY = robotY - robotSpeed;
}

if (Gdx.input.isKeyPressed(Input.Keys.A)) {
    robotX = robotX - robotSpeed;
}

if (Gdx.input.isKeyPressed(Input.Keys.D)) {
    robotX = robotX + robotSpeed;
}
```

### Run it now

Run the game and test W, A, S, and D. A and D change `robotX`, so they move horizontally. Try holding two keys at the same time.

> **Checkpoint:** The robot moves up, down, left, right, and diagonally.

The robot can leave the window. That limitation is expected for now.

## 9. Experiment with speed

Before running the game again, predict what will happen if you change `robotSpeed` to a noticeably larger value.

Run the game and test your prediction. Try one other value, then restore `robotSpeed` to its original value so later sessions begin from the required value.

This edit-run-observe cycle is one of the most important programming habits in the course:

```text
make a change -> run the game -> observe what happened -> adjust
```

## 10. Check the finished game

Before saving your work, confirm that:

- the game opens and the robot appears;
- W/A/S/D move it in all four directions;
- changing `robotSpeed` changes how quickly it moves.

Ask for help if one of these checks does not work. Fixing the game before saving creates a useful working checkpoint.

## 11. Commit and push

Use Android Studio's Git tools to save the finished work:

1. Open the Commit window.
2. Confirm that `RobotGame.java` is the file you changed.
3. Review the highlighted changes.
4. Enter this commit message:

   ```text
   Add robot movement
   ```

5. Commit the changes.
6. Push the commit to the assigned GitHub repository.

A commit is a named checkpoint in the project's history. Pushing uploads local commits to GitHub.

> **Final checkpoint:** The game still runs, and the `Add robot movement` commit has been pushed.

## Optional customizations

If you finish early, try one change at a time:

- load `robot-purple.png` instead of `robot.png` in `create()`.

Run after each change, and keep the same variables and overall structure for later sessions.
