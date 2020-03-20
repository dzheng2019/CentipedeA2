import java.util.Random;
import javalib.funworld.*;
import javalib.worldimages.*;
import java.awt.Color;

// Represents an abstract centipede world capable of drawing players and centipedes
// (concrete examples are starting codes or playing codes)
abstract class ACentipedeWorld extends World {
  Random rand; 
  Board board;
  Gnome player;
  int col;  
  int row;
  IList<CentipedeHead> centipedes;


  int units = 40;

  ACentipedeWorld(Random rand,
      Board board, Gnome player, 
      int row, int col, 
      IList<CentipedeHead> centipedes) {
    this.rand = rand;
    this.board = board;
    this.player = player;
    this.col = col;
    this.row = row;
    this.centipedes = centipedes;
  }

  ACentipedeWorld(int row, int col) {
    this(row, col, new Random());
  }

  ACentipedeWorld(int row, int col, Random rand) {
    this.rand = rand;
    this.col = col;
    this.row = row;
    this.board = new Board(row, col);
    this.player = new Gnome(
        new Posn(this.units / 2, row * this.units - this.units / 2),
        15, this.units / 2);
    this.centipedes = 
        new ConsList<CentipedeHead>(
            new CentipedeHead(
                new Posn(20, 20), 
                4, 
                this.units / 2, 
                new Posn(20, 20),
                new Posn(20, 20), 
                this.units), 
            new MtList<CentipedeHead>());
  }
  
  /*
   * TEMPLATE:
   * FIELDS:
   * this.rand                       --Random
   * this.board                      --Board
   * this.player                     --Gnome
   * this.col                        --int
   * this.row                        --int
   * this.centipedes                 --IList<Centipede>
   * METHODS ON FIELDS:
   * board.makeBoard(int row, int col) - IList<IList<ITile>>
   * board.makeRow(int row, int col) - IList<ITile>
   * board.draw() - WorldImage
   * board.drawOnBoard() - WorldScene 
   * board.collisionOccursLeft(Posn location, int size) - boolean
   * board.collisionOccursRight(Posn location, int size) - boolean
   * board.collisionOccurs(Posn location, int size) - boolean
   * board.produceDart(Gnome gnome, int speed) - Dart
   * board.randomBoard(boolean isPebbles, int num, Random rand) - Board
   * board.changeAtLocation(Posn location, boolean createPebbles) - Board
   * player.move(Board board) - Gnome
   * player.moveIfPossible(Board board, Posn moveDir) - Gnome
   * player.moveInDirection(Board board, Posn direction) - Gnome
   * player.gnomeWithSpeed(int speed) - Gnome
   * player.collisionWithCentipedes(IList<CentipedeHead> centipedes) - boolean
   */

  // draws an image of this world, with the player and the centipedes
  public WorldScene makeScene() {
    /*
     * SAME AS CLASS TEMPLATE
     */
    WorldScene scene = new WorldScene(this.col * this.units, this.row * this.units);
    WorldScene sceneWithoutCenti = this.player.drawOnBoard(this.board.drawOnBoard(scene));
    return new DrawAllCentipedes(sceneWithoutCenti).apply(this.centipedes);
  }

}

// Represents the starting state of the world, 
// where tiles can be filled with dandelions/pebbles
class StartingWorld extends ACentipedeWorld {
  StartingWorld(Random rand,
      Board board, Gnome player, 
      int row, int col, 
      IList<CentipedeHead> centipedes) {
    super(rand, board, player, row, col, centipedes);
  }

  StartingWorld(int row, int col) {
    this(col, row, new Random());
    this.player = new Gnome(
        new Posn(20, row * 40 - 20),
        40, this.units / 2);
  }

  StartingWorld(int row, int col, Random rand) {
    super(col, row, rand);
  }

  /*
   * TEMPLATE: SAME AS ABSTRACT TEMPLATE
   */
  
