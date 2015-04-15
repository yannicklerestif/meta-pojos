package com.yannicklerestif.metapojos.plugin.resources;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.yannicklerestif.metapojos.model.DataContainer;
import com.yannicklerestif.metapojos.plugin.MetaPojosPluginImpl;
import com.yannicklerestif.metapojos.plugin.PluginAccessor;

public class DataContainerManager {
	private DataContainer dc;

	private MetaPojosWorkspace workspace;

	private boolean dirty = true;

	public DataContainerManager(MetaPojosWorkspace workspace) {
		this.workspace = workspace;
	}

	public DataContainer getDataContainer() {
		return dc;
	}

	class ClassesReadingJob extends Job {

		private boolean canceled = false;

		public ClassesReadingJob() {
			super("Reading classes...");
		}

		@Override
		protected void canceling() {
			this.canceled = true;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
				dc = new DataContainer();

				String[] classesLocations = workspace.getClassesLocations();
				monitor.beginTask("Reading classes", classesLocations.length);

				for (String location : classesLocations) {
					monitor.subTask("Reading location : " + location);
					if (canceled == true)
						return Status.CANCEL_STATUS;
					dc.readClasses(location);
					monitor.worked(1);
				}
				monitor.done();
				return Status.OK_STATUS;
			} catch (Exception e) {
				return new Status(IStatus.ERROR, MetaPojosPluginImpl.PLUGIN_ID, "Error reading classes", e);
			}
		}

	}

	public synchronized IStatus prepareDataContainer() throws Exception {
		if (dirty == false)
			return Status.OK_STATUS;

		ClassesReadingJob classesReadingJob = new ClassesReadingJob();
		classesReadingJob.setUser(true);
		classesReadingJob.schedule();
		classesReadingJob.join();
		
		IStatus status = classesReadingJob.getResult();

		if(status.isOK())
			dirty = false;

		return status; 
	}

	public void setDirty() {
		this.dirty = true;
	}

}
