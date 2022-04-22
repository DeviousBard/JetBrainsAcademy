package metro;

import com.google.gson.Gson;
import graph.Graph;
import graph.IEdge;
import graph.INode;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HyperMetro extends Graph<StationData> {

    private static final double TRANSFER_TIME = 5.0d;
    private final Map<String, List<INode<StationData>>> metroLineTerminals = new HashMap<>();

    public HyperMetro(String fileName) {
        super();
        loadMetroLines(fileName);
    }

    public void runApp() {
        while (true) {
            String[] parsedCommand = getUserCommand();
            if (parsedCommand[0].equals("/exit")) {
                System.exit(0);
            } else if (parsedCommand[0].equals("/append") && (parsedCommand.length == 4 || parsedCommand.length == 3)) {
                INode<StationData> node = this.getNodeByLabel(parsedCommand[1] + "|" + parsedCommand[2]);
                double weight = 1.0d;
                if (parsedCommand.length == 4) {
                    weight = Double.parseDouble(parsedCommand[3]);
                }
                this.append(node, weight);
            } else if (parsedCommand[0].equals("/add-head") && (parsedCommand.length == 4 || parsedCommand.length == 3)) {
                INode<StationData> node = this.getNodeByLabel(parsedCommand[1] + "|" + parsedCommand[2]);
                double weight = 1.0d;
                if (parsedCommand.length == 4) {
                    weight = Double.parseDouble(parsedCommand[3]);
                }
                this.addHead(node, weight);
            } else if (parsedCommand[0].equals("/remove") && parsedCommand.length == 3) {
                INode<StationData> node = this.getNodeByLabel(parsedCommand[1] + "|" + parsedCommand[2], false);
                if (node == null) {
                    System.out.println("Invalid command");
                } else {
                    this.remove(node);
                }
            } else if (parsedCommand[0].equals("/output") && parsedCommand.length == 2) {
                List<INode<StationData>> terminals = metroLineTerminals.get(parsedCommand[1]);
                if (terminals == null) {
                    System.out.println("Invalid command");
                } else {
                    this.output(terminals.get(1));
                }
            } else if (parsedCommand[0].equals("/connect") && parsedCommand.length == 5) {
                INode<StationData> node1 = this.getNodeByLabel(parsedCommand[1] + "|" + parsedCommand[2], false);
                INode<StationData> node2 = this.getNodeByLabel(parsedCommand[3] + "|" + parsedCommand[4], false);
                if (node1 != null && node2 != null) {
                    this.connect(node1, node2);
                } else {
                    System.out.println("Invalid command");
                }
            } else if (parsedCommand[0].equals("/route") && parsedCommand.length == 5) {
                INode<StationData> node1 = this.getNodeByLabel(parsedCommand[1] + "|" + parsedCommand[2], false);
                INode<StationData> node2 = this.getNodeByLabel(parsedCommand[3] + "|" + parsedCommand[4], false);
                if (node1 != null && node2 != null) {
                    this.route(this.getNodeByLabel(node1.getLabel()), this.getNodeByLabel(node2.getLabel()));
                } else {
                    System.out.println("Invalid command");
                }
            } else if (parsedCommand[0].equals("/fastest-route") && parsedCommand.length == 5) {
                INode<StationData> node1 = this.getNodeByLabel(parsedCommand[1] + "|" + parsedCommand[2], false);
                INode<StationData> node2 = this.getNodeByLabel(parsedCommand[3] + "|" + parsedCommand[4], false);
                if (node1 != null && node2 != null) {
                    this.fastestRoute(node1, node2);
                } else {
                    System.out.println("Invalid command");
                }
            } else {
                System.out.println("Invalid command");
            }
        }
    }

    private String[] getLineStation(String label) {
        return label.split("\\|");
    }

    private void addHead(INode<StationData> node, double weight) {
        String[] lineStation = this.getLineStation(node.getLabel());
        List<INode<StationData>> terminals = this.metroLineTerminals.get(lineStation[0]);
        if (terminals.get(0) == null) {
            terminals.set(0, node);
        } else {
            this.addWeightedEdge(node, terminals.get(1), weight);
            this.addWeightedEdge(terminals.get(1), node, weight);
        }
        terminals.set(1, node);
        this.metroLineTerminals.put(lineStation[0], terminals);
    }

    private void append(INode<StationData> node, double weight) {
        String[] lineStation = this.getLineStation(node.getLabel());
        List<INode<StationData>> terminals = this.metroLineTerminals.get(lineStation[0]);
        if (terminals.get(0) == null) {
            terminals.set(1, node);
        } else {
            this.addWeightedEdge(node, terminals.get(0), weight);
            this.addWeightedEdge(terminals.get(0), node, weight);
        }
        terminals.set(0, node);
        this.metroLineTerminals.put(lineStation[0], terminals);
    }

    private void remove(INode<StationData> node) {
        String[] lineStation = getLineStation(node.getLabel());
        List<INode<StationData>> terminals = this.metroLineTerminals.get(lineStation[0]);
        List<IEdge<StationData>> adjacentNodes = this.getAdjacentNodes(node);
        if (terminals.get(0) != null && node.equals(terminals.get(0))) {
            if (adjacentNodes != null) {
                terminals.set(0, adjacentNodes.get(0).getSource());
            } else {
                terminals.set(0, null);
            }
        }
        if (terminals.get(1) != null && node.equals(terminals.get(1))) {
            if (adjacentNodes != null) {
                terminals.set(1, adjacentNodes.get(0).getSource());
            } else {
                terminals.set(1, null);
            }
        }
        this.metroLineTerminals.put(lineStation[0], terminals);
        this.removeNode(node);
    }

    private void output(INode<StationData> start) {
        String[] lineStation = this.getLineStation(start.getLabel());
        String line = lineStation[0];
        System.out.println("depot");
        INode<StationData> nextStation = start;
        INode<StationData> previousStation = start;
        INode<StationData> followingStation;
        do {
            followingStation = null;
            lineStation = this.getLineStation(nextStation.getLabel());
            System.out.print(lineStation[1]);
            List<IEdge<StationData>> adjacentNodes = this.getAdjacentNodes(nextStation);
            for (IEdge<StationData> stationEdge : adjacentNodes) {
                INode<StationData> station = stationEdge.getTarget();
                String[] ls = this.getLineStation(station.getLabel());
                if (!ls[0].equals(line)) {
                    System.out.printf(" - %s (%s line)", ls[1], ls[0]);
                } else {
                    if (!station.equals(previousStation)) {
                        followingStation = station;
                    }
                }
            }
            previousStation = nextStation;
            nextStation = followingStation;
            System.out.println();
        } while (followingStation != null);
        System.out.println("depot");
    }

    private void connect(INode<StationData> node1, INode<StationData> node2) {
        String[] lineStation1 = getLineStation(node1.getLabel());
        List<INode<StationData>> terminals1 = this.metroLineTerminals.get(lineStation1[0]);
        String[] lineStation2 = getLineStation(node2.getLabel());
        List<INode<StationData>> terminals2 = this.metroLineTerminals.get(lineStation2[0]);
        if (terminals1 == null || terminals2 == null) {
            System.out.println("Invalid command");
        } else {
            this.addWeightedEdge(node1, node2, TRANSFER_TIME);
            this.addWeightedEdge(node2, node1, TRANSFER_TIME);
        }
    }

    private void route(INode<StationData> start, INode<StationData> end) {
        this.calculateShortestPathFromSource(start, false);
        String[] lineStation = this.getLineStation(start.getLabel());
        String lastLine = lineStation[0];
        for (INode<StationData> pn : end.getShortestPath()) {
            lineStation = this.getLineStation(pn.getLabel());
            if (!lastLine.equals(lineStation[0])) {
                System.out.printf("Transition to line %s\n", lineStation[0]);
                lastLine = lineStation[0];
            }
            System.out.println(lineStation[1]);
        }
    }

    private void fastestRoute(INode<StationData> start, INode<StationData> end) {
        this.calculateShortestPathFromSource(start, false);
        String[] lineStation = this.getLineStation(start.getLabel());
        String lastLine = lineStation[0];
        for (INode<StationData> pn : end.getShortestPath()) {
            lineStation = this.getLineStation(pn.getLabel());
            if (!lastLine.equals(lineStation[0])) {
                System.out.printf("Transition to line %s\n", lineStation[0]);
                lastLine = lineStation[0];
            }
            System.out.println(lineStation[1]);
        }
        System.out.printf("Total: %d minutes in the way\n", (long) end.getDistance());
    }

    private String[] getUserCommand() {
        Scanner scanner = new Scanner(System.in);
        String[] parsedCommand = null;
        while (true) {
            try {
                parsedCommand = parseCommand(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid command");
            }
            if (parsedCommand == null || parsedCommand.length == 0 || !parsedCommand[0].startsWith("/")) {
                System.out.println("Invalid command");
            } else {
                break;
            }
        }
        return parsedCommand;
    }

    private String[] parseCommand(String command) {
        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"");
        Matcher regexMatcher = regex.matcher(command);
        List<String> matchList = new ArrayList<>();
        while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) {
                matchList.add(regexMatcher.group(1));
            } else {
                matchList.add(regexMatcher.group());
            }
        }
        return matchList.toArray(String[]::new);
    }

    private void loadMetroLines(String fileName) {
        MetroLineData metroLineData = null;
        Gson gson = new Gson();
        try (FileReader fr = new FileReader(fileName)) {
            metroLineData = gson.fromJson(fr, MetroLineData.class);
        } catch (FileNotFoundException e) {
            System.out.println("Error! Such a file doesn't exist!");
            System.exit(-1);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        this.buildNodeList(metroLineData);

        this.buildEdgeList();
    }

    private void buildNodeList(MetroLineData metroLineData) {
        for (String metroLineName : metroLineData.keySet()) {
            for (MetroLineData.MetroStationData metroStation : metroLineData.get(metroLineName)) {
                String label = metroLineName + "|" + metroStation.name;
                INode<StationData> node = this.getNodeByLabel(label);
                StationData sd = new StationData();
                node.setNodeData(sd);
                sd.setMetroLineName(metroLineName);
                sd.setTime(metroStation.time);
                for (String previousNode : metroStation.prev) {
                    String prevLabel = metroLineName + "|" + previousNode;
                    sd.addPreviousStation(this.getNodeByLabel(prevLabel));
                }
                for (String nextNode : metroStation.next) {
                    String nextLabel = metroLineName + "|" + nextNode;
                    sd.addNextStation(this.getNodeByLabel(nextLabel));
                }
                for (MetroLineData.TransferData transfer : metroStation.transfer) {
                    String transferLabel = transfer.line + "|" + transfer.station;
                    sd.addTransferStation(this.getNodeByLabel(transferLabel));
                }
            }
        }
    }

    private void buildEdgeList() {
        for (INode<StationData> node : this.getNodes()) {
            String metroLineName = node.getNodeData().getMetroLineName();
            List<INode<StationData>> terminals = this.metroLineTerminals.getOrDefault(metroLineName, Arrays.asList(null, null));
            this.metroLineTerminals.put(metroLineName, terminals);
            Set<INode<StationData>> previousStations = node.getNodeData().getPreviousStations();
            Set<INode<StationData>> nextStations = node.getNodeData().getNextStations();
            Set<INode<StationData>> transferStations = node.getNodeData().getTransfers();
            int nodeTime = node.getNodeData().getTime();
            for (INode<StationData> trans : transferStations) {
                this.addWeightedEdge(node, trans, TRANSFER_TIME);
                this.addWeightedEdge(trans, node, TRANSFER_TIME);
            }
            for (INode<StationData> prev : previousStations) {
                int prevTime = prev.getNodeData().getTime();
                this.addWeightedEdge(prev, node, prevTime);
                this.addWeightedEdge(node, prev, prevTime);
            }
            for (INode<StationData> next : nextStations) {
                this.addWeightedEdge(node, next, nodeTime);
                this.addWeightedEdge(next, node, nodeTime);
            }
            if (previousStations.size() == 0) {
                terminals.set(0, node);
            }
            if (nextStations.size() == 0) {
                terminals.set(1, node);
            }
        }
    }

    static class MetroLineData extends TreeMap<String, List<MetroLineData.MetroStationData>> {
        static class MetroStationData {
            public String name;
            public List<String> prev;
            public List<String> next;
            public int time;
            public MetroLineData.TransferData[] transfer;

            @Override
            public String toString() {
                return "{" + "\"name\": \"" + name + "\"" +
                        ", \"prev\": " + Arrays.toString(prev.stream().map(p -> "\"" + p + "\"").toArray()) +
                        ", \"next\": " + Arrays.toString(next.stream().map(n -> "\"" + n + "\"").toArray()) +
                        ", \"transfer\": " + Arrays.toString(transfer) +
                        ", \"time\": " + time +
                        "},";
            }
        }

        static class TransferData {
            public String line;
            public String station;

            @Override
            public String toString() {
                return "{\"line\": \"" + line + "\", \"station\": \"" + station + "\"},";
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("{");
            for (String lineName : this.keySet()) {
                sb.append("\"").append(lineName).append("\": ");
                sb.append(Arrays.toString(this.get(lineName).toArray())).append(",");
            }
            sb.append("},");
            String result = sb.toString().replaceAll(",,", ",").replaceAll(",]", "]").replaceAll(",}", "}");
            return result.substring(0, result.length() - 1);
        }
    }
}
