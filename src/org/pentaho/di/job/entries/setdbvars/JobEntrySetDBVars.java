package org.pentaho.di.job.entries.setdbvars;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleJobException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryBase;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.resource.ResourceEntry;
import org.pentaho.di.resource.ResourceEntry.ResourceType;
import org.pentaho.di.resource.ResourceReference;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

public class JobEntrySetDBVars extends JobEntryBase implements Cloneable, JobEntryInterface {
	
	private static Class<?> PKG = JobEntrySetDBVars.class;
	
	private DatabaseMeta connection;
	private String sql;
	private Boolean useVariableSubstitution = false;
	public int variableScope;
	private String varNameField;
	private String valueNameField;
	private String isEncryptedField;

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
	    sql = null;
	    connection = null;
	    variableScope = 0;

	}
	public JobEntrySetDBVars()
	{
		this("");
	}
	
	 public Object clone() {
		    JobEntrySetDBVars je = (JobEntrySetDBVars) super.clone();
		    
		    return je;
	}

	 
	public String getXML() {
	    StringBuffer retval = new StringBuffer( 200 );

	    retval.append( super.getXML() );

	    retval.append( "      " ).append( XMLHandler.addTagValue( "sql", sql ) );
	    retval.append( "      " ).append(
	      XMLHandler.addTagValue( "useVariableSubstitution", useVariableSubstitution ? "T" : "F" ) );

	    retval.append( "      " ).append(
	      XMLHandler.addTagValue( "connection", connection == null ? null : connection.getName() ) );

	    retval.append( "      " ).append(
	    	      XMLHandler.addTagValue( "variableScope", getVariableTypeCode( variableScope ) ) );
	    
	    retval.append( "      " ).append( XMLHandler.addTagValue( "variableNameField", varNameField ) );
	    retval.append( "      " ).append( XMLHandler.addTagValue( "valueNameField", valueNameField ) );
	    retval.append( "      " ).append( XMLHandler.addTagValue( "isEncryptedField", isEncryptedField ) );
	    
	    return retval.toString();
	}
	 
	
	public void loadXML( Node entrynode, List<DatabaseMeta> databases, List<SlaveServer> slaveServers,
	    Repository rep, IMetaStore metaStore ) throws KettleXMLException {
	    try {
	      super.loadXML( entrynode, databases, slaveServers );
	      sql = XMLHandler.getTagValue( entrynode, "sql" );
	      variableScope = getVariableType( XMLHandler.getTagValue( entrynode, "variableScope" ) );
	      String dbname = XMLHandler.getTagValue( entrynode, "connection" );
	      String sSubs = XMLHandler.getTagValue( entrynode, "useVariableSubstitution" );
	      varNameField = XMLHandler.getTagValue( entrynode, "variableNameField" );
	      valueNameField = XMLHandler.getTagValue( entrynode, "valueNameField" );
	      isEncryptedField = XMLHandler.getTagValue( entrynode, "isEncryptedField" );
	      
	      if ( sSubs != null && sSubs.equalsIgnoreCase( "T" ) ) {
	        useVariableSubstitution = true;
	      }
	      connection = DatabaseMeta.findDatabase( databases, dbname );

	    } catch ( KettleException e ) {
	      throw new KettleXMLException( "Unable to load job entry of type 'setDBVars' from XML node", e );
	    }
	} 
	 
	  public void loadRep( Repository rep, IMetaStore metaStore, ObjectId id_jobentry, List<DatabaseMeta> databases,
	    List<SlaveServer> slaveServers ) throws KettleException {
	    try {
	      sql = rep.getJobEntryAttributeString( id_jobentry, "sql" );
	      varNameField = rep.getJobEntryAttributeString( id_jobentry, "variableNameField" );
	      valueNameField = rep.getJobEntryAttributeString( id_jobentry, "valueNameField" );
	      isEncryptedField = rep.getJobEntryAttributeString( id_jobentry, "isEncryptedField" );
	      String sSubs = rep.getJobEntryAttributeString( id_jobentry, "useVariableSubstitution" );
	      if ( sSubs != null && sSubs.equalsIgnoreCase( "T" ) ) {
	        useVariableSubstitution = true;
	      }

	      variableScope = getVariableType( rep.getJobEntryAttributeString( id_jobentry, "variableScope" ) );
	      connection = rep.loadDatabaseMetaFromJobEntryAttribute( id_jobentry, "connection", "id_database", databases );
	      
	    } catch ( KettleDatabaseException dbe ) {
	      throw new KettleException( "Unable to load job entry of type 'setDBVars' from the repository with id_jobentry="
	        + id_jobentry, dbe );
	    }
	}	
	  
	public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_job ) throws KettleException {
	    try {
	      rep.saveDatabaseMetaJobEntryAttribute( id_job, getObjectId(), "connection", "id_database", connection );

	      rep.saveJobEntryAttribute( id_job, getObjectId(), "sql", sql );
	      rep.saveJobEntryAttribute( id_job, getObjectId(), "variableNameField", varNameField );
	      rep.saveJobEntryAttribute( id_job, getObjectId(), "valueNameField", valueNameField );
	      rep.saveJobEntryAttribute( id_job, getObjectId(), "isEncryptedField", isEncryptedField );
	      rep.saveJobEntryAttribute( id_job, getObjectId(), "useVariableSubstitution", useVariableSubstitution
	        ? "T" : "F" );

	      rep.saveJobEntryAttribute(
	    	        id_job, getObjectId(), "variableScope", getVariableTypeCode( variableScope ) );
	      
	    } catch ( KettleDatabaseException dbe ) {
	      throw new KettleException(
	        "Unable to save job entry of type 'setDBVars' to the repository for id_job=" + id_job, dbe );
	    }
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
	
	public String getVarNameField() {
		return varNameField;
	}
	public void setVarNameField(String varNameField) {
		this.varNameField = varNameField;
	}
	public String getValueNameField() {
		return valueNameField;
	}
	public void setValueNameField(String valueNameField) {
		this.valueNameField = valueNameField;
	}
	public String getIsEncryptedField() {
		return isEncryptedField;
	}
	public void setIsEncryptedField(String isEncryptedField) {
		this.isEncryptedField = isEncryptedField;
	}
	
	/**
	 * @param variableType
	 *          The variable type, see also VARIABLE_TYPE_...
	 * @return the variable type code for this variable type
	 */
	public static final String getVariableTypeCode( int variableType ) {
	    return variableTypeCode[variableType];
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
	  public int getVariableScope() {
	    return variableScope;
	  }

	  /**
	   * @param fileVariableType
	   *          the fileVariableType to set
	   */
	  public void setVariableScope( int variableScope ) {
	    this.variableScope = variableScope;
	  }
	  
	 @Override
	  public Result execute(Result previousResult, int arg1) throws KettleException {
		Result result = previousResult;

		ResultSet rs = null;
		
	    if ( connection != null ) {
	        Database db = new Database( this, connection );
	        db.shareVariablesWith( this );
	        try {
	            String mySQL = sql;
	            db.connect( parentJob.getTransactionId(), null );
	  
	            if ( !Const.isEmpty( mySQL ) ) {
	                // let it run
	                if ( useVariableSubstitution ) {
	                  mySQL = environmentSubstitute( mySQL );
	                }
	                if ( isDetailed() ) {
	                  logDetailed( BaseMessages.getString( PKG, "SetDBVars.Log.SQlStatement", mySQL ) );
	                }
	           }
	            
	           rs = db.openQuery(mySQL); 
	            
	           while (rs.next())
	           {
	        	   String varname = rs.getString(varNameField);
	        	   String value = rs.getString(valueNameField);
	        	   Boolean isEncryted= rs.getBoolean(isEncryptedField);
	        			   
	        	   if (isEncryted)  {    	
	        		   value = Encr.decryptPassword(value);
	        	   }
	        	   
	               switch ( variableScope ) {
	               case VARIABLE_TYPE_JVM:
	                 System.setProperty( varname, value );
	                 setVariable( varname, value );
	                 Job parentJobTraverse = parentJob;
	                 while ( parentJobTraverse != null ) {
	                   parentJobTraverse.setVariable( varname, value );
	                   parentJobTraverse = parentJobTraverse.getParentJob();
	                 }
	                 break;

	               case VARIABLE_TYPE_ROOT_JOB:
	                 // set variable in this job entry
	                 setVariable( varname, value );
	                 Job rootJob = parentJob;
	                 while ( rootJob != null ) {
	                   rootJob.setVariable( varname, value );
	                   rootJob = rootJob.getParentJob();
	                 }
	                 break;

	               case VARIABLE_TYPE_CURRENT_JOB:
	                 setVariable( varname, value );
	                 if ( parentJob != null ) {
	                   parentJob.setVariable( varname, value );
	                 } else {
	                   throw new KettleJobException( BaseMessages.getString(
	                     PKG, "SetDBVars.Error.UnableSetVariableCurrentJob", varname ) );
	                 }
	                 break;

	               case VARIABLE_TYPE_PARENT_JOB:
	                 setVariable( varname, value );

	                 if ( parentJob != null ) {
	                   parentJob.setVariable( varname, value );
	                   Job gpJob = parentJob.getParentJob();
	                   if ( gpJob != null ) {
	                     gpJob.setVariable( varname, value );
	                   } else {
	                     throw new KettleJobException( BaseMessages.getString(
	                       PKG, "SetDBVars.Error.UnableSetVariableParentJob", varname ) );
	                   }
	                 } else {
	                   throw new KettleJobException( BaseMessages.getString(
	                     PKG, "SetDBVars.Error.UnableSetVariableCurrentJob", varname ) );
	                 }
	                 break;

	               default:
	                 break;
	             }
	        	 
	             if ( log.isDetailed() ) {
	                   logDetailed( BaseMessages.getString( PKG, "SetDBVars.Log.SetVariableToValue", varname, isEncryted ? "*****Encrypted*****" : value ) );
	               }  
	        	   
	           }
	           
	        }catch ( SQLException e ) {
	            result.setNrErrors( 1 );
	            logError( BaseMessages.getString( PKG, "SetDBVars.ErrorRunJobEntry", e.getMessage() ) ); 
	        }catch ( KettleDatabaseException je ) {
	            result.setNrErrors( 1 );
	            logError( BaseMessages.getString( PKG, "SetDBVars.ErrorRunJobEntry", je.getMessage() ) );
	        } finally {
	            db.disconnect();
	        }
	    }else {
	        result.setNrErrors( 1 );
	        logError( BaseMessages.getString( PKG, "SetDBVars.NoDatabaseConnection" ) );
	    }

	    
	    
	    if ( result.getNrErrors() == 0 ) {
	        result.setResult( true );
	    }else {
	        result.setResult( false );
	    }
		return result;
	  }
		
	  public DatabaseMeta[] getUsedDatabaseConnections() {
		    return new DatabaseMeta[] { connection, };
	  }
	 
	  public List<ResourceReference> getResourceDependencies( JobMeta jobMeta ) {
	      List<ResourceReference> references = super.getResourceDependencies( jobMeta );
	      if ( connection != null ) {
	         ResourceReference reference = new ResourceReference( this );
	         reference.getEntries().add( new ResourceEntry( connection.getHostname(), ResourceType.SERVER ) );
	         reference.getEntries().add( new ResourceEntry( connection.getDatabaseName(), ResourceType.DATABASENAME ) );
	         references.add( reference );
	      }
	    return references;
  }	

}
