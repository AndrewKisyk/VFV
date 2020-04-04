package com.plstudio.a123.vfv.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.plstudio.a123.vfv.model.Requirement;

public class RequirementsCursorWrapper extends CursorWrapper {
    public RequirementsCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    public Requirement getRequirement(String age){
        String name = getString(getColumnIndex(RequirementsDbSchema.RequirementsTable.Cols.GNAME));
        String current_a = fromAge(age);
        String sex = getString(getColumnIndex(RequirementsDbSchema.RequirementsTable.Cols.SEX));
        String group = getString(getColumnIndex(RequirementsDbSchema.RequirementsTable.Cols.VFVGROUP));
        String a_12 = getString(getColumnIndex(RequirementsDbSchema.RequirementsTable.Cols.A_12));
        String a_13 = getString(getColumnIndex(RequirementsDbSchema.RequirementsTable.Cols.A_13));
        String a_14 = getString(getColumnIndex(RequirementsDbSchema.RequirementsTable.Cols.A_14));
        String a_15 = getString(getColumnIndex(RequirementsDbSchema.RequirementsTable.Cols.A_15));
        String a_16 = getString(getColumnIndex(RequirementsDbSchema.RequirementsTable.Cols.A_16));
        String a_17 = getString(getColumnIndex(RequirementsDbSchema.RequirementsTable.Cols.A_17));

        String status = getString(getColumnIndex(RequirementsDbSchema.RequirementsTable.Cols.STATUS));
        String img = getString(getColumnIndex(RequirementsDbSchema.RequirementsTable.Cols.IMG));

        Requirement requirement = new Requirement( sex, group, name, current_a, a_12,
                 a_13, a_14, a_15, a_16, a_17, img, status);

        if(!current_a.equals("0"))
            return requirement;
        else
            return null;
    }

    private String fromAge(String age){
        String age_req = new String() ;
        switch(age){
            case "12":
                age_req = getString(getColumnIndex(RequirementsDbSchema.RequirementsTable.Cols.A_12));
                break;
            case "13":
                age_req = getString(getColumnIndex(RequirementsDbSchema.RequirementsTable.Cols.A_13));
                break;
            case "14":
                age_req = getString(getColumnIndex(RequirementsDbSchema.RequirementsTable.Cols.A_14));
                break;
            case "15":
                age_req = getString(getColumnIndex(RequirementsDbSchema.RequirementsTable.Cols.A_15));
                break;
            case "16":
                age_req = getString(getColumnIndex(RequirementsDbSchema.RequirementsTable.Cols.A_16));
                break;
            case "17":
                age_req = getString(getColumnIndex(RequirementsDbSchema.RequirementsTable.Cols.A_17));
                break;
        }
        return age_req;
    }
}
