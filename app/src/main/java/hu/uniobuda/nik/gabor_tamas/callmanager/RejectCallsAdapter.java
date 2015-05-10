package hu.uniobuda.nik.gabor_tamas.callmanager;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Tram on 2015.05.09..
 */
public class RejectCallsAdapter extends BaseAdapter {

    List<RejectCalls> items;

    public RejectCallsAdapter(List<RejectCalls> items){
        this.items=items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final RejectCallHolder holder;    //a hatékonyság növelése holderekkel
        if(convertView==null){
            convertView=View.inflate(viewGroup.getContext(),R.layout.rejectcall_layout,null);
            holder=new RejectCallHolder();
            holder.name=(TextView)convertView.findViewById(R.id.name);
            holder.number=(TextView)convertView.findViewById(R.id.number);
            holder.time=(TextView)convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        } else {
            holder=(RejectCallHolder)convertView.getTag();
        }
        holder.name.setText(items.get(position).getName());
        holder.number.setText(items.get(position).getNumber());
        holder.time.setText(items.get(position).getTime());
        return convertView;
    }
}

class RejectCallHolder{
    TextView name;
    TextView number;
    TextView time;
}
