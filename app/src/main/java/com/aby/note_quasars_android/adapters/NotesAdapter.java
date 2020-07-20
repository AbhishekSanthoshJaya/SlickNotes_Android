package com.aby.note_quasars_android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aby.note_quasars_android.database.Note;
import com.aby.note_quasars_android.R;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> implements Filterable {

    Context context;
    List<Note> noteList = new ArrayList<>();
    private List<Note> noteListFull = new ArrayList<>();

    private OnItemClickListner listner;


    public NotesAdapter(Context context, List<Note> noteList) {
        this.context = context;
        this.noteList = noteList;
        noteListFull = new ArrayList<>(noteList);

    }

    public Note getNoteAt(int position){
        return noteList.get(position);
    }

    @Override
    public NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_note,parent,false);
        NotesViewHolder nvh = new NotesViewHolder(v);
        return nvh;
    }

    @Override
    public void onBindViewHolder(NotesViewHolder holder, int position) {
        holder.tvTitle.setText(noteList.get(position).getTitle());
        holder.tvNote.setText(noteList.get(position).getNote());
        ViewCompat.setTransitionName(holder.tvTitle, Integer.toString(noteList.get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    @Override
    public Filter getFilter() {
        return noteListFilter;
    }

    private Filter noteListFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Note> filterList = new ArrayList<>();
            if(charSequence == null || charSequence.length() == 0){
                filterList.addAll(noteListFull);
            }
            else{
                String filterPattern = charSequence.toString().trim().toLowerCase();
                for(Note note: noteListFull){
                    if(note.getTitle().toLowerCase().contains(filterPattern) ||
                            note.getNote().toLowerCase().contains(filterPattern) ){
                        filterList.add(note);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filterList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {


            noteList.clear();
            noteList.addAll((List)filterResults.values);
            notifyDataSetChanged();
        }
    };


    public class NotesViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle,tvNote;
        public NotesViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvNoteTitle);
            tvNote = itemView.findViewById(R.id.tvNoteText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position  =  getAdapterPosition();

                    if(listner != null && position != RecyclerView.NO_POSITION){
                        listner.onItemClick(noteList.get(position),tvTitle);
                    }

                }
            });

        }
    }

    public  interface  OnItemClickListner {
        void onItemClick(Note contact, View view);
    }

    public void setOnItemClickListner(OnItemClickListner onItemClickListner){
        this.listner = onItemClickListner;
    }

}
