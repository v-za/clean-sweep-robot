package com.team4.robot;

import com.team4.commons.ConfigManager;
import com.team4.commons.Location;
import com.team4.commons.LogManager;
import com.team4.commons.RobotException;
import com.team4.commons.Utilities;

import static com.team4.commons.State.LOW_BATTERY;

class PowerUnit implements PowerManager {

    private double batteryLevel;


    PowerUnit() {
        int maxBatteryLevel = Integer.parseInt(ConfigManager.getConfiguration("maxBatteryLevel"));
        setBatteryLevel(maxBatteryLevel);
    }

    @Override
    public void recharge() {
        int timeToCharge = Integer.parseInt(ConfigManager.getConfiguration("timeToCharge"));
        Utilities.doLoopedTimeDelay("...CHARGING...", timeToCharge, RobotCleanSweep.getInstance().getZeroTime());
        int maxBatteryLevel = Integer.parseInt(ConfigManager.getConfiguration("maxBatteryLevel"));
        setBatteryLevel(maxBatteryLevel);
    }

    @Override
    public double getBatteryLevel() {
        return batteryLevel;
    }

    @Override
    public void updateBatteryLevel(double units) {
        if(units < 0 || units > 3) {
            throw new RobotException("Invalid power usage level.");
        }
        setBatteryLevel(getBatteryLevel() - units);
        double batteryNeededToReachToKnownChargingStation = 200;
        if(getBatteryLevel() <= 200) {
            for(Location chargingStation : RobotCleanSweep.getInstance().getChargingStations()) {

            	if(RobotCleanSweep.getInstance().getLocation().getX()==2 && RobotCleanSweep.getInstance().getLocation().getY()==6) {
            		//System.out.println(getBatteryLevel());
            		//System.out.println("HERE HERE HERE!!!!");
            	}
            	AStar aStar = new AStar(RobotCleanSweep.getInstance().getGraph(),RobotCleanSweep.getInstance().getLocation(),chargingStation ,2);
            	if(aStar.search()!=null) {
                	double temp = aStar.getPathNode().getMaxFloorCost() + 7.0;

                	if(temp <= batteryNeededToReachToKnownChargingStation) {
                		batteryNeededToReachToKnownChargingStation = temp;
                		RobotCleanSweep.getInstance().setCurrentChargingStation(chargingStation);
                	}
            	}
            }
        }

        if(getBatteryLevel() <= batteryNeededToReachToKnownChargingStation) {
        	
        	
        	String dirtLevel = Integer.toString(RobotCleanSweep.getInstance().getVacuumCleaner().getDirtLevel());
        	LogManager.logForUnity(RobotCleanSweep.getInstance().getLocation(), "GO_CHARGE", Double.toString(getBatteryLevel()), dirtLevel, RobotCleanSweep.getNumberOfRuns());
        	
        	LogManager.print("...GOING BACK TO CHARGING STATION. NOW AT " + RobotCleanSweep.getInstance().getLocation() + "  Battery Level: " + getBatteryLevel(), RobotCleanSweep.getInstance().getZeroTime());
            RobotCleanSweep.getInstance().setState(LOW_BATTERY);
        }
    }

    private void setBatteryLevel(double batteryLevel) {
        final int maxBatteryLevel = Integer.parseInt(ConfigManager.getConfiguration("maxBatteryLevel"));
        if(batteryLevel < 0 || batteryLevel > maxBatteryLevel) {
            throw new RobotException("Invalid battery level.");
        }
        this.batteryLevel = batteryLevel;
    }
}
