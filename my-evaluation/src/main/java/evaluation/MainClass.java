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

	@CommandDescription(description = "parallelFile(uniq.en-es) alignFile(tr.*.intersect) outfile(*.dict) minCount(0-5) limit(-1)")
	public static void WriteDictForCCA(String parallelFile,String alignFile, String outfile, String minCount, String limit) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(parallelFile), "UTF8"));
		BufferedReader align = new BufferedReader(new FileReader(new File(alignFile)));
		int limitc = Integer.parseInt(limit);
		int minc=Integer.parseInt(minCount);
		Counter<Pair<String,String>> cc= new Counter<>();
		int c=0;
		while(true) {
			String line = br.readLine();
			if(line==null)
				break;
			String tmp = align.readLine();
			if(tmp==null)
				break;

			String[] as = tmp.split("\\s+");
//			System.out.println(line);
//			System.out.println(tmp);
			String[] tmp1 = line.split("\\t");
			String enstr= tmp1[0].trim();
			String frstr = tmp1[1].trim();
			String[] entoks = enstr.split("\\s+");
			String[] frtoks = frstr.split("\\s+");
//			System.out.println(Arrays.asList(entoks));
//			System.out.println(Arrays.asList(frtoks));
			c++;
			if(c%100000==0)
			{
				System.out.println("c="+c);
			}
			if(c==limitc) // give -1
				break;
			for (String aa : as) {
				if(!aa.contains("-")) {
					System.out.println("not aligned");
					continue;
				}
				String[] pp = aa.split("-");
				int idx = Integer.parseInt(pp[0]);
				int jdx = Integer.parseInt(pp[1]);
//                System.out.println(idx + " " + jdx + " " + entoks[idx] + " " + frtoks[jdx]);
				try {
					cc.incrementCount(new Pair<String, String>(entoks[idx], frtoks[jdx]));
				}
				catch (ArrayIndexOutOfBoundsException e)
				{
					System.out.println("ERROR on line:"+c);
					System.out.println(entoks.length+" "+frtoks.length);
				}
			}
		}
		PrintWriter w = new PrintWriter(outfile);
		List<Pair<String,String>> candidates = new ArrayList<>();
		Map<String,Pair<String,Double>> eng2frCounts = new HashMap<>();
		Map<String,Pair<String,Double>> fr2engCounts = new HashMap<>();
		for(Pair<String,String>ii:cc.getSortedItemsHighestFirst())
		{
			double cint=cc.getCount(ii);
			if(cint>minc)
			{
				if (Pattern.matches("\\p{Punct}", ii.getFirst())) continue;
				if (Pattern.matches("[0-9]", ii.getFirst())) continue;
				if (Pattern.matches("\\p{Punct}", ii.getSecond())) continue;
				if (Pattern.matches("[0-9]", ii.getSecond())) continue;
				// w.println(ii.getFirst() + " ||| " + ii.getSecond());
				String eetok=ii.getFirst();
				String fftok=ii.getSecond();
				if(!eng2frCounts.containsKey(eetok))
				{
					eng2frCounts.put(eetok,new Pair<String,Double>(fftok,cint));
				}
				else
				{
					Pair<String,Double>curr=eng2frCounts.get(eetok);
					if(curr.getSecond()<cint)
					{
						eng2frCounts.put(eetok,new Pair<String,Double>(fftok,cint));
					}
				}

				if(!fr2engCounts.containsKey(fftok))
				{
					fr2engCounts.put(fftok,new Pair<String,Double>(eetok,cint));
				}
				else
				{
					Pair<String,Double>curr=fr2engCounts.get(fftok);
					if(curr.getSecond()<cint)
					{
						fr2engCounts.put(fftok,new Pair<String,Double>(eetok,cint));
					}
				}
			}
		}
		System.out.println("computed maps...");
		// intersect
		for(String etok:eng2frCounts.keySet())
		{
			for(String ftok:fr2engCounts.keySet())
			{
				if(eng2frCounts.get(etok).getFirst().equals(ftok) && fr2engCounts.get(ftok).getFirst().equals(etok))
				{
					w.println(etok+" ||| "+ftok);
				}
			}
		}
		w.close();
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
