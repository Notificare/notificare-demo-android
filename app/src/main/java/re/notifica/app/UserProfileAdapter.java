package re.notifica.app;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class UserProfileAdapter extends BaseAdapter {

	private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private ArrayList<Integer> headers;
    private ArrayList<Integer> userCells;
    private static LayoutInflater inflater = null;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_SIMPLE = 2;


    public UserProfileAdapter(Activity activity, ArrayList<HashMap<String, String>> userProfileList, ArrayList<Integer> headersList, ArrayList<Integer> userCells) {
        this.activity = activity;
        this.data = userProfileList;
        this.headers = headersList;
        this.userCells = userCells;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getItemViewType(int position) {
        return headers.contains(position) ? TYPE_SEPARATOR : (userCells.contains(position)) ? TYPE_ITEM :TYPE_SIMPLE;
    }


    @Override
    public int getViewTypeCount() {
        return 3;
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
                    holder.name = (TextView) convertView.findViewById(R.id.item_name);
                    holder.email = (TextView) convertView.findViewById(R.id.item_email);
                    holder.token = (TextView) convertView.findViewById(R.id.item_token);
                    holder.name.setText(itemHash.get("name"));
                    holder.email.setText(itemHash.get("email"));
                    holder.token.setText(itemHash.get("token"));
                    holder.name.setTypeface(null, Typeface.BOLD);
                    break;
                case TYPE_SEPARATOR:
                    convertView = inflater.inflate(R.layout.row_header, null);
                    holder.name = (TextView) convertView.findViewById(R.id.item_label);
                    holder.name.setText(itemHash.get("label"));
                    //holder.name.setTypeface(null, Typeface.BOLD);


                    break;
                case TYPE_SIMPLE:
                    convertView = inflater.inflate(R.layout.row_segments, null);
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
        public TextView name;
        public TextView email;
        public TextView token;
    }

}
