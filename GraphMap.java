import java.io.*;
import java.util.ArrayList;
import java.lang.Math;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;



/**
 * Created by Maciej Mucha on 16.10.16.
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
         //sprawdzenie czy pierwszy node i ostani nie są takie same
        for (int i = 0; i < edge.size() ; i++) {
              if(edge.get(i).nodeIds.get(0).equals(edge.get(i).nodeIds.get(edge.get(i).nodeIds.size() - 1))){
                  edge.remove(i);
              }

        }

        for (int i = 0; i < edge.size(); i++) {
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
    public void findCrossings() {
        Crossing tempCross;
        for (int i = 0; i < edge.size(); i++) {
            for (int j = 0; j < edge.get(i).nodes.length; j++) {
                tempCross = new Crossing();
                tempCross.crossPoint = edge.get(i).nodes[j];
                tempCross.streetsCrossing.add(edge.get(i));
                for (int k = i + 1; k < edge.size(); k++) {
                    for (int l = 0; l < edge.get(k).nodes.length; l++) {
                        //celowe porównanie referencji
                       // if (edge.get(i).nodes[j] == edge.get(k).nodes[l]) {
                        if (edge.get(i).nodes[j].id.equals(edge.get(k).nodes[l].id)) {
                            tempCross.streetsCrossing.add(edge.get(k));
                            //tempCross.crossPoint = edge.get(i).nodes[j];
                        }

                    }


                }

                //sprawdzenie czy dane skrzyżowanie już przypadkiem nie istnieje
                if (tempCross.streetsCrossing.size() > 1) {
                    boolean bSameCrosspoint = false;
                    for (int q = 0; q < crossRoads.size(); q++) {
                        if (crossRoads.get(q).crossPoint == tempCross.crossPoint) bSameCrosspoint = true;
                    }
                    if (bSameCrosspoint == false) crossRoads.add(tempCross);
                }
                tempCross = null;
            }


        }
    }

    public void writeToFile(FileWriter o) throws IOException {
        for (int i = 0; i < crossRoads.size(); i++) {
            o.write("lat: " + crossRoads.get(i).crossPoint.lat + " ");
            o.write("lon: " + crossRoads.get(i).crossPoint.lon + " \n");
            o.write("node ID: " + crossRoads.get(i).crossPoint.id + "\n");

            for (int j = 0; j < crossRoads.get(i).streetsCrossing.size() ; j++) {
                o.write(crossRoads.get(i).streetsCrossing.get(j).name + "  ");
                o.write("lenght: " +String.format("%.2f",crossRoads.get(i).streetsCrossing.get(j).length) +  "km  ");
                o.write("Way ID: " + crossRoads.get(i).streetsCrossing.get(j).id + "\n");

            }

            o.write("------------------------------------------------------------------------------------- \n");
        }
    }

    public void checkForNullNodes(){
        for (int i = 0; i < edge.size() ; i++) {
            for (int j = 0; j <edge.get(i).nodes.length ; j++) {
                if(edge.get(i).nodes[j] == null) System.out.println("null ref");
            }
        }


    }





    public static void main(String[] args) {
        GraphMap Mygraph = new GraphMap();

        try{
            File xmlfile = new File("RynekBochnia.xml");
            SAXParserFactory factory =  SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            UserHandler userHandler = new UserHandler();
            System.out.println("first parse, finding ways...");
            saxParser.parse(xmlfile,userHandler);
            parseCount++;
            Mygraph.createNodeArrays();
            System.out.println("second parse, finding nodes... ");
            saxParser.parse(xmlfile,userHandler);
            System.out.println("calculating lenghts");
            Mygraph.calculateLenghtofWay();
             System.out.println("finding Crossings");
            Mygraph.findCrossings();
            System.out.println( "Writing to file: Graph.txt");
            //pisanie do pliku
            FileWriter output = new FileWriter("Graph.txt");
            Mygraph.writeToFile(output);
            output.close();

        }catch (SAXException e){
            System.out.println(e.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
        }

//        for (int a = 0; a < edge.size() ; a++) {
//            //System.out.println("id: " +edge.get(a).nodes.length);
//            for (int i = 0; i < edge.get(a).nodes.length; i++) {
//                if(edge.get(a).nodes[i]== null){
//                System.out.println("null node");
//                System.out.println(edge.get(a).id + " " + i);}
//            }
//        }
//            System.out.println("!!!!!!!!!!!!!!11");
//        for (int i = 0; i < Mygraph.crossRoads.size() ; i++) {
//            for (int j = 0; j < Mygraph.crossRoads.get(i).streetsCrossing.size(); j++) {
//                System.out.println(Mygraph.crossRoads.get(i).streetsCrossing.get(j).name);
//            }
//            System.out.println(Mygraph.crossRoads.get(i).crossPoint.id);
//            System.out.println("--------");
//        }
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
    private boolean bFootway = false;
    private String tempWayID;
    private ArrayList<String> tempRefs;
    private  String tempName = null;



    /**
     * definiuje zachowanie przy napotkaniu danego tagu xml
     * @param uri
     * @param localName
     * @param qName nazwa tagu
     * @param attributes atrubuty jakie tag posiada np <album name="sad" gdzie name jest atrybutem, a sad wartością
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
            if(attributes.getValue("v").equalsIgnoreCase("footway")) bFootway = true;
        }

        //znajduje nazwy ulic
        else if(qName.equalsIgnoreCase("tag") && attributes.getValue("k").equalsIgnoreCase("name") && bWay == true &&
                GraphMap.parseCount == 0){
            tempName = attributes.getValue("v");
        }
        //sprawdza czy nie jest footway
        else if(qName.equalsIgnoreCase("tag") && bWay == true && attributes.getValue("k").equalsIgnoreCase("footway") && GraphMap.parseCount == 0){
               bFootway = true;
        }



        //znajduje node
        else if(qName.equalsIgnoreCase("node") && GraphMap.parseCount==1){
            Node tempNode = null;
            if(attributes.getValue("lat") != null && attributes.getValue("lon") != null && attributes.getValue("id") != null) {
                tempNode = new Node(attributes.getValue("lat"), attributes.getValue("lon"), attributes.getValue("id"));

                for (int i = 0; i < GraphMap.edge.size(); i++) {
                    for (int j = 0; j < GraphMap.edge.get(i).nodeIds.size(); j++) {
                        if (GraphMap.edge.get(i).nodeIds.get(j).equalsIgnoreCase(tempNode.id)) {
                                int index = -2 ;
                                   index = GraphMap.edge.get(i).nodeIds.indexOf(tempNode.id);
                                    GraphMap.edge.get(i).nodes[index] = tempNode;
                                    index = GraphMap.edge.get(i).nodeIds.lastIndexOf(tempNode.id);
                                    GraphMap.edge.get(i).nodes[index] = tempNode;

                        }
                    }

                }
                tempNode = null;
            }else{
                throw new SAXException("Foulty node in input file");
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

        if((qName.equalsIgnoreCase("way") && GraphMap.parseCount == 0)){
            //tworzenie obiektów way
            if(bHighway == true && bFootway == false) {
                if (tempName == null){
                    GraphMap.edge.add(new Way(tempWayID));
                    int a = GraphMap.edge.size();
                    GraphMap.edge.get(a-1).nodeIds = tempRefs;
                    tempRefs = null; //na wszelki wypadek

                }else {
                    GraphMap.edge.add(new Way(tempWayID, tempName));
                    int a = GraphMap.edge.size();
                    GraphMap.edge.get(a - 1).nodeIds = tempRefs;
                    tempRefs = null; //na wszelki wypadek
                }
            }
            bWay = false;
            bHighway = false;
            tempName = null;
            bFootway = false;
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
    public String name;
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
        else{name = "no name"; }

    }
    public Way(String ID){
        id = ID;
        name = "no name";
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
    /**
     * Węzał w ktrórym krzyżują się drogi, dane geograficzne o skrzyżowaniu
     */
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

