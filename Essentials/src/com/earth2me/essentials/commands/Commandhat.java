package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class Commandhat extends EssentialsCommand
{
	@Override
	protected void run(IUser user, String commandLabel, String[] args) throws Exception
	{
		if (args.length > 0 && (args[0].contains("rem") || args[0].contains("off") || args[0].equalsIgnoreCase("0")))
		{
			final PlayerInventory inv = user.getInventory();
			final ItemStack head = inv.getHelmet();
			if (head == null || head.getType() == Material.AIR)
			{
				user.sendMessage(_("hatEmpty"));
			}
			else
			{
				final ItemStack air = new ItemStack(Material.AIR);
				inv.setHelmet(air);
				InventoryWorkaround.addItem(user.getInventory(), true, head);
				user.sendMessage(_("hatRemoved"));
			}
		}
		else
		{
			if (user.getItemInHand().getType() != Material.AIR)
			{
				final ItemStack hand = user.getItemInHand();
				if (hand.getType().getMaxDurability() == 0)
				{
					final PlayerInventory inv = user.getInventory();
					final ItemStack head = inv.getHelmet();
					inv.removeItem(hand);
					inv.setHelmet(hand);
					inv.setItemInHand(head);
					user.sendMessage(_("hatPlaced"));
				}
				else
				{
					user.sendMessage(_("hatArmor"));
				}
			}
			else
			{
				user.sendMessage(_("hatFail"));
			}
		}
	}
}