  // returns this starting world but with the given board and player
  StartingWorld updateStartWorld(Board board, Gnome player) {
    /*
     * TEMPLATE:
     * PARAMETERS:
     * board                            --Board
     * player                           --Gnome
     * METHODS ON FIELDS:
     * board.makeBoard(int row, int col) - IList<IList<ITile>>
     * board.makeRow(int row, int col) - IList<ITile>
     * board.draw() - WorldImage
     * board.drawOnBoard() - WorldScene 
     * board.collisionOccursLeft(Posn location, int size) - boolean
     * board.collisionOccursRight(Posn location, int size) - boolean
     * board.collisionOccurs(Posn location, int size) - boolean
     * board.produceDart(Gnome gnome, int speed) - Dart
     * board.randomBoard(boolean isPebbles, int num, Random rand) - Board
     * board.changeAtLocation(Posn location, boolean createPebbles) - Board
     * player.move(Board board) - Gnome
     * player.moveIfPossible(Board board, Posn moveDir) - Gnome
     * player.moveInDirection(Board board, Posn direction) - Gnome
     * player.gnomeWithSpeed(int speed) - Gnome
     * player.collisionWithCentipedes(IList<CentipedeHead> centipedes) - boolean
     */
    return new StartingWorld(this.rand, 
        board, player, 
        this.row, this.col, this.centipedes);
  }

  // converts an absolute click position (in pixels) to a board grid position
  Posn clickToGrid(Posn loc) {
    /*
     * TEMPLATE: SAME AS CLASS
     */
    return new Posn(loc.x / units, this.row - (loc.y / units) - 1);
  }

  // returns this world, but after the given key input is handled
  public World onKeyEvent(String key) {
    /*
     * TEMPLATE: SAME AS CLASS
     */
    int totalUpperTiles = (this.row - 1) * this.col;
    // returns a new world where 5% of the tiles excluding the bottom row are now dandelions
    if (key.equals("D") || key.equals("d")) {
      return updateStartWorld(
          this.board.randomBoard(false, totalUpperTiles / 20, this.rand),
          this.player);
    }
    // returns a new world where 5% of the tiles excluding the bottom row are now pebbles
    if (key.equals("P") || key.equals("p")) {
      return updateStartWorld(
          this.board.randomBoard(true, totalUpperTiles / 20, this.rand),
          this.player);
    }
    // returns a new world where the tiles are just grass
    if (key.equals("R") || key.equals("r")) {
      return new StartingWorld(row, col);
    }

    //ends the world
    if (key.equals("S") || key.equals("s")) {
      return new ShootingWorld(this.rand,
          this.board, this.player.gnomeWithSpeed(6), 
          this.row, this.col, 
          this.centipedes);
    }

    // returns a new world with the player one to the right
    if (key.equals("right")) {
      return updateStartWorld(
          this.board,
          this.player.moveIfPossible(board, new Posn(1, 0)));
    }

    // returns a new world with the player one to the left
    if (key.equals("left")) {
      return updateStartWorld(
          this.board,
          this.player.moveIfPossible(board, new Posn(-1, 0)));
    }
    return this;
  }

  //returns a new world based on the mouse event
  public World onMouseClicked(Posn pos, String buttonName) {
    /*
     * TEMPLATE: SAME AS CLASS
     */
    Posn gridLoc = this.clickToGrid(pos);
    // does not modify the world if clicked on the bottom row
    if (gridLoc.y == 0) {
      return this;
    }
    // returns a new world where the clicked on tile gets clicked
    if (buttonName.equals("RightButton")) {
      return updateStartWorld(
          board.clickAtLocation(gridLoc, false),
          this.player);
    } 
    // returns a new world where the clicked on tile gets clicked
    if (buttonName.equals("LeftButton")) {
      return updateStartWorld(
          board.clickAtLocation(gridLoc, true),
          this.player);    }
    return this;
  }
}

// Represents an abstract playing world (concrete classes either have a dart or not)
abstract class APlayWorld extends ACentipedeWorld {

  APlayWorld(Random rand,
      Board board, Gnome player, 
      int row, int col, 
      IList<CentipedeHead> centipedes) {
    super(rand, board, player, row, col, centipedes);
  }
  
  /*
   * TEMPLATE:
   * SAME AS ABSTRACT
   */

