package sweetmagic.util;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

public class SMDebug {

	private static final Logger LOGGER = LogUtils.getLogger();

	public static void info(Object obj) {

		if (obj instanceof String text) {
			LOGGER.info("★★★" + text);
		}

		else {
			LOGGER.info("★★★" + obj);
		}
	}

	public static void info() {
		LOGGER.info("★★★★★★★★★");
	}
}
