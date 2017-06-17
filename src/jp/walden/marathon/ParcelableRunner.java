package jp.walden.marathon;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class ParcelableRunner implements Parcelable {

	private static final String TAG = "ParceableRunner";
	private long runnerId;
	private String runnerName;

	public ParcelableRunner(long runnerId, String runnerName) {
		super();
		this.runnerId = runnerId;
		this.runnerName = runnerName;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
	      Log.v(TAG, "writeToParcel..."+ flags);
	      dest.writeLong(runnerId);
	      dest.writeString(runnerName);
	}

	public static final Parcelable.Creator<ParcelableRunner> CREATOR
    = new Parcelable.Creator<ParcelableRunner>() {
		public ParcelableRunner createFromParcel(Parcel in) {
		    return new ParcelableRunner(in);
		}
		
		public ParcelableRunner[] newArray(int size) {
		    return new ParcelableRunner[size];
		}
};

	private ParcelableRunner(Parcel in) {
		readFromParcel(in);
    }

	private void readFromParcel(Parcel in) {
	      Log.v(TAG, "readFromParcel...");
	      runnerId = in.readLong();
	      runnerName = in.readString();
	}

	public long getRunnerId() {
		return runnerId;
	}

	public void setRunnerId(long runnerId) {
		this.runnerId = runnerId;
	}

	public String getRunnerName() {
		return runnerName;
	}

	public void setRunnerName(String runnerName) {
		this.runnerName = runnerName;
	}

}
