package gumtree.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeContext;

public class Test {

	public static void main(String[] args) {
		String a = "if (!a){}";
		String b = "if (isTrue(a)){}";
		ArrayList<String> ret = compareTwoFilesWithGumTree(a, b);
		System.out.println(ret);
		/*
		 * INS 14 Pos:14, 
		 * INS SimpleType: File Pos:14, 
		 * INS SimpleName: home Pos:19, 
		 * INS QualifiedName: Property.PROPERTIES_FILE Pos:25, 
		 * INS SimpleName: File Pos:14, 
		 * DEL SimpleName: FileUtil Pos:10, 
		 * DEL SimpleName: newFile Pos:19, 
		 * DEL SimpleName: home Pos:27, 
		 * DEL QualifiedName: Property.PROPERTIES_FILE Pos:33, 
		 * DEL MethodInvocation Pos:10]
		 */
	}
	
	public static ArrayList<String> compareTwoFilesWithGumTree(String fileA, String fileB) {
		
		ArrayList<String> ret = new ArrayList<String>();
		
		try {
			TreeContext tc1 = new BlockJdtTreeGenerator().generateFromString(fileA);
			TreeContext tc2 = new BlockJdtTreeGenerator().generateFromString(fileB);
			
			ITree t1 = tc1.getRoot();
			ITree t2 = tc2.getRoot();
			
			Matcher m = Matchers.getInstance().getMatcher(t1, t2);
			m.match();
			
			ActionGenerator ag = new ActionGenerator(t1, t2, m.getMappings());
			ag.generate();

			List<Action> actions = ag.getActions();

			for(Action ac : actions){
				String s = rootTokenOfAction(ac.toString_Kui(tc1));
				if (!"".equals(s)) ret.add(s);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ret;
	}

	private static String rootTokenOfAction(String action) {
		int index = action.indexOf("\t");
		if (index > 0) {
			String subAction = action.substring(0, index);
			if (RegExp.endsWithPos(subAction)) {
				return subAction;
			} else {
				int j = action.indexOf("Pos:");
				if (j > 0)	 {
					String s = action.substring(j);
					return subAction.trim() + " " + s.substring(0, s.indexOf("\t"));
				} else {
					return action;
				}
				
			}
		} else {
			return action;
		}
	}

}
