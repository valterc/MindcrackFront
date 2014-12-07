package com.valterc.mindcrackfront.app.main.actionbar;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.valterc.mindcrackfront.app.R;
import com.valterc.mindcrackfront.app.main.navigationdrawer.NavigationDrawerFragment;
import com.valterc.mindcrackfront.app.main.navigationdrawer.NavigationDrawerStateListener;
import com.valterc.utils.ImageUtils;

import java.io.IOException;

/**
 * Created by Valter on 08/11/2014.
 */
public class MindcrackActionBarFragment extends Fragment implements NavigationDrawerStateListener {

    private boolean navigationDrawerOpen;
    private Typeface typefaceText;
    private Bitmap bitmapMindcrackLogo;

    private TextView textViewTitle;
    private ImageView imageViewCenter;
    private ImageSwitcher imageSwitcherRightImage;
    private ImageSwitcher imageSwitcherLeftImage;
    private NavigationDrawerFragment drawerFragment;

    public MindcrackActionBarFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        typefaceText = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
        SVG svg = null;
        try {
            svg = SVG.getFromAsset(getActivity().getAssets(), "mindcrack_logo.svg");
        } catch (SVGParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (svg != null && svg.getDocumentWidth() != -1) {
            bitmapMindcrackLogo = Bitmap.createBitmap((int) Math.ceil(svg.getDocumentWidth()),
                    (int) Math.ceil(svg.getDocumentHeight()),
                    Bitmap.Config.ARGB_8888);

            Canvas bmcanvas = new Canvas(bitmapMindcrackLogo);
            svg.renderToCanvas(bmcanvas);
        }

        drawerFragment = (NavigationDrawerFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        drawerFragment.setStateListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mindrack_actionbar, null);

        imageViewCenter = (ImageView) view.findViewById(R.id.imageViewActionBarCenterImage);
        textViewTitle = (TextView) view.findViewById(R.id.textViewActionBarTitle);
        imageSwitcherRightImage = (ImageSwitcher) view.findViewById(R.id.imageSwitcherActionBarRight);
        imageSwitcherLeftImage = (ImageSwitcher) view.findViewById(R.id.imageSwitcherActionBarLeft);


        imageSwitcherRightImage.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getActivity());
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });

        imageSwitcherLeftImage.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getActivity());
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });

        imageSwitcherLeftImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerFragment.isDrawerOpen()){
                    navigationDrawerOpen = false;
                    imageSwitcherLeftImage.setImageResource(R.drawable.menu_button);
                    drawerFragment.closeDrawer();
                } else {
                    navigationDrawerOpen = true;
                    imageSwitcherLeftImage.setImageResource(R.drawable.ic_arrow_back);
                    drawerFragment.openDrawer();
                }
            }
        });

        imageSwitcherRightImage.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.beat_fade_in));
        imageSwitcherRightImage.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
        imageSwitcherLeftImage.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_fade_in));
        imageSwitcherLeftImage.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_fade_out));

        imageViewCenter.setImageBitmap(bitmapMindcrackLogo);
        textViewTitle.setTypeface(typefaceText);
        imageSwitcherLeftImage.setImageResource(R.drawable.menu_button);

        return view;
    }

    public void setRightImageOnClickListener(View.OnClickListener listener){
        imageSwitcherRightImage.setOnClickListener(listener);

        /*

        imageSwitcherRightImage.setInAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
        imageSwitcherRightImage.setImageResource(R.drawable.heart_sad);

        imageSwitcherRightImage.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.beat_fade_in));
        imageSwitcherRightImage.setImageResource(R.drawable.heart);

         */
    }

    public void setRightImageOnLongClickListener(View.OnLongClickListener listener){
        imageSwitcherRightImage.setOnLongClickListener(listener);
    }

    public void setRightImageInAnimation(int animationId){
        if (getActivity() != null) {
            imageSwitcherRightImage.setInAnimation(AnimationUtils.loadAnimation(getActivity(), animationId));
        }
    }

    public void setRightImageResource(int resourceId){
        setRightImageResource(resourceId, false);
    }

    public void setRightImageResource(int resourceId, boolean bringToFront){
        if (bringToFront) {
            View v = imageSwitcherRightImage.getChildAt(0);
            imageSwitcherRightImage.bringChildToFront(v);
        }
        imageSwitcherRightImage.setImageResource(resourceId);
    }

    public void setTitleText(String text){
        textViewTitle.setText(text);
    }

    public void setTitleVisible(boolean visible){
        textViewTitle.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public void setCenterImageVisible(boolean visible){
        imageViewCenter.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public void setCenterImage(Bitmap bitmap){
        Bitmap roundedBitmap = ImageUtils.getRoundBitmap(bitmap);
        TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{ imageViewCenter.getDrawable() == null ? new ColorDrawable(0x00FFFFFF) : imageViewCenter.getDrawable(), new BitmapDrawable(getResources(), roundedBitmap)});
        transitionDrawable.setCrossFadeEnabled(true);
        imageViewCenter.setImageDrawable(transitionDrawable);
        transitionDrawable.startTransition(500);
    }

    public void resetCenterImage(){
        if (getActivity() != null && !isDetached()) {
            TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{imageViewCenter.getDrawable() == null ? new ColorDrawable(0x00FFFFFF) : imageViewCenter.getDrawable(), new BitmapDrawable(getResources(), bitmapMindcrackLogo)});
            transitionDrawable.setCrossFadeEnabled(true);
            imageViewCenter.setImageDrawable(transitionDrawable);
            transitionDrawable.startTransition(500);
        }
    }

    @Override
    public void onOpen() {
        if (navigationDrawerOpen)
            return;

        imageSwitcherLeftImage.setImageResource(R.drawable.ic_arrow_back);
        navigationDrawerOpen = true;
    }

    @Override
    public void onClose() {
        if (!navigationDrawerOpen)
            return;

        imageSwitcherLeftImage.setImageResource(R.drawable.menu_button);
        navigationDrawerOpen = false;
    }

}