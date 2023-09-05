package me.kan.BukkitPluginMages;

import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class Snowball
implements Listener
{
	public static BukkitPluginMages plugin;
	public final Logger logger = Logger.getLogger("Minecraft");

	public Snowball(BukkitPluginMages instance) {
		plugin = instance;
	}
	@EventHandler
	public void SnowballThrow(ProjectileHitEvent e) {
		Projectile snowball = e.getEntity();
		LivingEntity thrower = null;
		try{thrower = (LivingEntity) snowball.getShooter();}catch(Exception thrownFromDispencer){};


		if(thrower != null && snowball.getType() == EntityType.ARROW&& thrower.getType() == EntityType.PLAYER &&isPlayingGame((Player)thrower)){
			Player deity = (Player)thrower;
			World w = snowball.getWorld();
			Location landing = new Location (w, snowball.getLocation().getX(), snowball.getLocation().getY(), snowball.getLocation().getZ(), deity.getLocation().getYaw(), deity.getLocation().getPitch());
			Location landingTemp = snowball.getLocation();
			Boolean isWool = false;
			for(int i = 0; i<5 && !isWool; i++){
				if (landingTemp.getBlock().getTypeId() == 35){
					isWool = true;
				}else{
					landingTemp.setY(landingTemp.getY()-1);
				}

				if(w.getName().equalsIgnoreCase("world") && isWool){
					int woolColor = landingTemp.getBlock().getData();
					String color = null;
					if (plugin.RED[0].equals(deity)) color = "red";
					if (plugin.BLUE[0].equals(deity)) color = "blue";
					if(color == null){
						deity.getInventory().clear();
					}else{
						Player freeMage = getFreeMage(color);
						try{
							freeMage.teleport(landing);
							freeMage.getInventory().clear();
							freeMage.getInventory().addItem(new ItemStack(Material.SNOW_BALL, getMaxSnow(woolColor)));
							if(woolColor == 1 || woolColor == 12){
								specialAblilty(woolColor, freeMage, landing);
							}
						}catch(Exception nofreeplayers){}
					}
				}
			}
		}
		if(thrower != null && snowball.getType() == EntityType.SNOWBALL&& thrower.getType() == EntityType.PLAYER&&isPlayingGame((Player)thrower)){
			Player mage = (Player)thrower;
			World w = snowball.getWorld();
			Location landing = snowball.getLocation();
			Location landingTemp = snowball.getLocation();
			Location mageBlockUnder = new Location(w, mage.getLocation().getX(),mage.getLocation().getY()-1,mage.getLocation().getZ());
			int blockType = mageBlockUnder.getBlock().getTypeId();
			Boolean quickExit = false;
			for(int i = 0; i<5 && !quickExit; i++){
				if (landingTemp.getBlock().getTypeId() == 35){
					quickExit = true;
				}else{
					landingTemp.setY(landingTemp.getY()-1);
				}
			}
			if(blockType == 35 && w.getName().equalsIgnoreCase("world") && quickExit){
				int woolColor = mageBlockUnder.getBlock().getData();
				Location couldBeWater = new Location(w, mageBlockUnder.getX() ,mageBlockUnder.getY() + 1 ,mageBlockUnder.getZ());
				if ((couldBeWater.getBlock().getTypeId() != 8) && (couldBeWater.getBlock().getTypeId() != 9)){

					if (getSnowballs(mage)> getMaxSnow(woolColor)-1){
						setSnowballs(mage, getMaxSnow(woolColor - 1), woolColor);
					}
					if(woolColor == 0 || woolColor == 3 || woolColor == 4 || woolColor == 5 || woolColor == 6 || woolColor == 7 || woolColor == 11 || woolColor == 13 || woolColor == 14 || woolColor == 15){
						w.spawnEntity(landing, mobToSpawn(woolColor));
					}
					if(woolColor==8){
						Skeleton skeleton = (Skeleton) w.spawnEntity(landing, EntityType.SKELETON);
						skeleton.getEquipment().setItemInHand(new ItemStack(Material.BOW,1));
					}
					if(woolColor==14){
						plugin.FIRE = true;
					}
					if(woolColor == 11){
						landing.getBlock().setType(Material.WATER);
						plugin.WATER = true;
					}
				}else{
					mage.sendMessage("You can't summon mobs while in water");
					mage.getInventory().addItem(new ItemStack[] {new ItemStack(Material.getMaterial(332), (1)) });
				}
			}
		}
	}


	private Player getFreeMage(String color) {
		boolean exit= false;
		Player noob = null;
		if(color.equalsIgnoreCase("red")){
			for(int i = 1; i<4 && !exit; i++){
				if(isFree(plugin.RED[i])){
					noob = plugin.RED[i];
					exit = true;
				}
			}

		}else{
			for(int i = 1; i<4 && !exit; i++){
				if(isFree(plugin.BLUE[i])){
					noob = plugin.BLUE[i];
					exit = true;
				}
			}

		}
		return noob;
	}
	private boolean isFree(Player p){
		boolean isFree = false;
		Location locationTemp = p.getLocation();
		for(int i = 0; i<3 && !isFree; i++){
			if (locationTemp.getBlock().getTypeId() == 20){
				isFree = true;
			}else{
				locationTemp.setY(locationTemp.getY()-1);
			}
		}
		return isFree;
	}

	private boolean isPlayingGame(Player p) {
		boolean playing = false;
		for(int i = 0; i < 4; i++){
			if (plugin.RED[i].equals(p) || (plugin.BLUE[i].equals(p))){
				playing = true;
			}
		}
		if (playing){
			return true;
		}else{
			return false;
		}
	}

	private void setSnowballs(Player mage, int maxSnow, int woolColor) {
		mage.getInventory().removeItem(new ItemStack[] {new ItemStack(Material.getMaterial(332), (getSnowballs(mage)-getMaxSnow(woolColor)+1)) });
	}

	public int getSnowballs(Player player)
	{
		ItemStack[] inv = player.getInventory().getContents();

		int cuantity= 0;
		for(int i = 0; i < inv.length; i++) {
			if(inv[i] != null){
				if( inv[i].getTypeId() ==(332)){
					int cant = inv[i].getAmount();
					cuantity= cuantity + cant;
				}
			}
		}
		return cuantity;
	}
	private int getMaxSnow(int woolColor){
		switch(woolColor){
		case 0:
			return 5;
			//return EntityType.SNOWMAN;
		case 3:
			return 4;
			//return EntityType.CAVE_SPIDER;
		case 4:
			return 10;
			//return EntityType.CHICKEN;
		case 5:
			return 4;
			//return EntityType.CREEPER;
		case 6:
			return 3;
			//return EntityType.PIG;
		case 7:
			return 2;
			//return EntityType.IRON_GOLEM;
		case 8:
			return 4;
			//return EntityType.SKELETON;
		case 11:
			return 6;
			//return EntityType.SQUID;
		case 13:
			return 5;
			//return EntityType.ZOMBIE;
		case 14:
			return 2;
			//return EntityType.BLAZE;
		case 15:
			return 6;
			//return EntityType.ENDERMAN;
		default:
			return 5;
		}
	}

	private void specialAblilty(int woolColor, Player mage, Location landing) {
		switch(woolColor){
		case 1:
			mage.getInventory().addItem(new ItemStack(Material.GOLD_SWORD, 1));
		case 12:
			mage.getInventory().addItem(new ItemStack(Material.SADDLE, 1));
		}

	}

	private EntityType mobToSpawn(int woolColor) {
		switch (woolColor){
		case 0:
			return EntityType.SNOWMAN;
		case 3:
			return EntityType.CAVE_SPIDER;
		case 4:
			return EntityType.CHICKEN;
		case 5:
			return EntityType.CREEPER;
		case 6:
			return EntityType.PIG;
		case 7:
			return EntityType.IRON_GOLEM;
		case 11:
			return EntityType.SQUID;
		case 13:
			return EntityType.ZOMBIE;
		case 14:
			return EntityType.BLAZE;
		case 15:
			return EntityType.ENDERMAN;
		default:
			return EntityType.BAT;
		}

	}
	@EventHandler
	public void moveEvent (PlayerMoveEvent e) {
		Player mage = e.getPlayer();
		if(mage.equals(plugin.RED[0]) || mage.equals(plugin.BLUE[0])){
			Location toPlace = e.getTo();
			Boolean endLoop = false;
			for(int i = 0; i<3; i++){
				if (toPlace.getBlock().getTypeId() == 152){
					endLoop = true;
				}else{
					toPlace.setY(toPlace.getY()-1);
				}
			}
			if (endLoop){
				Location centerBlock = new Location(e.getPlayer().getWorld(), e.getFrom().getBlockX() + .5 , e.getFrom().getBlockY(),e.getFrom().getBlockZ() + .5, e.getFrom().getYaw(), e.getFrom().getPitch());
				mage.teleport(centerBlock);
			}
		}
		Location locationTemp = e.getFrom();
		Location locationToTemp = e.getTo();
		World w = mage.getWorld();
		boolean startsOnWool = false;
		boolean endsOnWool = false;
		for(int i = 0; i<3 && !startsOnWool; i++){
			if (locationTemp.getBlock().getTypeId() == 35){
				startsOnWool = true;
			}else{
				locationTemp.setY(locationTemp.getY()-1);
			}
		}
		for(int i = 0; i<3 && !endsOnWool; i++){
			if (locationToTemp.getBlock().getTypeId() == 35){
				endsOnWool = true;
			}else{
				locationToTemp.setY(locationToTemp.getY()-1);
			}
		}
		Location aboveWool = new Location (w, locationTemp.getBlockX(), locationTemp.getBlockY() + 1, locationTemp.getBlockZ());
		Location aboveWoolTo = new Location (w, locationToTemp.getBlockX(), locationToTemp.getBlockY() + 1, locationToTemp.getBlockZ());
		if(isPlayingGame(mage)&& startsOnWool && endsOnWool && !(aboveWool.getBlock().getTypeId() == 8 || aboveWool.getBlock().getTypeId() == 9) && !(aboveWoolTo.getBlock().getTypeId() == 8 || aboveWoolTo.getBlock().getTypeId() == 9) && w.getName().equals("world")&& (locationTemp.getBlock().getData() != locationToTemp.getBlock().getData())&&!(mage.isInsideVehicle())){
			int x = (int)e.getFrom().getBlockX();
			int z = (int)e.getFrom().getBlockZ();
			Location centerBlock = new Location(w, x + .5 , e.getFrom().getBlockY() + 1,z + .5, e.getFrom().getYaw(), e.getFrom().getPitch());
			mage.teleport(centerBlock);
		}
	}
	public int getColor(Location l){
		return l.getBlock().getData();
	}
	@EventHandler
	public void dropEvent (PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		if(isPlayingGame(p)){
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void leaveEvent (PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(isPlayingGame(p)){
			p.getInventory().clear();
			Snowball.plugin.endGame();
			Snowball.plugin.getServer().broadcastMessage(p.getDisplayName()+" quit while the game was in progress, so the game must end.");
		}
	}
	@EventHandler
	public void dieEvent (PlayerDeathEvent e) {
		Player p = e.getEntity();
		if(isPlayingGame(p)){
			for(int i=1; i< 4; i++){
				if(Snowball.plugin.RED[i].equals(p)){
					//do stuff to dead player
					Snowball.plugin.RED[0].damage(1);
				}else{
					if(Snowball.plugin.BLUE[i].equals(p)){
						//do stuff to dead player
						Snowball.plugin.BLUE[0].damage(1);
					}
				}
			}
			if(Snowball.plugin.RED[0].equals(p)){
				Snowball.plugin.getServer().broadcastMessage("BLUE TEAM WINS!");
				Snowball.plugin.endGame();
			}else{
				if(Snowball.plugin.BLUE[0].equals(p)){
					Snowball.plugin.getServer().broadcastMessage("RED TEAM WINS!");
					Snowball.plugin.endGame();
				}
			}
		}
	}
	@EventHandler
	public void regenhealthEvent (EntityRegainHealthEvent e) {
		if(e.getEntityType().equals(EntityType.PLAYER)){
			Player p = (Player) e.getEntity();
			if(Snowball.plugin.RED[0].equals(p)||Snowball.plugin.BLUE[0].equals(p)){
				e.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void pvpevent (EntityDamageByEntityEvent e) {
		Entity ep = e.getDamager();
		if(ep.getType().equals(EntityType.PLAYER)){
			Player p = (Player) ep;
			if(isPlayingGame(p)&&(e.getEntity().getType().equals(EntityType.PLAYER))){
				e.setCancelled(true);
			}
		}else{
			if(ep.getType().equals(EntityType.ARROW)&&e.getEntity().getType().equals(EntityType.PLAYER)){
				Player p = (Player) e.getEntity();
				if(isPlayingGame(p)){
					e.setCancelled(true);
				}
			}
		}
	}
	@EventHandler
	public void respawnevent (PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		if(isPlayingGame(p)){
			e.setRespawnLocation(plugin.glassBox);
		}
	}


	/*TODO, 
	 * THIS IS IN NEW ARENA
	 * NO DUPLICATE PLAYERS ENABLED*/
}