package sweetmagic.config;

import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
public class SMConfig {

	private static final ForgeConfigSpec.Builder BUILD = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;

	public static ForgeConfigSpec.BooleanValue spawnPhantom;	// ファントムスポーン
	public static ForgeConfigSpec.BooleanValue spawnDrowned;	// ドラウンドスポーン
	public static ForgeConfigSpec.BooleanValue spawnTrader;	// 行商人スポーン
	public static ForgeConfigSpec.BooleanValue canTackBlock;	// エンダーマンのブロック回収
	public static ForgeConfigSpec.IntValue spawnDate;			// スイマジモブのスポーン日数
	public static ForgeConfigSpec.BooleanValue spawnSMMob;		// スイマジモブのスポーン緩和
	public static ForgeConfigSpec.BooleanValue spawnCave;		// 洞窟でのスイマジモブのスポーン

	public static ForgeConfigSpec.BooleanValue hungerSetting;	// 空腹設定
	public static ForgeConfigSpec.BooleanValue foodQuality;	// 品質設定

	public static ForgeConfigSpec.IntValue prismChance;		// プリズムフォレストバイオームチャンス
	public static ForgeConfigSpec.IntValue fruitChance;		// フルーツフォレストバイオームチャンス
	public static ForgeConfigSpec.IntValue cherreyChance;		// 桜の木バイオームチャンス

	public static ForgeConfigSpec.IntValue plainFlowerChance;	// 平原花生成チャンス
	public static ForgeConfigSpec.IntValue forestFlowerChance;	// 森花生成チャンス
	public static ForgeConfigSpec.IntValue flowerFlowerChance;	// 花生成チャンス

	static {

		BUILD.push("Mob Setting");

		spawnPhantom = BUILD
				.comment("Setting up a Phantom spawn. Default: true.")
				.define("Spawn Phantom", true);

		spawnDrowned = BUILD
				.comment("Setting up a Drowned spawn. Default: true.")
				.define("Spawn Drowned", true);

		spawnTrader = BUILD
				.comment("Setting up a Wandering Trader spawn. Default: true.")
				.define("Spawn Wandering Trader", true);

		canTackBlock = BUILD
				.comment("Enderman will be prohibited from retrieving the block. Default: true.")
				.define("Enderman Block Recovery", true);

		spawnDate = BUILD
				.comment("Number of days until sweet magic mobs spring up. Default: 5")
				.defineInRange("Set Date", 5, 0, 999);

		spawnSMMob = BUILD
				.comment("If the sweet magic mobs are hard to come up, setting it to true will make them come up as well as other mobs. Default: false.")
				.define("Relaxed spawn conditions Mobs", false);

		spawnCave = BUILD
				.comment("Sweet Magic Mob spawn setting in Cave. Default: true")
				.define("Spawn Sweet Magic Mob", true);

		BUILD.pop();

		BUILD.push("Biome Setting");

		prismChance = BUILD
				.comment("Adjustment of Prism Forest generation frequency. Default: 5")
				.defineInRange("Prism Forest Chance", 5, 0, 20);

		fruitChance = BUILD
				.comment("Adjustment of Fruit Forest generation frequency. Default: 5")
				.defineInRange("Fruit Forest Chance", 5, 0, 20);

		cherreyChance = BUILD
				.comment("Adjustment of Cherry Blossoms Forest generation frequency. Default: 5")
				.defineInRange("Cherry Blossoms Forest Chance", 5, 0, 20);

		BUILD.pop();

		BUILD.push("Generate Setting");

		plainFlowerChance = BUILD
				.comment("Setting the frequency of flowers generated on the plains. Default: 4")
				.defineInRange("Plain Flower Chance", 4, 0, 10);

		forestFlowerChance = BUILD
				.comment("Setting the frequency of flowers generated in the forest. Default: 2")
				.defineInRange("Forest Flower Chance", 2, 0, 10);

		flowerFlowerChance = BUILD
				.comment("SEstablishing the frequency of producing flowers as dye. Default: 3")
				.defineInRange("SweetMagic Flower Chance", 3, 0, 10);

		BUILD.pop();

		BUILD.push("Magic Setting");

		hungerSetting = BUILD
				.comment("Setting hunger gauge decreases when magic is used. Default: false")
				.define("Hungry after Magic Use", false);

		BUILD.pop();

		BUILD.push("Food Setting");

		foodQuality = BUILD
				.comment("Quality levels will be set for food. Default: true")
				.define("Quality Level Enablement", true);

		BUILD.pop();

		SPEC = BUILD.build();
	}

	public static void loadConfig(ForgeConfigSpec spec, Path path) {
		final CommentedFileConfig config = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
		config.load();
		spec.setConfig(config);
	}
}
