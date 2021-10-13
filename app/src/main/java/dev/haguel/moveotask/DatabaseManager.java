package dev.haguel.moveotask;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dev.haguel.moveotask.entities.NoteEntity;
import dev.haguel.moveotask.entities.UserEntity;

public class DatabaseManager {

    // Finals
    public static final int GEO_LOCATION_REQUEST_CODE = 55;
    public static final String USERS_FIREBASE_KEY = "Users";
    public static final String USER_NAME_FIREBASE_KEY = "fullName";
    public static final String NOTES_FIREBASE_KEY = "Notes";

    private static DatabaseManager instance;
    public static DatabaseManager instance() {
        if (instance == null)
            instance = new DatabaseManager();
        return instance;
    }
    private DatabaseManager(){}

    // Interface
    public interface OnDataLoaded {
        public void onDataLoaded();
    }
    public interface OnNameLoaded{
        public void onNameLoaded();
    }

    // Data
    private ArrayList<NoteEntity> noteArrayList = new ArrayList<>();
    private String fullName = "";
    boolean hasLoadedOnce = false;


    public void registerUser(String userID, UserEntity userEntity){
        getDBRef().child(USERS_FIREBASE_KEY).child(userID).setValue(userEntity);
    }

    public void createOrEditNote(NoteEntity note) {
        getDBRef().child(USERS_FIREBASE_KEY).child(getUID()).child(NOTES_FIREBASE_KEY).child(String.valueOf(note.getCreated())).setValue(note);
    }

    public void deleteNote(long id) {
        // Find Index To delete
        int index = 0;
        for (int i = 0; i < noteArrayList.size(); i++) {
            if (id == noteArrayList.get(i).getCreated()){
                index = i;
            }
        }
        // Delete From DB and local
        getDBRef().child(USERS_FIREBASE_KEY).child(getUID()).child(NOTES_FIREBASE_KEY).child(String.valueOf(id)).removeValue();
        noteArrayList.remove(index);
    }

    public void loadUserNameFromDBAsync(OnNameLoaded listener){
        getDBRef().child(USERS_FIREBASE_KEY).child(getUID()).child(USER_NAME_FIREBASE_KEY)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setFullName(snapshot.getValue(String.class));
                if (listener != null) {
                    listener.onNameLoaded();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void loadNotesFromDBAsync(OnDataLoaded listener){
        getDBRef().child(USERS_FIREBASE_KEY).child(getUID()).child(NOTES_FIREBASE_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    // Try Parsing From Snapshot
                    noteArrayList.clear();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        if (snap != null) {
                            noteArrayList.add(snap.getValue(NoteEntity.class));
                        }
                    }
                    sortNotesByDateCreated();
                } catch (Exception e) { }
                notifyNotesListener(listener);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
               notifyNotesListener(listener);
            }
        });
    }

    private void notifyNotesListener(OnDataLoaded listener) {
        if (listener != null && !hasLoadedOnce) {
            listener.onDataLoaded();
        }
        hasLoadedOnce = true;
    }

    private String getUID(){
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            return FirebaseAuth.getInstance().getCurrentUser().getUid();

        return "";
    }

    private DatabaseReference getDBRef(){
        return FirebaseDatabase.getInstance().getReference();
    }

    private void sortNotesByDateCreated(){
        Collections.sort(noteArrayList, new Comparator<NoteEntity>() {
            @Override
            public int compare(NoteEntity o1, NoteEntity o2) {
                return Long.compare(o1.getCreated(), o2.getCreated());
            }
        });
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public ArrayList<NoteEntity> getNoteArrayList() {
        return noteArrayList;
    }

}
