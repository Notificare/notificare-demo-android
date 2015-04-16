package re.notifica.app;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import re.notifica.Notificare;
import re.notifica.NotificareCallback;
import re.notifica.NotificareError;
import re.notifica.model.NotificareUser;
import re.notifica.model.NotificareUserPreference;
import re.notifica.model.NotificareUserPreferenceOption;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
/**
 * Fragment with a User profile
 */
public class UserProfileFragment extends Fragment {
    public static final String ARG_NAVIGATION_NUMBER = "navigation_pos";

    public UserProfileFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        int i = getArguments().getInt(ARG_NAVIGATION_NUMBER);
        String label = getResources().getStringArray(R.array.navigation_labels)[i];
        //String url = getResources().getStringArray(R.array.navigation_urls)[i];

        Typeface lightFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Light.ttf");
        Typeface regularFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Regular.ttf");
        Typeface hairlineFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Hairline.ttf");

        final ListView userProfileList =  (ListView) rootView.findViewById(R.id.userProfileList);
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "", getString(R.string.loader), true);

        if(Notificare.shared().isLoggedIn())
            Notificare.shared().fetchUserDetails(new NotificareCallback<NotificareUser>() {

                @Override
                public void onSuccess(NotificareUser userResult) {

                    final NotificareUser result = userResult;
                    Notificare.shared().fetchUserPreferences(new NotificareCallback<List<NotificareUserPreference>>() {
                        @Override
                        public void onSuccess(List<NotificareUserPreference> notificareUserPreferences) {

                            if(result.getAccessToken() == null){
                                Notificare.shared().generateAccessToken(new NotificareCallback<NotificareUser>() {
                                    @Override
                                    public void onSuccess(NotificareUser notificareUser) {

                                    }

                                    @Override
                                    public void onError(NotificareError notificareError) {

                                    }
                                });
                            }


                            final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
                            final ArrayList<NotificareUserPreference> prefs = new ArrayList<NotificareUserPreference>();

                            HashMap<String, String> header1 = new HashMap<String, String>();
                            header1.put("label", getString(R.string.header_user_profile));
                            list.add(header1);

                            HashMap<String, String> userProfile = new HashMap<String, String>();
                            userProfile.put("name", result.getUserName());
                            userProfile.put("email", result.getUserId());
                            if(result.getAccessToken() == null){
                                userProfile.put("token", "");

                            } else {
                                userProfile.put("token", result.getAccessToken() + "@pushmail.notifica.re");

                            }
                            userProfile.put("icon", result.getUserId());
                            userProfile.put("label", "user_profile");
                            userProfile.put("action", result.getUserId());
                            list.add(userProfile);

                            final HashMap<String, String> changePass = new HashMap<String, String>();
                            changePass.put("label", getString(R.string.title_change_pass));
                            list.add(changePass);

                            HashMap<String, String> generateToken = new HashMap<String, String>();
                            generateToken.put("label", getString(R.string.title_generate_token));
                            list.add(generateToken);

                            HashMap<String, String> signOut = new HashMap<String, String>();
                            signOut.put("label", getString(R.string.title_signout));
                            list.add(signOut);

                            HashMap<String, String> header2 = new HashMap<String, String>();
                            header2.put("label", getString(R.string.header_user_preferences));
                            list.add(header2);

                            for (NotificareUserPreference preferenceObj : notificareUserPreferences) {
                                prefs.add(preferenceObj);
                                HashMap<String, String> pref = new HashMap<String, String>();
                                pref.put("label", preferenceObj.getLabel());
                                pref.put("preferenceId", preferenceObj.getId());

                                if(preferenceObj.getPreferenceType().equals("choice")){
                                    for (NotificareUserPreferenceOption segmentObj : preferenceObj.getPreferenceOptions()) {
                                        if(segmentObj.isSelected()){
                                            pref.put("name", segmentObj.getLabel());
                                        }
                                    }
                                }

                                if(preferenceObj.getPreferenceType().equals("single")){
                                    for (NotificareUserPreferenceOption segmentObj : preferenceObj.getPreferenceOptions()) {
                                        pref.put("segmentId", segmentObj.getUserSegmentId());
                                        if(segmentObj.isSelected()){
                                            pref.put("selected", "1");
                                        }
                                    }
                                }
                                list.add(pref);
                            }

                            ListAdapter adapter = new UserProfileAdapter(getActivity(), list, prefs);

                            userProfileList.setAdapter(adapter);

                            userProfileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> aView, View view, int position,
                                                        long arg) {

                                    if(list.get(position).get("label").equals(getString(R.string.title_change_pass))){
                                        onChangePassword();
                                    }

                                    if(list.get(position).get("label").equals(getString(R.string.title_generate_token))){
                                        onGenerateToken();
                                    }

                                    if(list.get(position).get("label").equals("user_profile") && !list.get(position).get("token").isEmpty()){
                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                        intent.putExtra(Intent.EXTRA_EMAIL, list.get(position).get("token"));
                                        intent.putExtra(Intent.EXTRA_SUBJECT, "Android Demo App");
                                        intent.putExtra(Intent.EXTRA_TEXT, "Type some text");
                                        intent.setData(Uri.parse("mailto:"));
                                        intent.setType("text/plain");
                                        startActivity(intent);
                                    }


                                    if(list.get(position).get("label").equals(getString(R.string.title_signout))){
                                        onSignOut();
                                    }

                                    if(list.get(position).get("preferenceId") != null){

                                        NotificareUserPreference preference = prefs.get(position - 6);

                                        if(preference.getPreferenceType().equals("choice")){
                                            showSingleChoiceOptions(prefs.get(position - 6));
                                        } else {
                                            showMultiChoiceOptions(prefs.get(position - 6));
                                        }


                                    }



                                }
                            });
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(NotificareError notificareError) {

                            dialog.dismiss();
                        }
                    });

                }

                @Override
                public void onError(NotificareError error) {
                    if (error.getCode() == NotificareError.FORBIDDEN || error.getCode() == NotificareError.UNAUTHORIZED) {
                        dialog.dismiss();
                        Toast.makeText(Notificare.shared().getApplicationContext(), "Unauthorized access", Toast.LENGTH_LONG).show();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(Notificare.shared().getApplicationContext(), "Error fetching user details", Toast.LENGTH_LONG).show();
                    }
                }
            });


        getActivity().setTitle(label);
        return rootView;
    }

    public void onChangePassword(){

        AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());

        LinearLayout layout = new LinearLayout(this.getActivity().getBaseContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        alert.setTitle(getString(R.string.app_name));
        alert.setMessage(getString(R.string.text_change_pass));

        final EditText pass1 = new EditText(this.getActivity());
        final EditText pass2 = new EditText(this.getActivity());
        pass1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        pass2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(pass1);
        layout.addView(pass2);
        alert.setView(layout);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                if (!pass1.getText().toString().equals(pass2.getText().toString())) {
                    onChangePasswordError(getString(R.string.error_pass_not_match));
                } else if (pass1.getText().toString() == null && pass2.getText().toString() == null) {
                    onChangePasswordError(getString(R.string.error_reset_pass));
                } else if (pass1.getText().toString().length() < 5) {
                    onChangePasswordError(getString(R.string.error_pass_too_short));
                } else {
                    doChangePassword(pass1);
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();

    }

    public void doChangePassword(EditText pass){
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "", getString(R.string.loader_signin), true);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());

        String password = pass.getText().toString();
        Notificare.shared().changePassword(password, new NotificareCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                dialog.dismiss();
                alert.setTitle(getString(R.string.app_name));
                alert.setMessage(getString(R.string.success_change_password));
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                alert.show();

            }

            @Override
            public void onError(NotificareError notificareError) {
                dialog.dismiss();
                alert.setTitle(getString(R.string.app_name));
                alert.setMessage(getString(R.string.error_change_password));
                alert.show();

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
            }
        });
    }

    public void onChangePasswordError(String error){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());
        alert.setTitle(getString(R.string.app_name));
        alert.setMessage(error);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }

    public void onGenerateToken(){

        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "", getString(R.string.loader_generate_token), true);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());


        Notificare.shared().generateAccessToken(new NotificareCallback<NotificareUser>() {
            @Override
            public void onSuccess(NotificareUser notificareUser) {
                dialog.dismiss();
                alert.setTitle(getString(R.string.app_name));
                alert.setMessage(getString(R.string.success_generate_token));
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        int i = getArguments().getInt(ARG_NAVIGATION_NUMBER);
                        Fragment fragment = new UserProfileFragment();
                        Bundle args = new Bundle();
                        args.putInt(UserProfileFragment.ARG_NAVIGATION_NUMBER, i);
                        fragment.setArguments(args);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                    }
                });
                alert.show();
            }

            @Override
            public void onError(NotificareError notificareError) {
                dialog.dismiss();
                alert.setTitle(getString(R.string.app_name));
                alert.setMessage(getString(R.string.error_generate_token));
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                alert.show();
            }
        });
    }

    public void showSingleChoiceOptions(final NotificareUserPreference preference){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());
        alert.setTitle(getString(R.string.app_name));

        ArrayList<String> tmpList =  new ArrayList<String>();
        int selectedPosition = 0;
        for(int i = 0; i < preference.getPreferenceOptions().size(); i++){
            if(preference.getPreferenceOptions().get(i).isSelected()){
                selectedPosition = i;
            }
            tmpList.add(preference.getPreferenceOptions().get(i).getLabel());
        }

        CharSequence[] charSeqOfNames = tmpList.toArray(new CharSequence[tmpList.size()]);
        boolean bl[] = new boolean[tmpList.size()];

        alert.setSingleChoiceItems(charSeqOfNames, selectedPosition, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                Notificare.shared().userSegmentAddToUserPreference(preference.getPreferenceOptions().get(which).getUserSegmentId(), preference, new NotificareCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {


                    }

                    @Override
                    public void onError(NotificareError notificareError) {

                    }
                });

            }
        });

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                refreshFragment();

            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    public void showMultiChoiceOptions(final NotificareUserPreference preference){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());
        alert.setTitle(getString(R.string.app_name));

        ArrayList<String> tmpList =  new ArrayList<String>();
        boolean bl[] = new boolean[preference.getPreferenceOptions().size()];
        int index = 0;
        for(NotificareUserPreferenceOption option : preference.getPreferenceOptions()){
            bl[index++] = option.isSelected();
            tmpList.add(option.getLabel());
        }

        CharSequence[] charSeqOfNames = tmpList.toArray(new CharSequence[tmpList.size()]);

        alert.setMultiChoiceItems(charSeqOfNames, bl, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                if(isChecked){

                    Notificare.shared().userSegmentAddToUserPreference(preference.getPreferenceOptions().get(which).getUserSegmentId(), preference, new NotificareCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {


                        }

                        @Override
                        public void onError(NotificareError notificareError) {

                        }
                    });

                } else {

                    Notificare.shared().userSegmentRemoveFromUserPreference(preference.getPreferenceOptions().get(which).getUserSegmentId(), preference, new NotificareCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {


                        }

                        @Override
                        public void onError(NotificareError notificareError) {

                        }
                    });

                }
            }
        });
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                refreshFragment();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }

    public void refreshFragment(){
        int i = getArguments().getInt(ARG_NAVIGATION_NUMBER);
        Fragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putInt(UserProfileFragment.ARG_NAVIGATION_NUMBER, i);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    public void onSignOut(){
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "", getString(R.string.signout), true);
        Notificare.shared().userLogout(new NotificareCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {

                SignInFragment fragment = new SignInFragment();
                Bundle args = new Bundle();
                args.putInt(SignInFragment.ARG_NAVIGATION_NUMBER, 4);
                fragment.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                dialog.dismiss();
            }

            @Override
            public void onError(NotificareError error) {
                dialog.dismiss();
            }
        });
    }
}