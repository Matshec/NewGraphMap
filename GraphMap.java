import java.io.File;
import java.util.ArrayList;
import java.lang.Math;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;



/**
 * Created by matshec on 16.10.16.
 * klasa ma sparsować dane z pliku .xml pobrane ze strony openstreetmap.org,
 * a następnie stworzyć z nich graf, uwzględniający tylko skrzyżowania i ulice.
 * Graf następnie będzie zapiany do pliku.
 */

public class GraphMap {
    public  ArrayList<Crossing> crossRoads;
    public static int parseCount = 0;
    public static ArrayList<Way> edge;



    GraphMap(){
        crossRoads = new ArrayList<Crossing>();
        edge = new ArrayList<Way>();
    }

    /**
     * Metoda wywłouje na każdym obiekcie z listy edge Metdoę createArraysizeofNodeIDs(), która w każdym z obiektów
     * tworzy tablicę referecji do obiektów Nodes o rozmiarze listy nodeRef. Innymi słowy tworzy pustą tablicę wiekości listy z refenrecjami do nodów
     */
    private void createNodeArrays(){
        for (int i = 0; i <  edge.size(); i++) {
            edge.get(i).createArraysizeOfNodeIDs();
        }
    }

    /**
     * Metoda dla każdeo obieku w lisćie edge wywłouje funkcje oblicząją długośc drogi .
     *
     */
    private void calculateLenghtofWay(){
        for (int i = 0; i < edge.size(); i++) {
            edge.get(i).CalculateLenght();
        }
    }

    /**
     * Znajduje skrzyżowania i tworzy odpowiedające im obiekty
     */
    public void findCrossings(){
        Crossing tempCross;
        for (int i = 0; i <  edge.size(); i++) {
            tempCross = new Crossing();
            tempCross.streetsCrossing.add(edge.get(i));
            for (int j = 0; j < edge.get(i).nodes.length; j++) {
                for (int k = i + 1; k < edge.size(); k++) {
                    for (int l = 0; l < edge.get(k).nodes.length; l++) {
                        //celowe porównanie referencji
                        if (edge.get(i).nodes[j] == edge.get(k).nodes[l]) {
                            tempCross.streetsCrossing.add(edge.get(k));
                            tempCross.crossPoint = edge.get(k).nodes[l];
                        }

                    }


                    if (tempCross.streetsCrossing.size() > 1) {
                        boolean bSameCrosspoint = false;
                        for (int q = 0; q < crossRoads.size(); q++) {
                            if (crossRoads.get(q).crossPoint == tempCross.crossPoint) bSameCrosspoint = true;
                        }
                        if (bSameCrosspoint == false) crossRoads.add(tempCross);
                    }
                 }
                }
            }

        }






    public static void main(String[] args) {
        GraphMap Mygraph = new GraphMap();

        try{
            File xmlfile = new File("BochniaMap.xml");
            SAXParserFactory factory =  SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            UserHandler userHandler = new UserHandler();
            saxParser.parse(xmlfile,userHandler);
            parseCount++;
            Mygraph.createNodeArrays();
            saxParser.parse(xmlfile,userHandler);
            Mygraph.calculateLenghtofWay();
            Mygraph.findCrossings();

        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(edge.size());
        // System.out.println(edge.get(edge.size()-1).nodeIds.get(0));
        // System.out.println(edge.get(4).nodeIds.get(0);
        //System.out.println(edge.get(0).nodes.get(2).id);

        for (int i = 0; i < Mygraph.crossRoads.size() ; i++) {
            for (int j = 0; j < Mygraph.crossRoads.get(i).streetsCrossing.size(); j++) {
                System.out.println(Mygraph.crossRoads.get(i).streetsCrossing.get(j).name);
            }
            System.out.println(Mygraph.crossRoads.get(i).crossPoint.id);
            System.out.println("--------");
        }
    }

}

/**
 * Klasa obsługująca wyjątki rzucane przez Sax Parser, definiuje zachowanie programu przy napotakniu danego tagu w pliku xml.
 * przeciąża puste metody znajdujące się w default handler
 * Prawdopodobnie najważniejsza klasa w projekcie
 */
class UserHandler extends DefaultHandler {

    private boolean bWay = false;
    private boolean bRel = false;
    private boolean bNode = false;
    private boolean bHighway = false;
    private int crossroadsIndex;
    private Way tempWay;
    private String tempWayID;
    private ArrayList<String> tempRefs;
    private  String tempName;



