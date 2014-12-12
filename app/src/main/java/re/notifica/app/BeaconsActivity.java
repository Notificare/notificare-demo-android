package re.notifica.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.graphics.Typeface;

import java.util.List;

import re.notifica.Notificare;
import re.notifica.beacon.BeaconRangingListener;
import re.notifica.model.NotificareBeacon;
import re.notifica.push.gcm.BaseActivity;

public class BeaconsActivity extends BaseActivity implements BeaconRangingListener {

    private ListView listView;
    private BeaconListAdapter beaconListAdapter;
    protected static final String TAG = BeaconsActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beacons);
        listView = (ListView)findViewById(R.id.beaconsList);
        TextView emptyText = (TextView)findViewById(R.id.empty_message);
        listView.setEmptyView(emptyText);
        beaconListAdapter = new BeaconListAdapter(this, R.layout.beacon_list_cell);
        listView.setAdapter(beaconListAdapter);
        if (Notificare.shared().getBeaconClient() != null) {
            Notificare.shared().getBeaconClient().addRangingListener(this);
        }

    }

    @Override
    public void onRangingBeacons(final List<NotificareBeacon> notificareBeacons) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                beaconListAdapter.clear();
                for (NotificareBeacon beacon : notificareBeacons) {
                    beaconListAdapter.add(beacon);
                }
            }

        });
    }


}
