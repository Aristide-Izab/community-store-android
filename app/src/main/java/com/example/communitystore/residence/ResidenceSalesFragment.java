package com.example.communitystore.residence;

import com.example.communitystore.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// sales tab
public class ResidenceSalesFragment extends Fragment {

    // dummy sales for now (title, date, amount)
    private static final String[][] SAMPLE_RECENT_INCOME = {
            {"Sales- Orders", "29 Aug 2026", "R1,600"},
            {"Sales- Orders", "29 Aug 2026", "R1,600"},
            {"Refund", "29 Aug 2026", "R350"}
    };

    private static final float UNSELECTED_ALPHA =
            0.55f;

    private TextView[] periodButtons;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        return inflater.inflate(
                R.layout.fragment_residence_sales,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        ResidenceHomeActivity home =
                (ResidenceHomeActivity) requireActivity();

        view.findViewById(R.id.backButton)
                .setOnClickListener(v ->
                        home.selectTab(ResidenceHomeActivity.TAB_HOME)
                );

        periodButtons =
                new TextView[]{
                        view.findViewById(R.id.periodWeek),
                        view.findViewById(R.id.periodMonth),
                        view.findViewById(R.id.periodYear)
                };

        for (int i = 0; i < periodButtons.length; i++) {

            int period = i;

            periodButtons[i].setOnClickListener(
                    v -> selectPeriod(period)
            );
        }

        selectPeriod(1);

        showRecentIncome(
                view.findViewById(R.id.recentIncomeList)
        );
    }

    private void selectPeriod(
            int period
    ) {

        for (int i = 0; i < periodButtons.length; i++) {

            boolean selected =
                    i == period;

            periodButtons[i].setSelected(selected);

            periodButtons[i].setAlpha(
                    selected
                            ? 1f
                            : UNSELECTED_ALPHA
            );
        }
    }

    private void showRecentIncome(
            LinearLayout list
    ) {

        LayoutInflater inflater =
                LayoutInflater.from(requireContext());

        for (String[] income : SAMPLE_RECENT_INCOME) {

            View row =
                    inflater.inflate(
                            R.layout.item_residence_income_row,
                            list,
                            false
                    );

            ((TextView) row.findViewById(R.id.incomeTitle))
                    .setText(income[0]);

            ((TextView) row.findViewById(R.id.incomeDate))
                    .setText(income[1]);

            ((TextView) row.findViewById(R.id.incomeAmount))
                    .setText(income[2]);

            list.addView(row);
        }
    }
}
