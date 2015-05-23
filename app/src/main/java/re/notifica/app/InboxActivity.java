package re.notifica.app;

import re.notifica.Notificare;
import re.notifica.model.NotificareInboxItem;
import re.notifica.push.gcm.BaseActivity;
import re.notifica.support.v7.app.ActionBarBaseActivity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class InboxActivity extends ActionBarBaseActivity {

    private ListView listView;
    private ArrayList<Integer> itemsToRemove;
    protected ArrayAdapter<NotificareInboxItem> inboxListAdapter;
    private ActionMode mActionMode;
    protected static final String TAG = InboxActivity.class.getSimpleName();

    private Typeface lightFont;
    private Typeface regularFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        listView = (ListView)findViewById(R.id.inboxList);

        Typeface lightFont = Typeface.createFromAsset(this.getAssets(), "fonts/Lato-Light.ttf");
        Typeface regularFont = Typeface.createFromAsset(this.getAssets(), "fonts/Lato-Regular.ttf");

        itemsToRemove = new ArrayList<Integer>();
        inboxListAdapter = new InboxListAdapter(this, R.layout.inbox_list_cell);
        listView.setAdapter(inboxListAdapter);

        TextView emptyText = (TextView)findViewById(R.id.empty_message);
        listView.setEmptyView(emptyText);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if(view.findViewById(R.id.inbox_delete).getVisibility() == View.VISIBLE){
                    //uncheck
                    view.findViewById(R.id.inbox_delete).setVisibility(View.INVISIBLE);
                    itemsToRemove.remove(new Integer(position));

                } else {
                    //check
                    itemsToRemove.add(position);
                    view.findViewById(R.id.inbox_delete).setVisibility(View.VISIBLE);
                }

                if (mActionMode != null) {
                    return true;
                }

                // Start the CAB using the ActionMode.Callback defined above
                mActionMode = startActionMode(mActionModeCallback);
                view.setSelected(true);
                return true;
            }
        });


        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotificareInboxItem item = inboxListAdapter.getItem(position);
				Notificare.shared().getInboxManager().markItem(item);
				Notificare.shared().openNotification(InboxActivity.this, item.getNotification());
            }
        });




    }


    @Override
    protected void onResume() {
        super.onResume();
        inboxListAdapter.clear();
        if (Notificare.shared().getInboxManager() != null) {
            for (NotificareInboxItem item : Notificare.shared().getInboxManager().getItems()) {
                inboxListAdapter.add(item);
            }
        }
    }

    /**
     * List adapter to show a row per beacon
     */
    public class InboxListAdapter extends ArrayAdapter<NotificareInboxItem> {

        private Activity context;
        private int resource;

        public InboxListAdapter(Activity context, int resource) {
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
            TextView dateView = (TextView)rowView.findViewById(R.id.inbox_date);
            TextView messageView = (TextView)rowView.findViewById(R.id.inbox_message);
            dateView.setTypeface(regularFont);
            messageView.setTypeface(lightFont);
            NotificareInboxItem item = getItem(position);

            dateView.setText(DateUtils.getRelativeTimeSpanString(item.getTimestamp().getTime(), new Date().getTime(), DateUtils.SECOND_IN_MILLIS));
            messageView.setText(item.getNotification().getMessage());
            dateView.setTextColor(Color.BLACK);
            messageView.setTextColor(Color.BLACK);
            if(item.getStatus()){
                dateView.setTextColor(Color.GRAY);
                messageView.setTextColor(Color.GRAY);
            }
            return rowView;
        }
    }


    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.inbox, menu);

            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_trash:

                    for(Integer position : itemsToRemove){
                        NotificareInboxItem msg = inboxListAdapter.getItem(position);
                        Notificare.shared().getInboxManager().removeItem(msg);
                        inboxListAdapter.remove(msg);
                    }

                    itemsToRemove.removeAll(itemsToRemove);
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            for(Integer position : itemsToRemove){
                listView.getChildAt(position).findViewById(R.id.inbox_delete).setVisibility(View.INVISIBLE);
            }
            itemsToRemove.removeAll(itemsToRemove);
            mActionMode = null;
        }
    };


}
