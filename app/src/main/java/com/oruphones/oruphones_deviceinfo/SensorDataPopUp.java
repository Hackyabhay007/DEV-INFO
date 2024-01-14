package com.oruphones.oruphones_deviceinfo;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SensorDataPopUp {
    static TextView textViewContent,heading;
    static Dialog dialog;
    static Button closeBtn;
    public static void showDialog(Context context, String content) {
        dialog = null;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_scroll_view);
        dialog.setCancelable(true);
        closeBtn = dialog.findViewById(R.id.close_btn_scrollview);
        textViewContent = dialog.findViewById(R.id.textViewContent);
        textViewContent.setVisibility(View.VISIBLE);
        heading = dialog.findViewById(R.id.heading_popupupscrollView);
        textViewContent.setText(content);
        closeBtn.setOnClickListener(v -> dismissDialog());
        dialog.show();
    }
    public static void updateDialogValue(String content) {
        textViewContent.setText(content);
    }
    public static void setHeading(String content) {

        if (heading!=null)
        {
            heading.setText(content);
        }
    }

    public static void dismissDialog() {
        dialog.dismiss();
    }
}