  // returns this world, but updates the player and the centipedes to advance by one tick
  public abstract World onTick();

  // returns this world updated after the given key input takes place
  public World onKeyEvent(String key) {
    if (key.equals("right")) {
      return this.updatePlayWorld(
          this.player.moveIfPossible(this.board, new Posn(1 ,0)),
          this.centipedes, this.board);
    }
    // returns a new world with the player one to the left
    if (key.equals("left")) {
      return this.updatePlayWorld(
          this.player.moveIfPossible(this.board, new Posn(-1 ,0)), 
          this.centipedes, this.board);
    }
    if (key.equals(" ")) {
      return this.onSpaceEvent();
    }
    return this;
  }

  // returns this world after the space bar is hit. A dart is shot if there is not one on 
  // the screen, nothing occurs if there is already a dart on the screen
  abstract World onSpaceEvent();

  // updates the playing world to have the new player, new list of centipedes, and new board
  abstract World updatePlayWorld(Gnome newPlayer, 
      IList<CentipedeHead> newCentipedes, 
      Board newBoard);

  // ends this world and displays a message based on whether the player won or lost
  public WorldEnd worldEnds() {
//    OffMap offMap = new OffMap(units, row);
//    boolean anyOffMap = new OrMap<CentipedeHead>(offMap).apply(this.centipedes);
//    if (player.collisionWithCentipedes(this.centipedes) || anyOffMap) {
//      return new WorldEnd(true, this.lastScene("YOU LOSE"));
//    } 
//    else
    if (this.centipedes.length() == 0) {
      return new WorldEnd(true, this.lastScene("YOU WIN"));
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }

  // returns the last scene of this world after the world has been ended
  public WorldScene lastScene(String msg) {
    WorldImage msgImage = new TextImage(msg, 60, Color.RED);
    WorldScene scene = new WorldScene(this.col * this.units, this.row * this.units);
    return scene.placeImageXY(msgImage, this.col * this.units / 2, this.row * this.units / 2);
  }
}

// Determines whether the given centipede head is off the board
class OffMap implements IPred<CentipedeHead> {

  int units;
  int row;
  
  /*
   * TEMPLATE:
   * FIELDS:
   * this.units           --int
   * this.row             --int
   * METHODS:
   * this.apply(CentipedeHead arg)      --boolean
   */
  
  OffMap(int units, int row) {
    this.units = units;
    this.row = row;
  }
  
  // determines if the given centipede head has a grid y position less than 0
  public Boolean apply(CentipedeHead arg) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * arg                --CentipedeHead
     * METHODS ON PARAMETERS:
     * this.createTail(int tailNum)                       --IList<CentipedeFollower>
     * this.move(Board b)                                 --CentipedeHead
     * this.shortenTail(IList<CentipedeFollower> tail)    --CentipedeHead
     */
    return arg.getGrid(units, row).y < 0;
  }
}

// Represents a playing world where the player can shoot, and no dart on board
class ShootingWorld extends APlayWorld {

  ShootingWorld(Random rand, 
      Board board, Gnome player, int row, int col,
      IList<CentipedeHead> centipedes) {
    super(rand, board, player, row, col, centipedes);
  }

  /*
   * TEMPLATE:
   * SAME AS ABSTRACT
   */
  
  // returns this shooting world with the given player, list of centipedes, and board
  public World updatePlayWorld(
      Gnome newPlayer, 
      IList<CentipedeHead> newCentipedes, Board newBoard) {
    return new ShootingWorld(this.rand, board, newPlayer, this.row, this.col, 
        newCentipedes);
  }

  // returns this shooting world after moving the centipedes by one tick
  public World onTick() {
    return this.updatePlayWorld(
        this.player, 
        new MoveHeads(this.board).apply(this.centipedes),
        this.board);
  }

  
  // Converts this world after the space bar is hit, by placing a dart at the players position
  public World onSpaceEvent() {
    return new NonShootingWorld(this.rand, this.board, this.player, this.row, this.col,
        this.centipedes, this.board.produceDart(this.player, 10));
  }

}

