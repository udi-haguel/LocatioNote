package dev.haguel.locationote.fragments.list_frag;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import dev.haguel.locationote.DatabaseManager;
import dev.haguel.locationote.R;

public class NoteListFragment extends Fragment {


    public static NoteListFragment newInstance() {
        return new NoteListFragment();
    }

    private RecyclerView rvNoteList;
    private LottieAnimationView lottieEmpty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_note_list, container, false);

        rvNoteList = root.findViewById(R.id.rvNoteList);
        lottieEmpty = root.findViewById(R.id.lottieEmpty);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        if (DatabaseManager.instance().getNoteArrayList().size() > 0) {
            NoteListAdapter adapter = new NoteListAdapter(this, DatabaseManager.instance().getNoteArrayList());
            rvNoteList.setAdapter(adapter);
            rvNoteList.setLayoutManager(new LinearLayoutManager(getContext()));

            rvNoteList.setVisibility(View.VISIBLE);
            lottieEmpty.setVisibility(View.GONE);
        } else {
            rvNoteList.setVisibility(View.GONE);
            lottieEmpty.setVisibility(View.VISIBLE);

        }
    }
}