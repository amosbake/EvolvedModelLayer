package mopel.io.evolvedmodellayer.model;

import java.util.Comparator;

/**
 * Author: mopel
 * Date : 2017/1/18
 */
public class BatteyTimeCompartor implements Comparator<Battery> {

    @Override
    public int compare(Battery o1, Battery o2) {
        return o1.getMinuteOfDay() - o2.getMinuteOfDay();
    }
}
