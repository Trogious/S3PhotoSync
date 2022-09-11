package net.swmud.trog.s3photosync.ui.main;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import net.swmud.trog.s3photosync.PathUtils;
import net.swmud.trog.s3photosync.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.provider.Settings.NameValueTable.VALUE;
import static java.sql.Types.TIMESTAMP;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment, container, false);
        TextView textView = root.findViewById(R.id.textView);
        textView.setText("");
        Context ctx = Objects.requireNonNull(getContext());
        List<File> dirs = PathUtils.getDCIMPaths(ctx);
        for (File dir : dirs) {
            textView.append(dir.getAbsolutePath() + "\n\n");
            File[] files = dir.listFiles();
            if (null == files) {
                Toast.makeText(ctx, "NULL", Toast.LENGTH_SHORT).show();
                continue;
            }
            for (File inner : files) {
                textView.append(inner.getAbsolutePath() + "\n\n");
            }
//            File appDir = Paths.get(dir.getAbsolutePath(), )
        }
        ImageView imageView = root.findViewById(R.id.imageView);
        for (Bitmap s : getImages(new Size(imageView.getMaxWidth(), imageView.getMaxHeight()))) {
//            textView.append(s + "\n\n");
            imageView.setImageBitmap(s);
            break;
        }

        return root;
    }

//    private String getRealPathFromURI(Uri contentUri) {
//        String[] proj = { MediaStore.Images.Media.DATA };
//        CursorLoader loader = new CursorLoader(mContext, contentUri, proj, null, null, null);
//        Cursor cursor = loader.loadInBackground();
//        assert cursor != null;
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        String result = cursor.getString(column_index);
//        cursor.close();
//        return result;
//    }

    public List<Bitmap> getImages(Size size) {
        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.RELATIVE_PATH

        };
        String selection = null; //sql-where-clause-with-placeholder-variables;
        String[] selectionArgs = null;
//        new String[] {
//                values-of-placeholder-variables
//        };
        String sortOrder = null; //sql-order-by-clause;

        List<Bitmap> images = new LinkedList<>();

        Cursor cursor = getContext().getContentResolver().query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );

        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        int exifDateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);

//        Log.e("MF", "pre cursor");
        while (cursor.moveToNext()) {
            // Use an ID column from the projection to get
            // a URI representing the media item itself.
//            images.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));
//            images.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String dateString = formatter.format(new Date(cursor.getLong(exifDateColumn)));
            Log.e("MF", "" + dateString);
//            Log.e("MF", "in cursor");
            long id = cursor.getLong(idColumn);
//            Log.e("MF", "id: " + id);
            Uri contentUri = ContentUris.withAppendedId(collection, id);
//            Log.e("MF", "content_uri: " + contentUri.toString());
            try {
                Bitmap thumbnail =
                        getContext().getContentResolver().loadThumbnail(
                                contentUri, size, null);
                images.add(thumbnail);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return images;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

    }

}
