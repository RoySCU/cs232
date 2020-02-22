import syntaxtree.*;
import visitor.*;
import java.util.*;

class CallGraph {
public static void main(String[] args){
        VisitorGetSet getSet = new VisitorGetSet();
        VisitorComputeSet computeSet = new VisitorComputeSet();

        MiniJavaParser myparser = new MiniJavaParser(System.in);
        try {
              Goal goal = myparser.Goal();
              goal.accept(getSet,null);

              CallGraphState state = new CallGraphState();
              state.getSet(getSet).processExtendedClassses(getSet).setSet(computeSet);
              
              goal.accept(computeSet,null);
             
              for(int i = 0; i < computeSet.OuputChain.size(); i++) {
                System.out.println(computeSet.OuputChain.get(i));
              }
        } catch (ParseException e) {
                System.out.println(e);
        }
}
}

















class CallGraphState{
        ArrayList<ArrayList<String> > ClassHierachy;
        ArrayList<ArrayList<String> > VarType;
        ArrayList<ArrayList<String> > MethodType;
        ArrayList<String> MethodChain;
        ArrayList<String> OuputChain;
public CallGraphState getSet(VisitorGetSet getSet) {
        this.MethodChain = getSet.MethodChain;
        this.ClassHierachy = getSet.ClassHierachy;
        this.VarType = getSet.VarType;
        this.MethodType = getSet.MethodType;   
        return this;  
}  
public CallGraphState processExtendedClassses(VisitorGetSet getSet){
        for(int i = 0; i < getSet.ClassHierachy.size(); i++) {
          for(int j = i + 1; j < getSet.ClassHierachy.size(); j++) {
             if(getSet.ClassHierachy.get(i).indexOf(getSet.ClassHierachy.get(j).get(0)) > 0) {
                for(int k = 1; k < getSet.ClassHierachy.get(j).size(); k++) {
                 this.ClassHierachy.get(i).add(getSet.ClassHierachy.get(j).get(k));
                }
             }
          }
        }

        for(int i = 0; i < this.VarType.size(); i++) {
           for(int j = 0; j < this.ClassHierachy.size(); j++) {
             for(int k = 0; k < this.ClassHierachy.get(j).size(); k++) {
                if(this.VarType.get(i).indexOf(this.ClassHierachy.get(j).get(0)) == 1 && 
                   this.VarType.get(i).indexOf(this.ClassHierachy.get(j).get(k)) < 0) {
                   this.VarType.get(i).add(this.ClassHierachy.get(j).get(k));
                }
             }
           }
        }
        return this;
}
public CallGraphState setSet(VisitorComputeSet computeSet){
        computeSet.MethodChain = this.MethodChain;
        computeSet.ClassHierachy = this.ClassHierachy;
        computeSet.VarType = this.VarType;
        computeSet.MethodType = this.MethodType;
        return this;
}
}




class VisitorGetSet extends GJDepthFirst<Object,String>{

