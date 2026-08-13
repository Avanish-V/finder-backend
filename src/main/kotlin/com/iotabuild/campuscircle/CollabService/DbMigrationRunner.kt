package com.iotabuild.campuscircle.CollabService

import org.springframework.boot.CommandLineRunner
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class DbMigrationRunner(private val jdbcTemplate: JdbcTemplate) : CommandLineRunner {
    override fun run(vararg args: String?) {
        try {
            // 1. Drop the old foreign key constraint pointing to 'collab_requests'
            jdbcTemplate.execute("""
                ALTER TABLE collab_connect_requests DROP CONSTRAINT IF EXISTS fkeci7ql6uadfhwtygawbe623bu;
            """)
            
            // 2. Drop the newly added constraint if it exists to avoid duplicates
            jdbcTemplate.execute("""
                ALTER TABLE collab_connect_requests 
                DROP CONSTRAINT IF EXISTS fk_collab_connect_requests_collabs;
            """)
            
            // 3. Add the correct constraint pointing to the 'collabs' table
            jdbcTemplate.execute("""
                ALTER TABLE collab_connect_requests 
                ADD CONSTRAINT fk_collab_connect_requests_collabs 
                FOREIGN KEY (collab_id) REFERENCES collabs(id);
            """)
            
            // 4. Drop the obsolete 'collab_requests' table
            jdbcTemplate.execute("""
                DROP TABLE IF EXISTS collab_requests CASCADE;
            """)
            
            println("DbMigrationRunner: Successfully migrated collab tables and updated foreign keys!")
        } catch (e: Exception) {
            println("DbMigrationRunner failed during migration: ${e.message}")
        }
    }
}
