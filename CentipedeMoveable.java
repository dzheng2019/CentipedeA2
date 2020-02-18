import java.awt.Color;

import javalib.funworld.WorldScene;
import javalib.worldimages.CircleImage;
import javalib.worldimages.EquilateralTriangleImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.Posn;
import javalib.worldimages.RotateImage;
import javalib.worldimages.WorldImage;
import tester.Tester;

// Finds the first centipede head which collides with the given position
// then splits the head at that location and appends back into a list of heads
class ChangeCentipedeHeadsAtLocation implements IListVisitor<CentipedeHead, IList<CentipedeHead>> {

  IFunc<CentipedeHead, IList<CentipedeHead>> splitHeadAtLocation;
  Posn location;

  ChangeCentipedeHeadsAtLocation(Posn location) {
    this.splitHeadAtLocation = new SplitHeadAtLocation(location);
    this.location = location;
  }

  /*
   * TEMPLATE: 
   * FIELDS:
   * this.splitHeadAtLocation                --IFunc<CentipedeHead, IList<CentipedeHead>>
   * this.location                           --Posn
   * METHODS:
   * this.apply(IList<CentipedeHead> arg)         --IList<CentipedeHead>
   * this.visitMt(IList<CentipedeHead> arg)       --IList<CentipedeHead>
   * this.visitCons(IList<CentipedeHead> arg)     --IList<CentipedeHead>
   * METHODS ON FIELDS:
   */

  // applies this function object to the the given list
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

  // returns an empty list because mt has no heads to change
  public IList<CentipedeHead> visitMt(MtList<CentipedeHead> mt) {
    /* TEMPLATE: SAME AS CLASS
     * PARAMATERS:
    * arg                                             --IList<CentipedeHead>
    * METHODS ON PARAMETERS:
    * this.accept(IListVisitor<CentipedeHead, U> visitor)         - U
    * this.length()                                               - int
    */
    return mt;
  }

  // if the first element of cons collides with head, then split it and
  // append the result with the rest of cons
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
    if (new LocationCollidesWithHead(this.location).apply(cons.first)) {
      return new Append<CentipedeHead>().apply(this.splitHeadAtLocation.apply(cons.first),
          cons.rest);
    }
    else {
      return new Append<CentipedeHead>().apply(
          new ConsList<CentipedeHead>(cons.first, new MtList<CentipedeHead>()),
          cons.rest.accept(this));
    }
  }
}

// Splits a centipedeHead into a list of two centipedeHeads
// if the head collides with the given position
class SplitHeadAtLocation implements IFunc<CentipedeHead, IList<CentipedeHead>> {
  Posn locationToChange;
  Util u = new Util();

  SplitHeadAtLocation(Posn location) {
    this.locationToChange = location;
  }

  /*
   * TEMPLATE:
   * FIELDS:
   * this.locationToChange            --Posn
   * METHODS:
   * this.apply(CentipedeHead head)   --IList<CentipedeHead>
   * METHODS ON FIELDS:
   */

  // Splits head at the location where the head or its list of followers collides
  // with location
  public IList<CentipedeHead> apply(CentipedeHead head) {
    /* Template:
     * FIELDS OF PARAMETERS:
     * head.location                      --Posn
     * head.size                          --int
     * head.tail                          --IList<CentipedeFollower>
     * METHODS ON PARAMETERS:
     * this.createTail(int tailNum)                                --IList<CentipedeFollower>
     * this.move(Board b)                                          --CentipedeHead
     * this.shortenTail(IList<CentipedeFollower> tail)             --CentipedeHead
     */
    if (u.posnInRadius(this.locationToChange, head.location, head.size)) {
      return new CreateHead().apply(head.tail);
    }
    else if (new LocationCollidesWithHead(this.locationToChange).apply(head)) {
      int index = new FindCollisionIndex(this.locationToChange).apply(head.tail);

      IList<CentipedeHead> newHead = new ConsList<CentipedeHead>(
          head.shortenTail(new FirstNElements<CentipedeFollower>(index).apply(head.tail)),
          new MtList<CentipedeHead>());

      IList<CentipedeHead> newHeadTail = new MakeHeadAtIndex(index + 1).apply(head.tail);
      return new Append<CentipedeHead>().apply(newHead, newHeadTail);
    }
    return new ConsList<CentipedeHead>(head, new MtList<CentipedeHead>());
  }

}

