package dev.chkb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public class Cfg {
	public String[] m = {"gg", "", "", ""};

	static final Gson G = new GsonBuilder().setPrettyPrinting().create();
	static final Path P = FabricLoader.getInstance().getConfigDir().resolve("chatkeybinds.json");
	public static Cfg c = new Cfg();

	public static void ld() {
		try {
			if (Files.exists(P)) {
				Cfg n = G.fromJson(Files.readString(P), Cfg.class);
				if (n != null) c = n;
			}
		} catch (Exception e) {
			c = new Cfg();
		}
		if (c.m == null || c.m.length != 4) c.m = new String[]{"gg", "", "", ""};
		for (int i = 0; i < 4; i++) if (c.m[i] == null) c.m[i] = "";
	}

	public static void sv() {
		try {
			Files.writeString(P, G.toJson(c));
		} catch (Exception e) {
		}
	}
}
