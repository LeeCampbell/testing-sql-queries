CREATE OR REPLACE VIEW marketing_internal.vw_date 
AS
SELECT 
    date_actual,
    month_name_abbreviated,
    year_actual
FROM 
    central_published.dim_date;
