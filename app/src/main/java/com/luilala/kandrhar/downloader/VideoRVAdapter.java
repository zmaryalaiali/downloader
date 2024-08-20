package com.luilala.kandrhar.downloader;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.luilala.kandrhar.downloader.folder.Folder;

import java.io.File;
import java.util.ArrayList;

public class VideoRVAdapter extends RecyclerView.Adapter<VideoRVAdapter.ViewHolder> {

    private ArrayList<VideoRVModel> videoRVModelArrayList;
    private Context context;
    private VideoClickInterface videoClickInterface;

    public VideoRVAdapter(ArrayList<VideoRVModel> videoRVModelArrayList, Context context, VideoClickInterface videoClickInterface) {
        this.videoRVModelArrayList = videoRVModelArrayList;
        this.context = context;
        this.videoClickInterface = videoClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        VideoRVModel videoRVModel = videoRVModelArrayList.get(position);
        holder.thumbnail.setImageBitmap(videoRVModel.getThumbnail());
        holder.tvTitle.setText(videoRVModel.getName());
//        Uri uri = Uri.fromFile();
        File file = new File(videoRVModel.getPath());
        String mb = converter(file.length());
        holder.tvLength.setText(mb);

        holder.ivShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareVideo(file);
            }
        });
        holder.ivThreeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.bottomStyleTheme);
                View view = LayoutInflater.from(context).inflate(R.layout.video_bs_layout, null);

                LinearLayout share, delete, rename;
                share = view.findViewById(R.id.bs_share);
                delete = view.findViewById(R.id.bs_delete);
                rename = view.findViewById(R.id.bs_rename);

                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareVideo(file);
                        bottomSheetDialog.dismiss();
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteVideo(file);
                        bottomSheetDialog.dismiss();

                    }
                });

                rename.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        videoRename(file);
                        bottomSheetDialog.dismiss();

                    }
                });

                bottomSheetDialog.setContentView(view);
                bottomSheetDialog.show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoClickInterface.onVideoClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoRVModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView thumbnail, ivThreeDate,ivShareBtn;
        private TextView tvTitle, tvLength;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.IVThumbnail);
            tvTitle = itemView.findViewById(R.id.tv_videoName);
            tvLength = itemView.findViewById(R.id.tv_videoLenght);
            ivThreeDate = itemView.findViewById(R.id.iv_threeDate);
            ivShareBtn = itemView.findViewById(R.id.iv_shareButton);


        }
    }


    public interface VideoClickInterface {
        void onVideoClick(int position);
    }

    private String converter(long by) {
        double kb = by / 1024;
        double mb = kb / 1024;
        double mbReminder = kb % 1024;
        String s = String.format("%.2f", mb);
        return s + " MB";
    }


    private void videoRename(File file) {

//        File newfile = myList.getFile();

        String oldname = file.getName().toString();
        String ext = oldname.substring(oldname.lastIndexOf("."));

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        EditText etRename = new EditText(context);
        etRename.setText(oldname);
        builder.setView(etRename);
        builder.setNegativeButton("Cancel",null);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = etRename.getText().toString().trim();
                if (newName.isEmpty()) {
                    Toast.makeText(context, "filed", Toast.LENGTH_SHORT).show();
                } else {
                    File file1 = new File(Folder.getFile(context), "/" + newName + ext);
                    file.renameTo(file1);
                    getVideos();

                }
            }
        });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
//

    }

    private void getVideos() {

        File folder = Folder.getFile(context);
        videoRVModelArrayList.removeAll(videoRVModelArrayList);


        if (folder.exists()) {
            File[] files = folder.listFiles();
            for (File file : files) {
                Uri uri = Uri.fromFile(file);
                Bitmap videothumbnail = ThumbnailUtils.createVideoThumbnail(uri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                videoRVModelArrayList.add(new VideoRVModel(file.getName(), uri.getPath(), videothumbnail));

            }
            this.notifyDataSetChanged();

        }

    }

    private void deleteVideo(File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Delete File")
                .setMessage("Are you sure you want to delete pdf");
        builder.setPositiveButton("Delete ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                file.delete();
                getVideos();
            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void shareVideo(File file) {

        ContentResolver resolver = context.getContentResolver();
        Intent shareintent=new Intent("android.intent.action.SEND");
        shareintent.setType("video/mp4");
        shareintent.putExtra("android.intent.extra.STREAM",
                Uri.parse(file.getPath()));
        context.startActivity(Intent.createChooser(shareintent,"share"));

    }
}
