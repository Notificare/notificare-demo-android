package re.notifica.app;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import re.notifica.Notificare;
import re.notifica.NotificareCallback;
import re.notifica.NotificareError;
import re.notifica.model.NotificareUser;


/**
 * Fragment with a WebView
 */
public class SignInFragment extends Fragment {
    public static final String ARG_NAVIGATION_NUMBER = "navigation_pos";

    public SignInFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_signin, container, false);
        final int i = getArguments().getInt(ARG_NAVIGATION_NUMBER);
        String label = getResources().getStringArray(R.array.navigation_labels)[i];
        String url = getResources().getStringArray(R.array.navigation_urls)[i];

        rootView.findViewById(R.id.buttonSignin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //signIn();

                EditText emailField = (EditText) rootView.findViewById(R.id.email);
                EditText passwordField = (EditText) rootView.findViewById(R.id.pass);

                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();

                TextView info = (TextView) rootView.findViewById(R.id.infoText);


                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                    info.setText(R.string.error_sign_in);
                } else if (password.length() < 6) {
                    info.setText(R.string.error_pass_too_short);
                } else if (!email.contains("@")) {
                    info.setText(R.string.error_invalid_email);
                } else {

                    final ProgressDialog dialog = ProgressDialog.show(getActivity(), "", getString(R.string.loader_signin), true);


                    Notificare.shared().userLogin(email, password, new NotificareCallback<Boolean>() {

                        @Override
                        public void onError(NotificareError error) {
                            dialog.dismiss();

                        }

                        @Override
                        public void onSuccess(Boolean result) {

                            Notificare.shared().fetchUserDetails(new NotificareCallback<NotificareUser>() {

                                @Override
                                public void onError(NotificareError error) {
                                    dialog.dismiss();
                                }

                                @Override
                                public void onSuccess(NotificareUser user) {
                                    Notificare.shared().setUserId(user.getUserId());
                                    Notificare.shared().registerDevice(Notificare.shared().getDeviceId(), user.getUserId(), user.getUserName(), new NotificareCallback<String>() {

                                        @Override
                                        public void onSuccess(String result) {

                                            Fragment fragment = new UserProfileFragment();
                                            Bundle args = new Bundle();
                                            args.putInt(UserProfileFragment.ARG_NAVIGATION_NUMBER, i);
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

                            });

                        }

                    });
                }
            }
        });
        getActivity().setTitle(label);
        return rootView;
    }
}