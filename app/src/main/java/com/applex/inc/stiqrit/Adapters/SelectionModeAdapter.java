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

public class SelectionModeAdapter extends RecyclerView.Adapter<SelectionModeAdapter.ProgrammingViewHolder> {
    private ArrayList<historyItems> mList;
     private Context mContext;
    private SelectionModeAdapter.OnItemClickListener mListener;

    public SelectionModeAdapter(ArrayList<historyItems> list, Context context) {
        mList = list;
        mContext = context;
    }
    public interface OnItemClickListener {
        void onItemClick(int position);

    }
    public void setOnItemClickListener(SelectionModeAdapter.OnItemClickListener listener)
    {
        mListener = listener;
    }
    @Override
    public int getItemCount() {
        return 0;
    }


    public class ProgrammingViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextTitle;
            public TextView mTextDesc;
            public TextView mdateView;

            public ProgrammingViewHolder(@NonNull View itemView, final SelectionModeAdapter.OnItemClickListener listener) {
                super(itemView);
                mTextTitle = itemView.findViewById(R.id.text_title);
                mTextDesc = itemView.findViewById(R.id.text_desc);
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
            }
        }

        @NonNull
        public SelectionModeAdapter.ProgrammingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.history_list_item_layout,viewGroup,false);
            SelectionModeAdapter.ProgrammingViewHolder evh = new SelectionModeAdapter.ProgrammingViewHolder(v, mListener);
            return evh;
        }

    @Override
    public void onBindViewHolder(@NonNull SelectionModeAdapter.ProgrammingViewHolder holder, int position) {
        historyItems currentItem = mList.get(position);
        holder.mTextTitle.setText(currentItem.getmTitle());
        holder.mTextDesc.setText(currentItem.getmDesc());
        holder.mdateView.setText(currentItem.getmDate());
    }


}

