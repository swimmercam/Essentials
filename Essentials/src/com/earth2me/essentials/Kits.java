package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IKits;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.commands.NoChargeException;
import com.earth2me.essentials.settings.Kit;
import com.earth2me.essentials.storage.AsyncStorageObjectHolder;
import com.earth2me.essentials.user.UserData.TimestampType;
import com.earth2me.essentials.utils.DateUtil;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.bukkit.inventory.ItemStack;


public class Kits extends AsyncStorageObjectHolder<com.earth2me.essentials.settings.Kits> implements IKits
{
	public Kits(final IEssentials ess)
	{
		super(ess, com.earth2me.essentials.settings.Kits.class);
		onReload();
	}

	@Override
	public File getStorageFile() throws IOException
	{
		return new File(ess.getDataFolder(), "kits.yml");
	}

	@Override
	public Kit getKit(String kitName) throws Exception
	{
		acquireReadLock();
		try
		{
			if (getData().getKits() == null || kitName == null
				|| !getData().getKits().containsKey(kitName.toLowerCase(Locale.ENGLISH)))
			{
				throw new Exception(_("kitError2"));
			}
			final Kit kit = getData().getKits().get(kitName.toLowerCase(Locale.ENGLISH));
			if (kit == null)
			{
				throw new Exception(_("kitError2"));
			}
			return kit;
		}
		finally
		{
			unlock();
		}
	}

	@Override
	public void sendKit(IUser user, String kitName) throws Exception
	{
		final Kit kit = getKit(kitName);
		sendKit(user, kit);
	}

	@Override
	public void sendKit(IUser user, Kit kit) throws Exception
	{
		final List<ItemStack> itemList = kit.getItems();
		user.giveItems(itemList, true);
	}

	@Override
	public Collection<String> getList() throws Exception
	{
		acquireReadLock();
		try
		{
			if (getData().getKits() == null)
			{
				return Collections.emptyList();
			}
			return new ArrayList<String>(getData().getKits().keySet());
		}
		finally
		{
			unlock();
		}
	}

	@Override
	public boolean isEmpty()
	{
		return getData().getKits().isEmpty();
	}

	@Override
	public void finishRead()
	{
	}

	@Override
	public void finishWrite()
	{
	}

	@Override
	public void checkTime(final IUser user, Kit kit) throws NoChargeException
	{
		final Calendar time = new GregorianCalendar();
		// Take the current time, and remove the delay from it.
		final double delay = kit.getDelay();
		final Calendar earliestTime = new GregorianCalendar();
		earliestTime.add(Calendar.SECOND, -(int)delay);
		earliestTime.add(Calendar.MILLISECOND, -(int)((delay * 1000.0) % 1000.0));

		// This value contains the most recent time a kit could have been used that would allow another use.

		final long earliestLong = earliestTime.getTimeInMillis();

		// When was the last kit used?
		final Long lastTime = user.getTimestamp(TimestampType.KIT);
		if (lastTime == null || lastTime < earliestLong)
		{
			user.setTimestamp(TimestampType.KIT, time.getTimeInMillis());
		}
		else if (lastTime > time.getTimeInMillis())
		{
			// This is to make sure time didn't get messed up on last kit use.
			// If this happens, let's give the user the benifit of the doubt.
			user.setTimestamp(TimestampType.KIT, time.getTimeInMillis());
		}
		else
		{
			time.setTimeInMillis(lastTime);
			time.add(Calendar.SECOND, (int)delay);
			time.add(Calendar.MILLISECOND, (int)((delay * 1000.0) % 1000.0));
			user.sendMessage(_("kitTimed", DateUtil.formatDateDiff(time.getTimeInMillis())));
			throw new NoChargeException();
		}
	}
}
