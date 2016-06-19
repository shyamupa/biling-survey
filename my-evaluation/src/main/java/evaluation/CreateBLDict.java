package evaluation;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.io.LineIO;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

import java.io.*;
import java.util.*;

public class CreateBLDict {

	private List<String> fr_vocab;
	private List<String> eng_vocab;
	private MultiMap gold;
//	private Map<String,String> gold;
//	private Map<String,List<Float>> enemb;
//	private Map<String,List<Float>> fremb;

	public CreateBLDict(String enbinfile, String frbinfile, Set<String> enside, MultiMap enfr)//Map<String,String> enfr)
			throws IOException {
		Map<String,float[]> enemb = loadEmbeddings(enbinfile);
//		eng_vocab = new ArrayList<>(enemb.keySet());
		eng_vocab = new ArrayList<>(enside);
		gold = enfr;
		Map<String,float[]> fremb = loadEmbeddings(frbinfile);
		fr_vocab = new ArrayList<>(fremb.keySet());
		System.out.println(eng_vocab.size()+" "+fr_vocab.size());
		computeDistMatrices(enemb, fremb);
	}

	public void computeDistMatrices(Map<String, float[]> enemb, Map<String, float[]> fremb) {
		int correct=0;
		float MRR = 0.0f;
		for (int i = 0; i < eng_vocab.size(); i++) {
//			System.out.println("word: "+eng_vocab.get(i));
//			String target=gold.get(eng_vocab.get(i));
			List<String> targets = (List<String>) gold.get(eng_vocab.get(i));
			List<Pair<String, Float>> topk = en_getTopK(eng_vocab.get(i), enemb, fremb);

			boolean found = false;
			int rank=0;
			for(int a=0;a<topk.size();a++) {
				for (String target : targets)
				{
					if (target.equals(topk.get(a).getFirst())) {
//						System.out.println("YAY!");
//						correct++;
						found = true;
						rank = a+1;
						break;
					}
				}
				if(found) {
					correct++;
					MRR += 1.0f/rank;
					break;
				}
//				System.out.println(topk.get(a).getFirst() + " " + topk.get(a).getSecond());
			}
//			System.out.println("--------------");
		}
		System.out.println(correct+"/"+eng_vocab.size() + " % age " + correct*1.0/eng_vocab.size());
		System.out.println(MRR+"/"+eng_vocab.size() + " MRR " + MRR*1.0/eng_vocab.size());
//		for (int i = 0; i < fr_vocab.size(); i++) {
//			System.out.println("word: "+eng_vocab.get(i));
//			List<Pair<String, Float>> topk = fr_getTopK(fr_vocab.get(i),enemb,fremb);
//			for(int a=0;a<topk.size();a++) System.out.println(topk.get(a).getFirst()+" "+topk.get(a).getSecond());
//			System.out.println("--------------");
//		}
		System.out.println("dist matrix done!");
	}

	public static Map<String,float[]> loadEmbeddings(String embedFile) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(embedFile));
		Map<String,float[]> wordEmbeddings = new HashMap<>();

		String line;
		while ((line = in.readLine()) != null) {
			line = line.trim();
			if (line.equals("")) {
				continue;
			}

			String fields[] = line.split("\\s+");

			String word = fields[0];
			float[] features = new float[fields.length - 1];
			float len=0.0f;
			for (int idx = 1; idx < fields.length; idx++) {
				features[idx - 1] = Float.parseFloat(fields[idx]);
				len += features[idx - 1]*features[idx - 1];
			}
			float N = (float) Math.sqrt(len);
			for (int idx = 1; idx < fields.length; idx++) {
				features[idx - 1] = features[idx - 1] / N;
			}
			wordEmbeddings.put(word, features);

		}

		in.close();

		// check embeddings
		if (wordEmbeddings.size() == 0) {
			throw new IOException("Embeddings are empty!");
		}

		int size = -1;

		for (String word : wordEmbeddings.keySet()) {
			if (size < 0) {
				size = wordEmbeddings.get(word).length;
			}
			if (size != wordEmbeddings.get(word).length) {
				throw new IOException("Different words have different embedding dimensionality for word: "+word +" in file: "+embedFile);
			}
		}
		return wordEmbeddings;

	}

//	public CreateBLDict(String eng_vocab, String fr_vocab, String binfile)
//			throws FileNotFoundException, IOException {
//	    this.eng_vocab = new ArrayList<>(LineIO.read(eng_vocab));
//	    this.fr_vocab = new ArrayList<>(LineIO.read(fr_vocab));
//	    this.vectors = new Vectors(new FileInputStream(new File(binfile)));
//	    System.out.println("done!");
//	}

