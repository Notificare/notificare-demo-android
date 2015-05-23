package re.notifica.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import re.notifica.Notificare;
import re.notifica.NotificareError;
import re.notifica.billing.BillingManager;
import re.notifica.billing.BillingResult;
import re.notifica.billing.Purchase;
import re.notifica.model.NotificareProduct;
import re.notifica.push.gcm.BaseActivity;

public class ProductsActivity extends AppCompatActivity implements Notificare.OnBillingReadyListener, BillingManager.OnRefreshFinishedListener, BillingManager.OnPurchaseFinishedListener {

    private ListView listView;
    private Boolean inProgress = false;
    protected ArrayAdapter<NotificareProduct> productListAdapter;
    protected static final String TAG = ProductsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        listView = (ListView)findViewById(R.id.productList);
        productListAdapter = new ProductListAdapter(this, R.layout.product_list_cell);
        listView.setAdapter(productListAdapter);

        TextView emptyText = (TextView)findViewById(R.id.empty_message);
        listView.setEmptyView(emptyText);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotificareProduct item = productListAdapter.getItem(position);
                Notificare.shared().getBillingManager().launchPurchaseFlow(ProductsActivity.this, item, ProductsActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Notificare.shared().getBillingManager().handleActivityResult(requestCode, resultCode, data)) {
            // Billingmanager handled the result
            inProgress = true; // wait for purchase to finish before doing other calls
        } else {
            // Something else came back to us
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Notificare.shared().addBillingReadyListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Notificare.shared().removeBillingReadyListener(this);
    }

    @Override
    public void onBillingReady() {
        if (!inProgress) {
            productListAdapter.clear();
            Notificare.shared().getBillingManager().refresh(this);
        }
    }

    @Override
    public void onPurchaseFinished(BillingResult billingResult, Purchase purchase) {
        inProgress = false;
        productListAdapter.clear();
        Notificare.shared().getBillingManager().refresh(this);
    }

    @Override
    public void onRefreshFinished() {
        productListAdapter.addAll(Notificare.shared().getBillingManager().getProducts());
    }

    @Override
    public void onRefreshFailed(NotificareError notificareError) {
        Toast.makeText(this, "billing refresh failed: " + notificareError.getMessage(), Toast.LENGTH_LONG).show();
    }


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

}
