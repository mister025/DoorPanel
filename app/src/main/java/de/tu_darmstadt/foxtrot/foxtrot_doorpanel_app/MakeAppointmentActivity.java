package de.tu_darmstadt.foxtrot.foxtrot_doorpanel_app;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.tu_darmstadt.foxtrot.foxtrot_doorpanel_app.adapter.EventListAdapter;
import de.tu_darmstadt.foxtrot.foxtrot_doorpanel_app.model.ScheduledEvents;
import de.tu_darmstadt.foxtrot.foxtrot_doorpanel_app.network.RetrofitClient;
import de.tu_darmstadt.foxtrot.foxtrot_doorpanel_app.network.interfacesApi.WorkerAPI;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MakeAppointmentActivity extends AppCompatActivity {

    ProgressDialog mProgress;

    private final String TAG = "make appointment";

    private WeekView mWeekView;

    private WorkerAPI workersApi;


    private WeekViewEvent activeEvent = null;
    private int slotColor = 0x77770000;
    private int activeSlotColor = 0xFF550000;
    private List<WeekViewEvent> events;
    private  boolean nameEdited = false;
    private  boolean mailEdited = false;
    private  boolean messageEdited = false;

    private EditText editName;
    private EditText editNumber;
    private EditText editMail;
    private EditText editMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_appointment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        workersApi = RetrofitClient.getRetrofitInstance().create(WorkerAPI.class);

        int workerID = getIntent().getIntExtra("workerID",0);

        Worker worker = ((TabletApplication)getApplicationContext()).getWorkerByID(workerID);

        editName = ((EditText) findViewById(R.id.editName));
        editNumber = ((EditText) findViewById(R.id.editPhone));
        editMail = ((EditText) findViewById(R.id.editEMail));
        editMessage = ((EditText) findViewById(R.id.editMessage));

        events = new ArrayList<WeekViewEvent>();

        for (de.tu_darmstadt.foxtrot.foxtrot_doorpanel_app.network.models.Event event:worker.getTimeslots()) {
            WeekViewEvent wvEvent = new WeekViewEvent();
            Calendar calStart = Calendar.getInstance();
            calStart.setTime(event.getStart());
            wvEvent.setStartTime(calStart);
            Calendar calEnd = Calendar.getInstance();
            calEnd.setTime(event.getEnd());
            wvEvent.setEndTime(calEnd);
            wvEvent.setName(event.getName());
            wvEvent.setColor(getResources().getColor(R.color.colorSlot));
            wvEvent.setId(event.getId());
            events.add(wvEvent);
        }

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        mWeekView.notifyDatasetChanged();


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<String> call = workersApi.bookWorkerTimeslot(workerID,
                        (int) activeEvent.getId(),editName.getText().toString(),
                        editNumber.getText().toString(), editMail.getText().toString(),
                        editMessage.getText().toString());

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String newStatus = response.body();

                        Context context = getApplicationContext();
                        Toast toast = Toast.makeText(context, "Status updated: " + newStatus,
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d(TAG, t.getMessage());
                    }
                });

                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab.setEnabled(false);



        // Set an action when any event is clicked.
        mWeekView.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {


                if (event.getColor()==getResources().getColor(R.color.colorSlot)) {

                    if (!(activeEvent == null)) {
                        activeEvent.setColor(getResources().getColor(R.color.colorSlot));
                    }
                    activeEvent = event;
                    activeEvent.setColor(getResources().getColor(R.color.colorSlotActive));
                    mWeekView.notifyDatasetChanged();


                    if (nameEdited && mailEdited && messageEdited) {

                        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSlotActive)));
                        fab.setEnabled(true);
                    }

                }



            }
        });

        editName.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                nameEdited = true;
                Log.v("DoorPanel", "name edited" );
                if (activeEvent != null && mailEdited && messageEdited){
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSlotActive)));
                    fab.setEnabled(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        editMail.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                mailEdited = true;
                Log.v("DoorPanel", "mail edited" );
                if (activeEvent != null && nameEdited && messageEdited){
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSlotActive)));
                    fab.setEnabled(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        editMessage.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                messageEdited = true;
                Log.v("DoorPanel", "message edited" );
                if (activeEvent != null && mailEdited && nameEdited){
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSlotActive)));
                    fab.setEnabled(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            @Override
            public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {

                List<WeekViewEvent> theseEvents = new ArrayList<WeekViewEvent>();

                for (WeekViewEvent event: events) {
                    int year = event.getStartTime().get(Calendar.YEAR);
                    int month = event.getStartTime().get(Calendar.MONTH)+1;
                    if (year==newYear && month==newMonth){
                        theseEvents.add(event);
                    }
                }

                return theseEvents;
            }
        });

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(new WeekView.EventLongPressListener() {
            @Override
            public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

            }
        });

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Syncing with calendar..");

    }







}