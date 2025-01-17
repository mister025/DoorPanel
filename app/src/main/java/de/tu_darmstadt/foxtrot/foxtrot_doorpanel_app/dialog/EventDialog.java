package de.tu_darmstadt.foxtrot.foxtrot_doorpanel_app.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventAttendee;
import de.tu_darmstadt.foxtrot.foxtrot_doorpanel_app.CalendarActivity;
import de.tu_darmstadt.foxtrot.foxtrot_doorpanel_app.R;

import java.util.Calendar;
import java.util.Date;


/**
 * The type Event dialog.
 */
public class EventDialog extends DialogFragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private TimePicker startTime;
    private DatePicker startDate;
    private TimePicker endTime;
    private DatePicker endDate;
    /**
     * The C.
     */
    final Calendar c = Calendar.getInstance();
    /**
     * The Hour.
     */
    int hour = c.get(Calendar.HOUR_OF_DAY);
    /**
     * The Minute.
     */
    int minute = c.get(Calendar.MINUTE);
    private Button createEvent;
    private Button cancelEvent;
    private EditText eventTitle;
    private EditText eventDes;
    private EditText eventLocation;
    private EditText eventAttendee;
    private EventAttendee eventAttendeeEmail[];
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_event_layout, container, false);

        eventTitle = (EditText)view.findViewById(R.id.eventTitle);
        eventDes = (EditText)view.findViewById(R.id.eventDes);
        eventLocation = (EditText)view.findViewById(R.id.eventLocation);
        eventAttendee = (EditText)view.findViewById(R.id.eventAttendee);

        startDate = (DatePicker)view.findViewById(R.id.startDate);

        startTime = (TimePicker) view.findViewById(R.id.startTime);
        endTime = (TimePicker)view.findViewById(R.id.endTime);
        endDate = (DatePicker) view.findViewById(R.id.endDate);

        createEvent = (Button)view.findViewById(R.id.createEvent);
        cancelEvent = (Button)view.findViewById(R.id.cancelEvent);

        createEvent.setOnClickListener(this);
        cancelEvent.setOnClickListener(this);

        return view;
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cancelEvent:
                dismiss();
                break;
            case R.id.createEvent:
                Calendar startCalendar = Calendar.getInstance();
                startCalendar.set(Calendar.DAY_OF_MONTH, startDate.getDayOfMonth());
                startCalendar.set(Calendar.MONTH, startDate.getMonth());
                startCalendar.set(Calendar.YEAR, startDate.getYear());
                startCalendar.set(Calendar.HOUR_OF_DAY, startTime.getCurrentHour());
                startCalendar.set(Calendar.MINUTE, startTime.getCurrentMinute());
                Date startDate = startCalendar.getTime();

                DateTime start = new DateTime(startDate);


                Calendar endCalendar = Calendar.getInstance();
                endCalendar.set(Calendar.DAY_OF_MONTH, endDate.getDayOfMonth());
                endCalendar.set(Calendar.MONTH, endDate.getMonth());
                endCalendar.set(Calendar.YEAR, endDate.getYear());
                endCalendar.set(Calendar.HOUR_OF_DAY, endTime.getCurrentHour());
                endCalendar.set(Calendar.MINUTE, endTime.getCurrentMinute());
                Date endDate = endCalendar.getTime();
                DateTime end = new DateTime(endDate);

                if(!eventAttendee.getText().toString().equalsIgnoreCase("")) {
                    eventAttendeeEmail = new EventAttendee[3];
                    String email[] = eventAttendee.getText().toString().trim().split(",");
                    int i = 0;
                    for (String s : email) {
                        EventAttendee eventAttendee = new EventAttendee();
                        eventAttendee.setEmail(s);
                        eventAttendeeEmail[i] = eventAttendee;
                    }
                }

                StringBuffer buffer = new StringBuffer(eventTitle.getText().toString()+"\n");
                buffer.append("\n");
                buffer.append(eventDes.getText().toString());
                ((CalendarActivity)getActivity()).createEventAsync(eventTitle.getText().toString(), eventLocation.getText().toString(), buffer.toString(), start, end, eventAttendeeEmail );
                dismiss();
                break;
        }
    }

    private String startDateString, endDateString;
    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        switch (datePicker.getId()){
            case R.id.startDate:
                startDateString  = i+" , "+i1+" , "+i2;
                System.out.println(startDateString);
                break;
            case R.id.endDate:
                endDateString  = i+" , "+i1+" , "+i2;
                System.out.println(endDateString);
                break;
        }
    }

    private String startTimeString, endTimeString;
    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        switch (timePicker.getId()){
            case R.id.startTime:
                startTimeString = i+", "+i1;
                System.out.println(startTimeString);
                break;
            case R.id.endTime:
                endTimeString = i+", "+i1;
                System.out.println(endTimeString);
                break;
        }
    }
}
