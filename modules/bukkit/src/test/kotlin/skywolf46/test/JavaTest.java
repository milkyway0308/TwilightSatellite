package skywolf46.test;

import org.bukkit.command.CommandSender;
import skywolf46.twilightsatellite.bukkit.commands.CommandArgs;
import skywolf46.twilightsatellite.bukkit.commands.bukkit.transformer.PlayerArgTransformer;
import skywolf46.twilightsatellite.bukkit.commands.bukkit.transformer.Transformers;

public class JavaTest {
	public void test() {
		CommandArgs<CommandSender> args = new CommandArgs<>(null, "/test hello world", 0);
		args.transformArgs(Transformers.player(), newArgs -> {
			newArgs.nextArg(String.class, Integer.class, (_args, name, amount) -> {

			});
		});
	}
}
