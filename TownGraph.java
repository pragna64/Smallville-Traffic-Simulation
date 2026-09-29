import java.io.*;
import java.util.*;

public class TownGraph {
    private Map<String, Node> nodes = new HashMap<>();
    private Map<String, List<Edge>> adjacencylist = new HashMap<>();
    private List<Edge> uniqueedges = new ArrayList<>();

    public Map<String, Node> getNodes() {
        return nodes;
    }

    public Map<String, List<Edge>> getAdjacencyList() {
        return adjacencylist;
    }

    public List<Edge> getUniqueEdges() {
        return uniqueedges;
    }

    public void loadNodes(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length < 5) 
                    continue;
                String id = p[0].trim();
                String name = p[1].trim();
                int x = Integer.parseInt(p[3].trim());
                int y = Integer.parseInt(p[4].trim());
                String type = p[2].trim();
                nodes.put(id, new Node(id, name, type, x, y));
                adjacencylist.putIfAbsent(id, new ArrayList<>());
            }
        } 
        catch (Exception e) {
            System.out.println("Error with loading nodes-" + e.getMessage());
        }
    }

    public void loadEdges(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length < 6) 
                    continue;
                String a = p[1].trim();
                String b = p[2].trim();
                String name = p[3].trim();
                double mult = Double.parseDouble(p[5].trim());
                if (nodes.containsKey(a) && nodes.containsKey(b)) {
                    Edge e1 = new Edge(a, b, name, mult);
                    Edge e2 = new Edge(b, a, name, mult);
                    adjacencylist.get(a).add(e1);
                    adjacencylist.get(b).add(e2);
                    uniqueedges.add(e1);
                }
            }
        } 
        catch (Exception e) {
            System.out.println("Error loading edges-" + e.getMessage());
        }
    }

    public void scaleCoordinates() {
        int minx = Integer.MAX_VALUE, maxx = Integer.MIN_VALUE;
        int miny = Integer.MAX_VALUE, maxy = Integer.MIN_VALUE;
        for (Node n : nodes.values()) {
            minx = Math.min(minx, n.rawx);
            maxx = Math.max(maxx, n.rawx);
            miny = Math.min(miny, n.rawy);
            maxy = Math.max(maxy, n.rawy);
        }
        int w = 900, h = 700, pad = 60;
        for (Node n: nodes.values()) {
            double px = (n.rawx - minx)/(double)(maxx - minx + 1);
            double py = (n.rawy - miny)/(double)(maxy - miny + 1);
            n.x = pad+(int)(px*(w-2*pad));
            n.y = pad+(int)((1-py)*(h-2*pad));
        }
        for (List<Edge> l : adjacencylist.values()){
            for (Edge e : l) {
                Node s = nodes.get(e.sourceid);
                Node t = nodes.get(e.targetid);
                if (s != null && t != null)
                    e.basedist = Math.hypot(s.x - t.x, s.y - t.y);
            }
        }
    }
}