package me.nomi.urdutyper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    String[] filenames;
    String[] urls;
    String uid;
    Context context;
    OnImageClickListener mOnImageClickListener;

    public MyAdapter(String[] filenames, String[] urls, String uid, Context context, OnImageClickListener mOnImageClickListener) {
        this.filenames = filenames;
        this.uid = uid;
        this.context = context;
        this.urls = urls;
        this.mOnImageClickListener = mOnImageClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.singlerow, parent, false);
        return new ViewHolder(view, mOnImageClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(filenames[position]);
        Picasso.get().load(urls[position]).error(R.drawable.not_found).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(urls[holder.getAdapterPosition()]).resize(500, 500).centerInside().error(R.drawable.not_found).into(holder.imageView);
            }
        });

    }

    @Override
    public int getItemCount() {
        return filenames.length;
    }

    public interface OnImageClickListener {
        void onImageClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textView;
        OnImageClickListener onImageClickListener;

        public ViewHolder(@NonNull View itemView, OnImageClickListener onImageClickListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            this.onImageClickListener = onImageClickListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onImageClickListener.onImageClick(getAdapterPosition());
        }
    }
}
