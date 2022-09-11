package net.swmud.trog.s3photosync;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class PathUtils {
    public static List<File> getDCIMPaths(@NonNull Context ctx) {
        List<File> list = new ArrayList<>();
        File[] aDirArray = ContextCompat.getExternalFilesDirs(ctx, null);
        for (File dir : aDirArray) {
            if (null != dir) {
                String path = dir.getAbsolutePath();
                int idx = path.indexOf("Android/");
                if (idx > -1) {
                    path = path.substring(0, idx);
                    dir = Paths.get(path, Environment.DIRECTORY_DCIM).toFile();
                    if (dir.isDirectory()) {
                        list.add(dir);
                    }
                    dir = Paths.get(path, Environment.DIRECTORY_DCIM, "Camera").toFile();
                    if (dir.isDirectory()) {
                        list.add(dir);
                    }
                }
            }
        }
//        list.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
//        list.add(Environment.getExternalStorageDirectory());
        return list;
    }
}
