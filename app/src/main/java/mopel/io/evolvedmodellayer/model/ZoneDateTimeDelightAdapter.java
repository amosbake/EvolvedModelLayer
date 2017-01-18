package mopel.io.evolvedmodellayer.model;

import android.support.annotation.NonNull;
import com.squareup.sqldelight.ColumnAdapter;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * Author: mopel
 * Date : 17/1/14
 */
public class ZoneDateTimeDelightAdapter implements ColumnAdapter<LocalDateTime, String> {
  private final DateTimeFormatter mDateTimeFormatter;

  public ZoneDateTimeDelightAdapter(DateTimeFormatter mDateTimeFormatter) {
    this.mDateTimeFormatter = mDateTimeFormatter;
  }

  @NonNull @Override public LocalDateTime decode(String databaseValue) {
    return LocalDateTime.parse(databaseValue, mDateTimeFormatter);
  }

  @Override public String encode(@NonNull LocalDateTime value) {
    return mDateTimeFormatter.format(value);
  }
}
