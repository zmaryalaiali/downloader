
package com.luilala.kandrhar.downloader.adapter;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luilala.kandrhar.downloader.DownloadListActivity;
import com.luilala.kandrhar.downloader.folder.Folder;
import com.luilala.kandrhar.downloader.R;
import com.luilala.kandrhar.downloader.model.NestedFormatModel;

import java.util.List;

public class NestedFormatAdapter extends RecyclerView.Adapter<NestedFormatAdapter.NestedFormatHolder> {
    private Context context;
    private List<NestedFormatModel> list;

    String title;

    public NestedFormatAdapter(Context context, List<NestedFormatModel> list, String title) {
        this.context = context;
        this.list = list;
        this.title = title;
    }

    @NonNull
    @Override
    public NestedFormatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_format, parent, false);
        return new NestedFormatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NestedFormatHolder holder, int position) {
        NestedFormatModel nestedFormatModel = list.get(position);
        holder.radioButton.setText(nestedFormatModel.getQuality());
//        holder.tvOption.setText(nestedFormatModel.getQuality());
        holder.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                holder.radioButton.setChecked(isChecked);
                nestedFormatModel.setSelect(isChecked);
            }
        });

        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                downloadFromUrl(nestedFormatModel.getUrl(), title, nestedFormatModel.getFormat(),nestedFormatModel);
                Intent intent = new Intent(context, DownloadListActivity.class);
                intent.putExtra("isDownload", true);
                intent.putExtra("title",title+nestedFormatModel.getFormat());
                intent.putExtra("url",nestedFormatModel.getUrl());
                context.startActivity(intent);
                Activity activity = (Activity) context;
                activity.finish();

            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NestedFormatHolder extends RecyclerView.ViewHolder {

        RadioButton radioButton;
        private TextView tvOption;

        public NestedFormatHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.rbtn_option);
            tvOption = itemView.findViewById(R.id.tv_option);

        }
    }

    private void downloadFromUrl(String youtubeDlUrl, String downloadTitle, String fileName, NestedFormatModel nestedFormatModel) {
        Uri uri = Uri.parse(youtubeDlUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(downloadTitle);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        try {
            request.setDestinationInExternalPublicDir(Folder.getFile(context).getName(), title + fileName);
        } catch (Exception e) {
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title + fileName);//(Environment.DIRECTORY_PICTURES,"picname.jpeg")
        }


    }
}