// Converts a list of centipede followers to a singleton list containing a
// centipede head.
// The head has the same position and direction as the first follower in the
// original list
// and its tail is the rest of the list of followers
class CreateHead implements IListVisitor<CentipedeFollower, IList<CentipedeHead>> {

  /*
   * TEMPLATE:
   * FIELDS:
   * METHODS:
   * this.apply(IList<CentipedeFollower> arg)             --IList<CentipedeHead>
   * this.visitMt(MtList<CentipedeFollower> mt)           --IList<CentipedeHead>
   * this.visitCons(ConsList<CentipedeFollower> cons)     --IList<CentipedeHead>
   * 
   */

  // Applies this onto a list of centipede followers, which performs double
  // dispatch to accumulate the type of the list
  public IList<CentipedeHead> apply(IList<CentipedeFollower> arg) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * arg                      --IList<CentipedeFollower>
     * METHODS ON PARAMETERS:
     * arg.accept(IListVisitor<CentipedeHead, U> visitor)         - U
     * arg.length()                                               - int
     */
    return arg.accept(this);
  }

  // returns an empty list of heads, because there are no followers to convert
  public IList<CentipedeHead> visitMt(MtList<CentipedeFollower> mt) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * mt                      --MtList<CentipedeFollower>
     * METHODS ON PARAMETERS:
     * mt.accept(IListVisitor<CentipedeFollower, U> visitor)     - U
     * mt.length()                                               - int
     */
    return new MtList<CentipedeHead>();
  }
  
  // converts the first follower in cons to a head with the rest of cons as its tail
  // also updates the previous goal of the followers to their current location
  public IList<CentipedeHead> visitCons(ConsList<CentipedeFollower> cons) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * cons                      --ConsList<CentipedeFollower>
     * METHODS ON PARAMETERS:
     * cons.accept(IListVisitor<CentipedeHead, U> visitor)         -- U
     * cons.length()                                               -- int
     * FIELDS OF PARAMETERS:
     * cons.first                                                  -- CentipedeFollower
     * cons.rest                                                   -- IList<CentipedeFollower>
     * METHODS ON FIELDS OF PARAMETERS:
     * 
     * cons.rest.accept(IListVisitor<CentipedeHead, U> visitor)   --U
     * cons.rest.length()                                         --int
     * cons.first.draw()                                          --WorldImage
     * cons.first.followToGoal()                                  --CentipedeFollower
     * cons.first.validMove(Board b, Posn direction)              --boolean
     * cons.first.movedPosToGoal(Posn goal)                       --Posn
     * cons.first.newMoveForward()                                --MoveForward
     * cons.first.transformToHead(IList<CentipedeFollower> tail)  --CentipedeHead
     * cons.first.setPreviousToLoc()                              --CentipedeFollower
     */
    return new ConsList<CentipedeHead>(
        cons.first.transformToHead(new UpdatePreviousLoc().apply(cons.rest)),
        new MtList<CentipedeHead>());
  }
}

// Updates the previous goal of a list of followers to their current location
// Useful when the list of followers reach their goal, so their current goals are reset and their
// previous goal needs to be reset to their current location
class UpdatePreviousLoc implements IListVisitor<CentipedeFollower, IList<CentipedeFollower>> {
  /*
   * TEMPLATE:
   * FIELDS: 
   * METHODS:
   * this.apply(IList<CentipedeFollower> arg)               --IList<CentipedeFollower>
   * this.visitMt(MtList<CentipedeFollower> mt)             --IList<CentipedeFollower>
   * this.visitCons(ConsList<CentipedeFollower> cons)       --IList<CentipedeFollower>
   * METHODS ON FIELDS:
   */
  // Applies this to a list of followers
  public IList<CentipedeFollower> apply(IList<CentipedeFollower> arg) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * arg                      --IList<CentipedeFollower>
     * METHODS ON PARAMETERS:
     * arg.accept(IListVisitor<CentipedeFollower, U> visitor)     - U
     * arg.length()                                               - int
     */
    return arg.accept(this);
  }
  
  // Returns an empty list of followers because there are none to update
  public IList<CentipedeFollower> visitMt(MtList<CentipedeFollower> mt) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * mt                      --MtList<CentipedeFollower>
     * METHODS ON PARAMETERS:
     * mt.accept(IListVisitor<CentipedeFollower, U> visitor)     - U
     * mt.length()                                               - int
     */
    return mt;
  }
  
  // updates the previous goal of the first of cons to its current position,
  // then recurs on the rest of cons
  public IList<CentipedeFollower> visitCons(ConsList<CentipedeFollower> cons) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * cons                      --ConsList<CentipedeFollower>
     * METHODS ON PARAMETERS:
     * cons.accept(IListVisitor<CentipedeFollower, U> visitor)     - U
     * cons.length()                                               - int
     * METHODS ON FIELDS OF PARAMETERS:
     * cons.first.draw()                                          --WorldImage
     * cons.first.followToGoal()                                  --CentipedeFollower
     * cons.first.validMove(Board b, Posn direction)              --boolean
     * cons.first.movedPosToGoal(Posn goal)                       --Posn
     * cons.first.newMoveForward()                                --MoveForward
     * cons.first.transformToHead(IList<CentipedeFollower> tail)  --CentipedeHead
     * cons.first.setPreviousToLoc()                              --CentipedeFollower
     */
    return new ConsList<CentipedeFollower>(cons.first.setPreviousToLoc(), cons.rest.accept(this));
  }
}

