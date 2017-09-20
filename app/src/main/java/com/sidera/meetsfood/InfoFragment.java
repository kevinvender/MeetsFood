package com.sidera.meetsfood;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sidera.R;
import com.sidera.meetsfood.api.ApiService;
import com.sidera.meetsfood.api.beans.FiglioDettagli;
import com.sidera.meetsfood.api.beans.FiglioTestata;
import com.sidera.meetsfood.data.Child;
import com.sidera.meetsfood.data.Tariffa;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class InfoFragment extends Fragment {

    private static final int CAMERA_REQUEST = 1888; // field

    // TODO: Rename and change types of parameters

    private File filename = null;
    private File filename_icon = null;

    public static InfoFragment newInstance() {
        Bundle args = new Bundle();
        InfoFragment fragment = new InfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public InfoFragment() {}

    public void refresh() {
        FiglioDettagli d = ApiService.figlioDettagli != null ? ApiService.figlioDettagli : new FiglioDettagli();
        ((TextView) rootView.findViewById(R.id.textIstituto)).setText(d.istituto);
        ((TextView) rootView.findViewById(R.id.textSchool)).setText(d.scuola);
        ((TextView) rootView.findViewById(R.id.textClass)).setText(d.classe);
        ((TextView) rootView.findViewById(R.id.textSchoolYear)).setText(d.anno_scolastico);
        ((TextView) rootView.findViewById(R.id.textSubscription)).setText(d.data_iscrizione);
        ((TextView) rootView.findViewById(R.id.textIntolerances)).setText(d.intolleranze);

        ((TextView) rootView.findViewById(R.id.textPan)).setText(d.codice_pan);
        ((TextView) rootView.findViewById(R.id.textCardId)).setText(d.card_id);

        ((TextView) rootView.findViewById(R.id.textNome)).setText(d.nome_resp);
        ((TextView) rootView.findViewById(R.id.textCognome)).setText(d.cognome_resp);
        ((TextView) rootView.findViewById(R.id.textIndirizzo)).setText(d.indirizzo_resp);
        ((TextView) rootView.findViewById(R.id.textCap)).setText(d.cap_resp);
        ((TextView) rootView.findViewById(R.id.textLocalita)).setText(d.localita_resp);
        ((TextView) rootView.findViewById(R.id.textProv)).setText(d.provinica_resp);

        ((TextView) rootView.findViewById(R.id.textTelefono)).setText(d.telefono_resp);
        ((TextView) rootView.findViewById(R.id.textEmail)).setText(d.email_resp);
        ((TextView) rootView.findViewById(R.id.textCodiceFisc)).setText(d.codiceFiscale_resp);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_info, container, false);
        refresh();


//        final Button button = (Button) rootView.findViewById(R.id.photo_button);
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Activity act = getActivity();
//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePictureIntent.resolveActivity(act.getPackageManager()) != null) {
//                    try {
//                        InfoFragment.this.filename = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "profile" + mId + ".jpg");
//                        InfoFragment.this.filename_icon = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "profile_icon" + mId + ".jpg");
//                    } catch (Exception e) {
//                        Log.e("", e.getMessage());
//                    }
//                    if (InfoFragment.this.filename != null && InfoFragment.this.filename_icon != null) {
//                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//                                Uri.fromFile(InfoFragment.this.filename));
//                        startActivityForResult(takePictureIntent, 1);
//                    }
//                }
//            }
//        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            try {
                saveImage(BitmapFactory.decodeFile(filename.toString()), filename.toString());
                saveImage(getThumbnail(Uri.fromFile(filename)), filename_icon.toString());
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void saveImage(Bitmap bitmap, String path) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            File f = new File(path);
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            bitmap = rotateImageIfRequired(getContext(), bitmap, Uri.fromFile(f));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
            fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.flush();
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError o) {
            o.printStackTrace();
        }
    }

    private Bitmap getThumbnail(Uri uri) {
        File image = new File(uri.getPath());

        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image.getPath(), bounds);
        if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
            return null;

        int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
                : bounds.outWidth;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = originalSize / 100;
        return BitmapFactory.decodeFile(image.getPath(), opts);
    }

    private static Bitmap rotateImageIfRequired(Context context,Bitmap img, Uri selectedImage) {

        // Detect rotation
        int rotation=getRotation(context, selectedImage);
        if(rotation!=0){
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            img.recycle();
            return rotatedImg;
        }else{
            return img;
        }
    }

    private static int getRotation(Context context,Uri selectedImage) {
        int rotation =0;
        ContentResolver content = context.getContentResolver();
        Cursor mediaCursor = content.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { "orientation", "date_added" },null, null,"date_added desc");

        if (mediaCursor != null && mediaCursor.getCount() !=0 ) {
            while(mediaCursor.moveToNext()){
                rotation = mediaCursor.getInt(0);
                break;
            }
        }
        mediaCursor.close();
        return rotation;
    }

    private static final int MAX_HEIGHT = 1024;
    private static final int MAX_WIDTH = 1024;
    public static Bitmap decodeSampledBitmap(Context context, Uri selectedImage)
            throws IOException {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img= rotateImageIfRequired(context, img, selectedImage);
        return img;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }
}
