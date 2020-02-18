import java.awt.Color;

import javalib.funworld.WorldScene;
import javalib.worldimages.CircleImage;
import javalib.worldimages.EquilateralTriangleImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.Posn;
import javalib.worldimages.RotateImage;
import javalib.worldimages.WorldImage;

class ChangeCentipedeHeadsAtLocation implements IListVisitor<CentipedeHead, IList<CentipedeHead>> {

  IFunc<CentipedeHead, IList<CentipedeHead>> splitHeadAtLocation;
  Posn location;
  ChangeCentipedeHeadsAtLocation(Posn location) {
    this.splitHeadAtLocation = new SplitHeadAtLocation(location);
    this.location = location;
  }

   
  public IList<CentipedeHead> apply(IList<CentipedeHead> arg) {
    
    return arg.accept(this);
  }

   
  public IList<CentipedeHead> visitMt(MtList<CentipedeHead> mt) {
    
    return mt;
  }

   
  public IList<CentipedeHead> visitCons(ConsList<CentipedeHead> cons) {

    if (new LocationCollidesWithHead(this.location).apply(cons.first)) {
      return new Append<CentipedeHead>().apply(
          this.splitHeadAtLocation.apply(cons.first), 
          cons.rest);
    }
    else {
      return new Append<CentipedeHead>().apply(
          new ConsList<CentipedeHead>(cons.first, new MtList<CentipedeHead>()), 
          cons.rest.accept(this));
    }
  }  
}

class SplitHeadAtLocation implements IFunc<CentipedeHead, IList<CentipedeHead>> {
  Posn locationToChange;
  Util u = new Util();

  SplitHeadAtLocation(Posn location) {
    this.locationToChange = location;
  }


   
  public IList<CentipedeHead> apply(CentipedeHead head) {
    /* Template:
     * head.location
     * head.size
     * head.tails
     */
    if (u.posnInRadius(this.locationToChange, head.location, head.size)) {
      return new CreateHead().apply(head.tail);
    }
    else if (new LocationCollidesWithHead(this.locationToChange).apply(head)) {
      int index = new FindCollisionIndex(this.locationToChange).apply(head.tail);
      
      IList<CentipedeHead> newHead = 
          new ConsList<CentipedeHead>(
              head.shortenTail(new FirstNElements<CentipedeFollower>(index).apply(head.tail)),
              new MtList<CentipedeHead>());
      
      IList<CentipedeHead> newHeadTail = new MakeHeadAtIndex(index + 1).apply(head.tail);
      return new Append<CentipedeHead>().apply(newHead, newHeadTail);
    }
    return new ConsList<CentipedeHead>(head, new MtList<CentipedeHead>());
  }

}


// assumed to be ran
class CreateHead implements IListVisitor<CentipedeFollower, IList<CentipedeHead>> {
  
   
  public IList<CentipedeHead> apply(IList<CentipedeFollower> arg) {
    return arg.accept(this);
  }

   
  public IList<CentipedeHead> visitMt(MtList<CentipedeFollower> mt) {
    return new MtList<CentipedeHead>();
  }

   
  public IList<CentipedeHead> visitCons(ConsList<CentipedeFollower> cons) {
    return new ConsList<CentipedeHead>(cons.first.transformToHead(
        new UpdatePreviousLoc().apply(cons.rest)), new MtList<CentipedeHead>());
  }
}

class UpdatePreviousLoc implements IListVisitor<CentipedeFollower, IList<CentipedeFollower>> {

   
  public IList<CentipedeFollower> apply(IList<CentipedeFollower> arg) {
    
    return arg.accept(this);
  }

   
  public IList<CentipedeFollower> visitMt(MtList<CentipedeFollower> mt) {
    
    return mt;
  }

   
  public IList<CentipedeFollower> visitCons(ConsList<CentipedeFollower> cons) {
    
    return new ConsList<CentipedeFollower>(cons.first.setPreviousToLoc(),
        cons.rest.accept(this));
  }
  
}


class MakeHeadAtIndex implements IListVisitor<CentipedeFollower, IList<CentipedeHead>> {

  int index;
  
  MakeHeadAtIndex(int index) {
    this.index = index;
  }
  
   
  public IList<CentipedeHead> apply(IList<CentipedeFollower> arg) {
    
    return arg.accept(this);
  }

   
  public IList<CentipedeHead> visitMt(MtList<CentipedeFollower> mt) {
    
    return new MtList<CentipedeHead>();
  }

   
  public IList<CentipedeHead> visitCons(ConsList<CentipedeFollower> cons) {
    if (index == 0) {
      return new CreateHead().apply(new UpdatePreviousLoc().apply(cons));
    }
    else {
      return new MakeHeadAtIndex(index - 1).apply(cons.rest);
    }
  }
}

