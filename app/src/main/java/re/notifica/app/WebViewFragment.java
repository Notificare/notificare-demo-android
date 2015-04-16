package re.notifica.app;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WebViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WebViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
/**
 * Fragment with a WebView
 */
public class WebViewFragment extends Fragment {
    public static final String ARG_NAVIGATION_NUMBER = "navigation_pos";

    public WebViewFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_webview, container, false);
        int i = getArguments().getInt(ARG_NAVIGATION_NUMBER);
        String label = getResources().getStringArray(R.array.navigation_labels)[i];
        String url = getResources().getStringArray(R.array.navigation_urls)[i];

        WebView webView =  (WebView) rootView.findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:window.scrollTo(0.0, 100.0);");
            }
        });
        getActivity().setTitle(label);
        return rootView;
    }
}