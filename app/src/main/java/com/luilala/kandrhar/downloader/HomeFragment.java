package com.luilala.kandrhar.downloader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class HomeFragment extends Fragment {
private Context context;
    Button btnDownload;
    TextInputLayout etVideoURL;

    Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        toolbar = view.findViewById(R.id.toolbar_home);
        etVideoURL = view.findViewById(R.id.et_video_url);
        btnDownload = view.findViewById(R.id.btn_download);


        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = etVideoURL.getEditText().getText().toString().trim();
                if (!link.isEmpty()) {
                    Intent intent = new Intent(context, VideoFormatActivity.class);
                    intent.putExtra("android.intent.extra.TEXT",link);
                    intent.putExtra("islink",true);
                    startActivity(intent);
//                    getAllFormat(extractVideoId(link));
                    Toast.makeText(context, "goto", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.pop_menu,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        
        if (item.getItemId() == R.id.menu_bottom_youtube)
        {
            Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}