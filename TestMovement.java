import java.util.Random;

import javalib.funworld.World;
import javalib.funworld.WorldScene;
import javalib.worldimages.Posn;
import tester.Tester;


class TestMove extends World {
  Random rand;
  int col;  
  int row; 
  Board b;
  IMoveableObject player;
  IMoveableObject centipedeH;

  // most general constructor 
  TestMove(int col, int row, Random rand, Board b, IMoveableObject player, IMoveableObject centipedeH) {
    this.rand = rand; 
    this.col = col;
    this.row = row;
    this.b = b;
    this.player = player;
    this.centipedeH = centipedeH;
  }

  // The constructor for use in "real" games
  TestMove(int col, int row) { 
    this(col, row, new Random());
  }

  // The constructor for use in testing, with a specified Random object
  TestMove(int col, int row, Random rand) { 
    this.rand = rand; 
    this.col = col;
    this.row = row;
    this.b = new Board(col, row);
    this.player = new Gnome(new Posn(20, row*40 - 20) , 5, 15);
    this.centipedeH =  new CentipedeHead (
        new Posn(20, 20), 
        4, 
        20, 
        0,
        new Posn(1, 0),
        new Posn(1, 0));


  }

  TestMove updateTest(IMoveableObject player, IMoveableObject centipede) {
    return new TestMove(this.col, this.row, this.rand, this.b, player, centipede);
  }

  //Constants
  int units = ITile.units;

  public WorldScene makeScene() {   
    WorldScene scene = new WorldScene(this.col * this.units, this.row * this.units);
    return centipedeH.drawOnBoard(b.drawOnBoard(scene)); 
  }

  public World onKeyEvent(String key) {
    if (key.equals("right")) {
      return new TestMove(this.col, this.row, this.rand, 
          this.b, this.player.moveIfPossible(this.b, new Posn(1 ,0)), this.centipedeH);
    }
    // returns a new world with the player one to the left
    if (key.equals("left")) {
      return new TestMove(this.col, this.row, this.rand, 
          this.b, this.player.moveIfPossible(this.b, new Posn(-1 ,0)), this.centipedeH);
    }
    return this;
  }

  public World onTick() {
    //return this;
    return updateTest(this.player, this.centipedeH.move(this.b));
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