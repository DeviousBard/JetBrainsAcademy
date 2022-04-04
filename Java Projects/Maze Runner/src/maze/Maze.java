package maze;

import java.io.*;
import java.util.*;

public class Maze {
    private static final Random RNG = new Random();

    private enum MazeArtifact {
        WALL(false, 1, "\u2588\u2588"), PASSAGE(true, 0, "  "), ESCAPE_ROUTE(true, 2, "//");

        public final boolean mapValue;
        public final int gridValue;
        public final String displayValue;


        MazeArtifact(boolean mapValue, int gridValue, String displayValue) {
            this.mapValue = mapValue;
            this.gridValue = gridValue;
            this.displayValue = displayValue;
        }

        static MazeArtifact byMapValue(boolean mapValue) {
            return mapValue ? PASSAGE : WALL;
        }

        static MazeArtifact byGridValue(int gridValue) {
            return gridValue == 0 ? PASSAGE : gridValue == 1 ? WALL : ESCAPE_ROUTE;
        }
    }

    private int columns;
    private int rows;
    private int[][] grid;
    private int[][] solutionGrid;
    private int[] entranceCell;
    private int[] exitCell;

    private Maze() {
    }

    public void saveMaze(String fileName) throws MazeException {
        try (FileWriter fw = new FileWriter(fileName); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(String.valueOf(this.rows));
            bw.write('\n');
            bw.write(this.entranceCell[0] + " " + this.entranceCell[1]);
            bw.write('\n');
            bw.write(this.exitCell[0] + " " + this.exitCell[1]);
            bw.write('\n');
            for (int[] row : grid) {
                bw.write(String.join(" ", Arrays.stream(row).mapToObj(String::valueOf).toArray(String[]::new)));
                bw.write('\n');
            }
        } catch (IOException e) {
            throw new MazeException(String.format("Cannot save the maze. java.io.IOException: %s", e.getMessage()));
        }
    }

    public static Maze loadMaze(String fileName) throws MazeException {
        Maze maze = new Maze();
        try (FileReader fr = new FileReader(fileName); BufferedReader br = new BufferedReader(fr)) {
            int dimension = Integer.parseInt(br.readLine());
            int[] entranceCell = Arrays.stream(br.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();
            int[] exitCell = Arrays.stream(br.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();
            maze.rows = dimension;
            maze.columns = dimension;
            maze.entranceCell = entranceCell;
            maze.exitCell = exitCell;
            int[][] grid = new int[dimension][dimension];
            String line;
            int index = 0;
            while ((line = br.readLine()) != null) {
                int[] row = Arrays.stream(line.split(" ")).mapToInt(Integer::parseInt).toArray();
                grid[index] = row;
                index++;
            }
            maze.grid = grid;
            maze.solutionGrid = maze.buildSolution();
        } catch (FileNotFoundException fe) {
            throw new MazeException(String.format("The file %s does not exist", fileName));
        } catch (IOException e) {
            throw new MazeException("Cannot load the maze. It has an invalid format");
        }
        return maze;
    }

    public static Maze generateMaze() {
        Maze maze = new Maze();
        int[] mazeDimensions = maze.getMazeDimensions();
        maze.rows = mazeDimensions[0];
        maze.columns = mazeDimensions[1];
        maze.grid = maze.generateMaze(maze.columns, maze.rows);
        maze.solutionGrid = maze.buildSolution();
        return maze;
    }

    public void printMaze() {
        this.printMaze(this.grid, this.columns, this.rows);
    }

    public void printSolution() {
        this.printMaze(this.solutionGrid, this.columns, this.rows);
    }

    private void printMaze(int[][] grid, int columns, int rows) {
        final StringBuilder sb = new StringBuilder();
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                sb.append(MazeArtifact.byGridValue(grid[y][x]).displayValue);
            }
            sb.append('\n');
        }
        System.out.println(sb);
    }

