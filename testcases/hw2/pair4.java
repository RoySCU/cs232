/*
	What makes this Input Output pair testcase nifty is the fact that the program will 
	run into a null pointer error since method testIO of class X calls method nullarg of 
	class Y with an uninitialized argument of type X.
*/
class IOProject2 {
    public static void main(String[] a){
        System.out.println(new X().testIO(10));
    }
}

class X {
	X x;
    public int testIO(int a){
        return new Y().nullarg(x, a);
    }

    public int op(int a, int b)
    {
    	return a + b;
    }
}

class Y {
	int c;
	public int nullarg(X x, int num){
		c = 5;
		return x.op(c, num);
	}
}