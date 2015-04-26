package moze_intel.projecte.utils;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Iterator;
import java.util.List;

/**
 * Helpers for Inventories, ItemStacks, Items, and the Ore Dictionary
 * Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class ItemHelper
{
	public static boolean areItemStacksEqual(ItemStack stack1, ItemStack stack2)
	{
		return ItemStack.areItemStacksEqual(getNormalizedStack(stack1), getNormalizedStack(stack2));
	}

	public static boolean areItemStacksEqualIgnoreNBT(ItemStack stack1, ItemStack stack2)
	{
		if (stack1.getItem() != stack2.getItem())
		{
			return false;
		}


		if (stack1.getItemDamage() == OreDictionary.WILDCARD_VALUE || stack2.getItemDamage() == OreDictionary.WILDCARD_VALUE)
		{
			return true;
		}

		return stack1.getItemDamage() == stack2.getItemDamage();
	}

	public static boolean basicAreStacksEqual(ItemStack stack1, ItemStack stack2)
	{
		return (stack1.getItem() == stack2.getItem()) && (stack1.getItemDamage() == stack2.getItemDamage());
	}

	public static boolean containsItemStack(List<ItemStack> list, ItemStack toSearch)
	{
		Iterator<ItemStack> iter = list.iterator();

		while (iter.hasNext())
		{
			ItemStack stack = iter.next();

			if (stack == null)
			{
				continue;
			}

			if (stack.getItem().equals(toSearch.getItem()))
			{
				if( !stack.getHasSubtypes() || stack.getItemDamage() == toSearch.getItemDamage())
				{
					return true;
				}
			}
		}
		return false;
	}

	public static boolean containsItemStack(ItemStack[] stacks, ItemStack toSearch)
	{
		for (ItemStack stack : stacks)
		{
			if (stack == null)
			{
				continue;
			}

			if (stack.getItem() == toSearch.getItem())
			{
				if (!stack.getHasSubtypes() || stack.getItemDamage() == toSearch.getItemDamage())
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns an ItemStack with stacksize 1.
	 */
	public static ItemStack getNormalizedStack(ItemStack stack)
	{
		ItemStack result = stack.copy();
		result.stackSize = 1;
		return result;
	}

	/**
	 * Get a List of itemstacks from an OD name.<br>
	 * It also makes sure that no items with damage 32767 are included, to prevent errors.
	 */
	public static List<ItemStack> getODItems(String oreName)
	{
		List<ItemStack> result = Lists.newArrayList();

		for (ItemStack stack : OreDictionary.getOres(oreName))
		{
			if (stack == null)
			{
				continue;
			}

			if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
			{
				List<ItemStack> list = Lists.newArrayList();

				ItemStack copy = stack.copy();
				copy.setItemDamage(0);

				list.add(copy.copy());

				String startName = copy.getUnlocalizedName();

				for (int i = 1; i <= 128; i++)
				{
					try
					{
						copy.setItemDamage(i);

						if (copy.getUnlocalizedName() == null || copy.getUnlocalizedName().equals(startName))
						{
							result.addAll(list);
							break;
						}
					}
					catch (Exception e)
					{
						PELogger.logFatal("Couldn't retrieve OD items for: " + oreName);
						PELogger.logFatal("Caused by: " + e.toString());

						result.addAll(list);
						break;
					}

					list.add(copy.copy());

					if (i == 128)
					{
						copy.setItemDamage(0);
						result.add(copy);
					}
				}
			}
			else
			{
				result.add(stack.copy());
			}
		}

		return result;
	}

	public static String getOreDictionaryName(ItemStack stack)
	{
		int[] oreIds = OreDictionary.getOreIDs(stack);

		if (oreIds.length == 0)
		{
			return "Unknown";
		}

		return OreDictionary.getOreName(oreIds[0]);
	}

	public static ItemStack getStackFromInv(IInventory inv, ItemStack stack)
	{
		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack s = inv.getStackInSlot(i);

			if (s == null)
			{
				continue;
			}

			if (basicAreStacksEqual(stack, s))
			{
				return s;
			}
		}

		return null;
	}

	public static ItemStack getStackFromInv(ItemStack[] inv, ItemStack stack)
	{
		for (ItemStack s : inv)
		{
			if (s == null)
			{
				continue;
			}

			if (basicAreStacksEqual(stack, s))
			{
				return s;
			}
		}

		return null;
	}

	/**
	 *	@throws NullPointerException
	 */
	public static ItemStack getStackFromString(String internal, int metaData)
	{
		Item item = (Item) Item.itemRegistry.getObject(internal);

		if (item == null)
		{
			return null;
		}

		return new ItemStack(item, 1, metaData);
	}

	public static boolean hasSpace(IInventory inv, ItemStack stack)
	{
		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack invStack = inv.getStackInSlot(i);

			if (invStack == null)
			{
				return true;
			}

			if (areItemStacksEqual(stack, invStack) && invStack.stackSize < invStack.getMaxStackSize())
			{
				return true;
			}
		}

		return false;
	}

	public static boolean hasSpace(ItemStack[] inv, ItemStack stack)
	{
		for (ItemStack invStack : inv)
		{
			if (invStack == null)
			{
				return true;
			}

			if (areItemStacksEqual(stack, invStack) && invStack.stackSize < invStack.getMaxStackSize())
			{
				return true;
			}
		}

		return false;
	}

	public static boolean invContainsItem(IInventory inv, ItemStack toSearch)
	{
		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack stack = inv.getStackInSlot(i);

			if (stack != null && basicAreStacksEqual(stack, toSearch))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean invContainsItem(ItemStack inv[], ItemStack toSearch)
	{
		for (ItemStack stack : inv)
		{
			if (stack != null && basicAreStacksEqual(stack, toSearch))
			{
				return true;
			}
		}

		return false;
	}

	public static boolean invContainsItem(ItemStack inv[], Item toSearch)
	{
		for (ItemStack stack : inv)
		{
			if (stack != null && stack.getItem() == toSearch)
			{
				return true;
			}
		}
		return false;
	}

	public static boolean isOre(Block block)
	{
		if (block.equals(Blocks.lit_redstone_ore))
		{
			return true;
		}

		return getOreDictionaryName(new ItemStack(block)).startsWith("ore");
	}

	/**
	 *	Returns an itemstack if the stack passed could not entirely fit in the inventory, otherwise returns null.
	 */
	public static ItemStack pushStackInInv(IInventory inv, ItemStack stack)
	{
		int limit;

		if (inv instanceof InventoryPlayer)
		{
			limit = 36;
		}
		else
		{
			limit = inv.getSizeInventory();
		}

		for (int i = 0; i < limit; i++)
		{
			ItemStack invStack = inv.getStackInSlot(i);

			if (invStack == null)
			{
				inv.setInventorySlotContents(i, stack);
				return null;
			}

			if (areItemStacksEqual(stack, invStack) && invStack.stackSize < invStack.getMaxStackSize())
			{
				int remaining = invStack.getMaxStackSize() - invStack.stackSize;

				if (remaining >= stack.stackSize)
				{
					invStack.stackSize += stack.stackSize;
					inv.setInventorySlotContents(i, invStack);
					return null;
				}

				invStack.stackSize += remaining;
				inv.setInventorySlotContents(i, invStack);
				stack.stackSize -= remaining;
			}
		}

		return stack.copy();
	}

	/**
	 *	Returns an itemstack if the stack passed could not entirely fit in the inventory, otherwise returns null.
	 */
	public static ItemStack pushStackInInv(ItemStack[] inv, ItemStack stack)
	{
		for (int i = 0; i < inv.length; i++)
		{
			ItemStack invStack = inv[i];

			if (invStack == null)
			{
				inv[i] = stack;
				return null;
			}

			if (areItemStacksEqual(stack, invStack) && invStack.stackSize < invStack.getMaxStackSize())
			{
				int remaining = invStack.getMaxStackSize() - invStack.stackSize;

				if (remaining >= stack.stackSize)
				{
					invStack.stackSize += stack.stackSize;
					inv[i] = invStack;
					return null;
				}

				invStack.stackSize += remaining;
				inv[i] = invStack;
				stack.stackSize -= remaining;
			}
		}

		return stack.copy();
	}
}
