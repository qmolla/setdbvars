package org.pentaho.di.job.entries.setdbvars;

import org.pentaho.di.core.Result;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.entry.JobEntryBase;
import org.pentaho.di.job.entry.JobEntryInterface;

public class JobEntrySetDBVars extends JobEntryBase implements Cloneable, JobEntryInterface {

	
	private static Class<?> PKG = JobEntrySetDBVars.class;
	
	private DatabaseMeta connection;
	private Boolean useVariableSubstitution = false;
	private String sql;

	

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
	
}
