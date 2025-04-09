package sweetmagic.util;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

public class SMDebug {

	private static final Logger LOGGER = LogUtils.getLogger();

	public static void info(Object... objArray) {

		String tip = "";

		for (int i = 0; i < objArray.length; i++) {
			Object obj = objArray[i];
			tip += obj instanceof String text ? text : obj;
			if (i != objArray.length - 1) { tip += " / "; }
		}

		LOGGER.info("★★★" + tip);
	}

	public static void info() {
		LOGGER.info("★★★★★★★★★");
	}
}
