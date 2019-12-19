package com.github.gumtreediff.gen.srcml;

import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.ITree;

import java.io.File;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GumTreeCComparer {

    private static Logger log = LoggerFactory.getLogger(GumTreeCComparer.class);

    public List<Action> compareCFilesWithGumTree(File prevFile, File revFile) {
        // Generate GumTree.
        ITree oldTree = null;
        ITree newTree = null;
        try {
//			oldTree = new GumTreeGenerator().generateITreeForCFileForCode(prevFile);
//			newTree = new GumTreeGenerator().generateITreeForCFileForCode(revFile);
			oldTree = new SrcmlCTreeGenerator().generateFromFile(prevFile).getRoot();
			newTree = new SrcmlCTreeGenerator().generateFromFile(revFile).getRoot();
        } catch (Exception e) {
            if (oldTree == null) {
                log.info("Null GumTree of Previous File: " + prevFile.getPath());
            } else if (newTree == null) {
                log.info("Null GumTree of Revised File: " + revFile.getPath());
            }
        }
        if (oldTree != null && newTree != null) {
            Matcher m = Matchers.getInstance().getMatcher(oldTree, newTree);
            m.match();
            ActionGenerator ag = new ActionGenerator(oldTree, newTree, m.getMappings());
            ag.generate();
            List<Action> actions = ag.getActions(); // change actions from bug to patch

            return actions;
        }

        return null;
    }
}
