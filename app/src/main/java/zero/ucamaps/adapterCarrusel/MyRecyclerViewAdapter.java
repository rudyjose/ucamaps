package zero.ucamaps.adapterCarrusel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import zero.ucamaps.R;


public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<Integer> mViewImage = Collections.emptyList();
    private List<String> mTexto = Collections.emptyList();
    private List<pasosInfo> infopasos;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;


    // data is passed into the constructor
   public MyRecyclerViewAdapter(Context context, List<Integer> image, List<String> texto) {
    //public MyRecyclerViewAdapter(Context context, List<pasosInfo> pasos) {
       this.mInflater = LayoutInflater.from(context);
       this.mViewImage = image;
        this.mTexto = texto;
       // this.infopasos=pasos;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int color = mViewImage.get(position);
        String d = mTexto.get(position);

      // holder.myView.setBackgroundColor(color);
        holder.myView.setImageResource(color);
        holder.myTextView.setText(d);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mTexto.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView myView;
        public TextView myTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            myView = (ImageView) itemView.findViewById(R.id.imageViewC);
            myTextView = (TextView) itemView.findViewById(R.id.tvTexto);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return mTexto.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
