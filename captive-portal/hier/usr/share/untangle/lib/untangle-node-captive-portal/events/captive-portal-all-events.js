{
    "category": "Captive Portal",
    "conditions": [
        {
            "column": "captive_portal_blocked",
            "javaClass": "com.untangle.node.reports.SqlCondition",
            "operator": "is",
            "value": "NOT NULL"
        }
    ],
    "defaultColumns": ["time_stamp","client_addr","login_name","event_info","auth_type"],
    "description": "All user sessions processed by Captive Portal.",
    "displayOrder": 12,
    "javaClass": "com.untangle.node.reports.EventEntry",
    "table": "capture_user_events",
    "title": "All User Events",
    "uniqueId": "captive-portal-CWKFK6AXDU"
}