package metro;

import graph.INode;

import java.util.HashSet;
import java.util.Set;

public class StationData {
    private String metroLineName;
    private Set<INode<StationData>> previousStations = new HashSet<>();
    private Set<INode<StationData>> nextStations = new HashSet<>();
    private Set<INode<StationData>> transfers = new HashSet<>();
    private int time;

    public String getMetroLineName() {
        return metroLineName;
    }

    public void setMetroLineName(String metroLineName) {
        this.metroLineName = metroLineName;
    }

    public Set<INode<StationData>> getPreviousStations() {
        return previousStations;
    }

    public void setPreviousStations(Set<INode<StationData>> previousStations) {
        this.previousStations = previousStations;
    }

    public Set<INode<StationData>> getNextStations() {
        return nextStations;
    }

    public void setNextStations(Set<INode<StationData>> nextStations) {
        this.nextStations = nextStations;
    }

    public Set<INode<StationData>> getTransfers() {
        return transfers;
    }

    public void setTransfers(Set<INode<StationData>> transfers) {
        this.transfers = transfers;
    }

    public void addNextStation(INode<StationData> station) {
        this.nextStations.add(station);
    }

    public void addPreviousStation(INode<StationData> station) {
        this.previousStations.add(station);
    }

    public void addTransferStation(INode<StationData> station) {
        this.transfers.add(station);
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
