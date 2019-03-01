package muhammed.awad.electronicdelegate.PharmacyApp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.victor.loading.rotate.RotateLoading;

import de.hdodenhof.circleimageview.CircleImageView;
import muhammed.awad.electronicdelegate.Models.CompanyModel;
import muhammed.awad.electronicdelegate.Models.MedicineModel;
import muhammed.awad.electronicdelegate.Models.OrderModel;
import muhammed.awad.electronicdelegate.PharmacyApp.Fragments.AllPharmaceuticalsFragment;
import muhammed.awad.electronicdelegate.R;

public class AddtoCartActivity extends AppCompatActivity
{
    String KEY;

    CircleImageView pharmaceutical_image;
    TextView pharmaceutical_name,pharmaceutical_price,pharmaceutical_info,quantity_txt,pharmaceutical_company_name_field;
    Button add_pharmaceutical_btn;

    String pharma_image,pharma_name,pharma_location,company_uid,order_name,pharma_price;

    FloatingActionButton minus_btn,add_btn;

    int i = 0;
    int o = 0;

    RotateLoading rotateLoading;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addto_cart);

        KEY = getIntent().getStringExtra(AllPharmaceuticalsFragment.EXTRA_PHARMACEUTICAL_ADD_TO_CART_KEY);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);

        pharmaceutical_image = findViewById(R.id.pharmaceutical_image);
        pharmaceutical_name = findViewById(R.id.pharmaceutical_name_field);
        pharmaceutical_price = findViewById(R.id.pharmaceutical_price_field);
        pharmaceutical_info = findViewById(R.id.pharmaceutical_info_field);
        pharmaceutical_company_name_field = findViewById(R.id.pharmaceutical_company_name_field);
        quantity_txt = findViewById(R.id.quantity_txt);

        add_pharmaceutical_btn = findViewById(R.id.addtocart_btn);

        minus_btn = findViewById(R.id.minus_btn);
        add_btn = findViewById(R.id.add_btn);

        rotateLoading = findViewById(R.id.rotateloading);

        rotateLoading.start();

        returndata();
        returndata(KEY);

        quantity_txt.setText("" + i);

        minus_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (i == 0 )
                {
                    Toast.makeText(getApplicationContext(), "can't order less than 0 item", Toast.LENGTH_SHORT).show();
                } else
                    {
                        i = i - 1;
                        quantity_txt.setText("" + i);
                    }
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i == 5)
                {
                    Toast.makeText(getApplicationContext(), "can't order more than 5 items", Toast.LENGTH_SHORT).show();
                } else
                    {
                        i = i + 1;
                        quantity_txt.setText("" + i);
                    }
            }
        });

        add_pharmaceutical_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (i == 0)
                {
                    Toast.makeText(getApplicationContext(), "you can't order less than 0 item", Toast.LENGTH_SHORT).show();
                } else
                    {
                        int all = i * o;
                        String allprice = String.valueOf(all);

                        AddtoDb(pharma_image,order_name,pharma_name,allprice,pharma_location, "" + i);
                    }
            }
        });
    }

    public void AddtoDb(String image,String name,String pharmacy,String price, String location, String q)
    {
        OrderModel orderModel = new OrderModel(image,name,pharmacy,price,location,q);

        String key = databaseReference.child("requests").push().getKey();

        databaseReference.child("requests").child(company_uid).child(key).setValue(orderModel);
        databaseReference.child("pharmacyrequest").child(getUID()).child(key).setValue(orderModel);

        Toast.makeText(getApplicationContext(), "Ordered Successfully ..", Toast.LENGTH_SHORT).show();

        Intent intent= new Intent(getApplicationContext(), PharmacyMainActivity.class);
        startActivity(intent);
    }

    public void returndata(String key)
    {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);


        mDatabase.child("Allpharmaceutical").child(key).addListenerForSingleValueEvent(
                new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        rotateLoading.stop();
                        // Get user value
                        MedicineModel medicineModel = dataSnapshot.getValue(MedicineModel.class);

                        pharma_image = medicineModel.getImageurl();
                        order_name = medicineModel.getName();
                        pharma_price = medicineModel.getPrice();

                        String string = pharma_price;

                        String[] parts = string.split(" ");
                        String pr = parts[0];

                        o = Integer.parseInt(pr);

                        pharmaceutical_name.setText(medicineModel.getName());
                        pharmaceutical_price.setText(medicineModel.getPrice());
                        pharmaceutical_info.setText(medicineModel.getInfo());
                        pharmaceutical_company_name_field.setText("From : " + medicineModel.getCompany_name());

                        company_uid = medicineModel.getCompany_uid();

                        Picasso.get()
                                .load(medicineModel.getImageurl())
                                .placeholder(R.drawable.addphoto)
                                .error(R.drawable.addphoto)
                                .into(pharmaceutical_image);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        Toast.makeText(getApplicationContext(), "can\'t fetch data", Toast.LENGTH_SHORT).show();
                        rotateLoading.stop();
                    }
                });
    }

    public void returndata()
    {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);


        mDatabase.child("AllUsers").child("Pharmacies").child(getUID()).addListenerForSingleValueEvent(
                new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        // Get user value
                        CompanyModel companyModel = dataSnapshot.getValue(CompanyModel.class);

                        pharma_location = companyModel.getGovernorate() + ", " + companyModel.getDistrict() + ", " + companyModel.getStreet() + ", " + companyModel.getBuilding();
                        pharma_name = companyModel.getTitle();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        Toast.makeText(getApplicationContext(), "can\'t fetch data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public String getUID()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        return userId;
    }
}
