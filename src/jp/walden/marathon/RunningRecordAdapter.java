package jp.walden.marathon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RunningRecordAdapter extends ArrayAdapter<RunningRecord> {
    
	Context context;
	int layoutResourceId;
	ArrayList<RunningRecord> data = null;
	
    public RunningRecordAdapter(Context context, int layoutResourceId, ArrayList<RunningRecord> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }
    
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RunningRecordHolder holder = null;
		
		if(row == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RunningRecordHolder();
            holder.date = (TextView)row.findViewById(R.id.date);
            holder.ranking = (TextView)row.findViewById(R.id.ranking);
            holder.total = (TextView)row.findViewById(R.id.total);
            holder.time = (TextView)row.findViewById(R.id.time);
            row.setTag(holder);
		} else {
			holder = (RunningRecordHolder)row.getTag();
		}
        RunningRecord record = data.get(position);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(record.getDate());
//        String year = String.format("%04d", calendar.get(Calendar.YEAR));
//        String month = String.format("%02d", calendar.get(Calendar.MONTH));
//        String day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
        String date = String.format
        		("%04d/%02d/%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1 , calendar.get(Calendar.DAY_OF_MONTH));
        holder.date.setText(date);
        Integer total = record.getTotal();
        String totalString = null;
        if(0 < total) {
        	totalString = String.format("%4s", String.valueOf(total));
        } else {
        	totalString = String.format("%4s", "-");
        }
        String ranking = String.valueOf(record.getRanking());
        holder.ranking.setText(ranking);
        holder.total.setText(totalString);
//        String ranking = String.valueOf(record.getRanking()) + / 
        holder.time.setText(String.format("%7s", record.getTime()));
        return row;
	}
	
    static class RunningRecordHolder
    {
    	TextView date;
        TextView ranking;
        TextView total;
        TextView time;
    }
}
