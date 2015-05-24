package re.notifica.app;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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

        Typeface lightFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Light.ttf");
        Typeface regularFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Regular.ttf");

        Button lostPassButton = (Button) rootView.findViewById(R.id.buttonLostPass);
        Button signUpButton = (Button) rootView.findViewById(R.id.buttonSignup);
        Button signInButton = (Button) rootView.findViewById(R.id.buttonSignin);
        final EditText emailField = (EditText) rootView.findViewById(R.id.email);
        final EditText passwordField = (EditText) rootView.findViewById(R.id.pass);

        emailField.setTypeface(lightFont);
        passwordField.setTypeface(lightFont);
        signInButton.setTypeface(lightFont);
        signUpButton.setTypeface(lightFont);
        lostPassButton.setTypeface(lightFont);

        rootView.findViewById(R.id.buttonSignin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //signIn();

                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                    //info.setText(R.string.error_sign_in);
                    builder.setMessage(R.string.error_sign_in)
                            .setTitle(R.string.app_name)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    AlertDialog dialogInfo = builder.create();
                    dialogInfo.show();
                } else if (password.length() < 6) {
                    //info.setText(R.string.error_pass_too_short);
                    builder.setMessage(R.string.error_pass_too_short)
                            .setTitle(R.string.app_name)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    AlertDialog dialogInfo = builder.create();
                    dialogInfo.show();
                } else if (!email.contains("@")) {
                    builder.setMessage(R.string.error_invalid_email)
                            .setTitle(R.string.app_name)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    AlertDialog dialogInfo = builder.create();
                    dialogInfo.show();
                    //info.setText(R.string.error_invalid_email);
                } else {

                    final ProgressDialog dialog = ProgressDialog.show(getActivity(), "", getString(R.string.loader_signin), true);


                    Notificare.shared().userLogin(email, password, new NotificareCallback<Boolean>() {

                        @Override
                        public void onError(NotificareError error) {
                            dialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(R.string.error_sign_in)
                                    .setTitle(R.string.app_name)
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //do things
                                        }
                                    });
                            AlertDialog dialogInfo = builder.create();
                            dialogInfo.show();
                            passwordField.setText(null);
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                            Notificare.shared().fetchUserDetails(new NotificareCallback<NotificareUser>() {

                                @Override
                                public void onError(NotificareError error) {
                                    dialog.dismiss();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setMessage(R.string.error_sign_in)
                                            .setTitle(R.string.app_name)
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    //do things
                                                }
                                            });
                                    AlertDialog dialogInfo = builder.create();
                                    dialogInfo.show();
                                    passwordField.setText(null);
                                }

                                @Override
                                public void onSuccess(NotificareUser user) {
                                    Fragment fragment = new UserProfileFragment();
                                    Bundle args = new Bundle();
                                    args.putInt(UserProfileFragment.ARG_NAVIGATION_NUMBER, i);
                                    fragment.setArguments(args);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                                    emailField.setText(null);
                                    passwordField.setText(null);
                                    dialog.dismiss();
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