package com.applex.inc.stiqrit.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.applex.inc.stiqrit.ModelItems.historyItems;
import com.applex.inc.stiqrit.R;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ProgrammingViewHolder> {

    private ArrayList<historyItems> mList;
    private Context mContext;

    public HistoryAdapter(ArrayList<historyItems> list, Context context) {
        mList = list;
        mContext = context;
    }

        private OnItemClickListener mListener;

        public interface OnItemClickListener
        {
            void onItemClick(int position);
//            void onShareClick(int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener)
        {
            mListener = listener;
        }



        public class ProgrammingViewHolder extends RecyclerView.ViewHolder{

            public TextView mTextTitle;
            public TextView mTextDesc;
//            public ImageButton shareimg;
            public TextView mdateView;

            public ProgrammingViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
                super(itemView);
                mTextTitle = itemView.findViewById(R.id.text_title);
                mTextDesc = itemView.findViewById(R.id.text_desc);
//                shareimg = itemView.findViewById(R.id.sharecard);
                mdateView = itemView.findViewById(R.id.date_text);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener != null){
                            int position = getAdapterPosition();
                            if(position != RecyclerView.NO_POSITION)
                            {
                                listener.onItemClick(position);
                            }
                        }
                    }
                });

//                    shareimg.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if(listener != null){
//                                int position = getAdapterPosition();
//                                if(position != RecyclerView.NO_POSITION)
//                                {
//                                    listener.onShareClick(position);
//                                }
//                            }
//                        }
//                    });

            }
        }


        @NonNull
        @Override
        public ProgrammingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.history_list_item_layout,viewGroup,false);
            ProgrammingViewHolder evh = new ProgrammingViewHolder(v, mListener);
            return evh;
        }

        @Override
        public void onBindViewHolder(@NonNull ProgrammingViewHolder programmingViewHolder, int i) {
            historyItems currentItem = mList.get(i);
            programmingViewHolder.mTextTitle.setText(currentItem.getmTitle());
            programmingViewHolder.mTextDesc.setText(currentItem.getmDesc());
            programmingViewHolder.mdateView.setText(currentItem.getmDate());
        }

//        @Override
//        public int getItemViewType(int position) {
//            return position;
//        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void restoreItem(historyItems item,int position){

        }
    }



