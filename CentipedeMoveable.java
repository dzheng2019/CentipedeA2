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

class ExamplesCentipedeMoveable {
  int unit = 40;
  Posn startPos = new Posn(20, 20);
  int centiSpeed = 8;
  int centiSize = 20;
  Posn startGoal = new Posn(20, 20);
  Posn goal1 = new Posn(60, 20);
  Posn goal2 = new Posn(60, 60);

  CentipedeFollower f1 = new CentipedeFollower(startPos, centiSpeed, centiSize, startPos, unit);
  CentipedeFollower f2 = new CentipedeFollower(startPos, centiSpeed, centiSize, startPos, unit);
  CentipedeFollower f3 = new CentipedeFollower(startPos, centiSpeed, centiSize, startPos, unit);
  CentipedeFollower f4 = new CentipedeFollower(startPos, centiSpeed, centiSize, startPos, unit);
  CentipedeFollower f5 = new CentipedeFollower(startPos, centiSpeed, centiSize, startPos, unit);
  IList<CentipedeFollower> oneStartFollowers = new ConsList<CentipedeFollower>(f5,
      new MtList<CentipedeFollower>());
  IList<CentipedeFollower> twoStartFollowers = new ConsList<CentipedeFollower>(f4,
      oneStartFollowers);
  IList<CentipedeFollower> threeStartFollowers = new ConsList<CentipedeFollower>(f3,
      twoStartFollowers);
  IList<CentipedeFollower> fourStartFollowers = new ConsList<CentipedeFollower>(f2,
      threeStartFollowers);
  IList<CentipedeFollower> fiveStartFollowers = new ConsList<CentipedeFollower>(f1,
      fourStartFollowers);
  CentipedeHead h_three = new CentipedeHead(startPos, centiSpeed, centiSize, startPos, startGoal,
      unit, threeStartFollowers);
  CentipedeHead h_five = new CentipedeHead(startPos, centiSpeed, centiSize, startPos, startGoal,
      unit, fiveStartFollowers);
  CentipedeHead h_three_convenience = new CentipedeHead(startPos, centiSpeed, centiSize, startPos,
      startGoal, unit, 3);
  CentipedeHead h_five_convenience = new CentipedeHead(startPos, centiSpeed, centiSize, startPos,
      startGoal, unit, 5);

  CentipedeFollower f3_move1 = new CentipedeFollower(new Posn(28, 20), centiSpeed, centiSize,
      new Posn(20, 20), unit);
  CentipedeFollower f3_move2 = new CentipedeFollower(new Posn(36, 20), centiSpeed, centiSize,
      new Posn(20, 20), unit);
  CentipedeFollower f3_move3 = new CentipedeFollower(new Posn(44, 20), centiSpeed, centiSize,
      new Posn(20, 20), unit);
  CentipedeFollower f3_move4 = new CentipedeFollower(new Posn(52, 20), centiSpeed, centiSize,
      new Posn(20, 20), unit);
  CentipedeFollower f3_move5 = new CentipedeFollower(new Posn(60, 20), centiSpeed, centiSize,
      new Posn(60, 20), unit);

  IList<CentipedeFollower> variousf3 = new ConsList<CentipedeFollower>(f3_move3,
      new ConsList<CentipedeFollower>(f3_move4,
          new ConsList<CentipedeFollower>(f3_move5, new MtList<CentipedeFollower>())));

  IList<CentipedeFollower> oneFollower = new ConsList<CentipedeFollower>(f3_move1,
      new MtList<CentipedeFollower>());
  IList<CentipedeFollower> twoFollowers = new ConsList<CentipedeFollower>(f3_move2, oneFollower);
  IList<CentipedeFollower> threeFollowers = new ConsList<CentipedeFollower>(f3_move1, twoFollowers);
  IList<CentipedeFollower> fourFollowers = new ConsList<CentipedeFollower>(f3_move1,
      threeFollowers);
  IList<CentipedeFollower> fiveFollowers = new ConsList<CentipedeFollower>(f3_move1, fourFollowers);

