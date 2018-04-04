package pl.edu.agh.student.graphMap;

import java.util.ArrayList;

public class Crossing {
    /**
     * lista referencji do opbiektów pl.edu.agh.student.graphMap.Way, dane o tym jakie droigi krzyżują się w tym miejscu
     */

    public ArrayList<Way> streetsCrossing = new ArrayList<Way>();
    /**
     * Węzał w ktrórym krzyżują się drogi, dane geograficzne o skrzyżowaniu
     */
    public Node crossPoint;

    public Crossing() {
    }
}