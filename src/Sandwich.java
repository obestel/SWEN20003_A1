import bagel.* ;

public class Sandwich extends StationaryEntity {

    public Sandwich() {
        FILENAME = "res/images/sandwich.png";
    }

    public Sandwich(double x, double  y){
        FILENAME = "res/images/sandwich.png";
        set_position(x,y);
    }

}
