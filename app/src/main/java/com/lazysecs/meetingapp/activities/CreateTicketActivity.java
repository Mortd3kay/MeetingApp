package com.lazysecs.meetingapp.activities;

import android.content.Intent;
import android.os.Bundle;

import com.lazysecs.meetingapp.interfaces.TicketManager;
import com.lazysecs.meetingapp.adapters.TicketStepperAdapter;
import com.lazysecs.meetingapp.models.Ticket;

public class CreateTicketActivity extends BaseCreateContentActivity implements TicketManager {

    private static final String NAME = "name";
    private static final String PRICE = "price";

    private String name;
    private int price;

    @Override
    public void setupAdapter() {
        TicketStepperAdapter ticketStepperAdapter = new TicketStepperAdapter(getSupportFragmentManager(), this);
        stepperLayout.setAdapter(ticketStepperAdapter);
    }

    @Override
    public void loadContent() {
        Intent intent = getIntent();
        this.saveAction(intent.getStringExtra("action"));

        if (intent.hasExtra("EXTRA_TICKET")) {
            Ticket ticket = intent.getParcelableExtra("EXTRA_TICKET");
            if (ticket != null) {
                this.saveId(ticket.getId());
                this.saveDescription(ticket.getDescription());
                this.saveDate(ticket.getDate());
                this.saveTime(ticket.getTime());
                this.saveLocation(ticket.getGeoPoint());
                this.saveName(ticket.getName());
                this.savePrice(ticket.getPrice());
            }
        }
    }

    @Override
    public void setupAdditionalOnSaveInstanceState(Bundle outState) {
        outState.putString(NAME, name);
        outState.putDouble(PRICE, price);
    }

    @Override
    public void saveName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void savePrice(int price) {
        this.price = price;
    }

    @Override
    public int getPrice() {
        return price;
    }
}
