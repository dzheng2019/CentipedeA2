
import javalib.funworld.*;
import javalib.worldimages.*;
import java.awt.Color;


// represetns a moveable object
abstract class AMoveableObject {
  Posn location;
  int speed;
  int size;

  Util u = new Util();

  /* Template:
   * Fields:
   * this.location - Posn
   * this.speed - int
   * this.size - int
   * this.u - Util
   * Methods:
   * this.getGrid(int unit, int row) - Posn
   * this.movedPos(Posn direction) - Posn
   * this.validMove(Board board, Posn moveDir) - Boolean
   * this.draw() - WorldImage
   * this.drawOnBoard(WorldScene scene) - WorldScene
   * Methods on fields:
   * this.u.convertAbsoluteToGrid(Posn location, int unit, int row) - Posn
   * this.u.flipX(Posn toFlip) - Posn
   * this.u.moveInDirection(Posn location, Posn direciton, int speed) - Posn
   * this.u.posnEqual(Posn p1, Posn p2, int radius) - boolean
   * this.u.posnInRadius(Posn p1, Posn p2, int radius) - boolean
   * this.u.createRandomInts(int num, int max, Random rand) - IList<Integer>
   * this.u.getCenter(Posn pos, int unit) - Posn 
   */

  AMoveableObject(Posn location, int speed, int size) {
    this.location = location;
    this.speed = speed;
    this.size = size;
  }

  // gets the grid loc of this 
  public Posn getGrid(int unit, int row) {
    /* Template:
     * Same as class template
     * Parameter:
     * unit - int
     * row - int
     */
    return u.convertAbsoluteToGrid(location, unit, row);
  }

  // gets the location of moving this in the given direction
  public Posn movedPos(Posn direction) {
    /* Template:
     * Same as class template
     * Parameter: 
     * direction - Posn
     * Fields of Parameter
     * direction.x - int
     * direction.y - int
     */
    return u.moveInDirection(this.location, direction, this.speed);
  }

  // determines if this can move in a given direction
  public boolean validMove(Board board, Posn moveDir) {
    /* Template:
     * Same as class template
     * Parameter: 
     * board - Board
     * moveDir - Posn
     * Method/Fields of Parameter
     * moveDir.x - int
     * moveDir.y - int
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
     * board.clickAtLocation(Posn location, boolean isLeft) - Board
     */
    return !board.collisionOccurs(u.moveInDirection(this.location, moveDir, this.speed), this.size);
  }

  // draws this
  abstract WorldImage draw();

  // draws this on scene
  public WorldScene drawOnBoard(WorldScene scene) {
    /* Template:
     * Same as class template
     * Parameter:
     * scene - WorldScene
     * Methods on Parameter:
     * scene.placeImageXY(WorldImage, int x, int y);
     */
    return scene.placeImageXY(this.draw(), location.x, location.y);
  }
}

// represents a gnome
class Gnome extends AMoveableObject {

  /* Template:
   * Same as abstract class template
   * Methods:
   * this.move(Board board) - Gnome
   * this.moveIfPossible(Board board, Posn moveDir) - Gnome
   * this.moveInDirection(Board board, Posn direction) - Gnome
   * this.gnomeWithSpeed(int speed) - Gnome
   * this.collisionWithCentipedes(IList<CentipedeHead> centipedes) - boolean
   */

  // constructs a gnome at a given location, speed, size
  Gnome(Posn location, int speed, int size) {
    super(location, speed, size);
  }

  // draws a gnome 
  public WorldImage draw() {
    /* Template:
     * Same as class template
     */
    return new CircleImage(this.size, OutlineMode.SOLID, Color.BLUE);
  }

  // Moves this gnome for each tick which passes in this game
  // (this does nothing because gnomes do not move without key input)
  Gnome move(Board board) {
    /* Template:
     * Same as class template
     * Parameter
     * board - Board
     * Methods on parameter:
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
     * board.clickAtLocation(Posn location, boolean isLeft) - Board
     */
    return this;
  }

  // checks if gnome can move 
  Gnome moveIfPossible(Board board, Posn moveDir) {
    /* Template:
     * Same as class template
     * Parameter:
     * board - Board
     * moveDir - Posn
     * Methods/Fields on parameter:
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
     * board.clickAtLocation(Posn location, boolean isLeft) - Board
     * moveDir.x - int
     * moveDir.y - int 
     */
    if (this.validMove(board, moveDir)) {
      return this.moveInDirection(board, moveDir);
    }
    else {
      return this;
    }
  }

