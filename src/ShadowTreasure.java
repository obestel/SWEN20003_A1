import bagel.*;
import bagel.util.Point;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.io.FileWriter; import java.io.PrintWriter; import java.io.IOException;

/**
 * An example Bagel game.
 */
public class ShadowTreasure extends AbstractGame {

    ArrayList<Zombie> zombies = new ArrayList<>() ; //ArrayLists to store multiple stationary entities
    ArrayList<Sandwich> sandwiches = new ArrayList<>() ;
    ArrayList<Point> shot_positions = new ArrayList<>() ;
    private final Treasure treasure = new Treasure();
    private final Player player = new Player() ;
    private final Shot shot = new Shot();
    private final Image background_image= new Image ("res/images/background.png") ;
    private static final int CLOSENESS = 50 ;
    private static final int KILL_RANGE = 25 ;
    private static final int SHOT_RANGE = 150 ;
    private static final int MAX_DISTANCE  = 1281 ; //Larger than maximum distance two entities could be from each other
    private int i = 0  ; //counting variable
    private double min_z_dist = MAX_DISTANCE;
    private double min_s_dist = MAX_DISTANCE ;
    private Point closest_zombie; // stores location of closest zombie
    private boolean shot_fired = false ;
    private static int dead_zombies = 0; // count dead zombies
    private static int sandwiches_eaten = 0 ; // count sandwiches eaten
    private static int zombies_shot_at = 0 ; // count zombies shot at


    // for rounding double number; use this to print the location of the player
    private static DecimalFormat df = new DecimalFormat("0.00");

    public ShadowTreasure() throws IOException {
        // Add code to initialize other attributes as needed
        super(1024, 768, "Shadow Treasure") ;
        this.loadEnvironment("res/IO/environment.csv");

    }

