package com.lazysecs.meetingapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.interfaces.TransferCategories;
import com.lazysecs.meetingapp.models.Category;
import com.lazysecs.meetingapp.models.MegaCategory;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryChipsAdapter extends RecyclerView.Adapter<CategoryChipsAdapter.ViewHolder> {
    private static final int UNSELECTED = -1;
    List<MegaCategory> megaCategories;
    ArrayList<String> categories;
    private int selectedItem = UNSELECTED;
    private RecyclerView recyclerView;
    private Context context;
    private TransferCategories transferCategories;

    public CategoryChipsAdapter(Context context, RecyclerView recyclerView, List<MegaCategory> megaCategories, TransferCategories transferCategories, ArrayList<String> categories) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.megaCategories = megaCategories;
        this.transferCategories = transferCategories;
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_category_item, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MegaCategory megaCategory = megaCategories.get(position);

        holder.expandButton.setText(megaCategory.getName());


        if (megaCategories.get(position).isExpanded())
            holder.arrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
        else
            holder.arrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);

//        holder.arrow.animate().rotationBy(180f).setDuration(300).start();


        holder.chipGroup.removeAllViews();

        for (Category category : megaCategory.getCategories()) {
            Chip chip = (Chip) LayoutInflater.from(context).inflate(R.layout.category_item, holder.chipGroup, false);
            chip.setText(category.getName());

            if (categories != null) {
                for (String categoryString : categories)
                    if (categoryString.contentEquals(chip.getText()))
                        chip.setChecked(true);
            }

            holder.chipGroup.addView(chip);

            chip.setOnTouchListener((v, event) -> {
                if (categories.size() == 15) {
                    Toast.makeText(context, "Можно выбрать не более 15 интересов :)", Toast.LENGTH_SHORT).show();
                    disableChips();
                }

                return false;
            });

            chip.setOnCheckedChangeListener((compoundButton, checked) -> {
                if (checked) {
                    if (categories.size() == 15)
                        disableChips();
                    else
                        categories.add(String.valueOf(chip.getText()));
                } else {
                    categories.remove(String.valueOf(chip.getText()));
                    allowChips();
                }

                transferCategories.getResult(categories);
            });
        }


        boolean isExpanded = megaCategories.get(position).isExpanded();
        holder.chipGroup.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        holder.expandButton.setOnClickListener(v -> {
            megaCategories.get(position).setExpanded(!megaCategories.get(position).isExpanded());
            holder.clickHandle();
        });
    }

    @Override
    public int getItemCount() {
        return megaCategories.size();
    }

    private void disableChips() {
        ViewHolder currentHolder;
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            currentHolder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);

            if (currentHolder != null) {
                for (int j = 0; j < currentHolder.chipGroup.getChildCount(); j++) {
                    if (!((Chip) currentHolder.chipGroup.getChildAt(j)).isChecked()) {
                        ((Chip) currentHolder.chipGroup.getChildAt(j)).setCheckable(false);
                    }
                }
            }
        }
    }

    private void allowChips() {
        ViewHolder currentHolder;
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            currentHolder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);

            if (currentHolder != null) {
                for (int j = 0; j < currentHolder.chipGroup.getChildCount(); j++) {
                    ((Chip) currentHolder.chipGroup.getChildAt(j)).setCheckable(true);
                }
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.expand_button)
        TextView expandButton;

        @BindView(R.id.arrow)
        ImageView arrow;

        @BindView(R.id.chip_group)
        ChipGroup chipGroup;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void clickHandle() {

//            MegaCategory megaCategory = megaCategories.get(getAdapterPosition());
//            megaCategory.setExpanded(!megaCategory.isExpanded());
            notifyItemChanged(getAdapterPosition());
        }
    }
}