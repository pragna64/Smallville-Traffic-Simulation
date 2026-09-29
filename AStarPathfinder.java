import java.util.*;

public class AStarPathfinder {
    private TownGraph graph;

    public AStarPathfinder(TownGraph graph) {
        this.graph = graph;
    }

    private double factor(Node current, Node destination) {
        return Math.sqrt(Math.pow(current.x - destination.x, 2)+ Math.pow(current.y - destination.y, 2));
    }

    public List<Node> findPath(String startId, String endId) {
        Map<String, Node> nodes = graph.getNodes();
        Map<String, List<Edge>> adjacencylist = graph.getAdjacencyList();
        Node start = nodes.get(startId);
        Node destination = nodes.get(endId);
        if (start == null || destination == null)
            return Collections.emptyList();
        Map<String, Double> gscore = new HashMap<>();
        Map<String, Double> fscore = new HashMap<>();
        Map<String, Node> parentmap = new HashMap<>();
        
        for (String nodeId : nodes.keySet()) {
            gscore.put(nodeId, Double.MAX_VALUE);
            fscore.put(nodeId, Double.MAX_VALUE);
        }

        PriorityQueue<Node> openSet = new PriorityQueue<Node>(new Comparator<Node>(){
            @Override
            public int compare(Node n1, Node n2){
                double f1 = fscore.getOrDefault(n1.id, Double.MAX_VALUE);
                double f2 = fscore.getOrDefault(n2.id, Double.MAX_VALUE);
                if (f1 < f2)
                    return -1;
                else if (f1 > f2)
                    return 1;
                else
                    return 0;
            }
        });
        gscore.put(startId, 0.0);
        fscore.put(startId, factor(start, destination));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            Node curr = openSet.poll();
            if (curr.id.equals(endId))
                return remakePath(parentmap, curr);
            for (Edge edge : adjacencylist.getOrDefault(curr.id, Collections.emptyList())) {
                if (edge.closed || edge.getWeight() == Double.MAX_VALUE) 
                    continue;
                double tentgscore = gscore.get(curr.id) + edge.getWeight();
                if (tentgscore < gscore.get(edge.targetid)) {
                    parentmap.put(edge.targetid, curr);
                    gscore.put(edge.targetid, tentgscore);
                    fscore.put(edge.targetid, tentgscore + factor(nodes.get(edge.targetid), destination));
                    openSet.add(nodes.get(edge.targetid));
                }
            }
        }
        return Collections.emptyList();
    }

    public List<Node> findPathToNearestType(String startId, String targetType) {
        Map<String, Node> nodes = graph.getNodes();
        List<Node> bestpath = Collections.emptyList();
        double shortestcost = Double.MAX_VALUE;
        for (Node potloc : nodes.values()) {
            if (potloc.type.equalsIgnoreCase(targetType)) {
                List<Node> currpath = findPath(startId, potloc.id);
                if (!currpath.isEmpty()) {
                    double acccost = computePathCost(currpath);
                    if (acccost < shortestcost) {
                        shortestcost = acccost;
                        bestpath = currpath;
                    }
                }
            }
        }
        return bestpath;
    }

    private double computePathCost(List<Node> route) {
        if (route == null || route.size() < 2) 
            return 0.0;
        double totcost = 0.0;
        Map<String, List<Edge>> adjacencylist = graph.getAdjacencyList();
        for (int i = 0; i < route.size() - 1; i++) {
            String currid = route.get(i).id;
            String nextid = route.get(i + 1).id;
            boolean segfound = false;
            for (Edge edge : adjacencylist.getOrDefault(currid, Collections.emptyList())) {
                if (edge.targetid.equals(nextid)) {
                    totcost += edge.getWeight();
                    segfound = true;
                    break;
                }
            }
            if(!segfound) 
                return Double.MAX_VALUE;
        }
        return totcost;
    }

    private List<Node> remakePath(Map<String, Node> parentMap, Node current) {
        List<Node> path = new ArrayList<>();
        path.add(current);
        while (parentMap.containsKey(current.id)) {
            current = parentMap.get(current.id);
            path.add(0, current);
        }
        return path;
    }
}