// Makes a head using the follower at the given index of the list with its
// tail as the rest of the list after the given index
class MakeHeadAtIndex implements IListVisitor<CentipedeFollower, IList<CentipedeHead>> {

  int index;

  MakeHeadAtIndex(int index) {
    this.index = index;
  }

  /*
   * TEMPLATE:
   * FIELDS:
   * this.index                          --int
   * METHODS:
   * this.apply(IList<CentipedeFollower> arg)               --IList<CentipedeFollower>
   * this.visitMt(MtList<CentipedeFollower> mt)             --IList<CentipedeFollower>
   * this.visitCons(ConsList<CentipedeFollower> cons)       --IList<CentipedeFollower>
   */
  
  // applies this to arg
  public IList<CentipedeHead> apply(IList<CentipedeFollower> arg) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * arg                      --IList<CentipedeFollower>
     * METHODS ON PARAMETERS:
     * arg.accept(IListVisitor<CentipedeFollower, U> visitor)     - U
     * arg.length()                                               - int
     */
    return arg.accept(this);
  }

  // returns an empty list of heads, as there are no followers to convert
  public IList<CentipedeHead> visitMt(MtList<CentipedeFollower> mt) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * mt                      --MtList<CentipedeFollower>
     * METHODS ON PARAMETERS:
     * mt.accept(IListVisitor<CentipedeFollower, U> visitor)     - U
     * mt.length()                                               - int
     */
    return new MtList<CentipedeHead>();
  }

  // if the index of this is 0, then creates a head from the first follower in cons
  // with its tail as the rest of cons
  public IList<CentipedeHead> visitCons(ConsList<CentipedeFollower> cons) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * cons                      --ConsList<CentipedeFollower>
     * METHODS ON PARAMETERS:
     * cons.accept(IListVisitor<CentipedeFollower, U> visitor)     - U
     * cons.length()                                               - int
     * METHODS ON FIELDS OF PARAMETERS:
     * cons.first.draw()                                          --WorldImage
     * cons.first.followToGoal()                                  --CentipedeFollower
     * cons.first.validMove(Board b, Posn direction)              --boolean
     * cons.first.movedPosToGoal(Posn goal)                       --Posn
     * cons.first.newMoveForward()                                --MoveForward
     * cons.first.transformToHead(IList<CentipedeFollower> tail)  --CentipedeHead
     * cons.first.setPreviousToLoc()                              --CentipedeFollower
     */
    if (index == 0) {
      return new CreateHead().apply(new UpdatePreviousLoc().apply(cons));
    }
    else {
      return new MakeHeadAtIndex(index - 1).apply(cons.rest);
    }
  }
}

// Finds the index (assuming one exists) in a list of followers where the follower
// collides with the given position
class FindCollisionIndex implements IListVisitor<CentipedeFollower, Integer> {

  Posn locationToChange;

  FindCollisionIndex(Posn locationToChange) {
    this.locationToChange = locationToChange;
  }

  /*
   * FIELDS:
   * this.locationToChange                        --Posn
   * METHODS:
   * this.apply(IList<CentipedeFollower> arg)     --Integer
   * this.visitMt(MtList<CentipedeFollower> mt)   --Integer
   * this.visitCons(ConsList<CentipedeFollower> cons)  --Integer
   */

