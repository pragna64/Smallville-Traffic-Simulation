public class Edge {
    public String sourceid;
    public String targetid;
    public String roadname;
    public double basedist;
    public double basemult;
    public double trafficmult = 1.0;
    public boolean closed = false;

    public Edge(String sourceid, String targetid, String roadname, double basemult) {
        this.sourceid = sourceid;
        this.targetid = targetid;
        this.roadname = roadname;
        this.basemult = basemult;
    }

    public double getWeight(){
        if (closed) 
            return Double.MAX_VALUE;
        return basedist*basemult*trafficmult;
    }
}