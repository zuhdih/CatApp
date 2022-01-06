package com.botnav.ui.upload;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.botnav.MainActivity;
import com.botnav.R;
import com.botnav.databinding.FragmentUploadBinding;
import com.github.drjacky.imagepicker.ImagePicker;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadFragment extends Fragment {

    private UploadViewModel uploadViewModel;
    private FragmentUploadBinding binding;
    private int serverResponseCode = 0;

    ImageView uploaded;
    Button btnOpen;
    Button btnUp;
    Bitmap upImage;
    String upImageUri;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        uploadViewModel =
                new ViewModelProvider(this).get(UploadViewModel.class);

        binding = FragmentUploadBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;



    }

   @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        btnOpen = view.findViewById(R.id.buttonUploadOpen);
        btnUp = view.findViewById(R.id.buttonUploadUpload);

        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            }
        });

        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(upImage != null){
                    /*ByteArrayOutputStream baos0 = new ByteArrayOutputStream();

                    upImage.compress(Bitmap.CompressFormat.JPEG, 88, baos0);
                    byte[] imageBytes0 = baos0.toByteArray();*/
                   //Base64.encodeToString(imageBytes0, Base64.DEFAULT);

                    new Thread(new Runnable() {
                        public void run() {

                            uploadFile(upImageUri);

                        }
                    }).start();

                }
                else{

                }
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uploaded = (ImageView) getView().findViewById(R.id.imageUp);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        upImage= selectedImage;
                        Log.i("upImageUri", "upImageUri :"+upImageUri);
                        uploaded.setImageBitmap(selectedImage);
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                upImageUri = picturePath;
                                upImage = BitmapFactory.decodeFile(picturePath);
                                uploaded.setImageBitmap(upImage);
                                Log.i("upImageUri", "upImageUri :"+upImageUri);
                                cursor.close();
                            }
                        }                    }
                    break;
            }}}


    public int uploadFile(String fileUri) {


        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        File sourceFile = new File(fileUri);

        if (!sourceFile.isFile()){


            Log.e("uploadFile", "::: Source File not exist :::"+ upImageUri);


            return 0;

        }
        else
        {
            try {


                String api_key = "8201b38d-3e24-4d39-94af-83120e72102b";
                String[] splitUri = upImageUri.split("/");
                String fileName = splitUri[splitUri.length-1].replaceAll("\\s","");
                URL url = new URL("https://api.thecatapi.com/v1/images/upload");

                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new MultipartBody.Builder()
                        .addFormDataPart("file", fileName,
                                RequestBody.create(new File(fileUri), MediaType.parse("image/jpg"))
                        )
                        .setType(MultipartBody.FORM)
                        .build();

                Request request = new Request.Builder()
                        .url("https://api.thecatapi.com/v1/images/upload")
                        .post(requestBody)
                        .addHeader("content-type", "multipart/form-data;")
                        .addHeader("x-api-key", "8201b38d-3e24-4d39-94af-83120e72102b")
                        .build();

                Response response = client.newCall(request).execute();

                Log.i("uploadFile", "HTTP Response is : "
                        + response.body().string());

                if(serverResponseCode == 200){

                    Log.i("respond", "::: 200 OK :::"+ upImageUri);
                }


            } catch (MalformedURLException ex) {


                ex.printStackTrace();


                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {


                e.printStackTrace();

                Log.e("Upload file to server Exception", "Exception : "  + e.getMessage(), e);
            }

            return serverResponseCode;

        } // End else block
    }

            @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}