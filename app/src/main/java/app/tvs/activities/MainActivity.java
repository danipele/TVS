package app.tvs.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import app.tvseries.R;

public abstract class MainActivity extends AppCompatActivity {

    protected ImageView deleteButton;
    public ImageView addButton;
    protected TextView headerTextView;
    protected ListView listView;
    protected ImageView leaveActivityButton;
    public BaseAdapter adapter;
    protected ConstraintLayout progressLayout;
    protected ConstraintLayout formLayout;
    protected ImageView closeButton;
    protected Button addInForm;

    protected boolean deleteMode = false;

    abstract protected void initHeader();
    abstract protected void initLeaveButton();
    abstract protected void deleteButtonAction();
    abstract protected void setAdapter();
    abstract protected void addButtonAction();
    abstract protected void initArrays();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        initActionBar();
        initViews();
        initHeader();
        initLeaveButton();
        initArrays();
        deleteMode = false;

        setAdapters();

        addButton.setOnClickListener(v -> addButtonAction());

        leaveActivityButton.setOnClickListener(v -> leaveActivity());

        deleteButton.setOnClickListener(v -> {
            if(adapter.getCount() != 0) {
                if (deleteMode) {
                    deleteButtonAction();
                    endDeleteButtonAction();
                } else {
                    startDeleteButtonAction();
                }
            }
        });

        closeButton.setOnClickListener(v -> closeForm());
    }

    public void closeForm() {
        formLayout.setVisibility(View.INVISIBLE);
        setButtonsClickable(true);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if(deleteMode) {
            endDeleteButtonAction();
        }
        else {
            leaveActivity();
        }
    }

    @Override
    public void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    protected void initActionBar() {
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.action_bar_tvseries);
        }
    }

    protected void initViews() {
        addButton = findViewById(R.id.addButton);
        headerTextView = findViewById(R.id.headerTextView);
        listView = findViewById(R.id.listView);
        leaveActivityButton = findViewById(R.id.backButton);
        deleteButton = findViewById(R.id.deleteButton);
        progressLayout = findViewById(R.id.addingLayout);
        formLayout = findViewById(R.id.addFormLayout);
        closeButton = findViewById(R.id.closeFormButton);
        addInForm = findViewById(R.id.addInFormButton);
    }

    protected void setAdapters() {
        setAdapter();
        listView.setAdapter(adapter);
        setListFooterView();
    }

    @SuppressLint("InflateParams")
    protected void setListFooterView() {
        LayoutInflater layoutInflater = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        View footerView = null;
        if(layoutInflater != null) {
            footerView = layoutInflater.inflate(R.layout.footer_list,null, true);
        }
        listView.addFooterView(footerView);
    }

    protected void startDeleteButtonAction() {
        deleteMode = true;
        deleteButton.setClickable(true);
        adapter.notifyDataSetChanged();
    }

    protected void endDeleteButtonAction() {
        deleteMode = false;
        adapter.notifyDataSetChanged();
        if(adapter.getCount() == 0) {
            findViewById(R.id.showArrowImageView).setVisibility(View.VISIBLE);
        }
    }

    public void setButtonsClickable(boolean bool) {
        deleteButton.setClickable(bool);
        leaveActivityButton.setClickable(bool);
        addButton.setClickable(bool);
    }

    protected void leaveActivity() {
        finish();
    }

    public Context getContext() {return this;}

    public boolean isProgressLayoutInvisible() {
        return progressLayout.getVisibility() == View.INVISIBLE;
    }

    public boolean isDeleteMode() {
        return deleteMode;
    }

}
