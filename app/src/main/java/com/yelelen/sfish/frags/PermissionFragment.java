package com.yelelen.sfish.frags;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.yelelen.sfish.R;
import com.yelelen.sfish.utils.Utils;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by yelelen on 17-9-11.
 */

public class PermissionFragment extends BottomSheetDialogFragment
        implements EasyPermissions.PermissionCallbacks {
    // 权限回调的标示
    private static final int RC = 0x0100;
    private static PermissionFragment mInstance;

    public PermissionFragment() {
        // Required empty public constructor
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TransStatusbarBottomSheetDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_permission, container, false);
        root.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPerm();
            }
        });
        return root;

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshState(getView());
    }

    private void refreshState(View root) {
        if (root == null)
            return;

        Context context = getContext();

        root.findViewById(R.id.im_state_permission_network)
                .setVisibility(hasNetPerm(context) ? View.VISIBLE : View.INVISIBLE);
        root.findViewById(R.id.im_state_permission_network_status)
                .setVisibility(hasNetPerm(context) ? View.VISIBLE : View.INVISIBLE);
        root.findViewById(R.id.im_state_permission_read)
                .setVisibility(hasReadPerm(context) ? View.VISIBLE : View.INVISIBLE);
        root.findViewById(R.id.im_state_permission_write)
                .setVisibility(hasWritePerm(context) ? View.VISIBLE : View.INVISIBLE);
        root.findViewById(R.id.im_state_permission_record)
                .setVisibility(hasRecordAudioPerm(context) ? View.VISIBLE : View.INVISIBLE);
        root.findViewById(R.id.im_state_permission_location)
                .setVisibility(hasLocationPerm(context) ? View.VISIBLE : View.INVISIBLE);

    }

    private static boolean hasWritePerm(Context context) {
        return EasyPermissions.hasPermissions(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private static boolean hasRecordAudioPerm(Context context) {
        return EasyPermissions.hasPermissions(context, Manifest.permission.RECORD_AUDIO);
    }

    private static boolean hasReadPerm(Context context) {
        return EasyPermissions.hasPermissions(context, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private static boolean hasReadPhonePerm(Context context) {
        return EasyPermissions.hasPermissions(context, Manifest.permission.READ_PHONE_STATE);
    }

    private static boolean hasSystemAlertPerm(Context context) {
        return EasyPermissions.hasPermissions(context, Manifest.permission.SYSTEM_ALERT_WINDOW);
    }

    private static boolean hasLocationPerm(Context context) {
        return EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private static boolean hasNetPerm(Context context) {
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE
        };

        return EasyPermissions.hasPermissions(context, perms);
    }

    private static void show(FragmentManager fm) {
        if (mInstance != null)
            mInstance.dismiss();
        mInstance = new PermissionFragment();
        mInstance.show(fm, PermissionFragment.class.getName());
    }

    public static boolean hasAllPerm(Context context, FragmentManager fm) {
//        boolean hasAll = hasNetPerm(context) && hasReadPerm(context)
//                && hasWritePerm(context) && hasReadPhonePerm(context)
//                && hasLocationPerm(context) && hasRecordAudioPerm(context);
        boolean hasNet = hasNetPerm(context);
        boolean hasRead = hasReadPerm(context);
        boolean hasWrite = hasWritePerm(context);
        boolean hasPhone = hasReadPhonePerm(context);
        boolean hasLocation = hasLocationPerm(context);
        boolean hasAudio = hasRecordAudioPerm(context);
        boolean hasAll = hasNet && hasRead && hasWrite && hasPhone && hasLocation && hasAudio;

        if (!hasAll) {
            show(fm);
        }

        return hasAll;
    }

    @AfterPermissionGranted(RC)
    private void requestPerm() {
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
//                Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO
        };

        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            Utils.showToast(getContext(), getString(R.string.label_permission_ok));
            refreshState(getView());
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.title_assist_permissions),
                    RC, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public static class TransStatusbarBottomSheetDialog extends BottomSheetDialog {
        public TransStatusbarBottomSheetDialog(@NonNull Context context) {
            super(context);
        }

        public TransStatusbarBottomSheetDialog(@NonNull Context context, @StyleRes int theme) {
            super(context, theme);
        }

        protected TransStatusbarBottomSheetDialog(@NonNull Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            final Window window = getWindow();
            if (window == null)
                return;

//            DisplayMetrics metrics = new DisplayMetrics();
//            window.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
            int screenHeight = metrics.heightPixels;


//            int statusbarHeight = 0;
//
//            try {
//                Class<?> clx = Class.forName("com.android.internal.R$dimen");
//                Object o = clx.newInstance();
//                Field field = clx.getField("status_bar_height");
//                int x = Integer.parseInt(field.get(o).toString());
//                statusbarHeight = getContext().getResources().getDimensionPixelSize(x);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            int statusbarHeight = Utils.getStatusBarHeight(getOwnerActivity());
            int dialogHeight = screenHeight - statusbarHeight;
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    dialogHeight <= 0 ? ViewGroup.LayoutParams.MATCH_PARENT : dialogHeight);
        }
    }

}

