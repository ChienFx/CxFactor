package com.android.chienfx.core.helper;

import android.content.Context;

import com.android.chienfx.core.Definition;
import com.android.chienfx.core.user.User;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class InternalStorage {
    private InternalStorage(){

    }

    public static void writeUserData(Context context, Object object) throws IOException {
        FileOutputStream fos = context.openFileOutput(Definition.LOCAL_FILE_NAME, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }

    public static Object readUserData(Context context)throws IOException, ClassNotFoundException{
        FileInputStream fis = context.openFileInput(Definition.LOCAL_FILE_NAME);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object obj = ois.readObject();
        return obj;
    }
}
