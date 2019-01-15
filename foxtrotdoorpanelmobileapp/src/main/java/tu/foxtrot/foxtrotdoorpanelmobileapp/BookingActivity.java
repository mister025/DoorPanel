package tu.foxtrot.foxtrotdoorpanelmobileapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;
import java.util.Arrays;

import tu.foxtrot.foxtrotdoorpanelmobileapp.R;

public class BookingActivity extends AppCompatActivity {

    private TextView name;
    private TextView email;
    private TextView text;
    private Button buttonAcceptBooking;
    private Event timeslot;
    private String calendarId;

    private com.google.api.services.calendar.Calendar mService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        calendarId = ((MobileApplication)getApplicationContext()).mCalendar;

        name = (TextView) findViewById(R.id.message_name);
        email = (TextView) findViewById(R.id.message_email);
        text = (TextView) findViewById(R.id.message_text);
        buttonAcceptBooking = (Button) findViewById(R.id.buttonAcceptBooking);

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, ((MobileApplication)getApplicationContext()).mCredential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();

        Intent intent = getIntent();
        int notificationID = intent.getIntExtra("notificationID",0);
        BookingNotification notification = (BookingNotification) ((MobileApplication)getApplicationContext()).notificationsList.get(notificationID);
        String slotID = notification.getTimeslot();
        try {
            timeslot = mService.events().get(calendarId,slotID).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        name.setText(intent.getStringExtra("Name").toString());
        email.setText(intent.getStringExtra("Email").toString());
        text.setText(intent.getStringExtra("Details").toString());

        buttonAcceptBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Event event = new Event()
                        .setSummary("meeting with "+notification.getName())
                        .setLocation(timeslot.getLocation())
                        .setDescription("meeting with "+notification.getName()+"\n"
                        +"message: "+notification.getMessage());

                event.setStart(timeslot.getStart());

                event.setEnd(timeslot.getEnd());

                String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT=1"};
                event.setRecurrence(Arrays.asList(recurrence));


                //event.send
                if(mService!=null) {
                    try {
                        mService.events().insert(calendarId, event).setSendNotifications(true).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Intent gmail = new Intent(Intent.ACTION_VIEW);
                gmail.setClassName("com.google.android.gm","com.google.android.gm.ComposeActivityGmail");
                gmail.putExtra(Intent.EXTRA_EMAIL, new String[] { notification.getEmail() });
                gmail.setData(Uri.parse(notification.getEmail()));
                gmail.putExtra(Intent.EXTRA_SUBJECT, "enter something");
                gmail.setType("plain/text");
                gmail.putExtra(Intent.EXTRA_TEXT, "hello "+notification.getName()+"\n"+
                        "I have accepted your request to meet from "+timeslot.getStart().toString()
                        +" until "+timeslot.getEnd().toString()+" in/at "+ timeslot.getLocation()+"\n"+
                        "Please be on time");
                startActivity(gmail);
            }
        });
    }
}
