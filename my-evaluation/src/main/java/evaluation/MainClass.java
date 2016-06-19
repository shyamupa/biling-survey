package evaluation;

import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.core.utilities.commands.CommandDescription;
import edu.illinois.cs.cogcomp.core.utilities.commands.InteractiveShell;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class MainClass {


    @CommandDescription(description ="String lang (sv),String en_vectors,String fr_vectors")
    public static void evaluateBiDict(String lang, String en_vectors,String fr_vectors) throws IOException {
//		String en_vectors = MYDIR+"bivec_vectors/uniq.en-"+lang+".bivec.en.trim";
//		String fr_vectors = MYDIR+"bivec_vectors/uniq.en-"+lang+".bivec."+lang+".trim";
		// String en_vectors = MYDIR+"bicvm_vectors/bicvm.en-"+lang+".en.200";
		// String fr_vectors = MYDIR+"bicvm_vectors/bicvm.en-"+lang+"."+lang+".200";vulic.en-sv.train.w60.en
		// String en_vectors = MYDIR+"vulic_vectors/vulic.en-"+lang+".train.w60.en";
		// String fr_vectors = MYDIR+"vulic_vectors/vulic.en-"+lang+".train.w60.fr";
		// String en_vectors = MYDIR+"cca_vectors/uniq.en-"+lang+".0.5.min0_orig1_projected.txt";
		// String fr_vectors = MYDIR+"cca_vectors/uniq.en-"+lang+".0.5.min0_orig2_projected.txt";

		String dict = "/home/upadhya3/bi-embedding/en."+lang+".dict" ; //"/home/upadhya3/wikt2dict/en.fr.dict";
		List<String> lines = LineIO.read(dict, "utf8");
//		Map<String,String> gold = new HashMap<>();
		MultiMap gold =new MultiValueMap();
		for (String line:lines)
		{
			String[] parts = line.split("\\t");
//			System.out.println(parts[0]+" "+parts[1]);
			String[] enparts=parts[0].split("\\s+");
			String[] frparts=parts[1].split("\\s+");
			for (String en:enparts)
			{
//				if (gold.containsKey(en))
//				{
//					System.out.println("duplicate "+en);
//				}
				for (String fr:frparts)
				{
					gold.put(en,fr);
				}
			}
//			gold.put(parts[0], parts[1]);
		}
		Set<String> enside = gold.keySet();
//		for(String k:enside)
//		{
//			System.out.println(k+" "+gold.get(k));
//		}
		System.out.println(enside.size());

		CreateBLDict dd = new CreateBLDict(en_vectors,fr_vectors, enside, gold);
    }

	public static void main(String args[]) throws Exception {
		InteractiveShell<MainClass> tester = new InteractiveShell<MainClass>(
				MainClass.class);
		if (args.length == 0)
			tester.showDocumentation();
		else {
			tester.runCommand(args);
		}
	}

}
