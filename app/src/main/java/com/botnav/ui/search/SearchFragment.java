package com.botnav.ui.search;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.botnav.R;
import com.botnav.databinding.FragmentSearchBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SearchFragment extends Fragment {

    private SearchViewModel searchViewModel;
    private FragmentSearchBinding binding;

    ImageView rando;
    Button btnRand;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSearch;
        searchViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        rando= view.findViewById(R.id.imageRand);
        btnRand = view.findViewById(R.id.buttonSearchRand);

        btnRand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new FetchImage().start();
            }
        });


    }

    class FetchImage extends Thread{

        Bitmap bitmap;
        String data = "";
        ProgressDialog progressDialog;

        FetchImage(){



        }

        @Override
        public void run() {

            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Getting your pic....");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                }
            });

            try {
                URL url =  new URL("https://api.thecatapi.com/v1/images/search");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = bufferedReader.readLine())!= null){
                    data = data + line;

                }

                if (!data.isEmpty()){
                    JSONArray arr = new JSONArray(data);
                    JSONObject obj = arr.getJSONObject(0);
                    String img = obj.getString("url");
                    InputStream i_s = new URL(img).openStream();
                    bitmap = BitmapFactory.decodeStream(i_s);


                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    rando.setImageBitmap(bitmap);

                }
            });



        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}