import syntaxtree.*;
import visitor.GJVoidDepthFirst;

class CallGraph {
    public static void main(String[] a){
        System.out.println("DotGraphExample_main -> Operator_compute");
        System.out.println("  Operator_compute -> Operator_add");
        System.out.println("		Operator_compute -> Operator_mult");
    }
}