  CentipedeFollower f3_move1_update = new CentipedeFollower(new Posn(20, 20), centiSpeed, centiSize,
      new Posn(20, 20), unit);
  CentipedeFollower f3_move2_update = new CentipedeFollower(new Posn(20, 20), centiSpeed, centiSize,
      new Posn(20, 20), unit);

  IList<CentipedeFollower> oneFollower_update = new ConsList<CentipedeFollower>(f3_move1_update,
      new MtList<CentipedeFollower>());
  IList<CentipedeFollower> twoFollowers_update = new ConsList<CentipedeFollower>(f3_move2_update,
      oneFollower_update);
  IList<CentipedeFollower> threeFollowers_update = new ConsList<CentipedeFollower>(f3_move1_update,
      twoFollowers_update);
  IList<CentipedeFollower> fourFollowers_update = new ConsList<CentipedeFollower>(f3_move1_update,
      threeFollowers_update);
  IList<CentipedeFollower> fiveFollowers_update = new ConsList<CentipedeFollower>(f3_move1_update,
      fourFollowers_update);

  CentipedeHead h_three_move1 = new CentipedeHead(new Posn(28, 20), centiSpeed, centiSize, startPos,
      goal1, unit, threeStartFollowers);
  CentipedeHead h_three_move2 = new CentipedeHead(new Posn(36, 20), centiSpeed, centiSize, startPos,
      goal1, unit, threeStartFollowers);
  CentipedeHead h_three_move3 = new CentipedeHead(new Posn(44, 20), centiSpeed, centiSize, startPos,
      goal1, unit, threeStartFollowers);
  CentipedeHead h_three_turnRight = new CentipedeHead(new Posn(52, 20), centiSpeed, centiSize,
      startPos, goal1, unit, threeStartFollowers);
  CentipedeHead h_three_move5 = new CentipedeHead(new Posn(60, 20), centiSpeed, centiSize, startPos,
      goal1, unit, threeStartFollowers);
  CentipedeHead h_three_move6 = new CentipedeHead(new Posn(60, 28), centiSpeed, centiSize, goal1,
      goal2, unit, new ConsList<CentipedeFollower>(f3_move1, twoStartFollowers));
  CentipedeHead h_three_move7 = new CentipedeHead(new Posn(60, 36), centiSpeed, centiSize, goal1,
      goal2, unit, new ConsList<CentipedeFollower>(f3_move2, twoStartFollowers));
  CentipedeHead h_three_move8 = new CentipedeHead(new Posn(60, 44), centiSpeed, centiSize, goal1,
      goal2, unit, new ConsList<CentipedeFollower>(f3_move3, twoStartFollowers));
  CentipedeHead h_three_move9 = new CentipedeHead(new Posn(60, 52), centiSpeed, centiSize, goal1,
      goal2, unit, new ConsList<CentipedeFollower>(f3_move4, twoStartFollowers));

  CentipedeHead head_only = new CentipedeHead(new Posn(60, 52), centiSpeed, centiSize, goal1, goal2,
      unit, new MtList<CentipedeFollower>());

  CentipedeHead f1ToHead = new CentipedeHead(new Posn(20, 20), centiSpeed, centiSize, startPos,
      new Posn(20, 60), unit, fourStartFollowers);

  CentipedeHead f2ToHead = new CentipedeHead(new Posn(20, 20), centiSpeed, centiSize, startPos,
      new Posn(20, 60), unit, threeStartFollowers);

  CentipedeHead f3ToHead = new CentipedeHead(new Posn(20, 20), centiSpeed, centiSize, startPos,
      new Posn(20, 60), unit, twoStartFollowers);

