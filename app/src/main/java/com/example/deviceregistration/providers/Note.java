//package com.example.deviceregistration.providers;
//
//import android.content.ContentResolver;
//import android.net.Uri;
//import android.provider.BaseColumns;
//
//import static com.example.deviceregistration.providers.NotesContentProvider.*;
//
//// Outer class
//public class Note {
//
//    // Constructor
//    public Note() {
//    }
//
//    // Keeps all columns organized and readily accessible
//    public static final class Notes implements BaseColumns {
//
//        // Constructor
//        private Notes() {
//        }
//
//        public static final Uri CONTENT_URI = Uri.parse("content://" + NotesContentProvider.AUTHORITY + "/notes");
//
//        //todo change this name later
//        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/example.tasksDB/"+ NOTES_TABLE_NAME;
//
//        public static final String NOTE_ID = "_id";
//
//        public static final String TITLE = "title";
//
//        public static final String TEXT = "text";
//    }
//
//}
