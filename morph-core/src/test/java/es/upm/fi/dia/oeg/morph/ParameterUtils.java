package es.upm.fi.dia.oeg.morph;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ParameterUtils
{
	public static Properties load(InputStream fis) throws IOException
	{
        Properties props = new Properties();
        props.load(fis);    
        fis.close();
        return props;
	}
}