  CentipedeHead f4ToHead = new CentipedeHead(new Posn(20, 20), centiSpeed, centiSize, startPos,
      new Posn(20, 60), unit, oneStartFollowers);

  CentipedeHead f5ToHead = new CentipedeHead(new Posn(20, 20), centiSpeed, centiSize, startPos,
      new Posn(20, 60), unit, new MtList<CentipedeFollower>());

  IList<CentipedeHead> two_heads = new ConsList<CentipedeHead>(h_three,
      new ConsList<CentipedeHead>(h_three_move9, new MtList<CentipedeHead>()));

  IList<CentipedeHead> twoStartToHead = new ConsList<CentipedeHead>(f4ToHead,
      new MtList<CentipedeHead>());

  // Board is 80 pixels wide by 80 pixels tall
  Board boardNoObstacles = new Board(2, 2);

  // Board is 240 pixels wide by 160 pixels tall
  Board boardObstacles = new Board(6, 4).changeAtLocation(new Posn(1, 2), false)
      .changeAtLocation(new Posn(0, 1), false).changeAtLocation(new Posn(1, 1), false);

  WorldScene boardNoObstaclesScene = new WorldScene(80, 80);

  void testCentiConstructors(Tester t) {
    t.checkExpect(h_three, h_three_convenience);
    t.checkExpect(h_five, h_five_convenience);
    t.checkExpect(h_three.createTail(30).length(), 30);
    t.checkExpect(h_three.createTail(3), threeStartFollowers);
    t.checkExpect(h_three.createTail(5), fiveStartFollowers);
  }

  // Testing that the centipede drops down first then turns right when goal is
  // below and to right
  // This should always skip a row because the centipede's goal is to the right of
  // it in the second row
  // Since the centipede normally moves to the left in the second row, it checks
  // collisisons to the left.
  // There is a wall there, so the centipede will go down
  void testDropTurnRight(Tester t) {
    t.checkExpect(this.h_three.move(boardNoObstacles), h_three_move1);
    t.checkExpect(this.h_three_move1.move(boardNoObstacles), h_three_move2);
    t.checkExpect(this.h_three_move2.move(boardNoObstacles), h_three_move3);
    t.checkExpect(this.h_three_move3.move(boardNoObstacles), h_three_turnRight);
    t.checkExpect(this.h_three_turnRight.move(boardNoObstacles), h_three_move5);
    t.checkExpect(this.h_three_move5.move(boardNoObstacles), h_three_move6);
    t.checkExpect(this.h_three_move6.move(boardNoObstacles), h_three_move7);
    t.checkExpect(this.h_three_move7.move(boardNoObstacles), h_three_move8);
    t.checkExpect(this.h_three_move8.move(boardNoObstacles), h_three_move9);
  }

  void testShortenTail(Tester t) {
    t.checkExpect(h_three_move1.shortenTail(new MtList<CentipedeFollower>()),
        new CentipedeHead(new Posn(28, 20), centiSpeed, centiSize, startPos, goal1, unit,
            new MtList<CentipedeFollower>()));
    t.checkExpect(h_three_move3.shortenTail(oneStartFollowers), new CentipedeHead(new Posn(44, 20),
        centiSpeed, centiSize, startPos, goal1, unit, oneStartFollowers));
    t.checkExpect(h_three_move6.shortenTail(fourFollowers), new CentipedeHead(new Posn(60, 28),
        centiSpeed, centiSize, goal1, goal2, unit, fourFollowers));
  }

  void testHeadWithNewPos(Tester t) {
    t.checkExpect(h_three_move2.headWithNewPos(new Posn(0, 0)), new CentipedeHead(new Posn(0, 0),
        centiSpeed, centiSize, startPos, goal1, unit, threeStartFollowers));
    t.checkExpect(h_three_turnRight.headWithNewPos(new Posn(63, 71)), new CentipedeHead(
        new Posn(63, 71), centiSpeed, centiSize, startPos, goal1, unit, threeStartFollowers));
    t.checkExpect(h_five.headWithNewPos(new Posn(253, 167)), new CentipedeHead(new Posn(253, 167),
        centiSpeed, centiSize, startPos, startGoal, unit, fiveStartFollowers));
  }

