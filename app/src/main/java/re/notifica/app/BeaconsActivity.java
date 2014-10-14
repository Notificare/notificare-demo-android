package re.notifica.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import re.notifica.Notificare;
import re.notifica.beacon.BeaconRangingListener;
import re.notifica.model.NotificareBeacon;
import re.notifica.push.gcm.BaseActivity;

public class BeaconsActivity extends BaseActivity implements BeaconRangingListener {

    private ListView listView;
    private BeaconListAdapter beaconListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beacons);
        listView = (ListView)findViewById(R.id.beaconsList);
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

    private class BeaconListAdapter extends ArrayAdapter<NotificareBeacon> {

        private Activity context;
        private int resource;


        public BeaconListAdapter(Activity context, int resource) {
            super(context, resource);
            this.context = context;
            this.resource = resource;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = convertView;
            if (rowView == null) {
                rowView = inflater.inflate(resource, null, true);
            }
            TextView nameView = (TextView)rowView.findViewById(R.id.name);
            TextView messageView = (TextView)rowView.findViewById(R.id.message);
            ImageView iconView = (ImageView)rowView.findViewById(R.id.icon);
            NotificareBeacon beacon = getItem(position);
            nameView.setText(beacon.getName());
            if (beacon.getNotification() != null) {
                messageView.setText(beacon.getNotification().getMessage());
            } else {
                messageView.setText("");
            }
            if (beacon.getBeaconData() != null) {
                  iconView.setContentDescription(String.format("%.2f", getItem(position).getBeaconData().getDistance()));
            } else {
                  iconView.setContentDescription("n/a");
            }
            return rowView;
        }

    }
}
