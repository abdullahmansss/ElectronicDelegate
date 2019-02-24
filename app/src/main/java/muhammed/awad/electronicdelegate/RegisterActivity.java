package muhammed.awad.electronicdelegate;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import muhammed.awad.electronicdelegate.Models.CompanyModel;

public class RegisterActivity extends AppCompatActivity
{
    public static final int RESULT_LOAD_IMAGE = 1;

    Button get_started,sign_in;

    EditText firstname,lastname,email,mobilenumber,company_title,building,street,district,governorate;
    Button verify_btn,cancel,select_images_btn,complete_btn;
    RecyclerView images_recyclerview;
    PinView pinView;

    List<Uri> fileDone;
    UploadListAdapter uploadListAdapter;

    ProgressDialog progressDialog;

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    String codeSent,mobile,first,last,all,email_Address,title,b,s,d,g;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        get_started = findViewById(R.id.getstarted_btn);
        sign_in = findViewById(R.id.signin_btn);

        fileDone = new ArrayList<>();

        uploadListAdapter = new UploadListAdapter(fileDone);

        get_started.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                OnVerificationStateChanged();

                showCompleteDialog();
            }
        });
    }

    private void showVerificationDialog()
    {
        final Dialog dialog = new Dialog(RegisterActivity.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.mobile_verification_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes();
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        pinView = dialog.findViewById(R.id.code_field);

        verify_btn = dialog.findViewById(R.id.verify_btn);
        cancel = dialog.findViewById(R.id.cancel_btn);

        verify_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String code = pinView.getText().toString();

                if (TextUtils.isEmpty(code) || code.equals(codeSent) )
                {
                    Toast.makeText(getApplicationContext(), "please enter a valid verification code", Toast.LENGTH_SHORT).show();
                    pinView.requestFocus();
                    return;
                }

                progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setTitle("Verification Code");
                progressDialog.setMessage("Please Wait Until Verify Your Number and Creating Account ...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                progressDialog.setCancelable(false);

                signInWithPhoneAuthCredential(SignIn(code));
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void showCompleteDialog()
    {
        final Dialog dialog = new Dialog(RegisterActivity.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.create_account_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes();
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        firstname = dialog.findViewById(R.id.first_name_field);
        lastname = dialog.findViewById(R.id.last_name_field);
        email = dialog.findViewById(R.id.company_email_field);
        mobilenumber = dialog.findViewById(R.id.mobile_field);
        building = dialog.findViewById(R.id.building_field);
        street = dialog.findViewById(R.id.street_field);
        district = dialog.findViewById(R.id.district_field);
        governorate = dialog.findViewById(R.id.governorate_field);
        company_title = dialog.findViewById(R.id.company_title_field);

        select_images_btn = dialog.findViewById(R.id.select_images_btn);
        complete_btn = dialog.findViewById(R.id.send_code_btn);
        cancel = dialog.findViewById(R.id.cancel_btn);

        images_recyclerview = dialog.findViewById(R.id.images_recyclerview);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        images_recyclerview.setLayoutManager(layoutManager);
        images_recyclerview.setHasFixedSize(true);
        images_recyclerview.setAdapter(uploadListAdapter);

        select_images_btn.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Images"), RESULT_LOAD_IMAGE);
            }
        });

        complete_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                first = firstname.getText().toString();
                last = lastname.getText().toString();
                all = first + " " + last;
                email_Address = email.getText().toString();
                mobile = mobilenumber.getText().toString();
                title = company_title.getText().toString();
                b = building.getText().toString();
                s = street.getText().toString();
                d = district.getText().toString();
                g = governorate.getText().toString();

                if (TextUtils.isEmpty(first))
                {
                    Toast.makeText(getApplicationContext(), "please enter your first name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(last))
                {
                    Toast.makeText(getApplicationContext(), "please enter your last name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email_Address))
                {
                    Toast.makeText(getApplicationContext(), "please enter your email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(mobile))
                {
                    Toast.makeText(getApplicationContext(), "please enter your mobile number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(title))
                {
                    Toast.makeText(getApplicationContext(), "please enter company title", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(b))
                {
                    Toast.makeText(getApplicationContext(), "please enter building number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(s))
                {
                    Toast.makeText(getApplicationContext(), "please enter street name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(d))
                {
                    Toast.makeText(getApplicationContext(), "please enter district name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(g))
                {
                    Toast.makeText(getApplicationContext(), "please enter governorate name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (fileDone.size() == 0)
                {
                    Toast.makeText(getApplicationContext(), "please select images", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setTitle("Verification Code");
                progressDialog.setMessage("Please Wait Until Sending Code ...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                progressDialog.setCancelable(false);

                startPhoneNumberVerification(mobile);

                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void startPhoneNumberVerification(String phoneNumber)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+2" + phoneNumber,                // Phone number to verify
                60,                              // Timeout duration
                TimeUnit.SECONDS,        // Unit of timeout
                this,                // Activity (for callback binding)
                callbacks);                 // OnVerificationStateChangedCallbacks
    }

    public void OnVerificationStateChanged ()
    {
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {

            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onVerificationFailed(FirebaseException e)
            {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "code can\'t send to : " + mobile, Toast.LENGTH_SHORT).show();
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken)
            {
                super.onCodeSent(s, forceResendingToken);
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "code sent to : " + mobile, Toast.LENGTH_SHORT).show();
                codeSent = s;
                showVerificationDialog();
            }
        };
    }

    public PhoneAuthCredential SignIn (String code)
    {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);

        return credential;
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            uploadImages();
                            CompanyModel companyModel = new CompanyModel(all,email_Address,mobile,b,s,d,g,title);
                            databaseReference.child("AllUsers").child("Companies").child(getUID()).setValue(companyModel);

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else
                            {
                                progressDialog.dismiss();
                                String error_message = task.getException().getMessage();
                                Toast.makeText(getApplicationContext(), error_message, Toast.LENGTH_SHORT).show();
                            }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK)
        {
            if (data != null)
            {
                if (data.getClipData() != null)
                {
                    int total_images_selected = data.getClipData().getItemCount();

                    for (int i = 0 ; i < total_images_selected ; i ++)
                    {
                        Uri image_uri = data.getClipData().getItemAt(i).getUri();
                        fileDone.add(image_uri);
                        uploadListAdapter.notifyDataSetChanged();
                    }
                } else if (data.getData() != null)
                {
                    Uri image_uri = data.getData();
                    fileDone.add(image_uri);
                    uploadListAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.Viewholder>
    {
        List<Uri> fileDonelist;

        UploadListAdapter(List<Uri> fileDonelist)
        {
            this.fileDonelist = fileDonelist;
        }

        @NonNull
        @Override
        public Viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_item, viewGroup, false);
            return new  Viewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Viewholder viewholder, int i)
        {
            Uri filedone = fileDonelist.get(i);

            Picasso.get()
                    .load(filedone)
                    .placeholder(R.drawable.company)
                    .error(R.drawable.company)
                    .into(viewholder.imageView);
        }

        @Override
        public int getItemCount()
        {
            return fileDonelist.size();
        }

        class Viewholder extends RecyclerView.ViewHolder
        {
            View view;

            ImageView imageView;

            Viewholder(@NonNull View itemView)
            {
                super(itemView);

                view = itemView;

                imageView = view.findViewById(R.id.image_view);
            }
        }
    }

    public void uploadImages()
    {
        UploadTask uploadTask;

        final StorageReference ref = storageReference.child("images");

        Uri image_url;

        Task<Uri> urlTask;

        for (int i = 0 ; i < fileDone.size() ; i ++)
        {
            image_url = fileDone.get(i);

            ref.child(image_url.getLastPathSegment());

            uploadTask = ref.putFile(image_url);

            urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnSuccessListener(new OnSuccessListener<Uri>()
            {
                @Override
                public void onSuccess(Uri uri)
                {

                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception exception)
                {
                    // Handle unsuccessful uploads
                    Toast.makeText(getApplicationContext(), "Can't Upload Photo", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
    }

    private String getUID()
    {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return id;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed()
    {
        finishAffinity();
    }
}
