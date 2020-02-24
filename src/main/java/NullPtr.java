import syntaxtree.*;
import visitor.*;
import java.util.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

class NullPtr {
public static void main(String[] args){
        VisitorGetSet getSet = new VisitorGetSet();
        MiniJavaParser myparser = new MiniJavaParser(System.in);
        try {
              Goal goal = myparser.Goal();
              goal.accept(getSet,null);

              NullPtrState state = new NullPtrState();
              state.getSet(getSet);
              
              for(int i = 0; i < state.ClassHierachy.size(); i++) {
                System.out.println(state.ClassHierachy.get(i));
              }            
              for(int i = 0; i < state.Var.size(); i++) {
                System.out.println(state.Var.get(i).prefix);
                System.out.println("type is " + state.Var.get(i).type);
                //System.out.println("Type is " + state.Var.get(i).Type);
              }
              for(int i = 0; i < state.ExpressionTable.size(); i++) {
                System.out.println(state.ExpressionTable.get(i).prefix);
                System.out.println(state.ExpressionTable.get(i).type);
                System.out.println(state.ExpressionTable.get(i).raw);
              }

        } catch (ParseException e) {
                System.out.println(e);
        }
}
}


class Metainfo {
   // location of this expression, should be class_method
   String prefix = "";
   // type of this expression
   String type = "";
   // full representation of this expression, copied in raw string
   String raw = "";
   // identifiers parsed (if below two)
   String identifier_1 = "";
   String identifier_2 = "";
}


class VarState {
   // class_method_N
   String prefix = "";
   // Type of var, class or primary type
   String Type = "";
   // 0: not touched
   // 1: class field
   // 2: method var
   // 3: method param
   int type = 0;
   // 0: not touched
   // 1: not null
   // 2: dont know
   int notnull = 0;
}

class NullPtrState{
        ArrayList<String> ClassHierachy;
        ArrayList<VarState> Var;
        ArrayList<String> MethodChain;
        ArrayList<Metainfo> ExpressionTable;
        ArrayList<Metainfo> AssignmentTable;
public NullPtrState getSet(VisitorGetSet Set) {
    ClassHierachy = Set.ClassHierachy;
    Var = Set.Var;
    MethodChain = Set.MethodChain;
    ExpressionTable = Set.ExpressionTable;
    AssignmentTable = Set.AssignmentTable;
    return this;
}
public NullPtrState processClassExtension() {

    return this;
}
}


