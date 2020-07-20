package com.aby.note_quasars_android.database;

import android.content.Context;
import android.util.Log;

import com.aby.note_quasars_android.interfaces.AddNoteViewInterface;
import com.aby.note_quasars_android.interfaces.DeleteNoteInterface;
import com.aby.note_quasars_android.interfaces.EditNoteViewInterface;
import com.aby.note_quasars_android.interfaces.FolderListerInterface;
import com.aby.note_quasars_android.interfaces.MainViewInterface;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LocalCacheManager {
    private Context context;
    private static LocalCacheManager _instance;
    private AppDatabase db;

    public static LocalCacheManager getInstance(Context context) {
        if (_instance == null) {
            _instance = new LocalCacheManager(context);
        }
        return _instance;
    }

    public LocalCacheManager(Context context) {
        this.context = context;
        db = AppDatabase.getAppDatabase(context);
    }

    public void getNotes(final MainViewInterface mainViewInterface) {
        db.noteDao()
                .getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Note>>() {
                        @Override
                        public void accept(List<Note> notes) throws Exception {
                            mainViewInterface.onNotesLoaded(notes);
                        }
                    });
    }

    public void getNotesInFolder(final MainViewInterface mainViewInterface, int folderId) {
        db.noteDao()
                .getNotesWithFolder(folderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Note>>() {
                    @Override
                    public void accept(List<Note> notes) throws Exception {
                        mainViewInterface.onNotesLoaded(notes);
                    }
                });
    }



    public void addNotes(final AddNoteViewInterface addNoteViewInterface, Note note) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                db.noteDao().insertAll(note);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                addNoteViewInterface.onNoteAdded();
            }

            @Override
            public void onError(Throwable e) {
                Log.d("ERROR", "onError: " + e);
                addNoteViewInterface.onDataNotAvailable();
            }
        });
    }


    public void updateNote(final EditNoteViewInterface editNoteViewInterface, Note note) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {

                db.noteDao().update(note);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                editNoteViewInterface.onNoteUpdated();
            }

            @Override
            public void onError(Throwable e) {
                Log.d("ERROR", "onError: " + e);
        }
        });
    }


    public void deleteNote(final DeleteNoteInterface deleteNoteInterface, Note note) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {

                db.noteDao().delete(note);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                deleteNoteInterface.onNoteDeleted();
            }

            @Override
            public void onError(Throwable e) {
                Log.d("ERROR", "onError: " + e);
            }
        });
    }

    // For folders

    public void getFolders(final FolderListerInterface folderListerInterface) {
        db.folderDao()
                .getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Folder>>() {
                    @Override
                    public void accept(List<Folder> folders) throws Exception {
                        folderListerInterface.onFoldersLoaded(folders);
                    }
                });
    }


    public void addFolders(final FolderListerInterface folderListerInterface, final String folderName) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                Folder folder = new Folder(folderName);
                db.folderDao().insertAll(folder);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                folderListerInterface.onFolderAdded();
            }

            @Override
            public void onError(Throwable e) {
                folderListerInterface.onDataNotAvailable();
            }
        });
    }


}
