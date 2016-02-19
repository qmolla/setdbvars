package org.pentaho.di.ui.job.entries.setdbvars;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.setdbvars.JobEntrySetDBVars;
import org.pentaho.di.job.entry.JobEntryDialogInterface;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.ui.core.widget.StyledTextComp;
import org.pentaho.di.ui.job.dialog.JobDialog;
import org.pentaho.di.ui.job.entry.JobEntryDialog;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class JobEntrySetDBVarsDialog extends JobEntryDialog implements JobEntryDialogInterface {

	private static Class<?> PKG = JobEntrySetDBVars.class; 
	private JobEntrySetDBVars jobEntry;
	
	private CCombo wConnection;
	private Label wlName;
	private Text wName;
	private FormData fdlName, fdName;
	private Shell shell;
	
	private boolean changed;
	
	
	private Label wlUseSubs;
	private Button wUseSubs;
	private FormData fdlUseSubs;
	private FormData fdUseSubs;
	
	private Label wlPosition;
	private FormData fdlPosition;
	
	private Label wlSQL;
	private StyledTextComp wSQL;
	private FormData fdlSQL, fdSQL;
	
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
		
	    shell.setLayout( formLayout );
	    shell.setText( BaseMessages.getString( PKG, "JobSQL.Title" ) );

	    wOK = new Button( shell, SWT.PUSH );
	    wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
	    wCancel = new Button( shell, SWT.PUSH );
	    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );

	    BaseStepDialog.positionBottomButtons( shell, new Button[] { wOK, wCancel }, margin, null );
	    
	    
	    // Connection line
	    wConnection = addConnectionLine( shell, wName, middle, margin );
	    if ( jobEntry.getDatabase() == null && jobMeta.nrDatabases() == 1 ) {
	      wConnection.select( 0 );
	    }
	    wConnection.addModifyListener( lsMod );
	    
	    // Use variable substitution?
	    wlUseSubs = new Label( shell, SWT.RIGHT );
	    wlUseSubs.setText( BaseMessages.getString( PKG, "SetDBVars.UseVariableSubst.Label" ) );
	    props.setLook( wlUseSubs );
	    fdlUseSubs = new FormData();
	    fdlUseSubs.left = new FormAttachment( 0, 0 );
	    fdlUseSubs.top = new FormAttachment( wConnection, 2 * margin );
	    fdlUseSubs.right = new FormAttachment( middle, -margin );
	    wlUseSubs.setLayoutData( fdlUseSubs );
	    wUseSubs = new Button( shell, SWT.CHECK );
	    props.setLook( wUseSubs );
	    wUseSubs.setToolTipText( BaseMessages.getString( PKG, "SetDBVars.UseVariableSubst.Tooltip" ) );
	    fdUseSubs = new FormData();
	    fdUseSubs.left = new FormAttachment( middle, 0 );
	    fdUseSubs.top = new FormAttachment(  wConnection, 2 * margin );
	    fdUseSubs.right = new FormAttachment( 100, 0 );
	    wUseSubs.setLayoutData( fdUseSubs );
	    wUseSubs.addSelectionListener( new SelectionAdapter() {
	    public void widgetSelected( SelectionEvent e ) {
	        jobEntry.setUseVariableSubstitution( !jobEntry.getUseVariableSubstitution() );
	        jobEntry.setChanged();
	      }
	    } );

	    wlPosition = new Label( shell, SWT.NONE );
	    wlPosition.setText( BaseMessages.getString( PKG, "SetDBVars.LineNr.Label", "0" ) );
	    props.setLook( wlPosition );
	    fdlPosition = new FormData();
	    fdlPosition.left = new FormAttachment( 0, 0 );
	    fdlPosition.right = new FormAttachment( 100, 0 );
	    fdlPosition.bottom = new FormAttachment( wOK, -margin );
	    wlPosition.setLayoutData( fdlPosition );
	    
	 // Script line
	    wlSQL = new Label( shell, SWT.NONE );
	    wlSQL.setText( BaseMessages.getString( PKG, "SetDBVars.Script.Label" ) );
	    props.setLook( wlSQL );
	    fdlSQL = new FormData();
	    fdlSQL.left = new FormAttachment( 0, 0 );
	    fdlSQL.top = new FormAttachment( wUseSubs, margin );
	    wlSQL.setLayoutData( fdlSQL );

	    wSQL =
	      new StyledTextComp( jobEntry, shell, SWT.MULTI | SWT.LEFT | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL, "" );
	    props.setLook( wSQL, Props.WIDGET_STYLE_FIXED );
	    wSQL.addModifyListener( lsMod );
	    fdSQL = new FormData();
	    fdSQL.left = new FormAttachment( 0, 0 );
	    fdSQL.top = new FormAttachment( wlSQL, margin );
	    fdSQL.right = new FormAttachment( 100, -10 );
	    fdSQL.bottom = new FormAttachment( wlPosition, -margin );
	    wSQL.setLayoutData( fdSQL );
	    
	    
		return null;
	}
}
