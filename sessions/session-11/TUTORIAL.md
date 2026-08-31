# Session 11 — Normalize Wheel Powers

In the last session, combining movement controls could produce wheel powers greater than `1` or less than `-1`. In this session, you will scale all four powers together so they stay between `-1` and `1` without changing the robot's requested movement direction.

Keep using this cycle:

```text
predict -> edit -> run -> observe
```

## 1. Start from your working game

Use Android Studio to pull any instructor updates, then open:

```text
core/src/main/java/org/ftcgame/RobotGame.java
```

Run the game and confirm that W/S drive forward and backward, A/D strafe, and the arrow keys turn through four mecanum wheel powers.

> **Checkpoint:** The completed Session 10 game runs correctly.

## 2. Find the largest power magnitude

A negative power describes direction, but its distance from zero describes its **magnitude**. `Math.abs(...)` returns that magnitude:

```text
Math.abs(0.5)  -> 0.5
Math.abs(-0.5) -> 0.5
```

`Math.max(...)` compares two values and returns the larger one. The code first compares the two front-wheel magnitudes. It then compares that result with each back-wheel magnitude, so `max` ends with the largest of all four.

Add this complete method immediately after `updateWheelPowers()` and before `moveRobot(...)`:

```java
private void normalizeWheelPowers() {
    float max;

    max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
    max = Math.max(max, Math.abs(backLeftPower));
    max = Math.max(max, Math.abs(backRightPower));

    if (max > 1.0f) {
        frontLeftPower /= max;
        frontRightPower /= max;
        backLeftPower /= max;
        backRightPower /= max;
    }
}
```

The wheel methods should now appear together:

```java
private void updateWheelPowers() {
    // Read controls and calculate unscaled powers.
}

private void normalizeWheelPowers() {
    // Scale all powers together when one exceeds the allowed range.
}

private void moveRobot(float deltaTime) {
```

The `/=` operator divides a variable and stores the result back in that variable. For example, `frontLeftPower /= max;` means the same thing as `frontLeftPower = frontLeftPower / max;`. The `f` in `1.0f` tells Java that the decimal value is a `float`, matching the power variables.

If `max` is already one or less, the method changes nothing. If W and D produce `(2, 0, 0, 2)`, `max` is two. Dividing every value by two produces `(1, 0, 0, 1)`.

Using the same divisor preserves the relationship among the wheels. A value that was half as large as another value remains half as large after the division.

Scaling all four wheel powers together so none exceeds the allowed range is called **normalization**.

## 3. Normalize before moving

In `render()`, add the normalization call between updating wheel powers and moving the robot:

```java
normalizeWheelPowers();
```

The drivetrain and position-saving sequence should now look like this:

```java
updateWheelPowers();
normalizeWheelPowers();
float previousRobotX = robotX;
float previousRobotY = robotY;
moveRobot(deltaTime);
```

The order matters:

1. calculate the powers requested by the controls;
2. normalize them;
3. remember the robot's position before it moves;
4. calculate motion from the resulting powers.

For W and D together, `(2, 0, 0, 2)` normalizes to `(1, 0, 0, 1)`. `moveRobot(...)` converts those powers back into motion:

```text
axial   = (1 + 0 + 0 + 1) / 4 = 0.5
lateral = (1 - 0 - 0 + 1) / 4 = 0.5
```

The robot keeps the requested diagonal direction, but each part of its movement is slower than W or D alone.

A physical mecanum robot has the same limit because motor power cannot exceed full power. Diagonal movement is slower than horizontal movement.  Drivers compensate by anticipating combined maneuvers, or favoring forward motion when speed matters. In addition analog gamepad sticks give them finer control than pressed keyboard keys. Teams may also tune how commands share the available power, but that is outside this course.

### Run it now

Run the game and compare these inputs:

1. Hold W by itself. All wheels can use full forward power.
2. Hold W and D together. The combined request is scaled so neither active wheel exceeds full power.
3. Hold W and Left Arrow together. Translation and rotation share the available wheel-power range.
4. Try several three-key combinations.

