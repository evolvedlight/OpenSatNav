package org.opensatnav;

import org.opensatnav.services.TripStatistics;
import org.opensatnav.services.TripStatisticsListener;
import org.opensatnav.services.TripStatistics.TripStatisticsStrings;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TripStatisticsController implements TripStatisticsListener {

	private static TripStatisticsListener instance;

	private View mStatsView;
	
	private TextView tripDurationMsecView;
	private TextView averTripSpeedView;
	private TextView currentSpeedView;
	private TextView tripDistanceMetersView;
	
	private TextView tripDurationUnitsView;
	private TextView averSpeedUnitsView;
	private TextView currentSpeedUnitsView;
	private TextView tripDistanceUnitsView;

	private boolean unitsToBeShown;
	
	public TripStatisticsController(final SatNavActivity satNavActivity) {

		// service
		TripStatisticsService.start(satNavActivity);

		// view
		mStatsView = View.inflate(satNavActivity,R.layout.tripstatistics, null );
		mStatsView.setVisibility(View.GONE);
		
		averTripSpeedView = (TextView)mStatsView.findViewById(R.id.aver_trip_speed);
		currentSpeedView = (TextView)mStatsView.findViewById(R.id.current_speed);
		tripDurationMsecView = (TextView)mStatsView.findViewById(R.id.trip_duration);
		tripDistanceMetersView = (TextView)mStatsView.findViewById(R.id.trip_distance);
		
		/*
		averSpeedUnitsView = (TextView)mStatsView.findViewById(R.id.aver_speed_units);
		currentSpeedUnitsView = (TextView)mStatsView.findViewById(R.id.current_speed_units);
		tripDistanceUnitsView = (TextView)mStatsView.findViewById(R.id.distance_units);
		tripDurationUnitsView = (TextView)mStatsView.findViewById(R.id.trip_duration_units);
		*/
		
		unitsToBeShown = true;
		
		Button closeStatsButton = (Button) mStatsView.findViewById(R.id.closeStatistics);
		Button resetStatsButton = (Button) mStatsView.findViewById(R.id.resetStatistics);
		
		closeStatsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				satNavActivity.showTripStatistics(false);
			}
		});
		
		resetStatsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				TripStatisticsService.getService().resetStatistics();
			}
		});
		
		instance = this;
	}
	
	@Override
	public void tripStatisticsChanged(TripStatistics statistics) {
		averTripSpeedView.setText(statistics.getAverageTripSpeedString(TripStatistics.METRIC));
		currentSpeedView.setText(statistics.getInstantSpeedString(TripStatistics.METRIC));
		tripDistanceMetersView.setText(statistics.getTripDistanceString(TripStatistics.METRIC));
		tripDurationMsecView.setText(statistics.getTripTimeString(TripStatistics.METRIC));
		
		/*
		if( unitsToBeShown ) {
			averSpeedUnitsView.setText(statistics.getSpeedUnits(TripStatistics.METRIC));
			currentSpeedUnitsView.setText(statistics.getSpeedUnits(TripStatistics.METRIC));
			tripDistanceUnitsView.setText(statistics.getDistanceUnits(TripStatistics.METRIC));
			tripDurationUnitsView.setText(statistics.getElapsedTimeUnits(TripStatistics.METRIC));
			unitsToBeShown = false;
		}
		*/
	}

	public View getView() {
		return mStatsView;
	}

	public void addViewTo(RelativeLayout parentView) {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.FILL_PARENT);;
		parentView.addView(mStatsView,params);
	}

	public void setVisible(boolean flag) {
		if( flag ) {
			mStatsView.setVisibility(View.VISIBLE);
		} else {
			mStatsView.setVisibility(View.GONE);
		}
	}

	public static TripStatisticsListener getInstance() {
		return instance;
	}

	public Object getAllStatistics() {
		final TripStatistics.TripStatisticsStrings stats = new TripStatistics.TripStatisticsStrings();
		stats.averSpeed = averTripSpeedView.getText() + "";
		stats.instSpeed = currentSpeedView.getText() + "";
		stats.tripDistance = tripDistanceMetersView.getText() + "";
		stats.tripDuration = tripDurationMsecView.getText() + "";
//		stats.speedUnits = averSpeedUnitsView.getText() + "";
//		stats.distanceUnits = tripDistanceUnitsView.getText() + "";
//		stats.elapsedTimeUnits = tripDurationUnitsView.getText() + "";
		return stats;
	}

	public void setAllStats(TripStatisticsStrings stats) {
		averTripSpeedView.setText(stats.averSpeed);
		currentSpeedView.setText(stats.instSpeed);
		tripDistanceMetersView.setText(stats.tripDistance);
		tripDurationMsecView.setText(stats.tripDuration);
//		averSpeedUnitsView.setText(stats.speedUnits);
//		tripDistanceUnitsView.setText(stats.distanceUnits);
//		tripDurationUnitsView.setText(stats.elapsedTimeUnits);
	}
}
