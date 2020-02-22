class DotGraphExample {
    public static void main(String[] a){
        System.out.println(new Operator().compute());
    }
}

class Operator{
    int op1int;
    int op2int;
    int op3int;
    int result;

    public int compute(){
        op1int = 10;
        op2int = 20;
        op3int = 3;
        result = this.mult(this.add(op1int, op2int), op3int);

        return result;
    }

    public int add(int a, int b){
        return a + b;
    }

    public int mult(int a, int b){
        return a * b;
    }
}
