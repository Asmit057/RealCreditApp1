-- Migration: Drop old unique constraint on customer_id in reward table
-- This allows multiple rewards (one per credit card) for the same customer

-- Drop the old unique constraint on customer_id
ALTER TABLE reward DROP INDEX UK_o3spux2ab266iwa8cc3c2ekno;
