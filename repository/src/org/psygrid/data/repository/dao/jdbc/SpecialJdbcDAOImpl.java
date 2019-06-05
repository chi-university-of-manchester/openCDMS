/*
Copyright (c) 2006-2008, The University of Manchester, UK.

This file is part of PsyGrid.

PsyGrid is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as 
published by the Free Software Foundation, either version 3 of 
the License, or (at your option) any later version.

PsyGrid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public 
License along with PsyGrid.  If not, see <http://www.gnu.org/licenses/>.
*/


package org.psygrid.data.repository.dao.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.psygrid.data.repository.dao.SpecialJdbcDao;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Rob Harper
 *
 */
public class SpecialJdbcDAOImpl implements SpecialJdbcDao {

    private DataSource dataSource;
    
    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

	public void addRecordDataToRecords() {
        JdbcTemplate jt = new JdbcTemplate(dataSource);
        
    	System.out.println("Create temporary table t_records_temp...");
        jt.execute("create table t_records_temp (c_id bigint not null, c_sch_st_date timestamp, c_stud_ent_date timestamp, " +
        		  "c_dataset_id bigint not null, c_identifier_id bigint not null unique, c_site_id bigint, " +
        		  "c_consent_modified timestamp, c_status_modified timestamp, c_deleted smallint, primary key (c_id))");

    	System.out.println("Copy data from t_records to t_records_temp...");
        jt.execute("insert into t_records_temp (c_id, c_sch_st_date, c_stud_ent_date, " +
        		  "c_dataset_id, c_identifier_id, c_site_id, " +
        		  "c_consent_modified, c_status_modified, c_deleted) " +
        		  "(select c_id, c_sch_st_date, c_stud_ent_date, " +
        		  "c_dataset_id, c_identifier_id, c_site_id, " +
        		  "c_consent_modified, c_status_modified, c_deleted from t_records)");
        
    	System.out.println("Drop constraints...");
        jt.execute("alter table t_consents drop foreign key FK94CDB704173E75FA");
        jt.execute("alter table t_doc_insts drop foreign key FK31607B1B55D83587");
        jt.execute("alter table t_elem_insts drop foreign key FKD52B6EE855D83587");
        jt.execute("alter table t_rec_reports drop foreign key FKC16171C555D83587");
        
        jt.execute("alter table t_records drop foreign key FKFC191177C8524107");
        jt.execute("alter table t_records drop foreign key FKFC1911777DB7B807");
        jt.execute("alter table t_records drop foreign key FKFC1911777474146");
        jt.execute("alter table t_records drop foreign key FKFC191177D0BFC2CD");
        
    	System.out.println("Drop t_records...");
    	jt.execute("drop table t_records");
    	
    	System.out.println("Creating updated t_records...");
    	jt.execute("create table t_records (c_id bigint not null, c_dataset_id bigint not null, " +
    			"c_identifier_id bigint not null unique, c_site_id bigint, c_consent_modified timestamp, " +
    			"c_status_modified timestamp, c_rcd_data_id bigint not null unique, c_deleted smallint, primary key (c_id))");

    	System.out.println("Creating t_record_data...");
        jt.execute("create table t_record_data (c_id bigint not null, c_notes clob(4096), c_sch_st_date timestamp, c_stud_ent_date timestamp, primary key (c_id))");

        System.out.println("Processing records to re-create t_records and introduce t_record_data...");
        String sql = "select c_id from t_records_temp";
        List records = jt.queryForList(sql);
        for ( int i=0, c=records.size(); i<c; i++ ){
        	Map record = (Map)records.get(i);
        	System.out.println("Processing record "+record.get("C_ID")+"...");
        	//insert new persistent
        	jt.update("insert into t_persistents (c_version) values (0)");
        	//get id of new persistent
        	long rdId = jt.queryForLong("select max(c_id) from t_persistents");
        	//insert new provenanceable
        	jt.update("insert into t_provenanceables (c_id) values ("+rdId+")");
        	//insert new record data
        	jt.update("insert into t_record_data (c_id, c_sch_st_date, c_stud_ent_date) " +
        			"(select "+rdId+", c_sch_st_date, c_stud_ent_date from t_records_temp where c_id="+record.get("C_ID")+")");
        	//insert record
        	jt.update("insert into t_records (c_id, c_dataset_id, c_identifier_id, c_site_id, " +
        			"c_consent_modified, c_status_modified, c_rcd_data_id, c_deleted) " +
    			"(select c_id, c_dataset_id, c_identifier_id, c_site_id, c_consent_modified, " +
    			"c_status_modified, "+rdId+", c_deleted from t_records_temp where c_id="+record.get("C_ID")+")");
        }
        
        //add in the constraints again
        System.out.println("Adding constraints...");
        jt.execute("alter table t_records add constraint FKFC191177C8524107 foreign key (c_site_id) references t_sites");
		jt.execute("alter table t_records add constraint FKFC1911777DB7B807 foreign key (c_identifier_id) references t_identifiers");
		jt.execute("alter table t_records add constraint FKFC1911777474146 foreign key (c_id) references t_statused_instances");
		jt.execute("alter table t_records add constraint FKFC1911771FDF16C foreign key (c_rcd_data_id) references t_record_data");
		jt.execute("alter table t_records add constraint FKFC191177D0BFC2CD foreign key (c_dataset_id) references t_datasets");
		
		jt.execute("alter table t_consents add constraint FK94CDB704173E75FA foreign key (c_elem_inst_id) references t_records");
		jt.execute("alter table t_doc_insts add constraint FK31607B1B55D83587 foreign key (c_record_id) references t_records");
		jt.execute("alter table t_elem_insts add constraint FKD52B6EE855D83587 foreign key (c_record_id) references t_records");
		jt.execute("alter table t_rec_reports add constraint FKC16171C555D83587 foreign key (c_record_id) references t_records");

		//Tidy up
        System.out.println("Tidy up...");
		jt.execute("drop table t_records_temp");

	}

	public List<Long> getRecordsInStatusReferredByProject(String project){
        JdbcTemplate jt = new JdbcTemplate(dataSource);
        String sql = "select r.c_id " +
        			 "from t_datasets ds, t_records r, t_statused_instances si, t_statuses s " +
        			 "where r.c_dataset_id=ds.c_id "+
        			 "and r.c_id=si.c_id "+
        			 "and si.c_status_id=s.c_id "+
        			 "and ds.c_project_code='"+project+"' "+
        			 "and s.c_short_name='Referred'";
        List records = jt.queryForList(sql);
        List<Long> result = new ArrayList<Long>();
        for ( int i=0, c=records.size(); i<c; i++ ){
        	Map record = (Map)records.get(i);
        	result.add((Long)record.get("C_ID"));
        }
        return result;
	}
	
}
