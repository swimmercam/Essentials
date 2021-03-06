package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.economy.Trade;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.permissions.Permissions;
import com.earth2me.essentials.settings.SpawnsHolder;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public class Commandspawn extends EssentialsCommand
{
	@Override
	public void run(final IUser user, final String commandLabel, final String[] args) throws Exception
	{
		final Trade charge = new Trade(commandName, ess);
		charge.isAffordableFor(user);
		if (args.length > 0 && Permissions.SPAWN_OTHERS.isAuthorized(user))
		{
			final IUser otherUser = getPlayer(args, 0);
			respawn(otherUser, null);
			if (!otherUser.equals(user))
			{
				otherUser.sendMessage(_("teleportAtoB", user.getDisplayName(), "spawn"));
				user.sendMessage(_("teleporting"));
			}
		}
		else
		{
			respawn(user, null);
		}
	}

	@Override
	protected void run(final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		final IUser user = getPlayer(args, 0);
		respawn(user, null);
		user.sendMessage(_("teleportAtoB", user.getDisplayName(), "spawn"));
		sender.sendMessage(_("teleporting"));
	}

	private void respawn(final IUser user, final Trade charge) throws Exception
	{
		final SpawnsHolder spawns = (SpawnsHolder)this.module;
		final Location spawn = spawns.getSpawn(ess.getRanks().getMainGroup(user));
		user.getTeleport().teleport(spawn, charge, TeleportCause.COMMAND);
	}
}
