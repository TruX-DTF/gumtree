package gumtree.example;

import com.github.gumtreediff.gen.jdt.AbstractJdtVisitor;
import com.github.gumtreediff.gen.jdt.JdtVisitor;

public class BlockJdtTreeGenerator extends AbstractBlockJdtTreeGenerator {

    @Override
    protected AbstractJdtVisitor createVisitor() {
        return new JdtVisitor();
    }

}