package com.example.chatchit;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.core.os.BuildCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;

public class MyEditText extends androidx.appcompat.widget.AppCompatEditText {
        public static String LinkUri;
        public MyEditText( Context context) {
            super(context);
        }

        public MyEditText( Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public MyEditText( Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
            final InputConnection ic = super.onCreateInputConnection(editorInfo);
            EditorInfoCompat.setContentMimeTypes(editorInfo,
                    new String[]{"image/gif",
                                 "image/jpeg",
                                 "image/png",
                                 "image/webp"}
                    );

            final InputConnectionCompat.OnCommitContentListener callback =
                    new InputConnectionCompat.OnCommitContentListener() {
                        @Override
                        public boolean onCommitContent( InputContentInfoCompat inputContentInfo,
                                                        int flags, Bundle opts) {
                            // read and display inputContentInfo asynchronously
                            if (BuildCompat.isAtLeastNMR1() && (flags &
                                    InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0) {
                                try {
                                    inputContentInfo.requestPermission();
                                } catch (Exception e) {
                                    return false; // return false if failed
                                }
                            }
                            // read and display inputContentInfo asynchronously.
                            // call inputContentInfo.releasePermission() as needed.
                            inputContentInfo.releasePermission();

                            //Uri linkUri = inputContentInfo.getContentUri();
                            Uri linkUri = inputContentInfo.getLinkUri();
                            LinkUri = (linkUri != null ? linkUri.toString() : "null");
                            return true;  // return true if succeeded
                        }
                    };
            return InputConnectionCompat.createWrapper(ic, editorInfo, callback);
        }
        public static String getLink(){
            return LinkUri;
        }
        public static void setLink(){
            LinkUri = null;
        }
}
