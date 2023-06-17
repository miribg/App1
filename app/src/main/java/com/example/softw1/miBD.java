package com.example.softw1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class miBD extends SQLiteOpenHelper {

    public miBD(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //crear tablas de db
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Usuario (" +
                        " 'user_name' TEXT PRIMARY KEY NOT NULL, " +
                        " 'name' TEXT," +
                        " 'surname' TEXT," +
                        " 'password' TEXT NOT NULL," +
                        " 'email' TEXT," +
                        " 'phone' INTEGER," +
                        " 'color' INTEGER" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Usuario (" +
                        " 'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        " 'titulo' TEXT," +
                        " 'fechaI' DATE NOT NULL," +
                        " 'fechaF' DATE," +
                        " 'horarioI' TIME NOT NULL," +
                        " 'horarioF' TIME NOT NULL," +
                        " 'lugar' TEXT," +
                        " 'notas' TEXT," +
                        " 'color' INTEGER," +
                        " 'nombreP' TEXT NOT NULL,"+
                        " FOREIGN KEY('nombreP') REFERENCES Usuario('user_name')"+
                        ")"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         db.execSQL("DROP TABLE IF EXISTS Usuario");
         onCreate(db);
    }




}
