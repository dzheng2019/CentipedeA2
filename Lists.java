// represents a list of type t
interface IList<T> {
  // accept the given list visitor into this
  <U> U accept(IListVisitor<T, U> visitor);
  // return the length of this
  int length();
}

// represents an empty list of type t
class MtList<T> implements IList<T> {
  // accepts a visitor of type t
  public <U> U accept(IListVisitor<T, U> visitor) {
    return visitor.visitMt(this);
  }

  // return the length of this (0 because empty)
  public int length() {
    return 0;
  }

}

// represents a element in a list of type t
class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }  

  // accepts a visitor of type t
  public <U> U accept(IListVisitor<T, U> visitor) {
    return visitor.visitCons(this);
  }

  //return the length of this (0 because empty)
  public int length() {
    return 1 + this.rest.length();
  }

}

//a function interface
interface IFunc<A, R> {
  R apply(A arg);
}

interface IPred<A> extends IFunc<A, Boolean> {

}

//a visitor that visits lists
interface IListVisitor<T, R> extends IFunc<IList<T>, R> {

  // visits an empty list
  R visitMt(MtList<T> mt);

  // visits an non empty list
  R visitCons(ConsList<T> cons);
}

class OrMap<T> implements IListVisitor<T, Boolean> {

  IPred<T> pred;

  OrMap(IPred<T> pred) {
    this.pred = pred;
  }

  @Override
  public Boolean apply(IList<T> arg) {
    // TODO Auto-generated method stub
    return arg.accept(this);
  }

  @Override
  public Boolean visitMt(MtList<T> mt) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Boolean visitCons(ConsList<T> cons) {
    // TODO Auto-generated method stub
    return pred.apply(cons.first) || this.apply(cons.rest);
  }

}

class OrMapListPred<T> implements IPred<IList<T>> {

  IPred<T> pred;

  OrMapListPred(IPred<T> pred) {
    this.pred = pred;
  }

  @Override
  public Boolean apply(IList<T> arg) {
    return new OrMap<T>(this.pred).apply(arg);
  }

}

class RowLength<T> implements IListVisitor<IList<T>, Integer> {
  @Override
  public Integer apply(IList<IList<T>> arg) {
    // TODO Auto-generated method stub
    return arg.accept(this);
  }

  @Override
  public Integer visitMt(MtList<IList<T>> mt) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Integer visitCons(ConsList<IList<T>> cons) {
    // TODO Auto-generated method stub
    return cons.first.length();
  }
}

class Map<T, R> implements IListVisitor<T, IList<R>> {

  IFunc<T, R> func; 

  Map(IFunc<T, R> func) {
    this.func = func;
  }

  @Override
  public IList<R> apply(IList<T> arg) {
    // TODO Auto-generated method stub
    return arg.accept(this);
  }

  @Override
  public IList<R> visitMt(MtList<T> mt) {
    // TODO Auto-generated method stub
    return new MtList<R>();
  }

  @Override
  public IList<R> visitCons(ConsList<T> cons) {
    // TODO Auto-generated method stub
    return new ConsList<R>(func.apply(cons.first), cons.rest.accept(this));
  }

}

class MapList<T, R> implements IFunc<IList<T>, IList<R>> {

  IFunc<T, R> func;

  MapList(IFunc<T, R> func) {
    this.func = func;
  }

  @Override
  public IList<R> apply(IList<T> arg) {
    return new Map<T, R>(func).apply(arg);
  } 
}

class ContainsInt implements IPred<Integer> {

  int check;

  ContainsInt(int check) {
    this.check = check;
  }

  @Override
  public Boolean apply(Integer arg) {
    // TODO Auto-generated method stub
    return arg.intValue() == check;
  }

}

class ChangeAtX<T> implements IListVisitor<T, IList<T>> {
  IFunc<T, T> func;
  int n;

  ChangeAtX(IFunc<T, T> func, int n) {
    this.func = func;
    this.n = n;
  }

  @Override
  public IList<T> apply(IList<T> arg) {
    // TODO Auto-generated method stub
    return arg.accept(this);
  }

  @Override
  public IList<T> visitMt(MtList<T> mt) {
    // TODO Auto-generated method stub
    return new MtList<T>();
  }

  @Override
  public IList<T> visitCons(ConsList<T> cons) {
    if (n < 0) {
      return cons;
    }
    if (n == 0) {
      return new ConsList<T>(func.apply(cons.first), cons.rest);
    }
    else {
      return new ConsList<T>(cons.first, new ChangeAtX<T>(func, n - 1).apply(cons.rest));
    }
  }
}

class ChangeAtXY<T> implements IListVisitor<IList<T>, IList<IList<T>>> {

  IFunc<T, T> func;
  int n;
  int r;

  ChangeAtXY(IFunc<T, T> func, int n, int r) {
    this.func = func;
    this.n = n;
    this.r = r;
  }

  @Override
  public IList<IList<T>> apply(IList<IList<T>> arg) {
    // TODO Auto-generated method stub
    return arg.accept(this);
  }

  @Override
  public IList<IList<T>> visitMt(MtList<IList<T>> mt) {
    // TODO Auto-generated method stub
    return mt;
  }

  @Override
  public IList<IList<T>> visitCons(ConsList<IList<T>> cons) {
    if (r < 0) {
      return cons;
    }
    if (r == 0) {
      return new ConsList<IList<T>>(
          new ChangeAtX<T>(func, n).apply(cons.first), cons.rest);
    }
    else {
      return new ConsList<IList<T>>(cons.first,
          new ChangeAtXY<T>(func, n, r - 1).apply(cons.rest));
    }
  } 
}

class MakeThree implements IFunc<Integer, Integer> {

  @Override
  public Integer apply(Integer arg) {
    // TODO Auto-generated method stub
    return 3;
  }

}

class ExamplesList {
  IList<Integer> OneDInt =
      new ConsList<Integer>(5, 
          new ConsList<Integer>(5, 
              new ConsList<Integer>(5, 
                  new ConsList<Integer>(5, new MtList<Integer>()))));
  IList<IList<Integer>> TwoDInt = 
      new ConsList<IList<Integer>>(OneDInt,
          new ConsList<IList<Integer>>(OneDInt, new MtList<IList<Integer>>()));

  IList<IList<Integer>> TwoDIntM = new ChangeAtXY<Integer>(new MakeThree(), 3, 1).apply(TwoDInt);
  
  
}