class VisitorGetSet extends GJDepthFirst<Object,String>{
        ArrayList<String> ClassHierachy = new ArrayList<String>();
        ArrayList<Metainfo> ExpressionTable = new ArrayList<Metainfo>();
        ArrayList<Metainfo> AssignmentTable = new ArrayList<Metainfo>();
        ArrayList<VarState> Var = new ArrayList<VarState>();
        ArrayList<String> MethodChain = new ArrayList<String>();

public Object visit(MethodDeclaration n, String s) {
        n.f7.accept(this, s + "_" + n.f2.f0.toString());
        n.f8.accept(this, s + "_" + n.f2.f0.toString());
        n.f10.accept(this, s + "_" + n.f2.f0.toString());
        n.f4.accept(this, s + "_" + n.f2.f0.toString());
        MethodChain.add(s + "_" + n.f2.f0.toString());
        return null;
}
public Object visit(ClassDeclaration n, String s) {
        ClassHierachy.add(n.f1.f0.toString());
        n.f3.accept(this, n.f1.f0.toString() + "_class");
        n.f4.accept(this, n.f1.f0.toString());
        return null;
}
public Object visit(ClassExtendsDeclaration n, String s) {
        ClassHierachy.add(n.f1.f0.toString() + "_extends_" + n.f3.f0.toString());
        // look up and add baseclass info
        for (int i = 0; i < Var.size(); i++) {
            if (Var.get(i).prefix.indexOf(n.f3.f0.toString() + "_") >= 0) {
                // copy with new one
                VarState state = new VarState();
                state.type = Var.get(i).type;
                state.notnull = Var.get(i).notnull;
                state.prefix = Var.get(i).prefix;
                state.prefix = state.prefix.replaceAll(n.f3.f0.toString(), n.f1.f0.toString());
                Var.add(state);
            };
        }
        n.f5.accept(this, n.f1.f0.toString());
        n.f6.accept(this, n.f1.f0.toString());
        return null;
}
public Object visit(VarDeclaration n, String s) {
        VarState state = new VarState();
        String sub = "";
        if(s != null) {
            // if the string passed in having "_class", indicating this is a class field
            if (s.indexOf("_class") > 0) {
              // remove the "_class" label
              sub = s.replaceAll("_class", "");
              state.prefix = sub + '_' + n.f1.f0.toString();
              state.type = 1; // labeled as class field
            }
            else {
              // is method local var
              state.prefix = s + '_' + n.f1.f0.toString();
              state.type = 2; // labeled as method var
                      
            }
            state.notnull = 1; // labaled as not null
            //state.Type = n.f0.f0.toString();
            Var.add(state);
        }
        n.f0.accept(this,n.f1.f0.toString());
        return null;
}

public Object visit(FormalParameter n, String s) {
        VarState state = new VarState();
        state.prefix = s + n.f1.f0.toString();
        //state.Type = n.f0.f0.toString();
        state.type = 3; // indicating a method param
        state.notnull = 1;
        Var.add(state);

        // I don't know why passing this
        n.f0.accept(this,n.f1.f0.toString());
        return null;
}
public Object visit(Type n, String s){
        n.f0.accept(this,s);
        return null;
}

public Object visit(MainClass n, String s) {
        n.f15.accept(this, n.f1.f0.toString() + "_" + "main");
        n.f14.accept(this, n.f1.f0.toString() + "_" + "main");
        return null;
}
//MessageSend
public Object visit(MessageSend n, String s){
        if (s != null) {
            Metainfo exp = new Metainfo();
            Printer p = new Printer();
            p.visit(n);

            exp.prefix = s;
            exp.type = "MessageSend";
            exp.identifier_1 = n.f2.f0.toString();
            exp.raw = p.toString();

            ExpressionTable.add(exp);
        }

        n.f0.accept(this,null);
        n.f4.accept(this,null);
        return null;
}
//PrintStatement
public Object visit(PrintStatement n, String s) {
        n.f2.accept(this, null);
        return null;
}
//BlockStatement
public Object visit(Block n, String s) {
        n.f1.accept(this, null);
        return null;
}

//AssignmentStatement
public Object visit(AssignmentStatement n, String s) {
        if (s != null) {
            Metainfo ass = new Metainfo();
            Printer p = new Printer();
            p.visit(n);

            ass.prefix = s;
            ass.type = "AssignmentStatement";
            ass.identifier_1 = n.f0.f0.toString();
            ass.raw = p.toString();

            AssignmentTable.add(ass);
            n.f2.accept(this, s);
            return null;
        }        
        n.f2.accept(this, s);
        return null;
}
//ArraryAssignmentStatement
public Object visit(ArrayAssignmentStatement n, String s) {
        n.f2.accept(this, null);
        n.f5.accept(this, null);
        return null;
}
//IfStatement
public Object visit(IfStatement n, String s) {
        n.f2.accept(this, null);
        n.f4.accept(this, null);
        n.f6.accept(this, null);
        return null;
}
//AllocationExpression
public Object visit(AllocationExpression n, String s){
    if (s != null) {
        Metainfo exp = new Metainfo();
        Printer p = new Printer();
        p.visit(n);

        exp.prefix = s;
        exp.type = "AllocationExpression";
        exp.identifier_1 = n.f1.f0.toString();
        exp.raw = p.toString();

        ExpressionTable.add(exp);
        return null;
    }
    return null;
}


//WhileStatement
public Object visit(WhileStatement n, String s) {
        n.f2.accept(this, null);
        n.f4.accept(this, null);
        return null;
}
// public Object visit(PrimaryExpression n, String s) {
//         n.f0.accept(this, s);
//         return null;
// }
//AndExpression
public Object visit(AndExpression n, String s){
        n.f0.accept(this, s);
        n.f2.accept(this, s);
        return null;
}

public Object visit(ThisExpression n, String s) {
    if (s != null) {
        Metainfo exp = new Metainfo();
        Printer p = new Printer();
        p.visit(n);

        exp.prefix = s;
        exp.type = "ThisExpression";
        exp.raw = p.toString();

        ExpressionTable.add(exp);
        return null;
    }
    n.f0.accept(this, s + "_this");
    return null;
   }

//CompareExpression
public Object visit(CompareExpression n, String s){
        n.f0.accept(this, s);
        n.f2.accept(this, s);
        return null;
}

//PlusExpression
public Object visit(PlusExpression n, String s){
        n.f0.accept(this, s);
        n.f2.accept(this, s);
        return null;
}
//MinusExpression
public Object visit(MinusExpression n, String s){
        n.f0.accept(this, s);
        n.f2.accept(this, s);
        return null;
}
//TimesExpression
public Object visit(TimesExpression n, String s){
        n.f0.accept(this, s);
        n.f2.accept(this, s);
        return null;
}
//ArrayLookupExpression
public Object visit(ArrayLookup n, String s){
        n.f0.accept(this, s);
        n.f2.accept(this, s);
        return null;
}
//ArrayLenghExpression
public Object visit(ArrayLength n, String s){
        n.f0.accept(this, s);
        return null;
}
//ExpressionList
public Object visit(ExpressionList n, String s){
        n.f0.accept(this,s);
        n.f1.accept(this,s);
        return null;
}
//ExpressionRest
public Object visit(ExpressionRest n, String s){
        n.f1.accept(this, s);
        return null;
}


}









class Printer {
    ArrayList<String> output;
    Printer() {
        output = new ArrayList<>();
    }
    public void visit(Node e) {
        if (e == null) {
            return;
        }
        Class<?> cls = e.getClass();
        Field[] fields = cls.getFields();
        for (Field f : fields) {
            if (NodeToken.class.isAssignableFrom(f.getType())) {
                try {
                    output.add(f.get(e).toString());
                }
                catch (IllegalAccessException ex) {
                    //
                }
            } else if (NodeList.class.isAssignableFrom(f.getType())) {
                try {
                    NodeList t = (NodeList)f.get(e);
                    visit(t);
                }
                catch (IllegalAccessException ex) {
                    //
                }
            } else if (Node.class.isAssignableFrom(f.getType())) {
                try {
                    Node t = (Node)f.get(e);
                    visit(t);
                }
                catch (IllegalAccessException ex) {
                    //
                }
            }
        }
    }
    public void visit(NodeList e) {
        for (Node n : e.nodes) {
            visit(n);
        }
    }
    public String toString() {
        return String.join(" ", output);
    }
}