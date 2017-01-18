package mopel.io.evolvedmodellayer.model;

import android.database.Cursor;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import mopel.io.BatteryModel;
import mopel.io.evolvedmodellayer.ZoneDateTimeDelightAdapter;
import mopel.io.evolvedmodellayer.dao.Db;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import rx.functions.Func1;

/**
 * Author: mopel
 * Date : 17/1/14
 */
@AutoValue public abstract class Battery implements BatteryModel, Parcelable {
  private static final ZoneDateTimeDelightAdapter TIME_DELIGHT_ADAPTER =
      new ZoneDateTimeDelightAdapter(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

  public static final Factory<Battery> BATTERY_FACTORY =
      new Factory<>(new BatteryModel.Creator<Battery>() {
        @Override
        public Battery create(@Nullable Long id, long health, @NonNull LocalDateTime created_at) {
          return new AutoValue_Battery(id, health, created_at);
        }
      }, TIME_DELIGHT_ADAPTER);

  public static Func1<Cursor, Battery> MAPPER = new Func1<Cursor, Battery>() {
    @Override public Battery call(Cursor cursor) {
      long id = Db.getLong(cursor, Battery.ID);
      String createTime = Db.getString(cursor, Battery.CREATED_AT);
      int health = Db.getInt(cursor, Battery.HEALTH);
      return new AutoValue_Battery(id, health,
          LocalDateTime.parse(createTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
  };
}
