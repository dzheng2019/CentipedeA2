

// increments all elements in a list above x by one 
class IncrementAboveX implements IListVisitor<Integer, IList<Integer>> {
  int x;
  
  /* Template:
   * Same as interface
   * Field:
   * x - int
   */
  
  IncrementAboveX(int x) {
    this.x = x;
  }
  
  // applies this func to a list
  public IList<Integer> apply(IList<Integer> arg) {
    return arg.accept(this);
  }

  // returns mt
  public IList<Integer> visitMt(MtList<Integer> mt) {
    return mt;
  }

  // returns a list where this element is incremented if it is above X
  public IList<Integer> visitCons(ConsList<Integer> cons) {
    if (cons.first >= x) {
      return new ConsList<Integer>(cons.first + 1, cons.rest.accept(this));
    }
    else {
      return new ConsList<Integer>(cons.first, cons.rest.accept(this));
    }
  } 
}

// increase all elements in this list by X
class IncrementAllByX implements IListVisitor<Integer, IList<Integer>> {
  int x;
  
  /* Template:
   * Same as interface
   * Field:
   * x - int
   */
  
  IncrementAllByX(int x) {
    this.x = x;
  }
  
  // applies this function to list
  public IList<Integer> apply(IList<Integer> arg) {
    return arg.accept(this);
  }

  // returns mt
  public IList<Integer> visitMt(MtList<Integer> mt) {
    return mt;
  }

  // increases this element by x
  public IList<Integer> visitCons(ConsList<Integer> cons) {
    return new ConsList<Integer>(cons.first + x, cons.rest.accept(this));
  }
}





