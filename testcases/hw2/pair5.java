// Since variable a can be of type A or B, this should return null pointer error.
class Main {
    public static void main(String[] a){
    	A a;
    	a = new B();
    	a = new A();
        System.out.println(a.m());
    }
}

class A {
    A f;
    public int m(){
        return f.m();
    }
}

class B extends A{
	public int m(){
		f = this;
        return f.m();
    }
}