class FindCollisionIndex implements IListVisitor<CentipedeFollower, Integer> {

  Posn locationToChange;

  FindCollisionIndex(Posn locationToChange) {
    this.locationToChange = locationToChange;
  }

   
  public Integer apply(IList<CentipedeFollower> arg) {
    
    return arg.accept(this);
  }

   
  public Integer visitMt(MtList<CentipedeFollower> mt) {
    
    return 0;
  }

   
  public Integer visitCons(ConsList<CentipedeFollower> cons) {
    
    if (new LocationCollidesWithFollower(locationToChange).apply(cons.first)) {
      return 0;
    }
    else {
      return 1 + cons.rest.accept(this);
    }
  }
}

class LocationCollidesWithHead implements IPred<CentipedeHead> {
  Posn location;
  Util u = new Util();

  LocationCollidesWithHead(Posn location) {
    this.location = location;
  }

   
  public Boolean apply(CentipedeHead head) {
    return u.posnInRadius(head.location, this.location, head.size)
        || new OrMap<CentipedeFollower>(new LocationCollidesWithFollower(this.location)).apply(head.tail);
  }  
}

class LocationCollidesWithFollower implements IPred<CentipedeFollower> {
  Posn location;
  Util u = new Util();

  LocationCollidesWithFollower(Posn location) {
    this.location = location;
  }

   
  public Boolean apply(CentipedeFollower follower) {
    return u.posnInRadius(follower.location, this.location, follower.size);
  }
}


class CentipedeHead extends AMoveableObject {
  Posn previousGoal;
  Posn goal;
  int unit;
  IList<CentipedeFollower> tail;
  CentipedeHead (
      Posn location, 
      int speed, 
      int size,
      Posn previousGoal,
      Posn goal,
      int unit,
      IList<CentipedeFollower> tail) {
    super(location, speed, size);
    this.previousGoal = previousGoal;
    this.goal = goal;
    this.unit = unit;
    this.tail = tail;
  }

  CentipedeHead(
      Posn location, 
      int speed, 
      int size,
      Posn previousGoal,
      Posn goal,
      int unit) {
    super(location, speed, size);
    this.previousGoal = previousGoal;
    this.goal = goal;
    this.unit = unit;
    this.tail = this.createTail(10);

  }

  IList<CentipedeFollower> createTail(int numFollowers) {
    if (numFollowers <= 0) {
      return new MtList<CentipedeFollower>();
    }
    else {
      CentipedeFollower follower = 
          new CentipedeFollower(this.location, this.speed, this.size, this.previousGoal, this.unit);
      return new ConsList<CentipedeFollower>(follower, this.createTail(numFollowers - 1));
    }
  }