  // applies this to arg
  public Integer apply(IList<CentipedeFollower> arg) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * arg                      --IList<CentipedeFollower>
     * METHODS ON PARAMETERS:
     * arg.accept(IListVisitor<CentipedeFollower, U> visitor)     - U
     * arg.length()                                               - int
     */
    return arg.accept(this);
  }

  // returns 0 as a default value, since there was no index in the list
  // where a collision occurred
  public Integer visitMt(MtList<CentipedeFollower> mt) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * mt                      --MtList<CentipedeFollower>
     * METHODS ON PARAMETERS:
     * mt.accept(IListVisitor<CentipedeFollower, U> visitor)     - U
     * mt.length()                                               - int
     */
    return 0;
  }

  // if the first of cons collides with the given posn, then return 0, else
  // add 1 to the recursive call on the rest of cons
  public Integer visitCons(ConsList<CentipedeFollower> cons) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * cons                      --ConsList<CentipedeFollower>
     * METHODS ON PARAMETERS:
     * cons.accept(IListVisitor<CentipedeFollower, U> visitor)     - U
     * cons.length()                                               - int
     * METHODS ON FIELDS OF PARAMETERS:
     * cons.first.draw()                                          --WorldImage
     * cons.first.followToGoal()                                  --CentipedeFollower
     * cons.first.validMove(Board b, Posn direction)              --boolean
     * cons.first.movedPosToGoal(Posn goal)                       --Posn
     * cons.first.newMoveForward()                                --MoveForward
     * cons.first.transformToHead(IList<CentipedeFollower> tail)  --CentipedeHead
     * cons.first.setPreviousToLoc()                              --CentipedeFollower
     */
    if (new LocationCollidesWithFollower(locationToChange).apply(cons.first)) {
      return 0;
    }
    else {
      return 1 + cons.rest.accept(this);
    }
  }
}

// Determines whether a given posn collides with a centipede head or its tail
class LocationCollidesWithHead implements IPred<CentipedeHead> {
  Posn location;
  Util u = new Util();

  LocationCollidesWithHead(Posn location) {
    this.location = location;
  }

  /*
   * FIELDS:
   * this.location                               --Posn
   * this.u                                      --Util
   * METHODS:
   * this.apply(IList<CentipedeHead> arg)           --Boolean
   */

  // determines if the head or any of the followers in the head's tail collide
  // with the given posn
  public Boolean apply(CentipedeHead head) {
    /*
     * TEMPLATE:
     * PARAMETERS:
     * head                     --CentipedeHead
     * FIELDS OF PARAMETERS:
     * head.location            --Posn
     * head.tail                --IList<CentipedeFollower>
     * head.goal                --Posn
     * head.previousGoal        --Posn
     * METHODS ON PARAMETERS:
     * head.createTail(int tailNum)                       --IList<CentipedeFollower>
     * head.move(Board b)                                 --CentipedeHead
     * head.shortenTail(IList<CentipedeFollower> tail)    --CentipedeHead
     * METHODS ON FIELDS OF PARAMETERS:
     * head.tail.length()       --int
     * head.tail.accept(IListVisitor<CentipedeFollower, U> func)       --U
     */
    return u.posnInRadius(head.location, this.location, head.size)
        || new OrMap<CentipedeFollower>(new LocationCollidesWithFollower(this.location))
            .apply(head.tail);
  }
}

// Determines if a centipede follower collides with a given posn
class LocationCollidesWithFollower implements IPred<CentipedeFollower> {
  Posn location;
  Util u = new Util();

  LocationCollidesWithFollower(Posn location) {
    this.location = location;
  }

  /*
   * FIELDS:
   * this.location                               --Posn
   * this.u                                      --Util
   * METHODS:
   * this.apply(IList<CentipedeFollower> arg)           --Boolean
   */

  // checks if location intersects the circle created by follower
  public Boolean apply(CentipedeFollower follower) {
    /*
     * TEMPLATE:
     * PARAMETERS:
     * follower                     --CentipedeFollower
     * FIELDS OF PARAMETERS:
     * follower.previousGoal        --Posn
     * follower.unit                --int
     * METHODS ON PARAMETERS:
     * follower.draw()                                          --WorldImage
     * follower.followToGoal()                                  --CentipedeFollower
     * follower.validMove(Board b, Posn direction)              --boolean
     * follower.movedPosToGoal(Posn goal)                       --Posn
     * follower.newMoveForward()                                --MoveForward
     * follower.transformToHead(IList<CentipedeFollower> tail)  --CentipedeHead
     * follower.setPreviousToLoc()                              --CentipedeFollower
     * METHODS ON FIELDS OF PARAMETERS:
     */
    return u.posnInRadius(follower.location, this.location, follower.size);
  }
}

