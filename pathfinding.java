import java.util.*;

public class DijkstraBot {
    private static final char EMPTY = '.';
    private static final char BOT = 'B';
    private static final char OBSTACLE = 'O';
    private static final char TARGET = 'T';
    private static final char PATH = 'P';

    private static final int[][] DIRECTIONS = {
            {-1, 0}, // Up
            {1, 0},  // Down
            {0, -1}, // Left
            {0, 1}   // Right
    };

    private static class Node implements Comparable<Node> {
        int x, y, distance;

        Node(int x, int y, int distance) {
            this.x = x;
            this.y = y;
            this.distance = distance;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.distance, other.distance);
        }
    }

    private static char[][] grid;
    private static int rows, cols;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Initialize grid
        System.out.println("Enter grid size (rows and columns): ");
        rows = scanner.nextInt();
        cols = scanner.nextInt();
        grid = new char[rows][cols];
        initializeGrid();

        // Place obstacles
        System.out.println("Enter number of obstacles: ");
        int obstacleCount = scanner.nextInt();
        placeObstacles(scanner, obstacleCount);

        // Place bot and target
        System.out.println("Enter bot starting position (x y): ");
        int botX = scanner.nextInt();
        int botY = scanner.nextInt();
        grid[botX][botY] = BOT;

        System.out.println("Enter target position (x y): ");
        int targetX = scanner.nextInt();
        int targetY = scanner.nextInt();
        grid[targetX][targetY] = TARGET;

        // Display initial grid
        System.out.println("Initial grid:");
        displayGrid();

        // Find shortest path
        List<int[]> path = findShortestPath(botX, botY, targetX, targetY);

        // Display result
        if (path != null) {
            System.out.println("Path found:");
            markPath(path);
            displayGrid();
        } else {
            System.out.println("No path found!");
        }
    }

    private static void initializeGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = EMPTY;
            }
        }
    }

    private static void placeObstacles(Scanner scanner, int count) {
        System.out.println("Enter obstacle positions (x y): ");
        for (int i = 0; i < count; i++) {
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            grid[x][y] = OBSTACLE;
        }
    }

    private static void displayGrid() {
        for (char[] row : grid) {
            for (char cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    private static List<int[]> findShortestPath(int startX, int startY, int targetX, int targetY) {
        PriorityQueue<Node> queue = new PriorityQueue<>();
        int[][] distances = new int[rows][cols];
        int[][][] previous = new int[rows][cols][2];
        boolean[][] visited = new boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            Arrays.fill(distances[i], Integer.MAX_VALUE);
        }

        distances[startX][startY] = 0;
        queue.add(new Node(startX, startY, 0));

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (visited[current.x][current.y]) continue;
            visited[current.x][current.y] = true;

            if (current.x == targetX && current.y == targetY) {
                return reconstructPath(previous, targetX, targetY);
            }

            for (int[] direction : DIRECTIONS) {
                int nextX = current.x + direction[0];
                int nextY = current.y + direction[1];

                if (isValid(nextX, nextY) && !visited[nextX][nextY]) {
                    int newDistance = distances[current.x][current.y] + 1;
                    if (newDistance < distances[nextX][nextY]) {
                        distances[nextX][nextY] = newDistance;
                        queue.add(new Node(nextX, nextY, newDistance));
                        previous[nextX][nextY][0] = current.x;
                        previous[nextX][nextY][1] = current.y;
                    }
                }
            }
        }

        return null; // No path found
    }

    private static boolean isValid(int x, int y) {
        return x >= 0 && x < rows && y >= 0 && y < cols && grid[x][y] != OBSTACLE;
    }

    private static List<int[]> reconstructPath(int[][][] previous, int targetX, int targetY) {
        List<int[]> path = new ArrayList<>();
        int x = targetX, y = targetY;

        while (previous[x][y][0] != 0 || previous[x][y][1] != 0) {
            path.add(new int[]{x, y});
            int tempX = previous[x][y][0];
            int tempY = previous[x][y][1];
            x = tempX;
            y = tempY;
        }

        Collections.reverse(path);
        return path;
    }

    private static void markPath(List<int[]> path) {
        for (int[] pos : path) {
            int x = pos[0];
            int y = pos[1];
            if (grid[x][y] != BOT && grid[x][y] != TARGET) {
                grid[x][y] = PATH;
            }
        }
    }
}