    private int[][] buildSolution() {
        Graph g = this.buildGraph();
        String entranceCellName = this.entranceCell[0] + "," + this.entranceCell[1];
        Node entranceNode = null;
        for (Node n : g.getNodes()) {
            if (n.getName().equals(entranceCellName)) {
                entranceNode = n;
                break;
            }
        }
        assert entranceNode != null;
        Graph g1 = Dijkstra.calculateShortestPathFromSource(g, entranceNode);
        String exitCellName = this.exitCell[0] + "," + this.exitCell[1];
        Node exitNode = null;
        for (Node n : g1.getNodes()) {
            if (n.getName().equals(exitCellName)) {
                exitNode = n;
                break;
            }
        }
        assert exitNode != null;
        Set<String> pathNodeNames = new HashSet<>();
        for (Node node : exitNode.getShortestPath()) {
            pathNodeNames.add(node.getName());
        }
        pathNodeNames.add(exitCellName);

        int[][] grid = new int[this.rows][this.columns];
        for (int y = 0; y < this.rows; y++) {
            for (int x = 0; x < this.columns; x++) {
                String cellName = y + "," + x;
                if (pathNodeNames.contains(cellName)) {
                    grid[y][x] = 2;
                } else {
                    grid[y][x] = this.grid[y][x];
                }
            }
        }
        return grid;
    }

    private Graph buildGraph() {
        Graph g = new Graph();
        Map<String, Node> nodeMap = new HashMap<>();
        int[][] deltas = new int[][]{
                new int[]{0, 1},
                new int[]{0, -1},
                new int[]{-1, 0},
                new int[]{1, 0}
        };
        for (int y = 0; y < this.rows; y++) {
            for (int x = 0; x < this.columns; x++) {
                if (this.grid[y][x] == 0) {
                    String nodeName = y + "," + x;
                    Node n = nodeMap.computeIfAbsent(nodeName, Node::new);
                    g.addNode(n);
                    for (int[] delta : deltas) {
                        int dy = y + delta[0];
                        int dx = x + delta[1];
                        if (dy >= 0 && dy < this.rows && dx >= 0 && dx < this.columns && this.grid[dy][dx] == 0) {
                            String adjNodeName = dy + "," + dx;
                            Node adjNode = nodeMap.computeIfAbsent(adjNodeName, Node::new);
                            n.addDestination(adjNode, 1);
                        }
                    }
                }
            }
        }
        return g;
    }

