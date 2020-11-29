package com.lazysecs.meetingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.activities.ComplaintActivity;
import com.lazysecs.meetingapp.models.Complaint;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsAdapter.ViewHolder> {

    private List<Complaint> complaints;
    private Context context;

    public ComplaintsAdapter(Context context, List<Complaint> complaints) {
        this.complaints = complaints;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.complaint_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Complaint complaint = complaints.get(position);

        holder.textContentId.setText(String.valueOf(complaint.getContentId()));
        holder.textContentType.setText(complaint.getContentType());
        holder.textCreated.setText(complaint.getCreated());
        holder.textMessage.setText(complaint.getMessage());
        holder.textSuspected.setText(String.valueOf(complaint.getSuspected().getId()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ComplaintActivity.class);
            intent.putExtra("EXTRA_COMPLAINT_ID", String.valueOf(complaint.getId()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return complaints.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_content_id)
        TextView textContentId;

        @BindView(R.id.text_content_type)
        TextView textContentType;

        @BindView(R.id.text_created)
        TextView textCreated;

        @BindView(R.id.text_message)
        TextView textMessage;

        @BindView(R.id.text_suspected)
        TextView textSuspected;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