  void testHeadWithNewGoal(Tester t) {
    t.checkExpect(h_three_move2.headWithNewGoal(new Posn(56, 40)),
        new CentipedeHead(new Posn(44, 20), centiSpeed, centiSize, new Posn(60, 20),
            new Posn(56, 40), unit, threeStartFollowers));
    t.checkExpect(h_three_turnRight.headWithNewGoal(new Posn(37, 120)),
        new CentipedeHead(new Posn(60, 20), centiSpeed, centiSize, new Posn(60, 20),
            new Posn(37, 120), unit, threeStartFollowers));
    t.checkExpect(h_five.headWithNewGoal(new Posn(195, 78)), new CentipedeHead(startPos, centiSpeed,
        centiSize, startPos, new Posn(195, 78), unit, fiveStartFollowers));
  }

  void testNextPos(Tester t) {
    t.checkExpect(h_three_move1.nextPos(), new Posn(36, 20));
    t.checkExpect(h_three_move2.nextPos(), new Posn(44, 20));
    t.checkExpect(h_three_move5.nextPos(), new Posn(60, 20));
    t.checkExpect(h_three_move7.nextPos(), new Posn(60, 44));
  }

  void testGetNextGoal(Tester t) {
    t.checkExpect(h_three_move2.getNextGoal(this.boardObstacles), new Posn(20, 60));
    t.checkExpect(h_three_turnRight.getNextGoal(this.boardObstacles), new Posn(100, 20));
    t.checkExpect(h_three_move5.getNextGoal(this.boardObstacles), new Posn(100, 20));
    t.checkExpect(h_five.getNextGoal(this.boardObstacles), new Posn(20, 60));
    t.checkExpect(h_three_move8.getNextGoal(this.boardObstacles), new Posn(20, 60));
  }

  void testDrawHead(Tester t) {
    t.checkExpect(head_only.draw(),
        new RotateImage(new EquilateralTriangleImage(centiSize * 2, OutlineMode.SOLID, Color.RED)
            .movePinholeTo(new Posn(0, 0)), 180.0));
    t.checkExpect(h_three.draw(),
        new RotateImage(new EquilateralTriangleImage(centiSize * 2, OutlineMode.SOLID, Color.RED)
            .movePinholeTo(new Posn(0, 0)), 90.0));
    t.checkExpect(h_five.draw(),
        new RotateImage(new EquilateralTriangleImage(centiSize * 2, OutlineMode.SOLID, Color.RED)
            .movePinholeTo(new Posn(0, 0)), 90.0));
    t.checkExpect(h_three_move9.draw(),
        new RotateImage(new EquilateralTriangleImage(centiSize * 2, OutlineMode.SOLID, Color.RED)
            .movePinholeTo(new Posn(0, 0)), 180.0));
  }

  void testDrawFollower(Tester t) {
    t.checkExpect(f1.draw(), new CircleImage(centiSize, OutlineMode.SOLID, Color.BLACK));
    t.checkExpect(f3.draw(), new CircleImage(centiSize, OutlineMode.SOLID, Color.BLACK));
    t.checkExpect(f3_move5.draw(), new CircleImage(centiSize, OutlineMode.SOLID, Color.BLACK));
  }

