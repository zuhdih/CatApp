package com.botnav.ui.view;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.botnav.R;
import com.botnav.databinding.FragmentViewBinding;
import com.botnav.ui.search.SearchFragment;
import com.squareup.moshi.Moshi;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ViewFragment extends Fragment {

    private ViewViewModel viewViewModel;
    private FragmentViewBinding binding;
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewViewModel =
                new ViewModelProvider(this).get(ViewViewModel.class);

        binding = FragmentViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            private ArrayList<Integer> al;
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(getParentFragment().getContext());
                View view= inflater.inflate(R.layout.card_view, null,false);
                return recyclerView.getChildViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        });



    }

    class FetchViewImage extends Thread{

        Bitmap bitmap;
        String data = "";
        ProgressDialog progressDialog;

        FetchViewImage(){



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
                OkHttpClient client = new OkHttpClient();
                Moshi moshi = new Moshi.Builder().build();
                //JsonAdapter<T> gistJsonAdapter = moshi.adapter(Gist.class);

                Request request = new Request.Builder()
                        .url("https://api.thecatapi.com/v1/images")
                        .get()
                        .addHeader("x-api-key", "8201b38d-3e24-4d39-94af-83120e72102b")
                        .build();

                Response response = client.newCall(request).execute();
            }
            catch (Exception e){
                e.printStackTrace();
            }

            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

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