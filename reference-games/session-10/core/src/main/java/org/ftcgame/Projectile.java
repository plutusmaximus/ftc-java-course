package org.ftcgame;

public class Projectile {
    public static final int SIZE = 16;
    private static final float SPEED = 400;

    private float x;
    private float y;
    private double directionX;
    private double directionY;

    public Projectile(
        float startX,
        float startY,
        double startDirectionX,
        double startDirectionY) {
        x = startX;
        y = startY;
        directionX = startDirectionX;
        directionY = startDirectionY;
    }

    public void update(float deltaTime) {
        x = (float) (x + directionX * SPEED * deltaTime);
        y = (float) (y + directionY * SPEED * deltaTime);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isTouching(float objectX, float objectY, float objectSize) {
        return x < objectX + objectSize &&
            x + SIZE > objectX &&
            y < objectY + objectSize &&
            y + SIZE > objectY;
    }

    public boolean isOutside(float width, float height) {
        return x + SIZE < 0 ||
            x > width ||
            y + SIZE < 0 ||
            y > height;
    }
}
