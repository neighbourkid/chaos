import robocode.*;
import java.util.Random;

public class ZigZagBot extends AdvancedRobot {
    private static final double AMPLITUDE = 100; // Maximum zig-zag distance
    private static final double FREQUENCY = 0.1; // Controls the zig-zag frequency
    private static final double RANDOMNESS_SCALE = 50; // Intensity of randomness
    private static final double WALL_THRESHOLD = 50; // Minimum distance to wall
    private static final double LOW_ENERGY_THRESHOLD = 20; // Energy level to stop firing
    private static final double SAFE_DISTANCE = 400; // Minimum distance to maintain from enemies
    private Random random = new Random(); // Random number generator
    private boolean movingForward = true; // Tracks current movement direction

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
        double time = getTime(); // Time since the start of the match
        // Calculate zig-zag offset
        double zigzagOffset = AMPLITUDE * Math.sin(FREQUENCY * time);
        // Add randomness to the movement
        double randomOffset = RANDOMNESS_SCALE * (random.nextDouble() - 0.5); // Random value between -scale/2 and +scale/2
        double moveDistance = zigzagOffset + randomOffset;

        // Move and turn
        setAhead(moveDistance); // Move forward or backward
        setTurnRight(45); // Zig-zag by turning a bit
        execute(); 
    }

    private boolean nearWall() {
        double x = getX();
        double y = getY();
        double battlefieldWidth = getBattleFieldWidth();
        double battlefieldHeight = getBattleFieldHeight();

        // Check if the bot is too close to any wall
        return (x < WALL_THRESHOLD || y < WALL_THRESHOLD ||
                x > battlefieldWidth - WALL_THRESHOLD ||
                y > battlefieldHeight - WALL_THRESHOLD);
    }

    private void avoidWall() {
        double x = getX();
        double y = getY();
        double battlefieldWidth = getBattleFieldWidth();
        double battlefieldHeight = getBattleFieldHeight();

        // Turn away from the closest wall
        if (x < WALL_THRESHOLD) {
            setTurnRight(90); // Turn right to move away from left wall
        } else if (x > battlefieldWidth - WALL_THRESHOLD) {
            setTurnLeft(90); // Turn left to move away from right wall
        } else if (y < WALL_THRESHOLD) {
            setTurnRight(90); // Turn right to move away from bottom wall
        } else if (y > battlefieldHeight - WALL_THRESHOLD) {
            setTurnLeft(90); // Turn left to move away from top wall
        }

        setBack(100); // Move back to create distance from the wall
        execute();
    }

   public void onScannedRobot(ScannedRobotEvent event) {
    double enemyEnergy = event.getEnergy();
    double energyDrop = previousEnergy - enemyEnergy;
    double distance = event.getDistance();

    // If the enemy is too close, move away (safe distance handling)
    if (distance < SAFE_DISTANCE) {
        setTurnRight(event.getBearing() + 90); // Turn perpendicular to the enemy
        setBack(150 + random.nextInt(50)); // Move back with randomness to avoid close-range attack
    } else {
        // Zig-zag movement and fire at a safe distance
        zigZagMove();
        
        // Fire only if energy is above the threshold
        if (getEnergy() > LOW_ENERGY_THRESHOLD) {
            double firePower = Math.min(3, Math.max(1, 400 / distance)); // Scale firepower based on distance
            setFire(firePower);
        }
    }

    // Detect if the enemy fired a bullet based on energy drop
    if (energyDrop > 0 && energyDrop <= 3) { // Bullet energy ranges between 0.1 and 3.0
        performEvasiveManeuver(event.getBearing());
    }

    // Update previous energy for next scan
    previousEnergy = enemyEnergy;

    execute(); 
}


    private void performEvasiveManeuver(double enemyBearing) {
        // Turn perpendicular to the enemy and move
        setTurnRight(normalizeBearing(enemyBearing + 180));
        setAhead(300 + random.nextDouble() * 50); // Add some randomness to the movement distance
        execute();
    }

    public void onHitRobot(HitRobotEvent e) {
        // Reverse direction when colliding with another robot
        reverseDirection();
    }

    public void onHitByBullet(HitByBulletEvent event) {
        // Reverse direction to avoid becoming an easy target
        reverseDirection();
    }

    public void onHitWall(HitWallEvent event) {
        // Reverse direction when hitting a wall
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
