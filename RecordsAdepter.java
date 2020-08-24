package bar.example.memoryplay;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.RecordsViewHolder> {

    private Records records;

    public RecordsAdapter(Records records) {
        this.records = records;
    }

    public class RecordsViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView turns;
        public RecordsViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_tv);
            turns = itemView.findViewById(R.id.turns_tv);
        }
    }

    @NonNull
    @Override
    public RecordsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_records,viewGroup,false);
        RecordsViewHolder recordsViewHolder = new RecordsViewHolder(view);
        return recordsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecordsViewHolder recordsViewHolder, int i) {
        recordsViewHolder.name.setText(records.names.get(i));
        recordsViewHolder.turns.setText(String.valueOf(records.turns.get(i)));
    }

    @Override
    public int getItemCount() {
        return records.names.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}

