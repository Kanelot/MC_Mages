package me.kan.BukkitPluginMages;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPluginMages extends JavaPlugin
{
	public static BukkitPluginMages plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public final Snowball playerListener = new Snowball(this);
	public final ButtonPress buttonListener = new ButtonPress(this);
	Player[] RED = new Player[4];
	Player[] BLUE = new Player[4];
	public int redNumber;
	public int blueNumber;
	boolean listenerHasStarted = false;
	boolean FIRE = false;
	boolean WATER = false;
	boolean isGaming = false;
	Location glassBox;
	Location rstrip;
	Location bstrip;
	int tx = 67;
	int ty = 28;
	int tz = -44;
	public void endGame(){
		HandlerList.unregisterAll(playerListener);
		listenerHasStarted = false;
		redNumber = 0;
		blueNumber = 0;
		if(WATER||FIRE){
			Location pt1 = new Location(plugin.getServer().getWorld("world"),plugin.tx-714, plugin.ty+5, plugin.tz+27);
			Location pt2 = new Location(plugin.getServer().getWorld("world"),plugin.tx-755, plugin.ty+6, plugin.tz+83);
			for(int x = pt2.getBlockX(); x<=pt1.getBlockX(); x++){
				for(int z = pt1.getBlockZ(); z<=pt2.getBlockZ(); z++){
					Location l = new Location(plugin.getServer().getWorld("world"), x, pt1.getBlockY(),z);
					l.getBlock().setType(Material.AIR);
				}
			}
		}
		if(FIRE){
			Location pt1 = new Location(plugin.getServer().getWorld("world"),plugin.tx-714, plugin.ty+4, plugin.tz+27);
			Location pt2 = new Location(plugin.getServer().getWorld("world"),plugin.tx-755, plugin.ty+4, plugin.tz+83);
			for(int x = pt2.getBlockX(); x<=pt1.getBlockX(); x++){
				for(int z = pt1.getBlockZ(); z<=pt2.getBlockZ(); z++){
					Location l = new Location(plugin.getServer().getWorld("world"), x, pt1.getBlockY(),z);
					Location l2 = new Location(plugin.getServer().getWorld("world"), x, pt1.getBlockY()-4,z);
					if(!(l.getBlock().getType().equals(Material.GLOWSTONE))){
						l.getBlock().setType(Material.WOOL);
						l.getBlock().setData(l2.getBlock().getData());
					}
				}
			}
		}
		WATER = false;
		FIRE = false;
		Location EXIT = new Location(RED[2].getWorld(), tx-734,ty+11,tz+19);
		for(int i = 0; i < 4; i++){
			RED[i].teleport(EXIT);
			RED[i].getInventory().clear();
		}
		for(int i = 0; i < 4; i++){
			BLUE[i].teleport(EXIT);
			BLUE[i].getInventory().clear();
		}
		RED[0] = null;
		RED[1] = null;
		RED[2] = null;
		RED[3] = null;
		BLUE[0] = null;
		BLUE[1] = null;
		BLUE[2] = null;
		BLUE[3] = null;
		isGaming = false;
	}
	public void addRed(Player p){
		boolean isFirstTime = true;
		for(int i = 0; redNumber < 5 && i<redNumber&&isFirstTime; i++){
			if (RED[i].equals(p)){
				isFirstTime = false;
			}
		}
		for(int i = 0; blueNumber < 5 &&i<blueNumber&&isFirstTime; i++){
			if (BLUE[i].equals(p)){
				isFirstTime = false;
			}
		}
		if(RED[3] != null){
			p.sendMessage("This team is already full");
		}else{
			if(isFirstTime){
				redNumber++;
				if (redNumber<5){
					RED[redNumber-1] = p;
					p.sendMessage("You are now Player " + redNumber + " of the red team!");
				}
			}
			else
			{p.sendMessage("You have already joined the game!");
			}
		}

	}

	public void addBlue(Player p){
		boolean isFirstTime = true;
		for(int i = 0; redNumber < 5 && i<redNumber&&isFirstTime; i++){
			if (RED[i].equals(p)){
				isFirstTime = false;
			}
		}
		for(int i = 0; blueNumber < 5 &&i<blueNumber&&isFirstTime; i++){
			if (BLUE[i].equals(p)){
				isFirstTime = false;
			}
		}
		if(BLUE[3] != null){
			p.sendMessage("This team is already full");
		}else{
			if(isFirstTime){
				blueNumber++;
				if (blueNumber<5){
					BLUE[blueNumber-1] = (p);
					p.sendMessage("You are now Player " + blueNumber + " of the blue team!");
				}
			}else{p.sendMessage("You have already joined the game!");}
		}

	}


	public void startGame(Player p) {
		this.activateListener();
		listenerHasStarted = true;
		glassBox = new Location(p.getWorld(), tx-734,ty+36,tz+60);
		bstrip = new Location(p.getWorld(), tx-737.5,ty+13,tz+29.5);
		rstrip = new Location(p.getWorld(), tx-730.5,ty+13,tz+29.5);
		RED[0].getInventory().clear();
		BLUE[0].getInventory().clear();
		RED[0].getInventory().addItem(new ItemStack(261,1));
		RED[0].getInventory().addItem(new ItemStack(262,20));
		BLUE[0].getInventory().addItem(new ItemStack(261,1));
		BLUE[0].getInventory().addItem(new ItemStack(262,20));
		RED[0].teleport(rstrip);
		BLUE[0].teleport(bstrip);
		for(int i = 1; i<4; i++){
			RED[i].getInventory().clear();
			BLUE[i].getInventory().clear();
			RED[i].teleport(glassBox);
			BLUE[i].teleport(glassBox);
		}
		isGaming = true;
	}
	public void onDisable()
	{
		if(isGaming){
			endGame();
		}
		PluginDescriptionFile pdfFile = getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");
	}

	public void onEnable()
	{
		redNumber = 0;
		blueNumber = 0;
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this.buttonListener, this);
		PluginDescriptionFile pdfFile = getDescription();
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled. Lets go!!!");

	}
	public void activateListener()
	{
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this.playerListener, this);
		this.logger.info("A fight has started!. Good luck to all!");
	}
}