        // to put class name heere
        String ClassName="";
        // to put method name here
        String MethodName="";
        ArrayList<ArrayList<String> > ClassHierachy = new ArrayList<ArrayList<String> >();
        ArrayList<ArrayList<String> > VarType = new ArrayList<ArrayList<String> >();
        ArrayList<String> MethodChain = new ArrayList<String>();
        ArrayList<ArrayList<String> > MethodType = new ArrayList<ArrayList<String> >();

public Object visit(MethodDeclaration n, String s) {
        n.f7.accept(this, null);
        n.f8.accept(this, null);
        n.f10.accept(this,null);
        n.f4.accept(this,s + "_" + n.f2.f0.toString());
        MethodChain.add(s + "_" + n.f2.f0.toString());
        MethodName = s + "_" + n.f2.f0.toString();
        n.f1.accept(this, "MethodType");
        return null;
}
public Object visit(ClassDeclaration n, String s) {
        ClassHierachy.add(new ArrayList<String>(Arrays.asList(n.f1.f0.toString())));
        n.f3.accept(this, null);
        n.f4.accept(this, n.f1.f0.toString());
        return null;
}
public Object visit(ClassExtendsDeclaration n, String s) {
        ClassHierachy.add(new ArrayList<String>(Arrays.asList(n.f1.f0.toString())));
        for(int i = 0; i < ClassHierachy.size(); i++) {
        if(ClassHierachy.get(i).get(0) == n.f3.f0.toString()) {
        ClassHierachy.get(i).add(n.f1.f0.toString());
        }
        }

        n.f5.accept(this, null);
        n.f6.accept(this, n.f1.f0.toString());
        return null;
}
public Object visit(VarDeclaration n, String s) {
        if(s != null) {
                if(VarType.size() > 0) {
                  for(int i = 0; i < VarType.size(); i++) {
                        if(VarType.get(i).get(0) == n.f1.f0.toString() && 
                           VarType.get(i).indexOf(n.f1.f0.toString()) < 0) {
                           VarType.get(i).add(n.f1.f0.toString());
                           n.f0.accept(this,n.f1.f0.toString());
                           return null;
                        }
                        if(VarType.get(i).get(0) == s && VarType.get(i).indexOf(s) >= 0) {
                          return null;
                        }
                }

                VarType.add(new ArrayList<String>(Arrays.asList(s,n.f1.f0.toString())));
                }
                else VarType.add(new ArrayList<String>(Arrays.asList(s,n.f1.f0.toString())));
        }
        n.f0.accept(this,n.f1.f0.toString());
        return null;
}
public Object visit(FormalParameterList n, String s) {
        n.f0.accept(this,s);
        n.f1.accept(this,s);
        return null;
}
public Object visit(FormalParameterRest n, String s) {
        n.f1.accept(this,s);
        return null;
}
public Object visit(FormalParameter n, String s) {
        n.f0.accept(this,n.f1.f0.toString());
        return null;
}
public Object visit(Type n, String s){
        n.f0.accept(this,s);
        return null;
}
public Object visit(ArrayType n, String s){
        if(s != null) {
          if(VarType.size() > 0) {
                for(int i = 0; i < VarType.size(); i++) {
                        if(VarType.get(i).get(0) == s && VarType.get(i).indexOf(s) < 0) {
                                VarType.get(i).add("intarray");
                                return null;
                        }
                        if(VarType.get(i).get(0) == s && VarType.get(i).indexOf(s) >= 0) {
                                return null;
                        }
                }
                VarType.add(new ArrayList<String>(Arrays.asList(s,"intarray")));

          }else VarType.add(new ArrayList<String>(Arrays.asList(s,"intarray")));
        }
        return null;
}
public Object visit(BooleanType n, String s){
        if(s != null) {
                if(VarType.size()>0) {
                        for(int i = 0; i < VarType.size(); i++) {
                                if(VarType.get(i).get(0) == s && VarType.get(i).indexOf(s) < 0) {
                                        VarType.get(i).add("boolean");
                                        return null;
                                }
                                if(VarType.get(i).get(0) == s && VarType.get(i).indexOf(s) >= 0) {
                                        return null;
                                }
                        }
                        VarType.add(new ArrayList<String>(Arrays.asList(s,"boolean")));

                }else VarType.add(new ArrayList<String>(Arrays.asList(s,"boolean")));
        }
        return null;
}
public Object visit(IntegerType n, String s){
        if(s != null) {
                if(VarType.size() > 0) {
                        for(int i = 0; i < VarType.size(); i++) {
                                if(VarType.get(i).get(0) == s && VarType.get(i).indexOf(s) < 0) {
                                        VarType.get(i).add("int");
                                        return null;
                                }
                                if(VarType.get(i).get(0) == s && VarType.get(i).indexOf(s) >= 0) {
                                        return null;
                                }
                        }

                        VarType.add(new ArrayList<String>(Arrays.asList(s,"int")));

                }else VarType.add(new ArrayList<String>(Arrays.asList(s,"int")));
        }
        return null;
}
public Object visit(Identifier n, String s){
        if(s == "MethodType") {
                MethodType.add(new ArrayList<String>(Arrays.asList(MethodName,n.f0.toString())));
        }
        if(s != null && s != "MethodType") {
                if(VarType.size() > 0) {
                        for(int i = 0; i < VarType.size(); i++) {
                                if(VarType.get(i).get(0) == s && VarType.get(i).indexOf(s) < 0) {
                                        VarType.get(i).add(n.f0.toString());
                                        return null;
                                }
                                if(VarType.get(i).get(0) == s && VarType.get(i).indexOf(s) >= 0) {
                                        return null;
                                }

                        }
                        VarType.add(new ArrayList<String>(Arrays.asList(s,n.f0.toString())));

                }else{
                        VarType.add(new ArrayList<String>(Arrays.asList(s,n.f0.toString())));
                }
        }
        return null;
}
public Object visit(MainClass n, String s) {
        n.f15.accept(this, n.f1.f0.toString() + "_" + "main");
        n.f14.accept(this, null);
        return null;
}
//MessageSend
public Object visit(MessageSend n, String s){
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
public Object visit(Expression n, String s) {
        n.f0.accept(this, s);
        return null;
}
//AssignmentStatement
public Object visit(AssignmentStatement n, String s) {
        n.f2.accept(this, n.f0.f0.toString());
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
//WhileStatement
public Object visit(WhileStatement n, String s) {
        n.f2.accept(this, null);
        n.f4.accept(this, null);
        return null;
}
public Object visit(PrimaryExpression n, String s) {
        n.f0.accept(this, s);
        return null;
}
//AndExpression
public Object visit(AndExpression n, String s){
        n.f0.accept(this, s);
        n.f2.accept(this, s);
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
//AllocationExpression
public Object visit(AllocationExpression n, String s){
        if(s != null) {
                if(VarType.size() > 0) {
                        for(int i = 0; i < VarType.size(); i++) {
                                if(VarType.get(i).get(0) == s && VarType.get(i).indexOf(n.f1.f0.toString()) < 0) {
                                        VarType.get(i).add(n.f1.f0.toString());
                                        return null;
                                }
                                if(VarType.get(i).get(0) == s && VarType.get(i).indexOf(s) >= 0) {
                                        return null;
                                }
                        }
                        VarType.add(new ArrayList<String>(Arrays.asList(s,n.f1.f0.toString())));

                }else VarType.add(new ArrayList<String>(Arrays.asList(s,n.f1.f0.toString())));
        }
        return null;
}

}

class VisitorComputeSet extends GJDepthFirst<Object,String>{
        // put class hierachy here
        ArrayList<ArrayList<String> > ClassHierachy;
        // put variable type set here
        ArrayList<ArrayList<String> > VarType;
        // method calling tuple
        String callee; String caller;


        ArrayList<String> MethodChain;
        ArrayList<String> OuputChain = new ArrayList<String>();
        ArrayList<ArrayList<String> > MethodType;
        Stack<String> InComing = new Stack<String>();
        String inter = "";
        String nextClass = "";
public Object visit(MethodDeclaration n, String s) {
        n.f8.accept(this, s + "_" + n.f2.f0.toString());
        n.f10.accept(this,s + "_" + n.f2.f0.toString());
        return null;
}
public Object visit(ClassDeclaration n, String s) {
        n.f4.accept(this, n.f1.f0.toString());
        return null;
}
public Object visit(ClassExtendsDeclaration n, String s) {
        n.f6.accept(this, n.f1.f0.toString());
        return null;
}
public Object visit(MainClass n, String s) {
        n.f15.accept(this, n.f1.f0.toString() + "_" + "main");
        return null;
}

//MessageSend
public Object visit(MessageSend n, String s){
        if(s != "InBracket") {
                callee = n.f2.f0.toString();
                caller = s;
                inter = s;
                n.f0.accept(this,"InBracket");
                n.f4.accept(this, s);
        } if(s == "InBracket" && inter != "") {
                callee = n.f2.f0.toString();
                caller = inter;
                n.f0.accept(this,"InBracket");
                n.f4.accept(this, s);
        }
        return null;
}
public String ComputeClassName(String input){
        for (int i = 0; i < input.length(); i++) {
                if(input.substring(i, i + 1).equals("_")) {
                        String L = input.substring(0, i).trim();
                        String N = input.substring(i + 1,input.length()).trim();
                        return L;
                }
        }
        return null;
}

public Object visit(ThisExpression n, String s) {
        if(s == "InBracket") {
                String holder = this.ComputeClassName(caller) + "_" + callee;
                for(int k = 0; k < MethodChain.size(); k++) {
                        if(MethodChain.indexOf(holder) >= 0) {
                                if(this.OuputChain.indexOf(caller + " -> " + holder)<0) {
                                        this.OuputChain.add(caller + " -> "+holder);
                                }
                                while(!InComing.empty()) {
                                        for(int l = 0; l < MethodType.size(); l++) {
                                                if(MethodType.get(l).get(0).equals(holder)) {
                                                        nextClass = MethodType.get(l).get(1);
                                                        break;
                                                }
                                        }
                                        String MethodName = InComing.pop();
                                        for(int y = 0; y < ClassHierachy.size(); y++) {
                                                if(ClassHierachy.get(y).get(0).equals(nextClass)) {
                                                        for(int x = 0; x < ClassHierachy.get(y).size(); x++) {
                                                           this.OuputChain.add(caller + " -> " + ClassHierachy.get(y).get(x) + "_" + MethodName);
                                                        }
                                                        holder = ClassHierachy.get(y).get(0) + "_" + MethodName;
                                                        break;
                                                }
                                        }
                                }
                                break;
                        }
                        if(MethodChain.indexOf(holder)<0) {
                                for(int k1 = 0; k1 < ClassHierachy.size(); k1++) {
                                        String pointer = this.ComputeClassName(caller);
                                        if(ClassHierachy.get(k1).indexOf(pointer) > 0) {
                                                String holder_new = ClassHierachy.get(k1).get(0) + "_" + callee;
                                                if(MethodChain.indexOf(holder_new) >= 0) {
                                                        if(this.OuputChain.indexOf(caller + " -> " + holder_new) < 0) {
                                                                this.OuputChain.add(caller + " -> " + holder_new);
                                                        }
                                                        while(!InComing.empty()) {
                                                                for(int i = 0; i < MethodType.size(); i++) {
                                                                        if(MethodType.get(i).get(0).equals(holder_new)) {
                                                                                nextClass = MethodType.get(i).get(1);
                                                                                break;
                                                                        }
                                                                }
                                                                String MethodName = InComing.pop();
                                                                for(int i = 0; i < ClassHierachy.size(); i++) {
                                                                        if(ClassHierachy.get(i).get(0).equals(nextClass)) {
                                                                                for(int j = 0; j < ClassHierachy.get(i).size(); j++) {
                                                                                        this.OuputChain.add(caller+" -> " + ClassHierachy.get(i).get(j) + "_" + MethodName);
                                                                                }
                                                                                holder_new = ClassHierachy.get(i).get(0) + "_" + MethodName;
                                                                                break;
                                                                        }
                                                                }
                                                        }
                                                        break;
                                                }

                                        }
                                }
                                break;
                        }
                }
                for(int i = 0; i < ClassHierachy.size(); i++) {
                        String pointer = this.ComputeClassName(caller);
                        if(ClassHierachy.get(i).indexOf(pointer) >= 0) {
                                for(int j = 1; j < ClassHierachy.get(i).size(); j++) {
                                        String holder_new = ClassHierachy.get(i).get(j) + "_" + callee;
                                        if(MethodChain.indexOf(holder_new) >= 0) {
                                                if(this.OuputChain.indexOf(caller + " -> " + holder_new)<0) {
                                                  this.OuputChain.add(caller + " -> " + holder_new);
                                                }
                                                while(!InComing.empty()) {
                                                        for(int l = 0; l < MethodType.size(); l++) {
                                                                if(MethodType.get(l).get(0).equals(holder)) {
                                                                  nextClass = MethodType.get(l).get(1);
                                                                  break;
                                                                }
                                                        }
                                                        String MethodName = InComing.pop();
                                                        for(int l = 0; l < ClassHierachy.size(); l++) {
                                                                if(ClassHierachy.get(l).get(0).equals(nextClass)) {
                                                                  for(int m = 0; m < ClassHierachy.get(l).size(); m++) {
                                                                    this.OuputChain.add(caller + " -> " + ClassHierachy.get(l).get(m) + "_" + MethodName);
                                                                  }
                                                                  holder_new = ClassHierachy.get(l).get(0) + "_" + MethodName;
                                                                  break;
                                                                }
                                                        }
                                                }
                                                break;
                                        }
                                }
                        }
                }

        }

        // set the caller-callee tuple to null
        callee=null;
        caller=null;
        return null;
}
public Object visit(Identifier n, String s) {
        if(s=="InBracket") {
                for(int i = 0; i < VarType.size(); i++) {
                        if(VarType.get(i).get(0) == n.f0.toString()) {
                                for(int j = 1; j < VarType.get(i).size(); j++) {
                                        String holder = VarType.get(i).get(j) + "_" + callee;
                                        for(int k = 0; k < MethodChain.size(); k++) {
                                                if(MethodChain.indexOf(holder)>=0) {
                                                        if(this.OuputChain.indexOf(caller + " -> " + holder) <0) {
                                                                this.OuputChain.add(caller + " -> " + holder);
                                                        }

                                                        while(!InComing.empty()) {
                                                                for(int l = 0; l < MethodType.size(); l++) {
                                                                        if(MethodType.get(l).get(0).equals(holder)) {
                                                                                nextClass=MethodType.get(l).get(1);
                                                                                break;
                                                                        }
                                                                }
                                                                String MethodName = InComing.pop();
                                                                for(int l = 0; l < ClassHierachy.size(); l++) {
                                                                        if(ClassHierachy.get(l).get(0).equals(nextClass)) {
                                                                                for(int m = 0; m < ClassHierachy.get(l).size(); m++) {
                                                                                  this.OuputChain.add(caller + " -> " + ClassHierachy.get(l).get(m) + "_" + MethodName);
                                                                                }
                                                                                holder = ClassHierachy.get(l).get(0) + "_" + MethodName;
                                                                                break;
                                                                        }
                                                                }
                                                        }
                                                        break;
                                                }
                                                if(MethodChain.indexOf(holder) < 0) {
                                                        for(int k1 = 0; k1 < ClassHierachy.size(); k1++) {
                                                                String pointer = VarType.get(i).get(j);
                                                                if(ClassHierachy.get(k1).indexOf(pointer) > 0) {
                                                                        String holder_new = ClassHierachy.get(k1).get(0) + "_" + callee;
                                                                        if(MethodChain.indexOf(holder_new) >= 0) {
                                                                                if(this.OuputChain.indexOf(caller + " -> " + holder_new) < 0) {
                                                                                        this.OuputChain.add(caller + " -> " + holder_new);
                                                                                }
                                                                                while(!InComing.empty()) {
                                                                                        for(int l = 0; l < MethodType.size(); l++) {
                                                                                                if(MethodType.get(l).get(0).equals(holder_new)) {
                                                                                                        nextClass = MethodType.get(l).get(1);
                                                                                                        break;
                                                                                                }
                                                                                        }
                                                                                        String MethodName = InComing.pop();
                                                                                        for(int l = 0; l < ClassHierachy.size(); l++) {
                                                                                                if(ClassHierachy.get(l).get(0).equals(nextClass)) {
                                                                                                        for(int m = 0; m < ClassHierachy.get(l).size(); m++) {
                                                                                                          this.OuputChain.add(caller + " -> " + ClassHierachy.get(l).get(m) + "_" + MethodName);
                                                                                                        }
                                                                                                        holder_new = ClassHierachy.get(l).get(0) + "_" + MethodName;
                                                                                                        break;
                                                                                                }
                                                                                        }
                                                                                }
                                                                                break;
                                                                        }

                                                                }
                                                        }
                                                        break;
                                                }
                                        }
                                        for(int k = 0; k < ClassHierachy.size(); k++) {
                                                String pointer = VarType.get(i).get(j);
                                                if(ClassHierachy.get(k).indexOf(pointer) >= 0) {
                                                        for(int l = 1; l < ClassHierachy.get(k).size(); l++) {
                                                                String holder_new = ClassHierachy.get(k).get(l) + "_" + callee;
                                                                if(MethodChain.indexOf(holder_new) >= 0) {
                                                                        if(this.OuputChain.indexOf(caller + " -> " + holder_new) < 0) {
                                                                                this.OuputChain.add(caller + " -> " + holder_new);
                                                                        }
                                                                        while(!InComing.empty()) {
                                                                                for(int m = 0; m < MethodType.size(); m++) {
                                                                                        if(MethodType.get(m).get(0).equals(holder_new)) {
                                                                                                nextClass = MethodType.get(m).get(1);
                                                                                                break;
                                                                                        }
                                                                                }
                                                                                String MethodName = InComing.pop();
                                                                                for(int m = 0; m < ClassHierachy.size(); m++) {
                                                                                        if(ClassHierachy.get(m).get(0).equals(nextClass)) {
                                                                                          for(int n1 = 0; n1 < ClassHierachy.get(m).size(); n1++) {
                                                                                            this.OuputChain.add(caller + " -> " + ClassHierachy.get(m).get(n1) + "_" + MethodName);
                                                                                           }
                                                                                           holder_new = ClassHierachy.get(m).get(0) + "_" + MethodName;
                                                                                           break;
                                                                                        }
                                                                                }
                                                                        }
                                                                        break;
                                                                }
                                                        }
                                                }
                                        }

                                }
                        }
                }
        }

        // set caller and callee tuple to null
        callee=null;
        caller=null;
        return null;
}
//PrintStatement
public Object visit(PrintStatement n, String s) {
        n.f2.accept(this, s);
        return null;
}
//BlockStatement
public Object visit(Block n, String s) {
        n.f1.accept(this, s);
        return null;
}
//AssignmentStatement
public Object visit(AssignmentStatement n, String s) {
        n.f2.accept(this, s);
        return null;
}
//ArraryAssignmentStatement
public Object visit(ArrayAssignmentStatement n, String s) {
        n.f2.accept(this, s);
        n.f5.accept(this, s);
        return null;
}
//IfStatement
public Object visit(IfStatement n, String s) {
        n.f2.accept(this, s);
        n.f4.accept(this, s);
        n.f6.accept(this, s);
        return null;
}
//WhileStatement
public Object visit(WhileStatement n, String s) {
        n.f2.accept(this, s);
        n.f4.accept(this, s);
        return null;
}
//AndExpression
public Object visit(AndExpression n, String s){
        n.f0.accept(this, s);
        n.f2.accept(this, s);
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
//AllocationExpression
public Object visit(AllocationExpression n, String s){
        if(s == "InBracket") {
                String holder = n.f1.f0.toString() + "_" + callee;
                for(int i = 0; i < MethodChain.size(); i++) {
                        if(MethodChain.indexOf(holder) >= 0) {
                                if(this.OuputChain.indexOf(caller + " -> " + holder) < 0) {
                                        this.OuputChain.add(caller + " -> " + holder);
                                }
                                while(!InComing.empty()) {
                                        for(int j = 0; j < MethodType.size(); j++) {
                                                if(MethodType.get(j).get(0).equals(holder)) {
                                                        nextClass = MethodType.get(j).get(1);
                                                        break;
                                                }
                                        }
                                        String MethodName = InComing.pop();
                                        for(int j = 0; j < ClassHierachy.size(); j++) {
                                                if(ClassHierachy.get(j).get(0).equals(nextClass)) {
                                                        for(int l = 0; l < ClassHierachy.get(j).size(); l++) {
                                                                this.OuputChain.add(caller + " -> " + ClassHierachy.get(j).get(l) + "_" + MethodName);
                                                        }
                                                        holder = ClassHierachy.get(j).get(0) + "_" + MethodName;
                                                        break;
                                                }
                                        }
                                }
                                break;
                        }
                }
                for(int i = 0; i < ClassHierachy.size(); i++) {
                        String pointer = n.f1.f0.toString();
                        if(ClassHierachy.get(i).indexOf(pointer) >= 0) {
                                for(int j = 1; j < ClassHierachy.get(i).size(); j++) {
                                        String holder_new = ClassHierachy.get(i).get(j) + "_" + callee;
                                        if(MethodChain.indexOf(holder_new) >= 0) {
                                                if(this.OuputChain.indexOf(caller + " -> " + holder_new) < 0) {
                                                        this.OuputChain.add(caller + " -> " + holder_new);
                                                }
                                                while(!InComing.empty()) {;
                                                        for(int l = 0; l < MethodType.size(); l++) {
                                                                if(MethodType.get(l).get(0).equals(holder_new)) {
                                                                        nextClass = MethodType.get(l).get(1);
                                                                        break;
                                                                }
                                                        }
                                                        String MethodName = InComing.pop();
                                                        for(int l = 0; l < ClassHierachy.size(); l++) {
                                                                if(ClassHierachy.get(l).get(0).equals(nextClass)) {
                                                                        for(int m = 0; m < ClassHierachy.get(l).size(); m++) {
                                                                                this.OuputChain.add(caller + " -> " + ClassHierachy.get(l).get(m) + "_" + MethodName);
                                                                        }
                                                                        holder_new = ClassHierachy.get(l).get(0) + "_" + MethodName;
                                                                        break;
                                                                }
                                                        }
                                                }
                                                break;
                                        }
                                }
                        }
                }
        }

        // setting to null
        callee=null;
        caller=null;
        return null;
}
//ArrayAllocationExpression
public Object visit(ArrayAllocationExpression n, String s){
        n.f3.accept(this, s);
        return null;
}
//BracketExpression
public Object visit(BracketExpression n, String s){
        if(s == "InBracket") {
                InComing.push(callee);
        }
        n.f1.accept(this, "InBracket");
        return null;
}
//NotExpression
public Object visit(NotExpression n, String s){
        n.f1.accept(this, s);
        return null;
}

}
