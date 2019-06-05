-- Patch to add update management reports
update t_mgmt_reports set c_view_action='ACTION_DR_VIEW_MGMT_REPORT' where c_view_action is null;