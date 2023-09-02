package com.plstudio.a123.vfv.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.plstudio.a123.vfv.datadriven.PreferenceUtils;
import com.plstudio.a123.vfv.database.RequirementsCursorWrapper;
import com.plstudio.a123.vfv.database.RequirementsDbSchema;
import com.plstudio.a123.vfv.model.Requirement;
import com.plstudio.a123.vfv.model.User;

import java.util.ArrayList;
import java.util.List;


public class RequirementsLab {
    private static RequirementsLab sRequirementsLab;
    private List<Requirement> requirements;
    private SQLiteDatabase mDatabase;
    private Context mContext;
    private static User user;

    public static RequirementsLab get(Context context) {
        if(sRequirementsLab == null) {
            sRequirementsLab = new RequirementsLab(context);
        }
        user = User.getUser(new PreferenceUtils(context));
        return sRequirementsLab;
    }

    private RequirementsLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new RequirementsBaseHelper(mContext).getWritableDatabase();

    }



    private RequirementsCursorWrapper queryRequirements(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                RequirementsDbSchema.RequirementsTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new RequirementsCursorWrapper(cursor);
    }

    public List<Requirement> getRequirements(String group, String status){
        List<Requirement> l_requirements = new ArrayList<>();

        String age = user.getAge();
        String sex = user.getSex();
        RequirementsCursorWrapper cursor = queryRequirements("SEX = ? AND VFVGROUP = ? AND STATUS = ?", new String[]{sex, group, status});

        try {
            cursor.moveToFirst();
            Requirement temp;
            while (!cursor.isAfterLast()) {
                temp = cursor.getRequirement(age);
                if(temp != null)
                    l_requirements.add(cursor.getRequirement(age));
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return l_requirements;
    }
    public List<Requirement> getRequirements(String status){
        List<Requirement> l_requirements = new ArrayList<>();
        String age = user.getAge();
        String sex = user.getSex();
        RequirementsCursorWrapper cursor = queryRequirements("SEX = ? AND STATUS = ?", new String[]{sex, status});

        try {
            cursor.moveToFirst();
            Requirement temp;
            while (!cursor.isAfterLast()) {
                temp = cursor.getRequirement(age);
                if(temp != null)
                    l_requirements.add(cursor.getRequirement(age));
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return l_requirements;
    }
    public int getGroupResult(String group) { return getRequirements(group, "1").size(); }
    public int getAllDoneResult(){ return getRequirements("1").size(); }

    public  void doRequirement(Requirement requirement, String status_to_set){
        ContentValues values = getContentValues(requirement, status_to_set);

        mDatabase.update(RequirementsDbSchema.RequirementsTable.NAME, values,
                RequirementsDbSchema.RequirementsTable.Cols.SEX + " = ? AND " +
                        RequirementsDbSchema.RequirementsTable.Cols.GNAME + " = ? "
                , new String[]{requirement.getSex(), requirement.getName().replace(" ", "_")});
        Log.d(null, "DONE!!!");
    }

    public void undoAllRequirements(){
        ContentValues values = getContentValues();
        mDatabase.update(RequirementsDbSchema.RequirementsTable.NAME, values,
                null,null);
    }

    private static ContentValues getContentValues(Requirement requirement, String status_to_set){
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
        values.put(RequirementsDbSchema.RequirementsTable.Cols.STATUS, status_to_set.toString());
        return values;

    }

    private static ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(RequirementsDbSchema.RequirementsTable.Cols.STATUS, "0");
        return values;
    }



}
