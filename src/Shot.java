public class Shot extends MovingEntity {
    public Shot(){
        FILENAME = "res/images/shot.png" ;
        STEP_SIZE = 25 ;
    }

    public Shot( double x, double y){
        FILENAME = "res/images/shot.png" ;
        STEP_SIZE = 25 ;
        set_position(x,y);
    }
}
