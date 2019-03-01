package muhammed.awad.electronicdelegate.PharmacyApp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import muhammed.awad.electronicdelegate.R;

public class NewsFeedFragment extends Fragment
{
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.pharmacy_news_fragment, container, false);

        return view;
    }
}
