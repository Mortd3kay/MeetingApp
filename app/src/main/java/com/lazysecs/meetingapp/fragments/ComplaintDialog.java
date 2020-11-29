package com.lazysecs.meetingapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.adapters.ComplaintsReasonsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ComplaintDialog extends DialogFragment {

    @BindView(R.id.recycle_view)
    RecyclerView recyclerView;
    private Callback callback;
    private List<String> complaintsReasons = new ArrayList<>();
    private String contentType;
    private int contentId;


    public ComplaintDialog(String contentType, int contentId) {
        this.contentType = contentType;
        this.contentId = contentId;
    }

    public static ComplaintDialog newInstance(String contentType, int contentId) {
        return new ComplaintDialog(contentType, contentId);
    }

    @OnClick(R.id.fullscreen_dialog_close)
    void closeDialog() {
        dismiss();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.EventsFilterDialogTheme);
    }

    private void initReasons() {
        complaintsReasons.add("Спам");
        complaintsReasons.add("Спекуляция");
        complaintsReasons.add("Оскорбления");
        complaintsReasons.add("Материалы для взрослых");
        complaintsReasons.add("Детская порнография");
        complaintsReasons.add("Пропаганда наркотиков");
        complaintsReasons.add("Продажа оружия");
        complaintsReasons.add("Насилие");
        complaintsReasons.add("Призыв к травле");
        complaintsReasons.add("Призыв к суициду");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complaint_dialog, container, false);
        ButterKnife.bind(this, view);

        initReasons();

        ComplaintsReasonsAdapter complaintsReasonsAdapter = new ComplaintsReasonsAdapter(requireActivity(), complaintsReasons, contentId, contentType, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(complaintsReasonsAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
//        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow())
//                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    public interface Callback {
        void onActionClick();
    }
}