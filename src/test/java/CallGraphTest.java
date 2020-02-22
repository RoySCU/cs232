import org.junit.Test;
import static org.junit.Assert.*;
import syntaxtree.*;
import visitor.*;

// for String -> Reader
import java.io.*;

public class CallGraphTest {
    @Test public void testTruth() {
        assertEquals(true, true);
    }

    @Test public void testExpressionVisitor() {
        DepthFirstVisitor dfv = new DepthFirstVisitor();
        parsecd("class C {}").accept(dfv);

        // assert something about the dfv state or return value
        assertEquals(true, true);
    }

    private ClassDeclaration parsecd(String code){
        ClassDeclaration cd = null;
        Reader r = new StringReader(code);

        try {
            cd = new MiniJavaParser(r).ClassDeclaration();
        } catch (ParseException e) {
            // TODO
        }

        return cd;
    }
}