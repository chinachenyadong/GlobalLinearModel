import java.io.*;
import java.util.*;

public class GLM
{
	static final String O = "O";
	static final String I = "I-GENE";
	static String[] states = new String[] { O, I };

	static String trainPath = "./data/gene.train";
	static String devPath = "./data/gene.dev";
	static String modelPath1 = "./data/tag.model";
	static String modelPath2 = "./data/tag.model2";

	static String outPath1 = "./data/gene.out1";
	static String outPath2 = "./data/gene.out2";

	// 特征权重
	static HashMap<String, Double> V = new HashMap<String, Double>();


	static void init_V(String path) throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" ");
			V.put(strs[0], Double.parseDouble(strs[1]));
		}
		br.close();
	}


	static HashMap<String, Double> FeatureVector(String t, String u, String s, String word) throws Exception
	{
		HashMap<String, Double> featureVector = new HashMap<String, Double>();
		String feature = "TRIGRAM:" + t + ":" + u + ":" + s;
		featureVector.put(feature, 1d);

		feature = "TAG:" + word + ":" + s;
		featureVector.put(feature, 1d);

		String suffix = word.substring(word.length() - 1);
		feature = "SUFF:" + suffix + ":" + 1 + ":" + s;
		featureVector.put(feature, 1d);

		if (word.length() >= 3)
		{
			suffix = word.substring(word.length() - 3);
			feature = "SUFF:" + suffix + ":" + 3 + ":" + s;
			featureVector.put(feature, 1d);

			suffix = word.substring(word.length() - 2);
			feature = "SUFF:" + suffix + ":" + 2 + ":" + s;
			featureVector.put(feature, 1d);
		}
		else if (word.length() == 2)
		{
			suffix = word.substring(word.length() - 2);
			feature = "SUFF:" + suffix + ":" + 2 + ":" + s;
			featureVector.put(feature, 1d);
		}

		return featureVector;
	}


	static HashSet<String> K(int k)
	{
		HashSet<String> set = new HashSet<String>();
		if (k == -1 || k == 0)
		{
			set.add("*");
		}
		else
		{
			set.add(I);
			set.add(O);
		}
		return set;
	}


	static Double FeatureVectorWeight(HashMap<String, Double> featureVector, HashMap<String, Double> v) throws Exception
	{
		double total_weight = 0d;
		for (Map.Entry<String, Double> entry : featureVector.entrySet())
		{
			String feature = entry.getKey();
			double featureCnt = entry.getValue();
			double weight = 0d;
			if (v.containsKey(feature))
			{
				weight = v.get(feature);
			}
			total_weight += featureCnt * weight;
		}
		return total_weight;
	}


	static ArrayList<String> viterbi(ArrayList<String> wordList) throws Exception
	{
		HashMap<Pair, Double> PI = new HashMap<Pair, Double>();
		HashMap<Pair, String> BP = new HashMap<Pair, String>();

		PI.clear();
		BP.clear();
		int n = wordList.size();
		String[] x = new String[n + 1];
		for (int i = 0; i < wordList.size(); ++i)
		{
			String word = wordList.get(i);
			x[i + 1] = word;
		}

		PI.put(new Pair(0, "*", "*"), 0d);

		String[] y = new String[n + 1];

		for (int k = 1; k <= n; ++k)
		{
			for (String u : K(k - 1))
			{
				for (String s : K(k))
				{
					String max_t = null;
					double max_pro = -99999999;
					for (String t : K(k - 2))
					{
						double pro = PI.get(new Pair(k - 1, t, u)) + FeatureVectorWeight(FeatureVector(t, u, s, x[k]), V);
						if (pro >= max_pro)
						{
							max_pro = pro;
							max_t = t;
						}
					}

					PI.put(new Pair(k, u, s), max_pro);
					BP.put(new Pair(k, u, s), max_t);
				}
			}
		}

		double max_pro = -99999999;
		String max_u = null;
		String max_s = null;
		for (String u : K(n - 1))
		{
			for (String s : K(n))
			{
				double pro = PI.get(new Pair(n, u, s)) + FeatureVectorWeight(FeatureVector(u, s, "STOP", " "), V);
				if (pro >= max_pro)
				{
					max_u = u;
					max_s = s;
					max_pro = pro;
				}
			}
		}

		y[n - 1] = max_u;
		y[n] = max_s;

		for (int k = n - 2; k >= 1; --k)
		{
			y[k] = BP.get(new Pair((k + 2), y[k + 1], y[k + 2]));
		}

		ArrayList<String> tagList = new ArrayList<String>();
		for (int i = 1; i <= n; ++i)
		{
			tagList.add(y[i]);
		}
		return tagList;
	}


	public static void part1(String path, String outPath) throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = null;
		ArrayList<String> wordList = new ArrayList<String>();

		FileWriter fw = new FileWriter(outPath);
		while ((line = br.readLine()) != null)
		{
			if (line.equals(""))
			{
				ArrayList<String> tagList = viterbi(wordList);
				for (int i = 0; i < tagList.size(); ++i)
				{
					String tag = tagList.get(i);
					String word = wordList.get(i);
					fw.write(word + " " + tag + "\n");
				}
				fw.write("\n");
				wordList.clear();
			}
			else
			{
				wordList.add(line);
			}
		}
		br.close();
		fw.close();
	}


	public static void fillFeature(ArrayList<Instance> instanceList) throws Exception
	{
		String feature = null;
		for (int i = 0; i < instanceList.size(); ++i)
		{
			Instance inst = instanceList.get(i);
			if (i == 0)
			{
				feature = "TRIGRAM:*:*:" + inst.getTag();
			}
			else if (i == 1)
			{
				feature = "TRIGRAM:*:" + instanceList.get(0).getTag() + ":" + inst.getTag();
			}
			else
			{
				feature = "TRIGRAM:" + instanceList.get(i - 2).getTag() + ":" + instanceList.get(i - 1).getTag() + ":" + inst.getTag();
			}
			inst.getFeatureMap().put(feature, 1d);
			feature = "TAG:" + inst.getWord() + ":" + inst.getTag();
			inst.getFeatureMap().put(feature, 1d);

			String word = inst.getWord();
			String tag = inst.getTag();
			String suffix = word.substring(word.length() - 1);
			feature = "SUFF:" + suffix + ":" + 1 + ":" + tag;
			inst.getFeatureMap().put(feature, 1d);

			if (word.length() >= 3)
			{
				suffix = word.substring(word.length() - 3);
				feature = "SUFF:" + suffix + ":" + 3 + ":" + tag;
				inst.getFeatureMap().put(feature, 1d);

				suffix = word.substring(word.length() - 2);
				feature = "SUFF:" + suffix + ":" + 2 + ":" + tag;
				inst.getFeatureMap().put(feature, 1d);
			}
			else if (word.length() == 2)
			{
				suffix = word.substring(word.length() - 2);
				feature = "SUFF:" + suffix + ":" + 2 + ":" + tag;
				inst.getFeatureMap().put(feature, 1d);
			}
		}
	}


	static ArrayList<String> getTags(ArrayList<Instance> instList) throws Exception
	{
		ArrayList<String> tagList = new ArrayList<String>();
		for (int i = 0; i < instList.size(); ++i)
		{
			tagList.add(instList.get(i).getTag());
		}
		return tagList;
	}


	static ArrayList<String> getWords(ArrayList<Instance> instList) throws Exception
	{
		ArrayList<String> wordList = new ArrayList<String>();
		for (int i = 0; i < instList.size(); ++i)
		{
			wordList.add(instList.get(i).getWord());
		}
		return wordList;
	}


	static boolean equal_Z_Y(ArrayList<String> Z, ArrayList<String> Y) throws Exception
	{
		for (int i = 0; i < Y.size(); ++i)
		{
			String y = Y.get(i);
			String z = Z.get(i);
			if (y.equals(z) == false)
			{
				return false;
			}
		}
		return true;
	}


	static void addFeature(HashMap<String, Double> featureVector, String feature, double value) throws Exception
	{
		if (featureVector.containsKey(feature) == false)
		{
			featureVector.put(feature, value);
		}
		else
		{
			featureVector.put(feature, featureVector.get(feature) + value);
		}
	}


	public static HashMap<String, Double> feature(ArrayList<Instance> instList, ArrayList<String> tagList) throws Exception
	{
		ArrayList<String> wordList = new ArrayList<String>();
		for (int i = 0; i < instList.size(); ++i)
		{
			wordList.add(instList.get(i).getWord());
		}

		HashMap<String, Double> featureMap = new HashMap<String, Double>();

		String feature = null;
		for (int i = 0; i < wordList.size(); ++i)
		{
			String word = wordList.get(i);
			String tag = tagList.get(i);

			if (i == 0)
			{
				feature = "TRIGRAM:*:*:" + tag;
			}
			else if (i == 1)
			{
				feature = "TRIGRAM:*:" + tagList.get(0) + ":" + tag;
			}
			else
			{
				feature = "TRIGRAM:" + tagList.get(i - 2) + ":" + tagList.get(i - 1) + ":" + tag;
			}
			addFeature(featureMap, feature, 1d);

			feature = "TAG:" + word + ":" + tag;
			addFeature(featureMap, feature, 1d);

			String suffix = word.substring(word.length() - 1);
			feature = "SUFF:" + suffix + ":" + 1 + ":" + tag;
			addFeature(featureMap, feature, 1d);

			if (word.length() >= 3)
			{
				suffix = word.substring(word.length() - 3);
				feature = "SUFF:" + suffix + ":" + 3 + ":" + tag;
				addFeature(featureMap, feature, 1d);

				suffix = word.substring(word.length() - 2);
				feature = "SUFF:" + suffix + ":" + 2 + ":" + tag;
				addFeature(featureMap, feature, 1d);
			}
			else if (word.length() == 2)
			{
				suffix = word.substring(word.length() - 2);
				feature = "SUFF:" + suffix + ":" + 2 + ":" + tag;
				addFeature(featureMap, feature, 1d);
			}
		}

		return featureMap;
	}


	public static void update_V(HashMap<String, Double> Y, HashMap<String, Double> Z) throws Exception
	{
		for (Map.Entry<String, Double> entry : Y.entrySet())
		{
			String key = entry.getKey();
			Double value1 = entry.getValue();
			Double value2 = Z.get(key);
			if (value2 == null)
			{
				value2 = 0.0;
			}
			// 全加入Y中的
			double value = value1 - value2;
			if (value != 0.0) // 并不加0
			{
				if (V.containsKey(key) == true)
				{
					V.put(key, V.get(key) + value);
				}
				else
				{
					V.put(key, value);
				}
			}
		}

		for (Map.Entry<String, Double> entry : Z.entrySet())
		{
			String key = entry.getKey();
			Double value1 = Y.get(key);
			Double value2 = entry.getValue();
			if (value1 == null)
			{
				//只加入Y中没有但在Z中有的
				double value = 0.0 - value2;
				if (value != 0.0)
				{
					if (V.containsKey(key) == true)
					{
						V.put(key, V.get(key) + value);
					}
					else
					{
						V.put(key, value);
					}
				}
			}
		}
	}


	public static void train(ArrayList<ArrayList<Instance>> sentList) throws Exception
	{
		// iteration
		for (int t = 1; t <= 5; ++t)
		{
			System.out.println("iteration : " + t);
			// sentence
			for (int i = 0; i < sentList.size(); ++i)
			{
				ArrayList<Instance> instList = sentList.get(i);
				ArrayList<String> Z = viterbi(getWords(instList));
				ArrayList<String> Y = getTags(instList);
				if (equal_Z_Y(Z, Y) == false)
				{
					update_V(feature(instList, Y), feature(instList, Z));
				}
			}
		}
	}


	public static void part2_train() throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(trainPath));
		String line = null;
		ArrayList<ArrayList<Instance>> sentList = new ArrayList<ArrayList<Instance>>();
		ArrayList<Instance> instanceList = new ArrayList<Instance>();
		int sentIndex = 0;
		while ((line = br.readLine()) != null)
		{
			if (line.equals(""))
			{
				System.out.println(++sentIndex);
				fillFeature(instanceList);
				sentList.add(instanceList);
				instanceList = new ArrayList<Instance>();
			}
			else
			{
				String[] strs = line.split(" ");
				instanceList.add(new Instance(strs[0], strs[1], new HashMap<String, Double>()));
			}
		}
		br.close();

		//		TrainList trainList = new TrainList(sentList);
		//		TrainList.serializeObject(trainList, "./data/TrainList");
		//		TrainList trainList = TrainList.deserializeObject("./data/TrainList");

		train(sentList);

		// 保存V
		FileWriter fw = new FileWriter(modelPath2);
		for (Map.Entry<String, Double> entry : V.entrySet())
		{
			fw.write(entry.getKey() + " " + entry.getValue() + "\n");
		}
		fw.close();
	}


	public static void main(String[] args) throws Exception
	{
		//		init_V(modelPath1, outPath1);
		//		part1(d evPath);
		part2_train();
		init_V(modelPath2);
		part1(devPath, outPath2);
		System.exit(0);
	}

}
