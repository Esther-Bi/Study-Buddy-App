package com.example.studybuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TeacherAdapter extends ArrayAdapter<Teacher>
{

    public TeacherAdapter(Context context, int resource, List<Teacher> shapeList)
    {
        super(context,resource,shapeList);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Teacher shape = getItem(position);

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell_teacher, parent, false);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.teacherName);

        tv.setText(shape.getName());

        return convertView;
    }
}