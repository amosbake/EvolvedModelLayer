package mopel.io.evolvedmodellayer;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;
import java.util.List;
import java.util.Random;
import mopel.io.evolvedmodellayer.dao.BatteryContentProvider;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = "MainActivity";
  private TextView tvShow;
  private BriteContentResolver db;
  private Random random = new Random();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    AndroidThreeTen.init(this);
    tvShow = (TextView) findViewById(R.id.show);
    db = new SqlBrite.Builder().build().wrapContentProvider(getContentResolver(), Schedulers.io());
    db.setLoggingEnabled(true);
  }

  @Override protected void onStart() {
    super.onStart();
    db.createQuery(BatteryContentProvider.CONTENT_URI, null, "created_at Between ? and ?",
        new String[] { "2017-01-14T00:00:00", "2017-01-14T23:59:59" }, null, true)
        .mapToList(Battery.MAPPER)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<List<Battery>>() {
          @Override public void onCompleted() {
            Log.i(TAG, "completed");
          }

          @Override public void onError(Throwable e) {
            Log.e(TAG, "onError: ", e);
          }

          @Override public void onNext(List<Battery> batteries) {
            Log.i(TAG, "onNext: " + batteries.toString());
            tvShow.setText(batteries.toString());
          }
        });
    tvShow.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Battery.HEALTH, random.nextInt(100));
        contentValues.put(Battery.CREATED_AT,
            DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ZonedDateTime.now()));
        getContentResolver().insert(BatteryContentProvider.CONTENT_URI, contentValues);
      }
    });
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }
}
