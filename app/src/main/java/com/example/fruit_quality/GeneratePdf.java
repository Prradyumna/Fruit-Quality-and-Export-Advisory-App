package com.example.fruit_quality;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class GeneratePdf extends AppCompatActivity {


    EditText edt;
    Button btn;
    ConstraintLayout container;
    String name,number,email,data,currentDateTime;

    String[] required_permissions = new String[]{
            android.Manifest.permission.READ_MEDIA_IMAGES
    };

    boolean is_storage_image_permitted = false;

    String TAG = "Aaviskar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_generate_pdf);

//        edt=(EditText)findViewById(R.id.edt);
        btn = (Button) findViewById(R.id.btn);
        container = (ConstraintLayout) findViewById(R.id.container);

        data = getIntent().getStringExtra("data");


        SharedPreferences sharedPreferences = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);

        name = sharedPreferences.getString("name", "default");  // Default: "Guest"
        number = sharedPreferences.getString("number", "default");  // Default: "Guest"
        email = sharedPreferences.getString("mail", "default");  // Default: "Guest"

         currentDateTime = getCurrentDateTime();


        //===== for study permission related to android 13
        //===== please watch our previous video..

        if (!is_storage_image_permitted) {
            requestPermissionStorageImages();
        }


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPDFandShare();
            }
        });

    }

    private String getCurrentDateTime() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

//    public Bitmap captureScreenShot(View view)
//    {
//        Bitmap returnBitmap=Bitmap.createBitmap(view.getWidth(),view.getHeight(),Bitmap.Config.ARGB_8888);
//        Canvas canvas=new Canvas(returnBitmap);
//        Drawable bgdrawable=view.getBackground();
//        if(bgdrawable!=null)
//            bgdrawable.draw(canvas);
//        else
//            canvas.drawColor(Color.WHITE);
//        view.draw(canvas);
//        return returnBitmap;
//    }
//
//    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
//        int width = image.getWidth();
//        int height = image.getHeight();
//
//        float bitmapRatio = (float)width / (float) height;
//        if (bitmapRatio > 1) {
//            width = maxSize;
//            height = (int) (width / bitmapRatio);
//        } else {
//            height = maxSize;
//            width = (int) (height * bitmapRatio);
//        }
//        return Bitmap.createScaledBitmap(image, width, height, true);
//    }

    public void createPDF(OutputStream ref_outst) {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 800, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        // Get Canvas to Draw
        Paint paint = new Paint();
        Canvas canvas = page.getCanvas();

        // ** Draw Invoice Header **
        paint.setTextSize(24);
        paint.setColor(Color.BLACK);
        canvas.drawText("Fruit Quality Report", 250, 50, paint);

        // ** Draw Business Info **
        paint.setTextSize(14);
        canvas.drawText("UserName: " + name, 50, 100, paint);
        canvas.drawText("Number: "+ number, 50, 130, paint);
        canvas.drawText("Email: "+ email, 50, 160, paint);


        canvas.drawText("____________________________________________________________________________", 50, 190, paint);
        // ** Draw Invoice Details **
        canvas.drawText("Report  No: #123456", 50, 250, paint);
        canvas.drawText("Date: "+ currentDateTime, 50, 280, paint);
        canvas.drawText("Quality: " +data, 50, 310 , paint);




        // Finish the Page
        pdfDocument.finishPage(page);
        // finish the page
       // document.finishPage(page);

        try {
            // write the document content
            pdfDocument.writeTo(ref_outst);

            // close the document
            pdfDocument.close();

            Toast.makeText(this, "PDF is saved", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createPDFandShare() {
        OutputStream outst;
        try {
            //=== scoped storage is support after Q
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver contentResolver = getContentResolver();
                ContentValues contentValues = new ContentValues();

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
                LocalDateTime now = LocalDateTime.now();
                String date_suffix="_"+dtf.format(now);
                Log.d(TAG,dtf.format(now));

                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Report"+date_suffix + ".pdf");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_DOCUMENTS + File.separator + "Fruit");
                Uri pdfUri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues);

                outst = contentResolver.openOutputStream(Objects.requireNonNull(pdfUri));
                Objects.requireNonNull(outst);

                //code to create pdf
                createPDF(outst);

                //=== now intent to share image
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("application/pdf");
                share.putExtra(Intent.EXTRA_STREAM, pdfUri);
                startActivity(Intent.createChooser(share, "Share PDF"));

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestPermissionStorageImages() {
        if (ContextCompat.checkSelfPermission(GeneratePdf.this, required_permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, required_permissions[0] + " Granted");
            is_storage_image_permitted = true;
        } else {
            //new android 13 code after onActivityResult is deprecated, now ActivityResultLauncher..
            request_permission_launcher_storage_images.launch(required_permissions[0]);
        }

    }

    private ActivityResultLauncher<String> request_permission_launcher_storage_images =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            Log.d(TAG, required_permissions[0] + " Granted");
                            is_storage_image_permitted = true;

                        } else {
                            Log.d(TAG, required_permissions[0] + " Not Granted");
                            is_storage_image_permitted = false;
                        }
                    });



}