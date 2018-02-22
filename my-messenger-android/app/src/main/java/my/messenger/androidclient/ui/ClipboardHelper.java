package my.messenger.androidclient.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import my.messenger.androidclient.R;

class ClipboardHelper {
    static void copyText(String str, Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Message", str);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(context,
                R.string.activity_chat_msg_menu_copy_message_result,
                Toast.LENGTH_SHORT)
                .show();
    }
}
