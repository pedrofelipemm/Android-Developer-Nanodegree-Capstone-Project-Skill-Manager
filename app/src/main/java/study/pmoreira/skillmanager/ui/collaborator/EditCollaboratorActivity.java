package study.pmoreira.skillmanager.ui.collaborator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.model.Collaborator;

import static study.pmoreira.skillmanager.ui.collaborator.CollaboratorActivity.EXTRA_COLLABORATOR;

public class EditCollaboratorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_collaborator);
    }

    public static void startActivity(Context context) {
        startActivity(context, null);
    }

    public static void startActivity(Context context, Collaborator collaborator) {
        Intent intent = new Intent(context, EditCollaboratorActivity.class);

        if (collaborator != null) {
            intent.putExtra(EXTRA_COLLABORATOR, collaborator);
        }

        context.startActivity(intent);
    }
}
