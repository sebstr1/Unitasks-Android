package com.sest1601.bathingsites.new_site;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sest1601.bathingsites.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewBathingSiteFragment extends Fragment {

    public NewBathingSiteFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_bathing_site, container, false);
    }
}
