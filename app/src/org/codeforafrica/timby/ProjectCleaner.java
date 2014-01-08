package org.codeforafrica.timby;


import java.util.ArrayList;

import org.codeforafrica.timby.model.Media;
import org.codeforafrica.timby.model.Project;

//used to delete a project's media on storage
public class ProjectCleaner {
	
	public static void clean (Project project)
	{
		// FIXME default to use first scene
		ArrayList<Media> alMedia = project.getScenesAsArray()[0].getMediaAsList();
		
	}

}