    private int[] getMazeDimensions() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the size of a new maze");
        int dimension = scanner.nextInt();
        return new int[]{dimension, dimension};
    }

    private int[][] generateMaze(int columns, int rows) {
        int[][] maze = new int[rows][columns];
        boolean[][] map = buildMap(columns - 2, rows - 2);
        for (int x = 0; x < columns; x++) {
            maze[0][x] = 1;
            maze[rows - 1][x] = 1;
        }
        for (int y = 1; y < rows - 1; y++) {
            maze[y][0] = 1;
            maze[y][columns - 1] = 1;
            for (int x = 1; x < columns - 1; x++) {
                maze[y][x] = MazeArtifact.byMapValue(map[y - 1][x - 1]).gridValue;
            }
        }
        this.addEgresses(maze, columns, rows);
        return maze;
    }

    private void addEgresses(int[][] maze, int columns, int rows) {
        boolean topBottomEgress = RNG.nextBoolean();
        int x;
        int y;
        while (true) {
            if (topBottomEgress) {
                y = 0;
                x = RNG.nextInt(columns - 2) + 1;
                if (maze[y + 1][x] == 0) {
                    maze[y][x] = 0;
                    this.entranceCell = new int[]{y, x};
                    break;
                }
            } else {
                x = 0;
                y = RNG.nextInt(rows - 2) + 1;
                if (maze[y][x + 1] == 0) {
                    maze[y][x] = 0;
                    this.entranceCell = new int[]{y, x};
                    break;
                }
            }
        }
        while (true) {
            if (topBottomEgress) {
                y = rows - 1;
                x = RNG.nextInt(columns - 2) + 1;
                if (maze[y - 2][x] == 0) {
                    maze[y][x] = 0;
                    maze[y - 1][x] = 0;
                    this.exitCell = new int[]{y, x};
                    break;
                }
            } else {
                x = columns - 1;
                y = RNG.nextInt(rows - 2) + 1;
                if (maze[y][x - 2] == 0) {
                    maze[y][x] = 0;
                    maze[y][x - 1] = 0;
                    this.exitCell = new int[]{y, x};
                    break;
                }
            }
        }
    }

    private boolean[][] buildMap(int columns, int rows) {
        boolean[][] map = new boolean[rows][columns];
        final LinkedList<int[]> frontiers = new LinkedList<>();
        int startingX = 0;
        int startingY = 0;
        frontiers.add(new int[]{startingY, startingX, startingY, startingX});

        while (!frontiers.isEmpty()) {
            final int[] f = frontiers.remove(RNG.nextInt(frontiers.size()));
            int y = f[2];
            int x = f[3];
            if (map[y][x] == MazeArtifact.WALL.mapValue) {
                map[f[0]][f[1]] = map[y][x] = MazeArtifact.PASSAGE.mapValue;
                if (x >= 2 && map[y][x - 2] == MazeArtifact.WALL.mapValue) {
                    frontiers.add(new int[]{y, x - 1, y, x - 2});
                }
                if (y >= 2 && map[y - 2][x] == MazeArtifact.WALL.mapValue) {
                    frontiers.add(new int[]{y - 1, x, y - 2, x});
                }
                if (x < columns - 2 && map[y][x + 2] == MazeArtifact.WALL.mapValue) {
                    frontiers.add(new int[]{y, x + 1, y, x + 2});
                }
                if (y < rows - 2 && map[y + 2][x] == MazeArtifact.WALL.mapValue) {
                    frontiers.add(new int[]{y + 1, x, y + 2, x});
                }
            }
        }
        return map;
    }

    static class Graph {
        private final Set<Node> nodes = new HashSet<>();

        public void addNode(Node nodeA) {
            nodes.add(nodeA);
        }

        public Set<Node> getNodes() {
            return nodes;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Node n : nodes) {
                sb.append("Node: ").append(n.getName()).append('\n');
                sb.append("   Adjacent Nodes: ");
                int i = 0;
                for (Node key : n.getAdjacentNodes().keySet()) {
                    sb.append(key.getName());
                    if (i < n.getAdjacentNodes().keySet().size() - 1) {
                        sb.append(" | ");
                    }
                    i++;
                }
                sb.append('\n');
            }
            return sb.toString();
        }
    }

    static class Node implements Comparable<Node> {
        private final String name;
        private LinkedList<Node> shortestPath = new LinkedList<>();
        private Integer distance = Integer.MAX_VALUE;
        private final Map<Node, Integer> adjacentNodes = new HashMap<>();

        public Node(String name) {
            this.name = name;
        }

        public void addDestination(Node destination, int distance) {
            adjacentNodes.put(destination, distance);
        }

        public String getName() {
            return name;
        }

        public Map<Node, Integer> getAdjacentNodes() {
            return adjacentNodes;
        }

        public Integer getDistance() {
            return distance;
        }

        public void setDistance(Integer distance) {
            this.distance = distance;
        }

        public List<Node> getShortestPath() {
            return shortestPath;
        }

        public void setShortestPath(LinkedList<Node> shortestPath) {
            this.shortestPath = shortestPath;
        }

        @Override
        public int compareTo(Node n) {
            return this.getName().compareTo(n.getName());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Node) {
                Node n = (Node)obj;
                return n.getName().equals(this.getName());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.getName().hashCode();
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    static class Dijkstra {

        public static Graph calculateShortestPathFromSource(Graph graph, Node source) {
            source.setDistance(0);

            Set<Node> settledNodes = new HashSet<>();
            Set<Node> unsettledNodes = new HashSet<>();
            unsettledNodes.add(source);

            while (unsettledNodes.size() != 0) {
                Node currentNode = getMinimumDistanceNode(unsettledNodes);
                unsettledNodes.remove(currentNode);
                for (Map.Entry<Node, Integer> adjacencyPair : currentNode.getAdjacentNodes().entrySet()) {
                    Node adjacentNode = adjacencyPair.getKey();
                    Integer edgeWeigh = adjacencyPair.getValue();

                    if (!settledNodes.contains(adjacentNode)) {
                        calculateMinimumDistance(adjacentNode, edgeWeigh, currentNode);
                        unsettledNodes.add(adjacentNode);
                    }
                }
                settledNodes.add(currentNode);
            }
            return graph;
        }

        private static void calculateMinimumDistance(Node evaluationNode, Integer edgeWeigh, Node sourceNode) {
            Integer sourceDistance = sourceNode.getDistance();
            if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
                evaluationNode.setDistance(sourceDistance + edgeWeigh);
                LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
                shortestPath.add(sourceNode);
                evaluationNode.setShortestPath(shortestPath);
            }
        }

        private static Node getMinimumDistanceNode(Set<Node> unsettledNodes) {
            Node minimumDistanceNode = null;
            int minimumDistance = Integer.MAX_VALUE;
            for (Node node : unsettledNodes) {
                int nodeDistance = node.getDistance();
                if (nodeDistance < minimumDistance) {
                    minimumDistance = nodeDistance;
                    minimumDistanceNode = node;
                }
            }
            return minimumDistanceNode;
        }
    }

    static class MazeException extends Exception {

        public MazeException(String message) {
            super(message);
        }
    }
}