package com.example.student_scheduler.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.student_scheduler.R;
import com.example.student_scheduler.database.Repository;
import com.example.student_scheduler.entities.Course;
import com.example.student_scheduler.entities.Term;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Objects;

/**
 * This activity allows the user to view details for a selected term. The data is populated into
 * EditText fields for the user to modify or delete. If there are associated courses with this term,
 * the user will not be allowed to delete the term until all courses have been deleted.
 *
 * The user can also use the floating action button in the bottom right-hand corner to view
 * additional options related to the term.
 */
public class TermDetails extends AppCompatActivity {

    EditText term_title;
    EditText term_start;
    EditText term_end;
    String termTitle;
    String termStart;
    String termEnd;
    int termID;
    Term term;
    CourseAdapter courseAdapter;
    Repository repository;

    // Confirmation Message
    String confirmMessage = "Term was successfully updated.";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_details);

        // Labels and edit text fields for selected term
        term_title = findViewById(R.id.term_title_edit);
        term_start = findViewById(R.id.term_start_edit);
        term_end = findViewById(R.id.term_end_edit);

        termTitle = getIntent().getStringExtra("term_title");
        termStart = getIntent().getStringExtra("term_start");
        termEnd= getIntent().getStringExtra("term_end");

        term_title.setText(termTitle);
        term_start.setText(termStart);
        term_end.setText(termEnd);

        term_title.requestFocus();

        termID = getIntent().getIntExtra("term_id", -1);

        // Display associated courses with the term
        RecyclerView courseListRecycler = findViewById(R.id.course_list_recycler);
        repository = new Repository(getApplication());
        courseAdapter = new CourseAdapter(this);
        courseListRecycler.setAdapter(courseAdapter);
        courseListRecycler.setLayoutManager(new LinearLayoutManager(this));
        courseAdapter.setCourses(repository.getAssociatedCourses(termID));

        // Update selected term and confirm update
        Button updateTerm = findViewById(R.id.update_term);
        updateTerm.setOnClickListener(view -> {
                term = new Term(termID, term_title.getText().toString(),
                        term_start.getText().toString(), term_end.getText().toString());
                repository.update(term);

                Toast.makeText(getApplication(), confirmMessage, Toast.LENGTH_SHORT).show();
                finish();
        });

        // Display toolbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Extended FAB with sub menu
        ExtendedFloatingActionButton termFab = findViewById(R.id.terms_extended_fab);
        termFab.setOnClickListener(this::showSubMenu);
    }

    /**
     * This method handles the click event for the back button in the action bar. When the back
     * button is clicked, `onBackPressed()` is called to go back to the previous activity.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the floating action button is clicked. This popup menu provides
     * the user two options: 1) adding a new term or 2) deleting the term.
     */
    public void showSubMenu(View view) {
        PopupMenu termPopupMenu = new PopupMenu(this, view);
        termPopupMenu.getMenuInflater().inflate(R.menu.term_menu, termPopupMenu.getMenu());
        termPopupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.add_course:
                    Intent toAddCourse = new Intent(TermDetails.this, AddCourse.class);
                    startActivity(toAddCourse);
                    break;
                case R.id.add_term:
                    Intent toAddTerm = new Intent(TermDetails.this, AddTerm.class);
                    startActivity(toAddTerm);
                    break;
                case R.id.delete_term:
                    if (courseAdapter.getItemCount() > 0 ){
                        Toast.makeText(TermDetails.this, "Please delete associated " +
                                "courses before deleting this term.",Toast.LENGTH_SHORT).show();
                    } else {
                        deleteTerm();
                    }
                    break;
            }
            return true;
        });
        termPopupMenu.show();
    }

    /**
     * This method will delete the term.
     */
    private void deleteTerm() {
        Term term = new Term(termID, term_title.getText().toString(),
                term_start.getText().toString(), term_end.getText().toString());
        repository.delete(term);
        Toast.makeText(this, "Term deleted", Toast.LENGTH_SHORT).show();
        finish();
    }

}