  CentipedeHead move(Board board) {
    int leftOrRight = this.location.y / this.unit + 1;
    Posn moveInCurDir = u.moveInDirection(this.location, new Posn(((leftOrRight % 2)*2 - 1), 0), this.speed);
    boolean withinGoal = u.posnInRadius(this.location, goal, speed/2);

    if (leftOrRight % 2 == 1 && board.collisionOccursRight(moveInCurDir, this.size) && withinGoal) {
      CentipedeHead newGoal = this.headWithNewGoal(this.getNextGoal(board));
      return newGoal.moveTowardsGoal();
    }
    else if (leftOrRight % 2 == 0 && board.collisionOccursLeft(moveInCurDir, this.size) && withinGoal) {
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

  CentipedeHead shortenTail(IList<CentipedeFollower> followers) {
    return new CentipedeHead(
        this.location, 
        this.speed, 
        this.size,
        this.previousGoal,
        this.goal,
        this.unit,
        followers); 
  }

  CentipedeHead headWithNewPos(Posn newPos) {
    return new CentipedeHead(newPos, this.speed, this.size,
        this.previousGoal, this.goal, this.unit, 
        new MoveTailForward(this.previousGoal).apply(this.tail));
  }

  CentipedeHead headWithNewGoal(Posn newGoal) {
    return new CentipedeHead(this.nextPos(), this.speed, this.size, 
        this.goal, newGoal, this.unit, 
        new MoveTailForward(this.previousGoal).apply(this.tail));
  }


  Posn nextPos() {
    Util u = new Util(); 
    int moveX = (this.goal.x - this.location.x);
    int moveY = (this.goal.y - this.location.y);
    if (moveY > 0) {
      return u.moveInDirection(this.location, new Posn(0, 1), this.speed);
    }
    else if (moveX < 0) {
      return u.moveInDirection(this.location, new Posn(-1, 0), this.speed);
    }
    else if (moveX > 0){
      return u.moveInDirection(this.location, new Posn(1, 0), this.speed);
    }
    else {
      return this.location;
    }
  }

  CentipedeHead moveTowardsGoal() {
    return this.headWithNewPos(this.nextPos()); 
  }

  Posn getNextGoal(Board b) {
    int leftOrRight = this.location.y / this.unit + 1;

    Posn moveInCurDir = u.moveInDirection(this.location, new Posn(((leftOrRight % 2)*2 - 1), 0), this.speed);
    Posn myCenter = u.getCenter(this.location, this.unit);
    if (b.collisionOccurs(moveInCurDir, this.size)) {
      return new Posn(myCenter.x, myCenter.y + unit);
    }
    else {
      return new Posn(myCenter.x + ((leftOrRight % 2)*2 - 1) *  unit, myCenter.y);
    }
  }

  public WorldImage draw() {

    WorldImage head = 
        new EquilateralTriangleImage(
            this.size * 2, 
            OutlineMode.SOLID, Color.RED).movePinholeTo(new Posn(0, 0));
    

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
  
  public WorldScene drawOnBoard(WorldScene scene) {
    return new DrawTail(scene.placeImageXY(this.draw(), this.location.x, this.location.y)).apply(this.tail);
  }
 
  public <T> T accept(IFunc<CentipedeHead, T> func) {
    return func.apply(this);
  } 
}

class CentipedeFollower extends AMoveableObject {

  Posn previousGoal;
  Util u = new Util();
  int unit;
  
  CentipedeFollower(Posn location, int speed, int size, Posn previousGoal, int unit) {
    super(location, speed, size);
    this.previousGoal = previousGoal;
    this.unit = unit;
  }


   
  WorldImage draw() {
    return new CircleImage(this.size, OutlineMode.SOLID, Color.BLACK);
  }

  CentipedeFollower followToGoal(Posn goal) {
    if (u.posnInRadius(this.location, goal, this.speed/2)) {
      return new CentipedeFollower(this.location, this.speed, this.size, goal, this.unit);
    }
    else {
      return new CentipedeFollower(this.movedPosToGoal(goal), this.speed, this.size, this.previousGoal, this.unit);
    }
  }

  
  public boolean validMove(Board b, Posn direction) {
    return true;
  }
  
  Posn movedPosToGoal(Posn goal) {
    Util u = new Util(); 
    int moveX = (goal.x - this.location.x);
    int moveY = (goal.y - this.location.y);
    if (moveY > 0) {
      return u.moveInDirection(this.location, new Posn(0, 1), this.speed);
    }
    else if (moveX < 0) {
      return u.moveInDirection(this.location, new Posn(-1, 0), this.speed);
    }
    else if (moveX > 0){
      return u.moveInDirection(this.location, new Posn(1, 0), this.speed);
    }
    else {
      return this.location;
    }
  }

  MoveTailForward newMoveForward() {
    return new MoveTailForward(this.previousGoal);
  }

  CentipedeHead transformToHead(IList<CentipedeFollower> tail) {
    Posn center = u.getCenter(this.location, this.unit);
    return new CentipedeHead(
        center, 
        this.speed, 
        this.size,
        center,
        new Posn(center.x, center.y + 40),
        this.unit,
        tail);
  }
  
  CentipedeFollower setPreviousToLoc() {
    Posn center = u.getCenter(this.location, this.unit);
    return new CentipedeFollower(center, this.speed, this.size, center, this.unit);
  }

}


class MoveTailForward implements IListVisitor<CentipedeFollower, IList<CentipedeFollower>> {

  Posn goal;

  MoveTailForward(Posn goal) {
    this.goal = goal;
  }

   
  public IList<CentipedeFollower> apply(IList<CentipedeFollower> arg) {
    return arg.accept(this);
  }

   
  public IList<CentipedeFollower> visitMt(MtList<CentipedeFollower> mt) {
    return mt;
  }

   
  public IList<CentipedeFollower> visitCons(ConsList<CentipedeFollower> cons) {
    return new ConsList<CentipedeFollower>(cons.first.followToGoal(goal),
        cons.first.newMoveForward().apply(cons.rest));
  }

}

class DrawTail implements IListVisitor<CentipedeFollower, WorldScene> {

  WorldScene scene;

  DrawTail(WorldScene scene) {
    this.scene = scene;
  }

  public WorldScene apply(IList<CentipedeFollower> arg) {
    return arg.accept(this);
  }

  public WorldScene visitMt(MtList<CentipedeFollower> mt) {
    return scene;
  }

  public WorldScene visitCons(ConsList<CentipedeFollower> cons) {
    return new DrawTail(cons.first.drawOnBoard(scene)).apply(cons.rest);
  }

}