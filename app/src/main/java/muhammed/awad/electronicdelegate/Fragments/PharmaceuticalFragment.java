package muhammed.awad.electronicdelegate.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import com.victor.loading.rotate.RotateLoading;

import muhammed.awad.electronicdelegate.Models.MedicineModel;
import muhammed.awad.electronicdelegate.R;

public class PharmaceuticalFragment extends Fragment
{
    View view;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    FirebaseRecyclerAdapter<MedicineModel, pharmaceuticalViewholder> firebaseRecyclerAdapter;

    RotateLoading rotateLoading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.pharmaceutical_fragment, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        recyclerView = view.findViewById(R.id.doctors_recyclerview);
        rotateLoading = view.findViewById(R.id.rotateloading);

        rotateLoading.start();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        DisplayallMedicines();
    }

    private void DisplayallMedicines()
    {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("pharmaceutical")
                .limitToLast(50);

        FirebaseRecyclerOptions<MedicineModel> options =
                new FirebaseRecyclerOptions.Builder<MedicineModel>()
                        .setQuery(query, MedicineModel.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MedicineModel, pharmaceuticalViewholder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull pharmaceuticalViewholder holder, int position, @NonNull final MedicineModel model)
            {
                rotateLoading.stop();

                String key = getRef(position).getKey();

                holder.BindPlaces(model);
            }

            @NonNull
            @Override
            public pharmaceuticalViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.pharmaceutical_item, parent, false);
                return new pharmaceuticalViewholder(view);
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        rotateLoading.stop();
    }

    public static class pharmaceuticalViewholder extends RecyclerView.ViewHolder
    {
        ImageView medicine_image;
        TextView medicine_name,medicine_price,medicine_info;

        pharmaceuticalViewholder(View itemView)
        {
            super(itemView);

            medicine_image = itemView.findViewById(R.id.medicine_image);
            medicine_name = itemView.findViewById(R.id.medicine_name);
            medicine_price = itemView.findViewById(R.id.medicine_price);
            medicine_info = itemView.findViewById(R.id.medicine_info);
        }

        void BindPlaces(final MedicineModel medicineModel)
        {
            medicine_name.setText(medicineModel.getName());
            medicine_price.setText(medicineModel.getPrice());
            medicine_info.setText(medicineModel.getInfo());

            Picasso.get()
                    .load(medicineModel.getImageurl())
                    .placeholder(R.drawable.logo22)
                    .error(R.drawable.logo22)
                    .into(medicine_image);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (firebaseRecyclerAdapter != null)
        {
            firebaseRecyclerAdapter.startListening();
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();

        if (firebaseRecyclerAdapter != null)
        {
            firebaseRecyclerAdapter.stopListening();
        }
    }
}
