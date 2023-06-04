package com.example.stayfit.WeightManagement.Database;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.stayfit.R;

import java.util.List;

public class WeightEntryAdapter extends RecyclerView.Adapter<WeightEntryAdapter.WeightEntryViewHolder> {
    private List<WeightEntryItem> weightEntryItems;

    public static class WeightEntryViewHolder extends RecyclerView.ViewHolder  {
        public TextView dateView;
        public TextView weightView;

        public View itemView;

        public WeightEntryViewHolder(View v) {
            super(v);
            dateView = v.findViewById(R.id.date_view);
            weightView = v.findViewById(R.id.weight_view);
            itemView = v;
        }
    }

    public WeightEntryAdapter(List<WeightEntryItem> weightEntryItems) {
        this.weightEntryItems = weightEntryItems;
    }

    @Override
    public WeightEntryAdapter.WeightEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weight_entry_item, parent, false);
        return new WeightEntryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(WeightEntryViewHolder holder, int position) {
        WeightEntryItem currentItem = weightEntryItems.get(position);
        holder.dateView.setText(currentItem.date);
        holder.weightView.setText(String.valueOf(currentItem.weightWithUnit));

        holder.itemView.setBackgroundColor(currentItem.selected ? Color.LTGRAY : Color.WHITE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentItem.selected = !currentItem.selected;
                holder.itemView.setBackgroundColor(currentItem.selected ? Color.LTGRAY : Color.WHITE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return weightEntryItems.size();
    }
}

