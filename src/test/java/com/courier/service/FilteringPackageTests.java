package com.courier.service;

import com.courier.model.Package;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.courier.service.VehicleService.shipPackages;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilteringPackageTests {

    PackageService packageService = new PackageService();
    static double maximumCarryingWeight;

    @Test
    void shouldFilterMorePackagesThenMoreWeights() {
        Package[] packages = new Package[5];
        packages[0] = (new Package("PKG1", 50, 30, "OFR001"));
        packages[1] = ((new Package("PKG2", 75, 125, "OFR008")));
        packages[2] = (new Package("PKG3", 175, 100, "OFR003"));
        packages[3] = (new Package("PKG4", 110, 60, "OFR002"));
        packages[4] = (new Package("PKG5", 155, 95, "NA"));
        maximumCarryingWeight = 200;
        String[] packageIds = {"PKG4", "PKG2", "PKG3", "PKG5", "PKG1"};
        List<Package> packageList = new ArrayList<>(Arrays.asList(packages));
        List<String> filteredPackageIds = new ArrayList<>();
        while (!packageList.isEmpty()) {
            List<Package> shippingPackagesList = packageService.filterPackages(packageList, maximumCarryingWeight);
            for (Package aPackage : shippingPackagesList) {
                filteredPackageIds.add(aPackage.getId());
                packageList.removeIf(p -> p.getId() == aPackage.getId());
            }
        }
        assertEquals(Arrays.asList(packageIds), filteredPackageIds);

    }

    @Test
    void testDeliveryEstimationTimeDifferentWeights() {
        Package[] packages = new Package[5];
        packages[0] = (new Package("PKG1", 50, 30, "OFR001"));
        packages[1] = ((new Package("PKG2", 75, 125, "OFR008")));
        packages[2] = (new Package("PKG3", 175, 100, "OFR003"));
        packages[3] = (new Package("PKG4", 110, 60, "OFR002"));
        packages[4] = (new Package("PKG5", 155, 95, "NA"));

        int numberOfVehicles = 2;
        int maxSpeed = 70;
        int maximumCarryingWeight = 200;

        double currentTime = 0;
        double pkg4Time = packages[3].getDistance() / maxSpeed;
        double pkg2Time = packages[1].getDistance() / maxSpeed;
        double pkg3Time = packages[2].getDistance() / maxSpeed;
        currentTime = 2 * pkg3Time;
        double pkg5Time = currentTime + packages[4].getDistance() / maxSpeed;
        currentTime = 2 * pkg2Time;
        double pkg1Time = currentTime + packages[0].getDistance() / maxSpeed;

        Double[] estimationTimings = {pkg1Time, pkg2Time, pkg3Time, pkg4Time, pkg5Time};

        shipPackages(packages, numberOfVehicles, maxSpeed, maximumCarryingWeight);
        for (int i = 0; i < packages.length; i++) {
            assertEquals(estimationTimings[i], packages[i].getEstimatedDeliveryTime(), 0.025);
        }
    }

    @Test
    void testDeliveryEstimationTimeEqualWeights() {
        Package[] packages = new Package[3];
        packages[0] = (new Package("PKG1", 50, 50, "OFR001"));
        packages[1] = ((new Package("PKG2", 50, 120, "OFR008")));
        packages[2] = (new Package("PKG3", 50, 100, "OFR003"));
        int numberOfVehicles = 1;
        int maxSpeed = 70;
        int maximumCarryingWeight = 60;

        double pkg1Time = packages[0].getDistance() / maxSpeed;
        double currentTime = 2 * pkg1Time;
        double pkg3Time = currentTime + packages[2].getDistance() / maxSpeed;
        currentTime = currentTime + 2 * packages[2].getDistance() / maxSpeed;
        double pkg2Time = currentTime + packages[1].getDistance() / maxSpeed;

        Double[] estimationTimings = {pkg1Time, pkg2Time, pkg3Time};

        shipPackages(packages, numberOfVehicles, maxSpeed, maximumCarryingWeight);
        for (int i = 0; i < packages.length; i++) {
            assertEquals(estimationTimings[i], packages[i].getEstimatedDeliveryTime(), 0.025);
        }
    }

    @Test
    void shouldFilterLeastDistanceIfEqualWeights() {
        List<Package> packages = new ArrayList<>();
        packages.add(new Package("PKG1", 50, 50));
        packages.add(new Package("PKG2", 50, 120));
        packages.add(new Package("PKG3", 50, 100));
        maximumCarryingWeight = 130;
        String[] packageIds = {"PKG1", "PKG3", "PKG2"};
        List<String> filteredPackageIds = new ArrayList<>();
        while (!packages.isEmpty()) {
            List<Package> packageList = packageService.filterPackages(packages, maximumCarryingWeight);
            for (Package aPackage : packageList) {
                filteredPackageIds.add(aPackage.getId());
                packages.removeIf(p -> p.getId() == aPackage.getId());
            }
        }
        assertEquals(Arrays.asList(packageIds), filteredPackageIds);
    }


}
