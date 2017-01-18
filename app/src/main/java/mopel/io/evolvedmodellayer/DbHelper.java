package mopel.io.evolvedmodellayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import mopel.io.BatteryModel;
import mopel.io.evolvedmodellayer.model.Battery;
import org.threeten.bp.LocalDateTime;

/**
 * Author: mopel
 * Date : 17/1/14
 */
public class DbHelper extends SQLiteOpenHelper {
  private static final int VERSION = 1;
  private static final String DB_NAME = "powersaver";

  private static DbHelper instance;

  public DbHelper(Context context) {
    super(context, DB_NAME, null, VERSION);
  }

  public static DbHelper getInstance(Context context) {
    if (instance == null) {
      instance = new DbHelper(context);
    }
    return instance;
  }

  @Override public void onCreate(SQLiteDatabase db) {
    db.execSQL(Battery.CREATE_TABLE);
    BatteryModel.Insert_battery insertBattery =
        new BatteryModel.Insert_battery(db, Battery.BATTERY_FACTORY);
    insertBattery.bind(100, LocalDateTime.of(2017, 1, 10, 5, 30));
    insertBattery.program.executeInsert();
    insertBattery.bind(90, LocalDateTime.of(2017, 1, 10, 6, 30));
    insertBattery.program.executeInsert();
    insertBattery.bind(70, LocalDateTime.of(2017, 1, 10, 7, 30));
    insertBattery.program.executeInsert();
    insertBattery.bind(50, LocalDateTime.of(2017, 1, 10, 8, 30));
    insertBattery.program.executeInsert();
    insertBattery.bind(30, LocalDateTime.of(2017, 1, 10, 9, 30));
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }
}
