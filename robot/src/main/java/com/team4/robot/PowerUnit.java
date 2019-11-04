package com.team4.robot;

import com.team4.commons.ConfigManager;
import com.team4.commons.Direction;
import com.team4.commons.Location;
import com.team4.commons.LocationFactory;
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

        //battery needed to reach Charging station
        AStar aStar = new AStar(RobotCleanSweep.getInstance().getGraph(),RobotCleanSweep.getInstance().getLocation(),LocationFactory.createLocation(0, 9) ,2);
        //searching path from current location to charging station
        
        double batteryNeededToReachToKnownChargingStation;

        //additional 3.0 for saftey, just allowing robot one more move.
        	aStar.search();
        	batteryNeededToReachToKnownChargingStation  = aStar.getPathNode().getMaxFloorCost() + 3.0;
      

        //double batteryNeededToReachToKnownChargingStation = 40;
        if(getBatteryLevel()<=batteryNeededToReachToKnownChargingStation )
        if(getBatteryLevel() <= batteryNeededToReachToKnownChargingStation) {
        	
        	System.out.println();
        	System.out.println("CURRENT LOCATION: " + RobotCleanSweep.getInstance().getLocation() + " BATTERY :" + RobotCleanSweep.getInstance().getPowerManager().getBatteryLevel());
            LogManager.print("...GOING BACK TO CHARGING STATION...", RobotCleanSweep.getInstance().getZeroTime());
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