  void testMoveTailForward(Tester t) {
    MoveTailForward func = new MoveTailForward(new Posn(80, 60));
    t.checkExpect(func.apply(new MtList<CentipedeFollower>()), new MtList<CentipedeFollower>());
    t.checkExpect(func.apply(this.oneStartFollowers), new ConsList<CentipedeFollower>(
        f5.followToGoal(new Posn(80, 60)), new MtList<CentipedeFollower>()));
    t.checkExpect(func.apply(this.threeFollowers),
        new ConsList<CentipedeFollower>(f3_move1.followToGoal(new Posn(80, 60)),
            new ConsList<CentipedeFollower>(f3_move2.followToGoal(new Posn(28, 20)),
                new ConsList<CentipedeFollower>(f3_move1.followToGoal(new Posn(20, 20)),
                    new MtList<CentipedeFollower>()))));
  }

  void testDrawTail(Tester t) {
    DrawTail func = new DrawTail(boardNoObstaclesScene);
    t.checkExpect(func.apply(new MtList<CentipedeFollower>()), boardNoObstaclesScene);
    t.checkExpect(func.apply(threeFollowers_update), f3_move1_update.drawOnBoard(
        f3_move2_update.drawOnBoard(f3_move1_update.drawOnBoard(boardNoObstaclesScene))));
    t.checkExpect(func.apply(twoFollowers),
        f3_move1.drawOnBoard(f3_move2.drawOnBoard(boardNoObstaclesScene)));
  }

  void testChangeCentipedeHeadsAtLocation(Tester t) {
    ChangeCentipedeHeadsAtLocation func1 = new ChangeCentipedeHeadsAtLocation(new Posn(60, 52));
    ChangeCentipedeHeadsAtLocation func2 = new ChangeCentipedeHeadsAtLocation(new Posn(36, 20));
    t.checkExpect(func1.apply(new MtList<CentipedeHead>()), new MtList<CentipedeHead>());
    t.checkExpect(func1.apply(new ConsList<CentipedeHead>(head_only, new MtList<CentipedeHead>())),
        new MtList<CentipedeHead>());
    t.checkExpect(
        func1.apply(new ConsList<CentipedeHead>(h_three_turnRight,
            new ConsList<CentipedeHead>(head_only, new MtList<CentipedeHead>()))),
        new ConsList<CentipedeHead>(h_three_turnRight, new MtList<CentipedeHead>()));
    t.checkExpect(
        func2.apply(new ConsList<CentipedeHead>(h_three_move7, new MtList<CentipedeHead>())),
        new ConsList<CentipedeHead>(new CentipedeHead(new Posn(60, 36), centiSpeed, centiSize,
            goal1, goal2, unit, new MtList<CentipedeFollower>()), twoStartToHead));
  }

  void testSplitHeadAtLocation(Tester t) {
    SplitHeadAtLocation func1 = new SplitHeadAtLocation(new Posn(60, 52));
    SplitHeadAtLocation func2 = new SplitHeadAtLocation(new Posn(36, 20));
    t.checkExpect(func1.apply(head_only), new MtList<CentipedeHead>());
    t.checkExpect(func1.apply(h_three_turnRight),
        new ConsList<CentipedeHead>(h_three_turnRight, new MtList<CentipedeHead>()));
    t.checkExpect(func2.apply(h_three_move7),
        new ConsList<CentipedeHead>(new CentipedeHead(new Posn(60, 36), centiSpeed, centiSize,
            goal1, goal2, unit, new MtList<CentipedeFollower>()), twoStartToHead));
  }

  void testCreateHead(Tester t) {
    CreateHead func = new CreateHead();
    t.checkExpect(func.apply(new MtList<CentipedeFollower>()), new MtList<CentipedeHead>());
    t.checkExpect(func.apply(oneStartFollowers),
        new ConsList<CentipedeHead>(f5ToHead, new MtList<CentipedeHead>()));
    t.checkExpect(func.apply(twoStartFollowers),
        new ConsList<CentipedeHead>(f4ToHead, new MtList<CentipedeHead>()));
    t.checkExpect(func.apply(threeStartFollowers),
        new ConsList<CentipedeHead>(f3ToHead, new MtList<CentipedeHead>()));
    t.checkExpect(func.apply(fourStartFollowers),
        new ConsList<CentipedeHead>(f2ToHead, new MtList<CentipedeHead>()));
  }

