public class Node {
    public String id;
    public String name;
    public String type;
    public int rawx, rawy;
    public int x, y;

    public Node(String id, String name, String type, int rawx, int rawy) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.rawx = rawx;
        this.rawy = rawy;
    }
}