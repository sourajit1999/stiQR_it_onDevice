package com.applex.inc.stiqrit.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.applex.inc.stiqrit.ModelItems.gridItems;
import com.applex.inc.stiqrit.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class gridAdapter extends RecyclerView.Adapter<gridAdapter.GridViewHolder> {

    private ArrayList<gridItems> mList;
    private Context mContext;


    public gridAdapter(ArrayList<gridItems> list, Context context) {
        mList = list;
        mContext = context;
    }

    private OnItemClickListener mListener;
    private OnItemLongClickListener mlonglistener;

    public interface OnItemClickListener
    {
        void onItemClick(int position);
        void onShareClick(int position);
    }

    public interface OnItemLongClickListener
    {
        void onItemLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) { mlonglistener = listener; }


    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.grid_item,viewGroup,false);
        GridViewHolder evh = new GridViewHolder(v, mListener, mlonglistener );
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder gridViewHolder, int i) {
        gridItems currentItem = mList.get(i);
        gridViewHolder.bind(currentItem);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

//        public void restoreItem(Items item,int position){
//
//        }


    public class GridViewHolder extends RecyclerView.ViewHolder{

        private ImageView mImageView;
        private TextView mTextView;
        //        private ImageButton shareimg;
//        private TextView mdateView;
        private LinearLayout mlistitem;


        public GridViewHolder(@NonNull View itemView, final OnItemClickListener listener,final OnItemLongClickListener llistener) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.thumbnail);
            mTextView = itemView.findViewById(R.id.title);
//            shareimg = itemView.findViewById(R.id.sharecard);
//            mdateView = itemView.findViewById(R.id.date_text);

//            mlistitem = itemView.findViewById(R.id.list_item) ;  //////ADDED

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
//                                if(!mImageView.getImageMatrix().equals(R.drawable.ic_check_circle_black_24dp)){
//                                    mImageView.setImageLevel(R.drawable.ic_check_circle_black_24dp);
//                                    mlistitem.setBackgroundResource(R.color.colorgrey);
//                                }
//                                else{
//                                    mImageView.setImageResource(R.drawable.ic_check_circle_black_24dp);
//                                    mlistitem.setBackgroundResource(R.color.colorbase);
//                                }
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onItemClick(position);
                        }

                    }
                }
            });

//            shareimg.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(listener != null){
//                        int position = getAdapterPosition();
//                        if(position != RecyclerView.NO_POSITION)
//                        {
//                            listener.onShareClick(position);
//                        }
//                    }
//                }
//            });

//
//            itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    if (llistener != null) {
//
//                        if (.mActionMode != null) {
//                            return false;
//                        }
//                        else {
//                            int position = getAdapterPosition();
//                            if (position != RecyclerView.NO_POSITION) {
//                                llistener.onItemLongClick(position);
//                            }
//                            return false;
//                        }
//                    }
//                    return false;
//                }
//            });

        }



        void bind(final gridItems item) {

            mTextView.setText(item.getmName());
//            mdateView.setText(item.getmText2());
//            mImageView.setImageURI(item.getUri());
            Picasso.with(mContext).load(item.getmResourceImage()).placeholder(R.drawable.ic_photo_black_24dp).into(mImageView);

//            mlistitem.setBackgroundResource(R.drawable.custom_ripple_list);

//            if(mActionMode != null){
//                shareimg.setVisibility(View.GONE);
//            }
//            if(item.isChecked()){
//                mlistitem.setBackgroundResource(colorSelection);
//                mImageView.setImageResource(R.drawable.ic_check_circle_black_24dp);
//            }
//            else if(mActionMode == null){
//                shareimg.setVisibility(View.VISIBLE);
//                mlistitem.setBackgroundResource(R.drawable.custom_ripple_list);
//                mImageView.setImageResource(R.drawable.ic_camera_black_24dp);
//            }

        }
    }

//        public ArrayList<Items> getSelected() {
//            ArrayList<Items> selected = new ArrayList<>();
//            for (int i = 0; i < mList.size(); i++) {
//                if (mList.get(i).isChecked()) {
//                    selected.add(mList.get(i));
//                }
//            }
//            return selected;
//        }

}