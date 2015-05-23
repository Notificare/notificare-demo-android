package re.notifica.app;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import re.notifica.Notificare;
import re.notifica.beacon.BeaconRangingListener;
import re.notifica.model.NotificareBeacon;


public class BeaconsFragment extends Fragment implements BeaconRangingListener {
    public static final String ARG_NAVIGATION_NUMBER = "navigation_pos";
    public BeaconListAdapter beaconListAdapter;
    protected static final String TAG = BeaconsFragment.class.getSimpleName();

    public BeaconsFragment() {
        // Empty constructor required for fragment subclasses

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_beacons, container, false);
        final int i = getArguments().getInt(ARG_NAVIGATION_NUMBER);
        String label = getResources().getStringArray(R.array.navigation_labels)[i];
        String url = getResources().getStringArray(R.array.navigation_urls)[i];

        final ListView listView =  (ListView) rootView.findViewById(R.id.beaconsList);

        if (Notificare.shared().getBeaconClient() != null) {

            Notificare.shared().getBeaconClient().addRangingListener(this);
        }

        beaconListAdapter = new BeaconListAdapter(getActivity(), R.layout.beacon_list_cell);
        listView.setAdapter(beaconListAdapter);

        TextView emptyText = (TextView)rootView.findViewById(R.id.empty_message);
        listView.setEmptyView(emptyText);


        getActivity().setTitle(label);
        return rootView;
    }

    public void onDestroyView (){

        super.onDestroyView();

        if (Notificare.shared().getBeaconClient() != null) {
            Notificare.shared().getBeaconClient().removeRangingListener(this);
        }
    }


    @Override
    public void onRangingBeacons(final List<NotificareBeacon> notificareBeacons) {

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                beaconListAdapter.clear();

                for (NotificareBeacon beacon : notificareBeacons) {
                    Log.i(TAG, beacon.getName());
                    beaconListAdapter.add(beacon);
                }
            }

        });


    }
}