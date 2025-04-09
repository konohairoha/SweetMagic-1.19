package sweetmagic.init.item.sm;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.SoundInit;
import sweetmagic.init.TagInit;

public class SMLootBag extends SMItem {

	public final int data;

	public SMLootBag(String name, int data) {
		super(name, setItem(SweetMagicCore.smMagicTab));
		this.data = data;
	}

	// 右クリック
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

		// アイテムスタックを取得
		ItemStack stack = player.getItemInHand(hand);

		if (!world.isClientSide) {
			TagKey<Item> tagKey = null;
			int value = 1;

			switch (this.data) {
			case 0:
				tagKey = TagInit.SEEDS;
				value = 5;
				break;
			case 1:
				tagKey = TagInit.EGGS;
				value = 5 + world.random.nextInt(8);
				break;
			case 2:
				tagKey = TagInit.MAGIC_ACCESSORY;
				value = 1;
				break;
			case 3:
				tagKey = TagInit.FLOWER;
				value = 5;
				break;
			}

			this.playSound(player, SoundInit.ROBE, 0.0625F, 1.075F);
			this.getOreLoot(world, player.getOnPos(), player, world.random, tagKey, value);
			stack.shrink(1);
		}

		return InteractionResultHolder.consume(stack);
	}

	// 鉱石辞書からランダムにアイテムを引っ張る
	public void getOreLoot(Level world, BlockPos pos, Player player, RandomSource rand, TagKey<Item> name, int value) {
		// ルートテーブルをリストに入れる
		List<ItemStack> seedList = this.getList(ForgeRegistries.ITEMS.tags().getTag(name).stream());

		for (int i = 0; i < value; i ++) {
			this.spawnItem(world, pos, seedList.get(rand.nextInt(seedList.size())));
		}
	}

	// リストの取得
	public List<ItemStack> getList(Stream<Item> stream) {
		return stream.map(Item::getDefaultInstance).collect(Collectors.toList());
	}

	// ツールチップの表示
	public void addTip(ItemStack stack, List<Component> toolTip) {
		toolTip.add(this.getText(this.name).withStyle(GREEN));
	}
}
