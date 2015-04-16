package re.notifica.app;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import re.notifica.model.NotificareProduct;

/**
 * List adapter to show a row per product
 */
public class ProductListAdapter extends ArrayAdapter<NotificareProduct> {

    private Activity context;
    private int resource;

    public ProductListAdapter(Activity context, int resource) {
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
        TextView titleView = (TextView)rowView.findViewById(R.id.product_title);
        TextView messageView = (TextView)rowView.findViewById(R.id.product_message);
        NotificareProduct item = getItem(position);

        titleView.setText(item.getName());
        messageView.setText(item.getSkuDetails().getPrice());
        titleView.setTextColor(Color.BLACK);
        messageView.setTextColor(Color.BLACK);
        return rowView;
    }
}
