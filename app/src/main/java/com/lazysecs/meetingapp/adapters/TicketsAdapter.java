package com.lazysecs.meetingapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.activities.TicketActivity;
import com.lazysecs.meetingapp.fragments.TicketChatFragment;
import com.lazysecs.meetingapp.models.Ticket;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TicketsAdapter extends RecyclerView.Adapter<TicketsAdapter.ViewHolder> {

    private List<Ticket> tickets;
    private Context context;

    public TicketsAdapter(Context context, List<Ticket> tickets) {
        this.tickets = tickets;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ticket_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Ticket ticket = tickets.get(position);

        holder.textName.setText(ticket.getName());
        holder.textPlace.setText(ticket.getGeoPoint().getAddress());
        holder.textPrice.setText(ticket.getPrice() + " ₽");
        holder.textDate.setText(parseDate(ticket.getDate()));

        if (ticket.getTime() != null)
            holder.textTime.setText(parseTime(ticket.getTime()));

        holder.layoutTicketInfo.setOnClickListener(v -> {
            Intent intent = new Intent(context, TicketActivity.class);
            intent.putExtra("EXTRA_TICKET_ID", String.valueOf(ticket.getId()));

            context.startActivity(intent);
        });

        holder.layoutSendMessage.setOnClickListener(v -> {
            sendMessage(String.valueOf(ticket.getCreator().getId()), ticket.getId());
            removeItemAfterRequest(position);

            Intent intent = new Intent(context, TicketChatFragment.class);
            intent.putExtra("EXTRA_USER_PROFILE_ID", String.valueOf(ticket.getCreator().getId()));
            context.startActivity(intent);
        });
    }

    private String parseTime(String time) {
        @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("hh:mm:ss");
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        @SuppressLint("SimpleDateFormat") String newDate = new SimpleDateFormat("hh:mm").format(Objects.requireNonNull(date));
        return newDate;
    }

    private String parseDate(String dateString) {
        @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        @SuppressLint("SimpleDateFormat") String newDate = new SimpleDateFormat("dd MMMM", new Locale("RU")).format(Objects.requireNonNull(date));
        return newDate;
    }


    private void sendMessage(String toUser, long ticket) {

    }

    private void removeItemAfterRequest(int position) {
        tickets.remove(position);

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }


    @Override
    public int getItemCount() {
        return tickets.size();
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

        @BindView(R.id.name)
        TextView textName;

        @BindView(R.id.place)
        TextView textPlace;

        @BindView(R.id.price)
        TextView textPrice;

        @BindView(R.id.date)
        TextView textDate;

        @BindView(R.id.time)
        TextView textTime;

        @BindView(R.id.layout_ticket_info)
        LinearLayout layoutTicketInfo;

        @BindView(R.id.layout_send_message)
        LinearLayout layoutSendMessage;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