//Represents a playing world with a dart alreayd on the board, so the player cannot shoot
class NonShootingWorld extends APlayWorld {

  Dart dart;

  NonShootingWorld(Random rand, Board board, Gnome player, int row, int col,
      IList<CentipedeHead> centipedes, Dart dart) {
    super(rand, board, player, row, col, centipedes);
    this.dart = dart;
  }
  
  /*
   * TEMPLATE:
   * SAME AS ABSTRACT
   * FIELDS:
   * dart                 --Dart
   * METHODS ON FIELDS:
   * dart.move(Board board) - Dart
   * dart.moveInDirection(Board board, Posn direction) - Dart
   * dart.collisionWithBoard(Board board) - boolean
   * dart.transformBoard(Board board, boolean createPebble) - Board
   * dart.collisionWithCentipedes(IList<CentipedeHead> centipedes) - boolean
   * dart.transformCentipedes(IList<CentipedeHead> centipedes) -  IList<CentipedeHead> 
   */

  // Returns this world after one tick, accounting for collisions between the dart and obstacles
  // and collisions between the dart and the centipede
  public World onTick() {
    /*
     * TEMPLATE: SAME AS CLASS
     */
    if (this.dart.collisionWithBoard(this.board)) {
      return new ShootingWorld(this.rand, this.dart.transformBoard(this.board, true), 
          this.player, this.row, this.col, this.centipedes);
    }
    else if (this.dart.collisionWithCentipedes(this.centipedes)) {
      return new ShootingWorld(this.rand, this.dart.transformBoard(this.board, false), this.player, 
          this.row, this.col, this.dart.transformCentipedes(centipedes, this.board));
    }
    else {
      return this.updatePlayWorld(
          this.player, 
          new MoveHeads(this.board).apply(this.centipedes), 
          this.board);
    }
  }


  // Does nothing when the player hits space, because a dart is already on the board
  World onSpaceEvent() {
    /*
     * TEMPLATE: SAME AS CLASS
     */
    return this;
  }


  // Returns this play world with the given player, centipedes, and board
  World updatePlayWorld(Gnome newPlayer, IList<CentipedeHead> newCentipedes, Board newBoard) {
    /*
     * TEMPLATE: SAME AS CLASS
     */
    return new NonShootingWorld(this.rand, board, newPlayer, this.row, this.col, 
        newCentipedes, this.dart.move(this.board));
  }

  // Makes the scene representing this by drawing the player and centipedes
  // on the board, then placing the dart on this image
  public WorldScene makeScene() {
    /*
     * TEMPLATE: SAME AS CLASS
     */
    WorldScene noDart = super.makeScene();
    return this.dart.drawOnBoard(noDart);
  }
}

// Draws all the centipedes in a list onto the given scene
class DrawAllCentipedes implements IListVisitor<CentipedeHead, WorldScene> {
  WorldScene scene;
  
  DrawAllCentipedes(WorldScene scene) {
    this.scene = scene;
  }
  
  /*
   * TEMPLATE:
   * FIELDS:
   * this.scene                                        --WorldScene
   * METHODS:
   * this.apply(IList<CentipedeHead> arg)              --WorldScene
   * this.visitMt(MtList<CentipedeHead> arg)           --WorldScene
   * this.visitCons(ConsList<CentipedeHead> arg)       --WorldScene
   * METHODS ON FIELDS:
   * this.scene.placeImageOnXY(int x, int y, WorldImage img)      --WorldScene
   */
  
  // applies this to arg
  public WorldScene apply(IList<CentipedeHead> arg) {
    /* TEMPLATE: SAME AS CLASS
     * PARAMATERS:
    * arg                                             --IList<CentipedeHead>
    * METHODS ON PARAMETERS:
    * this.accept(IListVisitor<CentipedeHead, U> visitor)         - U
    * this.length()                                               - int
    */
    return arg.accept(this);
  }

  // returns the scene with no centipede heads drawn on
  public WorldScene visitMt(MtList<CentipedeHead> mt) {
    /* TEMPLATE: SAME AS CLASS
     * PARAMATERS:
    * arg                                             --MtList<CentipedeHead>
    * METHODS ON PARAMETERS:
    * this.accept(IListVisitor<CentipedeHead, U> visitor)         - U
    * this.length()                                               - int
    */
    return scene;
  }
  
