package com.example.d_lockv2;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import es.dmoral.toasty.Toasty;
import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class MainActivity extends AppCompatActivity {

    Vibrator vibrator;

    ImageView background_image_of_login_screen;
    TextView welcome_txt;
    TextView aboutdlock_txt;
    TextView login_button;
    LottieAnimationView world_animation;
    TextView enter_password_txt;

    TextView dlock_community;
    TextView dlock_community_details;
    OtpTextView otpTextView;

    TextView temp ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Intent in = new Intent(MainActivity.this,mainpage.class);
        //startActivity(in);
        //finish();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //startActivity(new Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS));
        background_image_of_login_screen = findViewById(R.id.loginbackground);
        welcome_txt = findViewById(R.id.welcometxt);
        aboutdlock_txt = findViewById(R.id.aboutdlock);
        login_button = findViewById(R.id.loginbutton);
        world_animation = findViewById(R.id.bubbleanimation);
        enter_password_txt = findViewById(R.id.passwordtxt);

        dlock_community = findViewById(R.id.dlockcommunitytxt);
        dlock_community_details = findViewById(R.id.communitydetails);
        otpTextView = findViewById(R.id.otp_view);

        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        temp=findViewById(R.id.temp);

        YoYo.with(Techniques.Landing)
                .duration(5000)
                .repeat(0)
                .playOn(background_image_of_login_screen);
        YoYo.with(Techniques.ZoomIn)
                .duration(3000)
                .repeat(0)
                .playOn(welcome_txt);
        YoYo.with(Techniques.ZoomIn)
                .duration(3000)
                .playOn(aboutdlock_txt);
        YoYo.with(Techniques.ZoomIn)
                .duration(3000)
                .playOn(login_button);
    }

    public void tonextpage(View view)
    {
        YoYo.with(Techniques.SlideOutUp)
                .duration(1000)
                .playOn(background_image_of_login_screen);
        YoYo.with(Techniques.SlideOutUp)
                .duration(1000)
                .playOn(welcome_txt);
        YoYo.with(Techniques.SlideOutUp)
                .duration(1000)
                .playOn(aboutdlock_txt);
        YoYo.with(Techniques.SlideOutUp)
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {
                        world_animation.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.SlideInUp)
                                .duration(1000)
                                .playOn(world_animation);
                        enter_password_txt.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.SlideInUp)
                                .duration(1000)
                                .playOn(enter_password_txt);
                        dlock_community.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.SlideInUp)
                            .duration(1000)
                            .playOn(dlock_community);
                        dlock_community_details.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.SlideInUp)
                                .duration(1000)
                                .playOn(dlock_community_details);
                        YoYo.with(Techniques.FadeOut)
                                .withListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        temp.setVisibility(View.INVISIBLE);
                                        otpTextView.setClickable(true);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                })
                                .duration(700)
                                .playOn(temp);

                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        background_image_of_login_screen.setVisibility(View.INVISIBLE);
                        welcome_txt.setVisibility(View.INVISIBLE);
                        aboutdlock_txt.setVisibility(View.INVISIBLE);
                        login_button.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }

                })
                .duration(1000)
                .playOn(login_button);

        otpTextView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                // fired when user types something in the Otpbox
            }
            @Override
            public void onOTPComplete(String otp) {
                // fired when user has entered the OTP fully.
                if(otp.equals("1234"))
                {
                    otpTextView.showSuccess();
                    Intent in = new Intent(MainActivity.this,mainpage.class);
                    startActivity(in);
                    finish();
                }
                else
                {
                    otpTextView.showError();
                    otpTextView.resetState();
                    otpTextView.setOTP("");
                    Toasty.error(MainActivity.this,"WRONG PINCODE",Toast.LENGTH_SHORT).show();
                    vibrator.vibrate(100);
                }
            }
        });
    }
}