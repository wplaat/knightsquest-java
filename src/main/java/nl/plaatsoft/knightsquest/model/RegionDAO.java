package nl.plaatsoft.knightsquest.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.layout.Pane;

import nl.plaatsoft.knightsquest.tools.MyData;
import nl.plaatsoft.knightsquest.tools.MyFactory;
import nl.plaatsoft.knightsquest.tools.MyRandom;
import nl.plaatsoft.knightsquest.tools.MySound;
import nl.plaatsoft.knightsquest.ui.Constants;

/**
 * The Class RegionDAO
 * 
 * @author wplaat
 */
public class RegionDAO {

	/** The Constant log. */
	private static final Logger log = LogManager.getLogger(RegionDAO.class);

	/**
	 * Gets the region.
	 *
	 * @param newLand the new land
	 * @return the region
	 */
	public Region getRegion(Land newLand) {
		
		Iterator<Player> iter1 = MyFactory.getPlayerDAO().getPlayers().iterator();  	
		while (iter1.hasNext()) {
			Player player = iter1.next();			
			
			Iterator<Region> iter2 = player.getRegion().iterator();  
			while (iter2.hasNext()) {
				Region region = iter2.next();
				
				Iterator<Land> iter3 = region.getLands().iterator();  
				while (iter3.hasNext()) {
					Land land = iter3.next();
					
					if (land.equals(newLand)) {
						return region;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Creates the start region.
	 *
	 * @param regionId the region id
	 * @param player the player
	 * @param pane the pane
	 * @return the region
	 */
	public Region createStartRegion(int regionId, Player player, Pane pane) {

		Region region = null;

		boolean done = false;
		while (!done) {

			boolean bad = false;

			int x = MyRandom.nextInt(Constants.SEGMENT_X);
			int y = MyRandom.nextInt(Constants.SEGMENT_Y);

			// Each start region must have two lands between each other)
			List<Land> list1 = MyFactory.getLandDAO().getNeigbors2(MyFactory.getLandDAO().getLands()[x][y]);
			Iterator<Land> iter1 = list1.iterator();
			while (iter1.hasNext()) {
				Land land2 = (Land) iter1.next();
				if (land2.getPlayer() != null) {
					bad = true;
				}
			}

			if (!bad) {

				Land land3 = MyFactory.getLandDAO().getLands()[x][y];
				if (land3.getType() == LandEnum.GRASS) {

					// Clain land
					land3.setPlayer(player);

					// Place Tower
					Soldier soldier3 = new Soldier(SoldierEnum.TOWER, player, land3);		
					land3.setSoldier(soldier3);					
					//log.info("New "+soldier3.getType()+" [x="+land3.getX()+"|y="+land3.getY()+"] created!");

					// Create new region
					region = new Region(player.getId(), player);
					region.getLands().add(land3);
					// log.info("New Region [id="+region.getId()+"|playerId="+player.getId()+"] created!");

					player.getRegion().add(region);

					// Add some more lands to region
					List<Land> list4 = MyFactory.getLandDAO().getBotNewLand(land3);
					for (int i=0; i<(MyData.getLands()-1); i++) {					
						Land land4 = MyRandom.nextLand(list4);
						if (land4 != null) {
							land4.setPlayer(player);
							region.getLands().add(land4);
							
							if (i==1) {
								Soldier soldier4 = new Soldier(SoldierEnum.PAWN, player, land4);
								if (player.getType()==PlayerEnum.HUMAN_LOCAL) {
									soldier4.setEnabled(true);
								}								
								land4.setSoldier(soldier4);
								//log.info("New "+soldier4.getType()+" [x="+land4.getX()+"|y="+land4.getY()+"|regionId="+region.getId()+"] created!");
							}							
						}
					}
					done = true;
				}
			}
		}
		return region;
	}

	/**
	 * Gets the tower position.
	 *
	 * @param region the region
	 * @return the tower position
	 */
	public Land getTowerPosition(Region region) {

		Iterator<Land> iter = region.getLands().iterator();
		while (iter.hasNext()) {
			Land land = (Land) iter.next();
			if ((land.getSoldier() != null) && (land.getSoldier().getType() == SoldierEnum.TOWER)) {

				// log.info("Tower ["+land.getX()+","+land.getY()+"]");
				return land;
			}
		}
		return null;
	}

	/**
	 * Search.
	 *
	 * @param land2 the land 2
	 * @param regionId the region id
	 */
	/* recursion land search */
	private void search(Land land2, int regionId) {

		land2.setRegion(regionId);

		List<Land> list = MyFactory.getLandDAO().getBotRegionLand(land2);
		Iterator<Land> iter = list.iterator();
		while (iter.hasNext()) {
			Land land = (Land) iter.next();
			search(land, regionId);
		}
	}

	/**
	 * Detected regions.
	 *
	 * @return the int
	 */
	public int detectedRegions() {

		int regionId = 0;
		
		log.debug("enter");
		
		// Reset all castles
		Land lands[][] = MyFactory.getLandDAO().getLands();
		for (int x = 0; x < Constants.SEGMENT_X; x++) {
			for (int y = 0; y < Constants.SEGMENT_Y; y++) {
				lands[x][y].setRegion(regionId);
			}
		}

		// Find them again
		for (int x = 0; x < Constants.SEGMENT_X; x++) {
			for (int y = 0; y < Constants.SEGMENT_Y; y++) {

				Land land = lands[x][y];
				if ((land.getType() != LandEnum.WATER) && (land.getType() != LandEnum.OCEAN) && (land.getRegion() == 0)
						&& (land.getPlayer() != null)) {
					regionId++;
					search(land, regionId);
				}
			}
		}

		log.debug("leave");
		return regionId;
	}

	/**
	 * Rebuild regions.
	 *
	 * @param amountOfRegions the amount of regions
	 */
	public void rebuildRegions(int amountOfRegions) {

		log.debug("enter");
		
		// Remove all region		
		log.debug("step1");
		Iterator<Player> iter1 = MyFactory.getPlayerDAO().getPlayers().iterator();
		while (iter1.hasNext()) {
			Player player = (Player) iter1.next();

			List<Region> list2 = player.getRegion();
			Iterator<Region> iter2 = list2.iterator();
			while (iter2.hasNext()) {
				@SuppressWarnings("unused")
				Region region = (Region) iter2.next();
				iter2.remove();
			}
		}

		Land lands[][] = MyFactory.getLandDAO().getLands();

		// Detect new regions with castle and assign them to player again
		log.debug("step2");
		for (int regionId = 1; regionId <= amountOfRegions; regionId++) {

			List<Land> list = new ArrayList<Land>();

			int foodAvailable = 0;
			int foodNeeded = 0;
			int castleCount = 0;
			for (int x = 0; x < Constants.SEGMENT_X; x++) {
				for (int y = 0; y < Constants.SEGMENT_Y; y++) {
					if (lands[x][y].getRegion() == regionId) {

						foodAvailable++;
						list.add(lands[x][y]);

						if (lands[x][y].getSoldier() != null) {

							foodNeeded += MyFactory.getSoldierDAO().food(lands[x][y].getSoldier().getType());
							//log.info("FoodNeeded="+foodNeeded+" ["+x+","+y+"]");
							if (lands[x][y].getSoldier().getType() == SoldierEnum.TOWER) {
								if (++castleCount > 1) {

									/*
									 * Remove castle if there are more one
									 * castle in one region
									 */
									lands[x][y].setSoldier(null);
								}
							}
						}
					}
				}
			}

			//log.info("check food [regionId="+regionId+" foodAvailable=" + foodAvailable + " foodNeeded="+foodNeeded+"]");
			log.debug("step3");
			if (foodAvailable < foodNeeded) {
				Iterator<Land> iter = list.iterator();
				while (iter.hasNext()) {
					Land land = (Land) iter.next();
					if ((land.getSoldier()!=null) && (land.getSoldier().getType()!=SoldierEnum.TOWER) && (land.getBuilding()==null)) {

						// No food, soldiers die
						land.getSoldier().setType(SoldierEnum.CROSS);
						land.getSoldier().setEnabled(false);
						
						MySound.play(land.getPlayer(), MySound.CLIP_DIE);								
					}
				}
			}

			// Create castle if land is bigger then 2 
			log.debug("step4");
			if ((castleCount == 0) && (list.size() > 2)) {

				boolean created=false;
				// Create new castle.
				while (!created) {
					Land land = MyRandom.nextLand(list);
					if ((land!=null) && (land.getBuilding()==null)) {
						if ((land.getSoldier()==null) || (land.getSoldier().getType()==SoldierEnum.CROSS)) {					
							//log.info("Castle created");
							Soldier soldier = new Soldier(SoldierEnum.TOWER, land.getPlayer(), land);
							land.setSoldier(soldier);
							created=true;
						}
					}
				}
			}

			// Castle is destroyed when region size is one.
			log.debug("step5");
			if (list.size() == 1) {
				Iterator<Land> iter = list.iterator();
				while (iter.hasNext()) {
					Land land = (Land) iter.next();
					if ((land.getSoldier() != null) && (land.getSoldier().getType() == SoldierEnum.TOWER)) {
						land.getSoldier().setType(SoldierEnum.CROSS);
						land.getSoldier().setEnabled(false);
						MySound.play(land.getPlayer(), MySound.CLIP_CASTLE);		
					}
				}
			}

			// Create new region and add it to player
			log.debug("step6");
			Iterator<Land> iter = list.iterator();
			if (iter.hasNext()) {
				Land land = (Land) iter.next();
				Player player = land.getPlayer();
				Region region = new Region(regionId, player);
				region.setLands(list);
				player.getRegion().add(region);
			}
		}
		log.debug("leave");
	}
}
