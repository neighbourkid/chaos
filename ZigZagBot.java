import robocode.*;
import java.util.Random;

public class ZigZagBot extends AdvancedRobot {
    private static final double AMPLITUDE = 100; // Maximum zig-zag distance
    private static final double FREQUENCY = 0.1; // Controls the zig-zag frequency
    private static final double RANDOMNESS_SCALE = 50; // Intensity of randomness
    private Random random = new Random(); // Random number generator

    public void run() {
        while (true) {
            zigZagMove();
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
        execute(); // Execute the movement
    }
	public void onHitRobot(HitRobotEvent e) {
		// If he's in front of us, set back up a bit.
		if (e.getBearing() > -90 && e.getBearing() < 90) {
			back(100);
		} // else he's in back of us, so set ahead a bit.
		else {
			ahead(100);
		}
	}

    public void onScannedRobot(ScannedRobotEvent event) {
        // Fire when a robot is detected
        fire(1);
    }

    public void onHitByBullet(HitByBulletEvent event) {
        // Turn perpendicular to the bullet to avoid more hits
        setTurnRight(90 - event.getBearing());
        execute();
    }

    public void onHitWall(HitWallEvent event) {
        // Bounce off walls
        reverseDirection();
    }
}


