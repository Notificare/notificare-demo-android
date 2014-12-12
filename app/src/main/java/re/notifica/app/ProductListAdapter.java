package re.notifica.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import re.notifica.Notificare;
import re.notifica.NotificareCallback;
import re.notifica.NotificareError;
import re.notifica.model.NotificareProduct;
import re.notifica.model.NotificareUserPreference;
import re.notifica.model.NotificareUserPreferenceOption;

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
