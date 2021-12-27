import bagel.* ;
import bagel.util.Point ;

public class MovingEntity {

    public String FILENAME;
    public int STEP_SIZE;
    private Point position;
    private Image image ;
    private double directionX, directionY;



    public void set_position(double x, double y) {
        this.position = new Point(x, y);

    }

    public Point get_position() {
        return this.position;

    }

    // Note that the pointTo and normalizeD methods shown below are taken from Project 1 solutions.

    public void pointTo(Point dest) {
        this.directionX = dest.x - this.position.x;
        this.directionY = dest.y - this.position.y;
        normalizeD();
    }

    // normalize direction
    public void normalizeD() {
        double len = Math.sqrt(Math.pow(this.directionX, 2) + Math.pow(this.directionY, 2));
        this.directionX /= len;
        this.directionY /= len;
    }




    public void update() {
        this.position = new Point(this.position.x+STEP_SIZE*this.directionX, this.position.y+STEP_SIZE*this.directionY); // This code is taken from Project1 solutions

    }


    public void render() {
        this.image = new Image(FILENAME);
        image.drawFromTopLeft(position.x, position.y);

    }

}