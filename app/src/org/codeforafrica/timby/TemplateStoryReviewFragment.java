package org.codeforafrica.timby;

import org.codeforafrica.timby.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TemplateStoryReviewFragment extends Fragment {
	
    public TemplateStoryReviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story_review, null);
        
        return view;
    }
}
