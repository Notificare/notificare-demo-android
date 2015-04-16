package re.notifica.app;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import re.notifica.model.NotificareBeacon;

public class BeaconListAdapter extends ArrayAdapter<NotificareBeacon> {

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

        Typeface hairlineTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Hairline.ttf");
        Typeface myTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Regular.ttf");
        Typeface lightTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Light.ttf");


        TextView nameView = (TextView)rowView.findViewById(R.id.name);
        nameView.setTypeface(myTypeface);
        TextView messageView = (TextView)rowView.findViewById(R.id.message);
        messageView.setTypeface(lightTypeface);
        ImageView iconView = (ImageView)rowView.findViewById(R.id.icon);
        NotificareBeacon beacon = getItem(position);
        nameView.setText(beacon.getName());


        if (beacon.getNotification() != null) {
            messageView.setText(beacon.getNotification().getMessage());
        } else {
            messageView.setText("");
        }

        if (beacon.getCurrentProximity() == NotificareBeacon.PROXIMITY_IMMEDIATE) {
            iconView.setImageResource(R.drawable.signal_immediate);
        } else if (beacon.getCurrentProximity() == NotificareBeacon.PROXIMITY_NEAR) {
            iconView.setImageResource(R.drawable.signal_near);
        } else if (beacon.getCurrentProximity() == NotificareBeacon.PROXIMITY_FAR) {
            iconView.setImageResource(R.drawable.signal_far);
        } else if (beacon.getCurrentProximity() == NotificareBeacon.PROXIMITY_UNKNOWN) {
            iconView.setImageResource(R.drawable.signal_unkown);
        } else {
            iconView.setImageResource(R.drawable.signal_unkown);
        }
        return rowView;
    }

}
