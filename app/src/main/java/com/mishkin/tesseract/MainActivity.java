package com.mishkin.tesseract;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.Manifest;
import android.annotation.TargetApi;

import static androidx.core.text.HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS;
import static com.itextpdf.text.html.HtmlTags.FONT;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    static final int PHOTO_REQUEST_CODE = 1;
    private TessBaseAPI tessBaseApi;
    TextView textView;
    Uri outputFileUri;
    private static final String lang = "rus";
    String result = "empty";

    String text_filename = "";
    private static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100;
    private static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 101;
    private static int MY_PERMISSIONS_REQUEST_CAMERA = 102;

    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/TesseractSample/";
    private static final String PDF_PATH = Environment.getExternalStorageDirectory().toString() + "/PDF/";
    private static final String TESSDATA = "tessdata";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                     //   .setAction("Action", null).show();

                startCameraActivity();

              //  CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start((Activity) view.getContext());
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, ask it.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, ask it.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
/*
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, ask it.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

        */
        textView = (TextView) findViewById(R.id.textResult);
        StrictMode.VmPolicy.Builder newbuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(newbuilder.build());

        //openFile("textfile");

        if ( this.getIntent().hasExtra( "filename" ) ) {
            String FILENAME = this.getIntent().getStringExtra( "filename" );
            // Toast.makeText(getApplicationContext(),
            //    FILENAME, Toast.LENGTH_LONG).show();
            openFile(FILENAME); // mEditText.setLongClickable(false);
        }

    }


    /**
     * to get high resolution image from camera
     */
    private void startCameraActivity() {
        try {
           String IMGS_PATH = Environment.getExternalStorageDirectory().toString() + "/TesseractSample/imgs";
            //String IMGS_PATH =  "/TesseractSample/imgs";
            prepareDirectory(IMGS_PATH);

            String img_path = IMGS_PATH + "/ocr.jpg";

            outputFileUri = Uri.fromFile(new File(img_path));

         //   outputFileUri = FileProvider.getUriForFile(MainActivity.this,
                  //  MainActivity.this.getApplicationContext().getPackageName()  + ".provider",
                 //   new File(img_path));
        //    Uri photoURI = FileProvider.getUriForFile(context, MainActivity.this.getApplicationContext().getPackageName() + ".provider", createImageFile());
            final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            Log.e("outputFileUri ", outputFileUri.getPath() );
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                Log.e("start", "0");
                startActivityForResult(takePictureIntent, PHOTO_REQUEST_CODE);
                Log.e("start", "1");
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }



    // Метод для сохранения файла
    private void saveFile(String fileName) {
        try {
            OutputStream outputStream =this.openFileOutput(fileName, 0);
            OutputStreamWriter osw = new OutputStreamWriter(outputStream);


            //--save to string--
            String s = textView.getText().toString();

            osw.write(s);
            osw.close();

        //    Toast.makeText(getActivity(), "Файл успешно сохранен!", Toast.LENGTH_SHORT).show();
        } catch (Throwable t) {
            Toast.makeText(this,
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    // Метод для открытия файла
    private void openFile(String fileName) {
        try {
            InputStream inputStream = this.openFileInput(fileName);

            if (inputStream != null) {
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isr);
                String line;
                StringBuilder builder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
                }

                inputStream.close();
                String codeText = builder.toString();

                textView.setText(codeText);

            }




        } catch (Throwable t) {
            Toast.makeText(this,
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }





    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                Bitmap bmp = null;
                try {
                    InputStream is = this.getContentResolver().openInputStream(result.getUri());
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    bmp = BitmapFactory.decodeStream(is, null, options);

                } catch (Exception ex) {
                    Log.i(getClass().getSimpleName(), ex.getMessage());
                    //Toast.makeText(this, errorConvert, Toast.LENGTH_SHORT).show();
                }

               // ivImage.setImageBitmap(bmp);


               // doOCR(bmp);
            }
        }
    }
*/
@Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        //making photo
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            doOCR();
        } else {
            Toast.makeText(this, "ОШИБКА: изображение не было получено.", Toast.LENGTH_SHORT).show();
        }
    }

    private void doOCR() {
        prepareTesseract();
        startOCR(outputFileUri);
    }


    private void prepareDirectory(String path) {

        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e(TAG, "ERROR: Creation of directory " + path + " failed, check does Android Manifest have permission to write to external storage.");
            }
        } else {
            Log.i(TAG, "Created directory " + path);
        }
    }


    private void prepareTesseract() {
        try {
            prepareDirectory(DATA_PATH + TESSDATA);
        } catch (Exception e) {
            e.printStackTrace();
        }

        copyTessDataFiles(TESSDATA);
    }

    /**
     * Copy tessdata files (located on assets/tessdata) to destination directory
     *
     * @param path - name of directory with .traineddata files
     */
    private void copyTessDataFiles(String path) {
        try {
            String fileList[] = getAssets().list(path);

            for (String fileName : fileList) {

                // open file within the assets folder
                // if it is not already there copy it to the sdcard
                String pathToDataFile = DATA_PATH + path + "/" + fileName;
                if (!(new File(pathToDataFile)).exists()) {

                    InputStream in = getAssets().open(path + "/" + fileName);

                    OutputStream out = new FileOutputStream(pathToDataFile);

                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();

                    Log.d(TAG, "Copied " + fileName + "to tessdata");
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable to copy files to tessdata " + e.toString());
        }
    }


    /**
     * don't run this code in main thread - it stops UI thread. Create AsyncTask instead.
     * http://developer.android.com/intl/ru/reference/android/os/AsyncTask.html
     *
     * @param imgUri
     */
    private void startOCR(Uri imgUri) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4; // 1 - means max size. 4 - means maxsize/4 size. Don't use value <4, because you need more memory in the heap to store your data.
            Bitmap bitmap = BitmapFactory.decodeFile(imgUri.getPath(), options);

           // bitmap = changeBitmapContrastBrightness( bitmap, )

            result = extractText(bitmap);
            String sssr = ""; int i=0; String azbuka = "";
            while ( i<result.length() - 1 ) {

                char c = result.charAt( i );
                char c2 = result.charAt( i + 1 );

                if ( ( c == '-' || c == '–' || c == '—' ) && c2 == '\n' ) {
                    i++; i++; continue;
                }

                if ( ( c == '\n' ) && !Character.isUpperCase(c2) ) {
                    sssr = sssr + " ";
                    i++; continue;
                }
                sssr = sssr + result.charAt( i );

                i++;
            }

            sssr = sssr + result.charAt( i );
            textView.setText(sssr);


            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

            saveFile( timeStamp + ".txt" );
            text_filename = timeStamp + ".txt";
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }


    }


    private String extractText(Bitmap bitmap) {
        try {
            tessBaseApi = new TessBaseAPI();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            if (tessBaseApi == null) {
                Log.e(TAG, "Ошибка. TessBaseAPI вернул null.");
            }
        }

        tessBaseApi.init(DATA_PATH, lang);

//       //EXTRA SETTINGS
//        //For example if we only want to detect numbers
//        tessBaseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "1234567890");
//
//        //blackList Example
//        tessBaseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "!@#$%^&*()_+=-qwertyuiop[]}{POIU" +
//                "YTRWQasdASDfghFGHjklJKLl;L:'\"\\|~`xcvXCVbnmBNM,./<>?");

        Log.d(TAG, "Training file loaded");


        tessBaseApi.setImage(bitmap);
        String extractedText = "Пустой результат";
        try {
            extractedText = tessBaseApi.getUTF8Text();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка в распознавании текста.");
        }
        tessBaseApi.end();
        return extractedText;
    }


    public static Bitmap changeBitmapContrastBrightness (Bitmap bmp, float contrast, float brightness)
    {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_openfile) {

            Intent intent = new Intent( this, FileManagerActivity.class);
            startActivity(intent);

            return true;

        }
        if (id == R.id.action_topdf) {

            //  String PDFS_PATH = Environment.getExternalStorageDirectory().toString() + "/TesseractSample/imgs";
            //String IMGS_PATH =  "/TesseractSample/imgs";

            if (textView.getText().toString().length() > 0 ) {


                try {

                    prepareDirectory(PDF_PATH);

                    String filename = text_filename + ".pdf";
                    Document doc = new Document();
                    PdfWriter.getInstance(doc, new FileOutputStream(PDF_PATH + filename));
                    doc.open();
                    //  doc.add(new Paragraph(sssr));


                    final String FONT = "/assets/fonts/ArialMT.ttf";

                    BaseFont bf=BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                    Font font=new Font(bf,30,Font.NORMAL);
                    doc.add(new Paragraph(textView.getText().toString(),font));


                    doc.close();


                    File file = new File(PDF_PATH + filename);
                    Intent target = new Intent(Intent.ACTION_VIEW);
                    target.setDataAndType(Uri.fromFile(file),"application/pdf");
                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                    Intent intent = Intent.createChooser(target, "Open File");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        System.out.println( "____ error " + PDF_PATH + filename );
                        // Instruct the user to install a PDF reader here, or something
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }

            return true;

        }
        if (id == R.id.action_emailto) {


            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{ "Укажите кому"});
            email.putExtra(Intent.EXTRA_SUBJECT, "Тема письма");
            email.putExtra(Intent.EXTRA_TEXT, textView.getText().toString());

            email.setType("message/rfc822");

            startActivity(Intent.createChooser(email, "Choose an Email client :"));

            return true;

        }


        return super.onOptionsItemSelected(item);
    }
}
