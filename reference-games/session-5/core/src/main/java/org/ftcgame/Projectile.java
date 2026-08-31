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
