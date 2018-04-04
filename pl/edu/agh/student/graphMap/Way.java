package pl.edu.agh.student.graphMap;

import java.util.ArrayList;

public class Way {
    /**
     * Nazwa ulicy
     */
    public String name;
    /**
     * Długość ulicy, drogi
     */
    public double length = 0;
    public String id = null;
    /**
     * Lista referncji do nodów, potrzebna tymczasowo by znalezć wszystkie węzły należące do danej drogi
     */

    ArrayList<String> nodeIds;
    /**
     * tablica węzłów, obiektów pl.edu.agh.student.graphMap.Node jakie zawierają się w danej drodze
     */

    Node[] nodes;

    public Way(){
    }
    public Way(String ID, String _name){
        this.id = ID;
        if(_name != null) this.name = _name;
        else{
            this.name = "no name"; }

    }
    public Way(String ID){
        this.id = ID;
        this.name = "no name";
    }

    /**
     * Metoda oblicza długość drogina podstawie danch dostarczoncyh z GPS: longitude i latitude
     *
     * @return zwraca 0 w przypadku powodzenia, lub -1 gdy długość tablicy nodów jest mniejsza od 2
     */
    public int CalculateLenght() {
        if (nodes.length > 1) {
            double totalLenght = 0;
            for (int i = 1; i < nodes.length; i++) {
                totalLenght += haversineFormula(nodes[i - 1], nodes[i]);

            }
            length = totalLenght;
            return 0;
        }

        return -1;
    }

    /**
     * Tworzy tablice obieków pl.edu.agh.student.graphMap.Node o długości listy nodeRefs
     * Tablica przetrzynuje obiekty nodes we właściwej kolejności
     */

    public void createArraysizeOfNodeIDs() {
        nodes = new Node[nodeIds.size()];
    }

    double haversineFormula(Node fir, Node sec) {
        final double R = 6371; //km
        double latDistance = Math.toRadians(sec.lat - fir.lat);
        double lonDistance = Math.toRadians(sec.lon - fir.lon);
        double a = Math.pow(Math.sin(latDistance / 2), 2) +
                Math.cos(Math.toRadians(fir.lat)) * Math.cos(Math.toRadians(sec.lat)) * Math.pow(Math.sin(lonDistance / 2), 2);
        double distance = 2 * R * Math.asin(Math.sqrt(a));
        return distance;
    }
}