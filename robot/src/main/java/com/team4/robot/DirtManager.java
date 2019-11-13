package com.team4.robot;

import com.team4.commons.*;
import com.team4.sensor.*;

class DirtManager implements VacuumCleaner {

	private final int MAX_DIRT = Integer.parseInt(ConfigManager.getConfiguration("dirtCapacity"));

	private int dirtLevel;

	DirtManager() {
		dirtLevel = 0;
	}

	@Override
	public void clean(double cost) {
		int newDirtLevel = getDirtLevel() + 1;
		if(newDirtLevel > MAX_DIRT) {
			RobotCleanSweep.getInstance().setState(State.FULL_TANK);
			waitUntilTankEmpty();
		} else {
			setDirtLevel(newDirtLevel);
			RobotCleanSweep.getInstance().getPowerManager().updateBatteryLevel(cost);
			SensorSimulator.getInstance().setTileDone(RobotCleanSweep.getInstance().getLocation());
		}
	}

	@Override
	public int getDirtLevel() {
		return this.dirtLevel;
	}

	private void setDirtLevel(int dirt) {
		if(dirt < 0) {
			throw new RobotException("Invalid dirt level: " + dirt);
		}
		this.dirtLevel = dirt;
	}

	private void waitUntilTankEmpty() {
		//press any key to continue is better.
    	String dirtLevel = Integer.toString(RobotCleanSweep.getInstance().getVacuumCleaner().getDirtLevel());
    	String batteryLevel = Double.toString(RobotCleanSweep.getInstance().getPowerManager().getBatteryLevel());
    	for(int i =0; i<=4; i++) {
    		LogManager.logForUnity(RobotCleanSweep.getInstance().getLocation(), "DIRT_FULL",batteryLevel , dirtLevel, RobotCleanSweep.getNumberOfRuns());
    	}
    	
		System.out.println("----------  ---------  --------  ---------  ---------  ----------  --------------  -------------  ----------  ----------------------------\t------------------------");
		Utilities.doLoopedTimeDelay("...DIRT TANK FULL... (awaiting human intervention)...", 3, RobotCleanSweep.getInstance().getZeroTime());
		emptyTank();
	}

	private void emptyTank() {
		setDirtLevel(0);
		FloorDao floorDao = SensorSimulator.getInstance().getLocationInfo(RobotCleanSweep.getInstance().getLocation());
		
    	String dirtLevel = Integer.toString(RobotCleanSweep.getInstance().getVacuumCleaner().getDirtLevel());
    	String batteryLevel = Double.toString(RobotCleanSweep.getInstance().getPowerManager().getBatteryLevel());
    	clean(floorDao.floorType.getCost());
    	LogManager.logForUnity(RobotCleanSweep.getInstance().getLocation(), "DIRT_EMPTY",batteryLevel , dirtLevel, RobotCleanSweep.getNumberOfRuns());
		LogManager.print("...DIRT TANK EMPTY... (human action completed)...", RobotCleanSweep.getInstance().getZeroTime());
		System.out.println("----------  ---------  --------  ---------  ---------  ----------  --------------  -------------  ----------  ----------------------------\t------------------------");
		RobotCleanSweep.getInstance().setState(State.WORKING);
	}
}
