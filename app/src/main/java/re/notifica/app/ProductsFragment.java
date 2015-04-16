package re.notifica.app;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import re.notifica.model.NotificareProduct;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProductsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductsFragment extends Fragment {
    public static final String ARG_NAVIGATION_NUMBER = "navigation_pos";
    protected ArrayAdapter<NotificareProduct> productListAdapter;
    //public ListView listView;

    public ProductsFragment() {
        // Empty constructor required for fragment subclasses

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_products, container, false);
        final int i = getArguments().getInt(ARG_NAVIGATION_NUMBER);
        String label = getResources().getStringArray(R.array.navigation_labels)[i];
        String url = getResources().getStringArray(R.array.navigation_urls)[i];

        final ListView listView =  (ListView) rootView.findViewById(R.id.productList);

        productListAdapter = new ProductListAdapter(getActivity(), R.layout.product_list_cell);
        listView.setAdapter(productListAdapter);

        TextView emptyText = (TextView)rootView.findViewById(R.id.empty_message);
        listView.setEmptyView(emptyText);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               NotificareProduct item = productListAdapter.getItem(position);
                //Notificare.shared().getBillingManager().launchPurchaseFlow(ProductsActivity.this, item, ProductsActivity.this);
            }
        });
        getActivity().setTitle(label);
        return rootView;
    }

}