// Represents the head of a centipede, also containing its tail
class CentipedeHead extends AMoveableObject {
  Posn previousGoal;
  Posn goal;
  int unit;
  IList<CentipedeFollower> tail;

  CentipedeHead(Posn location, int speed, int size, Posn previousGoal, Posn goal, int unit,
      IList<CentipedeFollower> tail) {
    super(location, speed, size);
    this.previousGoal = previousGoal;
    this.goal = goal;
    this.unit = unit;
    this.tail = tail;
  }

  CentipedeHead(Posn location, int speed, int size, Posn previousGoal, Posn goal, int unit) {
    super(location, speed, size);
    this.previousGoal = previousGoal;
    this.goal = goal;
    this.unit = unit;
    this.tail = this.createTail(10);

  }

  CentipedeHead(Posn location, int speed, int size, Posn previousGoal, Posn goal, int unit,
      int tailNum) {
    super(location, speed, size);
    this.previousGoal = previousGoal;
    this.goal = goal;
    this.unit = unit;
    this.tail = this.createTail(tailNum);
  }

  /*
   * FIELDS:
   * this.location           --Posn
   * this.speed              --int
   * this.previousGoal       --Posn
   * this.goal               --Posn
   * this.unit               --int
   * this.tailNum            --int
   * METHODS:
   * this.createTail(int tailNum)                       --IList<CentipedeFollower>
   * this.move(Board b)                                 --CentipedeHead
   * this.shortenTail(IList<CentipedeFollower> tail)    --CentipedeHead
   * METHODS ON FIELDS:
   */

  // Creates a default list of followers with the same position, goal, and attributes as this
  IList<CentipedeFollower> createTail(int numFollowers) {
    /*
     * TEMPLATE: SAME AS CLASS
     */
    if (numFollowers <= 0) {
      return new MtList<CentipedeFollower>();
    }
    else {
      CentipedeFollower follower = new CentipedeFollower(this.location, this.speed, this.size,
          this.previousGoal, this.unit);
      return new ConsList<CentipedeFollower>(follower, this.createTail(numFollowers - 1));
    }
  }

  // Moves this forward by one tick, accounting for turning and collisions with obstacles
  CentipedeHead move(Board board) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * board                           --Board
     * METHODS ON PARAMETERS:
     * this.makeBoard(int row, int col) - IList<IList<ITile>>
     * this.makeRow(int row, int col) - IList<ITile>
     * this.draw() - WorldImage
     * this.drawOnBoard() - WorldScene 
     * this.collisionOccursLeft(Posn location, int size) - boolean
     * this.collisionOccursRight(Posn location, int size) - boolean
     * this.collisionOccurs(Posn location, int size) - boolean
     * this.produceDart(Gnome gnome, int speed) - Dart
     * this.randomBoard(boolean isPebbles, int num, Random rand) - Board
     * this.changeAtLocation(Posn location, boolean createPebbles) - Board
     * this.clickAtLocation(Posn location, boolean isLeft) - Board
    */

    int leftOrRight = this.location.y / this.unit + 1;
    Posn moveInCurDir = u.moveInDirection(this.location, new Posn(((leftOrRight % 2) * 2 - 1), 0),
        this.speed);
    boolean withinGoal = u.posnInRadius(this.location, goal, speed / 2);

