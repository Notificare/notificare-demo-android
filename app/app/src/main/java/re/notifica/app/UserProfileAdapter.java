package re.notifica.app;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class UserProfileAdapter extends BaseAdapter {

	private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;
    public UserProfileAdapter(Activity activity, ArrayList<HashMap<String, String>> userProfileList) {
        // TODO Auto-generated constructor stub
        this.activity = activity;
        this.data = userProfileList;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        View vi = convertView;
        if (vi == null) {
        	 vi = inflater.inflate(R.layout.row_user_profile, null);
        }
        TextView label = (TextView) vi.findViewById(R.id.item_label);
	    TextView value = (TextView) vi.findViewById(R.id.item_value);

        
        HashMap<String, String> itemHash = new HashMap<String, String>();
        itemHash = data.get(position);
 
		// Setting all values in listview
		label.setText(itemHash.get("label"));
		value.setText(itemHash.get("value"));

		vi.setTag(R.id.item_label, itemHash.get("label"));
		vi.setTag(R.id.item_value, itemHash.get("value"));

        return vi;
    }

}