    /**
     * Load from input file
     */
    private void loadEnvironment(String filename){
        // Code here to read from the file and set up the environment
        String text ;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))){

            while ((text = br.readLine()) != null) {

                String cells[] = text.split(",");

                if (cells[0].equals("Sandwich")) { //Reads CSV file data and extracts appropriate entries based on type

//                    sandwich.set_position(Double.parseDouble(cells[1]),Double.parseDouble(cells[2])); // Gets x and y
                    sandwiches.add(new Sandwich(Double.parseDouble(cells[1]),Double.parseDouble(cells[2]))) ; //Adds a sandwich to the sandwiches ArrayList

                }

                else if(cells[0].equals("Zombie")) {

//                    zombie.set_position(Double.parseDouble(cells[1]),Double.parseDouble(cells[2]));
                    zombies.add(new Zombie(Double.parseDouble(cells[1]),Double.parseDouble(cells[2]))) ;

                }

                else if(cells[0].equals("Treasure")){

                    treasure.set_position(Double.parseDouble(cells[1]),Double.parseDouble(cells[2]));
                }

                else{

                    player.set_position(Double.parseDouble(cells[1]),Double.parseDouble(cells[2]) );
                    player.set_energy(Integer.parseInt(cells[3]));
                }

            }
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs a state update.
     */
    @Override
    public void update(Input input) {

        // Logic to update the game, as per specification must go here

        if (input.wasPressed(Keys.ESCAPE)) { // Press escape to close the game
            Window.close();
        }

        if (i % 10 == 0) {  //This loop makes it so the player and shot position only changes every 10 frames

            // method to move set player move direction to the treasure if all the zombies have been killed
            if(dead_zombies == zombies.size()){

                if(shot_fired){
                    shot_positions.add(shot.get_position()) ;
                }


                player.pointTo(treasure.get_position());
            }

            // The following finds the zombie with the minimum distance and goes towards it if the players energy is >= 3.
            if (player.get_energy() >= 3 && dead_zombies != zombies.size() ) {

                for (Zombie z : zombies) {

                    // Evaluates the position of all zombies, and stores the closest one in closest_zombie
                    if (Math.sqrt(Math.pow((player.get_position().x - z.get_position().x), 2) + Math.pow((player.get_position().y - z.get_position().y), 2)) <= min_z_dist && !z.get_interacted_with()) {
                        min_z_dist = Math.sqrt(Math.pow((player.get_position().x - z.get_position().x), 2) + Math.pow((player.get_position().y - z.get_position().y), 2));
                        closest_zombie = z.get_position();
                    }
                }


                // Sets the players move direction towards the closest zombie
                for (Zombie z : zombies) {
                    if (Math.sqrt(Math.pow((player.get_position().x - z.get_position().x), 2) + Math.pow((player.get_position().y - z.get_position().y), 2)) == min_z_dist) {
                        player.pointTo(z.get_position());
                    }
                }

                // Fires the shot if the player comes within the shooting range
                if(min_z_dist<SHOT_RANGE && !shot_fired){
                    shot.set_position(player.get_position().x,player.get_position().y);
                    shot_fired = true ;
                    player.reachZombie();
                    zombies_shot_at +=1 ;

                }

                if(shot_fired) {
                    shot_positions.add(shot.get_position());
                }


                //Moves the shot towards the zombie
                if(shot_fired){
                    shot.pointTo(closest_zombie);
                    shot.update();
                }

                // Assigns a value to z.fired_at to be used in edge cases for the game
                if(shot_fired) {
                    for (Zombie z : zombies) {
                        if (Math.sqrt(Math.pow((player.get_position().x - z.get_position().x), 2) + Math.pow((player.get_position().y - z.get_position().y), 2)) == min_z_dist) {
                            z.shoot_at(); ;
                        }
                    }
                }

                // Method that considers shot range from zombie and determines whether it is dead or not. Counts dead zombies
                if(shot_fired) {
                    if (Math.sqrt(Math.pow(shot.get_position().x - closest_zombie.x, 2) + Math.pow(shot.get_position().y - closest_zombie.y, 2)) < KILL_RANGE) {
                        for (Zombie z : zombies) {
                            if (z.get_fired_at() && !z.get_interacted_with()) {
                                z.set_interacted_with();
                                shot_positions.add(shot.get_position()) ;
                                shot_fired = false ;
                                dead_zombies +=1 ;
                            }
                        }
                    }
                }



                min_z_dist = MAX_DISTANCE; // Resets min_z_dist
            }

            // The following finds the sandwich with the minimum distance and goes towards it if the players energy is < 3.
            if(player.get_energy()<3 && dead_zombies != zombies.size()) {

                for (Sandwich s : sandwiches) {

                    if (Math.sqrt(Math.pow((player.get_position().x - s.get_position().x), 2) + Math.pow((player.get_position().y - s.get_position().y), 2)) <= min_s_dist && !s.get_interacted_with()) {
                        min_s_dist = Math.sqrt(Math.pow((player.get_position().x - s.get_position().x), 2) + Math.pow((player.get_position().y - s.get_position().y), 2));
                    }
                }
                for (Sandwich s : sandwiches) {

                    if (Math.sqrt(Math.pow((player.get_position().x - s.get_position().x), 2) + Math.pow((player.get_position().y - s.get_position().y), 2)) == min_s_dist) {
                        player.pointTo(s.get_position());
                    }
                }
                min_s_dist = MAX_DISTANCE;


                if(shot_fired){
                    shot_positions.add(shot.get_position()) ;
                }

                // The following shot code is the same as the zombie shot code, and is included here for when
                // a player has shot a zombie, and energy drops below three, so the above section of zombie code does not
                // run( Has if (player.get_energy() >=3) condition )




                if(shot_fired){
                    shot.pointTo(closest_zombie);
                    shot.update();
                }


                // The following shot code is the same as the zombie shot code, and is included here for when
                // a player has shot a zombie, and energy drops below three, so the above section of zombie code does not
                // run( Has if (player.get_energy() >=3) condition )

                if(shot_fired) {
                    if (Math.sqrt(Math.pow(shot.get_position().x - closest_zombie.x, 2) + Math.pow(shot.get_position().y - closest_zombie.y, 2)) < KILL_RANGE) {
                        for (Zombie z : zombies) {
                            if (z.get_fired_at() && !z.get_interacted_with()) {
                                shot_positions.add(shot.get_position()) ;
                                z.set_interacted_with();
                                shot_fired = false ;
                                dead_zombies += 1 ;

                            }
                        }
                    }
                }

            }

            player.update();
        }

        background_image.drawFromTopLeft(0, 0);
        player.render();
        player.render_energy();
        treasure.render();

        //Renders shot if fired
        if(shot_fired){
            shot.render();
        }

        // The for loop below loops through each zombie added to the array list, and renders it if it satisfies rendering conditions(such as whether it has come into contact with the player)
        for (Zombie z : zombies) {

//            double zombie_player_dist = Math.sqrt(Math.pow((player.get_position().x - z.get_position().x), 2) + Math.pow((player.get_position().y - z.get_position().y), 2));
//            System.out.println(zombie_player_dist);

            if (!z.get_interacted_with()) {
                z.render();
            }
        }


        // Renders sandwiches if they haven't been eaten.
        for (Sandwich s : sandwiches) {

            double sandwich_player_dist = Math.sqrt(Math.pow((player.get_position().x - s.get_position().x), 2) + Math.pow((player.get_position().y - s.get_position().y), 2));

            if (!s.get_interacted_with()) {
                s.render();
            }

            // Checks if player eats sandwich.
            // Counts the number of sandwiches eaten and adds energy to the player if it comes into eating range
            if (sandwich_player_dist < CLOSENESS && !s.get_interacted_with()) {
                s.set_interacted_with();
                player.eatSandwich();
                sandwiches_eaten += 1 ;
            }
        }


            // If all zombies are killed, and player meets treasure. Ends game and prints energy level and "success" to stdout
        if(dead_zombies == zombies.size() && Math.sqrt(Math.pow((player.get_position().x - treasure.get_position().x), 2) + Math.pow((player.get_position().y - treasure.get_position().y), 2))<CLOSENESS){

            //Outputs stored bullet positions to csv
                try (PrintWriter pw = new PrintWriter(new FileWriter("output.csv"))) {
                    for (Point p : shot_positions) {
                        pw.println(df.format(p.x) + "," + df.format(p.y));
                    }}

                     catch(IOException e){
                        e.printStackTrace();
                    }


            System.out.println(player.get_energy() + ", success!") ;
            Window.close() ;

            }


            // Edge case where all sandwiches are eaten, player energy <3, no sandwiches left, so game terminates
        if(player.get_energy()<3 && sandwiches_eaten == sandwiches.size() && zombies_shot_at != zombies.size() ){

            try (PrintWriter pw = new PrintWriter(new FileWriter("output.csv"))) {
                for (Point p : shot_positions) {
                    pw.println(df.format(p.x) + "," + df.format(p.y));
                }}

            catch(IOException e){
                e.printStackTrace();
            };

            System.out.println(player.get_energy());
                Window.close();
            }

        if (i == 200) { //Implemented for when i is greater than the allowed range for data type int
                i = 0;
            }

        i += 1;




    }




    /**
     * The entry point for the program.
     */
    public static void main(String[] args) throws IOException {
        ShadowTreasure game = new ShadowTreasure();
        game.run();
    }
}








