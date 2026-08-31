# Session 2 — Make Movement Consistent

In this session, you will make movement consistent at different frame rates and give fixed game settings clear names. Keep using the same cycle as Session 1:

```text
predict -> edit -> run -> observe
```

When surrounding code is shown for context, copy only the new lines named in the instruction.

## 1. Start from your working game

Use Android Studio to pull any instructor updates, then open:

```text
core/src/main/java/org/ftcgame/RobotGame.java
```

Run the game and confirm that:

- the robot appears;
- W/A/S/D move it in four directions;
- the robot moves by `robotSpeed` during every frame.

Stop the game before editing.

> **Checkpoint:** The completed Session 1 game runs correctly.

## 2. What does “per frame” mean?

The `render()` method runs once for every frame the game draws. The Session 1 movement code adds or subtracts `robotSpeed` every time `render()` runs:

```java
robotY = robotY + robotSpeed;
```

The robot currently moves by `robotSpeed` pixels during every frame. If one computer draws more frames each second than another computer, which robot do you predict will move farther in one second?

We can test that prediction by temporarily asking libGDX to draw only 10 frames per second.

## 3. Temporarily lower the frame rate

Find `create()`. Add this line after the robot texture is loaded:

```java
Gdx.graphics.setForegroundFPS(10);
```

The finished `create()` method should look like this:

```java
@Override
public void create() {
    batch = new SpriteBatch();
    robotTexture = new Texture("robot.png");
    Gdx.graphics.setForegroundFPS(10);
}
```

This line asks the game to draw about 10 frames each second. It is experiment code, not part of the finished game.

### Run it now

Run the game and hold D for about two seconds. Compare the movement with the Session 1 game.

The robot should move much more slowly: it still moves by the same amount during each frame, but far fewer frames occur each second.

> **Checkpoint:** At 10 frames per second, the frame-based movement is noticeably slower.

If the difference is not obvious, confirm that the new line is inside `create()`.

## 4. Change to pixels per second

Instead of deciding how far the robot moves per frame, we will decide how far it should move per second.

Change:

```java
private float robotSpeed = 3;
```

to:

```java
private float robotSpeed = 250;
```

The new value means 250 pixels per second. Movement that changes an object's position without turning it is called **translation**. `robotSpeed` controls how quickly the robot translates across the screen.

The movement statements do not use seconds yet, so do not run the game at this point.

## 5. Measure the time between frames

At the beginning of `render()`, before `ScreenUtils.clear(...)`, add:

```java
float deltaTime = Gdx.graphics.getDeltaTime();
```

The new `deltaTime` variable should sit at the start of `render()` like this:

```java
@Override
public void render() {
    float deltaTime = Gdx.graphics.getDeltaTime();

    ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1);
```

`deltaTime` is the number of seconds since the previous frame. It is usually a small decimal number. The variable is created inside `render()` and can be used only within that method. A variable limited to a particular method or part of the code is called a **local variable**. A new `deltaTime` value is created during every call to `render()`.

## 6. Use elapsed time for movement

Change the W-key movement from:

```java
robotY = robotY + robotSpeed;
```

to:

```java
robotY = robotY + robotSpeed * deltaTime;
```

Java uses `*` for multiplication. `robotSpeed * deltaTime` multiplies pixels per second by seconds, producing the number of pixels to move during this frame.

The calculation now means:

```text
pixels per second × seconds since the last frame = pixels this frame
```

Make the same change for S, A, and D. The four input statements should become:

```java
if (Gdx.input.isKeyPressed(Input.Keys.W)) {
    robotY = robotY + robotSpeed * deltaTime;
}

if (Gdx.input.isKeyPressed(Input.Keys.S)) {
    robotY = robotY - robotSpeed * deltaTime;
}

if (Gdx.input.isKeyPressed(Input.Keys.A)) {
    robotX = robotX - robotSpeed * deltaTime;
}

if (Gdx.input.isKeyPressed(Input.Keys.D)) {
    robotX = robotX + robotSpeed * deltaTime;
}
```

### Run it now

The game is still limited to about 10 frames per second. Run it and hold D for about two seconds again.

