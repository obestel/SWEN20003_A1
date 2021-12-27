import bagel.* ;

public class Zombie extends StationaryEntity {
    private boolean fired_at = false ;

    public Zombie() {
        FILENAME = "res/images/zombie.png";
    }

    public Zombie(double x, double y){
        set_position(x, y);
        FILENAME = "res/images/zombie.png";

    }

    public void shoot_at(){
        fired_at = true ;
    }

    public boolean get_fired_at(){
        return this.fired_at ;
    }
}
