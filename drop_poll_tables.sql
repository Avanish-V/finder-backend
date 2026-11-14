-- ============================================================================
-- Drop Poll Tables - Cleanup Script
-- ============================================================================
-- 
-- This script removes poll-related tables that were created by Hibernate
-- but no longer have corresponding entity classes in the codebase.
--
-- Run this script manually on your database to fix the schema mismatch error:
-- ERROR: foreign key constraint "fkmec8o4ul2cbr1v466nc8p14wt" cannot be implemented
-- Detail: Key columns "post_id" and "id" are of incompatible types: bigint and uuid.
--
-- ============================================================================

-- Drop tables in reverse order of dependencies
DROP TABLE IF EXISTS poll_votes_v2 CASCADE;
DROP TABLE IF EXISTS poll_options_v2 CASCADE;
DROP TABLE IF EXISTS poll_posts CASCADE;

-- Optional: Also drop the old versions if they exist
DROP TABLE IF EXISTS poll_votes CASCADE;
DROP TABLE IF EXISTS poll_options CASCADE;

-- Verify the tables are gone
SELECT tablename 
FROM pg_tables 
WHERE schemaname = 'public' 
  AND tablename LIKE '%poll%';

-- ============================================================================
-- End of Cleanup Script
-- ============================================================================
