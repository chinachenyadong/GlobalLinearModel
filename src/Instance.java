import java.util.*;

public class Instance implements java.io.Serializable
{
	String word;
	String tag;
	HashMap<String, Double> featureMap;
	
	Instance(String word, String tag, HashMap<String, Double> featureMap)
	{
		this.word = word;
		this.tag = tag;
		this.featureMap = featureMap;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((featureMap == null) ? 0 : featureMap.hashCode());
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Instance other = (Instance) obj;
		if (featureMap == null)
		{
			if (other.featureMap != null)
				return false;
		}
		else if (!featureMap.equals(other.featureMap))
			return false;
		if (tag == null)
		{
			if (other.tag != null)
				return false;
		}
		else if (!tag.equals(other.tag))
			return false;
		if (word == null)
		{
			if (other.word != null)
				return false;
		}
		else if (!word.equals(other.word))
			return false;
		return true;
	}
	public String getWord()
	{
		return word;
	}
	public void setWord(String word)
	{
		this.word = word;
	}
	public String getTag()
	{
		return tag;
	}
	public void setTag(String tag)
	{
		this.tag = tag;
	}
	public HashMap<String, Double> getFeatureMap()
	{
		return featureMap;
	}
	public void setFeatureMap(HashMap<String, Double> featureMap)
	{
		this.featureMap = featureMap;
	}
	
	
}
