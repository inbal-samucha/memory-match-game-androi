package bar.example.memoryplay;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

public class RecordsActivity extends AppCompatActivity {

    SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.records_activity);

        sp = getSharedPreferences("settings",MODE_PRIVATE);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Gson gson = new Gson();
        Records records;
        String json = sp.getString("Records",null);
        if(json == null)
            records = new Records();
        else
            records = gson.fromJson(json,Records.class);

        RecordsAdapter recordsAdapter = new RecordsAdapter(records);
        recyclerView.setAdapter(recordsAdapter);

           /* for (int i = 0; i < records.names.size(); i++) {
                TableRow tableRow = new TableRow(this);
                TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                tableRow.setLayoutParams(tableLayoutParams);

                TextView name = new TextView(this);
                TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
                tableRowParams.weight = 1;
                name.setLayoutParams(tableRowParams);
                name.setText(records.names.get(i).toString());
                name.setTypeface(Typeface.SANS_SERIF);
                name.setTextSize(25f);
                name.setTextColor(Color.BLACK);
                name.setGravity(Gravity.CENTER);
                tableRow.addView(name);

                TextView turn = new TextView(this);
                turn.setLayoutParams(tableRowParams);
                turn.setText(String.valueOf(records.turns.get(i)));
                turn.setTypeface(Typeface.SANS_SERIF);
                turn.setTextSize(25f);
                turn.setTextColor(Color.BLACK);
                turn.setGravity(Gravity.CENTER);
                tableRow.addView(turn);

                tableLayout.addView(tableRow);
            }*/
        }
}
