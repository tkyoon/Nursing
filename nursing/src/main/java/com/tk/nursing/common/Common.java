package com.tk.nursing.common;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Common {
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	public static Properties properties = null;

	public static void loadXmlProperties() {
		try {
			properties = new Properties();
			FileInputStream fis = new FileInputStream("./conf/properties.xml");
			properties.loadFromXML(fis);
			// properties.list(System.out);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static int getRandomInt(int size){
		Random random = new Random();
		int ret = random.nextInt(size);
		return ret;
	}
}
