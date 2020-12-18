package com.example.passwordauthentication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
//Adapter is a bridge between UI component and data source that helps us to fill data in UI component. It holds and send the data toan Adapter view
//then view can takes the data from the adapter view and shows the data on different views like as RecyclerView.
public class ListofAppAdapter extends RecyclerView.Adapter<ListofAppAdapter.MyViewHolder> {
    private List<AppList> appList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView app_name;
        public ImageView app_icon, status;

        public MyViewHolder(View view){
            super(view);
            this.app_icon = (ImageView) view.findViewById(R.id.app_icon);
            this.app_name = (TextView) view.findViewById(R.id.list_app_name);
            this.status = (ImageView) view.findViewById(R.id.lock_icon);
        }
    }
    public ListofAppAdapter(List<AppList> list) {
        this.appList = list;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.installed_app_list, viewGroup, false));
    }

    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        AppList model = (AppList) this.appList.get(i);
        myViewHolder.app_icon.setImageDrawable(model.getIcon());
        myViewHolder.app_name.setText(model.getName());
        myViewHolder.status.setImageResource(model.getLocked());
    }

    public int getItemCount() {
        return this.appList.size();
    }
}
