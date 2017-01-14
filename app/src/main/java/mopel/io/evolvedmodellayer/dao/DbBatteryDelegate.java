package mopel.io.evolvedmodellayer.dao;

import java.util.List;
import mopel.io.evolvedmodellayer.Battery;
import rx.Observable;

/**
 * Author: mopel
 * Date : 17/1/14
 */

public interface DbBatteryDelegate {
  void saveBatteryInfo(Battery battery);

  Observable<List<Battery>> getDailyBatteryHistory(int year, int month, int day);
}