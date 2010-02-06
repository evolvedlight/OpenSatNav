package org.opensatnav;

import org.opensatnav.services.TripStatistics;
import org.opensatnav.services.TripStatisticsListener;
import org.opensatnav.services.TripStatistics.TripStatisticsStrings;

import android.content.Context;
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
	
	public TripStatisticsController(final Context context) {

		// pre-subscribe the controller as a listener
		TripStatisticsService.setController(this);
		
		// service
		TripStatisticsService.start(context);

		// view
		mStatsView = View.inflate(context,R.layout.tripstatistics, null );
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
		
		Button closeStatsButton = (Button) mStatsView.findViewById(R.id.closeStatistics);
		Button resetStatsButton = (Button) mStatsView.findViewById(R.id.resetStatistics);
		
		closeStatsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				((SatNavActivity)context).setViewingTripStats(false);
				((SatNavActivity)context).showTripStatistics(false);
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

	public Object getAllStatistics() {
		final TripStatistics.TripStatisticsStrings stats = new TripStatistics.TripStatisticsStrings();
		stats.averSpeed = averTripSpeedView.getText() + "";
		stats.instSpeed = currentSpeedView.getText() + "";
		stats.tripDistance = tripDistanceMetersView.getText() + "";
		stats.tripDuration = tripDurationMsecView.getText() + "";
		return stats;
	}

	public void setAllStats(TripStatisticsStrings stats) {
		averTripSpeedView.setText(stats.averSpeed);
		currentSpeedView.setText(stats.instSpeed);
		tripDistanceMetersView.setText(stats.tripDistance);
		tripDurationMsecView.setText(stats.tripDuration);
	}
}
