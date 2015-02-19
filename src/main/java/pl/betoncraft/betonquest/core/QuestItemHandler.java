/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2015  Jakub "Co0sh" Sapalski
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.core;

import java.util.List;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Handler for Journals.
 * 
 * @author Co0sh
 */
public class QuestItemHandler implements Listener {

    /**
     * Registers the quest item handler as Listener.
     */
    public QuestItemHandler() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        ItemStack item = event.getItemDrop().getItemStack();
        // if journal is dropped, remove it so noone else can pick it up
        if (Journal.isJournal(item)) {
            event.getItemDrop().remove();
        } else if (Utils.isQuestItem(item)) {
            Debug.info("Player " + event.getPlayer().getName() + " dropped " + item.getAmount()
                + " quest items, adding them to backpack");
            BetonQuest.getInstance().getDBHandler(PlayerConverter.getID(event.getPlayer()))
                    .addItem(item.clone(), item.getAmount());
            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onItemMove(InventoryClickEvent event) {
        if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (event.getView().getType().equals(InventoryType.CREATIVE)) {
            return;
        }
        // canceling all action that could lead to transfering the journal
        if (Journal.isJournal(event.getCursor()) || Utils.isQuestItem(event.getCursor())) {
            if (event.getAction().equals(InventoryAction.PLACE_ALL)
                || event.getAction().equals(InventoryAction.PLACE_ONE) || event.getAction().equals(
                    InventoryAction.PLACE_SOME)) {
                // this blocks normal clicking outside of the inventory
                boolean isOutside = event.getRawSlot() < (event.getView().countSlots() - 36);
                if (isOutside) {
                    event.setCancelled(true);
                }
            }
        } else if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
            if (Journal.isJournal(event.getCurrentItem()) || Utils.isQuestItem(event.getCurrentItem())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemDrag(InventoryDragEvent event) {
        if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        // this is moving the item across the inventory outside of Player's
        // inventory
        if (Journal.isJournal(event.getOldCursor()) || Utils.isQuestItem(event.getOldCursor())) {
            for (Integer slot : event.getRawSlots()) {
                if (slot < (event.getView().countSlots() - 36)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        if (event.getEntity().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        // this prevents the journal from dropping on death by removing it from
        // the list of drops
        List<ItemStack> drops = event.getDrops();
        ListIterator<ItemStack> litr = drops.listIterator();
        while (litr.hasNext()) {
            ItemStack stack = litr.next();
            if (Journal.isJournal(stack)) {
                litr.remove();
            }
            if (Utils.isQuestItem(stack)) {
                BetonQuest.getInstance().getDBHandler(PlayerConverter.getID(event.getEntity()))
                        .addItem(stack.clone(), stack.getAmount());
                litr.remove();
            }
        }
    }

    @EventHandler
    public void onItemFrameClick(PlayerInteractEntityEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        // this prevents the journal from being placed inside of item frame
        if (event.getRightClicked() instanceof ItemFrame
            && (Journal.isJournal(event.getPlayer().getItemInHand()) || Utils.isQuestItem(event
                    .getPlayer().getItemInHand()))) {
            event.setCancelled(true);
        }
    }
}