    /**
     * definiuje zachowanie przy napotkaniu danego tagu xml
     * @param uri
     * @param localName
     * @param qName nazwa tagu
     * @param attributes atrubuty jake tag posiada np <album name="sad" gdzie name jest atrybutem, a sad wartością
     * @throws SAXException
     */
    @Override
    public void startElement(String uri,String localName, String qName, Attributes attributes) throws SAXException{
        //znajduje tag way i towrzy nowy obiekt way, a takrze przypusuje referencje odpowiedniemu crossroads
        boolean wayIdfoud = false;

        //tymaczasowa lista "refs" dla obiektu Way


        if(qName.equalsIgnoreCase("way") && GraphMap.parseCount == 0){
            bWay =true;
            tempWayID = attributes.getValue("id");
            tempRefs = new ArrayList<String>();
        }

        //wrzucam wszysktkie "refs" z way do tymczasowej listy
        else if(qName.equalsIgnoreCase("nd") && bWay == true && GraphMap.parseCount == 0){
            tempRefs.add(attributes.getValue("ref"));
        }

        //sprawdzam czy droga jest typu highway
        else if(qName.equalsIgnoreCase("tag") && bWay == true && attributes.getValue("k").equalsIgnoreCase("highway") &&
                GraphMap.parseCount==0) {
            bHighway = true;
        }

        //znajduje nazwy ulic
        else if(qName.equalsIgnoreCase("tag") && attributes.getValue("k").equalsIgnoreCase("name") && bWay == true &&
                GraphMap.parseCount == 0){
            tempName = attributes.getValue("v");
        }



        //Realtnion jest niepotrzeban, inny sposób na znajdowanie skryżowań

        //znajduje relation, skrzyżowanie
//        else if(qName.equalsIgnoreCase("relation") && GraphMap.parseCount == 0){
//            bRel = true;
//            GraphMap.crossroads.add(new Crossing());
//            //i - indeks ostatniego elementu
//            crossroadsIndex = GraphMap.crossroads.size();
//        }
//        //znajduje "ref" w <relatoin>
//        if(bRel == true && qName.equalsIgnoreCase("member") && attributes.getValue("type").equalsIgnoreCase("way") &&
//                GraphMap.parseCount==0){
//            //odwołujemu sie do ostatniego ibiektu w liście crossroads, następinie dodajemy do listy Refs
//            //znajdującej się w tym obiekcie nowy wpis odczytany z pliku .xml
//           GraphMap.crossroads.get(crossroadsIndex-1).refs.add(attributes.getValue("ref"));
//        }

        //znajduje node
        else if(qName.equalsIgnoreCase("node") && GraphMap.parseCount==1){
            Node tempNode = new Node(attributes.getValue("lat"),attributes.getValue("lon"),attributes.getValue("id"));
            for (int i = 0; i < GraphMap.edge.size() ; i++) {
                for (int j = 0; j < GraphMap.edge.get(i).nodeIds.size(); j++) {
                    if (GraphMap.edge.get(i).nodeIds.get(j).equals(attributes.getValue("id"))){
                        int index = GraphMap.edge.get(i).nodeIds.indexOf(attributes.getValue("id"));
                        GraphMap.edge.get(i).nodes[index] = tempNode;

                    }
                }

            }

        }


    }

    /**
     * Wywoływana przy znaleziemu tagu zamykającego, ustawia odpowiedznie zmiennetypu boolean na false, zeby funkcja
     * startElemen mogła zawęzić dane do jednego danego tagu
     * @param uri
     * @param localName
     * @param qName
     */
    @Override
    public void endElement(String uri, String localName, String qName){
        if((qName.equalsIgnoreCase("way") && GraphMap.parseCount==0)){
            //tworzenie obiektów way
            if(bHighway == true ) {
                GraphMap.edge.add(new Way(tempWayID,tempName));
                int a = GraphMap.edge.size();
                GraphMap.edge.get(a-1).nodeIds = tempRefs;
                tempRefs = null; //na wszelki wypadek
            }
            bWay = false;
            bHighway = false;
        }

        else if(qName.equalsIgnoreCase("relation")){
            bRel = false;
        }
        else if(qName.equalsIgnoreCase("node")){
        }
    }

}

/**
 * klasa przechowująca dane o drodze/ulilcy wczytane i sparsowane z pliku .xml
 * Krawędz grafu
 */
class Way{
    // Nazwa ulicy
    /**
     * Nazwa ulicy
     */
    public String name = "no name";
    // długość ulicy, drogi wyliczona z danych GPS
    /**
     * Długość ulicy, drogi
     */
    public  double length = 0;
    public  String  id = null;
    //lista potrzebna tymczasoww żeby znleżć nody nalezące do danej drogi

    /**
     * Lista referncji do nodów, potrzebna tymczasowo by znalezć wszystkie węzły należące do danej drogi
     */

    ArrayList<String> nodeIds;

    /**
     * tablica węzłów, obiektów Node jakie zawierają się w danej drodze
     */

    Node[] nodes;

    public Way(){
    }
    public Way(String ID, String _name){
        id = ID;
        if(_name != null) this.name = _name;

    }
    public Way(String ID){
        id = ID;
    }

    /**
     * Metoda oblicza długość drogina podstawie danch dostarczoncyh z GPS: longitude i latitude
     * @return zwraca 0 w przypadku powodzenia, lub -1 gdy długość tablicy nodów jest mniejsza od 2
     */
    public int CalculateLenght(){
        if(nodes.length > 1) {
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
     * Tworzy tablice obieków Node o długości listy nodeRefs
     * Tablica przetrzynuje obiekty nodes we właściwej kolejności
     */

    public void createArraysizeOfNodeIDs(){
        nodes = new Node[nodeIds.size()];
    }


    private double haversineFormula(Node fir, Node sec){
        final double R = 6371; //km
        double latDistance = Math.toRadians(sec.lat-fir.lat);
        double lonDistance = Math.toRadians(sec.lon-fir.lon );
        double a = Math.pow(Math.sin(latDistance/2),2) +
                Math.cos(Math.toRadians(fir.lat)) * Math.cos(Math.toRadians(sec.lat)) * Math.pow(Math.sin(lonDistance/2),2);
        double distance = 2*R*Math.asin(Math.sqrt(a));
        return distance;
    }

}

/**
 * Przechowuje referencje do obiektów  klasy way, węzeł grafu
 */
class Crossing {
    /**
     * lista referencji do opbiektów Way, dane o tym jakie droigi krzyżują się w tym miejscu
     */

    public ArrayList<Way> streetsCrossing = new ArrayList<Way>();


    public Node crossPoint;



}

/**
 * Klase reprezentuje elemnt node parsownay z pliku.
 * Zawiera dane geograficzne węzła
 */

class Node{
    /**
     * długość i szerokość geograficzna
     */

    public double lon ,lat;

    public  String id;

    public Node(String lat, String lon, String id){
        //konwersja na double
        this.lon = Double.parseDouble(lon);
        this.lat = Double.parseDouble(lat);
        this.id = id;
    }
}