The animation may look choppy, but the robot should travel at approximately the intended speed. A longer frame has a larger `deltaTime`, so the robot moves farther during that frame.

> **Checkpoint:** Time-based movement remains usable at 10 frames per second even though the animation looks choppy.

## 7. Repeat the experiment at normal speed

Return to `create()` and remove this temporary line:

```java
Gdx.graphics.setForegroundFPS(10);
```

Run the game again. Hold D for about two seconds and compare the distance with the low-frame-rate test.

The animation should be smoother, while the distance traveled in the same time stays similar. More frames occur, but each uses a smaller `deltaTime`.

> **Checkpoint:** Changing the frame rate changes smoothness, but no longer causes a large change in movement speed.

## 8. Give game settings clear names

We are adding several literal numbers to the code. For example, `250` controls the robot's speed, while `100` sets each starting coordinate. Numbers like these are sometimes called **magic numbers** because their purpose may not be clear when they appear elsewhere in the code.

In Java, we can assign these values to **named constants**. Names such as `ROBOT_SPEED` and `ROBOT_START_X` make the values easier to understand and give us one place to change each setting.

Change:

```java
private float robotX = 100;
private float robotY = 100;
private float robotSpeed = 250;
```

to:

```java
private static final float ROBOT_START_X = 100;
private static final float ROBOT_START_Y = 100;
private static final float ROBOT_SPEED = 250;

private float robotX = ROBOT_START_X;
private float robotY = ROBOT_START_Y;
```

The new declarations should remain below the texture fields:

```java
private Texture robotTexture;

private static final float ROBOT_START_X = 100;
private static final float ROBOT_START_Y = 100;
private static final float ROBOT_SPEED = 250;

private float robotX = ROBOT_START_X;
private float robotY = ROBOT_START_Y;
```

A **constant** is a named value that does not change while the program runs. These three values configure the robot, so their uppercase names make them easy to recognize as constants.

`final` prevents the program from assigning a different value to a constant. `static` means the value belongs to the `RobotGame` class, so every part of the game uses the same setting.

`private` means that only code inside `RobotGame` can use these constants. Java uses words such as `private` and `public` to control which code can access a field. A `public` field can also be used by code in another class. These constants are `private` because the game currently uses them only inside `RobotGame`.

`robotX` and `robotY` are not constants. Movement changes them while the game runs.

Change the four movement statements from:

```java
if (Gdx.input.isKeyPressed(Input.Keys.W)) {
    robotY = robotY + robotSpeed * deltaTime;
}

if (Gdx.input.isKeyPressed(Input.Keys.S)) {
    robotY = robotY - robotSpeed * deltaTime;
}

if (Gdx.input.isKeyPressed(Input.Keys.A)) {
    robotX = robotX - robotSpeed * deltaTime;
}

if (Gdx.input.isKeyPressed(Input.Keys.D)) {
    robotX = robotX + robotSpeed * deltaTime;
}
```

to:

```java
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
```

### Run it now

Run the game and test all four movement keys. Renaming the setting should not change the robot's movement.

> **Checkpoint:** Movement still behaves the same, and fixed robot settings have uppercase constant names while robot positions remain variables.

## 9. Check the finished game

Before saving your work, confirm that:

- the temporary `setForegroundFPS(10)` line has been removed;
- movement uses `ROBOT_SPEED * deltaTime` in all four directions;
- movement behaves consistently at different frame rates;
- the robot's speed and starting coordinates use named constants;
- the robot position remains in variables that movement can change.

Ask for help if any check fails. Fix the game and run the checklist again before committing.

## 10. Commit and push

Use Android Studio's Git tools:

1. Open the Commit window.
2. Review the changed Java file.
3. Enter this commit message:

   ```text
   Make movement frame-rate independent
   ```

4. Commit the changes.
5. Push the commit to your assigned GitHub repository.

> **Final checkpoint:** The finished game works, and the `Make movement frame-rate independent` commit has been pushed.

## Optional customizations

If you finish early, make one change at a time and run after each one:

- adjust `ROBOT_SPEED` while keeping it measured in pixels per second;
- choose a different starting position.
