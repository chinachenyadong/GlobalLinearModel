import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.lang3.SerializationUtils;

public class TrainList implements java.io.Serializable
{
	ArrayList<ArrayList<Instance>> sentList;


	TrainList(ArrayList<ArrayList<Instance>> sentList)
	{
		this.sentList = sentList;
	}


	static public void serializeObject(Serializable trainList, String path)
	{
		try
		{
			OutputStream stream = new FileOutputStream(path);
			SerializationUtils.serialize(trainList, stream);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}


	public static TrainList deserializeObject(String path)
	{
		TrainList model = null;
		try
		{
			InputStream stream = new FileInputStream(path);
			model = (TrainList) SerializationUtils.deserialize(stream);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return model;
	}
}