    if (leftOrRight % 2 == 1 && board.collisionOccursRight(moveInCurDir, this.size) && withinGoal) {
      CentipedeHead newGoal = this.headWithNewGoal(this.getNextGoal(board));
      return newGoal.moveTowardsGoal();
    }
    else if (leftOrRight % 2 == 0 && board.collisionOccursLeft(moveInCurDir, this.size)
        && withinGoal) {
      CentipedeHead newGoal = this.headWithNewGoal(this.getNextGoal(board));
      return newGoal;
    }
    else if (withinGoal) {
      CentipedeHead headNewGoal = this.headWithNewGoal(this.getNextGoal(board));
      return headNewGoal.moveTowardsGoal();
    }
    else {
      return this.headWithNewPos(this.nextPos());
    }
  }

  // returns a new head with the tail as followers
  CentipedeHead shortenTail(IList<CentipedeFollower> followers) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * followers                --IList<CentipedeFollower>
     * METHODS ON PARAMETERS:
     * folowers.accept(IListVisitor<CentipedeFollower, U>)      --U
    */

    return new CentipedeHead(this.location, this.speed, this.size, this.previousGoal, this.goal,
        this.unit, followers);
  }

  // returns a new head with the position as newPos
  CentipedeHead headWithNewPos(Posn newPos) {
    /*
     * TEMPLATE: SAME AS CLASS
     */
    return new CentipedeHead(newPos, this.speed, this.size, this.previousGoal, this.goal, this.unit,
        new MoveTailForward(this.previousGoal).apply(this.tail));
  }

  // returns a new head with the goal as newGoal
  CentipedeHead headWithNewGoal(Posn newGoal) {
    /*
     * TEMPLATE: SAME AS CLASS
     */
    return new CentipedeHead(this.nextPos(), this.speed, this.size, this.goal, newGoal, this.unit,
        new MoveTailForward(this.previousGoal).apply(this.tail));
  }

  // Calculates the next position of this based on its current row,
  // not accounting for collisions
  Posn nextPos() {
    /*
     * TEMPLATE: SAME AS CLASS
     */
    Util u = new Util();
    int moveX = (this.goal.x - this.location.x);
    int moveY = (this.goal.y - this.location.y);
    if (moveY > 0) {
      return u.moveInDirection(this.location, new Posn(0, 1), this.speed);
    }
    else if (moveX < 0) {
      return u.moveInDirection(this.location, new Posn(-1, 0), this.speed);
    }
    else if (moveX > 0) {
      return u.moveInDirection(this.location, new Posn(1, 0), this.speed);
    }
    else {
      return this.location;
    }
  }

  // returns a new head moved one tick forward to its current goal
  CentipedeHead moveTowardsGoal() {
    /*
     * TEMPLATE: SAME AS CLASS
     */
    return this.headWithNewPos(this.nextPos());
  }

  // Calculates the next goal of this based on its row, collisios, and turning
  Posn getNextGoal(Board b) {
    int leftOrRight = this.location.y / this.unit + 1;

    Posn moveInCurDir = u.moveInDirection(this.location, new Posn(((leftOrRight % 2) * 2 - 1), 0),
        this.speed);
    Posn myCenter = u.getCenter(this.location, this.unit);
    if (b.collisionOccurs(moveInCurDir, this.size)) {
      return new Posn(myCenter.x, myCenter.y + unit);
    }
    else {
      return new Posn(myCenter.x + ((leftOrRight % 2) * 2 - 1) * unit, myCenter.y);
    }
  }

  // draws this head, rotated based on its direction calculated from the row
  // does not draw the followers of this
  public WorldImage draw() {
    /*
     * TEMPLATE: SAME AS CLASS
     */
    WorldImage head = new EquilateralTriangleImage(this.size * 2, OutlineMode.SOLID, Color.RED)
        .movePinholeTo(new Posn(0, 0));

    int moveY = (this.goal.y - this.location.y);

    int leftOrRight = this.location.y / this.unit + 1;

    if (moveY > 0) {
      return new RotateImage(head, 180);
    }
    if (leftOrRight % 2 == 1) {
      return new RotateImage(head, 90);
    }
    else if (leftOrRight % 2 == 0) {
      return new RotateImage(head, -90);
    }
    else {
      return head;
    }

  }

  // draws this head on the board connected to the tail of this
  public WorldScene drawOnBoard(WorldScene scene) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * scene                --WorldScene
     * METHODS ON PARAMETERS:
     * scene.placeImageXY(WorldImage img)     --WorldImage
     */
    return new DrawTail(scene.placeImageXY(this.draw(), this.location.x, this.location.y))
        .apply(this.tail);
  }

  // accepts a function object to be applied on this head
  public <T> T accept(IFunc<CentipedeHead, T> func) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * func             --IFunc<CentipedeHead, T>
     * METHODS ON PARAMETERS
     * func.apply(CentipedeHead head)       --T
     */
    return func.apply(this);
  }
}

// Represents an individual follower of a centipede
// (a list of followers represents the tail of the centipede)
class CentipedeFollower extends AMoveableObject {

  Posn previousGoal;
  Util u = new Util();
  int unit;

  CentipedeFollower(Posn location, int speed, int size, Posn previousGoal, int unit) {
    super(location, speed, size);
    this.previousGoal = previousGoal;
    this.unit = unit;
  }