//	public CreateBLDict(List<String> eng_vocab, List<String> fr_vocab,
//			String binfile) throws FileNotFoundException, IOException {
//		this.eng_vocab = eng_vocab;
//		this.fr_vocab = fr_vocab;
//		this.vectors = new Vectors(new FileInputStream(new File(binfile)));
//		System.out.println("done!");
//	}



	private List<Pair<String, Float>> fr_getTopK(String word, Map<String, float[]> enemb, Map<String, float[]> fremb ) {
		String[] bestw= new String[10];
		float[] bestd= new float[10];
		for(int a=0;a<bestd.length;a++) bestd[a]=0;
		for (int j = 0; j < eng_vocab.size(); j++) {
			float dist = ComputeDistance(eng_vocab.get(j),word, enemb,fremb);
			for(int a=0;a<bestd.length;a++)
			{
				if(dist>bestd[a])
				{
					for(int d=bestd.length-1;d > a;d--) {
						bestd[d] = bestd[d - 1];
						bestw[d] = bestw[d - 1];
					}
					bestd[a]=dist;
					bestw[a]=eng_vocab.get(j);
					break;
				}
			}
		}
		List<Pair<String,Float>> ans = new ArrayList<>();
		for(int a=0;a<bestd.length;a++)
			ans.add(new Pair<String, Float>(bestw[a],bestd[a]));
		return ans;
	}

	private List<Pair<String, Float>> en_getTopK(String word, Map<String, float[]> enemb, Map<String, float[]> fremb ) {
		String[] bestw= new String[10];
		float[] bestd= new float[10];
		for(int a=0;a<bestd.length;a++) bestd[a]=0;
		for (int j = 0; j < fr_vocab.size(); j++) {
			float dist = ComputeDistance(word,fr_vocab.get(j), enemb,fremb);
			for(int a=0;a<bestd.length;a++)
			{
				if(dist>bestd[a])
				{
					for(int d=bestd.length-1;d > a;d--) {
						bestd[d] = bestd[d - 1];
						bestw[d] = bestw[d - 1];
					}
					bestd[a]=dist;
					bestw[a]=fr_vocab.get(j);
					break;
				}
			}
		}
		List<Pair<String,Float>> ans = new ArrayList<>();
		for(int a=0;a<bestd.length;a++)
			ans.add(new Pair<String, Float>(bestw[a],bestd[a]));
		return ans;
	}

//	public MultiMap getBestPairs(float[][] dist_matrix) {
////		List<Pair<String, String>> ans = new ArrayList<>();
//		MultiMap ans = new MultiHashMap();
//		for (int i = 0; i < dist_matrix.length; i++) {
//			for (int j = 0; j < dist_matrix[0].length; j++) {
//				//
//				if (dist_matrix[i][j] > DELTA) {
//					if (!fr_vocab.contains(eng_vocab.get(i))
//							&& !eng_vocab.contains(fr_vocab.get(j)))
//						System.out.println(eng_vocab.get(i) + " "
//								+ fr_vocab.get(j) + " " + dist_matrix[i][j]);
////					ans.add(new Pair<String,String>(fr_vocab.get(j), eng_vocab.get(i)));
//					ans.put(fr_vocab.get(j), eng_vocab.get(i));
//				}
//			}
//		}
//		return ans;
//	}

	public static float ComputeDistance(String word1, String word2, Map<String,float[]> enemb, Map<String,float[]> fremb) {
		float[] w1 = enemb.get(word1);
		float[] w2 = fremb.get(word2);
		if (w1==null || w2 == null)
			return 0.0f;
		float dist = 0.0f;
//		System.out.println(word2+" "+word1);
		for (int i = 0; i < w2.length; i++) {
			dist += w1[i] * w2[i];
		}
		return dist;
	}
	static String MYDIR="/shared/bronte/upadhya3/europarl/mfaruqui/";
	public static void main(String[] args) throws IOException {
		String lang = args[0];
//		String en_vectors = MYDIR+"bivec_vectors/uniq.en-"+lang+".bivec.en.trim";
//		String fr_vectors = MYDIR+"bivec_vectors/uniq.en-"+lang+".bivec."+lang+".trim";
		// String en_vectors = MYDIR+"bicvm_vectors/bicvm.en-"+lang+".en.200";
		// String fr_vectors = MYDIR+"bicvm_vectors/bicvm.en-"+lang+"."+lang+".200";vulic.en-sv.train.w60.en
		String en_vectors = MYDIR+"vulic_vectors/vulic.en-"+lang+".train.w60.en";
		String fr_vectors = MYDIR+"vulic_vectors/vulic.en-"+lang+".train.w60.fr";
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
}
