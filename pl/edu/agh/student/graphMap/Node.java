package pl.edu.agh.student.graphMap;

public class Node {
    /**
     * długość i szerokość geograficzna
     */

    public double lon;
    public double lat;
    public String id;

    public Node(String lat, String lon, String id) {

        //konwersja na double
        this.lon = Double.parseDouble(lon);
        this.lat = Double.parseDouble(lat);
        this.id = id;

    }

}