  // draws the first head in cons on the given scene and recus on the list
  public WorldScene visitCons(ConsList<CentipedeHead> cons) {
    /* TEMPLATE: SAME AS CLASS
     * PARAMATERS:
    * cons                                             --ConsList<CentipedeHead>
    * FIELDS OF PARAMETERS:
    * cons.first                                       --CentipedeHead
    * cons.rest                                        --IList<CentipedeHead>
    * METHODS ON PARAMETERS:
    * this.accept(IListVisitor<CentipedeHead, U> visitor)         - U
    * this.length()                                               - int
    * METHODS ON FIELDS OF PARAMETERS:
    * this.createTail(int tailNum)                                --IList<CentipedeFollower>
    * this.move(Board b)                                          --CentipedeHead
    * this.shortenTail(IList<CentipedeFollower> tail)             --CentipedeHead
    */
    return new DrawAllCentipedes(cons.first.drawOnBoard(scene)).apply(cons.rest);
  }
}

// Moves the centipede heads based on collisions with obstacles on the board
class MoveHeads implements IListVisitor<CentipedeHead, IList<CentipedeHead>> {
  
  Board board;

  MoveHeads(Board board) {
    this.board = board;
  }
  
  /*
   * TEMPLATE:
   * FIELDS:
   * board          --Board
   * METHODS ON FIELDS:
   * this.makethis(int row, int col) - IList<IList<ITile>>
   * this.makeRow(int row, int col) - IList<ITile>
   * this.draw() - WorldImage
   * this.drawOnthis() - WorldScene 
   * this.collisionOccursLeft(Posn location, int size) - boolean
   * this.collisionOccursRight(Posn location, int size) - boolean
   * this.collisionOccurs(Posn location, int size) - boolean
   * this.produceDart(Gnome gnome, int speed) - Dart
   * this.randomthis(boolean isPebbles, int num, Random rand) - this
   * this.changeAtLocation(Posn location, boolean createPebbles) - this
   */

  // applies this to arg
  public IList<CentipedeHead> apply(IList<CentipedeHead> arg) {
    /* TEMPLATE: SAME AS CLASS
     * PARAMATERS:
    * arg                                             --IList<CentipedeHead>
    * METHODS ON PARAMETERS:
    * this.accept(IListVisitor<CentipedeHead, U> visitor)         - U
    * this.length()                                               - int
    */
    return arg.accept(this);
  }

  // returns an empty list of heads, since there are none to move
  public IList<CentipedeHead> visitMt(MtList<CentipedeHead> mt) {
    /* TEMPLATE: SAME AS CLASS
     * PARAMATERS:
    * arg                                             --MtList<CentipedeHead>
    * METHODS ON PARAMETERS:
    * this.accept(IListVisitor<CentipedeHead, U> visitor)         - U
    * this.length()                                               - int
    */
    return mt;
  }

  
  // moves the first head in cons based on collisions with obstacles on the board,
  // then recurs on the rest of the list
  public IList<CentipedeHead> visitCons(ConsList<CentipedeHead> cons) {
    /* TEMPLATE: SAME AS CLASS
     * PARAMATERS:
    * cons                                             --ConsList<CentipedeHead>
    * FIELDS OF PARAMETERS:
    * cons.first                                       --CentipedeHead
    * cons.rest                                        --IList<CentipedeHead>
    * METHODS ON PARAMETERS:
    * this.accept(IListVisitor<CentipedeHead, U> visitor)         - U
    * this.length()                                               - int
    * METHODS ON FIELDS OF PARAMETERS:
    * this.createTail(int tailNum)                                --IList<CentipedeFollower>
    * this.move(Board b)                                          --CentipedeHead
    * this.shortenTail(IList<CentipedeFollower> tail)             --CentipedeHead
    */
    return new ConsList<CentipedeHead>(cons.first.move(board), cons.rest.accept(this));
  } 

}