The robot may drive or turn more slowly when you hold multiple controls at once. Each control requests some of the wheels' power, and adding those requests can produce a value greater than full power. Normalization scales all four values down to fit the allowed range, making each requested movement slower than when its control is used alone. The same thing happens on a real robot because its motor commands also cannot exceed full power.

> **Checkpoint:** Every wheel power remains within its allowed range, including during combined translation and rotation.

## 4. Verify normalization with one controlled log

Temporarily add the wheel log at the end of `normalizeWheelPowers()` after the `if` block:

```java
Gdx.app.log("NORMALIZED", "FL=" + frontLeftPower
    + " FR=" + frontRightPower
    + " BL=" + backLeftPower
    + " BR=" + backRightPower);
```

The end of the method should look like this:

```java
if (max > 1.0f) {
    frontLeftPower /= max;
    frontRightPower /= max;
    backLeftPower /= max;
    backRightPower /= max;
}

Gdx.app.log("NORMALIZED", "FL=" + frontLeftPower
    + " FR=" + frontRightPower
    + " BL=" + backLeftPower
    + " BR=" + backRightPower);
```

Run the game and hold W and D. Confirm that the log shows `(1, 0, 0, 1)`, or values extremely close to that pattern.

Then hold S and A. The signs should reverse, producing `(-1, 0, 0, -1)`.

If a value is outside the range, inspect whether all four powers divide by `max`. If a movement direction is wrong, compare the axial, lateral, and yaw equations in `updateWheelPowers()` with the prediction table. Change one sign at a time, run again, and observe both the values and the robot.

Remove the temporary log after the experiment.

## 5. Compare the game with a real FTC drivetrain

The game now follows the same broad power flow as an FTC mecanum drivetrain:

```text
controller input -> axial, lateral, yaw
                 -> calculate four wheel powers
                 -> normalize the powers
```

On a real FTC robot, the program sends the four normalized powers to four motors. In the game, there are no motors, so `moveRobot(...)` converts those powers back into position and rotation changes that can be drawn on the screen.

A real robot may need different signs because of how its motors are mounted and configured, but the steps remain the same: read the controls, calculate four powers, normalize them, and send one power to each motor.

## 6. Test the complete game

Before saving your work, verify every required behavior:

1. W/S drive forward and backward relative to the robot.
2. A/D strafe left and right relative to the robot.
3. Left and Right Arrow turn in their established directions.
4. `updateWheelPowers()` calculates front-left, front-right, back-left, and back-right powers.
5. `normalizeWheelPowers()` keeps every power between `-1` and `1`.
6. All four powers are divided by the same largest magnitude when normalization is needed.
7. `moveRobot(...)` derives axial, lateral, and yaw motion from wheel powers and does not read movement keys.
8. Translation and rotation still use delta time.
9. Collecting each ball increases ammunition by one.
10. The Space key fires once per press only when ammunition is available.
11. Projectiles start at the robot's front and retain their firing direction.
12. Obstacle hits remove projectiles without scoring.
13. Goal hits award exactly one point.
14. Missed projectiles disappear after completely leaving the window.
15. Robot and projectile collisions remain axis-aligned.
16. The robot stops flush against the approached obstacle edge.
17. Holding H still displays the robot and obstacle hitboxes.
18. No temporary wheel-power log remains.

Ask for help if any check fails. Test one command at a time, predict the four powers, and compare the log pattern before changing a formula. Fix the game and repeat the checklist before committing.

## 7. Commit and push

Use Android Studio's Git tools:

1. Open the Commit window.
2. Confirm that `RobotGame.java` is the Java file you changed.
3. Review the highlighted changes.
4. Enter this commit message:

   ```text
   Add mecanum wheel-power movement
   ```

5. Commit the changes.
6. Push the commit to your assigned GitHub repository.

> **Final checkpoint:** The finished game works, and the `Add mecanum wheel-power movement` commit has been pushed.

## Optional customizations

If you finish early, make one change at a time:

- use the temporary log to predict and inspect another two-key combination, then remove it;
- change `ROBOT_SPEED` or `ROBOT_ROTATION_SPEED` while preserving the drivetrain methods;
- replace `robot.png` with another 128×128 transparent robot image at the same path.

Keep the four wheel-power fields, axial/lateral/yaw equations, normalization, and existing public gameplay behavior unchanged.
