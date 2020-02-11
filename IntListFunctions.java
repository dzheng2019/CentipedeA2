import java.util.Random;
import javalib.funworld.*;
import javalib.worldimages.*;
import java.awt.Color;
import tester.Tester;

// increments all elements in a list above x by one 
class IncrementAboveX implements IListVisitor<Integer, IList<Integer>> {

  int x;
  
  IncrementAboveX(int x) {
    this.x = x;
  }
  
  @Override
  public IList<Integer> apply(IList<Integer> arg) {
    return arg.accept(this);
  }

  @Override
  public IList<Integer> visitMt(MtList<Integer> mt) {
    return mt;
  }

  @Override
  public IList<Integer> visitCons(ConsList<Integer> cons) {
    if (cons.first >= x) {
      return new ConsList<Integer>(cons.first + 1, cons.rest.accept(this));
    }
    else {
      return new ConsList<Integer>(cons.first, cons.rest.accept(this));
    }
  } 
}

class IncrementAllByX implements IListVisitor<Integer, IList<Integer>> {
  int x;
  
  IncrementAllByX(int x) {
    this.x = x;
  }
  
  @Override
  public IList<Integer> apply(IList<Integer> arg) {
    return arg.accept(this);
  }

  @Override
  public IList<Integer> visitMt(MtList<Integer> mt) {
    return mt;
  }

  @Override
  public IList<Integer> visitCons(ConsList<Integer> cons) {
    return new ConsList<Integer>(cons.first + x, cons.rest.accept(this));
  }
}



