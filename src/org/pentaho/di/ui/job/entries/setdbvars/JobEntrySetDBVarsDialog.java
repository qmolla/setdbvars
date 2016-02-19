package org.pentaho.di.ui.job.entries.setdbvars;

import org.pentaho.di.core.Const;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.setdbvars.JobEntrySetDBVars;
import org.pentaho.di.job.entry.JobEntryDialogInterface;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.ui.job.dialog.JobDialog;
import org.pentaho.di.ui.job.entry.JobEntryDialog;

public class JobEntrySetDBVarsDialog extends JobEntryDialog implements JobEntryDialogInterface {

	private static Class<?> PKG = JobEntrySetDBVars.class; 
	private JobEntrySetDBVars jobEntry;
	
	private CCombo wConnection;
	private Label wlName;
	private Text wName;
	private FormData fdlName, fdName;
	private Shell shell;
	
	private boolean changed;
	
	private Button wOK, wCancel;


	
	public JobEntrySetDBVarsDialog(Shell parent, JobEntryInterface jobEntry, Repository rep, JobMeta jobMeta) {
		super(parent, jobEntry, rep, jobMeta);
		jobEntry = (JobEntrySetDBVars) jobEntryInt;
        if (this.jobEntry.getName() == null)
            this.jobEntry.setName(BaseMessages.getString(PKG, "SetDBVars.Name.Default"));
	}

	
	@Override
	public JobEntryInterface open() {
		
	    Shell parent = getParent();
	    Display display = parent.getDisplay();

	    shell = new Shell( parent, props.getJobsDialogStyle() );
	    props.setLook( shell );
	    JobDialog.setShellImage( shell, jobEntry );
		
	    ModifyListener lsMod = new ModifyListener() {
	        public void modifyText( ModifyEvent e ) {
	          jobEntry.setChanged();
	        }
	      };
	    changed = jobEntry.hasChanged();

	    int middle = props.getMiddlePct();
	    int margin = Const.MARGIN;
	    	    
	    
	    FormLayout formLayout = new FormLayout();
	    formLayout.marginWidth = Const.FORM_MARGIN;
	    formLayout.marginHeight = Const.FORM_MARGIN;
		
		
	    // Connection line
	    wConnection = addConnectionLine( shell, wName, middle, margin );
	    if ( jobEntry.getDatabase() == null && jobMeta.nrDatabases() == 1 ) {
	      wConnection.select( 0 );
	    }
	    wConnection.addModifyListener( lsMod );
		return null;
	}
}