  void testUpdatePreviousLoc(Tester t) {
    UpdatePreviousLoc func = new UpdatePreviousLoc();
    t.checkExpect(func.apply(new MtList<CentipedeFollower>()), new MtList<CentipedeHead>());
    t.checkExpect(func.apply(oneStartFollowers), oneStartFollowers);
    t.checkExpect(func.apply(fourStartFollowers), fourStartFollowers);
    t.checkExpect(func.apply(oneFollower), oneFollower_update);
    t.checkExpect(func.apply(twoFollowers), twoFollowers_update);
    t.checkExpect(func.apply(threeFollowers), threeFollowers_update);
    t.checkExpect(func.apply(fourFollowers), fourFollowers_update);
    t.checkExpect(func.apply(fiveFollowers), fiveFollowers_update);
  }

  void testMakeHeadAtIndex(Tester t) {
    MakeHeadAtIndex func1 = new MakeHeadAtIndex(0);
    MakeHeadAtIndex func2 = new MakeHeadAtIndex(2);

    t.checkExpect(func1.apply(oneStartFollowers),
        new ConsList<CentipedeHead>(f5ToHead, new MtList<CentipedeHead>()));
    t.checkExpect(func1.apply(threeStartFollowers),
        new ConsList<CentipedeHead>(f3ToHead, new MtList<CentipedeHead>()));
    t.checkExpect(func1.apply(fiveStartFollowers),
        new ConsList<CentipedeHead>(f1ToHead, new MtList<CentipedeHead>()));
    t.checkExpect(func2.apply(oneStartFollowers), new MtList<CentipedeHead>());
    t.checkExpect(func2.apply(threeStartFollowers),
        new ConsList<CentipedeHead>(new CentipedeHead(new Posn(20, 20), centiSpeed, centiSize,
            startPos, new Posn(20, 60), unit, new MtList<CentipedeFollower>()),
            new MtList<CentipedeHead>()));
  }

  // Test that the method returns the index of the first object which is collided
  // with
  void testFindCollisionIndex(Tester t) {
    FindCollisionIndex func1 = new FindCollisionIndex(new Posn(20, 20));
    FindCollisionIndex func2 = new FindCollisionIndex(new Posn(75, 10));
    FindCollisionIndex func3 = new FindCollisionIndex(new Posn(100, 40));

    t.checkExpect(func1.apply(fiveStartFollowers), 0);
    t.checkExpect(func2.apply(variousf3), 2);
    t.checkExpect(func3.apply(variousf3), 3);
  }

  void testLocationCollidesWithHead(Tester t) {
    LocationCollidesWithHead func1 = new LocationCollidesWithHead(new Posn(20, 20));
    LocationCollidesWithHead func2 = new LocationCollidesWithHead(new Posn(100, 20));
    t.checkExpect(func1.apply(f5ToHead), true);
    t.checkExpect(func1.apply(f2ToHead), true);
    t.checkExpect(func2.apply(f4ToHead), false);
    t.checkExpect(func2.apply(f3ToHead), false);

  }

  void testLocationCollidesWithFollower(Tester t) {
    LocationCollidesWithFollower func1 = new LocationCollidesWithFollower(new Posn(20, 20));
    LocationCollidesWithFollower func2 = new LocationCollidesWithFollower(new Posn(28, 20));
    LocationCollidesWithFollower func3 = new LocationCollidesWithFollower(new Posn(54, 20));

    t.checkExpect(func1.apply(f3_move1), true);
    t.checkExpect(func1.apply(f3_move2), true);
    t.checkExpect(func2.apply(f3_move4), false);
    t.checkExpect(func2.apply(f3_move5), false);
    t.checkExpect(func3.apply(f3_move3), true);
    t.checkExpect(func3.apply(f3_move1), false);
  }

}