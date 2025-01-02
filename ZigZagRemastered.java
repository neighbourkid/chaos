package ck;
import robocode.*;
import java.awt.Color;
import java.util.Random;

public class ZigZagRemastered extends AdvancedRobot {
    private static final double AMPLITUDE = 100; // Maximum zig-zag distance
    private static final double FREQUENCY = 0.1; // Controls the zig-zag frequency
    private static final double RANDOMNESS_SCALE = 50; // Intensity of randomness
    private static final double WALL_THRESHOLD = 50; // Minimum distance to wall
    private static final double LOW_ENERGY_THRESHOLD = 2; // Minimum energy to fire
    private static final double SAFE_DISTANCE = 400; // Minimum distance to maintain from enemies
    private Random random = new Random();
    private boolean movingForward = true;

    private double previousEnergy = 100; // Initial enemy energy

    public void run() {
        // Continuous radar scanning
        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        while (true) {
            if (nearWall()) {
                avoidWall();
            } else {
                zigZagMove();
            }
            scan();
        }
    }

    private void zigZagMove() {
        double time = getTime();
        double zigzagOffset = AMPLITUDE * Math.sin(FREQUENCY * time);
        double moveDistance = zigzagOffset;

        // Move and turn
        setAhead(moveDistance);
        setTurnRight(45); // Zig-zag by turning a bit
        execute();
    }

    private boolean nearWall() {
        double x = getX();
        double y = getY();
        double battlefieldWidth = getBattleFieldWidth();
        double battlefieldHeight = getBattleFieldHeight();

        return (x < WALL_THRESHOLD || y < WALL_THRESHOLD ||
                x > battlefieldWidth - WALL_THRESHOLD ||
                y > battlefieldHeight - WALL_THRESHOLD);
    }

    private void avoidWall() {
        double x = getX();
        double y = getY();
        double battlefieldWidth = getBattleFieldWidth();
        double battlefieldHeight = getBattleFieldHeight();

        if (x < WALL_THRESHOLD) {
            setTurnRight(90);
        } else if (x > battlefieldWidth - WALL_THRESHOLD) {
            setTurnLeft(90);
        } else if (y < WALL_THRESHOLD) {
            setTurnRight(90);
        } else if (y > battlefieldHeight - WALL_THRESHOLD) {
            setTurnLeft(90);
        }

        setBack(150);
        execute();
    }

    public void onScannedRobot(ScannedRobotEvent event) {
        double enemyEnergy = event.getEnergy();
        double energyDrop = previousEnergy - enemyEnergy;
        double distance = event.getDistance();

        // If the enemy is too close, move away
        if (distance < SAFE_DISTANCE) {
            setTurnRight(event.getBearing() + 90);
            setBack(SAFE_DISTANCE - distance);
        } else {
            zigZagMove();
        }

        // Fire only if energy is above the threshold
        if (getEnergy() > LOW_ENERGY_THRESHOLD) {
            double firePower = Math.min(3, Math.max(1, 400 / distance));
            setFire(firePower);
        } else {
            setFire(1); // Low-power fire for aggression
        }

        // Bullet evasion based on enemy energy drop
        if (energyDrop > 0 && energyDrop <= 3) {
            performEvasiveManeuver(event.getBearing());
        }

        previousEnergy = enemyEnergy;
        execute();
    }

    private void performEvasiveManeuver(double enemyBearing) {
        setTurnRight(normalizeBearing(enemyBearing + 90)); // Perpendicular movement
        setAhead(150); // Move away
        execute();
    }

    public void onHitRobot(HitRobotEvent e) {
        reverseDirection();
    }

    public void onHitByBullet(HitByBulletEvent event) {
        reverseDirection();
    }

    public void onHitWall(HitWallEvent event) {
        reverseDirection();
    }

    private void reverseDirection() {
        if (movingForward) {
            setBack(100);
            movingForward = false;
        } else {
            setAhead(100);
            movingForward = true;
        }
        execute();
    }

    private double normalizeBearing(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }
}
