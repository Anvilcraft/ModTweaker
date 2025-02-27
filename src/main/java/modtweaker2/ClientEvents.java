package modtweaker2;

import static modtweaker2.helpers.LogHelper.print;

import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.api.player.IPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEvents {
	public static int cooldown;
	public static boolean active;

	@SubscribeEvent
	public void onDrawTooltip(ItemTooltipEvent event) {

		IPlayer player = MineTweakerMC.getIPlayer(event.entityPlayer);
		if (player != null) {
			IItemStack hand = MineTweakerMC.getIItemStack(event.itemStack);
			if (hand != null) {
				if (active) {

					String print = hand.toString();
					event.toolTip.add(print);
					if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSprint)) {
						if (cooldown <= 0) {
							cooldown = 30;
							print(print + "  --  " + hand.getDisplayName());
						} else
							cooldown--;
					} else
						cooldown--;
				}

			}
		}
	}

}
