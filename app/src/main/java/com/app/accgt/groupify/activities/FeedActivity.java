package com.app.accgt.groupify.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.accgt.groupify.R;
import com.app.accgt.groupify.models.Event;
import com.app.accgt.groupify.utils.EventHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FeedActivity extends AppCompatActivity {
    private static final String TAG = FeedActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;
    private Button addEventButton;

    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        addEventButton = findViewById(R.id.add_event_button);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        context = this;

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Query query = FirebaseFirestore.getInstance()
                .collection("events")
                .orderBy("timestamp");
        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
                .setQuery(query, Event.class)
                .build();

        Log.d(TAG, "Creating adapter");
        adapter = new FirestoreRecyclerAdapter<Event, EventHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final EventHolder holder, final int position, @NonNull Event model) {
                holder.name.setText(model.getName());
                holder.participantsNumber.setText(String.valueOf(model.getUsers().size()));
                holder.duration.setText(String.valueOf(model.getDuration()));
                Log.d(TAG, "Bound event " + model.getName());

                holder.eventItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, EventActivity.class);
                        String eventId = getSnapshots().getSnapshot(holder.getAdapterPosition()).getId();
                        intent.putExtra("eventId", eventId);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public EventHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recycler_item, viewGroup, false);
                Log.d(TAG, "Created EventHolder");
                return new EventHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddEventActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}