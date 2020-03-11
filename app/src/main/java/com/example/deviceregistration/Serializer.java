package com.example.deviceregistration;


import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

//Supporting Java Class
// Adapted pickle class
// Object Serialization
//
// Example:
//      // Create the object
//      ImageData myImageData = new ImageData();
//      myImageData.caption = "VB";
//      myImageData.timeStamp = "2020";
//
//      // Save the object
//      Serializer.save(myImageData, "myData.ser");
//
//      // Recover the object
//      ImageData myImageData_ = null;
//      myImageData_ = (ImageData) Serializer.load(myImageData_, "myData.ser");
//
public class Serializer {

    // Save the object to the memory
    // args:    object: the object instance
    //          path:   the exact path and file name of where it will be stored
    static public void save(Object object, String path){
        Log.d("Serializer", "save: " + "called");

        try {

            // Create a file and a stream object
            FileOutputStream fileOut = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            // Write to the file
            out.writeObject(object);

            // Close the file and the stream
            out.close();
            fileOut.close();

            Log.d("Serializer", "Serialized data is saved in: " + path);
        } catch (IOException i) {
            i.printStackTrace();

            Log.d("Serializer", "Unable to save the file in: " + path);
        }

    }

    // Load the object from the memory
    // args:    object: the empty object instance
    //          path:   the exact path and file name of where it is be stored
    // returns: object: the recovered object instance
    static public Object load(Object object, String path){
        Log.d("Serializer", "load: " + "called");

        try {
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            object = (Object) in.readObject();
            in.close();
            fileIn.close();

            Log.d("Serializer", "Serialized data is read from: " + path);
        }
        catch (IOException i) {
            Log.d("Serializer", "File not found at: " + path);
        }
        catch (ClassNotFoundException c) {
            Log.d("Serializer", "Class not found at: " + path);
        }

        return object;
    }
}
