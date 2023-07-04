package com.ritchennai.pinnacle;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HRStatusAdapter extends RecyclerView.Adapter<HRStatusAdapter.VIEWHOLDER> {

    List<HRStatusModel> hrStatusModelList;

    public HRStatusAdapter(List<HRStatusModel> hrStatusModelList) {
        this.hrStatusModelList = hrStatusModelList;
    }

    @NonNull
    @Override
    public VIEWHOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hr_status_container,parent,false);
        return new VIEWHOLDER(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VIEWHOLDER holder, int position) {
        String name = hrStatusModelList.get(position).getName();
        String count = hrStatusModelList.get(position).getCount();
        String status = hrStatusModelList.get(position).getStatus();
        String from = hrStatusModelList.get(position).getFromDate();
        String to = hrStatusModelList.get(position).getToDate();
        String type = hrStatusModelList.get(position).getType();
        holder.setData(name,count,status,from,to,type);
    }

    @Override
    public int getItemCount() {
        return hrStatusModelList.size();
    }

    public static class VIEWHOLDER extends RecyclerView.ViewHolder {

        TextView nameTV,countTV,statusTV,fromTV,toTV,typeTV,Fromtextview,Totextview;

        public VIEWHOLDER(@NonNull View itemView) {
            super(itemView);
            nameTV=itemView.findViewById(R.id.hr_status_name);
            countTV=itemView.findViewById(R.id.hr_status_countTV);
            statusTV=itemView.findViewById(R.id.hr_status_sttusTV);
            fromTV=itemView.findViewById(R.id.hr_status_from);
            toTV=itemView.findViewById(R.id.hr_status_toTV);
            typeTV=itemView.findViewById(R.id.hr_status_type);
            Fromtextview=itemView.findViewById(R.id.Fromtextview);
            Totextview=itemView.findViewById(R.id.Totextview);
        }
        private void setData(String name,String count,String status,String from,String to,String type){

            nameTV.setText(name);
            countTV.setText(count);

            if(status.equals("APPROVED")){
                statusTV.setTextColor(Color.parseColor("#FF8BC34A"));
            }
            if(status.equals("DENIED")){
                statusTV.setTextColor(Color.parseColor("#FFFF0000"));
            }
            if(status.equals("PENDING")){
                statusTV.setTextColor(Color.parseColor("#FFC107"));
            }

            statusTV.setText(status);

            if(type.equals("Permission")){
                Fromtextview.setText("Date");
                Totextview.setText("Time");
            }else{
                Fromtextview.setText("From");
                Totextview.setText("To");
            }

            fromTV.setText(from);
            toTV.setText(to);
            typeTV.setText(type);
        }
    }
}
