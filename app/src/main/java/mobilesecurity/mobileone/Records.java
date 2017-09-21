package mobilesecurity.mobileone;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class Records extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getRecords());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    ArrayList<Record> getRecords() {
        RecordDbHelper mDbHelper = new RecordDbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                RecordsContract.RecordEntry.COLUMN_NAME_NAME,
                RecordsContract.RecordEntry.COLUMN_NAME_DATE,
                RecordsContract.RecordEntry.COLUMN_NAME_TIME,
                RecordsContract.RecordEntry.COLUMN_NAME_GPS_LONG,
                RecordsContract.RecordEntry.COLUMN_NAME_GPS_ALT,
                RecordsContract.RecordEntry.COLUMN_NAME_GPS_LAT,
        };

        String sortOrder = RecordsContract.RecordEntry.COLUMN_NAME_TIME + " ASC";

        Cursor cur = db.query(RecordsContract.RecordEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);

        ArrayList<Record> items = new ArrayList<>(cur.getCount());

        for(int i = 0; i < 10 && cur.moveToNext(); i++) {
            Record rec = new Record(
                    cur.getString(cur.getColumnIndexOrThrow(RecordsContract.RecordEntry.COLUMN_NAME_NAME)),
                    cur.getString(cur.getColumnIndexOrThrow(RecordsContract.RecordEntry.COLUMN_NAME_DATE)),
                    cur.getInt(cur.getColumnIndexOrThrow(RecordsContract.RecordEntry.COLUMN_NAME_TIME)),
                    cur.getDouble(cur.getColumnIndexOrThrow(RecordsContract.RecordEntry.COLUMN_NAME_GPS_LONG)),
                    cur.getDouble(cur.getColumnIndexOrThrow(RecordsContract.RecordEntry.COLUMN_NAME_GPS_ALT)),
                    cur.getDouble(cur.getColumnIndexOrThrow(RecordsContract.RecordEntry.COLUMN_NAME_GPS_LAT))
            );
            items.add(rec);
        }
        cur.close();
        db.close();
        mDbHelper.close();

        return items;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_RECORDS = "records";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, ArrayList<Record> records) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putSerializable(ARG_RECORDS, records);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View fragmentView;

            int selectedTab = getArguments().getInt(ARG_SECTION_NUMBER);

            @SuppressWarnings("unchecked")
            final ArrayList<Record> records = (ArrayList<Record>) getArguments().getSerializable(ARG_RECORDS);

            assert records != null;
            switch (selectedTab) {
                case 1:
                    fragmentView = inflater.inflate(R.layout.fragment_records_table, container, false);
                    TableLayout tableLayout = (TableLayout) fragmentView.findViewById(R.id.table);

                    for (Record rec : records) {
                        TextView nameTV = new TextView(container.getContext());
                        nameTV.setText(rec.getName());
                        TextView dateTV = new TextView(container.getContext());
                        dateTV.setText(rec.getDate());
                        TextView timeTV = new TextView(container.getContext());
                        timeTV.setText(String.valueOf(rec.getTime()));
                        TextView gpsTV = new TextView(container.getContext());
                        gpsTV.setText(String.valueOf(rec.getLat()));

                        TableRow row = new TableRow(container.getContext());

                        row.addView(nameTV);
                        row.addView(dateTV);
                        row.addView(timeTV);
                        row.addView(gpsTV);

                        tableLayout.addView(row);
                    }

                    return fragmentView;
                case 2:
                    fragmentView = inflater.inflate(R.layout.fragment_records_map, container, false);

                    SupportMapFragment mapFragment = SupportMapFragment.newInstance();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.add(R.id.mapLayout, mapFragment);
                    fragmentTransaction.commit();

                    mapFragment.getMapAsync(googleMap -> {
                        Geocoder geocoder = new Geocoder(getContext());
                        for (Record rec : records) {
                            String address;
                            try {
                                List<Address> addresses = geocoder.getFromLocation(rec.getLat(), rec.getLon(), 1);
                                address = String.format("%s, %s", addresses.get(0).getAddressLine(0), addresses.get(0).getCountryName());
                            } catch (Exception e) {
                                e.printStackTrace();
                                address = "Not available";
                            }

                            MarkerOptions mo = new MarkerOptions();
                            LatLng latLng = new LatLng(rec.getLat(), rec.getLon());
                            mo
                                    .position(latLng)
                                    .title("Name: " + rec.getName())
                                    .snippet(
                                            "Date: " + rec.getDate() +
                                            "\nTime: " + rec.getTime() +
                                            "\nAddress: "+ address);
                            googleMap.addMarker(mo);
                        }

                        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                            @Override
                            public View getInfoWindow(Marker marker) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(Marker marker) {
                                LinearLayout l = new LinearLayout(getContext());
                                l.setOrientation(LinearLayout.VERTICAL);

                                TextView title = new TextView(getContext());
                                title.setText(marker.getTitle());

                                TextView snippet = new TextView(getContext());
                                snippet.setText(marker.getSnippet());

                                l.addView(title);
                                l.addView(snippet);

                                return l;
                            }
                        });
                    });

                    return fragmentView;
                default:
                    return null;
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Record> records;

        SectionsPagerAdapter(FragmentManager fm, ArrayList<Record> records) {
            super(fm);
            this.records = records;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, records);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Table";
                case 1:
                    return "Map";
            }
            return null;
        }
    }
}
