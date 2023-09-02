package com.plstudio.a123.vfv.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.plstudio.a123.vfv.R;
import com.plstudio.a123.vfv.database.RequirementsDbSchema;
import com.plstudio.a123.vfv.model.Requirement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.plstudio.a123.vfv.database.RequirementsDbSchema.RequirementsTable;

public class RequirementsBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "requirements.db";

    private Context context;

    public RequirementsBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL("create table " + RequirementsTable.NAME + "(" +
            " _id integer primary key autoincrement, " +
            RequirementsTable.Cols.SEX + ", " +
            RequirementsTable.Cols.VFVGROUP + ", " +
            RequirementsTable.Cols.GNAME + ", " +
            RequirementsTable.Cols.A_12 + ", " +
            RequirementsTable.Cols.A_13 + ", " +
            RequirementsTable.Cols.A_14 + ", " +
            RequirementsTable.Cols.A_15 + ", " +
            RequirementsTable.Cols.A_16 + ", " +
            RequirementsTable.Cols.A_17 + ", " +
            RequirementsTable.Cols.IMG + ", " +
            RequirementsTable.Cols.STATUS + ")"
    );

    addRequirements(readFromFile(context), sqLiteDatabase);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public void addRequirements(List<Requirement> requirements, SQLiteDatabase mDatabase){
        ContentValues values;
        for(Requirement r: requirements){
            values = getContentValues(r);
            mDatabase.insert(RequirementsDbSchema.RequirementsTable.NAME, null, values);
        }
    }

    private List<Requirement> readFromFile(Context context )  {
        List<Requirement> list = new ArrayList<>();
        InputStream inputStream = context.getResources().openRawResource(R.raw.requirements);
        BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream));
        String eachline = null;
        try {
            eachline = bufferedReader.readLine();

            while (eachline != null) {
                // `the words in the file are separated by space`, so to get each words
                String[] words = eachline.split(" ");
                eachline = bufferedReader.readLine();
                if(eachline != null)
                    list.add(new Requirement(eachline));
            }
        } catch (IOException e) {
            Log.e(null, "Exception!!");
            e.printStackTrace();
        }
        return list;
    }

    private static ContentValues getContentValues(Requirement requirement){
        ContentValues values = new ContentValues();
        values.put(RequirementsDbSchema.RequirementsTable.Cols.SEX, requirement.getSex());
        values.put(RequirementsDbSchema.RequirementsTable.Cols.VFVGROUP, requirement.getGroup());
        values.put(RequirementsDbSchema.RequirementsTable.Cols.GNAME, requirement.getName());
        values.put(RequirementsDbSchema.RequirementsTable.Cols.A_12, requirement.getA_12());
        values.put(RequirementsDbSchema.RequirementsTable.Cols.A_13, requirement.getA_13());
        values.put(RequirementsDbSchema.RequirementsTable.Cols.A_14, requirement.getA_14());
        values.put(RequirementsDbSchema.RequirementsTable.Cols.A_15, requirement.getA_15());
        values.put(RequirementsDbSchema.RequirementsTable.Cols.A_16, requirement.getA_16());
        values.put(RequirementsDbSchema.RequirementsTable.Cols.A_17, requirement.getA_17());
        values.put(RequirementsDbSchema.RequirementsTable.Cols.IMG, requirement.getImg());
        values.put(RequirementsDbSchema.RequirementsTable.Cols.STATUS, "0");

        return values;
    }
}
