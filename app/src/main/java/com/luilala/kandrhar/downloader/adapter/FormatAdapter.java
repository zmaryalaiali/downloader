package com.luilala.kandrhar.downloader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luilala.kandrhar.downloader.R;
import com.luilala.kandrhar.downloader.model.FormatModel;

import java.util.List;

public class FormatAdapter extends RecyclerView.Adapter<FormatAdapter.FormatHolder> {
    private Context context;
    private List<FormatModel> list;
    String title;
    public FormatAdapter(Context context, List<FormatModel> list,    String title) {
        this.context = context;
        this.list = list;
        this.title = title;
    }
    @NonNull
    @Override
    public FormatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_format,parent,false);
        return  new FormatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FormatHolder holder, int position) {

        FormatModel formatModel = list.get(position);

        holder.tvTitle.setText(formatModel.getTitle());

        NestedFormatAdapter nestedFormatAdapter = new NestedFormatAdapter(context,formatModel.getNestedFormatModelList(),title);
        holder.recyclerViewFormats.setLayoutManager(new GridLayoutManager(context,2));

               holder.recyclerViewFormats. setAdapter(nestedFormatAdapter);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class FormatHolder extends RecyclerView.ViewHolder{

        RecyclerView recyclerViewFormats;
        TextView tvTitle;
        public FormatHolder(@NonNull View itemView) {
            super(itemView);
            recyclerViewFormats = itemView.findViewById(R.id.rv_formats);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }
}
