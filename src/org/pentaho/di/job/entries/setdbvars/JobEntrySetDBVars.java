package org.pentaho.di.job.entries.setdbvars;

import org.pentaho.di.core.Result;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.entry.JobEntryBase;
import org.pentaho.di.job.entry.JobEntryInterface;

public class JobEntrySetDBVars extends JobEntryBase implements Cloneable, JobEntryInterface {

	
	private static Class<?> PKG = JobEntrySetDBVars.class;
	
	private DatabaseMeta connection;
	private Boolean useVariableSubstitution = false;
	private String sql;

	public int fileVariableType;

	public static final int VARIABLE_TYPE_JVM = 0;
	public static final int VARIABLE_TYPE_CURRENT_JOB = 1;
	public static final int VARIABLE_TYPE_PARENT_JOB = 2;
	public static final int VARIABLE_TYPE_ROOT_JOB = 3;
	
	private static final String[] variableTypeCode = { "JVM", "CURRENT_JOB", "PARENT_JOB", "ROOT_JOB" };
	  private static final String[] variableTypeDesc = {
	    BaseMessages.getString( PKG, "SetDBVars.VariableType.JVM" ),
	    BaseMessages.getString( PKG, "SetDBVars.VariableType.CurrentJob" ),
	    BaseMessages.getString( PKG, "SetDBVars.VariableType.ParentJob" ),
	    BaseMessages.getString( PKG, "SetDBVars.VariableType.RootJob" ), };


	public JobEntrySetDBVars(String n)
	{
		super(n, "");
		setID(-1L);
	}

	public JobEntrySetDBVars()
	{
		this("");
	}
	
	
	@Override
	public Result execute(Result arg0, int arg1) throws KettleException {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void setDatabase( DatabaseMeta database ) {
		    this.connection = database;
	}

	public DatabaseMeta getDatabase() {
		    return connection;
	}
	
	public boolean getUseVariableSubstitution() {
		    return useVariableSubstitution;
	}

	public void setUseVariableSubstitution( boolean subs ) {
		    useVariableSubstitution = subs;
	}
	
	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
	
	public static final String getVariableTypeDescription( int variableType ) {
		    return variableTypeDesc[variableType];
	}
	
	public static final String[] getVariableTypeDescriptions() {
		    return variableTypeDesc;
	}

	public static final int getVariableType( String variableType ) {
		    for ( int i = 0; i < variableTypeCode.length; i++ ) {
		      if ( variableTypeCode[i].equalsIgnoreCase( variableType ) ) {
		        return i;
		      }
		    }
		    for ( int i = 0; i < variableTypeDesc.length; i++ ) {
		      if ( variableTypeDesc[i].equalsIgnoreCase( variableType ) ) {
		        return i;
		      }
		    }
		    return VARIABLE_TYPE_JVM;
	}
	
	  /**
	   * @return the fileVariableType
	   */
	  public int getFileVariableType() {
	    return fileVariableType;
	  }

	  /**
	   * @param fileVariableType
	   *          the fileVariableType to set
	   */
	  public void setFileVariableType( int fileVariableType ) {
	    this.fileVariableType = fileVariableType;
	  }
	
}
