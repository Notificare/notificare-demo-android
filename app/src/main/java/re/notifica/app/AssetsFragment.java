package re.notifica.app;


import android.app.DownloadManager;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import re.notifica.Notificare;
import re.notifica.NotificareCallback;
import re.notifica.NotificareError;
import re.notifica.model.NotificareAsset;


/**
 * A simple {@link Fragment} subclass.
 */
public class AssetsFragment extends Fragment {

    private static final String TAG = AssetsFragment.class.getSimpleName();

    private MenuItem mSearchMenuItem;
    private SearchView mSearchView;
    private AssetsAdapter mAdapter;

    private boolean mHasStoragePermission;

    public AssetsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_assets, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.list);
        View emptyView = rootView.findViewById(R.id.empty_message);

        mAdapter = new AssetsAdapter(getActivity());
        listView.setAdapter(mAdapter);
        listView.setEmptyView(emptyView);

        mHasStoragePermission = isStoragePermissionGranted();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_assets, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        mSearchMenuItem = menu.findItem(R.id.search);
        Drawable searchIcon = mSearchMenuItem.getIcon().mutate();
        searchIcon.setColorFilter(new LightingColorFilter(0x000000, 0xFFFFFF));
        mSearchMenuItem.setIcon(searchIcon);

        mSearchView = (SearchView) mSearchMenuItem.getActionView();
        mSearchView.setQueryHint(getString(R.string.hint_search_assets));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchView.clearFocus();
                mSearchMenuItem.collapseActionView();
                mAdapter.clear();

                Notificare.shared().fetchAssets(query, new NotificareCallback<List<NotificareAsset>>() {
                    @Override
                    public void onSuccess(List<NotificareAsset> notificareAssets) {
                        Log.i(AssetsFragment.class.getSimpleName(), String.format("Found %d assets", notificareAssets.size()));
                        mAdapter.addAll(notificareAssets);
                    }

                    @Override
                    public void onError(NotificareError notificareError) {
                        Log.e(AssetsFragment.class.getSimpleName(), "Error fetching assets", notificareError);
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.app_name)
                                .setMessage(notificareError.getMessage())
                                .setCancelable(false)
                                .setPositiveButton("OK", null)
                                .show();
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }


    class AssetsAdapter extends ArrayAdapter<NotificareAsset> {

        private LayoutInflater mInflater;

        public AssetsAdapter(Context context) {
            this(context, new ArrayList<NotificareAsset>());
        }

        public AssetsAdapter(Context context, List<NotificareAsset> items) {
            super(context, 0, items);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = mInflater.inflate(R.layout.row_asset, parent, false);
            NotificareAsset notificareAsset = getItem(position);

            AppCompatImageView imageView = (AppCompatImageView) convertView.findViewById(R.id.asset_image);
            if (notificareAsset.getKey() == null) {
                imageView.setVisibility(View.GONE);
            } else {
                if (notificareAsset.getContentType() != null &&
                        (notificareAsset.getContentType().equals("image/jpeg") ||
                                notificareAsset.getContentType().equals("image/gif") ||
                                notificareAsset.getContentType().equals("image/png"))) {
                    Ion.with(imageView)
                            .load("GET", Notificare.shared().getPushApiBaseUrl() + "/asset/file/" + notificareAsset.getKey());
                } else {
                    imageView.setBackgroundResource(R.drawable.ic_circle_black);
                    imageView.setSupportBackgroundTintList(ColorStateList.valueOf(0xF5F5F5));

                    Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_attachment_black_24dp);
                    drawable.setColorFilter(new LightingColorFilter(0x000000, 0x757575));
                    imageView.setBackgroundDrawable(drawable);
                }
            }

            ((TextView) convertView.findViewById(R.id.asset_title)).setText(notificareAsset.getTitle());
            ((TextView) convertView.findViewById(R.id.asset_description)).setText(Html.fromHtml(notificareAsset.getDescription()));

            convertView.setClickable(false);
            convertView.setTag(notificareAsset);

            if (mHasStoragePermission) {
                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        NotificareAsset asset = (NotificareAsset) v.getTag();

                        if (asset.getKey() != null) {
                            String url = Notificare.shared().getPushApiBaseUrl() + "/asset/file/" + asset.getKey();

                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                            request.setTitle(asset.getTitle());
                            request.setDescription(Html.fromHtml(asset.getDescription()));
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, asset.getOriginalFileName());

                            DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                            manager.enqueue(request);
                        }

                        return true;
                    }
                });
            } else {
                convertView.setLongClickable(false);
            }


            return convertView;
        }
    }

}
