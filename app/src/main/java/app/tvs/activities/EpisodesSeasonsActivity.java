package app.tvs.activities;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import app.tvseries.R;

public abstract class EpisodesSeasonsActivity extends MainActivity {

    protected Spinner spinner;
    protected TextView headerFormTextView;
    protected ConstraintLayout infoFormLayout;

    protected abstract void getInfoFromForm();
    protected abstract ArrayAdapter<Integer> updateSpinnerArray(ArrayAdapter<Integer> spinnerArray);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addInForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfoFromForm();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(formLayout.getVisibility() == View.VISIBLE) {
            closeForm();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void initLeaveButton() {
        leaveActivityButton.setImageDrawable(getDrawable(R.drawable.back));
        leaveActivityButton.setBackground(getDrawable(R.drawable.ripple_style));
    }

    @Override
    protected void initViews() {
        super.initViews();
        headerFormTextView = findViewById(R.id.headerFormTextView);
        initInfoForm();
    }

    @Override
    protected void addButtonAction() {
        formLayout.setVisibility(View.VISIBLE);
        infoFormLayout.setVisibility(View.VISIBLE);
        setButtonsClickable(false);
        updateSpinner();
    }

    @Override
    protected void startDeleteButtonAction() {
        setButtonsClickable(false);
        super.startDeleteButtonAction();
    }

    @Override
    protected void leaveActivity() {
        super.leaveActivity();
        overridePendingTransition(R.anim.left_in_animation, R.anim.right_out_animation);
    }

    protected void initInfoForm() {
        infoFormLayout = findViewById(R.id.infoLayoutSeasonsEpisodes);
        spinner = findViewById(R.id.nrSpinner);
    }

    @Override
    public void closeForm() {
        super.closeForm();
        infoFormLayout.setVisibility(View.INVISIBLE);
    }


    protected void resetInfo() {
        spinner.setSelection(0);
    }

    public void updateSpinner() {
        ArrayAdapter<Integer> spinnerArray = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);
        spinnerArray.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerArray = updateSpinnerArray(spinnerArray);
        if(spinnerArray.getCount() == 0)
            addInForm.setVisibility(View.INVISIBLE);
        else
            addInForm.setVisibility(View.VISIBLE);
        spinner.setAdapter(spinnerArray);
    }

    public boolean isFormLayoutInvisible() {
        return formLayout.getVisibility() == View.INVISIBLE;
    }
}