  /*
   * FIELDS:
   * this.previousGoal               --Posn
   * this.u                          --Util
   * this.unit                       --int
   * METHODS:
   * this.draw()                                          --WorldImage
   * this.followToGoal()                                  --CentipedeFollower
   * this.validMove(Board b, Posn direction)              --boolean
   * this.movedPosToGoal(Posn goal)                       --Posn
   * this.newMoveForward()                                --MoveForward
   * this.transformToHead(IList<CentipedeFollower> tail)  --CentipedeHead
   * this.setPreviousToLoc()                              --CentipedeFollower
   * METHODS ON FIELDS:
   * 
   */

  // draws this follower, which is just a simple circle
  WorldImage draw() {
    /*
     * TEMPLATE: SAME AS CLASS
     */
    return new CircleImage(this.size, OutlineMode.SOLID, Color.BLACK);
  }

  // If this location is within one tick of goal, does not move and resets previous goal to goal.
  // If not, moves htis forward by one tick.
  CentipedeFollower followToGoal(Posn goal) {
    /*
     * TEMPLATE: SAME AS CLASS
     */
    if (u.posnInRadius(this.location, goal, this.speed / 2)) {
      return new CentipedeFollower(this.location, this.speed, this.size, goal, this.unit);
    }
    else {
      return new CentipedeFollower(this.movedPosToGoal(goal), this.speed, this.size,
          this.previousGoal, this.unit);
    }
  }
  
  // followers can always make valid move
  public boolean validMove(Board b, Posn direction) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * b                --Board
     * direction        --Posn
     * METHODS ON PARAMETERS:
     * b.makeBoard(int row, int col) - IList<IList<ITile>>
     * b.makeRow(int row, int col) - IList<ITile>
     * b.draw() - WorldImage
     * b.drawOnBoard() - WorldScene 
     * b.collisionOccursLeft(Posn location, int size) - boolean
     * b.collisionOccursRight(Posn location, int size) - boolean
     * b.collisionOccurs(Posn location, int size) - boolean
     * b.produceDart(Gnome gnome, int speed) - Dart
     * b.randomBoard(boolean isPebbles, int num, Random rand) - Board
     * b.changeAtLocation(Posn location, boolean createPebbles) - Board
     * b.clickAtLocation(Posn location, boolean isLeft) - Board
     */

    return true;
  }
  
  // moves follower one tick to goal
  Posn movedPosToGoal(Posn goal) {
    /*
     * TEMPLATE: SAME AS CLASS
     */
    Util u = new Util();
    int moveX = (goal.x - this.location.x);
    int moveY = (goal.y - this.location.y);
    if (moveY > 0) {
      return u.moveInDirection(this.location, new Posn(0, 1), this.speed);
    }
    else if (moveX < 0) {
      return u.moveInDirection(this.location, new Posn(-1, 0), this.speed);
    }
    else if (moveX > 0) {
      return u.moveInDirection(this.location, new Posn(1, 0), this.speed);
    }
    else {
      return this.location;
    }
  }
  
  // creates a function object which allows the rest of tail to move to this
  MoveTailForward newMoveForward() {
    /*
     * TEMPLATE: SAME AS CLASS
     */
    return new MoveTailForward(this.previousGoal);
  }
  
  // transforms this follower into a head with the same attributes
  CentipedeHead transformToHead(IList<CentipedeFollower> tail) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * tail                --IList<CentipedeFollower>
     * METHODS ON PARAMETERS:
     * tail.accept(IListVisitor<CentipedeFollower, U>)      --U
     * tail.size()                                          --int
    */
    Posn center = u.getCenter(this.location, this.unit);
    return new CentipedeHead(center, this.speed, this.size, center,
        new Posn(center.x, center.y + 40), this.unit, tail);
  }

  // sets the previous goal of this to the center of the location of this
  CentipedeFollower setPreviousToLoc() {
    /*
     * TEMPLATE: SAME AS CLASS
     */
    Posn center = u.getCenter(this.location, this.unit);
    return new CentipedeFollower(center, this.speed, this.size, center, this.unit);
  }

}

// Moves all of the followers in a list forward by one tick
class MoveTailForward implements IListVisitor<CentipedeFollower, IList<CentipedeFollower>> {

  Posn goal;

  MoveTailForward(Posn goal) {
    this.goal = goal;
  }
  
