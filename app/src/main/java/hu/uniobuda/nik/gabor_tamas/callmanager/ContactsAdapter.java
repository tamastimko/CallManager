package hu.uniobuda.nik.gabor_tamas.callmanager;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Tram on 2015.03.24..
 * A Contacts objektumok listview-ban való hatákony tárolásához szükséges osztály
 */
public class ContactsAdapter extends BaseAdapter {

    private List<Contacts> items;

    public ContactsAdapter(List<Contacts> items){
        this.items=items;
    }

    //BaseAdapter-hez szükséges metódusok
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
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;    //a hatékonyság növelése holderekkel
        if(convertView==null){
            convertView=View.inflate(viewGroup.getContext(),R.layout.contact_layout,null);
            holder=new ViewHolder();
            holder.name=(TextView)convertView.findViewById(R.id.name);
            holder.number=(TextView)convertView.findViewById(R.id.number);
            holder.check=(CheckBox)convertView.findViewById(R.id.contchecker);
            convertView.setTag(holder);
        } else {
            holder=(ViewHolder)convertView.getTag();
        }
        holder.name.setText(items.get(position).getName());
        holder.number.setText(items.get(position).getNumber());
        holder.check.setChecked(items.get(position).isChecked());
        holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    items.get(position).setChecked(true);
                else
                    items.get(position).setChecked(false);
            }
        });
        return convertView;
    }

    //OnCreate esetén nem működik ezért nem használom
    public void setViewsEnabled(ListView listView,boolean enabled){
        ViewHolder holder;
        for (int i=0;i<listView.getChildCount();i++) {
            holder=(ViewHolder)listView.getChildAt(i).getTag();
            holder.name.setEnabled(enabled);
            holder.number.setEnabled(enabled);
            holder.check.setEnabled(enabled);
        }
    }
}


class ViewHolder {
    TextView name;
    TextView number;
    CheckBox check;
}
