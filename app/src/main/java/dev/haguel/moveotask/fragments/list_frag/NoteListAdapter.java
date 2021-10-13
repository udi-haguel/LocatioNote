package dev.haguel.moveotask.fragments.list_frag;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import dev.haguel.moveotask.DatabaseManager;
import dev.haguel.moveotask.R;
import dev.haguel.moveotask.Utils;
import dev.haguel.moveotask.activities.NoteActivity;
import dev.haguel.moveotask.entities.NoteEntity;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NotesViewHolder> {

    List<NoteEntity> notes = new ArrayList<>();
    NoteListFragment noteListFragment;

    public NoteListAdapter(NoteListFragment noteListFragment, List<NoteEntity> notes){
        this.notes.clear();
        this.notes = notes;
        this.noteListFragment = noteListFragment;
    }


    @NonNull
    @Override
    public NoteListAdapter.NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.single_note, parent, false);
        return new NotesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteListAdapter.NotesViewHolder holder, int position) {
        holder.tvTitle.setText(notes.get(position).getTitle());
        holder.tvDate.setText(Utils.longToDateFormatter(notes.get(position).getDate(), false));

        holder.itemView.setOnClickListener(v->{
            NoteActivity.startNotActivity(noteListFragment.getActivity(), notes.get(position));
        });

        holder.ivDelete.setOnClickListener(v->{
            DatabaseManager.instance().deleteNote(notes.get(position).getCreated());
            notifyDataSetChanged();
            noteListFragment.updateUI();
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }


    public static class NotesViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle;
        TextView tvDate;
        ImageView ivDelete;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvSingleNoteTitle);
            tvDate = itemView.findViewById(R.id.tvSingleNoteDate);
            ivDelete = itemView.findViewById(R.id.ivSingleNoteDelete);
        }
    }
}
