import java.util.Random;

import javalib.funworld.World;
import javalib.funworld.WorldScene;
import javalib.worldimages.Posn;
import tester.Tester;


class TestMove extends World {
  Random rand;
  int width;  
  int row; 
  Board b;
  IMoveableObject player;

  // most general constructor 
  TestMove(int width, int height, Random rand, Board tiles, IMoveableObject player) {
    this.rand = rand; 
    this.width = width;
    this.row = height;
    this.b = tiles;
    this.player = player;
  }

  // The constructor for use in "real" games
  TestMove(int width, int height) { 
    this(width, height, new Random());
  }

  // The constructor for use in testing, with a specified Random object
  TestMove(int width, int height, Random rand) { 
    this.rand = rand; 
    this.width = width;
    this.row = height;
    this.b = new Board(width, height);
    this.player = new Gnome(new Posn(0, 0) , 5, 50);
   // this.centipede = new CentipedeHead(new Posn(0, 0), 30, 5);
  }
  
  
  //Constants
  int units = ITile.units;

  public WorldScene makeScene() {   
    WorldScene scene = new WorldScene(this.width * this.units, this.row * this.units);
    return scene.placeImageXY(
        b.draw(), this.width * this.units / 2 , this.row * this.units / 2).placeImageXY(
            player.draw(), player.location().x, this.row * this.units - player.location().y);
  }
  
  public World onKeyEvent(String key) {
    if (key.equals("right")) {
      return new TestMove(this.width, this.row, this.rand, this.b, this.player.move(new Posn(1 ,0)));
    }
    // returns a new world with the player one to the left
    if (key.equals("left")) {
      return new TestMove(this.width, this.row, this.rand, this.b, this.player.move(new Posn(-1 ,0)));
    }
    return this;
  }
  
}  

class ExamplesMove {
  boolean testBigBang(Tester t) {
    World w = new TestMove(6, 6);
    int worldWidth = 40 * 6;
    int worldHeight = 40 * 6;
    double tickRate = .1;
    return w.bigBang(worldWidth, worldHeight, tickRate);
  }
}