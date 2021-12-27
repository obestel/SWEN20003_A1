import bagel.* ;
import bagel.util.Point ;

public class StationaryEntity {

    public String FILENAME;
    private Point position;
    private Image image ;
    private boolean interacted_with = false ;


    public void set_position(double x, double y) {
        this.position = new Point(x, y);

    }

    public Point get_position() {
        return this.position;

    }


    public void set_interacted_with(){
        this.interacted_with = true ;
    }

    public boolean get_interacted_with(){
        return interacted_with ;
    }


    public void render() {
        this.image = new Image(FILENAME);
        image.drawFromTopLeft(position.x, position.y);


    }

}
