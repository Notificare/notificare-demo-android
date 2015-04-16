package re.notifica.app;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BlankFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
/**
 * Fragment with a MainView
 */
public class MainFragment extends Fragment {
    public static final String ARG_NAVIGATION_NUMBER = "navigation_pos";


    public MainFragment() {
        // Empty constructor required for fragment subclasses

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final int i = getArguments().getInt(ARG_NAVIGATION_NUMBER);
        String label = getResources().getStringArray(R.array.navigation_labels)[i];
        String url = getResources().getStringArray(R.array.navigation_urls)[i];

        Button buttonDashboard = (Button) rootView.findViewById(R.id.buttonDashboard);
        ImageButton mailButton = (ImageButton) rootView.findViewById(R.id.mailButton);
        ImageButton facebookButton = (ImageButton) rootView.findViewById(R.id.facebookButton);
        ImageButton twitterButton = (ImageButton) rootView.findViewById(R.id.twitterButton);
        TextView titleText = (TextView) rootView.findViewById(R.id.titleText);
        TextView infoText = (TextView) rootView.findViewById(R.id.infoText);
        Typeface lightFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Light.ttf");
        Typeface regularFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Regular.ttf");
        titleText.setTypeface(regularFont);
        infoText.setTypeface(lightFont);
        buttonDashboard.setTypeface(lightFont);




        buttonDashboard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://dashboard.notifica.re")));
            }

        });


        mailButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, "support@notifica.re");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Android Demo App");
                intent.putExtra(Intent.EXTRA_TEXT, "Type some text");
                intent.setData(Uri.parse("mailto:"));
                intent.setType("text/plain");
                startActivity(intent);
            }

        });


        facebookButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                try {
                    getActivity().getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/392549870780732")));
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/notificare")));
                }

            }

        });

        twitterButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=notificare")));
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/notificare")));
                }

            }

        });

        getActivity().setTitle(label);
        return rootView;
    }
}