//            double p2z_x = (zombie.get_x() - player.get_position().x) / zombie_player_dist; // Variables needed to step to direction of desired entity
//            double p2z_y = (zombie.get_y() - player.get_position().y) / zombie_player_dist;
//            double p2s_x = (sandwich.get_x() - player.get_position().x) / sandwich_player_dist;
//            double p2s_y = (sandwich.get_y() - player.get_position().y) / sandwich_player_dist;

//        System.out.println(sandwich_player_dist) ;
//        System.out.println("sandwichx" + Math.pow((player.get_position().x - sandwich.get_position().x), 2)) ;
//        System.out.println("playerx" + Math.pow((player.get_position().y - sandwich.get_position().y), 2)) ;
//        System.out.println("sandwichy" + sandwich.get_position().y) ;







//            if (player.get_energy() >= 3) {
//                player.pointTo(zombie.get_position());
//
//            }

//            if (player.get_energy() < 3) {
//                if (!sandwich.get_interacted_with()) {
//                    player.pointTo(sandwich.get_position());
//                }
////
//            }



//        double sandwich_player_dist = Math.sqrt(Math.pow((player.get_position().x - sandwich.get_position().x), 2) + Math.pow((player.get_position().y - sandwich.get_position().y), 2)); //Variables for Euclidean Distance
//        double zombie_player_dist = Math.sqrt(Math.pow((player.get_position().x - zombie.get_position().x), 2) + Math.pow((player.get_position().y - zombie.get_position().y), 2));


//





//
//
//
//            Zombie zombie = new Zombie(z.get_position().x, z.get_position().y);
//            z.render();
//            System.out.println(z.get_position()) ;
//            System.out.println(z.FILENAME) ;


//
//        if(!zombie.get_interacted_with()){
//            zombie.render();
//        }


//        if(!sandwich.get_interacted_with()){
//        sandwich.render();
//        }



//        if (sandwich_player_dist < CLOSENESS && !sandwich.get_interacted_with()) {
//            sandwich.set_interacted_with();
//            player.eatSandwich();
//        }

//        if (zombie_player_dist < CLOSENESS && !zombie.get_interacted_with()) {
//            zombie.set_interacted_with();
//            player.reachZombie();
//        }


//
//        if (zombie_player_dist < CLOSENESS) {
//            player.set_energy(player.get_energy() - 3);
//            Window.close(); // closes window when player meets zombie
////            printInfo(player.get_x(),player.get_y(), player.get_energy());
//        }


//            if (i % 10 == 0){
//                shot.update();
//            }
//
//            if(Math.sqrt(Math.pow((shot.get_position().x - closest_zombie.x), 2) + Math.pow((shot.get_position().y - closest_zombie.y), 2))< KILL_RANGE){
//                z.set_interacted_with();
//
//            }



//
//
//
//            Zombie zombie = new Zombie(z.get_position().x, z.get_position().y);
//            z.render();
//            System.out.println(z.get_position()) ;
//            System.out.println(z.FILENAME) ;


//            if (zombie_player_dist < SHOT_RANGE && !z.get_interacted_with()){
////                z.set_interacted_with();
////                player.reachZombie();
//                Shot shot = new Shot();
//
//                shot.set_position(player.get_position().x, closest_zombie.get_position().y);
//                shot.pointTo(closest_zombie);
//                shot.render();
//
//
//            }