  // Moves this gnome in the given direction without consideration for collisions
  Gnome moveInDirection(Board board, Posn direction) {
    /* Template:
     * Same as class template
     * Parameter:
     * board - Board
     * direction - Posn
     * Methods/Fields on parameter:
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
     * board.clickAtLocation(Posn location, boolean isLeft) - Board
     * direction.x - int
     * direction.y - int 
     */
    return new Gnome(this.movedPos(direction), this.speed, this.size);
  }

  // creates a new gnome with a given speed 
  Gnome gnomeWithSpeed(int speed) {
    /* Template:
     * Same as class template:
     * Parameter:
     * speed - int 
     */
    return new Gnome(this.location, speed, size);
  }

  // determines if a gnome collides with a list of centipedes
  boolean collisionWithCentipedes(IList<CentipedeHead> centipedes) {
    /* Template:
     * Same as class template:
     * Parameter:
     * centipedes - IList<CentipedeHead>
     * Methods on parameters:
     * centipedes.accept(IListVisitor<CentipedeHead, R> visitor) - R
     * centipedes.length() - int 
     */
    return new OrMap<CentipedeHead>(new LocationCollidesWithHead(this.location)).apply(centipedes);
  }
}

// represents a dart
class Dart extends AMoveableObject {

  /* Template:
   * Same as abstract class
   * Methods:
   * this.move(Board board) - Dart
   * this.moveInDirection(Board board, Posn direction) - Dart
   * this.collisionWithBoard(Board board) - boolean
   * this.transformBoard(Board board, boolean createPebble) - Board
   * this.collisionWithCentipedes(IList<CentipedeHead> centipedes) - boolean
   * this.transformCentipedes(IList<CentipedeHead> centipedes) -  IList<CentipedeHead> 
   */

  // constructs a dart with a given location, speed, size
  Dart(Posn location, int speed, int size) {
    super(location, speed, size);
  }

  // draws a dart
  public WorldImage draw() {
    return new RectangleImage(this.size / 3, this.size / 2, 
        OutlineMode.SOLID, Color.BLUE);
  }

  // Moves this dart up if possible to do so
  Dart move(Board board) {
    /* Template:
     * Same as class template
     * Parameter
     * board - Board
     * Methods on parameter:
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
     * board.clickAtLocation(Posn location, boolean isLeft) - Board
     */
    return this.moveInDirection(board, new Posn(0, -1));
  }

  // Moves this dart in the 
  Dart moveInDirection(Board board, Posn direction) {
    /* Template:
     * Same as class template
     * Parameter:
     * board - Board
     * direction - Posn
     * Methods/Fields on parameter:
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
     * board.clickAtLocation(Posn location, boolean isLeft) - Board
     * direction.x - int
     * direction.y - int 
     */
    return new Dart(this.movedPos(direction), this.speed, this.size);
  }

  // checks if a dart collides with a board
  boolean collisionWithBoard(Board board) {   
    /* Template:
     * Same as class template
     * Parameter:
     * board - Board
     * Methods/Fields on parameter:
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
     * board.clickAtLocation(Posn location, boolean isLeft) - Board
     */
    // darts only collide with things in their column
    return board.collisionOccurs(this.location, 0);
  }

  // only used when a collision occurs
  Board transformBoard(Board board, boolean createPebble) {
    /* Template:
     * Same as class template
     * Parameter:
     * board - Board
     * createPebble - boolean
     * Methods/Fields on parameter:
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
     * board.clickAtLocation(Posn location, boolean isLeft) - Board
     */
    return board.changeAtLocation(this.location, createPebble);
  }

  // checks if a dart collides with a list of centipedes
  boolean collisionWithCentipedes(IList<CentipedeHead> centipedes) {    
    /* Template:
     * Same as class template:
     * Parameter:
     * centipedes - IList<CentipedeHead>
     * Methods on parameters:
     * centipedes.accept(IListVisitor<CentipedeHead, R> visitor) - R
     * centipedes.length() - int 
     */
    return new OrMap<CentipedeHead>(new LocationCollidesWithHead(this.location)).apply(centipedes);
  }

  // transforms a list of centipede heads based on a dart position 
  IList<CentipedeHead> transformCentipedes(IList<CentipedeHead> centipedes) {
    /* Template:
     * Same as class template:
     * Parameter:
     * centipedes - IList<CentipedeHead>
     * Methods on parameters:
     * centipedes.accept(IListVisitor<CentipedeHead, R> visitor) - R
     * centipedes.length() - int 
     */
    return new ChangeCentipedeHeadsAtLocation(this.location).apply(centipedes);
  }
}


