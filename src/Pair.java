
public class Pair
{
	int k;
	String u;
	String v;
	
	Pair(int k, String u, String v)
	{
		this.k = k;
		this.u = u;
		this.v = v;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + k;
		result = prime * result + ((u == null) ? 0 : u.hashCode());
		result = prime * result + ((v == null) ? 0 : v.hashCode());
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
		Pair other = (Pair) obj;
		if (k != other.k)
			return false;
		if (u == null)
		{
			if (other.u != null)
				return false;
		}
		else if (!u.equals(other.u))
			return false;
		if (v == null)
		{
			if (other.v != null)
				return false;
		}
		else if (!v.equals(other.v))
			return false;
		return true;
	}
	
	
}