  /*
   * FIELDS:
   * this.goal                        --Posn
   * METHODS:
   * this.apply(IList<CentipedeFollower> arg)          --IList<CentipedeFollower>>
   * this.visitMt(MtList<CentipedeFollower> mt)        --IList<CentipedeFollower>>
   * this.visitCons(ConsList<CentipedeFollower> cons)  --IList<CentipedeFollower>>
   */ 

  // applies this to arg
  public IList<CentipedeFollower> apply(IList<CentipedeFollower> arg) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * arg                      --IList<CentipedeFollower>
     * METHODS ON PARAMETERS:
     * arg.accept(IListVisitor<CentipedeFollower, U> visitor)     - U
     * arg.length()                                               - int
     */
    return arg.accept(this);
  }

  // returns empty list since there are no followers to move forward
  public IList<CentipedeFollower> visitMt(MtList<CentipedeFollower> mt) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * mt                      --MtList<CentipedeFollower>
     * METHODS ON PARAMETERS:
     * mt.accept(IListVisitor<CentipedeFollower, U> visitor)     - U
     * mt.length()                                               - int
     */
    return mt;
  }

  // moves the first of cons forward by one tick then recurs on cons.rest
  public IList<CentipedeFollower> visitCons(ConsList<CentipedeFollower> cons) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * cons                      --ConsList<CentipedeFollower>
     * METHODS ON PARAMETERS:
     * cons.accept(IListVisitor<CentipedeFollower, U> visitor)     - U
     * cons.length()                                               - int
     * METHODS ON FIELDS OF PARAMETERS:
     * cons.first.draw()                                          --WorldImage
     * cons.first.followToGoal()                                  --CentipedeFollower
     * cons.first.validMove(Board b, Posn direction)              --boolean
     * cons.first.movedPosToGoal(Posn goal)                       --Posn
     * cons.first.newMoveForward()                                --MoveForward
     * cons.first.transformToHead(IList<CentipedeFollower> tail)  --CentipedeHead
     * cons.first.setPreviousToLoc()                              --CentipedeFollower
     */
    return new ConsList<CentipedeFollower>(cons.first.followToGoal(goal),
        cons.first.newMoveForward().apply(cons.rest));
  }

}

// Draws a list of followers onto a world scene
class DrawTail implements IListVisitor<CentipedeFollower, WorldScene> {
  WorldScene scene;

  DrawTail(WorldScene scene) {
    this.scene = scene;
  }
  
  /*
   * TEMPLATE:
   * FIELDS:
   * scene            --WorldScene
   * METHODS ON FIELDS:
   * scene.placeImageXY(WorldImage img)     --WorldImage
   */

  // applies this to arg
  public WorldScene apply(IList<CentipedeFollower> arg) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * arg                      --IList<CentipedeFollower>
     * METHODS ON PARAMETERS:
     * arg.accept(IListVisitor<CentipedeFollower, U> visitor)     - U
     * arg.length()                                               - int
     */
    return arg.accept(this);
  }

  // returns the scene with no followers drawn on
  public WorldScene visitMt(MtList<CentipedeFollower> mt) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * mt                      --MtList<CentipedeFollower>
     * METHODS ON PARAMETERS:
     * mt.accept(IListVisitor<CentipedeFollower, U> visitor)     - U
     * mt.length()                                               - int
     */
    return scene;
  }

  // draws the first follower in this onto the scene, then recurs on the list
  public WorldScene visitCons(ConsList<CentipedeFollower> cons) {
    /*
     * TEMPLATE: SAME AS CLASS
     * PARAMETERS:
     * cons                      --ConsList<CentipedeFollower>
     * METHODS ON PARAMETERS:
     * cons.accept(IListVisitor<CentipedeFollower, U> visitor)     - U
     * cons.length()                                               - int
     * METHODS ON FIELDS OF PARAMETERS:
     * cons.first.draw()                                          --WorldImage
     * cons.first.followToGoal()                                  --CentipedeFollower
     * cons.first.validMove(Board b, Posn direction)              --boolean
     * cons.first.movedPosToGoal(Posn goal)                       --Posn
     * cons.first.newMoveForward()                                --MoveForward
     * cons.first.transformToHead(IList<CentipedeFollower> tail)  --CentipedeHead
     * cons.first.setPreviousToLoc()                              --CentipedeFollower
     */
    return new DrawTail(cons.first.drawOnBoard(scene)).apply(cons.rest);
  }

}