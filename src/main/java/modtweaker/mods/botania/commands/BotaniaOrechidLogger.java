package modtweaker.mods.botania.commands;

import minetweaker.MineTweakerAPI;
import minetweaker.MineTweakerImplementationAPI;
import minetweaker.api.player.IPlayer;
import minetweaker.api.server.ICommandFunction;
import vazkii.botania.api.BotaniaAPI;

import java.util.Set;

public class BotaniaOrechidLogger implements ICommandFunction {

	@Override
	public void execute(String[] arguments, IPlayer player) {

		Set<String> keys = BotaniaAPI.oreWeights.keySet();
		System.out.println("Orechid Keys: " + keys.size());
		for (String str : BotaniaAPI.oreWeights.keySet()) {
			System.out.println("Orechid Key: " + str);
			MineTweakerAPI.logCommand(str + ": " + BotaniaAPI.oreWeights.get(str));
		}
		if (player != null) {
			player.sendChat(MineTweakerImplementationAPI.platform.getMessage("List generated; see minetweaker.log in your minecraft dir"));
		}
	}
}
