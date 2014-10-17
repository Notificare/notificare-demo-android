package re.notifica.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import re.notifica.model.NotificareUserPreference;

public class UserProfileAdapter extends BaseAdapter {

	private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private ArrayList<Integer> headers;
    private ArrayList<Integer> userCells;
    private ArrayList<Integer> segmentCells;
    private ArrayList<NotificareUserPreference> prefs;
    private static LayoutInflater inflater = null;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_SIMPLE = 2;
    private static final int TYPE_SINGLE = 3;
    private static final int TYPE_CHOICE = 4;
    private static final int TYPE_SELECT = 5;


    public UserProfileAdapter(Activity activity, ArrayList<HashMap<String, String>> userProfileList, ArrayList<NotificareUserPreference> prefs) {
        this.activity = activity;
        this.data = userProfileList;
        this.headers = new ArrayList<Integer>();
        this.userCells = new ArrayList<Integer>();
        this.segmentCells = new ArrayList<Integer>();

        this.prefs = prefs;

        for (int i=0; i < this.prefs.size(); i++) {
            segmentCells.add(i + 5);
        }

        headers.add(0);
        headers.add(4);
        userCells.add(1);

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getItemViewType(int position) {

        if(headers.contains(position)){
            return TYPE_SEPARATOR;
        } else {
            if(userCells.contains(position)){
                return TYPE_ITEM;
            } else {
                if(segmentCells.contains(position)){

                    if(this.prefs.get(position - 5).getPreferenceType().equals("single")){
                        return TYPE_SINGLE;
                    } else if (this.prefs.get(position - 5).getPreferenceType().equals("choice")){
                        return TYPE_CHOICE;
                    } else if (this.prefs.get(position - 5).getPreferenceType().equals("select")){
                        return TYPE_SELECT;
                    } else {
                        return TYPE_SINGLE;
                    }


                } else {
                    return TYPE_SIMPLE;
                }
            }
        }

    }


    @Override
    public int getViewTypeCount() {
        return 6;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        HashMap<String, String> itemHash = new HashMap<String, String>();
        itemHash = data.get(position);
        ViewHolder holder = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = inflater.inflate(R.layout.row_user_profile, null);
                    holder.icon = (ImageView) convertView.findViewById(R.id.item_icon);
                    holder.name = (TextView) convertView.findViewById(R.id.item_name);
                    holder.email = (TextView) convertView.findViewById(R.id.item_email);
                    holder.token = (TextView) convertView.findViewById(R.id.item_token);
                    holder.name.setText(itemHash.get("name"));
                    holder.email.setText(itemHash.get("email"));
                    holder.token.setText(itemHash.get("token"));
                    holder.name.setTypeface(null, Typeface.BOLD);

                    String url = "/" + md5(itemHash.get("email").trim().toLowerCase()) + "?s=160";

                    final ViewHolder finalHolder = holder;
                    GravatarHandler.get(url, null, new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            Bitmap image = BitmapFactory.decodeByteArray(responseBody, 0, responseBody.length);
                            finalHolder.icon.setImageBitmap(image);

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
                     break;
                case TYPE_SEPARATOR:
                    convertView = inflater.inflate(R.layout.row_header, null);
                    holder.name = (TextView) convertView.findViewById(R.id.item_label);
                    holder.name.setText(itemHash.get("label"));

                    break;
                case TYPE_SIMPLE:
                    convertView = inflater.inflate(R.layout.row_user_profile_simple, null);
                    holder.name = (TextView) convertView.findViewById(R.id.item_label);
                    holder.name.setText(itemHash.get("label"));
                    holder.name.setTypeface(null, Typeface.BOLD);
                    break;

                case TYPE_SINGLE:
                    convertView = inflater.inflate(R.layout.row_segment_single, null);
                    holder.name = (TextView) convertView.findViewById(R.id.item_label);
                    holder.name.setText(itemHash.get("label"));
                    holder.name.setTypeface(null, Typeface.BOLD);
                    break;

                case TYPE_CHOICE:
                    convertView = inflater.inflate(R.layout.row_segment_choice, null);
                    holder.label = (TextView) convertView.findViewById(R.id.item_label);
                    holder.name = (TextView) convertView.findViewById(R.id.item_name);
                    holder.label.setText(itemHash.get("label"));
                    holder.name.setText(itemHash.get("name"));
                    holder.label.setTypeface(null, Typeface.BOLD);
                    break;

                case TYPE_SELECT:
                    convertView = inflater.inflate(R.layout.row_segment_select, null);
                    holder.name = (TextView) convertView.findViewById(R.id.item_label);
                    holder.name.setText(itemHash.get("label"));
                    holder.name.setTypeface(null, Typeface.BOLD);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        return convertView;
    }

    public static class ViewHolder {
        public ImageView icon;
        public TextView name;
        public TextView email;
        public TextView token;
        public TextView label;
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
