CREATE OR REPLACE VIEW ${datasource.user}.dashboard_project_items AS (
  SELECT *
  FROM (
    WITH project_data AS (
    SELECT
      p.id project_id
    , pd.id project_detail_id
    , p.created_datetime
    , pd.status
    , pd.version
    , pi.project_title
    , pi.field_stage
    , df.field_name
    , df.ukcs_area
    , po.operator_org_grp_id
    , po.publishable_org_unit_id
    , COALESCE(pd.submitted_datetime, pd.created_datetime, p.created_datetime) sort_key
    , pd.is_current_version
    , DECODE(
      (
        SELECT MAX(details.version)
        FROM ${datasource.user}.project_details details
        WHERE details.project_id = pd.project_id
        AND details.status IN ('QA', 'PUBLISHED', 'ARCHIVED')
      )
      , pd.version, 1
      , 0
      ) is_latest_submitted_version
    FROM ${datasource.user}.projects p
    JOIN ${datasource.user}.project_details pd ON pd.project_id = p.id
    JOIN ${datasource.user}.project_operators po ON po.project_detail_id = pd.id
    LEFT JOIN ${datasource.user}.project_information pi ON pi.project_detail_id = pd.id
    LEFT JOIN ${datasource.user}.project_locations pl ON pl.project_detail_id = pd.id
    LEFT JOIN ${datasource.user}.devuk_fields df ON df.field_id = pl.field_id
    )
    , regulator_requested_updates AS (
    SELECT
      pd.project_id
    , rur.id update_request_id
    , rur.deadline_date
    , rur.requested_datetime
    FROM project_data pd
    JOIN ${datasource.user}.regulator_update_requests rur ON rur.project_detail_id = pd.project_detail_id AND pd.is_latest_submitted_version = 1
    )
    SELECT
      pd.*
    , COALESCE(
        (
          SELECT 1
          FROM regulator_requested_updates rru
          WHERE rru.project_id = pd.project_id
        )
      , 0
      ) update_requested
    , (
        SELECT rru.deadline_date
        FROM regulator_requested_updates rru
        WHERE rru.project_id = pd.project_id
      ) update_deadline_date
    , COALESCE(
        (
          -- We use the year 3999 as the constant here to ensure projects which have an update
          -- request without a deadline date appear before projects without an update request
          SELECT COALESCE(rru.deadline_date, TO_TIMESTAMP ('01-01-3999 00:00:00.000000', 'DD-MM-YYYY HH24:MI:SS.FF'))
          FROM regulator_requested_updates rru
          WHERE rru.project_id = pd.project_id
        )
      , TO_TIMESTAMP ('01-01-4000 00:00:00.000000', 'DD-MM-YYYY HH24:MI:SS.FF')
      ) update_sort_key
    FROM project_data pd
  )
);

CREATE OR REPLACE VIEW ${datasource.user}.operator_dashboard_items AS (
  SELECT *
  FROM ${datasource.user}.dashboard_project_items dpi
  WHERE dpi.is_current_version = 1
);

CREATE OR REPLACE VIEW ${datasource.user}.regulator_dashboard_items AS (
  SELECT *
  FROM ${datasource.user}.dashboard_project_items dpi
  WHERE dpi.is_latest_submitted_version = 1
);
