package com.example.stayfit.WeightManagement.Database;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.stayfit.R;

import java.util.List;

/**
 * Adapter-Klasse zur Darstellung von Gewichtseinträgen in einer RecyclerView.
 */
public class WeightEntryAdapter extends RecyclerView.Adapter<WeightEntryAdapter.WeightEntryViewHolder> {

    // Liste von Gewichtseinträgen, die angezeigt werden sollen
    private List<WeightEntryItem> weightEntryItems;

    /**
     * ViewHolder-Klasse, die das Layout für einzelne Elemente in der RecyclerView definiert.
     */
    public static class WeightEntryViewHolder extends RecyclerView.ViewHolder  {

        // TextView für das Anzeigen des Datums
        public TextView dateView;

        // TextView für das Anzeigen des Gewichts
        public TextView weightView;

        // Das View-Objekt, das das gesamte Listenelement darstellt
        public View itemView;

        public WeightEntryViewHolder(View v) {
            super(v);
            dateView = v.findViewById(R.id.date_view);
            weightView = v.findViewById(R.id.weight_view);
            itemView = v;
        }
    }

    /**
     * Konstruktor für den WeightEntryAdapter.
     *
     * @param weightEntryItems Liste von Gewichtseinträgen, die angezeigt werden sollen.
     */
    public WeightEntryAdapter(List<WeightEntryItem> weightEntryItems) {
        this.weightEntryItems = weightEntryItems;
    }

    @Override
    public WeightEntryAdapter.WeightEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflation des Layouts für einzelne Gewichtseinträge
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weight_entry_item, parent, false);
        return new WeightEntryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(WeightEntryViewHolder holder, int position) {
        // Abrufen des aktuellen Gewichtseintrags
        WeightEntryItem currentItem = weightEntryItems.get(position);

        // Setzen des Textes für Datum und Gewicht
        holder.dateView.setText(currentItem.date);
        holder.weightView.setText(String.valueOf(currentItem.weightWithUnit));

        // Hintergrundfarbe basierend auf dem ausgewählten Status setzen
        holder.itemView.setBackgroundColor(currentItem.selected ? Color.LTGRAY : Color.WHITE);

        // OnClickListener, um den ausgewählten Status zu wechseln, wenn das Element angeklickt wird
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
        // Gibt die Anzahl der Elemente in der Liste zurück
        return weightEntryItems.size();
    }
}


