import bagel.* ;
import bagel.Font;
import bagel.util.Colour;




public class Player extends MovingEntity {


    private int energy ;
    private final Font font = new Font("res/font/DejaVuSans-Bold.ttf", 20) ;
    private final DrawOptions dop = new DrawOptions() ;


    public Player(){
        FILENAME = "res/images/player.png" ;
        STEP_SIZE = 10 ;
    }

    public void set_energy(int energy) {
        this.energy = energy ;
    }

    public int get_energy(){
        return this.energy;
    }


    public void render_energy(){
         font.drawString("energy: "+ energy,20,760, dop.setBlendColour(Colour.BLACK));
     }

    public void eatSandwich(){
        energy += 5;
    }
    public void reachZombie(){
        energy -= 3;
    }

}
